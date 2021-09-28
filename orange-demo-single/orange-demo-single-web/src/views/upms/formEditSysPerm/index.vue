<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formCreatePerm" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="权限名称" prop="SysPerm.permName">
            <el-input class="input-item" v-model="formData.SysPerm.permName"
              :clearable="true" placeholder="权限名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="所属模块" prop="SysPerm.moduleId">
            <el-cascader class="input-item" :options="permModuleList" v-model="formData.SysPerm.moduleId"
              placeholder="选择所属模块" :clearable="true" size="mini" :props="{value: 'moduleId', label: 'moduleName'}" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="关联的url" prop="SysPerm.url">
            <el-input class="input-item" v-model="formData.SysPerm.url"
              :clearable="true" placeholder="关联的url" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="显示顺序" prop="SysPerm.showOrder">
            <el-input-number class="input-item" v-model="formData.SysPerm.showOrder"
              :clearable="true" controls-position="right" placeholder="权限在当前模块下的顺序" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" @click="onAddClick()"
              :disabled="!(checkPermCodeExist('formSysPerm:fragmentSysPerm:updatePerm') || checkPermCodeExist('formSysPerm:fragmentSysPerm:addPerm'))">
              保存
            </el-button>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { findTreeNodePath } from '@/utils';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SystemController, DictionaryController } from '@/api';

export default {
  name: 'formEditSysPerm',
  props: {
    permId: {
      default: undefined
    },
    moduleId: {
      default: undefined
    },
    rowData: {
      type: Object
    },
    currentPermGroupId: String,
    permModuleList: {
      type: Array,
      default: () => {
        return [];
      }
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachedPageChildMixin],
  data () {
    return {
      formData: {
        SysPerm: {
          permId: undefined,
          moduleId: this.currentPermGroupId,
          permName: undefined,
          url: undefined,
          showOrder: undefined,
          deletedFlag: undefined
        }
      },
      rules: {
        'SysPerm.permName': [
          {required: true, message: '请输入权限名称', trigger: 'blur'}
        ],
        'SysPerm.url': [
          {required: true, message: '请输入关联的url', trigger: 'blur'}
        ],
        'SysPerm.showOrder': [
          {required: true, message: '请输入显示顺序', trigger: 'blur'}
        ]
      },
      formCreatePerm: {
        formFilter: {
        },
        formFilterCopy: {
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
     * 更新新建权限
     */
    refreshFormCreatePerm (reloadData = false) {
      if (!this.formCreatePerm.isInit) {
        this.formData.SysPerm.moduleId = findTreeNodePath(this.permModuleList, this.formData.SysPerm.moduleId, 'moduleId');
      }
      this.formCreatePerm.isInit = true;
    },
    /**
     * 新增
     */
    onAddClick () {
      this.$refs.formCreatePerm.validate((valid) => {
        if (!valid) return;
        let params = {
          sysPermDto: {
            permId: this.permId,
            showOrder: this.formData.SysPerm.showOrder,
            moduleId: Array.isArray(this.formData.SysPerm.moduleId) ? this.formData.SysPerm.moduleId[this.formData.SysPerm.moduleId.length - 1] : undefined,
            url: this.formData.SysPerm.url,
            permName: this.formData.SysPerm.permName
          }
        };

        if (this.isEdit) {
          SystemController.updatePerm(this, params).then(res => {
            this.$message.success('编辑成功');
            this.onCancel(true);
          }).catch(e => {});
        } else {
          SystemController.addPerm(this, params).then(res => {
            this.$message.success('新增成功');
            this.onCancel(true);
          }).catch(e => {});
        }
      });
    },
    initFormData () {
      if (this.rowData != null) this.formData.SysPerm = {...this.formData.SysPerm, ...this.rowData};
    },
    formInit () {
      this.initFormData();
      this.refreshFormCreatePerm();
    }
  },
  computed: {
    isEdit () {
      return this.permId != null;
    }
  },
  created () {
    this.formInit();
  }
}
</script>
