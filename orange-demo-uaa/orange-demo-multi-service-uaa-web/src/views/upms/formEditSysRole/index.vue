<template>
  <el-form ref="form" :model="formData" :rules="rules" label-width="80px" size="mini" label-position="right" @submit.native.prevent>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="角色名称" clearable />
        </el-form-item>
        <el-form-item v-if="false" label="角色类型" :required="true">
          <el-radio-group v-model="formData.adminRole">
            <el-radio :label="1">管理员</el-radio>
            <el-radio :label="0">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-card shadow="never">
          <div slot="header" class="card-header">
            <span>菜单权限</span>
            <el-input size="mini" v-model="menuNameFilter" placeholder="输入菜单名称过滤" style="width: 250px;" clearable suffix-icon="el-icon-search" />
          </div>
          <el-scrollbar style="height: 230px;" wrap-class="scrollbar_dropdown__wrap">
            <el-tree ref="authTree" :data="authData" :props="treeProps" :check-strictly="false"
              show-checkbox node-key="menuId" :filter-node-method="filterMneuNode">
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>
    <!-- 弹窗下发按钮栏，必须设置class为dialog-btn-layer -->
    <el-row type="flex" justify="end" class="dialog-btn-layer" style="margin-top: 20px;">
      <el-button size="mini" @click="onCancel(false)" >取消</el-button>
      <el-button type="primary" size="mini" @click="onSubmit"
        :disabled="!(checkPermCodeExist('formSysRole:fragmentSysRole:update') || checkPermCodeExist('formSysRole:fragmentSysRole:add'))">
        确定
      </el-button>
    </el-row>
  </el-form>
</template>

<script>
import { treeDataTranslate, findItemFromList } from '@/utils'
import { SystemController } from '@/api'

export default {
  props: {
    rowData: {
      type: Object
    }
  },
  data () {
    return {
      menuNameFilter: undefined,
      formData: {
        roleId: undefined,
        roleName: undefined,
        adminRole: false,
        menuIdListString: undefined
        /*
        permsJsonData: {
          checked: [],
          halfChecked: [],
        },
        */
      },
      rules: {
        roleName: [{required: true, message: '角色名称不能为空', trigger: 'blur'}]
      },
      treeProps: {
        label: 'menuName'
      },
      authData: [],
      allowParentList: []
    }
  },
  methods: {
    filterMneuNode (value, data) {
      if (!value) return true;
      if (data.menuName.indexOf(value) !== -1) {
        this.allowParentList.push(data.menuId);
        return true;
      } else {
        return this.allowParentList.indexOf(data.parentId) !== -1;
      }
    },
    onCancel (isSuccess = false) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSubmit () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          let selectMenu = [];
          selectMenu = this.$refs.authTree.getHalfCheckedKeys();
          selectMenu = selectMenu.concat(this.$refs.authTree.getCheckedKeys());

          if (selectMenu.length <= 0) {
            this.$message.error({message: '请选择角色的菜单权限', showClose: true});
            return;
          }
          let params = {
            sysRoleDto: {...this.formData}
          }
          params.menuIdListString = selectMenu.join(',');
          if (this.isEdit) {
            SystemController.updateRole(this, params).then(res => {
              this.$message.success('编辑成功');
              this.onCancel(true);
            }).catch(e => {});
          } else {
            SystemController.addRole(this, params).then(res => {
              this.$message.success('添加成功');
              this.onCancel(true);
            }).catch(e => {});
          }
        }
      });
    },
    loadAuthData () {
      SystemController.getMenuPermList(this, {}).then(res => {
        this.authData = treeDataTranslate(res.data, 'menuId', 'parentId');
        if (Array.isArray(this.formData.menuIdList)) {
          let tempList = [];
          this.formData.menuIdList.forEach((item) => {
            let tempMenu = findItemFromList(res.data, item, 'menuId');
            // 判断是否为叶子节点
            if (tempMenu != null && (!Array.isArray(tempMenu.children) || tempMenu.children.length <= 0)) {
              tempList.push(item);
            }
          });
          this.$refs.authTree.setCheckedKeys(tempList);
        }
      }).catch(e => {});
    }
  },
  computed: {
    isEdit () {
      return this.formData.roleId != null;
    }
  },
  mounted () {
    if (this.rowData) {
      this.formData = {...this.formData, ...this.rowData};
      if (Array.isArray(this.formData.sysRoleMenuList)) {
        this.formData.menuIdList = this.formData.sysRoleMenuList.map(item => item.menuId);
      }
    }
    this.loadAuthData();
  },
  watch: {
    menuNameFilter (val) {
      this.allowParentList = [];
      this.$refs.authTree.filter(val);
    }
  }
}
</script>

<style>
</style>
