<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formClassStudent:formClassStudent:formSetClassStudent')"
          @click="onFormSetClassStudentClick()">
          设置班级学生
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formClassStudent.Student.impl.dataList" size="mini" @sort-change="formClassStudent.Student.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formClassStudent.Student.impl.getTableIndex" />
          <el-table-column label="姓名" prop="studentName">
          </el-table-column>
          <el-table-column label="手机号码" prop="loginMobile">
          </el-table-column>
          <el-table-column label="所属校区" prop="schoolIdDictMap.name">
          </el-table-column>
          <el-table-column label="年级" prop="gradeIdDictMap.name">
          </el-table-column>
          <el-table-column label="经验等级" prop="experienceLevelDictMap.name">
          </el-table-column>
          <el-table-column label="注册时间" prop="registerTime">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.registerTime, 'day')}}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click="onDeleteClassStudentClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formClassStudent:formClassStudent:deleteClassStudent')">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formClassStudent.Student.impl.totalCount"
            :current-page="formClassStudent.Student.impl.currentPage"
            :page-size="formClassStudent.Student.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formClassStudent.Student.impl.onCurrentPageChange"
            @size-change="formClassStudent.Student.impl.onPageSizeChange">
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
  name: 'formClassStudent',
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
      formClassStudent: {
        formFilter: {
        },
        formFilterCopy: {
        },
        Student: {
          impl: new TableWidget(this.loadStudentData, this.loadStudentVerify, true)
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
     * 班级学生数据获取函数，返回Primise
     */
    loadStudentData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        classId: this.classId
      }
      return new Promise((resolve, reject) => {
        StudentClassController.listClassStudent(this, params).then(res => {
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
     * 班级学生数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadStudentVerify () {
      return true;
    },
    /**
     * 更新班级学生
     */
    refreshFormClassStudent (reloadData = false) {
      if (reloadData) {
        this.formClassStudent.Student.impl.refreshTable(true, 1);
      } else {
        this.formClassStudent.Student.impl.refreshTable();
      }
      if (!this.formClassStudent.isInit) {
        // 初始化下拉数据
      }
      this.formClassStudent.isInit = true;
    },
    /**
     * 设置班级学生
     */
    onFormSetClassStudentClick () {
      let params = {
        classId: this.classId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formSetClassStudent', query: params});
    },
    /**
     * 移除
     */
    onDeleteClassStudentClick (row) {
      let params = {
        classId: this.classId,
        studentId: row.studentId
      };

      this.$confirm('是否从班级中移除此学生？').then(res => {
        StudentClassController.deleteClassStudent(this, params).then(res => {
          this.$message.success('移除成功');
          this.formClassStudent.Student.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormClassStudent();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormClassStudent();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
