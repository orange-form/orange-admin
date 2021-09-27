<template>
  <el-form ref="form" :model="formData" :rules="rules" label-width="110px" size="mini" label-position="right" @submit.native.prevent>
    <el-row :gutter="20" class="full-width-input">
      <el-col :span="12">
        <el-form-item label="上级菜单">
          <el-cascader :options="menuTree" v-model="parentMenuPath" :props="menuProps" placeholder="选择父菜单"
            :disabled="!canEditParent || isEdit" :clearable="true" :change-on-select="true" size="mini"
            @change="onParentMenuChange" />
        </el-form-item>
      </el-col>
      <el-col :span="12" >
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="formData.menuName" placeholder="菜单名称" clearable />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="菜单类型" prop="menuType">
          <el-select v-model="formData.menuType" :disabled="isEdit" placeholder="菜单类型" @change="onMenuTypeChange">
            <el-option v-for="item in getValidMenuType" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="显示顺序" prop="showOrder">
          <el-input-number v-model="formData.showOrder" controls-position="right"
            :min="1" :max="99999" placeholder="请输入显示顺序"></el-input-number>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="绑定类型">
          <el-select v-model="formData.bindType" plaaceholder="菜单绑定类型"
            :disabled="formData.menuType !== 1 || isEdit"
            @change="onBindTypeChange">
            <el-option v-for="item in SysMenuBindType.getList()" :key="item.id"
              :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12" v-if="formData.bindType === SysMenuBindType.ROUTER">
        <el-form-item label="菜单路由">
          <el-input v-model="formData.formRouterName" placeholder="菜单路由"
            :disabled="formData.menuType !== 1" />
        </el-form-item>
      </el-col>
      <el-col :span="12" v-if="formData.bindType === SysMenuBindType.ONLINE_FORM">
        <el-form-item label="在线表单" prop="onlineFormId">
          <el-cascader v-model="onlineFormPath" :options="formTreeData" filterable
            :clearable="true" placeholder="选择菜单绑定的在线表单"
            :props="{value: 'id', label: 'name'}"
            @change="onOnlineFormChange" />
        </el-form-item>
      </el-col>
      <el-col :span="12" v-if="formData.bindType === SysMenuBindType.WORK_ORDER">
        <el-form-item label="工作流" prop="onlineFlowEntryId">
          <el-cascader v-model="onlineFlowPath" :options="entryTreeData" filterable
            :clearable="true" placeholder="选择菜单绑定的工单"
            :props="{value: 'id', label: 'name'}"
            @change="onOnlineEntryChange" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="菜单图标" prop="icon">
          <icon-select v-model="formData.icon" :height="28" />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-card shadow="never">
          <div slot="header" class="card-header">
            <span>权限字列表</span>
            <el-input size="mini" v-model="permCodeNameFilter" placeholder="输入权限字名称过滤" style="width: 250px;" clearable suffix-icon="el-icon-search" />
          </div>
          <el-scrollbar style="height: 210px;" wrap-class="scrollbar_dropdown__wrap">
            <el-tree ref="permCodeTree" :check-strictly="true"
              v-show="formData.menuType && formData.bindType !== SysMenuBindType.ONLINE_FORM"
              :props="treeProps" node-key="permCodeId" :default-expanded-keys="defaultExpandedKeys"
              show-checkbox :data="getPermCodeTree" :filter-node-method="filterPermCodeNode"></el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>
    <!-- 弹窗按钮 -->
    <el-row type="flex" justify="end" class="dialog-btn-layer mt20">
      <el-button size="mini" @click="onCancel(false)" >取消</el-button>
      <el-button type="primary" size="mini" @click="onSubmit"
        :disabled="!(checkPermCodeExist('formSysMenu:fragmentSysMenu:add') || checkPermCodeExist('formSysMenu:fragmentSysMenu:update'))">
        确定
      </el-button>
    </el-row>
  </el-form>
</template>

<script>
// eslint-disable-next-line no-unused-vars
import { treeDataTranslate, findTreeNodePath, findTreeNode, findItemFromList } from '@/utils';
import { SystemController } from '@/api';
import { OnlinePageController } from '@/api/onlineController.js';
import { FlowOperationController } from '@/api/flowController.js';
import IconSelect from '@/components/IconSelect/index.vue';

export default {
  props: {
    menuId: {
      type: String
    },
    parentId: {
      type: String
    },
    menuList: {
      type: Array,
      default: undefined
    },
    rowData: {
      type: Object,
      default: undefined
    }
  },
  components: {
    IconSelect
  },
  data () {
    return {
      // 是否自动用上级菜单的名称过滤权限字列表，当这个开关打开后，会使用getAutoFilterString返回的字符串当做过滤字符串
      autoFilter: false,
      permCodeNameFilter: undefined,
      allowParentList: [],
      formData: {
        menuId: undefined,
        parentId: undefined,
        menuName: undefined,
        showOrder: undefined,
        menuType: undefined,
        icon: undefined,
        bindType: this.SysMenuBindType.ROUTER,
        onlineFormId: undefined,
        onlineFlowEntryId: undefined,
        formRouterName: undefined
      },
      onlineFormPath: [],
      formTreeData: [],
      onlineFlowPath: [],
      entryTreeData: [],
      menuProps: {
        label: 'menuName',
        value: 'menuId'
      },
      parentMenuType: undefined,
      parentMenuPath: [],
      menuTree: [],
      permCodeList: [],
      defaultExpandedKeys: [],
      rules: {
        menuName: [{required: true, message: '请输入菜单名称', trigger: 'blur'}],
        showOrder: [{required: true, message: '请输入菜单显示顺序', trigger: 'blur'}],
        onlineFormId: [{required: true, message: '请选择菜单绑定的在线表单', trigger: 'blur'}],
        onlineFlowEntryId: [{required: true, message: '请选择菜单绑定的工单', trigger: 'blur'}],
        formRouterName: [{required: true, message: '请输入菜单路由名称', trigger: 'blur'}]
      },
      treeProps: {
        label: 'showName'
      }
    };
  },
  methods: {
    getAutoFilterString () {
      let node = findTreeNode(this.menuTree, this.parentMenuPath[this.parentMenuPath.length - 1], 'menuId');
      return node ? node.menuName : undefined;
    },
    onMenuTypeChange (value) {
      if (this.autoFilter && value === this.SysMenuType.BUTTON && (this.permCodeNameFilter == null || this.permCodeNameFilter === '')) {
        this.permCodeNameFilter = this.getAutoFilterString();
      }
    },
    filterPermCodeNode (value, data) {
      if (!value) return true;
      if (data.showName.indexOf(value) !== -1) {
        this.allowParentList.push(data.permCodeId);
        return true;
      } else {
        return this.allowParentList.indexOf(data.parentId) !== -1;
      }
    },
    onParentMenuChange (value, isInit) {
      if (!isInit) this.formData.menuType = undefined;
      this.parentMenuType = undefined;
      if (Array.isArray(value) && value.length > 0) {
        let node = findTreeNode(this.menuTree, value[value.length - 1], 'menuId');
        if (node) this.parentMenuType = node.menuType;
      }
    },
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSubmit () {
      return new Promise((resolve, reject) => {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            let params = {};
            params.sysMenuDto = {...this.formData};
            if (this.formData.bindType === this.SysMenuBindType.ROUTER) {
              params.sysMenuDto.onlineFormId = undefined;
              params.sysMenuDto.onlineFlowEntryId = undefined;
            }
            if (this.parentMenuPath.length > 0) {
              params.sysMenuDto.parentId = this.parentMenuPath[this.parentMenuPath.length - 1];
            }
            if ([this.SysMenuType.MENU, this.SysMenuType.BUTTON, this.SysMenuType.FRAGMENT].indexOf(params.sysMenuDto.menuType) !== -1) {
              let tempList = this.$refs.permCodeTree.getHalfCheckedKeys();
              tempList = tempList.concat(this.$refs.permCodeTree.getCheckedKeys());
              params.permCodeIdListString = tempList.join(',');
            }
            if (this.isEdit) {
              SystemController.updateMenu(this, params).then(res => {
                resolve(res);
                this.$message.success('编辑成功');
                this.onCancel(true);
              }).catch(e => {
                reject(e);
              });
            } else {
              SystemController.addMenu(this, params).then(res => {
                resolve(res);
                this.$message.success('添加成功');
                this.onCancel(true);
              }).catch(e => {
                reject(e);
              });
            }
          } else {
            reject();
          }
        });
      });
    },
    formatMenuTree () {
      this.menuTree = [];
      if (Array.isArray(this.menuList)) {
        this.menuTree = this.menuList.filter((item) => {
          return item.menuType !== this.SysMenuType.BUTTON && item.menuId !== this.formData.menuId;
        });
      }
      this.menuTree = treeDataTranslate(this.menuTree, 'menuId', 'parentId');
    },
    onBindTypeChange (value) {
      this.formData.onlineFormId = undefined;
      this.formData.formRouterName = undefined;
      this.formData.onlineFlowEntryId = undefined;
    },
    onOnlineFormChange (values) {
      this.formData.onlineFormId = values[1];
    },
    onOnlineEntryChange (values) {
      this.formData.onlineFlowEntryId = values[0];
      this.formData.onlineFormId = values[1];
    },
    loadPageAndForms () {
      OnlinePageController.listAllPageAndForm(this, {}).then(res => {
        let formList = res.data.formList;
        let pageList = res.data.pageList;
        if (!Array.isArray(formList) || !Array.isArray(pageList)) {
          this.formTreeData = [];
          return;
        }
        this.formTreeData = pageList.map(page => {
          let children = formList.filter(form => {
            return form.pageId === page.pageId && form.formKind === this.SysOnlineFormKind.PAGE && form.formType === this.SysOnlineFormType.QUERY;
          }).map(form => {
            return {
              id: form.formId,
              name: form.formName
            }
          });

          return {
            id: page.pageId,
            name: page.pageName,
            disabled: !page.published,
            children
          }
        });
        if (this.formData.onlineFormId) {
          this.onlineFormPath = findTreeNodePath(this.formTreeData, this.formData.onlineFormId, 'id');
        }
      }).catch(e => {});
    },
    loadFlowEntryForms () {
      FlowOperationController.listFlowEntryForm(this, {}).then(res => {
        if (Array.isArray(res.data)) {
          this.entryTreeData = res.data.map(entry => {
            let formList = [];
            if (Array.isArray(entry.formList)) {
              formList = entry.formList.filter(form => form.formType === this.SysOnlineFormType.WORK_ORDER).map(form => {
                return {
                  id: form.formId,
                  name: form.formName
                }
              });
            }
            if (formList.length > 0) {
              return {
                id: entry.entryId,
                name: entry.processDefinitionName,
                children: formList
              }
            }
            return null;
          }).filter(entry => entry != null);
        }
        if (this.formData.onlineFlowEntryId) {
          this.onlineFlowPath = [this.formData.onlineFlowEntryId, this.formData.onlineFormId];
        }
      }).catch(e => {});
    },
    initData () {
      this.defaultExpandedKeys = [];
      this.formatMenuTree();
      this.parentMenuPath = findTreeNodePath(this.menuTree, this.formData.parentId, 'menuId');
      this.onParentMenuChange(this.parentMenuPath, true);
      this.onMenuTypeChange(this.formData.menuType);
      SystemController.getPermCodeList(this, {}).then(res => {
        this.permCodeList = res.data;
        if (Array.isArray(this.formData.permCodeIdList) && this.formData.permCodeIdList.length > 0) {
          let tempList = [];
          this.permCodeList.forEach((item) => {
            if (findItemFromList(this.formData.permCodeIdList, item.permCodeId) != null) {
              tempList.push(item.permCodeId);
            }
          });
          this.defaultExpandedKeys = tempList;
          this.$refs.permCodeTree.setCheckedKeys(tempList);
          this.$nextTick(() => {
            this.allowParentList = [];
            if (this.permCodeNameFilter != null && this.permCodeNameFilter !== '') this.$refs.permCodeTree.filter(this.permCodeNameFilter);
          });
        }
      }).catch(e => {});
      this.loadPageAndForms();
      this.loadFlowEntryForms();
    }
  },
  computed: {
    getValidMenuType () {
      let allList = this.SysMenuType.getList();
      if (this.parentMenuType == null) {
        return allList.filter((item) => {
          return [this.SysMenuType.MENU, this.SysMenuType.DIRECTORY].indexOf(item.id) !== -1;
        });
      } else {
        return allList.filter((item) => {
          switch (Number.parseInt(this.parentMenuType)) {
            case this.SysMenuType.DIRECTORY: return [this.SysMenuType.MENU, this.SysMenuType.DIRECTORY].indexOf(item.id) !== -1;
            case this.SysMenuType.MENU: return [this.SysMenuType.FRAGMENT, this.SysMenuType.BUTTON].indexOf(item.id) !== -1;
            case this.SysMenuType.FRAGMENT: return item.id === this.SysMenuType.BUTTON;
            default: return false;
          }
        });
      }
    },
    isEdit () {
      return this.formData.menuId != null;
    },
    // 判断是否新建一个目录
    isNew () {
      return this.formData.parentId == null && this.formData.menuId == null;
    },
    canEditParent () {
      return this.parentId == null;
    },
    getPermCodeTree () {
      if (this.permCodeList != null) {
        return treeDataTranslate(this.permCodeList, 'permCodeId', 'parentId');
      } else {
        return [];
      }
    }
  },
  mounted () {
    if (this.rowData != null) {
      this.formData = {...this.formData, ...this.rowData};
      if (this.formData.onlineFlowEntryId != null) {
        this.formData.bindType = this.SysMenuBindType.WORK_ORDER;
      } else {
        this.formData.bindType = this.formData.onlineFormId == null ? this.SysMenuBindType.ROUTER : this.SysMenuBindType.ONLINE_FORM;
      }
      if (Array.isArray(this.formData.sysMenuPermCodeList)) {
        this.formData.permCodeIdList = this.formData.sysMenuPermCodeList.map(item => item.permCodeId);
      }
    }
    if (this.parentId != null) this.formData.parentId = this.parentId;
    this.initData();
  },
  watch: {
    permCodeNameFilter (val) {
      this.allowParentList = [];
      this.$refs.permCodeTree.filter(val);
    }
  }
};
</script>
