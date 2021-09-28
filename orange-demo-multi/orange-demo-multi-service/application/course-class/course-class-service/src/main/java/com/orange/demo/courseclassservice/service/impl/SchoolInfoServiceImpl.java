package com.orange.demo.courseclassservice.service.impl;

import com.orange.demo.courseclassservice.service.*;
import com.orange.demo.courseclassservice.dao.*;
import com.orange.demo.courseclassservice.model.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 校区数据数据操作服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@Service("schoolInfoService")
public class SchoolInfoServiceImpl extends BaseService<SchoolInfo, Long> implements SchoolInfoService {

    @Autowired
    private SchoolInfoMapper schoolInfoMapper;
    @Autowired
    private AreaCodeService areaCodeService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SchoolInfo> mapper() {
        return schoolInfoMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param schoolInfo 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SchoolInfo saveNew(SchoolInfo schoolInfo) {
        schoolInfo.setSchoolId(idGenerator.nextLongId());
        schoolInfoMapper.insert(schoolInfo);
        return schoolInfo;
    }

    /**
     * 更新数据对象。
     *
     * @param schoolInfo         更新的对象。
     * @param originalSchoolInfo 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SchoolInfo schoolInfo, SchoolInfo originalSchoolInfo) {
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        return schoolInfoMapper.updateByPrimaryKey(schoolInfo) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param schoolId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long schoolId) {
        // 这里先删除主数据
        return this.removeById(schoolId);
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSchoolInfoListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SchoolInfo> getSchoolInfoList(SchoolInfo filter, String orderBy) {
        return schoolInfoMapper.getSchoolInfoList(null, null, filter, orderBy);
    }

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSchoolInfoListWithRelation)方法。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public <M> List<SchoolInfo> getSchoolInfoList(
            String inFilterField, Set<M> inFilterValues, SchoolInfo filter, String orderBy) {
        String inFilterColumn = MyModelUtil.mapToColumnName(inFilterField, SchoolInfo.class);
        return schoolInfoMapper.getSchoolInfoList(inFilterColumn, inFilterValues, filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getSchoolInfoList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    @Override
    public List<SchoolInfo> getSchoolInfoListWithRelation(SchoolInfo filter, String orderBy) {
        List<SchoolInfo> resultList = schoolInfoMapper.getSchoolInfoList(null, null, filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 同时还包含主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSchoolInfoList)，以便获取更好的查询性能。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    @Override
    public <M> List<SchoolInfo> getSchoolInfoListWithRelation(
            String inFilterField, Set<M> inFilterValues, SchoolInfo filter, String orderBy) {
        String inFilterColumn = MyModelUtil.mapToColumnName(inFilterField, SchoolInfo.class);
        List<SchoolInfo> resultList =
                schoolInfoMapper.getSchoolInfoList(inFilterColumn, inFilterValues, filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly(), batchSize);
        return resultList;
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param schoolInfo         最新数据对象。
     * @param originalSchoolInfo 原有数据对象。
     * @return 数据全部正确返回true，否则false，同时返回具体的错误信息。
     */
    @Override
    public CallResult verifyRelatedData(SchoolInfo schoolInfo, SchoolInfo originalSchoolInfo) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        if (this.needToVerify(schoolInfo, originalSchoolInfo, SchoolInfo::getProvinceId)
                && !areaCodeService.existId(schoolInfo.getProvinceId())) {
            return CallResult.error(String.format(errorMessageFormat, "所在省份"));
        }
        if (this.needToVerify(schoolInfo, originalSchoolInfo, SchoolInfo::getCityId)
                && !areaCodeService.existId(schoolInfo.getCityId())) {
            return CallResult.error(String.format(errorMessageFormat, "所在城市"));
        }
        return CallResult.ok();
    }
}
