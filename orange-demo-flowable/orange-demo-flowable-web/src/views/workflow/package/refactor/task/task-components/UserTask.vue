<template>
  <div style="margin-top: 16px">
    <el-form-item label="处理用户">
      <TagSelect v-model="userTaskForm.assignee">
        <el-button slot="append" class="append-add" type="default" icon="el-icon-plus" @click="onSelectAssignee(false)" />
      </TagSelect>
    </el-form-item>
    <el-form-item label="候选用户">
      <TagSelect v-model="userTaskForm.candidateUsers">
        <el-button slot="append" class="append-add" type="default" icon="el-icon-plus" @click="onSelectCandidateUsers(true)" />
      </TagSelect>
    </el-form-item>
    <el-form-item label="分组类型">
      <el-select v-model="formData.groupType" placeholder="请选择分组类型" @change="onGroupTypeChange">
        <el-option label="部门" value="DEPT" />
        <el-option label="岗位" value="POST" />
        <el-option label="流程发起人部门领导" value="DEPT_POST_LEADER" />
        <el-option label="流程发起人上级部门领导" value="UP_DEPT_POST_LEADER" />
      </el-select>
    </el-form-item>
    <el-form-item v-if="formData.groupType == 'DEPT' || formData.groupType == 'POST'"
      :label="formData.groupType === 'DEPT' ? '候选部门' : '候选岗位'"
    >
      <TagSelect v-model="candidateGroupIds">
        <el-button slot="append" class="append-add" type="default" icon="el-icon-plus" @click="onSelectCandidatGroups(true)" />
      </TagSelect>
    </el-form-item>
    <el-form-item label="到期时间">
      <el-input v-model="userTaskForm.dueDate" clearable @change="updateElementTask('dueDate')" />
    </el-form-item>
    <el-form-item label="跟踪时间">
      <el-input v-model="userTaskForm.followUpDate" clearable @change="updateElementTask('followUpDate')" />
    </el-form-item>
    <el-form-item label="优先级">
      <el-input v-model="userTaskForm.priority" clearable @change="updateElementTask('priority')" />
    </el-form-item>
  </div>
</template>

<script>
import { findItemFromList, treeDataTranslate } from '@/utils';
import { DictionaryController } from '@/api';
import TagSelect from '@/views/workflow/components/TagSelect.vue';
import TaskUserSelect from '@/views/workflow/components/TaskUserSelect.vue';
import TaskGroupSelect from '@/views/workflow/components/TaskGroupSelect.vue';
import TaskPostSelect from '@/views/workflow/components/TaskPostSelect.vue';

export default {
  name: "UserTask",
  props: {
    id: String,
    type: String
  },
  components: {
    TagSelect
  },
  inject: ['flowEntry'],
  data() {
    return {
      candidateGroupIds: [],
      defaultTaskForm: {
        assignee: "",
        candidateUsers: "",
        candidateGroups: "",
        dueDate: "",
        followUpDate: "",
        priority: ""
      },
      formData: {
        groupType: 'DEPT'
      },
      groupList: undefined,
      groupMap: new Map(),
      deptPostList: [],
      deptPostMap: new Map(),
      userTaskForm: {
        assignee: "",
        candidateUsers: "",
        candidateGroups: "",
        dueDate: "",
        followUpDate: "",
        priority: ""
      }
    };
  },
  methods: {
    onSelectAssignee (multiple = false) {
      this.$dialog.show('选择用户', TaskUserSelect, {
        area: ['1000px', '600px']
      }, {
        multiple: multiple
      }).then(res => {
        let assignee = null;
        if (Array.isArray(res)) {
          assignee = res.map(item => item.loginName).join(',');
        } else {
          assignee = (res || {}).loginName;
        }
        this.userTaskForm.assignee = assignee;
      }).catch(e => {});
    },
    onSelectCandidateUsers (multiple = false) {
      let usedUserIdList = (this.userTaskForm.candidateUsers == null || this.userTaskForm.candidateUsers === '') ? [] : this.userTaskForm.candidateUsers.split(',');
      this.$dialog.show('选择候选用户', TaskUserSelect, {
        area: ['1000px', '600px']
      }, {
        multiple: multiple,
        usedUserIdList: usedUserIdList
      }).then(res => {
        let candidateUsers = [];
        if (Array.isArray(res)) {
          candidateUsers = res.map(item => item.loginName);
        } else {
          if (res && res.loginName !== '') {
            candidateUsers = [res.loginName];
          }
        }
        // 跟老的候选人合并
        if (Array.isArray(usedUserIdList) && usedUserIdList.length > 0) {
          candidateUsers.forEach(item => {
            if (usedUserIdList.indexOf(item) === -1) {
              usedUserIdList.push(item);
            }
          });
        } else {
          usedUserIdList = candidateUsers;
        }

        this.userTaskForm.candidateUsers = usedUserIdList.join(',');
      }).catch(e => {});
    },
    loadDeptWidgetDropdownList () {
      return new Promise((resolve, reject) => {
        DictionaryController.dictSysDept(this, {}).then(res => {
          res.getList().forEach(item => {
            this.groupMap.set(item.id, item);
          });
          this.groupList = treeDataTranslate(res.getList());
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
      
    },
    loadDeptPostList () {
      return new Promise((resolve, reject) => {
        DictionaryController.dictDeptPost(this, {}).then(res => {
          res.forEach(item => {
            this.deptPostMap.set(item.deptPostId, item);
          });
          this.deptPostList = res.sort((value1, value2) => {
            return value1.level - value2.level;
          });
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    handlerDeptChange (usedIdList) {
      this.$dialog.show('选择部门', TaskGroupSelect, {
        area: ['600px', '600px']
      }, {
        allGroupList: this.groupList,
        usedIdList: usedIdList
      }).then(res => {
        if (Array.isArray(res)) {
          if (!Array.isArray(this.candidateGroupIds)) this.candidateGroupIds = [];
          res.forEach(item => {
            if (findItemFromList(this.candidateGroupIds, item.id, 'id') == null) {
              this.candidateGroupIds.push(item);
            }
          });
        }
        this.userTaskForm.candidateGroups = Array.isArray(this.candidateGroupIds) ? this.candidateGroupIds.map(item => item.id).join(',') : '';
      }).catch(e => {});
    },
    handlerPostChange (usedIdList) {
      this.$dialog.show('选择岗位', TaskPostSelect, {
        area: ['1000px', '615px'],
        skin: 'layer-advance-dialog'
      }, {
        deptList: this.groupList,
        deptPostList: this.deptPostList,
        usedIdList: usedIdList
      }).then(res => {
        if (Array.isArray(res)) {
          if (!Array.isArray(this.candidateGroupIds)) this.candidateGroupIds = [];
          res.forEach(item => {
            if (findItemFromList(this.candidateGroupIds, item.id, 'id') == null) {
              this.candidateGroupIds.push({
                id: item.deptPostId,
                name: `${item.deptName} / ${item.postShowName}`
              });
            }
          });
        }
        this.userTaskForm.candidateGroups = Array.isArray(this.candidateGroupIds) ? this.candidateGroupIds.map(item => item.id).join(',') : '';
      }).catch(e => {});
    },
    onSelectCandidatGroups () {
      let usedIdList = this.userTaskForm.candidateGroups ? this.userTaskForm.candidateGroups.split(',') : [];
      if (this.formData.groupType === 'DEPT') {
        this.handlerDeptChange(usedIdList);
      } else {
        this.handlerPostChange(usedIdList);
      }
    },
    resetTaskForm() {
      this.userTaskForm = {
        assignee: "",
        candidateUsers: "",
        candidateGroups: "",
        dueDate: "",
        followUpDate: "",
        priority: ""
      }
      this.candidateGroupIds = [];
      let formData = (window.bpmnInstances.bpmnElement && window.bpmnInstances.bpmnElement.businessObject) ? window.bpmnInstances.bpmnElement.businessObject.formKey : '';
      let formObj = formData ? JSON.parse(formData) : {};
      if (formObj) {
        this.formData = {
          formId: formObj.formId,
          routerName: formObj.routerName,
          editable: !formObj.readOnly,
          groupType: formObj.groupType || 'DEPT'
        }
      } else {
        this.formData = {
          groupType: 'DEPT'
        }
      }
      for (let key in this.defaultTaskForm) {
        let value;
        if (key === "candidateUsers" || key === "candidateGroups") {
          value = (window.bpmnInstances.bpmnElement || {}).businessObject[key] || this.defaultTaskForm[key];
          if (key === "candidateGroups" && value) {
            this.candidateGroupIds = value.split(',');
            if (Array.isArray(this.candidateGroupIds)) {
              this.candidateGroupIds = this.candidateGroupIds.map(item => {
                if (this.formData.groupType === 'DEPT') {
                  return this.groupMap.get(item);
                } else {
                  let temp = this.deptPostMap.get(item);
                  if (temp) {
                    return {
                      id: temp.deptPostId,
                      name: `${temp.deptName} / ${temp.postShowName}`
                    }
                  }
                }
              }).filter(item => item != null);
            }
          }
        } else {
          value = (window.bpmnInstances.bpmnElement || {}).businessObject[key] || this.defaultTaskForm[key];
        }
        this.$set(this.userTaskForm, key, value);
      }
    },
    updateFormKey () {
      if (this.formData == null) return;
      let formKeyString = JSON.stringify({
        formId: this.flowEntry().bindFormType === this.SysFlowEntryBindFormType.ONLINE_FORM ? this.formData.formId : undefined,
        routerName: this.flowEntry().bindFormType === this.SysFlowEntryBindFormType.ONLINE_FORM ? undefined : this.formData.routerName,
        readOnly: !this.formData.editable,
        groupType: this.formData.groupType || 'DEPT'
      });
      window.bpmnInstances.modeling.updateProperties(window.bpmnInstances.bpmnElement, { formKey: formKeyString });
    },
    onGroupTypeChange () {
      this.candidateGroupIds = [];
      this.userTaskForm.candidateGroups = '';
      this.updateFormKey();
    },
    updateElementTask(key) {
      let taskAttr = Object.create(null);
      if (key === "candidateUsers" || key === "candidateGroups") {
        taskAttr[key] = this.userTaskForm[key] || null;
      } else {
        taskAttr[key] = this.userTaskForm[key] || null;
      }
      window.bpmnInstances.modeling.updateProperties(window.bpmnInstances.bpmnElement, taskAttr);
    }
  },
  watch: {
    id: {
      immediate: true,
      handler(newValue) {
        if (this.groupList == null) {
          let httpCall = [
            this.loadDeptWidgetDropdownList(),
            this.loadDeptPostList()
          ];
          Promise.all(httpCall).then(res => {
            this.bpmnElement = window.bpmnInstances.bpmnElement;
            this.$nextTick(() => {
              this.resetTaskForm();
            });
          }).catch(e => {});
        } else {
          this.bpmnElement = window.bpmnInstances.bpmnElement;
          this.$nextTick(() => this.resetTaskForm());
        }
      }
    },
    'userTaskForm.assignee': {
      handler () {
        this.updateElementTask('assignee');
      }
    },
    'userTaskForm.candidateUsers': {
      handler () {
        this.updateElementTask('candidateUsers');
      }
    },
    'candidateGroupIds': {
      handler () {
        this.userTaskForm.candidateGroups = Array.isArray(this.candidateGroupIds) ? this.candidateGroupIds.map(item => item.id).join(',') : '';
      }
    },
    'userTaskForm.candidateGroups': {
      handler () {
        this.updateElementTask('candidateGroups');
      }
    }
  },
  beforeDestroy() {
    this.bpmnElement = null;
    this.groupMap = null;
  }
};
</script>

<style scoped>
  .append-add {
    border: none;
    border-left: 1px solid #DCDFE6;
    border-radius: 0px;
    background: #F5F7FA;
  }
</style>