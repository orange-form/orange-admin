<template>
  <div style="position: relative;">
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
        <el-form-item label="班级级别">
          <el-select class="filter-item" v-model="formClass.formFilter.classLevel" :clearable="true" filterable
            placeholder="班级级别" :loading="formClass.classLevel.impl.loading"
            @visible-change="formClass.classLevel.impl.onVisibleChange"
            @change="onClassLevelValueChange">
            <el-option v-for="item in formClass.classLevel.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级名称">
          <el-input class="filter-item" v-model="formClass.formFilter.className"
            :clearable="true" placeholder="班级名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormClass(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formClass:formClass:formCreateClass')"
          @click="onFormCreateClassClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formClass.Class.impl.dataList" size="mini" @sort-change="formClass.Class.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formClass.Class.impl.getTableIndex" />
          <el-table-column label="班级名称" prop="className">
          </el-table-column>
          <el-table-column label="所属校区" prop="schoolIdDictMap.name">
          </el-table-column>
          <el-table-column label="班级级别" prop="classLevelDictMap.name">
          </el-table-column>
          <el-table-column label="已完成课时" prop="finishClassHour">
          </el-table-column>
          <el-table-column label="班级创建时间" prop="createTime">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" min-width="150px">
            <template slot-scope="scope">
              <el-button @click="onFormEditClassClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formClass:formClass:formEditClass')">
                编辑
              </el-button>
              <el-button @click="onFormClassStudentClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formClass:formClass:formClassStudent')">
                学生
              </el-button>
              <el-button @click="onFormClassCourseClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formClass:formClass:formClassCourse')">
                课程
              </el-button>
              <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formClass:formClass:delete')">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formClass.Class.impl.totalCount"
            :current-page="formClass.Class.impl.currentPage"
            :page-size="formClass.Class.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formClass.Class.impl.onCurrentPageChange"
            @size-change="formClass.Class.impl.onPageSizeChange">
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
import { StudentClassController, DictionaryController } from '@/api';
import formEditClass from '@/views/generated/formEditClass';
import formCreateClass from '@/views/generated/formCreateClass';

export default {
  name: 'formClass',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formClass: {
        formFilter: {
          schoolId: undefined,
          classLevel: undefined,
          className: undefined
        },
        formFilterCopy: {
          schoolId: undefined,
          classLevel: undefined,
          className: undefined
        },
        schoolId: {
          impl: new DropdownWidget(this.loadSchoolIdDropdownList)
        },
        classLevel: {
          impl: new DropdownWidget(this.loadClassLevelDropdownList)
        },
        Class: {
          impl: new TableWidget(this.loadClassData, this.loadClassVerify, true, 'createTime', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 班级数据数据获取函数，返回Primise
     */
    loadClassData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        studentClassFilter: {
          className: this.formClass.formFilterCopy.className,
          schoolId: this.formClass.formFilterCopy.schoolId,
          classLevel: this.formClass.formFilterCopy.classLevel
        }
      }
      return new Promise((resolve, reject) => {
        StudentClassController.list(this, params).then(res => {
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
     * 班级数据数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadClassVerify () {
      this.formClass.formFilterCopy.className = this.formClass.formFilter.className;
      this.formClass.formFilterCopy.schoolId = this.formClass.formFilter.schoolId;
      this.formClass.formFilterCopy.classLevel = this.formClass.formFilter.classLevel;
      return true;
    },
    /**
     * 所属校区下拉数据获取函数
     */
    loadSchoolIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSchoolInfo(this, params).then(res => {
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
     * 班级级别下拉数据获取函数
     */
    loadClassLevelDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictClassLevel(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 班级级别选中值改变
     */
    onClassLevelValueChange (value) {
    },
    /**
     * 更新班级管理
     */
    refreshFormClass (reloadData = false) {
      if (reloadData) {
        this.formClass.Class.impl.refreshTable(true, 1);
      } else {
        this.formClass.Class.impl.refreshTable();
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
        area: ['800px']
      }, params).then(res => {
        this.refreshFormClass();
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
        area: ['800px']
      }, params).then(res => {
        this.formClass.Class.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 学生
     */
    onFormClassStudentClick (row) {
      let params = {
        classId: row.classId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formClassStudent', query: params});
    },
    /**
     * 课程
     */
    onFormClassCourseClick (row) {
      let params = {
        classId: row.classId
      };

      params.closeVisible = 1;
      this.$router.push({name: 'formClassCourse', query: params});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        classId: row.classId
      };

      this.$confirm('是否删除此班级？').then(res => {
        StudentClassController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formClass.Class.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormClass();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormClass();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
