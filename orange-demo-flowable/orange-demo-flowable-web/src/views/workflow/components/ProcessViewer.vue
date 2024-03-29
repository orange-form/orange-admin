<template>
  <div class="process-viewer">
    <div class="process-canvas" style="height: 100%;" ref="processCanvas" v-show="!isLoading" />
    <!-- 自定义箭头样式，用于已完成状态下流程连线箭头 -->
    <defs ref="customDefs">
      <marker id="sequenceflow-end-white-success" viewBox="0 0 20 20" refX="11" refY="10" markerWidth="10" markerHeight="10" orient="auto">
        <path class="success-arrow" d="M 1 5 L 11 10 L 1 15 Z" style="stroke-width: 1px; stroke-linecap: round; stroke-dasharray: 10000, 1;"></path>
      </marker>
      <marker id="conditional-flow-marker-white-success" viewBox="0 0 20 20" refX="-1" refY="10" markerWidth="10" markerHeight="10" orient="auto">
        <path class="success-conditional" d="M 0 10 L 8 6 L 16 10 L 8 14 Z" style="stroke-width: 1px; stroke-linecap: round; stroke-dasharray: 10000, 1;"></path>
      </marker>
    </defs>
    <!-- 已完成节点悬浮弹窗 -->
    <el-dialog class="comment-dialog" :title="dlgTitle || '审批记录'" :visible.sync="dialogVisible">
      <el-row>
        <el-table :data="taskCommentList" size="mini" border header-cell-class-name="table-header-gray" height="500px">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" />
          <el-table-column label="执行人" prop="createUsername" width="150px" />
          <el-table-column label="操作" width="150px">
            <template slot-scope="scope">
              <el-tag size="mini" :type="getOperationTagType(scope.row.approvalType)" effect="dark">{{SysFlowTaskOperationType.getValue(scope.row.approvalType)}}</el-tag>
              <el-tag v-if="scope.row.delegateAssginee != null" size="mini" type="success" effect="plain" style="margin-left: 10px;">{{scope.row.delegateAssginee}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="审批意见">
            <template slot-scope="scope">
              <span>{{scope.row.comment ? scope.row.comment : ''}}</span>
            </template>
          </el-table-column>
          <el-table-column label="处理时间" prop="createTime" width="160px" />
        </el-table>
      </el-row>
    </el-dialog>
    <div style="position: absolute; top: 0px; left: 0px; width: 100%;">
      <el-row type="flex" justify="end">
        <el-button-group key="scale-control" size="medium">
          <el-button size="medium" type="default" :plain="true" :disabled="defaultZoom <= 0.3" icon="el-icon-zoom-out" @click="processZoomOut()" />
          <el-button size="medium" type="default" style="width: 90px;">{{ Math.floor(this.defaultZoom * 10 * 10) + "%" }}</el-button>
          <el-button size="medium" type="default" :plain="true" :disabled="defaultZoom >= 3.9" icon="el-icon-zoom-in" @click="processZoomIn()" />
          <el-button size="medium" type="default" icon="el-icon-c-scale-to-original" @click="processReZoom()" />
          <slot />
        </el-button-group>
      </el-row>
    </div>
  </div>
</template>

<script>
import '../package/theme/index.scss';
// import BpmnViewer from 'bpmn-js/lib/Viewer';
import BpmnModeler from 'bpmn-js/lib/Modeler';

export default {
  props: {
    xml: {
      type: String
    },
    finishedInfo: {
      type: Object
    },
    // 所有节点审批记录
    allCommentList: {
      type: Array
    }
  },
  data () {
    return {
      dialogVisible: false,
      dlgTitle: undefined,
      defaultZoom: 1,
      // 是否正在加载流程图
      isLoading: true,
      bpmnViewer: undefined,
      // 已完成流程元素
      processNodeInfo: undefined,
      // 当前任务id
      selectTaskId: undefined,
      // 任务节点审批记录
      taskCommentList: [],
      // 已完成任务悬浮延迟Timer
      hoverTimer: null
    }
  },
  methods: {
    processReZoom () {
      this.defaultZoom = 1;
      this.bpmnViewer.get('canvas').zoom('fit-viewport', 'auto');
    },
    processZoomIn (zoomStep = 0.1) {
      let newZoom = Math.floor(this.defaultZoom * 100 + zoomStep * 100) / 100;
      if (newZoom > 4) {
        throw new Error('[Process Designer Warn ]: The zoom ratio cannot be greater than 4');
      }
      this.defaultZoom = newZoom;
      this.bpmnViewer.get('canvas').zoom(this.defaultZoom);
    },
    processZoomOut (zoomStep = 0.1) {
      let newZoom = Math.floor(this.defaultZoom * 100 - zoomStep * 100) / 100;
      if (newZoom < 0.2) {
        throw new Error('[Process Designer Warn ]: The zoom ratio cannot be less than 0.2');
      }
      this.defaultZoom = newZoom;
      this.bpmnViewer.get('canvas').zoom(this.defaultZoom);
    },
    getOperationTagType (type) {
      switch (type) {
        case this.SysFlowTaskOperationType.AGREE:
        case this.SysFlowTaskOperationType.MULTI_AGREE:
          return 'success';
        case this.SysFlowTaskOperationType.REFUSE:
        case this.SysFlowTaskOperationType.PARALLEL_REFUSE:
        case this.SysFlowTaskOperationType.MULTI_REFUSE:
          return 'warning';
        case this.SysFlowTaskOperationType.STOP:
          return 'danger'
        default:
          return 'primary';
      }
    },
    // 流程图预览清空
    clearViewer () {
      if (this.$refs.processCanvas) this.$refs.processCanvas.innerHTML = '';
      if (this.bpmnViewer) this.bpmnViewer.destroy();
      this.bpmnViewer = null;
    },
    // 添加自定义箭头
    addCustomDefs () {
      const canvas = this.bpmnViewer.get('canvas');
      const svg = canvas._svg;
      const customDefs = this.$refs.customDefs;
      svg.appendChild(customDefs);
    },
    // 任务悬浮弹窗
    onSelectElement (element) {
      this.selectTaskId = undefined;
      this.dlgTitle = undefined;
      
      if (this.processNodeInfo == null || this.processNodeInfo.finishedTaskSet == null) return;
      
      if (element == null || this.processNodeInfo.finishedTaskSet.indexOf(element.id) === -1) {
        return;
      }
      
      this.selectTaskId = element.id;
      this.dlgTitle = element.businessObject ? element.businessObject.name : undefined;
      // 计算当前悬浮任务审批记录，如果记录为空不显示弹窗
      this.taskCommentList = (this.allCommentList || []).filter(item => {
        return item.taskKey === this.selectTaskId;
      });
      this.dialogVisible = true;
    },
    // 显示流程图
    async importXML (xml) {
      this.clearViewer();
      if (xml != null && xml !== '') {
        try {
          this.bpmnViewer = new BpmnModeler({
            container: this.$refs.processCanvas
          });
          // 任务节点悬浮事件
          this.bpmnViewer.on('element.click', ({ element }) => {
            this.onSelectElement(element);
          });
          
          this.isLoading = true;
          await this.bpmnViewer.importXML(xml);
          this.addCustomDefs();
        } catch (e) {
          console.error(e);
          this.clearViewer();
        } finally {
          this.isLoading = false;
          this.setProcessStatus(this.processNodeInfo);
        }
      }
    },
    // 设置流程图元素状态
    setProcessStatus (processNodeInfo) {
      this.processNodeInfo = processNodeInfo;
      if (this.isLoading || this.processNodeInfo == null || this.bpmnViewer == null) return;
      let { finishedSequenceFlowSet, finishedTaskSet, unfinishedTaskSet } = this.processNodeInfo;
      const canvas = this.bpmnViewer.get('canvas');
      const elementRegistry = this.bpmnViewer.get('elementRegistry');
      if (Array.isArray(finishedSequenceFlowSet)) {
        finishedSequenceFlowSet.forEach(item => {
          if (item != null) {
            canvas.addMarker(item, 'success');
            let element = elementRegistry.get(item);
            const conditionExpression = element.businessObject.conditionExpression;
            if (conditionExpression) {
              canvas.addMarker(item, 'condition-expression');
            }
          }
        });
      }
      if (Array.isArray(finishedTaskSet)) {
        finishedTaskSet.forEach(item => {
          canvas.addMarker(item, 'success');
        });
      }
      if (Array.isArray(unfinishedTaskSet)) {
        unfinishedTaskSet.forEach(item => {
          canvas.addMarker(item, 'current');
        });
      }
    }
  },
  destroyed () {
    this.clearViewer();
  },
  watch: {
    xml: {
      handler (newXml) {
        this.importXML(newXml);
      },
      immediate: true
    },
    finishedInfo: {
      handler (newInfo) {
        this.setProcessStatus(newInfo);
      },
      immediate: true
    }
  }
}
</script>

<style scoped>
  .comment-dialog >>> .el-dialog__body {
    padding: 0px;
  }
</style>
