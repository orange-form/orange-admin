<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="form" :model="formData" class="full-width-input" :rules="rules"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row>
        <el-col :span="24">
          <el-form-item label="表单参数" prop="objectFieldName">
            <el-select v-model="formData.objectFieldName" placeholder="请选择要添加的表单参数" clearable
              @change="onParamChange">
              <el-option v-for="item in columnList" :key="item.objectFieldName"
                :label="item.objectFieldName" :value="item.objectFieldName" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" style="margin-top: 15px;">
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
import { findItemFromList } from '@/utils';

export default {
  props: {
    columnList: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      formData: {
        objectFieldName: undefined,
        primaryKey: false,
        builtin: false
      },
      rules: {
        objectFieldName: [
          { required: true, message: '表单参数不能为空', trigger: 'blur' }
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
    },
    onParamChange (value) {
      let paramColumn = findItemFromList(this.columnList, value, 'objectFieldName');
      if (paramColumn != null) this.formData.primaryKey = paramColumn.primaryKey;
    }
  }
}
</script>

<style>
</style>
