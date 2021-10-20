<template>
  <!-- 流程图 -->
  <div class="form-single-fragment" style="position: relative;">
    <el-row>
      <ProcessViewer :xml="taskProcessXml" :finishedInfo="finishedInfo" style="height: 655px" />
    </el-row>
  </div>
</template>

<script>
import '@/staticDict/flowStaticDict.js';
import { FlowOperationController } from '@/api/flowController.js';
import ProcessViewer from '@/views/workflow/components/ProcessViewer.vue';

export default {
  name: 'formTaskProcessViewer',
  props: {
    processDefinitionId: {
      type: String,
      required: true
    },
    processInstanceId: {
      type: String
    }
  },
  components: {
    ProcessViewer
  },
  data () {
    return {
      finishedInfo: undefined,
      taskProcessXml: undefined
    }
  },
  methods: {
    getTaskHighlightData () {
      if (this.processInstanceId == null || this.processInstanceId === '') {
        return;
      }
      let params = {
        processInstanceId: this.processInstanceId
      }
      FlowOperationController.viewHighlightFlowData(this, params).then(res => {
        // 已完成节点
        this.finishedInfo = res.data;
      }).catch(e => {});
    },
    getTaskProcessXml () {
      let params = {
        processDefinitionId: this.processDefinitionId
      }
      FlowOperationController.viewProcessBpmn(this, params).then(res => {
        // 当前流程实例xml
        this.taskProcessXml = res.data;
      }).catch(e => {});
    }
  },
  mounted () {
    this.getTaskHighlightData();
    this.getTaskProcessXml();
  }
}
</script>

<style>
</style>
