<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="form" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="按钮类型" prop="type">
            <el-select class="input-item" v-model="formData.type"
              :clearable="true" placeholder="按钮类型" @change="onOperationTypeChange">
              <el-option v-for="item in SysFlowTaskOperationType.getList().filter(item => item.id !== SysFlowTaskOperationType.STOP)"
                :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="按钮名称" prop="label">
            <el-input class="input-item" v-model="formData.label"
              :clearable="true" placeholder="按钮名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="显示顺序">
            <el-input-number class="input-item" v-model="formData.showOrder"
              :clearable="true" placeholder="显示顺序" />
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
    }
  },
  data () {
    return {
      formData: {
        id: undefined,
        type: undefined,
        label: undefined,
        showOrder: 0
      },
      rules: {
        type: [{ required: true, message: '请选择按钮类型', trigger: 'blur' }],
        label: [{ required: true, message: '请输入按钮名称', trigger: 'blur' }]
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
        if (valid) {
          this.onCancel(true);
        }
      });
    },
    onOperationTypeChange (type) {
      if (type == null || type === '') {
        this.formData.label = undefined;
      } else {
        this.formData.label = this.SysFlowTaskOperationType.getValue(type);
      }
    }
  },
  mounted () {
    if (this.rowData) {
      this.formData = {
        ...this.rowData
      }
    }
  }
}
</script>

<style>
</style>
