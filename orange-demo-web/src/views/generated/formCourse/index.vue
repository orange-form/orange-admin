<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="所属年级">
          <el-select class="filter-item" v-model="formCourse.formFilter.gradeId" :clearable="true" filterable
            placeholder="所属年级" :loading="formCourse.gradeId.impl.loading"
            @visible-change="formCourse.gradeId.impl.onVisibleChange"
            @change="onGradeIdValueChange">
            <el-option v-for="item in formCourse.gradeId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属学科">
          <el-select class="filter-item" v-model="formCourse.formFilter.subjectId" :clearable="true" filterable
            placeholder="所属学科" :loading="formCourse.subjectId.impl.loading"
            @visible-change="formCourse.subjectId.impl.onVisibleChange"
            @change="onSubjectIdValueChange">
            <el-option v-for="item in formCourse.subjectId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程难度">
          <el-select class="filter-item" v-model="formCourse.formFilter.difficulty" :clearable="true" filterable
            placeholder="课程难度" :loading="formCourse.difficulty.impl.loading"
            @visible-change="formCourse.difficulty.impl.onVisibleChange"
            @change="onDifficultyValueChange">
            <el-option v-for="item in formCourse.difficulty.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input class="filter-item" v-model="formCourse.formFilter.courseName"
            :clearable="true" placeholder="课程名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormCourse(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formCourse:formCourse:formCreateCourse')"
          @click="onFormCreateCourseClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formCourse.Course.impl.dataList" size="mini" @sort-change="formCourse.Course.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formCourse.Course.impl.getTableIndex" />
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
          <el-table-column label="课程价格" prop="price" sortable="custom">
          </el-table-column>
          <el-table-column label="课程图片" min-width="180px">
            <template slot-scope="scope">
              <el-image v-for="item in parseUploadData(scope.row.pictureUrl, {courseId: scope.row.courseId, fieldName: 'pictureUrl', asImage: true})"
                :preview-src-list="getPictureList(scope.row.pictureUrl, {courseId: scope.row.courseId, fieldName: 'pictureUrl', asImage: true})"
                class="table-cell-image" :key="item.url" :src="item.url" fit="fill">
                <div slot="error" class="table-cell-image">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click="onFormEditCourseClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formCourse:formCourse:formEditCourse')">
                编辑
              </el-button>
              <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formCourse:formCourse:delete')">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formCourse.Course.impl.totalCount"
            :current-page="formCourse.Course.impl.currentPage"
            :page-size="formCourse.Course.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formCourse.Course.impl.onCurrentPageChange"
            @size-change="formCourse.Course.impl.onPageSizeChange">
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
import { CourseController, DictionaryController } from '@/api';
import formCreateCourse from '@/views/generated/formCreateCourse';
import formEditCourse from '@/views/generated/formEditCourse';

export default {
  name: 'formCourse',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formCourse: {
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
          impl: new TableWidget(this.loadCourseData, this.loadCourseVerify, true, 'createTime', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 课程数据数据获取函数，返回Primise
     */
    loadCourseData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        courseFilter: {
          courseName: this.formCourse.formFilterCopy.courseName,
          difficulty: this.formCourse.formFilterCopy.difficulty,
          gradeId: this.formCourse.formFilterCopy.gradeId,
          subjectId: this.formCourse.formFilterCopy.subjectId
        }
      }
      return new Promise((resolve, reject) => {
        CourseController.list(this, params).then(res => {
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
     * 课程数据数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadCourseVerify () {
      this.formCourse.formFilterCopy.courseName = this.formCourse.formFilter.courseName;
      this.formCourse.formFilterCopy.difficulty = this.formCourse.formFilter.difficulty;
      this.formCourse.formFilterCopy.gradeId = this.formCourse.formFilter.gradeId;
      this.formCourse.formFilterCopy.subjectId = this.formCourse.formFilter.subjectId;
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
     * 所属学科下拉数据获取函数
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
     * 所属学科选中值改变
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
     * 更新课程管理
     */
    refreshFormCourse (reloadData = false) {
      if (reloadData) {
        this.formCourse.Course.impl.refreshTable(true, 1);
      } else {
        this.formCourse.Course.impl.refreshTable();
      }
      if (!this.formCourse.isInit) {
        // 初始化下拉数据
      }
      this.formCourse.isInit = true;
    },
    /**
     * 新建
     */
    onFormCreateCourseClick () {
      let params = {};

      this.$dialog.show('新建', formCreateCourse, {
        area: ['800px']
      }, params).then(res => {
        this.refreshFormCourse();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onFormEditCourseClick (row) {
      let params = {
        courseId: row.courseId
      };

      this.$dialog.show('编辑', formEditCourse, {
        area: ['800px']
      }, params).then(res => {
        this.formCourse.Course.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        courseId: row.courseId
      };

      this.$confirm('是否删除此课程？').then(res => {
        CourseController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formCourse.Course.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormCourse();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormCourse();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
