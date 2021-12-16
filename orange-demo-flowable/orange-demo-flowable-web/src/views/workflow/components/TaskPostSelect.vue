<template>
  <el-container>
    <el-main style="background-color: white;">
      <el-card class="base-card" shadow="never" :body-style="{ padding: '0px' }" style="border: none;">
        <div slot="header" class="base-card-header">
          <el-row type="flex" align="middle">
            <el-radio-group v-model="formData.deptType" size="mini" @change="formData.deptId = undefined">
              <el-radio-button label="allDeptPost">全部</el-radio-button>
              <el-radio-button label="selfDeptPost">本部门</el-radio-button>
              <el-radio-button label="upDeptPost">上级部门</el-radio-button>
              <el-radio-button label="deptPost">指定部门</el-radio-button>
            </el-radio-group>
            <el-cascader v-model="formData.deptId" :clearable="true"
              size="mini" placeholder="选择部门" v-show="formData.deptType === 'deptPost'"
              :props="{value: 'id', label: 'name', checkStrictly: true}"
              :options="deptList">
            </el-cascader>
            <!--
            <el-select v-model="formData.deptId" size="mini" placeholder="选择部门" v-show="formData.deptType === 'deptPost'" style="margin-left: 10px;">
              <el-option v-for="item in deptList" :key="item.id"
                :label="item.name" :value="item.id"
              />
            </el-select>
            -->
          </el-row>
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
          <el-table-column label="岗位名称">
            <template slot-scope="scope">
              <span>{{scope.row.postShowName || scope.row.postName}}</span>
            </template>
          </el-table-column>
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
export default {
  name: 'taskPostSelect',
  props: {
    deptList: {
      type: Array
    },
    postList: {
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
      formData: {
        deptType: 'allDeptPost',
        deptId: undefined,
        postId: undefined
      },
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
    handleSelectionChange (values) {
      this.selectPost = values;
    },
    onAddPostClick () {
      this.onCancel(true, this.selectPost.map(item => {
        return {
          id: `${this.formData.deptType}__${item.deptPostId || item.postId}`,
          deptType: this.formData.deptType,
          deptPostId: this.formData.deptType === 'deptPost' ? item.deptPostId : undefined,
          postId: this.formData.deptType === 'deptPost' ? undefined : item.postId
        }
      }));
    }
  },
  computed: {
    getDeptTreeData () {
      return this.deptList;
    },
    getValidDeptPostList () {
      if (this.formData.deptType !== 'deptPost') {
        return this.postList || [];
      } else {
        return (this.deptPostList || []).filter(item => {
          return item.deptId === (Array.isArray(this.formData.deptId) ? this.formData.deptId[this.formData.deptId.length - 1] : this.formData.deptId);
        });
      }
    }
  },
  mounted () {
  }
}
</script>

<style scoped>
  .dept-tree >>> .el-tree-node__content {
    height: 35px;
  }
</style>
