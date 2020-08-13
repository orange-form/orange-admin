<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="统计日期">
          <date-range class="filter-item" v-model="formCourseStats.formFilter.statsDate" :clearable="true" :allowTypes="['day']" align="left"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd hh:mm:ss" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormCourseStats(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-form ref="formCourseStats" :model="formData" class="full-width-input" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col class="table-box" :span="12">
          <el-card class="base-card mb20" style="height: 350px" shadow="never" :body-style="{padding: '0px'}">
            <div slot="header" class="base-card-header">
              <span>课程统计（年级）</span>
              <div class="base-card-operation">
              </div>
            </div>
            <div class="card-chart" style="height: calc(350px - 50px);">
              <ve-pie class="chart-border" height="100%"
                :extend="formCourseStats.courseStatsByGradeId.chartOption"
                :data="formCourseStats.courseStatsByGradeId.impl.chartData"
                :settings="formCourseStats.courseStatsByGradeId.chartSetting"
                :loading="formCourseStats.courseStatsByGradeId.impl.loading"
                :data-empty="formCourseStats.courseStatsByGradeId.impl.dataEmpty"
                :after-set-option-once="onCourseStatsByGradeIdOptionSet" />
            </div>
          </el-card>
        </el-col>
        <el-col class="table-box" :span="12">
          <el-card class="base-card mb20" style="height: 350px" shadow="never" :body-style="{padding: '0px'}">
            <div slot="header" class="base-card-header">
              <span>课程统计（学科）</span>
              <div class="base-card-operation">
              </div>
            </div>
            <div class="card-chart" style="height: calc(350px - 50px);">
              <ve-pie class="chart-border" height="100%"
                :extend="formCourseStats.courseStatsBySubject.chartOption"
                :data="formCourseStats.courseStatsBySubject.impl.chartData"
                :settings="formCourseStats.courseStatsBySubject.chartSetting"
                :loading="formCourseStats.courseStatsBySubject.impl.loading"
                :data-empty="formCourseStats.courseStatsBySubject.impl.dataEmpty"
                :after-set-option-once="onCourseStatsBySubjectOptionSet" />
            </div>
          </el-card>
        </el-col>
        <el-col class="table-box" :span="24">
          <el-card class="base-card" style="height: 350px" shadow="never" :body-style="{padding: '0px'}">
            <div slot="header" class="base-card-header">
              <span>课程流水统计</span>
              <div class="base-card-operation">
              </div>
            </div>
            <div class="card-chart" style="height: calc(350px - 50px);">
              <ve-histogram class="chart-border" height="100%"
                :extend="formCourseStats.CourseTransStats.chartOption"
                :data="formCourseStats.CourseTransStats.impl.chartData"
                :settings="formCourseStats.CourseTransStats.chartSetting"
                :loading="formCourseStats.CourseTransStats.impl.loading"
                :data-empty="formCourseStats.CourseTransStats.impl.dataEmpty"
                :after-set-option-once="onCourseTransStatsOptionSet"/>
            </div>
          </el-card>
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
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { CourseTransStatsController, DictionaryController } from '@/api';

export default {
  name: 'formCourseStats',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formData: {
        CourseTransStats: {
          statsId: undefined,
          statsDate: undefined,
          subjectId: undefined,
          gradeId: undefined,
          gradeName: undefined,
          courseId: undefined,
          courseName: undefined,
          studentAttendCount: undefined,
          studentFlowerAmount: undefined,
          studentFlowerCount: undefined,
          isDatasourceInit: false
        }
      },
      formCourseStats: {
        formFilter: {
          statsDate: []
        },
        formFilterCopy: {
          statsDate: []
        },
        courseStatsByGradeId: {
          impl: new ChartWidget(this.loadCourseStatsByGradeIdData, this.loadCourseStatsByGradeIdVerify,
            ['gradeIdShowName', 'studentFlowerAmount']),
          chartOption: defaultPieChartOption,
          chartSetting: {
            radius: 80,
            dimension: 'gradeIdShowName',
            metrics: 'studentFlowerAmount',
            labelMap: {
              'studentFlowerAmount': '献花数量'
            }
          }
        },
        courseStatsBySubject: {
          impl: new ChartWidget(this.loadCourseStatsBySubjectData, this.loadCourseStatsBySubjectVerify,
            ['subjectIdShowName', 'studentFlowerAmount']),
          chartOption: defaultPieChartOption,
          chartSetting: {
            radius: 80,
            dimension: 'subjectIdShowName',
            metrics: 'studentFlowerAmount',
            labelMap: {
              'studentFlowerAmount': '献花数量'
            }
          }
        },
        CourseTransStats: {
          impl: new ChartWidget(this.loadCourseTransStatsData, this.loadCourseTransStatsVerify,
            ['statsDateShowName', 'studentAttendCount', 'studentFlowerAmount', 'studentFlowerCount']),
          chartOption: defaultBarChartOption,
          chartSetting: {
            dimension: ['statsDateShowName'],
            metrics: ['studentAttendCount', 'studentFlowerAmount', 'studentFlowerCount'],
            labelMap: {
              'studentAttendCount': '上课次数',
              'studentFlowerAmount': '献花数量',
              'studentFlowerCount': '献花次数'
            }
          }
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 课程统计（年级）数据获取函数，返回Primise
     */
    loadCourseStatsByGradeIdData () {
      let params = {
        groupParam: [
          {
            fieldName: 'gradeId'
          }
        ],
        courseTransStatsFilter: {
          statsDateStart: Array.isArray(this.formCourseStats.formFilterCopy.statsDate) ? this.formCourseStats.formFilterCopy.statsDate[0] : undefined,
          statsDateEnd: Array.isArray(this.formCourseStats.formFilterCopy.statsDate) ? this.formCourseStats.formFilterCopy.statsDate[1] : undefined
        }
      }
      return new Promise((resolve, reject) => {
        CourseTransStatsController.listWithGroup(this, params).then(res => {
          resolve({
            dataList: res.data.dataList.map((item) => {
              return {...item, gradeIdShowName: item.gradeIdDictMap.name};
            }),
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 课程统计（年级）数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadCourseStatsByGradeIdVerify () {
      this.formCourseStats.formFilterCopy.statsDate = this.formCourseStats.formFilter.statsDate;
      return true;
    },
    /**
     * 获取课程统计（年级）的echarts实例
     */
    onCourseStatsByGradeIdOptionSet (echarts) {
      echarts.resize();
      this.formCourseStats.courseStatsByGradeId.impl.chartObject = echarts;
    },
    /**
     * 课程统计（学科）数据获取函数，返回Primise
     */
    loadCourseStatsBySubjectData () {
      let params = {
        groupParam: [
          {
            fieldName: 'subjectId'
          }
        ],
        courseTransStatsFilter: {
          statsDateStart: Array.isArray(this.formCourseStats.formFilterCopy.statsDate) ? this.formCourseStats.formFilterCopy.statsDate[0] : undefined,
          statsDateEnd: Array.isArray(this.formCourseStats.formFilterCopy.statsDate) ? this.formCourseStats.formFilterCopy.statsDate[1] : undefined
        }
      }
      return new Promise((resolve, reject) => {
        CourseTransStatsController.listWithGroup(this, params).then(res => {
          resolve({
            dataList: res.data.dataList.map((item) => {
              return {...item, subjectIdShowName: item.subjectIdDictMap.name};
            }),
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 课程统计（学科）数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadCourseStatsBySubjectVerify () {
      this.formCourseStats.formFilterCopy.statsDate = this.formCourseStats.formFilter.statsDate;
      return true;
    },
    /**
     * 获取课程统计（学科）的echarts实例
     */
    onCourseStatsBySubjectOptionSet (echarts) {
      echarts.resize();
      this.formCourseStats.courseStatsBySubject.impl.chartObject = echarts;
    },
    /**
     * 课程流水统计数据获取函数，返回Primise
     */
    loadCourseTransStatsData () {
      let params = {
        groupParam: [
          {
            fieldName: 'statsDate'
          }
        ],
        courseTransStatsFilter: {
          statsDateStart: Array.isArray(this.formCourseStats.formFilterCopy.statsDate) ? this.formCourseStats.formFilterCopy.statsDate[0] : undefined,
          statsDateEnd: Array.isArray(this.formCourseStats.formFilterCopy.statsDate) ? this.formCourseStats.formFilterCopy.statsDate[1] : undefined
        }
      }
      return new Promise((resolve, reject) => {
        CourseTransStatsController.listWithGroup(this, params).then(res => {
          resolve({
            dataList: res.data.dataList.map((item) => {
              return {...item, statsDateShowName: this.formatDateByStatsType(item.statsDate, this.formCourseStats.CourseTransStats.statsType)};
            }),
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 课程流水统计数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadCourseTransStatsVerify () {
      this.formCourseStats.formFilterCopy.statsDate = this.formCourseStats.formFilter.statsDate;
      return true;
    },
    /**
     * 获取课程流水统计的echarts实例
     */
    onCourseTransStatsOptionSet (echarts) {
      echarts.resize();
      this.formCourseStats.CourseTransStats.impl.chartObject = echarts;
    },
    /**
     * 更新课程统计
     */
    refreshFormCourseStats (reloadData = false) {
      this.formCourseStats.courseStatsByGradeId.impl.refreshChart(reloadData);
      this.formCourseStats.courseStatsBySubject.impl.refreshChart(reloadData);
      this.formCourseStats.CourseTransStats.impl.refreshChart(reloadData);
      if (!this.formCourseStats.isInit) {
        // 初始化下拉数据
      }
      this.formCourseStats.isInit = true;
    },
    onResume () {
      this.refreshFormCourseStats(true);
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormCourseStats();
    }
  },
  computed: {
  },
  created () {
    this.formInit();
  }
}
</script>
