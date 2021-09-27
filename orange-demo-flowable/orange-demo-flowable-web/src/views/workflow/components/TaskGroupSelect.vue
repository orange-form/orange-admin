<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row type="flex" justify="end">
        <el-button type="primary" size="mini" @click="onSubmit" :disabled="!canCommit">
            添加分组
          </el-button>
      </el-row>
    </el-form>
    <el-row style="margin-top: 18px;">
      <el-col :span="24">
        <el-table :data="allGroupList" size="mini" height="452px"
          header-cell-class-name="table-header-gray"
          row-key="id" :default-expand-all="true"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50px"
            header-align="center" align="center"
            :selectable="canSelect"
          />
          <el-table-column label="部门名称" prop="name" />
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { TableWidget } from '@/utils/widget.js';
import { DictionaryController } from '@/api';

export default {
  name: 'taskGroupSelect',
  props: {
    allGroupList: {
      type: Array,
      required: true
    },
    // 已经被使用的分组列表
    usedIdList: {
      type: Array
    }
  },
  data () {
    return {
      multiSelectGroup: [],
      taskGroupListWidget: new TableWidget(this.loadSysDeptData, this.loadSysDeptVerify, false, false, 'createTime', 1)
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
        return this.usedIdList.indexOf(row.id) === -1;
      } else {
        return true;
      }
    },
    onSubmit () {
      this.onCancel(true, this.multiSelectGroup);
    },
    handleSelectionChange (values) {
      this.multiSelectGroup = values;
    },
    loadSysDeptData (params) {
      return new Promise((resolve, reject) => {
        DictionaryController.dictSysDeptByParentId(this, {}).then(res => {
          resolve({
            dataList: res.getList(),
            totalCount: res.getList().length
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    loadSysDeptVerify () {
      return true;
    },
    refresh (reloadData = false) {
      // 重新获取数据组件的数据
      if (reloadData) {
        this.taskGroupListWidget.refreshTable(true, 1);
      } else {
        this.taskGroupListWidget.refreshTable();
      }
    }
  },
  computed: {
    canCommit () {
      return this.multiSelectGroup.length > 0;
    }
  },
  mounted () {
    this.refresh();
  }
}
</script>

<style>
</style>
