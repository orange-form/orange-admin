<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="所属省份">
          <el-select class="filter-item" v-model="formStudent.formFilter.provinceId" :clearable="true" filterable
            placeholder="所属省份" :loading="formStudent.provinceId.impl.loading"
            @visible-change="formStudent.provinceId.impl.onVisibleChange"
            @change="onProvinceIdValueChange">
            <el-option v-for="item in formStudent.provinceId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属城市">
          <el-select class="filter-item" v-model="formStudent.formFilter.cityId" :clearable="true" filterable
            placeholder="所属城市" :loading="formStudent.cityId.impl.loading"
            @visible-change="formStudent.cityId.impl.onVisibleChange"
            @change="onCityIdValueChange">
            <el-option v-for="item in formStudent.cityId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属校区">
          <el-select class="filter-item" v-model="formStudent.formFilter.schoolId" :clearable="true" filterable
            placeholder="所属校区" :loading="formStudent.schoolId.impl.loading"
            @visible-change="formStudent.schoolId.impl.onVisibleChange"
            @change="onSchoolIdValueChange">
            <el-option v-for="item in formStudent.schoolId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册日期">
          <date-range class="filter-item" v-model="formStudent.formFilter.registerDate" :clearable="true" :allowTypes="['day']" align="left"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd hh:mm:ss" />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input class="filter-item" v-model="formStudent.formFilter.searchString"
            :clearable="true" placeholder="输入学生姓名 / 手机号码 模糊查询" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormStudent(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formStudent:formStudent:formCreateStudent')"
          @click="onFormCreateStudentClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formStudent.Student.impl.dataList" size="mini" @sort-change="formStudent.Student.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formStudent.Student.impl.getTableIndex" />
          <el-table-column label="姓名" prop="studentName">
          </el-table-column>
          <el-table-column label="手机号码" prop="loginMobile">
          </el-table-column>
          <el-table-column label="所属校区" prop="schoolIdDictMap.name">
          </el-table-column>
          <el-table-column label="经验等级" prop="experienceLevelDictMap.name" sortable="custom">
          </el-table-column>
          <table-progress-column label="学币状态" :min="0" max-column="totalCoin" value-column="leftCoin" />
          <el-table-column label="状态 " prop="statusDictMap.name">
          </el-table-column>
          <el-table-column label="注册时间" prop="registerTime">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.registerTime, 'day')}}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click="onFormEditStudentClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formStudent:formStudent:formEditStudent')">
                编辑
              </el-button>
              <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formStudent:formStudent:delete')">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formStudent.Student.impl.totalCount"
            :current-page="formStudent.Student.impl.currentPage"
            :page-size="formStudent.Student.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formStudent.Student.impl.onCurrentPageChange"
            @size-change="formStudent.Student.impl.onPageSizeChange">
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
import { StudentController, DictionaryController } from '@/api';
import formEditStudent from '@/views/generated/formEditStudent';
import formCreateStudent from '@/views/generated/formCreateStudent';

export default {
  name: 'formStudent',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formStudent: {
        formFilter: {
          provinceId: undefined,
          cityId: undefined,
          schoolId: undefined,
          registerDate: [],
          searchString: undefined
        },
        formFilterCopy: {
          provinceId: undefined,
          cityId: undefined,
          schoolId: undefined,
          registerDate: [],
          searchString: undefined
        },
        provinceId: {
          impl: new DropdownWidget(this.loadProvinceIdDropdownList)
        },
        cityId: {
          impl: new DropdownWidget(this.loadCityIdDropdownList)
        },
        schoolId: {
          impl: new DropdownWidget(this.loadSchoolIdDropdownList)
        },
        Student: {
          impl: new TableWidget(this.loadStudentData, this.loadStudentVerify, true, 'registerTime', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 学生数据数据获取函数，返回Primise
     */
    loadStudentData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        studentFilter: {
          provinceId: this.formStudent.formFilterCopy.provinceId,
          cityId: this.formStudent.formFilterCopy.cityId,
          schoolId: this.formStudent.formFilterCopy.schoolId,
          registerTimeStart: Array.isArray(this.formStudent.formFilterCopy.registerDate) ? this.formStudent.formFilterCopy.registerDate[0] : undefined,
          registerTimeEnd: Array.isArray(this.formStudent.formFilterCopy.registerDate) ? this.formStudent.formFilterCopy.registerDate[1] : undefined,
          searchString: this.formStudent.formFilterCopy.searchString
        }
      }
      return new Promise((resolve, reject) => {
        StudentController.list(this, params).then(res => {
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
     * 学生数据数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadStudentVerify () {
      this.formStudent.formFilterCopy.provinceId = this.formStudent.formFilter.provinceId;
      this.formStudent.formFilterCopy.cityId = this.formStudent.formFilter.cityId;
      this.formStudent.formFilterCopy.schoolId = this.formStudent.formFilter.schoolId;
      this.formStudent.formFilterCopy.registerDate = this.formStudent.formFilter.registerDate;
      this.formStudent.formFilterCopy.searchString = this.formStudent.formFilter.searchString;
      return true;
    },
    /**
     * 所属省份下拉数据获取函数
     */
    loadProvinceIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictAreaCodeByParentId(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所属省份选中值改变
     */
    onProvinceIdValueChange (value) {
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formStudent.formFilter.cityId = undefined;
      this.formStudent.cityId.impl.dirty = true;
      this.onCityIdValueChange(this.formStudent.formFilter.cityId);
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formStudent.formFilter.schoolId = undefined;
      this.formStudent.schoolId.impl.dirty = true;
      this.onSchoolIdValueChange(this.formStudent.formFilter.schoolId);
    },
    /**
     * 所属城市下拉数据获取函数
     */
    loadCityIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {
          parentId: this.formStudent.formFilter.provinceId
        };
        if (params.parentId == null || params.parentId === '') {
          resolve([]);
          return;
        }
        DictionaryController.dictAreaCodeByParentId(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所属城市选中值改变
     */
    onCityIdValueChange (value) {
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formStudent.formFilter.schoolId = undefined;
      this.formStudent.schoolId.impl.dirty = true;
      this.onSchoolIdValueChange(this.formStudent.formFilter.schoolId);
    },
    /**
     * 所属校区下拉数据获取函数
     */
    loadSchoolIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {
          provinceId: this.formStudent.formFilter.provinceId,
          cityId: this.formStudent.formFilter.cityId
        };
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
     * 更新学生管理
     */
    refreshFormStudent (reloadData = false) {
      if (reloadData) {
        this.formStudent.Student.impl.refreshTable(true, 1);
      } else {
        this.formStudent.Student.impl.refreshTable();
      }
      if (!this.formStudent.isInit) {
        // 初始化下拉数据
      }
      this.formStudent.isInit = true;
    },
    /**
     * 新建
     */
    onFormCreateStudentClick () {
      let params = {};

      this.$dialog.show('新建', formCreateStudent, {
        area: ['800px']
      }, params).then(res => {
        this.refreshFormStudent();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onFormEditStudentClick (row) {
      let params = {
        studentId: row.studentId
      };

      this.$dialog.show('编辑', formEditStudent, {
        area: ['800px']
      }, params).then(res => {
        this.formStudent.Student.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        studentId: row.studentId
      };

      this.$confirm('是否删除此学生？').then(res => {
        StudentController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formStudent.Student.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormStudent();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormStudent();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
