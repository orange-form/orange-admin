package com.orange.admin.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.orange.admin.common.core.constant.DataPermRuleType;
import com.orange.admin.common.core.annotation.DeptFilterColumn;
import com.orange.admin.common.core.annotation.UserFilterColumn;
import com.orange.admin.common.core.annotation.EnableDataPerm;
import com.orange.admin.common.core.exception.NoDataPermException;
import com.orange.admin.common.core.object.GlobalThreadLocal;
import com.orange.admin.common.core.object.TokenData;
import com.orange.admin.common.core.util.ApplicationContextHolder;
import com.orange.admin.common.core.util.ContextUtil;
import com.orange.admin.common.core.util.MyModelUtil;
import com.orange.admin.config.CacheConfig;
import com.orange.admin.upms.dao.SysDataPermMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.*;

/**
 * Mybatis拦截器。目前用于数据权限的统一拦截和注入处理。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
@Component
public class MybatisDataPermInterceptor implements Interceptor {

    @Autowired
    private CacheManager cacheManager;

    /**
     * HTTP Head或HTTP Request Parameter中菜单Id数据的KEY名称。
     */
    private static final String MENU_ID_HEADER_KEY = "MenuId";
    /**
     * 对象缓存。由于Set是排序后的，因为在查找排除方法名称时效率更高。
     * 在应用服务启动的监听器中(LoadDataPermMapperListener)，会调用当前对象的(loadMappersWithDataPerm)方法，加载缓存。
     */
    private Map<String, ModelDataPermInfo> cacheMap = new HashMap<>();

    /**
     * 预先加载需要数据权限过滤的Mapper到缓存，该函数会在(LoadDataPermMapperListener)监听器中调用。
     */
    public void loadMappersWithDataPerm() {
        @SuppressWarnings("all")
        Map<String, Mapper> mapperMap =
                ApplicationContextHolder.getApplicationContext().getBeansOfType(Mapper.class);
        for (Mapper<?> mapperProxy : mapperMap.values()) {
            // 优先处理jdk的代理
            Object proxy = ReflectUtil.getFieldValue(mapperProxy, "h");
            // 如果不是jdk的代理，再看看cjlib的代理。
            if (proxy == null) {
                proxy = ReflectUtil.getFieldValue(mapperProxy, "CGLIB$CALLBACK_0");
            }
            Class<?> mapperClass =
                    (Class<?>) ReflectUtil.getFieldValue(proxy, "mapperInterface");
            EnableDataPerm rule = mapperClass.getAnnotation(EnableDataPerm.class);
            if (rule != null) {
                loadRules(mapperClass, rule);
            }
        }
    }

    private void loadRules(Class<?> mapperClass, EnableDataPerm rule) {
        // 由于给数据权限Mapper添加@EnableDataPerm，将会导致无限递归，因此这里检测到之后，
        // 会在系统启动加载监听器的时候，及时抛出异常。
        if (mapperClass.equals(SysDataPermMapper.class)) {
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
                // 这里是给tk.mapper和pagehelper中，分页查询先获取数据总量的查询。
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
        cacheMap.put(mapperClass.getName(), info);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 只有在HttpServletRequest场景下，该拦截器才起作用，对于系统级别的预加载数据不会应用数据权限。
        if (!ContextUtil.hasRequestContext()) {
            return invocation.proceed();
        }
        // 判断当前线程本地存储中，业务操作是否禁用了数据权限过滤，如果禁用，则不进行后续的数据过滤处理了。
        if (!GlobalThreadLocal.enabledDataPerm()) {
            return invocation.proceed();
        }
        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        StatementHandler delegate =
                (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
        //通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
        MappedStatement mappedStatement =
                (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
        SqlCommandType commandType = mappedStatement.getSqlCommandType();
        TokenData tokenData = TokenData.takeFromRequest();
        // 数据过滤权限中，只是过滤SELECT语句。如果是管理员则不参与数据权限的数据过滤，显示全部数据。
        if (commandType != SqlCommandType.SELECT || tokenData == null || Boolean.TRUE.equals(tokenData.getIsAdmin())) {
            return invocation.proceed();
        }
        String sqlId = mappedStatement.getId();
        int pos = StringUtils.lastIndexOf(sqlId, ".");
        String className = StringUtils.substring(sqlId, 0, pos);
        String methodName = StringUtils.substring(sqlId, pos + 1);
        // 先从缓存中查找当前Mapper是否存在。
        ModelDataPermInfo info = cacheMap.get(className);
        // 再次查找当前方法是否为排除方法，如果不是，就参与数据权限注入过滤。
        if (info != null && !CollUtil.contains(info.getExcludeMethodNameSet(), methodName)) {
            String menuId = ContextUtil.getHttpRequest().getHeader(MENU_ID_HEADER_KEY);
            if (StringUtils.isBlank(menuId)) {
                menuId = ContextUtil.getHttpRequest().getParameter(MENU_ID_HEADER_KEY);
                if (StringUtils.isBlank(menuId)) {
                    throw new IllegalStateException(
                            "No [ MENU_ID ] key found in Http Header for SQL_ID [ " + sqlId + " ].");
                }
            }
            Cache cache = cacheManager.getCache(CacheConfig.CacheEnum.DATA_PERMISSION_CACHE.name());
            Map<Object, Map<Integer, String>> menuIdAndDataPermMap =
                   (Map<Object, Map<Integer, String>>) cache.get(tokenData.getSessionId(), Map.class);
            if (menuIdAndDataPermMap == null) {
                throw new NoDataPermException(
                        "No Related DataPerm found with SESSION_ID [ " + tokenData.getSessionId() + " ].");
            }
            Map<Integer, String> dataPermMap = menuIdAndDataPermMap.get(Long.valueOf(menuId));
            if (MapUtils.isEmpty(dataPermMap)) {
                throw new NoDataPermException(
                        "No Related DataPerm found with MENU_ID [ " + menuId + " ] for SQL_ID [ " + sqlId + " ].");
            }
            processDataPerm(info, dataPermMap, delegate.getBoundSql());
        }
        return invocation.proceed();
    }

    private void processDataPerm(ModelDataPermInfo info, Map<Integer, String> dataPermMap, BoundSql boundSql)
            throws JSQLParserException {
        if (dataPermMap.containsKey(DataPermRuleType.TYPE_ALL)) {
            return;
        }
        String sql = boundSql.getSql();
        Select select = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect selectBody = (PlainSelect) select.getSelectBody();
        FromItem fromItem = selectBody.getFromItem();
        PlainSelect subSelect = null;
        if (fromItem instanceof SubSelect) {
            subSelect = (PlainSelect) ((SubSelect) fromItem).getSelectBody();
        }
        List<String> criteriaList = new LinkedList<>();
        for (Map.Entry<Integer, String> entry : dataPermMap.entrySet()) {
            String filterClause = processDataPermRule(info, entry.getKey(), entry.getValue());
            if (StringUtils.isNotBlank(filterClause)) {
                criteriaList.add(filterClause);
            }
        }
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            StringBuilder filterBuilder = new StringBuilder(128);
            filterBuilder.append("(");
            filterBuilder.append(StringUtils.join(criteriaList, " OR "));
            filterBuilder.append(")");
            String dataFilter = filterBuilder.toString();
            if (subSelect != null) {
                buildWhereClause(subSelect, dataFilter);
            } else {
                buildWhereClause(selectBody, dataFilter);
            }
        }
        sql = select.toString();
        ReflectUtil.setFieldValue(boundSql, "sql", sql);
    }

    private String processDataPermRule(ModelDataPermInfo info, Integer ruleType, String deptIds) {
        TokenData tokenData = TokenData.takeFromRequest();
        StringBuilder filter = new StringBuilder(128);
        if (ruleType == DataPermRuleType.TYPE_USER_ONLY) {
            if (StringUtils.isNotBlank(info.getUserFilterColumn())) {
                filter.append(info.getMainTableName())
                        .append(".")
                        .append(info.getUserFilterColumn())
                        .append(" = ")
                        .append(tokenData.getUserId());
            }
        } else {
            if (StringUtils.isNotBlank(info.getDeptFilterColumn())) {
                if (ruleType == DataPermRuleType.TYPE_DEPT_ONLY) {
                    filter.append(info.getMainTableName())
                            .append(".")
                            .append(info.getDeptFilterColumn())
                            .append(" = ")
                            .append(tokenData.getDeptId());
                } else if (ruleType == DataPermRuleType.TYPE_CUSTOM_DETP_LIST) {
                    filter.append(info.getMainTableName())
                            .append(".")
                            .append(info.getDeptFilterColumn())
                            .append(" IN (")
                            .append(deptIds)
                            .append(") ");
                }
            }
        }
        return filter.toString();
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
}
