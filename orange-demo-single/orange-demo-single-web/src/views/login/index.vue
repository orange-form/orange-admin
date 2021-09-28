<template>
  <div class="login-form">
    <div class="login-box">
      <img :src="bkImg" style="height: 100%; flex-shrink: 0" />
      <div class="login-input">
        <img :src="logoImg" />
        <span class="title">中台化低代码生成工具</span>
        <el-form :model="dataForm" :rules="dataRule" size="medium" ref="dataForm" @keyup.enter.native="dataFormSubmit()" style="width: 356px; margin-top: 4vh;">
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
          <el-button class="login-btn-submit" type="warning" style="width: 100%;"
            @click="dataFormSubmit()"
            :loading="isHttpLoading">
            登录
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { SystemController } from '@/api';
import { mapMutations } from 'vuex';
import projectConfig from '@/core/config';
import { encrypt, setToken } from '@/utils';

export default {
  data () {
    return {
      bkImg: require('@/assets/img/login.png'),
      logoImg: require('@/assets/img/login_logo.png'),
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
            setToken(data.data.tokenData);
            this.setCurrentMenuId(null);
            this.$router.replace({ name: 'main' });
          }).catch(e => {});
        }
      });
    },
    ...mapMutations(['setUserInfo', 'setMenuList', 'setCurrentMenuId'])
  },
  mounted () {
    this.setMenuList([]);
    this.setUserInfo({});
    setToken();
  }
};
</script>

<style lang="scss">
.login-form {
  width: 100vw;
  height: 100vh;
  background: #292D36;
  display: flex;
  justify-content: center;
  align-items: center;
  .login-box {
    display: flex;
    align-items: center;
    height: 70vh;
    padding: 7vh;
    border-radius: 3px;
    background: white;
    .login-input {
      height: 100%;
      width: 420px;
      display: flex;
      align-items: center;
      flex-direction: column;
      img {
        width: 356px;
        height: 42px;
        margin-top: 6vh;
      }
      .title {
        color: #232323;
        font-size: 20px;
        margin-top: 2vh;
      }
    }
  }
}

.login-form .el-input__inner {
  height: 45px!important;
}

.login-form .el-button {
  height: 45px!important;
  font-size: 20px;
}
</style>
