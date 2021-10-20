<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formEditFlowCategory" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="分类名称" prop="FlowCategory.name">
            <el-input class="input-item" v-model="formData.FlowCategory.name"
              :clearable="true" placeholder="流程分类名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="分类编码" prop="FlowCategory.code">
            <el-input class="input-item" v-model="formData.FlowCategory.code"
              :disabled="isEdit" :clearable="true" placeholder="分类编码" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="显示顺序" prop="FlowCategory.showOrder">
            <el-input-number class="input-item" v-model="formData.FlowCategory.showOrder"
              :clearable="true" controls-position="right" placeholder="显示顺序" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" @click="onSubmitClick()">
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
import { FlowCategoryController } from '@/api/flowController.js';

export default {
  name: 'formEditFlowCategory',
  props: {
    categoryId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        FlowCategory: {
          categoryId: undefined,
          name: undefined,
          code: undefined,
          showOrder: undefined,
          isDatasourceInit: false
        }
      },
      rules: {
        'FlowCategory.code': [
          {required: true, message: '请输入分类编码', trigger: 'blur'},
          {type: 'string', pattern: /^[A-Za-z0-9]+$/, message: '分类编码只能输入英文字母和数字', trigger: 'blur'}
        ],
        'FlowCategory.showOrder': [
          {required: true, message: '请输入显示顺序', trigger: 'blur'},
          {type: 'integer', message: '显示顺序只允许输入整数', trigger: 'blur', transform: (value) => Number(value)},
          {type: 'number', min: 0, message: '显示顺序必须大于0', trigger: 'blur', transform: (value) => Number(value)}
        ],
        'FlowCategory.name': [
          {required: true, message: '请输入流程分类名称', trigger: 'blur'}
        ]
      },
      formEditFlowCategory: {
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
     * 更新编辑流程分类
     */
    refreshFormEditFlowCategory (reloadData = false) {
      this.loadFlowCategoryData().then(res => {
        if (!this.formEditFlowCategory.isInit) {
          // 初始化下拉数据
        }
        this.formEditFlowCategory.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onSubmitClick () {
      this.$refs.formEditFlowCategory.validate((valid) => {
        if (!valid) return;
        if (
          this.formData.FlowCategory.name == null ||
          this.formData.FlowCategory.code == null ||
          this.formData.FlowCategory.showOrder == null
        ) {
          this.$message.error('请求失败，发现必填参数为空！');
          return;
        }
        let params = {
          flowCategoryDto: {
            categoryId: this.isEdit ? this.categoryId : undefined,
            name: this.formData.FlowCategory.name,
            code: this.formData.FlowCategory.code,
            showOrder: this.formData.FlowCategory.showOrder
          }
        };

        let httpCall = this.isEdit ? FlowCategoryController.update(this, params) : FlowCategoryController.add(this, params);
        httpCall.then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 获取FlowCategory详细信息
     */
    loadFlowCategoryData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.FlowCategory.isDatasourceInit && this.isEdit) {
          if (
            this.categoryId == null
          ) {
            this.resetFormData();
            reject();
            return;
          }
          let params = {
            categoryId: this.categoryId
          };
          FlowCategoryController.view(this, params).then(res => {
            this.formData.FlowCategory = {...res.data, isDatasourceInit: true};
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
      this.$refs.formEditFlowCategory.resetFields();
      /*
      this.formData = {
        FlowCategory: {
          categoryId: undefined,
          parentId: undefined,
          name: undefined,
          code: undefined,
          showOrder: undefined,
          updateTime: undefined,
          updateUserId: undefined,
          createTime: undefined,
          createUserId: undefined,
          isDatasourceInit: false
        }
      }
      */
    },
    formInit () {
      this.refreshFormEditFlowCategory();
    }
  },
  computed: {
    isEdit () {
      return this.categoryId != null && this.categoryId !== '';
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
