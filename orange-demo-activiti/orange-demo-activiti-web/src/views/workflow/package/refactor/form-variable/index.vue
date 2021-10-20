<template>
  <div class="panel-tab__content">
    <el-form size="mini" label-width="90px" @submit.native.prevent>
      <el-form-item label="任务变量">
        <el-select v-model="taskVariableList" clearable multiple collapse-tags
          placeholder="选择任务节点使用的变量" filterable default-first-option
          @change="onSelectVariableChange">
          <el-option v-for="item in allVariableList()" :key="item.variableId"
            :value="item.variableId" :label="item.showName">
            <el-row type="flex" justify="space-between">
              <span>{{item.showName}}</span>
              <span>{{item.variableName}}</span>
            </el-row>
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'formVariable',
  props: {
    id: String,
    type: String
  },
  inject: ['flowEntry', 'allVariableList', 'prefix'],
  data () {
    return {
      variableList: undefined,
      taskVariableList: [],
      otherExtensions: []
    }
  },
  methods: {
    resetFormVariable () {
      this.bpmnELement = window.bpmnInstances.bpmnElement;

      let elExtensionElements = this.bpmnELement.businessObject.get("extensionElements") || window.bpmnInstances.moddle.create("bpmn:ExtensionElements", { values: [] });
      this.variableList = elExtensionElements.values.filter(ex => ex.$type === `${this.prefix}:VariableList`)?.[0] ||
        window.bpmnInstances.moddle.create(`${this.prefix}:VariableList`, { variableList: [] });
      this.taskVariableList = JSON.parse(JSON.stringify(this.variableList.variableList || []));
      this.taskVariableList = this.taskVariableList.map(item => item.id);
      this.updateElementExtensions();
    },
    updateElementExtensions () {
      // 更新回扩展元素
      let elExtensionElements = this.bpmnELement.businessObject.get("extensionElements")  || window.bpmnInstances.moddle.create("bpmn:ExtensionElements", { values: [] });
      let otherExtensions = elExtensionElements.values.filter(ex => ex.$type !== `${this.prefix}:VariableList`);
      this.variableList.variableList = this.taskVariableList.map(item => {
        return window.bpmnInstances.moddle.create(`${this.prefix}:FormVariable`, {
          id: item
        });
      });
      const newElExtensionElements = window.bpmnInstances.moddle.create(`bpmn:ExtensionElements`, {
        values: otherExtensions.concat(this.variableList)
      });
      // 更新到元素上
      window.bpmnInstances.modeling.updateProperties(this.bpmnELement, {
        extensionElements: newElExtensionElements
      });
    },
    onSelectVariableChange (values) {
      this.updateElementExtensions();
    }
  },
  watch: {
    id: {
      immediate: true,
      handler (val) {
        if (val && val.length) {
          this.$nextTick(() => {
            this.resetFormVariable();
          });
        }
      }
    }
  }
}
</script>

<style scoped>
  .full-line-btn {
    width: 100%;
    margin-top: 10px;
    border: 1px dashed #EBEEF5;
  }
</style>
