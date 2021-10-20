<template>
  <div class="process-design" style="display: flex; height: 100%;">
    <MyProcessDesigner
      :key="`designer-${reloadIndex}`"
      v-model="xmlString"
      v-bind="controlForm"
      keyboard
      ref="processDesigner"
      :processId="flowEntryInfo.processDefinitionKey"
      :processName="flowEntryInfo.processDefinitionName"
      :events="[
        'element.click',
        'connection.added',
        'connection.removed',
        'connection.changed'
      ]"
      @element-click="elementClick"
      @init-finished="initModeler"
      @event="handlerEvent"
      @save="onSaveProcess"
    />
    <MyProcessPenal :key="`penal-${reloadIndex}`" :bpmn-modeler="modeler" :prefix="controlForm.prefix" :width="600" class="process-panel" />
  </div>
</template>

<script>
import Vue from 'vue';
import '../package/theme/index.scss';
import { MyProcessDesigner, MyProcessPenal } from '../package/index.js';
// 自定义元素选中时的弹出菜单（修改 默认任务 为 用户任务）
import CustomContentPadProvider from '../package/process-designer/plugins/content-pad';
// 自定义左侧菜单（修改 默认任务 为 用户任务）
import CustomPaletteProvider from '../package/process-designer/plugins/palette';
import { vuePlugin } from '../package/highlight';
import 'highlight.js/styles/atom-one-dark-reasonable.css';
Vue.use(vuePlugin);

export default {
  name: 'ProcessDesigner',
  props: {
    flowEntryInfo: {
      type: Object,
      required: true
    }
  },
  components: {
    MyProcessDesigner,
    MyProcessPenal
  },
  data () {
    return {
      reloadIndex: 0,
      xmlString: this.flowEntryInfo.bpmnXml,
      modeler: null,
      controlForm: {
        processId: '',
        processName: '',
        simulation: false,
        labelEditing: false,
        labelVisible: false,
        prefix: 'activiti',
        headerButtonSize: 'small',
        additionalModel: [CustomContentPadProvider, CustomPaletteProvider]
      }
    }
  },
  methods: {
    elementClick (element) {
      this.element = element;
    },
    initModeler (modeler) {
      setTimeout(() => {
        this.modeler = modeler;
      }, 10);
    },
    handlerEvent (eventName, element) {
    },
    onSaveProcess (saveData) {
      this.$emit('save', saveData);
    }
  }
}
</script>

<style>
</style>
