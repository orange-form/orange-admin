<template>
  <el-container style="position: relative;">
    <el-aside width="300px">
      <el-card class="base-card" shadow="never" :body-style="{ padding: '0px' }">
        <div slot="header" class="base-card-header">
          <span>所属校区</span>
        </div>
        <el-scrollbar :style="{height: (getMainContextHeight - 94) + 'px'}" class="custom-scroll">
          <el-tree ref="schoolId" :props="{label: 'name'}"
            :data="formStudent.schoolId.impl.dropdownList"
            node-key="id" @node-click="onSchoolIdValueChange"
            :highlight-current="true" :default-expand-all="true" />
        </el-scrollbar>
      </el-card>
    </el-aside>
    <el-main style="padding-left: 15px;">
      <el-scrollbar :style="{height: (getMainContextHeight - 42) + 'px'}" class="custom-scroll">
        <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="所属年级">
              <el-select class="filter-item" v-model="formStudent.formFilter.gradeId" :clearable="true" filterable
                placeholder="所属年级" :loading="formStudent.gradeId.impl.loading"
                @visible-change="formStudent.gradeId.impl.onVisibleChange"
                @change="onGradeIdValueChange">
                <el-option v-for="item in formStudent.gradeId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
              </el-select>
            </el-form-item>
            <el-form-item label="注册日期">
              <date-range class="filter-item" v-model="formStudent.formFilter.registerDate" :clearable="true" :allowTypes="['day']" align="left"
                range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
                format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss" />
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
            <el-table ref="student" :data="formStudent.Student.impl.dataList" size="mini" @sort-change="formStudent.Student.impl.onSortChange"
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
                  <el-button @click.stop="onFormEditStudentClick(scope.row)" type="text" size="mini"
                    :disabled="!checkPermCodeExist('formStudent:formStudent:formEditStudent')">
                    编辑
                  </el-button>
                  <el-button @click.stop="onDeleteClick(scope.row)" type="text" size="mini"
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
      </el-scrollbar>
    </el-main>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex';
/* eslint-disable-next-line */
import rules from '@/utils/validate.js';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { StudentController, DictionaryController } from '@/api';
import formCreateStudent from '@/views/generated/formCreateStudent.vue';
import formEditStudent from '@/views/generated/formEditStudent.vue';

export default {
  name: 'formStudent',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formStudent: {
        formFilter: {
          schoolId: undefined,
          gradeId: undefined,
          registerDate: [],
          searchString: undefined
        },
        formFilterCopy: {
          schoolId: undefined,
          gradeId: undefined,
          registerDate: [],
          searchString: undefined
        },
        schoolId: {
          impl: new DropdownWidget(this.loadSchoolIdDropdownList, true, 'id', 'parentId')
        },
        gradeId: {
          impl: new DropdownWidget(this.loadGradeIdDropdownList)
        },
        Student: {
          impl: new TableWidget(this.loadStudentWidgetData, this.loadStudentVerify, true, false, 'registerTime', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 学生数据数据获取函数，返回Promise
     */
    loadStudentWidgetData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        studentDtoFilter: {
          gradeId: this.formStudent.formFilterCopy.gradeId,
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
      this.formStudent.formFilterCopy.gradeId = this.formStudent.formFilter.gradeId;
      this.formStudent.formFilterCopy.schoolId = this.formStudent.formFilter.schoolId;
      this.formStudent.formFilterCopy.registerDate = this.formStudent.formFilter.registerDate;
      this.formStudent.formFilterCopy.searchString = this.formStudent.formFilter.searchString;
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
      if (value.id !== this.formStudent.formFilter.schoolId) {
        this.formStudent.formFilter.schoolId = value.id;
      } else {
        this.formStudent.formFilter.schoolId = undefined;
        this.$nextTick(() => {
          this.$refs.schoolId.setCurrentKey(null);
        });
      }
      this.refreshFormStudent(true);
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
        this.formStudent.schoolId.impl.onVisibleChange(true);
      }
      this.formStudent.isInit = true;
    },
    /**
     * 新建
     */
    onFormCreateStudentClick () {
      let params = {};

      this.$dialog.show('新建', formCreateStudent, {
        area: '800px'
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
        area: '800px'
      }, params).then(res => {
        this.formStudent.Student.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      if (
        row.studentId == null
      ) {
        this.$message.error('请求失败，发现必填参数为空！');
        return;
      }
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
  computed: {
    ...mapGetters(['getMainContextHeight'])
  },
  mounted () {
    // 初始化页面数据
    this.formInit();
  },
  watch: {
  }
}
</script>

<style scoped>
  >>> .el-tree-node__content {
    height: 35px;
  }
</style>
