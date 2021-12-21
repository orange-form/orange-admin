<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formCreateAuthClient" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="150px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="应用标识" prop="AuthClientDetails.clientId">
            <el-input class="input-item" v-model="formData.AuthClientDetails.clientId"
              :disabled="isEdit" :clearable="true" placeholder="应用标识" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="应用名称" prop="AuthClientDetails.clientDesc">
            <el-input class="input-item" v-model="formData.AuthClientDetails.clientDesc"
              :clearable="true" placeholder="应用名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户端秘钥（明文）" prop="AuthClientDetails.clientSecretPlain">
            <el-input class="input-item" v-model="formData.AuthClientDetails.clientSecretPlain"
              :disabled="isEdit" :clearable="true" placeholder="客户端秘钥（明文）" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="回调地址" prop="AuthClientDetails.webServerRedirectUri">
            <el-input class="input-item" v-model="formData.AuthClientDetails.webServerRedirectUri"
              :clearable="true" placeholder="回调地址" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="TOKEN 有效期" prop="AuthClientDetails.accessTokenValidity">
            <el-input-number class="input-item" v-model="formData.AuthClientDetails.accessTokenValidity"
              :clearable="true" controls-position="right" placeholder="TOKEN 有效期" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" @click="onUpdateClick()">
              保存
            </el-button>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { AuthClientDetailsController, DictionaryController } from '@/api';

export default {
  name: 'formCreateAuthClient',
  props: {
    clientId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        AuthClientDetails: {
          clientId: undefined,
          clientSecret: undefined,
          clientSecretPlain: undefined,
          clientDesc: undefined,
          authorizedGrantTypes: undefined,
          webServerRedirectUri: undefined,
          accessTokenValidity: undefined,
          refreshTokenValidity: undefined,
          deletedFlag: undefined,
          isDatasourceInit: false
        }
      },
      rules: {
        'AuthClientDetails.clientId': [
          {required: true, message: '请输入应用标识', trigger: 'blur'}
        ],
        'AuthClientDetails.accessTokenValidity': [
          {required: true, message: '请输入TOKEN 有效期', trigger: 'blur'},
          {type: 'integer', message: 'TOKEN 有效期只允许输入整数', trigger: 'blur', transform: (value) => Number(value)},
          {type: 'number', min: 0, message: 'TOKEN 有效期必须大于0', trigger: 'blur', transform: (value) => Number(value)}
        ]
      },
      formCreateAuthClient: {
        formFilter: {
        },
        formFilterCopy: {
        },
        menuBlock: {
          isInit: false
        },
        isInit: false
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    /**
     * 更新新建应用客户端
     */
    refreshFormCreateAuthClient (reloadData = false) {
      this.loadAuthClientDetailsData().then(res => {
        if (!this.formCreateAuthClient.isInit) {
          // 初始化下拉数据
        }
        this.formCreateAuthClient.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onUpdateClick () {
      this.$refs.formCreateAuthClient.validate((valid) => {
        if (!valid) return;
        let params = {
          authClientDetails: {
            clientId: this.formData.AuthClientDetails.clientId,
            clientSecretPlain: this.formData.AuthClientDetails.clientSecretPlain,
            clientDesc: this.formData.AuthClientDetails.clientDesc,
            webServerRedirectUri: this.formData.AuthClientDetails.webServerRedirectUri,
            accessTokenValidity: this.formData.AuthClientDetails.accessTokenValidity
          }
        };

        let httpCall = this.isEdit ? AuthClientDetailsController.update(this, params) : AuthClientDetailsController.add(this, params);
        httpCall.then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 获取应用客户端详细信息
     */
    loadAuthClientDetailsData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.AuthClientDetails.isDatasourceInit && this.isEdit) {
          let params = {
            clientId: this.clientId
          };
          AuthClientDetailsController.view(this, params).then(res => {
            this.formData.AuthClientDetails = {...res.data, isDatasourceInit: true};
            resolve();
          }).catch(e => {
            reject();
          });
        } else {
          resolve();
        }
      });
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormCreateAuthClient();
    }
  },
  computed: {
    isEdit () {
      return this.clientId != null && this.clientId !== '';
    }
  },
  created () {
    this.formInit();
  }
}
</script>
