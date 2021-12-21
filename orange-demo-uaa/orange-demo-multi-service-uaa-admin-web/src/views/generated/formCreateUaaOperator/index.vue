<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formCreateUaaOperator" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="120px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="登录名称" prop="SysUaaOperator.loginName">
            <el-input class="input-item" v-model="formData.SysUaaOperator.loginName"
              :clearable="true" placeholder="登录名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="操作员昵称" prop="SysUaaOperator.showName">
            <el-input class="input-item" v-model="formData.SysUaaOperator.showName"
              :clearable="true" placeholder="操作员昵称" />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="!isEdit">
          <el-form-item label="操作员密码" prop="SysUaaOperator.password">
            <el-input class="input-item" v-model="formData.SysUaaOperator.password"
              :clearable="true" placeholder="密码" />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="!isEdit">
          <el-form-item label="再次输入密码" prop="SysUaaOperator.repeatPassword">
            <el-input class="input-item" v-model="formData.SysUaaOperator.repeatPassword"
              :clearable="true" placeholder="再次输入密码" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="操作员头像" prop="SysUaaOperator.headImageUrl">
            <el-upload class="upload-image-item" name="uploadFile" :headers="getUploadHeaders"
              :action="getUploadActionUrl('/admin/uaaadmin/sysUaaOperator/upload')"
              :data="{fieldName: 'headImageUrl', asImage: true}"
              :on-success="onHeadImageUrlUploadSuccess"
              :on-remove="onHeadImageUrlRemoveFile"
              :before-upload="pictureFile"
              :on-error="onUploadError"
              :show-file-list="false">
              <div v-if="formCreateUaaOperator.headImageUrl.impl.fileList[0] != null" style="position: relative">
                <img class="upload-image-show"
                  :src="formCreateUaaOperator.headImageUrl.impl.fileList[0].url" />
                <div class="upload-img-del el-icon-close"
                  @click.stop="() => {formCreateUaaOperator.headImageUrl.impl.fileList = []}" />
              </div>
              <i v-else class="el-icon-plus upload-image-item"></i>
            </el-upload>
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
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SysUaaOperatorController, DictionaryController } from '@/api';

export default {
  name: 'formCreateUaaOperator',
  props: {
    operatorId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        SysUaaOperator: {
          operatorId: undefined,
          loginName: undefined,
          password: undefined,
          repeatPassword: undefined,
          showName: undefined,
          operatorType: this.UserType.NORMAL,
          headImageUrl: undefined,
          deletedFlag: undefined,
          isDatasourceInit: false
        }
      },
      rules: {
        'SysUaaOperator.loginName': [
          {required: true, message: '请输入登录名称', trigger: 'blur'}
        ],
        'SysUaaOperator.showName': [
          {required: true, message: '请输入用户昵称', trigger: 'blur'}
        ],
        'SysUaaOperator.password': [
          {required: true, message: '请输入用户密码', trigger: 'blur'}
        ],
        'SysUaaOperator.repeatPassword': [
          {required: true, message: '请输入再次输入密码', trigger: 'blur'}
        ]
      },
      formCreateUaaOperator: {
        formFilter: {
        },
        formFilterCopy: {
        },
        headImageUrl: {
          impl: new UploadWidget(1)
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
     * 更新新建操作员
     */
    refreshFormCreateUaaOperator (reloadData = false) {
      this.loadSysUaaOperatorData().then(res => {
        if (!this.formCreateUaaOperator.isInit) {
          // 初始化下拉数据
        }
        this.formCreateUaaOperator.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onUpdateClick () {
      this.$refs.formCreateUaaOperator.validate((valid) => {
        if (!valid) return;
        if (!this.isEdit && this.formData.SysUaaOperator.password !== this.formData.SysUaaOperator.repeatPassword) {
          this.$message.error('两次密码输入不一致，请核对！');
          return;
        }
        let params = {
          sysUaaOperator: {
            operatorId: this.operatorId,
            loginName: this.formData.SysUaaOperator.loginName,
            password: this.isEdit ? undefined : this.formData.SysUaaOperator.password,
            showName: this.formData.SysUaaOperator.showName,
            operatorType: this.formData.SysUaaOperator.operatorType,
            headImageUrl: this.formData.SysUaaOperator.headImageUrl
          }
        };

        let httpCall = this.isEdit ? SysUaaOperatorController.update(this, params) : SysUaaOperatorController.add(this, params)
        httpCall.then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 获取操作员详细信息
     */
    loadSysUaaOperatorData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.SysUaaOperator.isDatasourceInit && this.isEdit) {
          let params = {
            operatorId: this.operatorId
          };
          SysUaaOperatorController.view(this, params).then(res => {
            this.formData.SysUaaOperator = {...res.data, repeatPassword: res.password, isDatasourceInit: true};
            
            let headImageUrlDownloadParams = {
              operatorId: this.formData.SysUaaOperator.operatorId,
              fieldName: 'headImageUrl',
              asImage: true
            }
            this.formCreateUaaOperator.headImageUrl.impl.fileList = this.parseUploadData(this.formData.SysUaaOperator.headImageUrl, headImageUrlDownloadParams);
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
    /**
     * 用户头像上传成功
     */
    onHeadImageUrlUploadSuccess (response, file, fileList) {
      if (response.success) {
        file.downloadUri = response.data.downloadUri;
        file.filename = response.data.filename;
        file.url = URL.createObjectURL(file.raw);
        this.formCreateUaaOperator.headImageUrl.impl.onFileChange(file, fileList);
        this.formData.SysUaaOperator.headImageUrl = this.fileListToJson(this.formCreateUaaOperator.headImageUrl.impl.fileList);
      } else {
        this.$message.error(response.message);
      }
    },
    /**
     * 移除用户头像
     */
    onHeadImageUrlRemoveFile (file, fileList) {
      this.formCreateUaaOperator.headImageUrl.impl.onFileChange(file, fileList);
      this.formData.SysUaaOperator.headImageUrl = this.fileListToJson(this.formCreateUaaOperator.headImageUrl.impl.fileList);
    },
    onUploadError (e, file, fileList) {
      this.$message.error('头像上传失败');
    },
    onUploadLimit (files, fileList) {
      this.$message.error('已经超出最大上传个数限制');
    },
    formInit () {
      this.refreshFormCreateUaaOperator();
    }
  },
  computed: {
    isEdit () {
      return this.operatorId != null && this.operatorId !== '';
    }
  },
  created () {
    this.formInit();
  }
}
</script>
