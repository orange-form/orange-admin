<template>
  <el-container class="advance-query-form">
    <el-aside width="300px">
      <el-card class="base-card" shadow="never" :body-style="{ padding: '0px' }" style="border: none;">
        <div slot="header" class="base-card-header">
          <span>字典列表</span>
        </div>
        <el-scrollbar :style="{'height': (getMainContextHeight - 51) + 'px'}" class="custom-scroll">
          <el-tree :data="dictList" :props="{label: 'name'}" node-key="variableName" :highlight-current="true"
            :current-node-key="(dictList[0] || {}).variableName" @node-click="onDictChange">
            <div class="module-node-item" slot-scope="{ data }">
              <span style="padding-left: 24px;">{{data.name}}</span>
            </div>
          </el-tree>
        </el-scrollbar>
      </el-card>
    </el-aside>
    <el-main style="margin-left: 15px; background-color: white; padding: 20px;">
      <el-form label-width="120px" size="mini" label-position="left" @submit.native.prevent>
        <filter-box :item-width="350">
          <el-form-item v-if="dirtyCount > 0" label="失效缓存数量：">
            <span style="color: #F56C6C;">{{dirtyCount}}</span>
          </el-form-item>
          <el-button slot="operator" type="primary" size="mini" :plain="true"
            :disabled="!checkPermCodeExist('formSysDict:fragmentSysDict:reloadCache') || currentDict == null"
            @click="onRefreshCacheData">
            同步缓存
          </el-button>
          <el-button slot="operator" type="primary" size="mini"
            :disabled="!checkPermCodeExist('formSysDict:fragmentSysDict:add') || currentDict == null"
            @click="onAddDictData">
            添加数据
          </el-button>
        </filter-box>
      </el-form>
      <el-row>
        <el-col :span="24">
          <el-table :data="getCurrentDictData" size="mini" header-cell-class-name="table-header-gray"
            :row-style="tableRowStyle"
            :height="(getMainContextHeight - 90) + 'px'" row-key="id">
            <el-table-column label="ID" prop="id" />
            <el-table-column label="字典名称" prop="name">
              <template slot-scope="scope">
                <span>{{scope.row.name}}</span>
                <el-tag v-if="scope.row.dirty" size="mini" effect="dark" type="warning"
                  style="margin-left: 15px;">
                  缓存失效
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150px">
              <template slot-scope="scope">
                <el-button type="text" size="mini" :disabled="!checkPermCodeExist('formSysDict:fragmentSysDict:update')" @click="onUpdateDictDataClick(scope.row)">编辑</el-button>
                <el-button type="text" size="mini" :disabled="!checkPermCodeExist('formSysDict:fragmentSysDict:delete')" @click="onDeleteDictDataClick(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex';
import { treeDataTranslate, findItemFromList } from '@/utils';
/* eslint-disable-next-line */
import { DictionaryController } from '@/api';
import editDict from '@/views/upms/formEditDict';

export default {
  name: 'formDictManagement',
  data () {
    return {
      dictList: [
      ],
      dirtyCount: 0,
      currentDict: undefined,
      currentDictDataList: []
    }
  },
  methods: {
    tableRowStyle ({row, rowIndex}) {
      if (row.dirty) {
        return {
          background: '#FFE1E1'
        }
      }
    },
    updateDictData () {
      this.currentDictDataList = [];
      this.dirtyCount = 0;
      this.currentDict.listApi(this).then(res => {
        let cachedMap = new Map();
        if (Array.isArray(res.cachedResultList)) {
          res.cachedResultList.forEach(item => {
            cachedMap.set(item.id, item);
          });
        }
        if (Array.isArray(res.fullResultList)) {
          res.fullResultList.forEach(item => {
            let cachedItem = cachedMap.get(item.id);
            if (cachedItem == null || cachedItem.name !== item.name) {
              item.dirty = true;
              this.dirtyCount++;
            }
          });
        }
        cachedMap = null;
        if (this.currentDict.treeFlag) {
          this.currentDictDataList = treeDataTranslate(res.fullResultList, 'id', 'parentId');
        } else {
          this.currentDictDataList = res.fullResultList;
        }
      }).catch(e => {});
    },
    onDictChange (data) {
      if (this.currentDict === data) return;
      this.currentDict = findItemFromList(this.dictList, (data || {}).variableName, 'variableName');
      this.currentDictDataList = [];
      if (this.currentDict == null) {
        this.$message.error('没有找到相关字典');
        return;
      }
      this.updateDictData();
    },
    onRefreshCacheData () {
      this.$confirm('是否同步缓存？').then(res => {
        return this.currentDict.reloadCachedDataApi(this);
      }).then(res => {
        this.$message.success('同步成功');
        this.updateDictData();
      }).catch(e => {});
    },
    onAddDictData () {
      this.$dialog.show(`新建字典数据 - [${this.currentDict.name}]`, editDict, {
        area: '500px'
      }, {
        dictInfo: this.currentDict,
        dictData: this.currentDict.treeFlag ? this.currentDictDataList : []
      }).then(res => {
        this.updateDictData();
      }).catch(e => {});
    },
    onUpdateDictDataClick (row) {
      this.$dialog.show(`编辑字典数据 - [${this.currentDict.name}]`, editDict, {
        area: '500px'
      }, {
        dictInfo: this.currentDict,
        currentData: row,
        dictData: this.currentDict.treeFlag ? this.currentDictDataList : []
      }).then(res => {
        this.updateDictData();
      }).catch(e => {});
    },
    onDeleteDictDataClick (row) {
      this.$confirm('是否删除此字典数据？').then(res => {
        let params = {};
        params[this.currentDict.idKey] = row.id;
        return this.currentDict.deleteApi(this, params);
      }).then(res => {
        this.$message.success('删除成功');
        this.updateDictData();
      }).catch(e => {});
    }
  },
  computed: {
    getCurrentDictData () {
      return this.currentDictDataList;
    },
    ...mapGetters(['getMainContextHeight'])
  },
  mounted () {
    this.onDictChange(this.dictList[0]);
  }
}
</script>

<style scoped>
  >>> .el-tree-node__content {
    height: 35px;
  }

  >>> .el-tree-node__content .is-leaf {
    display: none;
  }

  .module-node-item {
    width: 100%;
    height: 35px;
    line-height: 35px;
  }
</style>
