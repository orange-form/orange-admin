<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formModifyPassword" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="120px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input class="input-item" v-model.trim="formData.oldPassword"
              type="password" show-password
              :clearable="true" placeholder="旧密码" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="新密码" prop="password">
            <el-input class="input-item" v-model.trim="formData.password"
              type="password" show-password
              :clearable="true" placeholder="新密码" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="新密码确认" prop="repeatPassword">
            <el-input class="input-item" v-model.trim="formData.repeatPassword"
              type="password" show-password
              :clearable="true" placeholder="新密码确认" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" @click="onSave()">
              保存
            </el-button>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { SystemController } from '@/api';
import { encrypt } from '@/utils';

export default {
  data () {
    return {
      formData: {
        oldPassword: undefined,
        password: undefined,
        repeatPassword: undefined
      },
      rules: {
        'oldPassword': [
          {required: true, message: '请输入旧密码', trigger: 'blur'}
        ],
        'password': [
          {required: true, message: '请输入新密码', trigger: 'blur'}
        ],
        'repeatPassword': [
          {required: true, message: '请输入新密码', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSave () {
      this.$refs.formModifyPassword.validate((valid) => {
        if (!valid) return;
        if (this.formData.password !== this.formData.repeatPassword) {
          this.$message.error('两次密码输入不一致，请核对！');
          return;
        }
        
        let params = {
          oldPass: encrypt(this.formData.oldPassword),
          newPass: encrypt(this.formData.password)
        };

        SystemController.changePassword(this, params).then(res => {
          this.$message.success('密码修改成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    }
  }
}
</script>

<style>
</style>
