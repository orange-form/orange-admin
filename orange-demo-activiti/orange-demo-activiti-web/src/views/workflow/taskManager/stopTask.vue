<template>
  <!-- 终止流程 -->
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="form" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="终止原因" prop="reason">
            <el-input class="input-item" v-model="formData.reason" type="textarea"
              :clearable="true" placeholder="终止原因" />
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
import { FlowOperationController } from '@/api/flowController.js';

export default {
  props: {
    processInstanceId: {
      type: String,
      required: true
    },
    taskId: {
      type: String
    }
  },
  data () {
    return {
      formData: {
        reason: undefined
      },
      rules: {
        reason: [
          {
            required: true,
            message: '请输入终止原因',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSubmit () {
      this.$refs.form.validate(valid => {
        if (!valid) return;
        FlowOperationController.stopProcessInstance(this, {
          processInstanceId: this.processInstanceId,
          taskId: this.taskId,
          stopReason: this.formData.reason
        }).then(res => {
          this.onCancel(true);
        }).catch(e => {});
      });
    }
  }
}
</script>

<style>
</style>
