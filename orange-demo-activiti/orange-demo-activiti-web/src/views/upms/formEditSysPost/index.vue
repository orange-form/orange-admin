<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="fragmentEditPost" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="岗位名称" prop="SysPost.postName">
            <el-input class="input-item" v-model="formData.SysPost.postName"
              :clearable="true" placeholder="岗位名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="岗位层级" prop="SysPost.level">
            <el-input-number class="input-item" v-model="formData.SysPost.level"
              :clearable="true" controls-position="right" placeholder="岗位层级" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="领导岗位">
            <el-switch class="input-item" v-model="formData.SysPost.leaderPost" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini"
              :disabled="!(checkPermCodeExist('formSysPost:fragmentSysPost:add') || checkPermCodeExist('formSysPost:fragmentSysPost:update'))"
              @click="onSaveClick()">
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
import rules from '@/utils/validate.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SysPostController, DictionaryController } from '@/api';

export default {
  name: 'formEditSysPost',
  props: {
    postId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        SysPost: {
          postId: undefined,
          postName: undefined,
          level: undefined,
          leaderPost: false,
          isDatasourceInit: false
        }
      },
      rules: {
        'SysPost.postName': [
          {required: true, message: '请输入岗位名称', trigger: 'blur'}
        ],
        'SysPost.level': [
          {required: true, message: '请输入岗位层级', trigger: 'blur'}
        ]
      },
      fragmentEditPost: {
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
     * 更新编辑岗位
     */
    refreshFragmentEditPost (reloadData = false) {
      this.loadSysPostData().then(res => {
        if (!this.fragmentEditPost.isInit) {
          // 初始化下拉数据
        }
        this.fragmentEditPost.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onSaveClick () {
      this.$refs.fragmentEditPost.validate((valid) => {
        if (!valid) return;
        if (
          this.formData.SysPost.postName == null ||
          this.formData.SysPost.level == null
        ) {
          this.$message.error('请求失败，发现必填参数为空！');
          return;
        }
        let params = {
          sysPostDto: {
            postId: this.postId,
            postName: this.formData.SysPost.postName,
            level: this.formData.SysPost.level,
            leaderPost: this.formData.SysPost.leaderPost
          }
        };

        let httpCall = this.isEdit ? SysPostController.update(this, params) : SysPostController.add(this, params);
        httpCall.then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 获取岗位管理详细信息
     */
    loadSysPostData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.SysPost.isDatasourceInit && this.isEdit) {
          if (
            this.postId == null
          ) {
            this.resetFormData();
            reject();
            return;
          }
          let params = {
            postId: this.postId
          };
          SysPostController.view(this, params).then(res => {
            this.formData.SysPost = {...res.data, isDatasourceInit: true};
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
     * 重置表单数据
     */
    resetFormData () {
      this.formData = {
        SysPost: {
          postId: undefined,
          postName: undefined,
          level: undefined,
          leaderPost: undefined,
          createUserId: undefined,
          createTime: undefined,
          updateUserId: undefined,
          updateTime: undefined,
          deletedFlag: undefined,
          isDatasourceInit: false
        }
      }
    },
    formInit () {
      this.refreshFragmentEditPost();
    }
  },
  computed: {
    isEdit () {
      return this.postId != null;
    }
  },
  mounted () {
    // 初始化页面数据
    this.formInit();
  },
  watch: {
  }
}
</script>
