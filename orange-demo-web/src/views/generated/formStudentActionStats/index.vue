<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="统计日期">
          <date-range class="filter-item" v-model="formStudentActionStats.formFilter.statsDate" :clearable="true" :allowTypes="['day']" align="left"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd hh:mm:ss" />
        </el-form-item>
        <el-form-item label="年级">
          <el-select class="filter-item" v-model="formStudentActionStats.formFilter.gradeId" :clearable="true" filterable
            placeholder="年级" :loading="formStudentActionStats.gradeId.impl.loading"
            @visible-change="formStudentActionStats.gradeId.impl.onVisibleChange"
            @change="onGradeIdValueChange">
            <el-option v-for="item in formStudentActionStats.gradeId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormStudentActionStats(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formStudentActionStats.StudentActionStats.impl.dataList" size="mini" @sort-change="formStudentActionStats.StudentActionStats.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formStudentActionStats.StudentActionStats.impl.getTableIndex" />
          <el-table-column label="统计日期">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.statsDate, 'day')}}</span>
            </template>
          </el-table-column>
          <el-table-column label="所属年级" prop="gradeIdDictMap.name" />
          <el-table-column label="购课学币" prop="buyCourseAmount">
            <template slot-scope="scope">
              <a href="javascript:void(0);" @click="onFormBuyCourseDetailClick(scope.row)">{{scope.row.buyCourseAmount}}</a>
            </template>
          </el-table-column>
          <el-table-column label="购买视频" prop="buyVideoAmount">
            <template slot-scope="scope">
              <a href="javascript:void(0);" @click="onFormBuyVideoDetailClick(scope.row)">{{scope.row.buyVideoAmount}}</a>
            </template>
          </el-table-column>
          <el-table-column label="购买作业" prop="buyPaperAmount">
            <template slot-scope="scope">
              <a href="javascript:void(0);" @click="onFormBuyPaperDetailClick(scope.row)">{{scope.row.buyPaperAmount}}</a>
            </template>
          </el-table-column>
          <el-table-column label="购买鲜花" prop="buyFlowerAmount">
            <template slot-scope="scope">
              <a href="javascript:void(0);" @click="onFormBuyFlowerDetailClick(scope.row)">{{scope.row.buyFlowerAmount}}</a>
            </template>
          </el-table-column>
          <el-table-column label="充值学币" prop="rechargeCoinAmount">
            <template slot-scope="scope">
              <a href="javascript:void(0);" @click="onFormBuyCoinDetailClick(scope.row)">{{scope.row.rechargeCoinAmount}}</a>
            </template>
          </el-table-column>
          <el-table-column label="上课次数" prop="doCourseCount">
            <template slot-scope="scope">
              <a href="javascript:void(0);" @click="onFormDoCourseDetailClick(scope.row)">{{scope.row.doCourseCount}}</a>
            </template>
          </el-table-column>
          <el-table-column label="观看视频" prop="watchVideoCount">
            <template slot-scope="scope">
              <a href="javascript:void(0);" @click="onFormWatchVideoDetailClick(scope.row)">{{scope.row.watchVideoCount}}</a>
            </template>
          </el-table-column>
          <el-table-column label="做题数量" prop="doExerciseCount">
            <template slot-scope="scope">
              <a href="javascript:void(0);" @click="onFormRfreshDetailClick(scope.row)">{{scope.row.doExerciseCount}}</a>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formStudentActionStats.StudentActionStats.impl.totalCount"
            :current-page="formStudentActionStats.StudentActionStats.impl.currentPage"
            :page-size="formStudentActionStats.StudentActionStats.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formStudentActionStats.StudentActionStats.impl.onCurrentPageChange"
            @size-change="formStudentActionStats.StudentActionStats.impl.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { StudentActionStatsController, DictionaryController } from '@/api';

export default {
  name: 'formStudentActionStats',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formStudentActionStats: {
        formFilter: {
          statsDate: [],
          gradeId: undefined
        },
        formFilterCopy: {
          statsDate: [],
          gradeId: undefined
        },
        gradeId: {
          impl: new DropdownWidget(this.loadGradeIdDropdownList)
        },
        StudentActionStats: {
          impl: new TableWidget(this.loadStudentActionStatsData, this.loadStudentActionStatsVerify, true, 'statsDate', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 学生行为统计数据获取函数，返回Primise
     */
    loadStudentActionStatsData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        groupParam: [
          {
            fieldName: 'statsDate'
          },
          {
            fieldName: 'gradeId'
          }
        ],
        studentActionStatsFilter: {
          statsDateStart: Array.isArray(this.formStudentActionStats.formFilterCopy.statsDate) ? this.formStudentActionStats.formFilterCopy.statsDate[0] : undefined,
          statsDateEnd: Array.isArray(this.formStudentActionStats.formFilterCopy.statsDate) ? this.formStudentActionStats.formFilterCopy.statsDate[1] : undefined,
          gradeId: this.formStudentActionStats.formFilterCopy.gradeId
        }
      }
      return new Promise((resolve, reject) => {
        StudentActionStatsController.listWithGroup(this, params).then(res => {
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
     * 学生行为统计数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadStudentActionStatsVerify () {
      this.formStudentActionStats.formFilterCopy.statsDate = this.formStudentActionStats.formFilter.statsDate;
      this.formStudentActionStats.formFilterCopy.gradeId = this.formStudentActionStats.formFilter.gradeId;
      return true;
    },
    /**
     * 年级下拉数据获取函数
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
     * 年级选中值改变
     */
    onGradeIdValueChange (value) {
    },
    /**
     * 更新学生行为统计
     */
    refreshFormStudentActionStats (reloadData = false) {
      if (reloadData) {
        this.formStudentActionStats.StudentActionStats.impl.refreshTable(true, 1);
      } else {
        this.formStudentActionStats.StudentActionStats.impl.refreshTable();
      }
      if (!this.formStudentActionStats.isInit) {
        // 初始化下拉数据
      }
      this.formStudentActionStats.isInit = true;
    },
    /**
     * 学生行为详情
     */
    onFormBuyCourseDetailClick (row) {
      let params = {
        actionType: this.StudentActionType.BUY_COURSE,
        createTime: this.getDateRangeFilter(row.statsDate),
        gradeId: row.gradeId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formStudentActionDetail', query: params});
    },
    /**
     * 学生行为详情
     */
    onFormBuyVideoDetailClick (row) {
      let params = {
        actionType: this.StudentActionType.BUY_VIDEO_COURSE,
        createTime: this.getDateRangeFilter(row.statsDate),
        gradeId: row.gradeId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formStudentActionDetail', query: params});
    },
    /**
     * 学生行为详情
     */
    onFormBuyFlowerDetailClick (row) {
      let params = {
        actionType: this.StudentActionType.BUY_FLOWER,
        createTime: this.getDateRangeFilter(row.statsDate),
        gradeId: row.gradeId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formStudentActionDetail', query: params});
    },
    /**
     * 学生行为详情
     */
    onFormBuyPaperDetailClick (row) {
      let params = {
        actionType: this.StudentActionType.BUY_PAPER,
        createTime: this.getDateRangeFilter(row.statsDate),
        gradeId: row.gradeId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formStudentActionDetail', query: params});
    },
    /**
     * 学生行为详情
     */
    onFormBuyCoinDetailClick (row) {
      let params = {
        actionType: this.StudentActionType.RECHARGE,
        createTime: this.getDateRangeFilter(row.statsDate),
        gradeId: row.gradeId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formStudentActionDetail', query: params});
    },
    /**
     * 学生行为详情
     */
    onFormDoCourseDetailClick (row) {
      let params = {
        actionType: this.StudentActionType.SIGNIN_COURSE,
        createTime: this.getDateRangeFilter(row.statsDate),
        gradeId: row.gradeId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formStudentActionDetail', query: params});
    },
    /**
     * 学生行为详情
     */
    onFormWatchVideoDetailClick (row) {
      let params = {
        actionType: this.StudentActionType.WATCH_VIDEO,
        createTime: this.getDateRangeFilter(row.statsDate),
        gradeId: row.gradeId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formStudentActionDetail', query: params});
    },
    /**
     * 学生行为详情
     */
    onFormRfreshDetailClick (row) {
      let params = {
        actionType: this.StudentActionType.DO_PAPER,
        createTime: this.getDateRangeFilter(row.statsDate),
        gradeId: row.gradeId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formStudentActionDetail', query: params});
    },
    onResume () {
      this.refreshFormStudentActionStats();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormStudentActionStats();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
