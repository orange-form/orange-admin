<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formClassCourse:formClassCourse:formSetClassCourse')"
          @click="onFormSetClassCourseClick()">
          设置班级课程
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formClassCourse.Course.impl.dataList" size="mini" @sort-change="formClassCourse.Course.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formClassCourse.Course.impl.getTableIndex" />
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
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click="onDeleteClassCourseClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formClassCourse:formClassCourse:deleteClassCourse')">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formClassCourse.Course.impl.totalCount"
            :current-page="formClassCourse.Course.impl.currentPage"
            :page-size="formClassCourse.Course.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formClassCourse.Course.impl.onCurrentPageChange"
            @size-change="formClassCourse.Course.impl.onPageSizeChange">
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
import { StudentClassController, DictionaryController } from '@/api';

export default {
  name: 'formClassCourse',
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
      formClassCourse: {
        formFilter: {
        },
        formFilterCopy: {
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
    /**
     * 班级课程数据获取函数，返回Primise
     */
    loadCourseData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        classId: this.classId
      }
      return new Promise((resolve, reject) => {
        StudentClassController.listClassCourse(this, params).then(res => {
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
      return true;
    },
    /**
     * 更新班级课程
     */
    refreshFormClassCourse (reloadData = false) {
      if (reloadData) {
        this.formClassCourse.Course.impl.refreshTable(true, 1);
      } else {
        this.formClassCourse.Course.impl.refreshTable();
      }
      if (!this.formClassCourse.isInit) {
        // 初始化下拉数据
      }
      this.formClassCourse.isInit = true;
    },
    /**
     * 设置班级课程
     */
    onFormSetClassCourseClick () {
      let params = {
        classId: this.classId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formSetClassCourse', query: params});
    },
    /**
     * 移除
     */
    onDeleteClassCourseClick (row) {
      let params = {
        classId: this.classId,
        courseId: row.courseId
      };

      this.$confirm('是否从班级中移除此课程？').then(res => {
        StudentClassController.deleteClassCourse(this, params).then(res => {
          this.$message.success('移除成功');
          this.formClassCourse.Course.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormClassCourse();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormClassCourse();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
