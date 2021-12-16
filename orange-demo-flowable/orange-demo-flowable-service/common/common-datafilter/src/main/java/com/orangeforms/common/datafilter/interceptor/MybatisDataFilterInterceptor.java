package com.orangeforms.common.datafilter.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.annotation.*;
import com.orangeforms.common.core.exception.NoDataPermException;
import com.orangeforms.common.core.object.GlobalThreadLocal;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.core.util.ApplicationContextHolder;
import com.orangeforms.common.core.util.ContextUtil;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.RedisKeyUtil;
import com.orangeforms.common.datafilter.config.DataFilterProperties;
import com.orangeforms.common.datafilter.constant.DataPermRuleType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.*;

/**
 * Mybatis拦截器。目前用于数据权限的统一拦截和注入处理。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
@Component
public class MybatisDataFilterInterceptor implements Interceptor {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private DataFilterProperties properties;

    /**
     * 对象缓存。由于Set是排序后的，因此在查找排除方法名称时效率更高。
     * 在应用服务启动的监听器中(LoadDataPermMapperListener)，会调用当前对象的(loadMappersWithDataPerm)方法，加载缓存。
     */
    private final Map<String, ModelDataPermInfo> cachedDataPermMap = new HashMap<>();
    /**
     * 租户租户对象缓存。
     */
    private final Map<String, ModelTenantInfo> cachedTenantMap = new HashMap<>();

    /**
     * 预先加载与数据过滤相关的数据到缓存，该函数会在(LoadDataFilterInfoListener)监听器中调用。
     */
    public void loadInfoWithDataFilter() {
        Map<String, BaseDaoMapper> mapperMap =
                ApplicationContextHolder.getApplicationContext().getBeansOfType(BaseDaoMapper.class);
        for (BaseDaoMapper<?> mapperProxy : mapperMap.values()) {
            // 优先处理jdk的代理
            Object proxy = ReflectUtil.getFieldValue(mapperProxy, "h");
            // 如果不是jdk的代理，再看看cjlib的代理。
            if (proxy == null) {
                proxy = ReflectUtil.getFieldValue(mapperProxy, "CGLIB$CALLBACK_0");
            }
            Class<?> mapperClass = (Class<?>) ReflectUtil.getFieldValue(proxy, "mapperInterface");
            if (properties.getEnabledTenantFilter()) {
                loadTenantFilterData(mapperClass);
            }
            if (properties.getEnabledDataPermFilter()) {
                EnableDataPerm rule = mapperClass.getAnnotation(EnableDataPerm.class);
                if (rule != null) {
                    loadDataPermFilterRules(mapperClass, rule);
                }
            }
        }
    }

    private void loadTenantFilterData(Class<?> mapperClass) {
        Class<?> modelClass = (Class<?>) ((ParameterizedType)
                mapperClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
        Field[] fields = ReflectUtil.getFields(modelClass);
        for (Field field : fields) {
            if (field.getAnnotation(TenantFilterColumn.class) != null) {
                ModelTenantInfo tenantInfo = new ModelTenantInfo();
                tenantInfo.setModelName(modelClass.getSimpleName());
                tenantInfo.setTableName(modelClass.getAnnotation(TableName.class).value());
                tenantInfo.setFieldName(field.getName());
                tenantInfo.setColumnName(MyModelUtil.mapToColumnName(field, modelClass));
                // 判断当前dao中是否包括不需要自动注入租户Id过滤的方法。
                DisableTenantFilter disableTenantFilter = mapperClass.getAnnotation(DisableTenantFilter.class);
                if (disableTenantFilter != null) {
                    // 这里开始获取当前Mapper已经声明的的SqlId中，有哪些是需要排除在外的。
                    // 排除在外的将不进行数据过滤。
                    Set<String> excludeMethodNameSet = new HashSet<>();
                    for (String excludeName : disableTenantFilter.includeMethodName()) {
                        excludeMethodNameSet.add(excludeName);
                        // 这里是给pagehelper中，分页查询先获取数据总量的查询。
                        excludeMethodNameSet.add(excludeName + "_COUNT");
                    }
                    tenantInfo.setExcludeMethodNameSet(excludeMethodNameSet);
                }
                cachedTenantMap.put(mapperClass.getName(), tenantInfo);
                break;
            }
        }
    }

    private void loadDataPermFilterRules(Class<?> mapperClass, EnableDataPerm rule) {
        String sysDataPermMapperName = "SysDataPermMapper";
        // 由于给数据权限Mapper添加@EnableDataPerm，将会导致无限递归，因此这里检测到之后，
        // 会在系统启动加载监听器的时候，及时抛出异常。
        if (StringUtils.equals(sysDataPermMapperName, mapperClass.getSimpleName())) {
            throw new IllegalStateException("Add @EnableDataPerm annotation to SysDataPermMapper is ILLEGAL!");
        }
        // 这里开始获取当前Mapper已经声明的的SqlId中，有哪些是需要排除在外的。
        // 排除在外的将不进行数据过滤。
        Set<String> excludeMethodNameSet = null;
        String[] excludes = rule.excluseMethodName();
        if (excludes.length > 0) {
            excludeMethodNameSet = new HashSet<>();
            for (String excludeName : excludes) {
                excludeMethodNameSet.add(excludeName);
                // 这里是给pagehelper中，分页查询先获取数据总量的查询。
                excludeMethodNameSet.add(excludeName + "_COUNT");
            }
        }
        // 获取Mapper关联的主表信息，包括表名，user过滤字段名和dept过滤字段名。
        Class<?> modelClazz = (Class<?>)
                ((ParameterizedType) mapperClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
        Field[] fields = ReflectUtil.getFields(modelClazz);
        Field userFilterField = null;
        Field deptFilterField = null;
        for (Field field : fields) {
            if (null != field.getAnnotation(UserFilterColumn.class)) {
                userFilterField = field;
            }
            if (null != field.getAnnotation(DeptFilterColumn.class)) {
                deptFilterField = field;
            }
            if (userFilterField != null && deptFilterField != null) {
                break;
            }
        }
        // 通过注解解析与Mapper关联的Model，并获取与数据权限关联的信息，并将结果缓存。
        ModelDataPermInfo info = new ModelDataPermInfo();
        info.setMainTableName(MyModelUtil.mapToTableName(modelClazz));
        info.setExcludeMethodNameSet(excludeMethodNameSet);
        if (userFilterField != null) {
            info.setUserFilterColumn(MyModelUtil.mapToColumnName(userFilterField, modelClazz));
        }
        if (deptFilterField != null) {
            info.setDeptFilterColumn(MyModelUtil.mapToColumnName(deptFilterField, modelClazz));
        }
        cachedDataPermMap.put(mapperClass.getName(), info);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 判断当前线程本地存储中，业务操作是否禁用了数据权限过滤，如果禁用，则不进行后续的数据过滤处理了。
        if (!GlobalThreadLocal.enabledDataFilter()) {
            return invocation.proceed();
        }
        // 只有在HttpServletRequest场景下，该拦截器才起作用，对于系统级别的预加载数据不会应用数据权限。
        if (!ContextUtil.hasRequestContext()) {
            return invocation.proceed();
        }
        // 没有登录的用户，不会参与租户过滤，如果需要过滤的，自己在代码中手动实现
        // 通常对于无需登录的白名单url，也无需过滤了。
        // 另外就是登录接口中，获取菜单列表的接口，由于尚未登录，没有TokenData，所以这个接口我们手动加入了该条件。
        if (TokenData.takeFromRequest() == null) {
            return invocation.proceed();
        }
        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        StatementHandler delegate =
                (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
        // 通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
        MappedStatement mappedStatement =
                (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
        SqlCommandType commandType = mappedStatement.getSqlCommandType();
        // 对于INSERT语句，我们不进行任何数据过滤。
        if (commandType == SqlCommandType.INSERT) {
            return invocation.proceed();
        }
        String sqlId = mappedStatement.getId();
        int pos = StringUtils.lastIndexOf(sqlId, ".");
        String className = StringUtils.substring(sqlId, 0, pos);
        String methodName = StringUtils.substring(sqlId, pos + 1);
        // 先进行租户过滤条件的处理，再将解析并处理后的SQL Statement交给下一步的数据权限过滤去处理。
        // 这样做的目的主要是为了减少一次SQL解析的过程，因为这是高频操作，所以要尽量去优化。
        Statement statement = null;
        if (properties.getEnabledTenantFilter()) {
            statement = this.processTenantFilter(className, methodName, delegate.getBoundSql(), commandType);
        }
        // 处理数据权限过滤。
        if (properties.getEnabledDataPermFilter()) {
            this.processDataPermFilter(className, methodName, delegate.getBoundSql(), commandType, statement, sqlId);
        }
        return invocation.proceed();
    }

    private Statement processTenantFilter(
            String className, String methodName, BoundSql boundSql, SqlCommandType commandType) throws JSQLParserException {
        ModelTenantInfo info = cachedTenantMap.get(className);
        if (info == null || CollUtil.contains(info.getExcludeMethodNameSet(), methodName)) {
            return null;
        }
        String sql = boundSql.getSql();
        Statement statement = CCJSqlParserUtil.parse(sql);
        StringBuilder filterBuilder = new StringBuilder(64);
        filterBuilder.append(info.tableName).append(".")
                .append(info.columnName)
                .append("=")
                .append(TokenData.takeFromRequest().getTenantId());
        String dataFilter = filterBuilder.toString();
        if (commandType == SqlCommandType.UPDATE) {
            Update update = (Update) statement;
            this.buildWhereClause(update, dataFilter);
        } else if (commandType == SqlCommandType.DELETE) {
            Delete delete = (Delete) statement;
            this.buildWhereClause(delete, dataFilter);
        } else {
            Select select = (Select) statement;
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
            FromItem fromItem = selectBody.getFromItem();
            if (fromItem != null) {
                PlainSelect subSelect = null;
                if (fromItem instanceof SubSelect) {
                    subSelect = (PlainSelect) ((SubSelect) fromItem).getSelectBody();
                }
                if (subSelect != null) {
                    buildWhereClause(subSelect, dataFilter);
                } else {
                    buildWhereClause(selectBody, dataFilter);
                }
            }
        }
        log.info("Tenant Filter Where Clause [{}]", dataFilter);
        ReflectUtil.setFieldValue(boundSql, "sql", statement.toString());
        return statement;
    }

    private void processDataPermFilter(
            String className, String methodName, BoundSql boundSql, SqlCommandType commandType, Statement statement, String sqlId)
            throws JSQLParserException {
        // 判断当前线程本地存储中，业务操作是否禁用了数据权限过滤，如果禁用，则不进行后续的数据过滤处理了。
        // 数据过滤权限中，INSERT不过滤。如果是管理员则不参与数据权限的数据过滤，显示全部数据。
        TokenData tokenData = TokenData.takeFromRequest();
        if (Boolean.TRUE.equals(tokenData.getIsAdmin())) {
            return;
        }
        ModelDataPermInfo info = cachedDataPermMap.get(className);
        // 再次查找当前方法是否为排除方法，如果不是，就参与数据权限注入过滤。
        if (info == null || CollUtil.contains(info.getExcludeMethodNameSet(), methodName)) {
            return;
        }
        String dataPermSessionKey = RedisKeyUtil.makeSessionDataPermIdKey(tokenData.getSessionId());
        String dataPermData = redissonClient.getBucket(dataPermSessionKey).get().toString();
        if (StringUtils.isBlank(dataPermData)) {
            throw new NoDataPermException("No Related DataPerm found for SQL_ID [ " + sqlId + " ].");
        }
        Map<Integer, String> dataPermMap = new HashMap<>(8);
        for (Map.Entry<String, Object> entry : JSON.parseObject(dataPermData).entrySet()) {
            dataPermMap.put(Integer.valueOf(entry.getKey()), entry.getValue().toString());
        }
        if (MapUtils.isEmpty(dataPermMap)) {
            throw new NoDataPermException("No Related DataPerm found for SQL_ID [ " + sqlId + " ].");
        }
        if (dataPermMap.containsKey(DataPermRuleType.TYPE_ALL)) {
            return;
        }
        this.processDataPerm(info, dataPermMap, boundSql, commandType, statement);
    }

    private void processDataPerm(
            ModelDataPermInfo info,
            Map<Integer, String> dataPermMap,
            BoundSql boundSql,
            SqlCommandType commandType,
            Statement statement) throws JSQLParserException {
        List<String> criteriaList = new LinkedList<>();
        for (Map.Entry<Integer, String> entry : dataPermMap.entrySet()) {
            String filterClause = processDataPermRule(info, entry.getKey(), entry.getValue());
            if (StringUtils.isNotBlank(filterClause)) {
                criteriaList.add(filterClause);
            }
        }
        if (CollectionUtils.isEmpty(criteriaList)) {
            return;
        }
        StringBuilder filterBuilder = new StringBuilder(128);
        filterBuilder.append("(");
        filterBuilder.append(StringUtils.join(criteriaList, " OR "));
        filterBuilder.append(")");
        String dataFilter = filterBuilder.toString();
        if (statement == null) {
            String sql = boundSql.getSql();
            statement = CCJSqlParserUtil.parse(sql);
        }
        if (commandType == SqlCommandType.UPDATE) {
            Update update = (Update) statement;
            this.buildWhereClause(update, dataFilter);
        } else if (commandType == SqlCommandType.DELETE) {
            Delete delete = (Delete) statement;
            this.buildWhereClause(delete, dataFilter);
        } else {
            Select select = (Select) statement;
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
            FromItem fromItem = selectBody.getFromItem();
            PlainSelect subSelect = null;
            if (fromItem != null) {
                if (fromItem instanceof SubSelect) {
                    subSelect = (PlainSelect) ((SubSelect) fromItem).getSelectBody();
                }
                if (subSelect != null) {
                    buildWhereClause(subSelect, dataFilter);
                } else {
                    buildWhereClause(selectBody, dataFilter);
                }
            }
        }
        log.info("DataPerm Filter Where Clause [{}]", dataFilter);
        ReflectUtil.setFieldValue(boundSql, "sql", statement.toString());
    }

    private String processDataPermRule(ModelDataPermInfo info, Integer ruleType, String deptIds) {
        TokenData tokenData = TokenData.takeFromRequest();
        StringBuilder filter = new StringBuilder(128);
        if (ruleType == DataPermRuleType.TYPE_USER_ONLY) {
            if (StringUtils.isNotBlank(info.getUserFilterColumn())) {
                if (properties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getUserFilterColumn())
                        .append(" = ")
                        .append(tokenData.getUserId());
            }
        } else {
            if (StringUtils.isNotBlank(info.getDeptFilterColumn())) {
                if (ruleType == DataPermRuleType.TYPE_DEPT_ONLY) {
                    if (properties.getAddTableNamePrefix()) {
                        filter.append(info.getMainTableName()).append(".");
                    }
                    filter.append(info.getDeptFilterColumn())
                            .append(" = ")
                            .append(tokenData.getDeptId());
                } else if (ruleType == DataPermRuleType.TYPE_DEPT_AND_CHILD_DEPT) {
                    filter.append(" EXISTS ")
                            .append("(SELECT 1 FROM ")
                            .append(properties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation WHERE ")
                            .append(properties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation.parent_dept_id = ")
                            .append(tokenData.getDeptId())
                            .append(" AND ");
                    if (properties.getAddTableNamePrefix()) {
                        filter.append(info.getMainTableName()).append(".");
                    }
                    filter.append(info.getDeptFilterColumn())
                            .append(" = ")
                            .append(properties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation.dept_id) ");
                } else if (ruleType == DataPermRuleType.TYPE_MULTI_DEPT_AND_CHILD_DEPT) {
                    filter.append(" EXISTS ")
                            .append("(SELECT 1 FROM ")
                            .append(properties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation WHERE ")
                            .append(properties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation.parent_dept_id IN (")
                            .append(deptIds)
                            .append(") AND ");
                    if (properties.getAddTableNamePrefix()) {
                        filter.append(info.getMainTableName()).append(".");
                    }
                    filter.append(info.getDeptFilterColumn())
                            .append(" = ")
                            .append(properties.getDeptRelationTablePrefix())
                            .append("sys_dept_relation.dept_id) ");
                } else if (ruleType == DataPermRuleType.TYPE_CUSTOM_DEPT_LIST) {
                    if (properties.getAddTableNamePrefix()) {
                        filter.append(info.getMainTableName()).append(".");
                    }
                    filter.append(info.getDeptFilterColumn())
                            .append(" IN (")
                            .append(deptIds)
                            .append(") ");
                }
            }
        }
        return filter.toString();
    }

    private void buildWhereClause(Update update, String dataFilter) throws JSQLParserException {
        if (update.getWhere() == null) {
            update.setWhere(CCJSqlParserUtil.parseCondExpression(dataFilter));
        } else {
            AndExpression and = new AndExpression(
                    CCJSqlParserUtil.parseCondExpression(dataFilter), update.getWhere());
            update.setWhere(and);
        }
    }

    private void buildWhereClause(Delete delete, String dataFilter) throws JSQLParserException {
        if (delete.getWhere() == null) {
            delete.setWhere(CCJSqlParserUtil.parseCondExpression(dataFilter));
        } else {
            AndExpression and = new AndExpression(
                    CCJSqlParserUtil.parseCondExpression(dataFilter), delete.getWhere());
            delete.setWhere(and);
        }
    }

    private void buildWhereClause(PlainSelect select, String dataFilter) throws JSQLParserException {
        if (select.getWhere() == null) {
            select.setWhere(CCJSqlParserUtil.parseCondExpression(dataFilter));
        } else {
            AndExpression and = new AndExpression(
                    CCJSqlParserUtil.parseCondExpression(dataFilter), select.getWhere());
            select.setWhere(and);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 这里需要空注解，否则sonar会不happy。
    }

    @Data
    private static final class ModelDataPermInfo {
        private Set<String> excludeMethodNameSet;
        private String userFilterColumn;
        private String deptFilterColumn;
        private String mainTableName;
    }

    @Data
    private static final class ModelTenantInfo {
        private Set<String> excludeMethodNameSet;
        private String modelName;
        private String tableName;
        private String fieldName;
        private String columnName;
    }
}
