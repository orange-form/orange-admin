<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="字典名称">
          <el-input class="filter-item" v-model="formOnlineDict.formFilter.dictName"
            :clearable="true" placeholder="字典名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshOnlineDict(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini"
          @click="onFormCreateDictClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="class" :data="formOnlineDict.dict.impl.dataList" size="mini" @sort-change="formOnlineDict.dict.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formOnlineDict.dict.impl.getTableIndex" />
          <el-table-column label="字典名称" prop="dictName" />
          <el-table-column label="字典类型" prop="dictTypeDictMap.name">
            <template slot-scope="scope">
              <el-tag size="mini" :type="getDictTypeTagType(scope.row.dictType)">
                {{scope.row.dictTypeDictMap.name}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="是否树字典">
            <template slot-scope="scope">
              <el-tag size="mini" :type="scope.row.treeFlag ? 'success' : 'danger'">
                {{scope.row.treeFlag ? '是' : '否'}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="150px">
            <template slot-scope="scope">
              <el-button class="table-btn success" @click.stop="onFormEditDictClick(scope.row)" type="text" size="mini">
                编辑
              </el-button>
              <el-button class="table-btn delete" @click.stop="onFormDeleteDictClick(scope.row)" type="text" size="mini">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formOnlineDict.dict.impl.totalCount"
            :current-page="formOnlineDict.dict.impl.currentPage"
            :page-size="formOnlineDict.dict.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formOnlineDict.dict.impl.onCurrentPageChange"
            @size-change="formOnlineDict.dict.impl.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import '@/staticDict/onlineStaticDict.js';
/* eslint-disable-next-line */
import { TableWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { OnlineDictController } from '@/api/onlineController.js';
import EditOnlineDict from './editOnlineDict.vue';

export default {
  name: 'formOnlineDict',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formOnlineDict: {
        formFilter: {
          dictName: undefined
        },
        formFilterCopy: {
          dictName: undefined
        },
        dict: {
          impl: new TableWidget(this.loadDynamicDictData, this.loadDynamicDictVerify, true, false)
        },
        isInit: false
      }
    }
  },
  methods: {
    getDictTypeTagType (type) {
      switch (type) {
        case this.SysOnlineDictType.TABLE: return 'success';
        case this.SysOnlineDictType.URL: return 'primary';
        case this.SysOnlineDictType.STATIC: return 'warning';
        case this.SysOnlineDictType.CUSTOM: return 'danger';
        default:
          return 'info';
      }
    },
    /**
     * 获取动态表单字典列表
     */
    loadDynamicDictData (params) {
      params = {
        ...params,
        onlineDictDtoFilter: {
          dictName: this.formOnlineDict.formFilterCopy.dictName
        }
      }
      return new Promise((resolve, reject) => {
        OnlineDictController.list(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    loadDynamicDictVerify () {
      this.formOnlineDict.formFilterCopy.dictName = this.formOnlineDict.formFilter.dictName;
      return true;
    },
    refreshOnlineDict (reloadData) {
      if (reloadData) {
        this.formOnlineDict.dict.impl.refreshTable(true, 1);
      } else {
        this.formOnlineDict.dict.impl.refreshTable();
      }
      if (!this.formOnlineDict.isInit) {
        // 初始化下拉数据
      }
      this.formOnlineDict.isInit = true;
    },
    onFormCreateDictClick () {
      this.$dialog.show('新建字典', EditOnlineDict, {
        area: '600px',
        offset: '100px'
      }).then(res => {
        this.formOnlineDict.dict.impl.refreshTable();
      }).catch(e => {
        console.log(e);
      });
    },
    onFormEditDictClick (row) {
      this.$dialog.show(row ? '编辑字典' : '新建字典', EditOnlineDict, {
        area: '600px',
        offset: '100px'
      }, {
        dictId: row.dictId
      }).then(res => {
        this.formOnlineDict.dict.impl.refreshTable();
      }).catch(e => {
      });
    },
    onFormDeleteDictClick (row) {
      this.$confirm('是否删除此字典？').then(res => {
        let params = {
          dictId: row.dictId
        }

        return OnlineDictController.delete(this, params);
      }).then(res => {
        this.$message.success('删除成功！');
        this.formOnlineDict.dict.impl.refreshTable();
      }).catch(e => {});
    },
    onResume () {
      this.refreshOnlineDict();
    },
    initFormData () {
    },
    formInit () {
      this.refreshOnlineDict();
    }
  },
  mounted () {
    // 初始化页面数据
    this.formInit();
  },
  watch: {
  }
}
</script>

<style>
</style>
