<template>
  <el-container class="advance-query-form">
    <el-aside width="300px">
      <el-card class="base-card" shadow="never" :body-style="{ padding: '0px' }" style="border: none;">
        <div slot="header" class="base-card-header">
          <span style="font-size: 16px; font-weight: 500; color: #282828;">所属部门</span>
        </div>
        <el-scrollbar :style="{height: '500px'}" class="custom-scroll">
          <el-tree class="dept-tree" ref="deptTree" :data="getDeptTreeData" :props="{label: 'name'}"
            node-key="id" :default-expand-all="true" :expand-on-click-node="false"
            @node-click="onDeptNodeClick"
            :highlight-current="true">
            <div slot-scope="{ data }" style="height: 35px; line-height: 35px;">
              {{data.name}}
            </div>
          </el-tree>
        </el-scrollbar>
      </el-card>
    </el-aside>
    <el-main style="margin-left: 15px; background-color: white;">
      <el-card class="base-card" shadow="never" :body-style="{ padding: '0px' }" style="border: none;">
        <div slot="header" class="base-card-header">
          <span style="font-size: 16px; font-weight: 500; color: #282828;">岗位列表</span>
          <div class="base-card-operation">
            <el-button type="primary" size="mini"
              :disabled="selectPost.length <= 0"
              @click="onAddPostClick()"
            >
              添加岗位
            </el-button>
          </div>
        </div>
        <el-table :data="getValidDeptPostList" size="mini" height="500px"
          header-cell-class-name="table-header-gray"
          row-key="deptPostId"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50px" :selectable="canSelect" />
          <el-table-column label="岗位名称" prop="postShowName" />
          <el-table-column label="领导岗位">
            <template slot-scope="scope">
              <el-tag size="mini" :type="scope.row.leaderPost ? 'success' : 'danger'">
                {{scope.row.leaderPost ? '是' : '否'}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="岗位级别" prop="level" />
        </el-table>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
import { DictionaryController } from '@/api/index.js';

export default {
  name: 'taskPostSelect',
  props: {
    deptList: {
      type: Array
    },
    deptPostList: {
      type: Array
    },
    usedIdList: {
      type: Array
    }
  },
  data () {
    return {
      currentDept: undefined,
      // deptList: [],
      selectPost: []
    }
  },
  methods: {
    onCancel (isSuccess, data) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess, data);
      }
    },
    canSelect (row) {
      if (Array.isArray(this.usedIdList) && this.usedIdList.length > 0) {
        return this.usedIdList.indexOf(row.deptPostId) === -1;
      } else {
        return true;
      }
    },
    loadSysDeptData () {
      DictionaryController.dictSysDept(this, {}).then(res => {
        this.deptList = res.getList();
        this.currentDept = this.deptList[0];
        if (this.currentDept) {
          this.$nextTick(() => {
            this.$refs.deptTree.setCurrentKey(this.currentDept.id);
          });
        }
      }).catch(e => {});
    },
    onDeptNodeClick (data) {
      this.selectPost = [];
      this.currentDept = data;
    },
    handleSelectionChange (values) {
      this.selectPost = values;
    },
    onAddPostClick () {
      this.onCancel(true, this.selectPost);
    }
  },
  computed: {
    getDeptTreeData () {
      return this.deptList;
    },
    getValidDeptPostList () {
      if (Array.isArray(this.deptPostList)) {
        if (this.currentDept == null) {
          return this.deptPostList;
        } else {
          return this.deptPostList.filter(item => {
            return item.deptId === this.currentDept.id;
          });
        }
      } else {
        return [];
      }
    }
  },
  mounted () {
    // this.loadSysDeptData();
    if (Array.isArray(this.deptList)) {
      this.currentDept = this.deptList[0];
      if (this.currentDept) {
        this.$nextTick(() => {
          this.$refs.deptTree.setCurrentKey(this.currentDept.id);
        });
      }
    }
  }
}
</script>

<style scoped>
  .dept-tree >>> .el-tree-node__content {
    height: 35px;
  }
</style>
