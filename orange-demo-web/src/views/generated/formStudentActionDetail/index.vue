<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="统计日期">
          <date-range class="filter-item" v-model="formStudentActionDetail.formFilter.statsDate" :clearable="true" :allowTypes="['day']" align="left"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd hh:mm:ss" />
        </el-form-item>
        <el-form-item label="所属年级">
          <el-select class="filter-item" v-model="formStudentActionDetail.formFilter.gradeId" :clearable="true" filterable
            placeholder="所属年级" :loading="formStudentActionDetail.gradeId.impl.loading"
            @visible-change="formStudentActionDetail.gradeId.impl.onVisibleChange"
            @change="onGradeIdValueChange">
            <el-option v-for="item in formStudentActionDetail.gradeId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="行为类型">
          <el-select class="filter-item" v-model="formStudentActionDetail.formFilter.actionType" :clearable="true" filterable
            placeholder="行为类型" :loading="formStudentActionDetail.actionType.impl.loading"
            @visible-change="formStudentActionDetail.actionType.impl.onVisibleChange"
            @change="onActionTypeValueChange">
            <el-option v-for="item in formStudentActionDetail.actionType.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormStudentActionDetail(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formStudentActionDetail.StudentActionTrans.impl.dataList" size="mini" @sort-change="formStudentActionDetail.StudentActionTrans.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formStudentActionDetail.StudentActionTrans.impl.getTableIndex" />
          <el-table-column label="学生名称" prop="studentName">
          </el-table-column>
          <el-table-column label="所属校区" prop="schoolIdDictMap.name">
          </el-table-column>
          <el-table-column label="所属年级" prop="gradeIdDictMap.name">
          </el-table-column>
          <el-table-column label="行为类型" prop="actionTypeDictMap.name">
          </el-table-column>
          <el-table-column label="设备类型" prop="deviceTypeDictMap.name">
          </el-table-column>
          <el-table-column label="观看视频" prop="watchVideoSeconds">
          </el-table-column>
          <el-table-column label="购买鲜花" prop="flowerCount">
          </el-table-column>
          <el-table-column label="购买作业" prop="paperCount">
          </el-table-column>
          <el-table-column label="购买视频" prop="videoCount">
          </el-table-column>
          <el-table-column label="购买课程" prop="courseCount">
          </el-table-column>
          <el-table-column label="充值学币" prop="coinCount">
          </el-table-column>
          <el-table-column label="发生时间" prop="createTime">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formStudentActionDetail.StudentActionTrans.impl.totalCount"
            :current-page="formStudentActionDetail.StudentActionTrans.impl.currentPage"
            :page-size="formStudentActionDetail.StudentActionTrans.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formStudentActionDetail.StudentActionTrans.impl.onCurrentPageChange"
            @size-change="formStudentActionDetail.StudentActionTrans.impl.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
    <label v-if="closeVisible == '1'" class="page-close-box">
      <el-button type="text" @click="onCancel(true)" icon="el-icon-close" />
    </label>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { DictionaryController, StudentActionTransController } from '@/api';

export default {
  name: 'formStudentActionDetail',
  props: {
    actionType: {
      default: undefined
    },
    createTime: {
      default: undefined
    },
    gradeId: {
      default: undefined
    },
    closeVisible: {
      type: [Number, String],
      default: 0
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin, cachedPageChildMixin],
  data () {
    return {
      formStudentActionDetail: {
        formFilter: {
          statsDate: this.createTime,
          gradeId: this.parseParams(this.gradeId, 'integer'),
          actionType: this.parseParams(this.actionType, 'integer')
        },
        formFilterCopy: {
          statsDate: this.createTime,
          gradeId: this.parseParams(this.gradeId, 'integer'),
          actionType: this.parseParams(this.actionType, 'integer')
        },
        gradeId: {
          impl: new DropdownWidget(this.loadGradeIdDropdownList)
        },
        actionType: {
          impl: new DropdownWidget(this.loadActionTypeDropdownList)
        },
        StudentActionTrans: {
          impl: new TableWidget(this.loadStudentActionTransData, this.loadStudentActionTransVerify, true, 'createTime', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      this.removeCachePage(this.$options.name);
      this.refreshParentCachedPage = isSuccess;
      this.$router.go(-1);
    },
    /**
     * 学生行为流水数据获取函数，返回Primise
     */
    loadStudentActionTransData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        studentActionTransFilter: {
          gradeId: this.formStudentActionDetail.formFilterCopy.gradeId,
          actionType: this.formStudentActionDetail.formFilterCopy.actionType,
          createTimeStart: Array.isArray(this.formStudentActionDetail.formFilterCopy.statsDate) ? this.formStudentActionDetail.formFilterCopy.statsDate[0] : undefined,
          createTimeEnd: Array.isArray(this.formStudentActionDetail.formFilterCopy.statsDate) ? this.formStudentActionDetail.formFilterCopy.statsDate[1] : undefined
        }
      }
      return new Promise((resolve, reject) => {
        StudentActionTransController.list(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 学生行为流水数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadStudentActionTransVerify () {
      this.formStudentActionDetail.formFilterCopy.gradeId = this.formStudentActionDetail.formFilter.gradeId;
      this.formStudentActionDetail.formFilterCopy.actionType = this.formStudentActionDetail.formFilter.actionType;
      this.formStudentActionDetail.formFilterCopy.statsDate = this.formStudentActionDetail.formFilter.statsDate;
      return true;
    },
    /**
     * 所属年级下拉数据获取函数
     */
    loadGradeIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictGrade(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所属年级选中值改变
     */
    onGradeIdValueChange (value) {
    },
    /**
     * 行为类型下拉数据获取函数
     */
    loadActionTypeDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictStudentActionType(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 行为类型选中值改变
     */
    onActionTypeValueChange (value) {
    },
    /**
     * 更新学生行为详情
     */
    refreshFormStudentActionDetail (reloadData = false) {
      if (reloadData) {
        this.formStudentActionDetail.StudentActionTrans.impl.refreshTable(true, 1);
      } else {
        this.formStudentActionDetail.StudentActionTrans.impl.refreshTable();
      }
      if (!this.formStudentActionDetail.isInit) {
        // 初始化下拉数据
        this.formStudentActionDetail.gradeId.impl.onVisibleChange(true).catch(e => {});
        this.formStudentActionDetail.actionType.impl.onVisibleChange(true).catch(e => {});
      }
      this.formStudentActionDetail.isInit = true;
    },
    onResume () {
      this.refreshFormStudentActionDetail();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormStudentActionDetail();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
