<template>
  <el-form ref="form" :model="formData" :rules="rules" label-width="80px" size="mini" label-position="right" @submit.native.prevent>
    <el-row :gutter="20">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" placeholder="字典名称" clearable />
      </el-form-item>
    </el-row>
    <!-- 弹窗下发按钮栏，必须设置class为dialog-btn-layer -->
    <el-row type="flex" justify="end" class="dialog-btn-layer">
      <el-button size="mini" @click="onCancel(false)" >取消</el-button>
      <el-button type="primary" size="mini" @click="onSubmit">确定</el-button>
    </el-row>
  </el-form>
</template>

<script>
export default {
  name: 'DictEdit',
  props: {
    dictInfo: {
      type: Object,
      required: true
    },
    currentData: {
      type: Object,
      default: undefined
    }
  },
  data () {
    return {
      formData: {
        name: undefined,
        id: undefined
      },
      rules: {
        name: [{required: true, message: '字典数据名称不能为空', trigger: 'blur'}]
      }
    }
  },
  methods: {
    onCancel (isSuccess = false) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSubmit () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          let params = {};
          params[this.dictInfo.variableName] = {};
          params[this.dictInfo.variableName][this.dictInfo.nameKey] = this.formData.name;

          if (this.formData.id == null) {
            this.dictInfo.addApi(this, params).then(res => {
              this.$message.success('操作成功');
              this.onCancel(true);
            }).catch(e => {

            });
          } else {
            params[this.dictInfo.variableName][this.dictInfo.idKey] = this.formData.id;
            this.dictInfo.updateApi(this, params).then(res => {
              this.$message.success('操作成功');
              this.onCancel(true);
            }).catch(e => {
            });
          }
        }
      });
    }
  },
  mounted () {
    if (this.currentData != null) {
      this.formData.id = this.currentData.id;
      this.formData.name = this.currentData.name;
    }
  }
}
</script>

<style>
</style>
