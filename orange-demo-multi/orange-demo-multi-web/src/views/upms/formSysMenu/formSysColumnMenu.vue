<template>
  <div class="tab-dialog-box" style="position: relative;">
    <el-tabs v-model="activeFragment">
      <el-tab-pane label="栏目管理" name="fragmentSysColumn" style="width: 100%;">
          <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
            <el-row type="flex" justify="end" style="margin-bottom: 18px;">
              <el-button type="primary" size="mini" :plain="true"
                @click="initFormData()">
                刷新
              </el-button>
              <el-button type="primary" size="mini" :disabled="!checkPermCodeExist('formSysMenu:fragmentSysMenu:add')"
                @click="onCreateSysColumnClick()">
                新建
              </el-button>
            </el-row>
          </el-form>
          <el-row>
            <el-col :span="24">
              <el-table :data="columnList" size="mini" :height="getTabPaneHeight + 'px'"
                header-cell-class-name="table-header-gray" row-key="menuId">
                <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" />
                <el-table-column label="栏目名称" prop="columnName" />
                <el-table-column label="显示顺序" prop="showOrder" />
                <el-table-column label="操作" width="200px">
                  <template slot-scope="scope">
                    <el-button @click="onEditSysColumnClick(scope.row)" type="text" size="mini"
                      :disabled="!checkPermCodeExist('formSysMenu:fragmentSysMenu:update')">
                      编辑
                    </el-button>
                    <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
                      :disabled="!checkPermCodeExist('formSysMenu:fragmentSysMenu:delete')">
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-col>
          </el-row>
      </el-tab-pane>
      <el-tab-pane label="菜单管理" name="fragmentSysMenu" style="width: 100%;">
        <el-form label-width="80px" size="mini" label-position="right" @submit.native.prevent>
          <el-row>
            <el-col :span="12">
              <el-form-item label="所属栏目" style="flex-grow: 1">
                <el-select class="filter-item" v-model="currentColumnId" filterable
                  placeholder="所属栏目">
                  <el-option v-for="item in columnList" :key="item.columnId" :value="item.columnId" :label="item.columnName" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-row type="flex" justify="end">
                <el-button type="primary" size="mini" :plain="true"
                  @click="initFormData(true)">
                  刷新
                </el-button>
                <el-button type="primary" size="mini" :disabled="!checkPermCodeExist('formSysMenu:fragmentSysMenu:add')"
                  @click="onCreateSysMenuClick()">
                  新建
                </el-button>
              </el-row>
            </el-col>
          </el-row>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table :data="currentMenuTree" size="mini" :height="getTabPaneHeight + 'px'"
              header-cell-class-name="table-header-gray" row-key="menuId">
              <el-table-column label="菜单名称" prop="menuName" width="300px">
              </el-table-column>
              <el-table-column label="菜单图标" prop="icon" width="100px">
                <template slot-scope="scope">
                  <i :class="scope.row.icon" />
                </template>
              </el-table-column>
              <el-table-column label="菜单顺序" prop="showOrder" width="100px">
              </el-table-column>
              <el-table-column label="菜单类型" prop="menuType" width="100px">
                <template slot-scope="scope">
                  <el-tag size="mini" :type="getMenuType(scope.row)">{{SysMenuType.getValue(scope.row.menuType)}}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="菜单路由" prop="formRouterName" min-width="250px">
              </el-table-column>
              <el-table-column label="操作" width="200px">
                <template slot-scope="scope">
                  <el-button @click="onEditSysMenuClick(scope.row)" type="text" size="mini"
                    :disabled="!checkPermCodeExist('formSysMenu:fragmentSysMenu:update')">
                    编辑
                  </el-button>
                  <el-button @click="onAddChildSysMenuClick(scope.row)" type="text" size="mini"
                    :disabled="!checkPermCodeExist('formSysMenu:fragmentSysMenu:add') || scope.row.menuType === SysMenuType.BUTTON">
                    添加
                  </el-button>
                  <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
                    :disabled="!checkPermCodeExist('formSysMenu:fragmentSysMenu:delete')">
                    删除
                  </el-button>
                  <el-button @click="onShowPermList(scope.row)" type="text" size="mini"
                    v-if="checkPermCodeExist('formSysMenu:fragmentSysMenu:listSysMenuPermDetail')"
                    :disabled="scope.row.menuType === SysMenuType.DIRECTORY">
                    权限详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { treeDataTranslate } from '@/utils';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { DictionaryController, SystemController } from '@/api';
import formEditSysMenu from '@/views/upms/formEditSysMenu';
import formMenuPerm from './formSysMenuPerm';
import formEditColumn from '@/views/upms/formEditSysMenu/editColumn.vue';

export default {
  name: 'formSysMenu',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      activeFragment: 'fragmentSysColumn',
      allMenuList: [],
      menuTree: [],
      currentColumnId: undefined,
      formSysMenu: {
        currentColumnId: {
          impl: new DropdownWidget(this.loadCurrentColumnDropdownList, false, 'columnId', 'columnName'),
          value: []
        },
        isInit: false
      }
    }
  },
  methods: {
    getMenuType (row) {
      if (row.menuType === 0) {
        return 'primary'
      } else if (row.menuType === 1) {
        return 'success';
      } else if (row.menuType === 2) {
        return 'danger';
      } else if (row.menuType === 3) {
        return 'warning';
      }
    },
    /**
     * 新建栏目
     */
    onCreateSysColumnClick () {
      this.$dialog.show('新建', formEditColumn, {
        area: ['500px']
      }, {}).then(res => {
        this.initFormData();
      }).catch(e => {});
    },
    /**
     * 编辑栏目
     */
    onEditSysColumnClick (row) {
      this.$dialog.show('编辑', formEditColumn, {
        area: ['500px']
      }, {
        ...row
      }).then(res => {
        this.initFormData();
      }).catch(e => {});
    },
    /**
     * 新建菜单
     */
    onCreateSysMenuClick () {
      let params = {
        parentId: this.currentColumnId,
        menuList: this.allMenuList
      };
      this.$dialog.show('新建', formEditSysMenu, {
        area: ['800px']
      }, params).then(res => {
        this.initFormData();
      }).catch(e => {});
    },
    /**
     * 编辑菜单
     */
    onEditSysMenuClick (row) {
      SystemController.viewMenu(this, {menuId: row.menuId}).then(res => {
        let params = {
          rowData: res.data,
          menuId: row.menuId,
          menuList: this.allMenuList
        };

        this.$dialog.show('编辑', formEditSysMenu, {
          area: ['800px']
        }, params).then(res => {
          this.formSysMenu.SysMenu.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 添加子菜单
     */
    onAddChildSysMenuClick (row) {
      let params = {
        parentId: row.menuId,
        menuList: this.allMenuList
      };

      this.$dialog.show('添加子菜单', formEditSysMenu, {
        area: ['800px']
      }, params).then(res => {
        this.formSysMenu.SysMenu.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        menuId: row.menuId
      };

      this.$confirm('是否删除此菜单项？').then(res => {
        SystemController.deleteMenu(this, params).then(res => {
          this.$message.success('删除成功');
          this.formSysMenu.SysMenu.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 权限详情
     */
    onShowPermList (row) {
      let params = {
        menuId: row.menuId
      };

      this.$dialog.show('权限详情 - ' + row.menuName, formMenuPerm, {
        area: '1200px',
        offset: '30px'
      }, params).catch(e => {
      });
    },
    onResume () {
      this.initFormData();
    },
    initFormData () {
      SystemController.getMenuPermList(this, {}).then(res => {
        this.allMenuList = res.data.map((item) => {
          return {...item};
        });
        this.menuTree = treeDataTranslate(res.data, 'menuId');
        // 获取默认的栏目
        this.currentColumnId = undefined;
        for (let i = 0; i < this.menuTree.length; i++) {
          if (this.menuTree[i].menuType === this.SysMenuType.DIRECTORY) {
            this.currentColumnId = this.menuTree[i].menuId;
            break;
          }
        }
      }).catch(e => {});
    },
    formInit () {
      this.initFormData();
    }
  },
  created () {
    this.formInit();
  },
  computed: {
    getTabPaneHeight () {
      return (this.getMainContextHeight - 150);
    },
    currentMenuTree () {
      if (this.currentColumnId == null || this.currentColumnId === '') return [];
      for (let i = 0; i < this.menuTree.length; i++) {
        if (this.menuTree[i].menuId === this.currentColumnId) return this.menuTree[i].children;
      }
      return [];
    },
    columnList () {
      return this.menuTree.map((item) => {
        return {
          menuId: item.menuId,
          columnId: item.menuId,
          columnName: item.menuName,
          showOrder: item.showOrder
        };
      });
    },
    ...mapGetters(['getMainContextHeight'])
  }
}
</script>
