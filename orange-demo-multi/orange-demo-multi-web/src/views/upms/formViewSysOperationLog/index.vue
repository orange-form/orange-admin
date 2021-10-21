<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formViewOperationLog" :model="formData" class="full-width-input" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="操作模块：">
            <span class="input-item">{{formData.formViewSysOperationLog.serviceName}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="操作人员：">
            <span class="input-item">{{formData.formViewSysOperationLog.operatorName}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="请求地址：">
            <span class="input-item">{{formData.formViewSysOperationLog.requestUrl}}</span>
            <el-tag size="mini" style="margin-left: 10px"
              :type="formData.formViewSysOperationLog.requestMethod === 'GET' ? 'success' : 'primary'">
              {{formData.formViewSysOperationLog.requestMethod}}
            </el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="主机地址：">
            <span class="input-item">{{formData.formViewSysOperationLog.requestIp}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="操作方法：">
            <span class="input-item">{{formData.formViewSysOperationLog.apiMethod}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="Trace Id：">
            <span class="input-item">{{formData.formViewSysOperationLog.traceId}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="Session Id：">
            <span class="input-item">{{formData.formViewSysOperationLog.sessionId}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="请求参数：">
            <JsonViewer v-model="formData.formViewSysOperationLog.requestArguments" :expand-depth="5" boxed />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="返回结果：">
            <JsonViewer v-model="formData.formViewSysOperationLog.responseResult" :expand-depth="5" boxed />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="登录状态：">
            <el-tag v-if="formData.formViewSysOperationLog.success != null"
              :type="formData.formViewSysOperationLog.success ? 'success' : 'danger'">
              {{formData.formViewSysOperationLog.success ? '成功' : '失败'}}
            </el-tag>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import JsonViewer from 'vue-json-viewer';

export default {
  props: {
    rowData: Object
  },
  components: {
    JsonViewer
  },
  data () {
    return {
      formData: {
        formViewSysOperationLog: {
          apiClass: undefined,
          apiMethod: undefined,
          description: undefined,
          operationTime: undefined,
          operationType: undefined,
          operatorId: undefined,
          operatorName: undefined,
          requestArguments: {},
          requestIp: undefined,
          requestMethod: undefined,
          requestUrl: undefined,
          responseResult: {},
          serviceName: undefined,
          traceId: undefined,
          sessionId: undefined,
          success: true
        }
      }
    }
  },
  mounted () {
    this.formData.formViewSysOperationLog = { ...this.rowData };
    this.formData.formViewSysOperationLog.requestArguments = JSON.parse(this.rowData.requestArguments) || {};
    this.formData.formViewSysOperationLog.responseResult = JSON.parse(this.rowData.responseResult) || {};
  }
}
</script>

<style>
  .jv-code {
    padding: 0px!important;
  }

  .jv-node {
    padding: 3px;
    margin: 0px;
    background: #F3F3F3;
  }
</style>
