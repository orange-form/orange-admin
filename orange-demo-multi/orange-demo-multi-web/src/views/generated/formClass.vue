<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="所属校区">
          <el-select class="filter-item" v-model="formClass.formFilter.schoolId" :clearable="true" filterable
            placeholder="所属校区" :loading="formClass.schoolId.impl.loading"
            @visible-change="formClass.schoolId.impl.onVisibleChange"
            @change="onSchoolIdValueChange">
            <el-option v-for="item in formClass.schoolId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级名称">
          <el-input class="filter-item" v-model="formClass.formFilter.className"
            :clearable="true" placeholder="班级名称"
          />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormClass(true)">查询</el-button>
      </filter-box>
    </el-form>
    <el-form ref="formClass" :model="formData" class="full-width-input" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="base-card" shadow="never" style="height: 620px">
            <div slot="header" class="base-card-header">
              <span>班级列表</span>
              <div class="base-card-operation">
                <el-button @click="onFormCreateClassClick()" type="text" size="mini"
                  :disabled="!checkPermCodeExist('formClass:formClass:formCreateClass')">
                  新建
                </el-button>
              </div>
            </div>
            <el-row class="no-scroll" :gutter="20">
              <el-col class="table-box" :span="24">
                <el-table ref="studentClass" :data="formClass.StudentClass.impl.dataList" size="mini" @sort-change="formClass.StudentClass.impl.onSortChange"
                  header-cell-class-name="table-header-gray"
                  highlight-current-row @current-change="formClass.StudentClass.impl.currentRowChange">
                  <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formClass.StudentClass.impl.getTableIndex" />
                  <el-table-column label="班级名称" prop="className">
                  </el-table-column>
                  <el-table-column label="所属校区" prop="schoolIdDictMap.name">
                  </el-table-column>
                  <el-table-column label="创建时间" prop="createTime" sortable="custom">
                    <template slot-scope="scope">
                      <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" fixed="right">
                    <template slot-scope="scope">
                      <el-button @click.stop="onFormEditClassClick(scope.row)" type="text" size="mini"
                        :disabled="!checkPermCodeExist('formClass:formClass:formEditClass')">
                        编辑
                      </el-button>
                      <el-button @click.stop="onDeleteClick(scope.row)" type="text" size="mini"
                        :disabled="!checkPermCodeExist('formClass:formClass:delete')">
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-row type="flex" justify="end" style="margin-top: 10px;">
                  <el-pagination
                    :total="formClass.StudentClass.impl.totalCount"
                    :current-page="formClass.StudentClass.impl.currentPage"
                    :page-size="formClass.StudentClass.impl.pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    layout="total, prev, pager, next, sizes"
                    @current-change="formClass.StudentClass.impl.onCurrentPageChange"
                    @size-change="formClass.StudentClass.impl.onPageSizeChange">
                  </el-pagination>
                </el-row>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-row class="no-scroll" :gutter="20">
            <el-col :span="24" class="gutter-bottom">
              <el-card class="base-card" shadow="never" style="height: 300px">
                <div slot="header" class="base-card-header">
                  <span>班级课程</span>
                  <div class="base-card-operation">
                    <el-button @click="onFormSetClassCourseClick()" type="text" size="mini"
                      :disabled="!checkPermCodeExist('formClass:formClass:formSetClassCourse') || !formSetClassCourseEnabled">
                      设置班级课程
                    </el-button>
                  </div>
                </div>
                <el-row class="no-scroll" :gutter="20">
                  <el-col class="table-box" :span="24">
                    <el-table ref="course" :data="formClass.Course.impl.dataList" size="mini" @sort-change="formClass.Course.impl.onSortChange"
                      header-cell-class-name="table-header-gray">
                      <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formClass.Course.impl.getTableIndex" />
                      <el-table-column label="课程名称" prop="courseName">
                      </el-table-column>
                      <el-table-column label="所属年级" prop="gradeIdDictMap.name">
                      </el-table-column>
                      <el-table-column label="课程顺序" prop="classCourse.courseOrder" sortable="custom">
                      </el-table-column>
                      <el-table-column label="操作" fixed="right">
                        <template slot-scope="scope">
                          <el-button @click.stop="onDeleteClassCourseClick(scope.row)" type="text" size="mini"
                            :disabled="!checkPermCodeExist('formClass:formClass:deleteClassCourse') || !deleteClassCourseEnabled">
                            移除
                          </el-button>
                          <el-button @click.stop="onFormEditClassCourseOrderClick(scope.row)" type="text" size="mini"
                            :disabled="!checkPermCodeExist('formClass:formClass:formEditClassCourseOrder') || !formEditClassCourseOrderEnabled">
                            课程顺序
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                    <el-row type="flex" justify="end" style="margin-top: 10px;">
                      <el-pagination
                        :total="formClass.Course.impl.totalCount"
                        :current-page="formClass.Course.impl.currentPage"
                        :page-size="formClass.Course.impl.pageSize"
                        :page-sizes="[10, 20, 50, 100]"
                        layout="total, prev, pager, next, sizes"
                        @current-change="formClass.Course.impl.onCurrentPageChange"
                        @size-change="formClass.Course.impl.onPageSizeChange">
                      </el-pagination>
                    </el-row>
                  </el-col>
                </el-row>
              </el-card>
            </el-col>
            <el-col :span="24">
              <el-card class="base-card" shadow="never" style="height: 300px">
                <div slot="header" class="base-card-header">
                  <span>班级学生</span>
                  <div class="base-card-operation">
                    <el-button @click="onFormSetClassStudentClick()" type="text" size="mini"
                      :disabled="!checkPermCodeExist('formClass:formClass:formSetClassStudent') || !formSetClassStudentEnabled">
                      设置班级学生
                    </el-button>
                  </div>
                </div>
                <el-row class="no-scroll" :gutter="20">
                  <el-col class="table-box" :span="24">
                    <el-table ref="student" :data="formClass.Student.impl.dataList" size="mini" @sort-change="formClass.Student.impl.onSortChange"
                      header-cell-class-name="table-header-gray">
                      <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formClass.Student.impl.getTableIndex" />
                      <el-table-column label="姓名" prop="studentName">
                      </el-table-column>
                      <el-table-column label="年级" prop="gradeIdDictMap.name">
                      </el-table-column>
                      <el-table-column label="所属校区" prop="schoolIdDictMap.name">
                      </el-table-column>
                      <el-table-column label="操作" fixed="right">
                        <template slot-scope="scope">
                          <el-button @click.stop="onDeleteClassStudentClick(scope.row)" type="text" size="mini"
                            :disabled="!checkPermCodeExist('formClass:formClass:deleteClassStudent') || !deleteClassStudentEnabled">
                            移除
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                    <el-row type="flex" justify="end" style="margin-top: 10px;">
                      <el-pagination
                        :total="formClass.Student.impl.totalCount"
                        :current-page="formClass.Student.impl.currentPage"
                        :page-size="formClass.Student.impl.pageSize"
                        :page-sizes="[10, 20, 50, 100]"
                        layout="total, prev, pager, next, sizes"
                        @current-change="formClass.Student.impl.onCurrentPageChange"
                        @size-change="formClass.Student.impl.onPageSizeChange">
                      </el-pagination>
                    </el-row>
                  </el-col>
                </el-row>
              </el-card>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { findTreeNode, findTreeNodePath, findItemFromList } from '@/utils';
/* eslint-disable-next-line */
import rules from '@/utils/validate.js';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { StudentClassController, DictionaryController } from '@/api';
import formCreateClass from '@/views/generated/formCreateClass.vue';
import formEditClass from '@/views/generated/formEditClass.vue';
import formEditClassCourseOrder from '@/views/generated/formEditClassCourseOrder.vue';

export default {
  name: 'formClass',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formData: {
        StudentClass: {
          classId: undefined,
          className: undefined,
          schoolId: undefined,
          leaderId: undefined,
          finishClassHour: undefined,
          classLevel: undefined,
          createUserId: undefined,
          createTime: undefined,
          status: undefined,
          course: {
            classCourse: {
              classId: undefined,
              courseId: undefined,
              courseOrder: undefined
            }
          },
          student: {
          },
          isDatasourceInit: false
        }
      },
      formClass: {
        formFilter: {
          schoolId: undefined,
          className: undefined
        },
        formFilterCopy: {
          schoolId: undefined,
          className: undefined
        },
        schoolId: {
          impl: new DropdownWidget(this.loadSchoolIdDropdownList)
        },
        classCourseCard: {
          isInit: false
        },
        StudentClass: {
          impl: new TableWidget(this.loadStudentClassWidgetData, this.loadStudentClassVerify, true, true, 'createTime', 1)
        },
        Course: {
          impl: new TableWidget(this.loadCourseWidgetData, this.loadCourseVerify, true, false)
        },
        Student: {
          impl: new TableWidget(this.loadStudentWidgetData, this.loadStudentVerify, true, false)
        },
        classStudentCard: {
          isInit: false
        },
        classCard: {
          isInit: false
        },
        classInfo: {
          isInit: false
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 班级数据数据获取函数，返回Promise
     */
    loadStudentClassWidgetData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        studentClassDtoFilter: {
          className: this.formClass.formFilterCopy.className,
          schoolId: this.formClass.formFilterCopy.schoolId
        }
      }
      return new Promise((resolve, reject) => {
        StudentClassController.list(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
          // 恢复当前选择行
          if (this.formClass.StudentClass.impl.currentRow !== null) {
            this.$nextTick(() => {
              let currentRow = findItemFromList(res.data.dataList, this.formClass.StudentClass.impl.currentRow.classId, 'classId');
              this.$refs.studentClass.setCurrentRow(currentRow);
            });
          }
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 班级数据数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadStudentClassVerify () {
      this.formClass.formFilterCopy.className = this.formClass.formFilter.className;
      this.formClass.formFilterCopy.schoolId = this.formClass.formFilter.schoolId;
      return true;
    },
    /**
     * 班级课程数据获取函数，返回Promise
     */
    loadCourseWidgetData (params) {
      if (
        (this.formClass.StudentClass.impl.currentRow || {}).classId == null
      ) {
        this.formClass.Course.impl.clearTable();
        return Promise.reject();
      }
      if (params == null) params = {};
      params = {
        ...params,
        classId: (this.formClass.StudentClass.impl.currentRow || {}).classId
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
     * 班级学生数据获取函数，返回Promise
     */
    loadStudentWidgetData (params) {
      if (
        (this.formClass.StudentClass.impl.currentRow || {}).classId == null
      ) {
        this.formClass.Student.impl.clearTable();
        return Promise.reject();
      }
      if (params == null) params = {};
      params = {
        ...params,
        classId: (this.formClass.StudentClass.impl.currentRow || {}).classId
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
     * 所属校区下拉数据获取函数
     */
    loadSchoolIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSysDeptByParentId(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所属校区选中值改变
     */
    onSchoolIdValueChange (value) {
    },
    /**
     * 更新班级课程
     */
    refreshClassCourseCardWidget (reloadData = false) {
      // 更新容器下所有的数据组件
      if (reloadData) {
        this.formClass.Course.impl.refreshTable(true, 1);
      } else {
        this.formClass.Course.impl.refreshTable();
      }
      this.formClass.classCourseCard.isInit = true;
    },
    /**
     * 更新班级学生
     */
    refreshClassStudentCardWidget (reloadData = false) {
      // 更新容器下所有的数据组件
      if (reloadData) {
        this.formClass.Student.impl.refreshTable(true, 1);
      } else {
        this.formClass.Student.impl.refreshTable();
      }
      this.formClass.classStudentCard.isInit = true;
    },
    /**
     * 更新班级列表
     */
    refreshClassCardWidget (reloadData = false) {
      // 更新容器下所有的数据组件
      if (reloadData) {
        this.formClass.StudentClass.impl.refreshTable(true, 1);
      } else {
        this.formClass.StudentClass.impl.refreshTable();
      }
      this.formClass.classCard.isInit = true;
    },
    /**
     * 更新班级管理
     */
    refreshFormClass (reloadData = false) {
      if (reloadData) {
        this.formClass.StudentClass.impl.refreshTable(true, 1);
      } else {
        this.formClass.StudentClass.impl.refreshTable();
      }
      if (reloadData) {
        this.formClass.Course.impl.refreshTable(true, 1);
      } else {
        this.formClass.Course.impl.refreshTable();
      }
      if (reloadData) {
        this.formClass.Student.impl.refreshTable(true, 1);
      } else {
        this.formClass.Student.impl.refreshTable();
      }
      if (!this.formClass.isInit) {
        // 初始化下拉数据
      }
      this.formClass.isInit = true;
    },
    /**
     * 新建
     */
    onFormCreateClassClick () {
      let params = {};

      this.$dialog.show('新建', formCreateClass, {
        area: '800px'
      }, params).then(res => {
        this.refreshClassCardWidget();
      }).catch(e => {});
    },
    /**
     * 设置班级课程
     */
    onFormSetClassCourseClick () {
      let params = {
        classId: (this.formClass.StudentClass.impl.currentRow || {}).classId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formSetClassCourse', query: params});
    },
    /**
     * 设置班级学生
     */
    onFormSetClassStudentClick () {
      let params = {
        classId: (this.formClass.StudentClass.impl.currentRow || {}).classId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formSetClassStudent', query: params});
    },
    /**
     * 移除
     */
    onDeleteClassCourseClick (row) {
      if (
        (this.formClass.StudentClass.impl.currentRow || {}).classId == null ||
        row.courseId == null
      ) {
        this.$message.error('请求失败，发现必填参数为空！');
        return;
      }
      let params = {
        classId: (this.formClass.StudentClass.impl.currentRow || {}).classId,
        courseId: row.courseId
      };

      this.$confirm('是否从班级中移除此课程？').then(res => {
        StudentClassController.deleteClassCourse(this, params).then(res => {
          this.$message.success('移除成功');
          this.formClass.Course.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 课程顺序
     */
    onFormEditClassCourseOrderClick (row) {
      let params = {
        classId: (this.formClass.StudentClass.impl.currentRow || {}).classId,
        courseId: row.courseId
      };

      this.$dialog.show('课程顺序', formEditClassCourseOrder, {
        area: '400px'
      }, params).then(res => {
        this.formClass.Course.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 移除
     */
    onDeleteClassStudentClick (row) {
      if (
        (this.formClass.StudentClass.impl.currentRow || {}).classId == null ||
        row.studentId == null
      ) {
        this.$message.error('请求失败，发现必填参数为空！');
        return;
      }
      let params = {
        classId: (this.formClass.StudentClass.impl.currentRow || {}).classId,
        studentId: row.studentId
      };

      this.$confirm('是否从班级中移除此学生？').then(res => {
        StudentClassController.deleteClassStudent(this, params).then(res => {
          this.$message.success('移除成功');
          this.formClass.Student.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onFormEditClassClick (row) {
      let params = {
        classId: row.classId
      };

      this.$dialog.show('编辑', formEditClass, {
        area: '800px'
      }, params).then(res => {
        this.formClass.StudentClass.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      if (
        row.classId == null
      ) {
        this.$message.error('请求失败，发现必填参数为空！');
        return;
      }
      let params = {
        classId: row.classId
      };

      this.$confirm('是否删除此班级？').then(res => {
        StudentClassController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formClass.StudentClass.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormClass(true);
    },
    initFormData () {
    },
    /**
     * 重置表单数据
     */
    resetFormData () {
      this.formData = {
        StudentClass: {
          classId: undefined,
          className: undefined,
          schoolId: undefined,
          leaderId: undefined,
          finishClassHour: undefined,
          classLevel: undefined,
          createUserId: undefined,
          createTime: undefined,
          status: undefined,
          course: {
            classCourse: {
              classId: undefined,
              courseId: undefined,
              courseOrder: undefined
            }
          },
          student: {
          },
          isDatasourceInit: false
        }
      }
    },
    formInit () {
      this.refreshFormClass();
    }
  },
  computed: {
    formSetClassCourseEnabled () {
      return this.formClass.StudentClass.impl.currentRow != null;
    },
    formSetClassStudentEnabled () {
      return this.formClass.StudentClass.impl.currentRow != null;
    },
    deleteClassCourseEnabled () {
      return this.formClass.StudentClass.impl.currentRow != null;
    },
    formEditClassCourseOrderEnabled () {
      return this.formClass.StudentClass.impl.currentRow != null;
    },
    deleteClassStudentEnabled () {
      return this.formClass.StudentClass.impl.currentRow != null;
    }
  },
  mounted () {
    // 初始化页面数据
    this.formInit();
  },
  watch: {
    // 班级数据选择行数据变化
    'formClass.StudentClass.impl.currentRow': {
      handler (newValue) {
        this.formClass.Course.impl.refreshTable(true, 1);
        this.formClass.Student.impl.refreshTable(true, 1);
      },
      immediate: true
    }
  }
}
</script>
