<template>
  <div class="login-form">
    <el-card class="box-card" style="width: 30vw; background-color: rgba(255, 255, 255, 0.9); border-width: 0px;" :body-style="{ padding: '40px' }">
      <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()">
        <el-row>
          <h3 class="title">橙单代码生成器</h3>
          <el-col :span="24">
            <el-form-item prop="mobilePhone">
              <el-input v-model="dataForm.mobilePhone" style="width: 100%;" placeholder="帐号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="password">
              <el-input v-model="dataForm.password" style="width: 100%;" type="password" placeholder="密码" show-password></el-input>
            </el-form-item>
          </el-col>
          <el-button class="login-btn-submit" type="primary" style="width: 100%;" @click="dataFormSubmit()" :loading="isHttpLoading">登录</el-button>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { SystemController } from '@/api';
import { mapMutations } from 'vuex';
import projectConfig from '@/core/config';
import { encrypt } from '@/utils';

export default {
  data () {
    return {
      dataForm: {
        mobilePhone: 'admin',
        password: '123456'
      },
      dataRule: {
        mobilePhone: [
          { required: true, message: '帐号不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' }
        ]
      },
      projectName: projectConfig.projectName
    };
  },
  methods: {
    dataFormSubmit () {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          let params = {
            loginName: this.dataForm.mobilePhone,
            password: encrypt(this.dataForm.password)
          };

          SystemController.login(this, params, null, {showMask: false}).then(data => {
            this.setMenuList(data.data.menuList);
            delete data.data.menuList;

            this.setUserInfo(data.data);
            window.sessionStorage.setItem('token', data.data.tokenData);
            this.setCurrentMenuId(null);
            this.$router.replace({ name: 'main' });
          }).catch(e => {});
        }
      });
    },
    ...mapMutations(['setUserInfo', 'setMenuList', 'setCurrentMenuId'])
  },
  created () {
    this.setMenuList([]);
    this.setUserInfo({});
    window.sessionStorage.removeItem('token');
  }
};
</script>

<style lang="scss">
.login-form {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  height: 100vh;
  background: url(~@/assets/img/login_bg.jpg) no-repeat 50%, #000;
  background-size: 100%;

  .title {
    margin: 0px;
    margin-bottom: 20px;
    color: #707070;
    font-size: 24;
    text-align: center;
    display: block;
  }
}
</style>
