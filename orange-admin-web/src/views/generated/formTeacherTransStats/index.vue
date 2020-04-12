<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="统计日期">
          <date-range class="filter-item" v-model="formTeacherTransStats.formFilter.statsDate" :clearable="true" :allowTypes="['day']" align="left"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd hh:mm:ss" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormTeacherTransStats(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-form ref="formTeacherTransStats" :model="formData" class="full-width-input" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col class="table-box" :span="24">
          <ve-line height="300px"
            :extend="formTeacherTransStats.TeacherTransStats.chartOption"
            :data="formTeacherTransStats.TeacherTransStats.impl.chartData"
            :settings="formTeacherTransStats.TeacherTransStats.chartSetting"
            :loading="formTeacherTransStats.TeacherTransStats.impl.loading"
            :data-empty="formTeacherTransStats.TeacherTransStats.impl.dataEmpty"
            :after-set-option-once="onTeacherTransStatsOptionSet"/>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { defaultLineChartOption, defaultBarChartOption, defaultPieChartOption } from '@/utils/chartOption.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { TeacherTransStatsController, DictionaryController } from '@/api';

export default {
  name: 'formTeacherTransStats',
  props: {
    teacherId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        TeacherTransStats: {
          statsId: undefined,
          statsDate: undefined,
          statsMonth: undefined,
          provinceId: undefined,
          cityId: undefined,
          schoolId: undefined,
          schoolName: undefined,
          teacherId: undefined,
          teacherName: undefined,
          videoWatchCount: undefined,
          flowerCount: undefined,
          newStudent: undefined,
          sysDept: {
            deptId: undefined,
            deptName: undefined,
            showOrder: undefined,
            deletedFlag: undefined,
            createUserId: undefined,
            createUsername: undefined,
            createTime: undefined,
            updateTime: undefined
          },
          teacher: {
            teacherId: undefined,
            teacherName: undefined,
            birthday: undefined,
            gender: undefined,
            subjectId: undefined,
            level: undefined,
            flowerCount: undefined,
            schoolId: undefined,
            userId: undefined,
            registerDate: undefined,
            available: undefined
          },
          isDatasourceInit: false
        }
      },
      formTeacherTransStats: {
        formFilter: {
          statsDate: []
        },
        formFilterCopy: {
          statsDate: []
        },
        TeacherTransStats: {
          impl: new ChartWidget(this.loadTeacherTransStatsData, this.loadTeacherTransStatsVerify,
            ['statsDateShowName', 'videoWatchCount', 'newStudent', 'flowerCount']),
          chartOption: defaultLineChartOption,
          chartSetting: {
            dimension: ['statsDateShowName'],
            metrics: ['videoWatchCount', 'newStudent', 'flowerCount'],
            labelMap: {
              'videoWatchCount': '视频观看数量',
              'newStudent': '新增学生数量',
              'flowerCount': '献花数量'
            }
          }
        },
        isInit: false
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    /**
     * 老师流水统计数据获取函数，返回Primise
     */
    loadTeacherTransStatsData () {
      let params = {
        groupParam: [
          {
            fieldName: 'statsDate'
          }
        ],
        teacherTransStatsFilter: {
          statsDateStart: Array.isArray(this.formTeacherTransStats.formFilterCopy.statsDate) ? this.formTeacherTransStats.formFilterCopy.statsDate[0] : undefined,
          statsDateEnd: Array.isArray(this.formTeacherTransStats.formFilterCopy.statsDate) ? this.formTeacherTransStats.formFilterCopy.statsDate[1] : undefined,
          teacherId: this.teacherId
        }
      }
      return new Promise((resolve, reject) => {
        TeacherTransStatsController.listWithGroup(this, params).then(res => {
          resolve({
            dataList: res.data.dataList.map((item) => {
              return {...item, statsDateShowName: this.formatDateByStatsType(item.statsDate, this.formTeacherTransStats.TeacherTransStats.statsType)};
            }),
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 老师流水统计数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadTeacherTransStatsVerify () {
      this.formTeacherTransStats.formFilterCopy.statsDate = this.formTeacherTransStats.formFilter.statsDate;
      return true;
    },
    /**
     * 获取老师流水统计的echarts实例
     */
    onTeacherTransStatsOptionSet (echarts) {
      echarts.resize();
      this.formTeacherTransStats.TeacherTransStats.impl.chartObject = echarts;
    },
    /**
     * 更新老师个人统计
     */
    refreshFormTeacherTransStats (reloadData = false) {
      this.formTeacherTransStats.TeacherTransStats.impl.refreshChart(reloadData);
      if (!this.formTeacherTransStats.isInit) {
        // 初始化下拉数据
      }
      this.formTeacherTransStats.isInit = true;
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormTeacherTransStats();
    }
  },
  computed: {
  },
  created () {
    this.formInit();
  }
}
</script>
