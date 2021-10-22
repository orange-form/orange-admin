<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row type="flex" justify="space-between">
        <el-form-item label="登录名称">
          <el-input class="filter-item" v-model="formSysUser.formFilter.sysUserLoginName"
            @change="refreshFormSysUser(true)"
            :clearable="true" placeholder="登录名称" />
        </el-form-item>
        <div>
          <el-input v-if="!multiple" size="mini" v-model="assignee" placeholder="自定义用户" style="width: 200px; margin: 0px 10px;">
          </el-input>
          <el-button type="primary" size="mini" @click="setStartUser">
            流程发起人
          </el-button>
          <el-button v-if="showAssignee" type="primary" size="mini" @click="useAppointedAssignee">
            使用指定审批人
          </el-button>
          <el-button type="primary" size="mini" @click="onSubmit" :disabled="!canCommit">
            添加用户
          </el-button>
        </div>
      </el-row>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-radio-group class="radio-table" v-model="selectUserId" style="width: 100%;">
          <el-table :data="formSysUser.sysUserWidget.dataList" size="mini" height="410px"
            header-cell-class-name="table-header-gray"
            row-key="userId"
            @sort-change="formSysUser.sysUserWidget.onSortChange"
            @selection-change="handleSelectionChange"
          >
            <el-table-column v-if="this.multiple" header-align="center"
              :reserve-selection="multiple"
              align="center" type="selection" width="50px"
              :selectable="canSelect"
            />
            <el-table-column v-else label="" header-align="center" align="center" width="50px">
              <template slot-scope="scope">
                <el-radio :label="scope.row.userId"> </el-radio>
              </template>
            </el-table-column>
            <el-table-column label="用户名" prop="loginName" />
            <el-table-column label="昵称" prop="showName" />
            <el-table-column label="所属部门" prop="deptIdDictMap.name" />
            <el-table-column label="账号类型" prop="userTypeDictMap.name" />
            <el-table-column label="创建时间">
              <template slot-scope="scope">
                <span>{{formatDateByStatsType(scope.row.createTime, 'day')}}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-col :span="24">
            <el-row type="flex" justify="end" style="margin-top: 10px;">
              <el-pagination
                :total="formSysUser.sysUserWidget.totalCount"
                :current-page="formSysUser.sysUserWidget.currentPage"
                :page-size="formSysUser.sysUserWidget.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, prev, pager, next, sizes"
                @current-change="formSysUser.sysUserWidget.onCurrentPageChange"
                @size-change="formSysUser.sysUserWidget.onPageSizeChange">
              </el-pagination>
            </el-row>
          </el-col>
        </el-radio-group>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { findItemFromList } from '@/utils';
import { statsDateRangeMixin } from '@/core/mixins';
import { TableWidget } from '@/utils/widget.js';
import { SystemController } from '@/api';

export default {
  name: 'TaskUserSelect',
  props: {
    // 是否显示指定审批人
    showAssignee: {
      type: Boolean,
      default: true
    },
    // 是否多选
    multiple: {
      type: Boolean,
      default: false
    },
    // 已经被使用的用户列表
    usedUserIdList: {
      type: Array
    }
  },
  mixins: [statsDateRangeMixin],
  data () {
    return {
      assignee: undefined,
      // 单选下选中的用户
      selectUserId: undefined,
      // 多选下选中的用户列表
      multiSelectUser: [],
      formSysUser: {
        formFilter: {
          sysUserLoginName: undefined
        },
        formFilterCopy: {
          sysUserLoginName: undefined
        },
        sysUserWidget: new TableWidget(this.loadSysUserData, this.loadSysUserVerify, true, false, 'createTime', 1),
        isInit: false
      }
    }
  },
  methods: {
    onCancel (isSuccess, data) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess, data);
      }
    },
    onClear () {
      this.onCancel(true);
    },
    setStartUser () {
      this.onCancel(true, {
        /* eslint-disable-next-line */
        loginName: '${startUserName}'
      });
    },
    useAppointedAssignee () {
      this.onCancel(true, {
        /* eslint-disable-next-line */
        loginName: '${appointedAssignee}'
      });
    },
    canSelect (row) {
      if (Array.isArray(this.usedUserIdList) && this.usedUserIdList.length > 0) {
        return this.usedUserIdList.indexOf(row.loginName) === -1;
      } else {
        return true;
      }
    },
    onSubmit () {
      let selectUser = this.multiSelectUser;
      if (!this.multiple) {
        if (this.assignee != null && this.assignee !== '') {
          this.onCancel(true, {
            loginName: this.assignee
          });
          return;
        }
        selectUser = findItemFromList(this.formSysUser.sysUserWidget.dataList, this.selectUserId, 'userId');
      }
      this.onCancel(true, selectUser);
    },
    handleSelectionChange (values) {
      this.multiSelectUser = values;
    },
    /**
     * 用户管理数据获取函数，返回Primise
     */
    loadSysUserData (params) {
      params.sysUserDtoFilter = {
        loginName: this.formSysUser.formFilterCopy.sysUserLoginName
      }
      return new Promise((resolve, reject) => {
        SystemController.getUserList(this, params).then(res => {
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
     * 用户管理数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysUserVerify () {
      this.formSysUser.formFilterCopy.sysUserLoginName = this.formSysUser.formFilter.sysUserLoginName;
      return true;
    },
    /**
     * 更新用户管理
     */
    refreshFormSysUser (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.formSysUser.sysUserWidget.refreshTable(true, 1);
      } else {
        this.formSysUser.sysUserWidget.refreshTable();
      }
      this.formSysUser.isInit = true;
    }
  },
  computed: {
    canCommit () {
      return this.multiple ? this.multiSelectUser.length > 0 : ((this.selectUserId != null && this.selectUserId !== '') || this.assignee != null);
    }
  },
  mounted () {
    this.refreshFormSysUser();
  }
}
</script>

<style scoped>
  .radio-table >>> .el-radio__label {
    display: none;
  }
</style>
