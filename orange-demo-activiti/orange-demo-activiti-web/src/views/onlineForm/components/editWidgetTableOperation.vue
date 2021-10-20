<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="form" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row>
        <el-col :span="24">
          <el-form-item label="操作名称" prop="name">
            <el-input class="input-item" v-model="formData.name"
              :clearable="true" placeholder="操作按钮名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="!formData.rowOperation">
          <el-form-item label="操作名称" prop="btnType">
            <el-select v-model="formData.btnType">
              <el-option label="primary" value="primary" />
              <el-option label="success" value="success" />
              <el-option label="warning" value="warning" />
              <el-option label="danger" value="danger" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="!formData.rowOperation">
          <el-form-item label="镂空模式" prop="btnType">
            <el-switch v-model="formData.plain" />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="formData.rowOperation">
          <el-form-item label="操作按钮类名">
            <el-input class="input-item" v-model="formData.btnClass"
              :clearable="true" placeholder="操作按钮类名" />
          </el-form-item>
        </el-col>
        <el-col :span="24"
          v-if="formData.type === SysCustomWidgetOperationType.ADD ||
            formData.type === SysCustomWidgetOperationType.EDIT ||
            formData.type === SysCustomWidgetOperationType.CUSTOM"
        >
          <el-form-item label="操作表单">
            <el-select v-model="formData.formId" placeholder="选择操作按钮触发表单" clearable>
              <el-option v-for="form in getFormList" :key="form.formId"
                :label="form.formName" :value="form.formId" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini"
              @click="onSubmit()">
              保存
            </el-button>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>

export default {
  props: {
    rowData: {
      type: Object
    },
    formList: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      formData: {
        type: this.SysCustomWidgetOperationType.CUSTOM,
        name: undefined,
        btnType: undefined,
        plain: false,
        enabled: true,
        builtin: false,
        rowOperation: true,
        btnClass: 'table-btn primary',
        formId: undefined
      },
      rules: {
        name: [
          { required: true, message: '操作按钮名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess, this.formData);
      }
    },
    onSubmit () {
      this.$refs.form.validate(valid => {
        if (!valid) return;
        this.onCancel(true);
      });
    }
  },
  computed: {
    getFormList () {
      return this.formList.filter(item => {
        if (this.formData.type === this.SysCustomWidgetOperationType.CUSTOM) {
          return item.formType === this.SysOnlineFormType.QUERY;
        } else {
          return item.formType === this.SysOnlineFormType.FORM;
        }
      });
    }
  },
  mounted () {
    if (this.rowData != null) {
      this.formData = {
        ...this.rowData
      }
    }
  }
}
</script>

<style>
</style>
