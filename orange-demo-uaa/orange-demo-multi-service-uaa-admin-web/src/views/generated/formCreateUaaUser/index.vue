<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formCreateUaaUser" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="120px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="登录名称" prop="SysUaaUser.username">
            <el-input class="input-item" v-model="formData.SysUaaUser.username"
              :clearable="true" placeholder="登录名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="用户昵称" prop="SysUaaUser.showName">
            <el-input class="input-item" v-model="formData.SysUaaUser.showName"
              :clearable="true" placeholder="用户昵称" />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="!isEdit">
          <el-form-item label="用户密码" prop="SysUaaUser.password">
            <el-input class="input-item" v-model="formData.SysUaaUser.password"
              :clearable="true" placeholder="密码" />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="!isEdit">
          <el-form-item label="再次输入密码" prop="SysUaaUser.repeatPassword">
            <el-input class="input-item" v-model="formData.SysUaaUser.repeatPassword"
              :clearable="true" placeholder="密码" />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="isEdit">
          <el-form-item label="用户状态" prop="SysUaaUser.locked">
            <el-select class="input-item" v-model="formData.SysUaaUser.locked" :clearable="true" filterable
              placeholder="用户状态" :loading="formCreateUaaUser.locked.impl.loading"
              @visible-change="formCreateUaaUser.locked.impl.onVisibleChange"
              @change="onLockedValueChange">
              <el-option v-for="item in formCreateUaaUser.locked.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
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
import { SysUaaUserController, DictionaryController } from '@/api';
import { encrypt } from '@/utils';

export default {
  name: 'formCreateUaaUser',
  props: {
    userId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        SysUaaUser: {
          userId: undefined,
          username: undefined,
          password: undefined,
          repeatPassword: undefined,
          showName: undefined,
          locked: this.UserStatus.USED,
          isDatasourceInit: false
        }
      },
      rules: {
        'SysUaaUser.username': [
          {required: true, message: '请输入登录名称', trigger: 'blur'}
        ],
        'SysUaaUser.password': [
          {required: true, message: '请输入密码', trigger: 'blur'}
        ],
        'SysUaaUser.repeatPassword': [
          {required: true, message: '请输入再次输入密码', trigger: 'blur'}
        ],
        'SysUaaUser.showName': [
          {required: true, message: '请输入用户昵称', trigger: 'blur'}
        ],
        'SysUaaUser.locked': [
          {required: true, message: '请输入用户状态', trigger: 'blur'}
        ]
      },
      formCreateUaaUser: {
        formFilter: {
        },
        formFilterCopy: {
        },
        locked: {
          impl: new DropdownWidget(this.loadLockedDropdownList)
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
     * 用户状态下拉数据获取函数
     */
    loadLockedDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictUserStatus(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 用户状态选中值改变
     */
    onLockedValueChange (value) {
    },
    /**
     * 更新新建UAA用户
     */
    refreshFormCreateUaaUser (reloadData = false) {
      this.loadSysUaaUserData().then(res => {
        if (!this.formCreateUaaUser.isInit) {
          // 初始化下拉数据
        }
        this.formCreateUaaUser.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onUpdateClick () {
      this.$refs.formCreateUaaUser.validate((valid) => {
        if (!valid) return;
        if (!this.isEdit && this.formData.SysUaaUser.password !== this.formData.SysUaaUser.repeatPassword) {
          this.$message.error('两次密码输入不一致，请核对！');
          return;
        }
        let params = {
          sysUaaUser: {
            userId: this.userId,
            username: this.formData.SysUaaUser.username,
            password: this.isEdit ? undefined : encrypt(this.formData.SysUaaUser.password),
            showName: this.formData.SysUaaUser.showName,
            locked: this.formData.SysUaaUser.locked
          }
        };

        let httpCall = this.isEdit ? SysUaaUserController.update(this, params) : SysUaaUserController.add(this, params);
        httpCall.then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 获取UAA用户详细信息
     */
    loadSysUaaUserData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.SysUaaUser.isDatasourceInit && this.isEdit) {
          let params = {
            userId: this.userId
          };
          SysUaaUserController.view(this, params).then(res => {
            this.formData.SysUaaUser = {...res.data, repeatPassword: res.password, isDatasourceInit: true};
            if (this.formData.SysUaaUser.lockedDictMap) this.formCreateUaaUser.locked.impl.dropdownList = [this.formData.SysUaaUser.lockedDictMap];
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
      this.formCreateUaaUser.locked.impl.onVisibleChange(true);
    },
    formInit () {
      this.refreshFormCreateUaaUser();
    }
  },
  computed: {
    isEdit () {
      return this.userId != null && this.userId !== '';
    }
  },
  created () {
    this.formInit();
  }
}
</script>
