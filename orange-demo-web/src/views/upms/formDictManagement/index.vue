<template>
  <div>
    <el-form label-width="75px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="字典列表">
          <el-select class="filter-item" v-model="currentDictId" placeholder="请选择字典" @change="onDictChange">
            <el-option v-for="item in dictList" :key="item.variableName" :label="item.name" :value="item.variableName"></el-option>
          </el-select>
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
        <el-table :data="getCurrentDictData" size="mini" header-cell-class-name="table-header-gray">
          <el-table-column label="ID" prop="id" />
          <el-table-column label="字典名称" prop="name" />
          <el-table-column label="操作" width="150px">
            <template slot-scope="scope">
              <el-button type="text" size="mini" :disabled="!checkPermCodeExist('formSysDict:fragmentSysDict:update')" @click="onUpdateDictDataClick(scope.row)">编辑</el-button>
              <el-button type="text" size="mini" :disabled="!checkPermCodeExist('formSysDict:fragmentSysDict:delete')" @click="onDeleteDictDataClick(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { findItemFromList } from '@/utils';
/* eslint-disable-next-line */
import { DictionaryController } from '@/api';
import editDict from '@/views/upms/formEditDict';

export default {
  name: 'systemDictManagement',
  data () {
    return {
      dictList: [
        {
          variableName: 'grade',
          name: '年级',
          nameKey: 'gradeName',
          idKey: 'gradeId',
          deletedKey: 'gradeIds',
          listApi: DictionaryController.dictGrade,
          addApi: DictionaryController.dictAddGrade,
          deleteApi: DictionaryController.dictDeleteGrade,
          batchDeleteApi: DictionaryController.dictBatchDeleteGrade,
          updateApi: DictionaryController.dictUpdateGrade,
          reloadCachedDataApi: DictionaryController.dictReloadGradeCachedData
        }
      ],
      currentDictId: undefined,
      currentDict: undefined,
      currentDictDataList: []
    }
  },
  methods: {
    updateDictData () {
      this.currentDictDataList = [];
      this.currentDict.listApi(this).then(res => {
        this.currentDictDataList = res.getList();
      }).catch(e => {});
    },
    onDictChange (value) {
      this.currentDict = findItemFromList(this.dictList, value, 'variableName');
      this.currentDictDataList = [];
      if (this.currentDict == null) {
        this.$message.error('没有找到相关字典');
        return;
      }
      this.updateDictData();
    },
    onRefreshCacheData () {
      this.$confirm('是否同步缓存？').then(res => {
        this.currentDict.reloadCachedDataApi(this).then(res => {
          this.$message.success('同步成功');
        }).catch(e => {});
      }).catch(e => {});
    },
    onAddDictData () {
      this.$dialog.show(`新建字典数据 - [${this.currentDict.name}]`, editDict, {
        area: '500px'
      }, {
        dictInfo: this.currentDict
      }).then(res => {
        this.updateDictData();
      }).catch(e => {});
    },
    onUpdateDictDataClick (row) {
      this.$dialog.show(`编辑字典数据 - [${this.currentDict.name}]`, editDict, {
        area: '500px'
      }, {
        dictInfo: this.currentDict,
        currentData: row
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
    }
  }
}
</script>
