<template>
  <div style="margin-top: 16px">
    <el-form-item label="候选类型">
      <el-select v-model="formData.groupType" placeholder="请选择分组类型" @change="onGroupTypeChange">
        <el-option label="处理用户" value="ASSIGNEE" />
        <el-option label="候选用户组" value="USERS" />
        <el-option label="角色" value="ROLE" />
        <el-option label="部门" value="DEPT" />
        <el-option label="岗位" value="POST" />
        <el-option label="流程发起人部门领导" value="DEPT_POST_LEADER" />
        <el-option label="流程发起人上级部门领导" value="UP_DEPT_POST_LEADER" />
      </el-select>
    </el-form-item>
    <el-form-item label="处理用户" v-if="formData.groupType === 'ASSIGNEE'">
      <TagSelect v-model="userTaskForm.assignee">
        <el-button slot="append" class="append-add" type="default" icon="el-icon-plus" @click="onSelectAssignee(false)" />
      </TagSelect>
    </el-form-item>
    <el-form-item label="候选用户" v-if="formData.groupType === 'USERS'">
      <TagSelect v-model="userTaskForm.candidateUsers">
        <el-button slot="append" class="append-add" type="default" icon="el-icon-plus" @click="onSelectCandidateUsers(true)" />
      </TagSelect>
    </el-form-item>
    <el-form-item v-if="formData.groupType === 'ROLE'" label="候选角色">
      <el-select v-model="candidateGroupIds" placeholder="" :multiple="true" @change="onSelectRoleChange">
        <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id" />
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
import { SysPostController, DictionaryController } from '@/api';
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
  inject: ['flowEntry', 'prefix'],
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
        groupType: 'ASSIGNEE'
      },
      roleList: undefined,
      groupList: undefined,
      groupMap: new Map(),
      postList: [],
      deptPostList: [],
      postMap: new Map(),
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
    loadSysRoleList () {
      return new Promise((resolve, reject) => {
        DictionaryController.dictSysRole(this, {}).then(res => {
          this.roleList = res.getList();
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
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
    loadSysPostList () {
      this.postMap = new Map();
      return new Promise((resolve, reject) => {
        SysPostController.list(this, {}).then(res => {
          this.postList = res.data.dataList;
          this.postList.forEach(item => {
            this.postMap.set(item.postId, item);
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
        postList: this.postList,
        usedIdList: usedIdList
      }).then(res => {
        this.userTaskForm.candidateGroups = '';
        if (Array.isArray(res)) {
          if (!Array.isArray(this.candidateGroupIds)) this.candidateGroupIds = [];
          res.forEach(item => {
            let temp = this.getDeptPostItem(item);
            if (findItemFromList(this.candidateGroupIds, item.id, 'id') == null) {
              this.candidateGroupIds.push({
                ...item,
                name: `${temp.deptName} / ${temp.postName}`
              });
            }
          });
          this.updateDeptPost();
        }
        this.userTaskForm.candidateGroups = Array.isArray(this.candidateGroupIds) ? this.candidateGroupIds.map(item => item.id).join(',') : '';
      }).catch(e => {});
    },
    getDeptPostItem (item) {
      let deptName;
      switch (item.deptType) {
        case 'allDeptPost':
          deptName = '全部';
          break;
        case 'selfDeptPost':
          deptName = '本部门';
          break;
        case 'upDeptPost':
          deptName = '上级部门';
          break;
        case 'deptPost':
          deptName = (this.deptPostMap.get(item.deptPostId) || {}).deptName || '未知岗位';
          break;
      }
      let postName = item.deptType === 'deptPost' ? ((this.deptPostMap.get(item.deptPostId) || {}).postShowName || '未知岗位') :
        ((this.postMap.get(item.postId) || {}).postName || '未知岗位');

      return {
        deptName,
        postName
      }
    },
    onSelectCandidatGroups () {
      let usedIdList = this.userTaskForm.candidateGroups ? this.userTaskForm.candidateGroups.split(',') : [];
      if (this.formData.groupType === 'DEPT') {
        this.handlerDeptChange(usedIdList);
      } else {
        this.handlerPostChange(usedIdList);
      }
    },
    onSelectRoleChange (value) {
      this.$nextTick(() => {
        this.userTaskForm.candidateGroups = Array.isArray(value) ? value.join(',') : '';
      });
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
          groupType: formObj.groupType || 'ASSIGNEE'
        }
      } else {
        this.formData = {
          groupType: 'ASSIGNEE'
        }
      }
      for (let key in this.defaultTaskForm) {
        let value;
        if (key === "candidateUsers" || key === "candidateGroups") {
          value = (window.bpmnInstances.bpmnElement || {}).businessObject[key] || this.defaultTaskForm[key];
          if (key === "candidateGroups" && value) {
            this.candidateGroupIds = value.split(',');
            if (Array.isArray(this.candidateGroupIds) && this.formData.groupType === 'DEPT') {
              this.candidateGroupIds = this.candidateGroupIds.map(item => {
                return this.groupMap.get(item);
              }).filter(item => item != null);
            }
          }
        } else {
          value = (window.bpmnInstances.bpmnElement || {}).businessObject[key] || this.defaultTaskForm[key];
        }
        this.$set(this.userTaskForm, key, value);
      }
      // 岗位
      if (this.formData.groupType === 'POST') {
        let elExtensionElements = window.bpmnInstances.bpmnElement.businessObject.get("extensionElements") ||
          window.bpmnInstances.moddle.create("bpmn:ExtensionElements", { values: [] });
        this.deptPostListElement = elExtensionElements.values.filter(ex => ex.$type === `${this.prefix}:DeptPostList`)?.[0] ||
          window.bpmnInstances.moddle.create(`${this.prefix}:DeptPostList`, { deptPostList: [] });
        this.candidateGroupIds = this.deptPostListElement.deptPostList.map(item => {
          item.deptType = item.type;
          item.type = undefined;
          let temp = this.getDeptPostItem({
            ...item
          });
          if (temp) {
            return {
              ...item,
              name: `${temp.deptName} / ${temp.postName}`
            }
          }
        });
      }
    },
    updateFormKey () {
      if (this.formData == null) return;
      let formKeyString = JSON.stringify({
        formId: this.flowEntry().bindFormType === this.SysFlowEntryBindFormType.ONLINE_FORM ? this.formData.formId : undefined,
        routerName: this.flowEntry().bindFormType === this.SysFlowEntryBindFormType.ONLINE_FORM ? undefined : this.formData.routerName,
        readOnly: !this.formData.editable,
        groupType: this.formData.groupType || 'ASSIGNEE'
      });
      window.bpmnInstances.modeling.updateProperties(window.bpmnInstances.bpmnElement, { formKey: formKeyString });
    },
    onGroupTypeChange () {
      this.userTaskForm.assignee = undefined;
      this.userTaskForm.candidateUsers = undefined;
      this.candidateGroupIds = [];
      this.userTaskForm.candidateGroups = '';
      this.updateFormKey();
    },
    updateDeptPost () {
      // 岗位
      if (this.formData.groupType === 'POST') {
        let elExtensionElements = window.bpmnInstances.bpmnElement.businessObject.get("extensionElements")  || window.bpmnInstances.moddle.create("bpmn:ExtensionElements", { values: [] });
        let otherExtensions = elExtensionElements.values.filter(ex => ex.$type !== `${this.prefix}:DeptPostList`);
        if (this.deptPostListElement == null) {
          this.deptPostListElement = window.bpmnInstances.moddle.create(`${this.prefix}:DeptPostList`, { deptPostList: [] });
        }
        this.deptPostListElement.deptPostList = this.candidateGroupIds.map(item => {
          return window.bpmnInstances.moddle.create(`${this.prefix}:DeptPost`, {
            id: item.id,
            type: item.deptType,
            postId: item.postId,
            deptPostId: item.deptPostId
          });
        });
        const newElExtensionElements = window.bpmnInstances.moddle.create(`bpmn:ExtensionElements`, {
          values: otherExtensions.concat(this.deptPostListElement)
        });
        // 更新到元素上
        window.bpmnInstances.modeling.updateProperties(window.bpmnInstances.bpmnElement, {
          extensionElements: newElExtensionElements
        });
      }
    },
    updateElementTask(key) {
      let taskAttr = Object.create(null);
      if (key === "candidateUsers" || key === "candidateGroups") {
        taskAttr[key] = this.userTaskForm[key] || null;
        let type = key === "candidateUsers" ? "USERS" : this.formData.groupType;
        this.updateUserCandidateGroups(type, taskAttr[key]);
      } else {
        taskAttr[key] = this.userTaskForm[key] || null;
      }
      window.bpmnInstances.modeling.updateProperties(window.bpmnInstances.bpmnElement, taskAttr);
    },
    updateUserCandidateGroups (type, value) {
      console.log(type, value);
      let elExtensionElements = window.bpmnInstances.bpmnElement.businessObject.get("extensionElements") || window.bpmnInstances.moddle.create("bpmn:ExtensionElements", { values: [] });
      let otherExtensions = elExtensionElements.values.filter(ex => ex.$type !== `${this.prefix}:UserCandidateGroups`);
      let userCandidateGroupsElement = window.bpmnInstances.moddle.create(`${this.prefix}:UserCandidateGroups`, {
        type: type,
        value: value
      });
      if (type !== 'POST' && value != null && value !== '') otherExtensions.push(userCandidateGroupsElement)
      const newElExtensionElements = window.bpmnInstances.moddle.create(`bpmn:ExtensionElements`, {
        values: otherExtensions
      });
      // 更新到元素上
      window.bpmnInstances.modeling.updateProperties(window.bpmnInstances.bpmnElement, {
        extensionElements: newElExtensionElements
      });
    }
  },
  watch: {
    id: {
      immediate: true,
      handler(newValue) {
        if (this.roleList == null) {
          this.loadSysRoleList().then(res => {
            let httpCall = [
              this.loadDeptWidgetDropdownList(),
              this.loadSysPostList(),
              this.loadDeptPostList()
            ];
            return Promise.all(httpCall);
          }).then(res => {
            this.bpmnElement = window.bpmnInstances.bpmnElement;
            this.$nextTick(() => this.resetTaskForm());
          }).catch(e => {
            this.roleList = undefined;
          });
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
        if (this.formData.groupType === 'ROLE') {
          this.userTaskForm.candidateGroups = Array.isArray(this.candidateGroupIds) ? this.candidateGroupIds.join(',') : '';
        } else if (this.formData.groupType === 'POST') {
          this.updateDeptPost();
        } else {
          this.userTaskForm.candidateGroups = Array.isArray(this.candidateGroupIds) ? this.candidateGroupIds.map(item => item.id).join(',') : '';
        }
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
    this.postMap = null;
    this.deptPostMap = null;
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