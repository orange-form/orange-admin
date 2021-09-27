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
        <el-form-item label="所属部门" prop="deptId">
          <el-cascader class="input-item" v-model="deptId.value" :clearable="true"
            placeholder="所属部门" :loading="deptId.impl.loading" :props="{value: 'deptId', label: 'deptName', checkStrictly: true}"
            @visible-change="onDeptIdVisibleChange" :options="deptId.impl.dropdownList"
            @change="onDeptIdValueChange">
          </el-cascader>
        </el-form-item>
        <el-form-item label="用户岗位" prop="deptPostIdList">
          <el-select v-model="formData.deptPostIdList" multiple placeholder="用户岗位">
            <el-option v-for="deptPost in deptPostList" :key="deptPost.deptPostId"
              :label="deptPost.postShowName" :value="deptPost.deptPostId" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户角色" prop="roleIdList">
          <el-select v-model="formData.roleIdList" multiple placeholder="用户角色">
            <el-option v-for="role in roleList" :key="role.roleId"
              :label="role.roleName" :value="role.roleId" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据权限" prop="dataPermIdList">
          <el-select v-model="formData.dataPermIdList" multiple placeholder="数据权限">
            <el-option v-for="dataPerm in dataPermList" :key="dataPerm.dataPermId"
              :label="dataPerm.dataPermName" :value="dataPerm.dataPermId" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <!-- 弹窗下发按钮栏，必须设置class为dialog-btn-layer -->
    <el-row type="flex" justify="end" class="dialog-btn-layer">
      <el-button size="mini" type="primary" :plain="true" @click="onCancel(false)" >取消</el-button>
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
import { SystemController, SysDeptController, SysDataPermController, DictionaryController } from '@/api';
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
        dataPermIdList: [],
        deptPostIdList: [],
        roleIdList: []
      },
      params: {
        userId: undefined,
        loginName: undefined,
        password: undefined,
        showName: undefined,
        deptId: '',
        userType: 2,
        userStatus: 0,
        dataPermIdListString: undefined,
        deptPostIdListString: undefined,
        roleIdListString: undefined
      },
      deptId: {
        impl: new DropdownWidget(this.loadDeptmentDropdownList, true, 'deptId'),
        value: []
      },
      rules: {
        loginName: [{required: true, message: '用户名称不能为空', trigger: 'blur'}],
        password: [{required: true, message: '用户密码不能为空', trigger: 'blur'}],
        passwordRepeat: [{required: true, message: '重输密码不能为空', trigger: 'blur'}],
        showName: [{required: true, message: '用户昵称不能为空', trigger: 'blur'}],
        dataPermIdList: [{required: true, message: '数据权限不能为空', trigger: 'change'}],
        deptPostIdList: [{required: true, message: '用户岗位不能为空', trigger: 'change'}],
        roleIdList: [{required: true, message: '用户角色不能为空', trigger: 'change'}]
      },
      showHeaderSelect: false,
      dataPermList: [],
      deptPostList: [],
      roleList: []
    }
  },
  methods: {
    /**
     * 所属部门下拉数据获取函数
     */
    loadDeptmentDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        SysDeptController.list(this, params).then(res => {
          resolve(res.data.dataList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所属部门下拉框显隐
     */
    onDeptIdVisibleChange (show) {
      this.deptId.impl.onVisibleChange(show).catch(e => {});
    },
    /**
     * 所属部门选中值改变
     */
    onDeptIdValueChange (value) {
      this.formData.deptId = Array.isArray(value) ? value[value.length - 1] : undefined;
      this.formData.deptPostIdList = undefined;
      this.loadDeptPost();
    },
    /**
     * 获取部门岗位列表
     */
    loadDeptPost () {
      if (this.formData.deptId == null || this.formData.deptId === '') {
        this.deptPostList = [];
        return;
      }
      DictionaryController.dictDeptPost(this, {
        deptId: this.formData.deptId
      }).then(res => {
        this.deptPostList = res;
      }).catch(e => {});
    },
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
            sysUserDto: {
              userId: this.formData.userId,
              loginName: this.formData.loginName,
              password: this.formData.password,
              showName: this.formData.showName,
              userType: this.formData.userType,
              deptId: this.formData.deptId,
              userStatus: this.formData.userStatus
            },
            dataPermIdListString: Array.isArray(this.formData.dataPermIdList) ? this.formData.dataPermIdList.join(',') : undefined,
            deptPostIdListString: Array.isArray(this.formData.deptPostIdList) ? this.formData.deptPostIdList.join(',') : undefined,
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
    loadDataPerm () {
      SysDataPermController.list(this, {}).then(res => {
        this.dataPermList = res.data.dataList;
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
      this.formData = {...this.rowData, dataPermIdList: [], deptPostIdList: [], roleIdList: []};
      if (Array.isArray(this.formData.sysDataPermUserList)) {
        this.formData.dataPermIdList = this.formData.sysDataPermUserList.map(item => item.dataPermId);
      }
      if (Array.isArray(this.formData.sysUserPostList)) {
        this.formData.deptPostIdList = this.formData.sysUserPostList.map(item => item.deptPostId);
      }
      if (Array.isArray(this.formData.sysUserRoleList)) {
        this.formData.roleIdList = this.formData.sysUserRoleList.map(item => item.roleId);
      }
    }
    this.deptId.impl.onVisibleChange(true).then(res => {
      this.deptId.value = findTreeNodePath(this.deptId.impl.dropdownList, this.formData.deptId, 'deptId');
    });
    this.loadRole();
    this.loadDataPerm();
    this.loadDeptPost();
  }
}
</script>

<style lang="scss" scoped>
</style>
