<template>
  <div class="panel-tab__content">
    <el-form size="mini" label-width="90px" @submit.native.prevent>
      <el-form-item label="集合类型">
        <el-select v-model="collectionType" key="collectionType" placeholder="">
          <el-option label="候选用户" value="assignee" />
          <el-option label="候选分组" value="group" :disabled="true" />
        </el-select>
      </el-form-item>
      <el-form-item label="候选用户">
        <TagSelect v-model="assigneeList">
          <el-button slot="append" class="append-add" type="default" icon="el-icon-plus" @click="onSelectAssigneeList()" />
        </TagSelect>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import TagSelect from '@/views/workflow/components/TagSelect.vue';
import TaskUserSelect from '@/views/workflow/components/TaskUserSelect.vue';
import { findItemFromList } from '@/utils';

export default {
  name: "ElementMultiInstanceAssignee",
  props: {
    id: String,
    type: String
  },
  components: {
    TagSelect
  },
  inject: ['prefix'],
  data () {
    return {
      collectionType: 'assignee',
      assigneeList: [],
      elementAssigneeList: []
    }
  },
  methods: {
    resetMultiInstanceAssignee () {
      this.bpmnELement = window.bpmnInstances.bpmnElement;
      let elExtensionElements = this.bpmnELement.businessObject.get("extensionElements") || window.bpmnInstances.moddle.create("bpmn:ExtensionElements", { values: [] });
      this.elementAssigneeList = elExtensionElements.values.filter(ex => ex.$type === `${this.prefix}:AssigneeList`)?.[0] ||
        window.bpmnInstances.moddle.create(`${this.prefix}:AssigneeList`, { assigneeList: [], type: 'assignee' });
      this.collectionType = this.elementAssigneeList.type || 'assignee';
      this.assigneeList = (this.elementAssigneeList.assigneeList || []).map(item => {
        return {
          id: item.id,
          name: item.id
        }
      });
      this.updateElementExtensions();
    },
    updateElementExtensions () {
      let elExtensionElements = this.bpmnELement.businessObject.get("extensionElements")  || window.bpmnInstances.moddle.create("bpmn:ExtensionElements", { values: [] });
      let otherExtensions = elExtensionElements.values.filter(ex => ex.$type !== `${this.prefix}:AssigneeList`);
      let element = window.bpmnInstances.moddle.create(`${this.prefix}:AssigneeList`, {
        assigneeList: (this.assigneeList || []).map(item => {
          return window.bpmnInstances.moddle.create(`${this.prefix}:Assignee`, {
            id: item.id
          });
        }),
        type: this.collectionType
      });
      const newElExtensionElements = window.bpmnInstances.moddle.create(`bpmn:ExtensionElements`, {
        values: [...otherExtensions, element]
      });
      // 更新到元素上
      window.bpmnInstances.modeling.updateProperties(this.bpmnELement, {
        extensionElements: newElExtensionElements
      });
    },
    onSelectAssigneeList () {
      this.$dialog.show('选择候选用户', TaskUserSelect, {
        area: ['1000px', '600px']
      }, {
        multiple: true,
        usedUserIdList: Array.isArray(this.assigneeList) ? (this.assigneeList || []).map(item => item.id) : []
      }).then(res => {
        if (!Array.isArray(this.assigneeList)) this.assigneeList = [];
        if (Array.isArray(res)) {
          res.forEach(item => {
            let temp = findItemFromList(this.assigneeList, item.loginName, 'id');
            if (temp == null) {
              this.assigneeList.push({
                id: item.loginName,
                name: item.loginName
              });
            }
          });
        } else {
          if (res && res.loginName !== '') {
            let temp = findItemFromList(this.assigneeList, res.loginName, 'id');
            if (temp == null) {
              this.assigneeList.push({
                id: res.loginName,
                name: res.loginName
              });
            }
          }
        }
        this.assigneeList = [...this.assigneeList];
        this.updateElementExtensions();
      });
    }
  },
  watch: {
    id: {
      immediate: true,
      handler (val) {
        if (val && val.length) {
          this.$nextTick(() => {
            this.resetMultiInstanceAssignee();
          });
        }
      }
    },
    assigneeList: {
      handler () {
        this.updateElementExtensions();
      }
    }
  }
}
</script>

<style scoped>
  .append-add {
    border: none;
    border-left: 1px solid #DCDFE6;
    border-radius: 0px;
    background: #F5F7FA;
  }
</style>
