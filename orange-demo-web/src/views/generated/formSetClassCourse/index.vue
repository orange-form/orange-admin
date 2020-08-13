<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="所属年级">
          <el-select class="filter-item" v-model="formSetClassCourse.formFilter.gradeId" :clearable="true" filterable
            placeholder="所属年级" :loading="formSetClassCourse.gradeId.impl.loading"
            @visible-change="formSetClassCourse.gradeId.impl.onVisibleChange"
            @change="onGradeIdValueChange">
            <el-option v-for="item in formSetClassCourse.gradeId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属科目">
          <el-select class="filter-item" v-model="formSetClassCourse.formFilter.subjectId" :clearable="true" filterable
            placeholder="所属科目" :loading="formSetClassCourse.subjectId.impl.loading"
            @visible-change="formSetClassCourse.subjectId.impl.onVisibleChange"
            @change="onSubjectIdValueChange">
            <el-option v-for="item in formSetClassCourse.subjectId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程难度">
          <el-select class="filter-item" v-model="formSetClassCourse.formFilter.difficulty" :clearable="true" filterable
            placeholder="课程难度" :loading="formSetClassCourse.difficulty.impl.loading"
            @visible-change="formSetClassCourse.difficulty.impl.onVisibleChange"
            @change="onDifficultyValueChange">
            <el-option v-for="item in formSetClassCourse.difficulty.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input class="filter-item" v-model="formSetClassCourse.formFilter.courseName"
            :clearable="true" placeholder="课程名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormSetClassCourse(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="tableSelectRowList.length <= 0 || !checkPermCodeExist('formSetClassCourse:formSetClassCourse:addClassCourse')"
          @click="onAddClassCourseClick()">
          添加
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formSetClassCourse.Course.impl.dataList" size="mini" @sort-change="formSetClassCourse.Course.impl.onSortChange"
          @selection-change="onCourseSelectionChange" header-cell-class-name="table-header-gray">
          <el-table-column label="序号" type="index" header-align="center" align="center" width="55px" :index="formSetClassCourse.Course.impl.getTableIndex" />
          <el-table-column type="selection" header-align="center" align="center" width="55px" />
          <el-table-column label="课程名称" prop="courseName">
          </el-table-column>
          <el-table-column label="课程难度" prop="difficultyDictMap.name">
          </el-table-column>
          <el-table-column label="所属年级" prop="gradeIdDictMap.name">
          </el-table-column>
          <el-table-column label="所属学科" prop="subjectIdDictMap.name">
          </el-table-column>
          <el-table-column label="课时数量" prop="classHour">
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
            </template>
          </el-table-column>
        </el-table>
        <el-col :span="24">
          <el-row type="flex" justify="end" style="margin-top: 10px;">
            <el-pagination
              :total="formSetClassCourse.Course.impl.totalCount"
              :current-page="formSetClassCourse.Course.impl.currentPage"
              :page-size="formSetClassCourse.Course.impl.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, prev, pager, next, sizes"
              @current-change="formSetClassCourse.Course.impl.onCurrentPageChange"
              @size-change="formSetClassCourse.Course.impl.onPageSizeChange">
            </el-pagination>
          </el-row>
        </el-col>
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
import { StudentClassController, DictionaryController } from '@/api';

export default {
  name: 'formSetClassCourse',
  props: {
    classId: {
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
      tableSelectRowList: [],
      formSetClassCourse: {
        formFilter: {
          gradeId: undefined,
          subjectId: undefined,
          difficulty: undefined,
          courseName: undefined
        },
        formFilterCopy: {
          gradeId: undefined,
          subjectId: undefined,
          difficulty: undefined,
          courseName: undefined
        },
        gradeId: {
          impl: new DropdownWidget(this.loadGradeIdDropdownList)
        },
        subjectId: {
          impl: new DropdownWidget(this.loadSubjectIdDropdownList)
        },
        difficulty: {
          impl: new DropdownWidget(this.loadDifficultyDropdownList)
        },
        Course: {
          impl: new TableWidget(this.loadCourseData, this.loadCourseVerify, true)
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
    onCourseSelectionChange (values) {
      this.tableSelectRowList = values;
    },
    /**
     * 班级课程数据获取函数，返回Primise
     */
    loadCourseData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        courseFilter: {
          courseName: this.formSetClassCourse.formFilterCopy.courseName,
          difficulty: this.formSetClassCourse.formFilterCopy.difficulty,
          gradeId: this.formSetClassCourse.formFilterCopy.gradeId,
          subjectId: this.formSetClassCourse.formFilterCopy.subjectId
        },
        classId: this.classId
      }
      return new Promise((resolve, reject) => {
        StudentClassController.listNotInClassCourse(this, params).then(res => {
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
     * 班级课程数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadCourseVerify () {
      this.formSetClassCourse.formFilterCopy.courseName = this.formSetClassCourse.formFilter.courseName;
      this.formSetClassCourse.formFilterCopy.difficulty = this.formSetClassCourse.formFilter.difficulty;
      this.formSetClassCourse.formFilterCopy.gradeId = this.formSetClassCourse.formFilter.gradeId;
      this.formSetClassCourse.formFilterCopy.subjectId = this.formSetClassCourse.formFilter.subjectId;
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
     * 所属科目下拉数据获取函数
     */
    loadSubjectIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSubject(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所属科目选中值改变
     */
    onSubjectIdValueChange (value) {
    },
    /**
     * 课程难度下拉数据获取函数
     */
    loadDifficultyDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictCourseDifficult(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 课程难度选中值改变
     */
    onDifficultyValueChange (value) {
    },
    /**
     * 更新设置班级课程
     */
    refreshFormSetClassCourse (reloadData = false) {
      if (reloadData) {
        this.formSetClassCourse.Course.impl.refreshTable(true, 1);
      } else {
        this.formSetClassCourse.Course.impl.refreshTable();
      }
      if (!this.formSetClassCourse.isInit) {
        // 初始化下拉数据
      }
      this.formSetClassCourse.isInit = true;
    },
    /**
     * 添加
     */
    onAddClassCourseClick () {
      let params = {
        classId: this.classId,
        classCourseList: this.tableSelectRowList.map((item) => {
          return {
            courseId: item.courseId
          };
        })
      };

      StudentClassController.addClassCourse(this, params).then(res => {
        this.$message.success('添加成功');
        this.refreshFormSetClassCourse();
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormSetClassCourse(true);
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormSetClassCourse();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
