<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formEditSysDataPerm" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="120px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="数据权限名称" prop="SysDataPerm.dataPermName">
            <el-input class="input-item" v-model="formData.SysDataPerm.dataPermName"
              :clearable="true" placeholder="显示名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="过滤规则" prop="SysDataPerm.ruleType">
            <el-select class="input-item" v-model="formData.SysDataPerm.ruleType" :clearable="true"
              placeholder="过滤规则" :loading="formEditSysDataPerm.ruleType.impl.loading"
              @visible-change="onRuleTypeVisibleChange"
              @change="onRuleTypeValueChange">
              <el-option v-for="item in formEditSysDataPerm.ruleType.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-card shadow="never">
            <div slot="header" class="card-header">
              <span>部门列表</span>
              <el-input size="mini" v-model="deptNameFilter" placeholder="输入部门名称过滤" style="width: 250px;" clearable suffix-icon="el-icon-search" />
            </div>
            <el-scrollbar style="height: 250px;" wrap-class="scrollbar_dropdown__wrap">
              <el-tree ref="deptTree" :data="deptTree" show-checkbox node-key="id" default-expand-all
                :check-strictly="true" :props="{...deptProps, disabled: () => {return (formData.SysDataPerm.ruleType !== SysDataPermType.CUSTOM_DEPT_AND_CHILD && formData.SysDataPerm.ruleType !== SysDataPermType.CUSTOM_DEPT)}}" :filter-node-method="filterDeptNode" />
            </el-scrollbar>
          </el-card>
        </el-col>
      </el-row>
      <el-col :span="24" style="margin-top: 20px;">
        <el-row type="flex" justify="end">
          <el-button type="primary" size="mini" :plain="true" @click="onCancel(false)">
            取消
          </el-button>
          <el-button type="primary" size="mini" @click="onUpdateClick()"
            :disabled="!(checkPermCodeExist('formSysDataPerm:fragmentSysDataPerm:add') || checkPermCodeExist('formSysDataPerm:fragmentSysDataPerm:update'))">
            保存
          </el-button>
        </el-row>
      </el-col>
    </el-form>
  </div>
</template>

<script>
import { treeDataTranslate } from '@/utils';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SystemController, DictionaryController, SysDataPermController } from '@/api';

export default {
  name: 'formEditSysDataPerm',
  props: {
    dataPermId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachedPageChildMixin],
  data () {
    return {
      deptTree: [],
      deptNameFilter: undefined,
      deptProps: {
        label: 'name'
      },
      formData: {
        SysDataPerm: {
          dataPermId: undefined,
          dataPermName: undefined,
          ruleType: undefined,
          deptIdListString: undefined,
          createUserId: undefined,
          createUsername: undefined
        }
      },
      rules: {
        'SysDataPerm.dataPermName': [
          {required: true, message: '请输入数据权限名称', trigger: 'blur'}
        ],
        'SysDataPerm.ruleType': [
          {required: true, message: '请选择过滤规则', trigger: 'blur'}
        ]
      },
      formEditSysDataPerm: {
        formFilter: {
        },
        formFilterCopy: {
        },
        ruleType: {
          impl: new DropdownWidget(this.loadRuleTypeDropdownList)
        },
        isInit: false
      }
    }
  },
  methods: {
    filterDeptNode (value, data) {
      if (!value) return true;
      return data.deptName ? data.deptName.indexOf(value) !== -1 : true;
    },
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    /**
     * 过滤规则下拉数据获取函数
     */
    loadRuleTypeDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSysDataPermType(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 过滤规则下拉框显隐
     */
    onRuleTypeVisibleChange (show) {
      this.formEditSysDataPerm.ruleType.impl.onVisibleChange(show).catch(e => {});
    },
    /**
     * 过滤规则选中值改变
     */
    onRuleTypeValueChange (value) {
    },
    /**
     * 部门列表数据获取函数
     */
    loadDeptList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSysDept(this, params).then(res => {
          this.deptTree = treeDataTranslate(res.getList(), 'id');
          if (Array.isArray(this.formData.SysDataPerm.dataPermDeptList)) {
            this.$refs.deptTree.setCheckedKeys(this.formData.SysDataPerm.dataPermDeptList.map((item) => {
              return item.deptId;
            }));
          }
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 更新编辑数据权限
     */
    refreshFormEditSysDataPerm (reloadData = false) {
      this.formEditSysDataPerm.ruleType.impl.onVisibleChange(true).catch(e => {});
      this.formEditSysDataPerm.isInit = true;
    },
    /**
     * 编辑
     */
    onUpdateClick () {
      this.$refs.formEditSysDataPerm.validate((valid) => {
        if (!valid) return;
        let params = {
          sysDataPermDto: {
            dataPermId: this.dataPermId,
            ruleType: this.formData.SysDataPerm.ruleType,
            dataPermName: this.formData.SysDataPerm.dataPermName
          }
        };
        if (this.formData.SysDataPerm.ruleType === this.SysDataPermType.CUSTOM_DEPT_AND_CHILD ||
          this.formData.SysDataPerm.ruleType === this.SysDataPermType.CUSTOM_DEPT) {
          let deptList = this.$refs.deptTree.getCheckedKeys();
          if (deptList.length <= 0) {
            this.$message.error('请选择数据权限部门');
            return;
          }
          params.deptIdListString = Array.isArray(deptList) ? deptList.join(',') : undefined;
        }
        if (this.dataPermId == null) {
          SysDataPermController.add(this, params).then(res => {
            this.$message.success('添加成功');
            this.onCancel(true);
          }).catch(e => {});
        } else {
          SysDataPermController.update(this, params).then(res => {
            this.$message.success('编辑成功');
            this.onCancel(true);
          }).catch(e => {});
        }
      });
    },
    /**
     * 获取数据权限详细信息
     */
    loadSysDataPermData () {
      return new Promise((resolve, reject) => {
        if (this.dataPermId == null) {
          resolve();
        } else {
          let params = {
            dataPermId: this.dataPermId
          };
          SysDataPermController.view(this, params).then(res => {
            this.formData.SysDataPerm = {...res.data};
            resolve();
          }).catch(e => {
            reject();
          });
        }
      });
    },
    initFormData () {
    },
    formInit () {
      let loadAllDatasource = [
        this.loadSysDataPermData()
      ];
      Promise.all(loadAllDatasource).then(res => {
        this.initFormData();
        this.refreshFormEditSysDataPerm();
        this.loadDeptList();
      }).catch(e => {});
    }
  },
  mounted () {
    this.formInit();
  },
  watch: {
    deptNameFilter (val) {
      this.$refs.deptTree.filter(val);
    }
  }
}
</script>

<style scoped>
</style>
