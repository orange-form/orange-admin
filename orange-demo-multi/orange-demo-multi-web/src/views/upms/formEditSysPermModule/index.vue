<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formCreatePermModule" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="120px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="模块名称" prop="SysPermModule.moduleName">
            <el-input class="input-item" v-model="formData.SysPermModule.moduleName"
              :clearable="true" placeholder="权限模块名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="模块类型">
            <el-select class="input-item" v-model="formData.SysPermModule.moduleType"
              placeholder="模块类型" :clearable="true" size="mini" :disabled="isEdit">
              <el-option v-for="item in SysPermModuleType.getList()" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="所属模块">
            <el-cascader class="input-item" :options="getPermGroupTree" v-model="formData.SysPermModule.parentId"
              placeholder="选择所属模块" :clearable="true" size="mini"
              :props="{value: 'moduleId', label: 'moduleName', checkStrictly: true}" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="显示顺序" prop="SysPermModule.showOrder">
            <el-input-number class="input-item" v-model="formData.SysPermModule.showOrder"
              :clearable="true" controls-position="right" placeholder="权限模块在当前层级下的顺序" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true" @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" @click="onAddClick()"
              :disabled="!(checkPermCodeExist('formSysPerm:fragmentSysPerm:addPermModule') || checkPermCodeExist('formSysPerm:fragmentSysPerm:updatePermModule'))">
              保存
            </el-button>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { treeDataTranslate, findTreeNodePath } from '@/utils';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { DictionaryController, SystemController } from '@/api';

export default {
  name: 'formEditSysPermModule',
  props: {
    parentId: {
      default: undefined
    },
    moduleType: {
      default: undefined
    },
    moduleId: {
      default: undefined
    },
    rowData: {
      type: Object,
      default: undefined
    },
    moduleList: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachedPageChildMixin],
  data () {
    return {
      formData: {
        SysPermModule: {
          moduleId: undefined,
          parentId: undefined,
          moduleName: undefined,
          moduleType: undefined,
          level: undefined,
          showOrder: undefined
        }
      },
      rules: {
        'SysPermModule.moduleName': [
          {required: true, message: '请输入模块名称', trigger: 'blur'}
        ],
        'SysPermModule.showOrder': [
          {required: true, message: '请输入显示顺序', trigger: 'blur'}
        ]
      },
      formCreatePermModule: {
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
     * 更新新建权限模块
     */
    refreshFormCreatePermModule (reloadData = false) {
      if (!this.formCreatePermModule.isInit) {
        this.formData.SysPermModule.parentId = findTreeNodePath(this.getPermGroupTree, this.formData.SysPermModule.parentId, 'moduleId')
      }
      this.formCreatePermModule.isInit = true;
    },
    /**
     * 新增
     */
    onAddClick () {
      this.$refs.formCreatePermModule.validate((valid) => {
        if (!valid) return;
        let params = {
          sysPermModuleDto: {
            moduleId: this.moduleId,
            moduleName: this.formData.SysPermModule.moduleName,
            showOrder: this.formData.SysPermModule.showOrder,
            moduleType: this.formData.SysPermModule.moduleType,
            parentId: Array.isArray(this.formData.SysPermModule.parentId) ? this.formData.SysPermModule.parentId[this.formData.SysPermModule.parentId.length - 1] : undefined
          }
        };

        if (this.isEdit) {
          SystemController.updatePermGroup(this, params).then(res => {
            this.$message.success('编辑成功');
            this.onCancel(true);
          }).catch(e => {});
        } else {
          SystemController.addPermGroup(this, params).then(res => {
            this.$message.success('新增成功');
            this.onCancel(true);
          }).catch(e => {});
        }
      });
    },
    initFormData () {
      if (this.rowData != null) {
        this.formData.SysPermModule = {...this.formData.SysPermModule, ...this.rowData};
      }
      if (this.parentId != null) this.formData.SysPermModule.parentId = this.parentId;
      if (this.moduleType != null) this.formData.SysPermModule.moduleType = this.moduleType;
    },
    formInit () {
      this.initFormData();
      this.refreshFormCreatePermModule();
    }
  },
  computed: {
    isEdit () {
      return this.moduleId != null;
    },
    getPermGroupTree () {
      let tempList = this.moduleList.map((item) => {
        if (item.moduleType === this.SysPermModuleType.GROUP) {
          return {...item, children: undefined};
        }
      }).filter((item) => {
        return item != null;
      });
      return treeDataTranslate(tempList, 'moduleId');
    }
  },
  mounted () {
    this.formInit();
  }
}
</script>
