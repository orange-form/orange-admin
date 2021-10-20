<template>
  <div class="panel-tab__content">
    <el-form ref="form" size="mini" label-width="80px" :model="formData" :rules="rules" @submit.native.prevent>
      <el-form-item label="表单路由" prop="routerName"
        v-if="flowEntry().bindFormType === SysFlowEntryBindFormType.ROUTER_FORM">
        <el-input v-model="formData.routerName" clearable @change="updateElementFormKey" />
      </el-form-item>
      <el-form-item label="在线表单" prop="formId"
        v-if="flowEntry().bindFormType === SysFlowEntryBindFormType.ONLINE_FORM">
        <el-select v-model="formData.formId" clearable placeholder="" @change="updateElementFormKey">
          <el-option v-for="item in formList().filter(item => item.formType === SysOnlineFormType.FLOW)" :key="item.formId" :label="item.formName" :value="item.formId" />
        </el-select>
      </el-form-item>
      <el-form-item label="允许编辑">
        <el-switch v-model="formData.editable" @change="updateElementFormKey" />
      </el-form-item>
      <el-row>
        <el-col :span="24">
          <el-divider>按钮设置</el-divider>
        </el-col>
        <el-col :span="24" style="border-top: 1px solid #EBEEF5;">
          <el-table :data="formOperationList" :show-header="false">
            <el-table-column label="操作" width="45px">
              <template slot-scope="scope">
                <el-button class="table-btn delete" type="text" icon="el-icon-remove-outline"
                  @click="onDeleteOperation(scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="按钮名称" min-width="100px">
              <template slot-scope="scope">
                <el-button class="table-btn" type="text" style="text-decoration: underline;"
                  @click="onEditOperation(scope.row)">
                  {{scope.row.label}}
                </el-button>
              </template>
            </el-table-column>
            <el-table-column label="按钮类型" min-width="100px">
              <template slot-scope="scope">
                <el-tag size="mini" effect="dark">{{SysFlowTaskOperationType.getValue(scope.row.type)}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="显示顺序" prop="showOrder" width="60px" />
          </el-table>
          <el-button class="full-line-btn" icon="el-icon-plus" @click="onEditOperation(null)">添加按钮</el-button>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import EditOperation from './formEditOperation.vue';

export default {
  name: 'flowFormConfig',
  props: {
    id: String,
    type: String
  },
  inject: ['flowEntry', 'formList', 'prefix'],
  data () {
    return {
      formData: {
        formId: this.flowEntry().defaultFormId,
        routerName: this.flowEntry().defaultRouterName,
        editable: false
      },
      operationList: undefined,
      formOperationList: [],
      rules: {
        formId: [
          {required: true, message: '请选择在线表单！', trigger: 'blur'}
        ],
        routerName: [
          {required: true, message: '表单路由不能为空！', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    resetFormList () {
      this.bpmnELement = window.bpmnInstances.bpmnElement;
      let formData = this.bpmnELement.businessObject.formKey;
      let formObj = formData ? JSON.parse(formData) : undefined;
      if (formObj) {
        this.formData = {
          formId: formObj.formId,
          routerName: formObj.routerName,
          editable: !formObj.readOnly,
          groupType: formObj.groupType || 'DEPT'
        }
      }
      let elExtensionElements = this.bpmnELement.businessObject.get("extensionElements") || window.bpmnInstances.moddle.create("bpmn:ExtensionElements", { values: [] });
      this.operationList = elExtensionElements.values.filter(ex => ex.$type === `${this.prefix}:OperationList`)?.[0] ||
        window.bpmnInstances.moddle.create(`${this.prefix}:OperationList`, { operationList: [] });
      this.formOperationList = JSON.parse(JSON.stringify(this.operationList.operationList || []));
      this.formOperationList.forEach(item => {
        item.showOrder = Number.parseInt(item.showOrder);
      });
      // 更新元素扩展属性，避免后续报错
      this.updateElementFormKey();
      this.updateElementExtensions();
    },
    updateElementFormKey () {
      this.$refs.form.validate(valid => {
        if (!valid) return;
        let formKeyString = JSON.stringify({
          formId: this.flowEntry().bindFormType === this.SysFlowEntryBindFormType.ONLINE_FORM ? this.formData.formId : undefined,
          routerName: this.flowEntry().bindFormType === this.SysFlowEntryBindFormType.ONLINE_FORM ? undefined : this.formData.routerName,
          readOnly: !this.formData.editable,
          groupType: this.formData.groupType || 'DEPT'
        });
        window.bpmnInstances.modeling.updateProperties(this.bpmnELement, { formKey: formKeyString });
      });
    },
    updateElementExtensions () {
      // 更新回扩展元素
      let elExtensionElements = this.bpmnELement.businessObject.get("extensionElements")  || window.bpmnInstances.moddle.create("bpmn:ExtensionElements", { values: [] });
      let otherExtensions = elExtensionElements.values.filter(ex => ex.$type !== `${this.prefix}:OperationList`);
      const newElExtensionElements = window.bpmnInstances.moddle.create(`bpmn:ExtensionElements`, {
        values: otherExtensions.concat(this.operationList)
      });
      this.operationList.operationList = this.formOperationList.map(item => {
        return window.bpmnInstances.moddle.create(`${this.prefix}:FormOperation`, {
          id: item.id,
          label: item.label,
          type: item.type,
          showOrder: item.showOrder
        });
      });
      // 更新到元素上
      window.bpmnInstances.modeling.updateProperties(this.bpmnELement, {
        extensionElements: newElExtensionElements
      });
    },
    onEditOperation (operation) {
      this.$dialog.show(operation ? '编辑按钮' : '添加按钮', EditOperation, {
        area: '500px'
      }, {
        rowData: operation
      }).then(res => {
        if (res.id == null) {
          res.id = new Date().getTime();
          this.formOperationList.push(res);
        } else {
          this.formOperationList.forEach(item => {
            if (item.id === res.id) {
              item.label = res.label;
              item.type = res.type;
              item.showOrder = res.showOrder;
            }
          });
        }
        this.formOperationList.sort((value1, value2) => {
          return value1.showOrder - value2.showOrder;
        });
        this.updateElementExtensions(); 
      });
    },
    onDeleteOperation (operation) {
      this.$confirm('是否删除此按钮？').then(res => {
        this.formOperationList = this.formOperationList.filter(item => {
          return item.id !== operation.id;
        });
        this.updateElementExtensions(); 
      }).catch(e => {});
    }
  },
  watch: {
    id: {
      immediate: true,
      handler (val) {
        if (val && val.length) {
          this.$nextTick(() => {
            this.resetFormList();
          });
        }
      }
    }
  }
}
</script>

<style scoped>
  .full-line-btn {
    width: 100%;
    margin-top: 10px;
    border: 1px dashed #EBEEF5;
  }
</style>
