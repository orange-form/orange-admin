<template>
  <div style="position: relative;" :style="getFlowFormStyle">
    <OnlineQueryForm ref="form" v-if="Number.parseInt(formType) === SysOnlineFormType.QUERY"
      :operationType="operationType"
      :formId="formId"
      :params="params"
      :key="formKey"
      @ready="onFormReady"
    />
    <OnlineEditForm ref="form" v-if="Number.parseInt(formType) === SysOnlineFormType.FORM"
      :saveOnClose="saveOnClose"
      :formId="formId"
      :key="formKey"
      :params="params"
      :operationType="operationType"
      :rowData="rowData"
      :flowData="flowData"
      @close="onClose"
      @ready="onFormReady"
    />
    <WorkflowForm ref="form" v-if="Number.parseInt(formType) === SysOnlineFormType.FLOW"
      :formId="formId"
      :key="formKey"
      :readOnly="readOnly"
      @ready="onFormReady"
    />
    <WorkOrderForm ref="form" v-if="Number.parseInt(formType) === SysOnlineFormType.WORK_ORDER"
      :entryId="entryId"
      :formId="formId"
      :key="formKey"
      :isPreview="isPreview"
      :readOnly="readOnly"
      @ready="onFormReady"
    />
    <label v-if="closeVisible === '1'" class="page-close-box">
      <el-button type="text" @click="onClose(true)" icon="el-icon-close" />
    </label>
  </div>
</template>

<script>
import OnlineQueryForm from '@/views/onlineForm/formRender/onlineQueryForm.vue';
import OnlineEditForm from '@/views/onlineForm/formRender/onlineEditForm.vue';
import WorkflowForm from '@/views/onlineForm/formRender/workflowForm.vue';
import WorkOrderForm from '@/views/onlineForm/formRender/onlineWorkOrder.vue';
import { SysCustomWidgetOperationType } from '@/staticDict/onlineStaticDict.js';

export default {
  props: {
    formId: {
      type: String,
      required: true
    },
    formType: {
      type: [Number, String],
      required: true
    },
    closeVisible: {
      type: String,
      default: '0'
    },
    saveOnClose: {
      type: String,
      default: '1'
    },
    operationType: {
      type: [Number, String],
      default: SysCustomWidgetOperationType.ADD
    },
    params: {
      type: Object
    },
    isPreview: {
      type: Boolean,
      default: false
    },
    rowData: {
      type: Object
    },
    flowData: {
      type: Object
    },
    readOnly: {
      type: [String, Boolean]
    },
    entryId: {
      type: String
    }
  },
  components: {
    OnlineQueryForm,
    OnlineEditForm,
    WorkflowForm,
    WorkOrderForm
  },
  data () {
    return {
      formKey: 0,
      formConfig: {}
    }
  },
  methods: {
    onClose (isSuccess, data) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess, data);
      } else {
        this.$router.go(-1);
      }
    },
    setFormData (formData, setFormData, flowData) {
      if (this.$refs.form && this.$refs.form.setFormData) this.$refs.form.setFormData(formData, setFormData, flowData);
    },
    getVariableData (variableList) {
      let funGetVariableData = null;
      if (Array.isArray(this.$refs.form) && this.$refs.form.length > 0) {
        funGetVariableData = this.$refs.form[0].getVariableData;
      } else {
        if (this.$refs.form != null) funGetVariableData = this.$refs.form.getVariableData;
      }
      if (typeof funGetVariableData === 'function') {
        return funGetVariableData(variableList);
      }
    },
    getFormData () {
      let funGetFormData = null;
      if (Array.isArray(this.$refs.form) && this.$refs.form.length > 0) {
        funGetFormData = this.$refs.form[0].getFormData;
      } else {
        if (this.$refs.form != null) funGetFormData = this.$refs.form.getFormData;
      }

      if (typeof funGetFormData === 'function') {
        return funGetFormData();
      }
    },
    onFormReady () {
      this.$emit('ready');
    }
  },
  computed: {
    getFlowFormStyle () {
      if (Number.parseInt(this.formType) === this.SysOnlineFormType.FLOW && this.formConfig) {
        return {
          width: (this.formConfig.width != null && this.formConfig.width !== '') ? this.formConfig.width + 'px' : '100%'
        }
      } else {
        return undefined;
      }
    }
  },
  provide () {
    return {
      preview: () => this.isPreview
    }
  },
  watch: {
    formId: {
      handler (newValue) {
        this.formKey++;
      }
    }
  },
  created () {
  }
}
</script>

<style>
</style>
