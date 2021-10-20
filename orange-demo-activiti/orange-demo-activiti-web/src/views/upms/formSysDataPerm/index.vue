<template>
  <div class="tab-dialog-box" style="position: relative;">
    <el-tabs v-model="activeFragmentId" :before-leave="onFragmentChange">
      <el-tab-pane label="数据权限管理" name="fragmentSysDataPerm" style="width: 100%;"
        v-if="checkPermCodeExist('formSysDataPerm:fragmentSysDataPerm')">
        <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="过滤规则">
              <el-select class="filter-item" v-model="fragmentSysDataPerm.formFilter.sysDatapermType" :clearable="true"
                placeholder="过滤规则" :loading="fragmentSysDataPerm.sysDatapermType.impl.loading"
                @visible-change="onSysDatapermTypeVisibleChange"
                @change="onSysDatapermTypeValueChange">
                <el-option v-for="item in fragmentSysDataPerm.sysDatapermType.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
              </el-select>
            </el-form-item>
            <el-form-item label="数据权限名称">
              <el-input class="filter-item" v-model="fragmentSysDataPerm.formFilter.sysDataPermName"
                :clearable="true" placeholder="数据权限名称" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFragmentSysDataPerm(true)">查询</el-button>
            <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formSysDataPerm:fragmentSysDataPerm:add')"
              @click="onAddDataPermClick()">
              新建
            </el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysDataPerm.SysDataPerm.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysDataPerm.SysDataPerm.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
                :index="fragmentSysDataPerm.SysDataPerm.impl.getTableIndex" />
              <el-table-column label="权限名称" prop="dataPermName">
              </el-table-column>
              <el-table-column label="过滤规则">
                <template slot-scope="scope">
                  <span>{{SysDataPermType.getValue(scope.row.ruleType)}}</span>
                </template>
              </el-table-column>
              <el-table-column label="创建人" prop="createUsername">
              </el-table-column>
              <el-table-column label="操作" fixed="right">
                <template slot-scope="scope">
                <el-button @click="onEditDataPermClick(scope.row)" type="text" size="mini"
                  :disabled="!checkPermCodeExist('formSysDataPerm:fragmentSysDataPerm:update')">
                  编辑
                </el-button>
                <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
                  :disabled="!checkPermCodeExist('formSysDataPerm:fragmentSysDataPerm:delete')">
                  删除
                </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-col :span="24">
              <el-row type="flex" justify="end" style="margin-top: 10px;">
                <el-pagination
                  :total="fragmentSysDataPerm.SysDataPerm.impl.totalCount"
                  :current-page="fragmentSysDataPerm.SysDataPerm.impl.currentPage"
                  :page-size="fragmentSysDataPerm.SysDataPerm.impl.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  layout="total, prev, pager, next, sizes"
                  @current-change="fragmentSysDataPerm.SysDataPerm.impl.onCurrentPageChange"
                  @size-change="fragmentSysDataPerm.SysDataPerm.impl.onPageSizeChange">
                </el-pagination>
              </el-row>
            </el-col>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="用户授权" name="fragmentSysDataPermUser" style="width: 100%;"
        v-if="checkPermCodeExist('formSysDataPerm:fragmentSysDataPermUser')">
        <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
          <filter-box :item-width="350">
            <el-form-item label="数据权限">
              <el-select class="filter-item" v-model="fragmentSysDataPermUser.formFilter.dataPermId" clearable
                placeholder="数据权限" :loading="fragmentSysDataPermUser.dataPermId.impl.loading"
                @visible-change="fragmentSysDataPermUser.dataPermId.impl.onVisibleChange"
                @change="onDataPermChange">
                <el-option v-for="item in fragmentSysDataPermUser.dataPermId.impl.dropdownList" :key="item.dataPermId"
                  :value="item.dataPermId" :label="item.dataPermName" />
              </el-select>
            </el-form-item>
            <el-form-item label="用户名">
              <el-input class="filter-item" v-model="fragmentSysDataPermUser.formFilter.searchString"
                :clearable="true" placeholder="输入用户名 / 昵称查询" @change="refreshFragmentSysDataPermUser(true)" />
            </el-form-item>
            <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFragmentSysDataPermUser(true)">
              查询
            </el-button>
            <el-button slot="operator" type="primary" size="mini" @click="onAddDataPermUserClick()"
              :disabled="!checkPermCodeExist('formSysDataPerm:fragmentSysDataPermUser:addDataPermUser') ||
              fragmentSysDataPermUser.formFilter.dataPermId == null || fragmentSysDataPermUser.formFilter.dataPermId === ''">
              添加用户
            </el-button>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="fragmentSysDataPermUser.SysDataPermUserList.impl.dataList" size="mini" :height="getTableHeight + 'px'"
              @sort-change="fragmentSysDataPermUser.SysDataPermUserList.impl.onSortChange"
              header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px"
                :index="fragmentSysDataPermUser.SysDataPermUserList.impl.getTableIndex" />
              <el-table-column label="用户名" prop="loginName">
              </el-table-column>
              <el-table-column label="昵称" prop="showName">
              </el-table-column>
              <el-table-column label="账号类型">
                <template slot-scope="scope">
                  <span>{{SysUserType.getValue(scope.row.userType)}}</span>
                </template>
              </el-table-column>
              <el-table-column label="状态">
                <template slot-scope="scope">
                  <el-tag :type="getUserStatusType(scope.row.userStatus)" size="mini">{{SysUserStatus.getValue(scope.row.userStatus)}}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" fixed="right" width="80px">
                <template slot-scope="scope">
                  <el-button class="btn-table-delete" type="text" size="mini"
                    :disabled="!checkPermCodeExist('formSysDataPerm:fragmentSysDataPermUser:deleteDataPermUser')"
                    @click="onDeleteRow(scope.row)">移除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-col :span="24">
              <el-row type="flex" justify="end" style="margin-top: 10px;">
                <el-pagination
                  :total="fragmentSysDataPermUser.SysDataPermUserList.impl.totalCount"
                  :current-page="fragmentSysDataPermUser.SysDataPermUserList.impl.currentPage"
                  :page-size="fragmentSysDataPermUser.SysDataPermUserList.impl.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  layout="total, prev, pager, next, sizes"
                  @current-change="fragmentSysDataPermUser.SysDataPermUserList.impl.onCurrentPageChange"
                  @size-change="fragmentSysDataPermUser.SysDataPermUserList.impl.onPageSizeChange">
                </el-pagination>
              </el-row>
            </el-col>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SysDataPermController, DictionaryController } from '@/api';
import formEditSysDataPerm from '@/views/upms/formEditSysDataPerm';
import formSetSysDataPermUser from '@/views/upms/formSetSysDataPermUser';

export default {
  name: 'formSysDataPerm',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      activeFragmentId: undefined,
      fragmentSysDataPerm: {
        formFilter: {
          sysDatapermType: undefined,
          sysDataPermName: undefined
        },
        formFilterCopy: {
          sysDatapermType: undefined,
          sysDataPermName: undefined
        },
        sysDatapermType: {
          impl: new DropdownWidget(this.loadSysDatapermTypeDropdownList)
        },
        SysDataPerm: {
          impl: new TableWidget(this.loadSysDataPermData, this.loadSysDataPermVerify, true, false)
        },
        isInit: false
      },
      fragmentSysDataPermUser: {
        formFilter: {
          dataPermId: undefined,
          searchString: undefined
        },
        dataPermId: {
          impl: new DropdownWidget(this.loadDataPermDropdownList)
        },
        SysDataPermUserList: {
          impl: new TableWidget(this.loadSysDataPermUserListData, this.loadSysDataPermUserListVerify, true, false)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 数据权限数据获取函数，返回Primise
     */
    loadSysDataPermData (params) {
      params.sysDataPermDtoFilter = {
        searchString: this.fragmentSysDataPerm.formFilterCopy.sysDataPermName,
        ruleType: this.fragmentSysDataPerm.formFilterCopy.sysDatapermType
      };
      return new Promise((resolve, reject) => {
        SysDataPermController.list(this, params).then(res => {
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
     * 数据权限数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysDataPermVerify () {
      this.fragmentSysDataPerm.formFilterCopy.sysDataPermName = this.fragmentSysDataPerm.formFilter.sysDataPermName;
      this.fragmentSysDataPerm.formFilterCopy.sysDatapermType = this.fragmentSysDataPerm.formFilter.sysDatapermType;
      return true;
    },
    /**
     * 过滤规则下拉数据获取函数
     */
    loadSysDatapermTypeDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSysDataPermType(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 过滤规则下拉框显隐
     */
    onSysDatapermTypeVisibleChange (show) {
      this.fragmentSysDataPerm.sysDatapermType.impl.onVisibleChange(show).catch(e => {});
    },
    /**
     * 过滤规则选中值改变
     */
    onSysDatapermTypeValueChange (value) {
    },
    /**
     * 更新数据权限管理
     */
    refreshFragmentSysDataPerm (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysDataPerm.SysDataPerm.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysDataPerm.SysDataPerm.impl.refreshTable();
      }
      this.fragmentSysDataPerm.sysDatapermType.impl.onVisibleChange(true).catch(e => {});
      this.fragmentSysDataPerm.isInit = true;
    },
    /**
     * 新建
     */
    onAddDataPermClick () {
      let params = {};

      this.$dialog.show('新建数据权限', formEditSysDataPerm, {
        area: ['800px', '520px']
      }, params).then(res => {
        this.fragmentSysDataPermUser.dataPermId.impl.dirty = true;
        this.refreshFragmentSysDataPerm();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onEditDataPermClick (row) {
      let params = {
        dataPermId: row.dataPermId
      };

      this.$dialog.show('编辑数据权限', formEditSysDataPerm, {
        area: ['800px', '520px']
      }, params).then(res => {
        if (row.dataPermId === this.fragmentSysDataPermUser.formFilter.dataPermId) {
          this.fragmentSysDataPermUser.formFilter.dataPermId = undefined;
          this.fragmentSysDataPermUser.SysDataPermUserList.impl.clearTable();
        }
        this.fragmentSysDataPermUser.dataPermId.impl.dirty = true;
        this.fragmentSysDataPerm.SysDataPerm.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        dataPermId: row.dataPermId
      };

      this.$confirm('是否删除此数据权限？').then(res => {
        SysDataPermController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          if (row.dataPermId === this.fragmentSysDataPermUser.formFilter.dataPermId) {
            this.fragmentSysDataPermUser.formFilter.dataPermId = undefined;
            this.fragmentSysDataPermUser.SysDataPermUserList.impl.clearTable();
          }
          this.fragmentSysDataPermUser.dataPermId.impl.dirty = true;
          this.fragmentSysDataPerm.SysDataPerm.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onDataPermChange (value) {
      this.refreshFragmentSysDataPermUser(true);
    },
    getUserStatusType (status) {
      if (status === this.SysUserStatus.NORMAL) {
        return 'success';
      } else if (status === this.SysUserStatus.LOCKED) {
        return 'danger';
      } else {
        return 'info';
      }
    },
    loadDataPermDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        SysDataPermController.list(this, params).then(res => {
          resolve(res.data.dataList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 用户列表数据获取函数，返回Primise
     */
    loadSysDataPermUserListData (params) {
      return new Promise((resolve, reject) => {
        if (this.fragmentSysDataPermUser.formFilter.dataPermId == null || this.fragmentSysDataPermUser.formFilter.dataPermId === '') {
          this.$message.error('请选择数据权限');
          resolve({
            dataList: [],
            totalCount: 0
          });
          return;
        }

        params.dataPermId = this.fragmentSysDataPermUser.formFilter.dataPermId;
        params.searchString = this.fragmentSysDataPermUser.formFilter.searchString;
        SysDataPermController.listDataPermUser(this, params).then(res => {
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
     * 用户列表数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysDataPermUserListVerify () {
      return true;
    },
    onDeleteRow (row) {
      this.$confirm('是否移除此用户?').then(res => {
        let params = {
          dataPermId: this.fragmentSysDataPermUser.formFilter.dataPermId,
          userId: row.userId
        }

        SysDataPermController.deleteDataPermUser(this, params).then(res => {
          this.refreshFragmentSysDataPermUser(true);
          this.$message.success('移除成功');
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 更新数据权限授权
     */
    refreshFragmentSysDataPermUser (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.fragmentSysDataPermUser.SysDataPermUserList.impl.refreshTable(true, 1);
      } else {
        this.fragmentSysDataPermUser.SysDataPermUserList.impl.refreshTable();
      }
      this.fragmentSysDataPermUser.isInit = true;
    },
    /**
     * 授权用户
     */
    onAddDataPermUserClick () {
      let params = {
        dataPermId: this.fragmentSysDataPermUser.formFilter.dataPermId
      };

      this.$dialog.show('授权用户', formSetSysDataPermUser, {
        area: ['1100px', '580px']
      }, params).then(res => {
        this.refreshFragmentSysDataPermUser(true);
      }).catch(e => {
        this.refreshFragmentSysDataPermUser(true);
      });
    },
    buildFragmentPermCodeMap () {
      this.permCodeList = [
        {
          key: 'fragmentSysDataPerm',
          permCode: 'formSysDataPerm:fragmentSysDataPerm',
          refresh: this.refreshFragmentSysDataPerm
        },
        {
          key: 'fragmentSysDataPermUser',
          permCode: 'formSysDataPerm:fragmentSysDataPermUser',
          refresh: this.refreshFragmentSysDataPermUser
        }
      ];
    },
    onFragmentChange (fragmentId) {
      for (let i = 0; i < this.permCodeList.length; i++) {
        if (this.permCodeList[i].key === fragmentId) {
          this.activeFragmentId = fragmentId;
          // if (this.permCodeList[i].refresh) this.permCodeList[i].refresh();
          return true;
        }
      }
      return false;
    },
    getActiveFragment () {
      for (let i = 0; i < this.permCodeList.length; i++) {
        if (this.permCodeList[i].key === this.activeFragmentId) {
          return this.permCodeList[i];
        }
      }
    },
    /**
     * 根据权限获取默认显示的fragment
     */
    getDefaultFragment () {
      for (let i = 0; i < this.permCodeList.length; i++) {
        if (this.checkPermCodeExist(this.permCodeList[i].permCode)) {
          this.activeFragmentId = this.permCodeList[i].key;
          return this.permCodeList[i];
        }
      }
      return undefined;
    },
    onResume () {
      this.refreshFragmentSysDataPerm();
    },
    initFormData () {
    },
    formInit () {
      this.buildFragmentPermCodeMap();
      let defaultFragment = this.getDefaultFragment();
      if (defaultFragment == null) {
        this.$message.error('您没有访问这个页面的权限，请与系统管理员联系！');
      } else {
        if (defaultFragment.refresh) defaultFragment.refresh();
      }
    }
  },
  computed: {
    getTableHeight () {
      return (this.getMainContextHeight - 192);
    },
    ...mapGetters(['getMainContextHeight'])
  },
  created () {
    this.formInit();
  }
}
</script>
