<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="教师职级">
          <el-select class="filter-item" v-model="formTeacher.formFilter.level" :clearable="true" filterable
            placeholder="教师职级" :loading="formTeacher.level.impl.loading"
            @visible-change="formTeacher.level.impl.onVisibleChange"
            @change="onLevelValueChange">
            <el-option v-for="item in formTeacher.level.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="所教科目">
          <el-select class="filter-item" v-model="formTeacher.formFilter.subjectId" :clearable="true" filterable
            placeholder="所教科目" :loading="formTeacher.subjectId.impl.loading"
            @visible-change="formTeacher.subjectId.impl.onVisibleChange"
            @change="onSubjectIdValueChange">
            <el-option v-for="item in formTeacher.subjectId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册日期">
          <date-range class="filter-item" v-model="formTeacher.formFilter.registerDate" :clearable="true" :allowTypes="['day']" align="left"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd hh:mm:ss" />
        </el-form-item>
        <el-form-item label="校区名称">
          <el-input class="filter-item" v-model="formTeacher.formFilter.schoolName"
            :clearable="true" placeholder="校区名称" />
        </el-form-item>
        <el-form-item label="老师名称">
          <el-input class="filter-item" v-model="formTeacher.formFilter.teacherName"
            :clearable="true" placeholder="老师名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormTeacher(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formTeacher:formTeacher:formCreateTeacher')"
          @click="onFormCreateTeacherClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formTeacher.Teacher.impl.dataList" size="mini" @sort-change="formTeacher.Teacher.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formTeacher.Teacher.impl.getTableIndex" />
          <el-table-column label="教师名称" prop="teacherName">
          </el-table-column>
          <el-table-column label="教师职级" prop="levelDictMap.name" sortable="custom">
          </el-table-column>
          <el-table-column label="所教科目" prop="subjectIdDictMap.name">
          </el-table-column>
          <el-table-column label="出生日期" prop="birthday">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.birthday, 'day')}}</span>
            </template>
          </el-table-column>
          <el-table-column label="所属校区" prop="schoolIdDictMap.name">
          </el-table-column>
          <el-table-column label="鲜花数量" prop="flowerCount" sortable="custom">
          </el-table-column>
          <el-table-column label="入职时间" prop="registerDate">
            <template slot-scope="scope">
              <span>{{formatDateByStatsType(scope.row.registerDate, 'day')}}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click="onFormEditTeacherClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formTeacher:formTeacher:formEditTeacher')">
                编辑
              </el-button>
              <el-button @click="onFormTeacherTransStatsClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formTeacher:formTeacher:formTeacherTransStats')">
                统计
              </el-button>
              <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formTeacher:formTeacher:delete')">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formTeacher.Teacher.impl.totalCount"
            :current-page="formTeacher.Teacher.impl.currentPage"
            :page-size="formTeacher.Teacher.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formTeacher.Teacher.impl.onCurrentPageChange"
            @size-change="formTeacher.Teacher.impl.onPageSizeChange">
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
import { TeacherController, DictionaryController } from '@/api';
import formCreateTeacher from '@/views/generated/formCreateTeacher';
import formTeacherTransStats from '@/views/generated/formTeacherTransStats';
import formEditTeacher from '@/views/generated/formEditTeacher';

export default {
  name: 'formTeacher',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formTeacher: {
        formFilter: {
          level: undefined,
          subjectId: undefined,
          registerDate: [],
          schoolName: undefined,
          teacherName: undefined
        },
        formFilterCopy: {
          level: undefined,
          subjectId: undefined,
          registerDate: [],
          schoolName: undefined,
          teacherName: undefined
        },
        level: {
          impl: new DropdownWidget(this.loadLevelDropdownList)
        },
        subjectId: {
          impl: new DropdownWidget(this.loadSubjectIdDropdownList)
        },
        Teacher: {
          impl: new TableWidget(this.loadTeacherData, this.loadTeacherVerify, true, 'registerDate', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 老师列表数据获取函数，返回Primise
     */
    loadTeacherData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        sysDeptFilter: {
          deptName: this.formTeacher.formFilterCopy.schoolName
        },
        teacherFilter: {
          teacherName: this.formTeacher.formFilterCopy.teacherName,
          subjectId: this.formTeacher.formFilterCopy.subjectId,
          level: this.formTeacher.formFilterCopy.level,
          registerDateStart: Array.isArray(this.formTeacher.formFilterCopy.registerDate) ? this.formTeacher.formFilterCopy.registerDate[0] : undefined,
          registerDateEnd: Array.isArray(this.formTeacher.formFilterCopy.registerDate) ? this.formTeacher.formFilterCopy.registerDate[1] : undefined
        }
      }
      return new Promise((resolve, reject) => {
        TeacherController.list(this, params).then(res => {
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
     * 老师列表数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadTeacherVerify () {
      this.formTeacher.formFilterCopy.schoolName = this.formTeacher.formFilter.schoolName;
      this.formTeacher.formFilterCopy.teacherName = this.formTeacher.formFilter.teacherName;
      this.formTeacher.formFilterCopy.subjectId = this.formTeacher.formFilter.subjectId;
      this.formTeacher.formFilterCopy.level = this.formTeacher.formFilter.level;
      this.formTeacher.formFilterCopy.registerDate = this.formTeacher.formFilter.registerDate;
      return true;
    },
    /**
     * 教师职级下拉数据获取函数
     */
    loadLevelDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictTeacherLevelType(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 教师职级选中值改变
     */
    onLevelValueChange (value) {
    },
    /**
     * 所教科目下拉数据获取函数
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
     * 所教科目选中值改变
     */
    onSubjectIdValueChange (value) {
    },
    /**
     * 更新老师管理
     */
    refreshFormTeacher (reloadData = false) {
      if (reloadData) {
        this.formTeacher.Teacher.impl.refreshTable(true, 1);
      } else {
        this.formTeacher.Teacher.impl.refreshTable();
      }
      if (!this.formTeacher.isInit) {
        // 初始化下拉数据
      }
      this.formTeacher.isInit = true;
    },
    /**
     * 新建
     */
    onFormCreateTeacherClick () {
      let params = {};

      this.$dialog.show('新建', formCreateTeacher, {
        area: ['800px']
      }, params).then(res => {
        this.refreshFormTeacher();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onFormEditTeacherClick (row) {
      let params = {
        teacherId: row.teacherId
      };

      this.$dialog.show('编辑', formEditTeacher, {
        area: ['800px']
      }, params).then(res => {
        this.formTeacher.Teacher.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 统计
     */
    onFormTeacherTransStatsClick (row) {
      let params = {
        teacherId: row.teacherId
      };

      this.$dialog.show('统计', formTeacherTransStats, {
        area: ['800px']
      }, params).then(res => {
        this.formTeacher.Teacher.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        teacherId: row.teacherId
      };

      this.$confirm('是否删除此老师？').then(res => {
        TeacherController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formTeacher.Teacher.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormTeacher();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormTeacher();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
