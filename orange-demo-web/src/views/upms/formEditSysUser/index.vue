<template>
  <el-form ref="form" :model="formData" :rules="rules" label-width="110px" size="mini" label-position="right" @submit.native.prevent>
    <el-row :gutter="20" class="full-width-input">
      <el-col :span="24">
        <el-form-item label="登录名称" prop="loginName">
          <el-input v-model="formData.loginName" placeholder="用户登录名称" clearable :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="登录密码" v-if="!isEdit" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="用户登录密码" clearable />
        </el-form-item>
        <el-form-item label="再次输入密码" v-if="!isEdit" prop="passwordRepeat">
          <el-input v-model="formData.passwordRepeat" type="password" placeholder="再次输入用户密码" clearable />
        </el-form-item>
        <el-form-item label="用户昵称" prop="showName">
          <el-input v-model="formData.showName" placeholder="用户昵称" clearable />
        </el-form-item>
        <el-form-item label="账号类型" prop="userType">
          <el-select v-model="formData.userType">
            <el-option v-for="item in SysUserType.getList()" :key="item.id"
              :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户状态" prop="userStatus" v-if="isEdit">
          <el-radio-group v-model="formData.userStatus">
            <el-radio v-for="item in SysUserStatus.getList()" :key="item.id" :label="item.id">
              {{item.name}}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户角色" prop="roleIdList">
          <el-select v-model="formData.roleIdList" multiple placeholder="用户角色">
            <el-option v-for="role in roleList" :key="role.roleId"
              :label="role.roleName" :value="role.roleId" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <!-- 弹窗下发按钮栏，必须设置class为dialog-btn-layer -->
    <el-row type="flex" justify="end" class="dialog-btn-layer">
      <el-button size="mini" type="primary" @click="onCancel(false)" >取消</el-button>
      <el-button type="primary" size="mini" @click="onSubmit"
        :disabled="!(checkPermCodeExist('formSysUser:fragmentSysUser:update') || checkPermCodeExist('formSysUser:fragmentSysUser:add'))">
        确定
      </el-button>
    </el-row>
  </el-form>
</template>

<script>
/* eslint-disable-next-line */
import { findTreeNodePath } from '@/utils';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
import { SystemController } from '@/api';
import { uploadMixin } from '@/core/mixins';

export default {
  props: {
    rowData: Object
  },
  mixins: [uploadMixin],
  data () {
    return {
      formData: {
        userId: undefined,
        loginName: undefined,
        password: undefined,
        showName: undefined,
        userType: 2,
        userStatus: 0,
        roleIdList: []
      },
      params: {
        userId: undefined,
        loginName: undefined,
        password: undefined,
        showName: undefined,
        
        userType: 2,
        userStatus: 0,
        roleIdListString: undefined
      },
      rules: {
        loginName: [{required: true, message: '用户名称不能为空', trigger: 'blur'}],
        password: [{required: true, message: '用户密码不能为空', trigger: 'blur'}],
        passwordRepeat: [{required: true, message: '重输密码不能为空', trigger: 'blur'}],
        showName: [{required: true, message: '用户昵称不能为空', trigger: 'blur'}],
        roleIdList: [{required: true, message: '用户角色不能为空', trigger: 'change'}]
      },
      showHeaderSelect: false,
      roleList: []
    }
  },
  methods: {
    onCancel (isSuccess = false) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSubmit () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.formData.userId == null && (this.formData.password !== this.formData.passwordRepeat)) {
            this.$message.error({message: '两次密码输入不一致，请重新输入', showClose: true});
            return;
          }

          let params = {
            sysUser: {
              userId: this.formData.userId,
              loginName: this.formData.loginName,
              password: this.formData.password,
              showName: this.formData.showName,
              userType: this.formData.userType,
              
              userStatus: this.formData.userStatus
            },
            roleIdListString: Array.isArray(this.formData.roleIdList) ? this.formData.roleIdList.join(',') : undefined
          }

          let operation = null;
          if (this.formData.userId != null) {
            operation = SystemController.updateUser(this, params);
          } else {
            operation = SystemController.addUser(this, params);
          }

          operation.then(res => {
            this.$message.success(this.formData.userId != null ? '编辑成功' : '添加成功');
            this.onCancel(true);
          }).catch(e => {
            //
          })
        }
      });
    },
    loadRole () {
      SystemController.getRoleList(this, {}).then(res => {
        this.roleList = res.data.dataList;
      }).catch(e => {});
    },
    loadRowData (id) {
      var params = {
        userId: id
      }
      return SystemController.getUser(this, params);
    }
  },
  computed: {
    isEdit () {
      return (this.formData.userId != null);
    }
  },
  mounted () {
    if (this.rowData != null) {
      this.formData = {...this.rowData};
      if (Array.isArray(this.formData.sysUserRoleList)) {
        this.formData.roleIdList = this.formData.sysUserRoleList.map(item => item.roleId);
      }
    }
    this.loadRole();
  }
}
</script>

<style lang="scss" scoped>
</style>
