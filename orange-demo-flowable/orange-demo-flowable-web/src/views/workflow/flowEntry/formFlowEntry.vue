<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="流程分类">
          <el-select class="filter-item" v-model="formFlowEntry.formFilter.categoryId"
            :clearable="true" filterable placeholder="流程分类"
            :loading="formFlowEntry.categoryIdWidget.loading"
            @visible-change="formFlowEntry.categoryIdWidget.onVisibleChange"
          >
            <el-option v-for="item in formFlowEntry.categoryIdWidget.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="流程名称">
          <el-input class="filter-item" v-model="formFlowEntry.formFilter.processDefinitionName"
            :clearable="true" placeholder="流程名称" />
        </el-form-item>
        <el-form-item label="流程标识">
          <el-input class="filter-item" v-model="formFlowEntry.formFilter.processDefinitionKey"
            :clearable="true" placeholder="流程标识" />
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select class="filter-item" v-model="formFlowEntry.formFilter.status" :clearable="true" filterable
            placeholder="发布状态">
            <el-option v-for="item in SysFlowEntryPublishedStatus.getList()" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormFlowEntry(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formFlowEntry:formFlowEntry:update')"
          @click="onAddFlowEntryClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="flowEntry" :data="formFlowEntry.flowEntryWidget.dataList" size="mini" @sort-change="formFlowEntry.flowEntryWidget.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formFlowEntry.flowEntryWidget.getTableIndex" />
          <el-table-column label="流程名称" prop="processDefinitionName">
          </el-table-column>
          <el-table-column label="流程标识" prop="processDefinitionKey">
          </el-table-column>
          <el-table-column label="流程分类" prop="flowCategory.name">
          </el-table-column>
          <el-table-column label="发布状态" prop="status">
            <template slot-scope="scope">
              <el-tag size="mini" :type="scope.row.status === SysFlowEntryPublishedStatus.PUBLISHED ? 'success' : 'warning'">
                {{SysFlowEntryPublishedStatus.getValue(scope.row.status)}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="流程主版本" prop="mainFlowEntryPublish" header-align="center" align="center">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.mainFlowEntryPublish" size="mini" type="primary" effect="dark">
                {{'V:' + scope.row.mainFlowEntryPublish.publishVersion}}
              </el-tag>
              <el-tag v-if="scope.row.mainFlowEntryPublish" size="mini" effect="dark" style="margin-left: 10px"
                :type="scope.row.mainFlowEntryPublish.activeStatus ? 'success' : 'danger'"
              >
                {{scope.row.mainFlowEntryPublish.activeStatus ? '激活' : '挂起'}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="最近发布时间" prop="lastestPublishTime" sortable="custom" />
          <el-table-column label="创建时间" prop="createTime" sortable="custom" />
          <el-table-column label="操作" fixed="right" width="250px">
            <template slot-scope="scope">
              <el-button class="table-btn success" @click.stop="onStartFlowEntryClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formFlowEntry:formFlowEntry:start') ||
                  !(scope.row.mainFlowEntryPublish && scope.row.mainFlowEntryPublish.activeStatus)">
                启动
              </el-button>
              <el-button class="table-btn success" @click.stop="onEditFlowEntryClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formFlowEntry:formFlowEntry:update')">
                编辑
              </el-button>
              <el-button @click.stop="onPublishedClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formFlowEntry:formFlowEntry:update')">
                发布
              </el-button>
              <el-button @click.stop="onPublishedEntryListClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formFlowEntry:formFlowEntry:update')">
                版本管理
              </el-button>
              <el-button class="table-btn delete" @click.stop="onDeleteFlowEntryClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formFlowEntry:formFlowEntry:update')">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formFlowEntry.flowEntryWidget.totalCount"
            :current-page="formFlowEntry.flowEntryWidget.currentPage"
            :page-size="formFlowEntry.flowEntryWidget.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formFlowEntry.flowEntryWidget.onCurrentPageChange"
            @size-change="formFlowEntry.flowEntryWidget.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
    <FormEditFlowEntry v-if="showFlowEntryDesign" :flowEntry="currentFlowEntry" @close="onEditFlowEntryClose" />
  </div>
</template>

<script>
import '@/staticDict/flowStaticDict.js';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { FlowEntryController, FlowDictionaryController, FlowOperationController } from '@/api/flowController.js';
import FormPublishedFlowEntry from './formPublishedFlowEntry.vue';
import FormEditFlowEntry from './formEditFlowEntry.vue';

export default {
  name: 'formFlowEntry',
  components: {
    FormEditFlowEntry
  },
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      showFlowEntryDesign: false,
      currentFlowEntry: undefined,
      formFlowEntry: {
        formFilter: {
          status: undefined,
          categoryId: undefined,
          processDefinitionName: undefined,
          processDefinitionKey: undefined
        },
        formFilterCopy: {
          status: undefined,
          categoryId: undefined,
          processDefinitionName: undefined,
          processDefinitionKey: undefined
        },
        categoryIdWidget: new DropdownWidget(this.loadCategoryIdDropdownList),
        flowEntryWidget: new TableWidget(this.loadFlowEntryWidgetData, this.loadFlowEntryVerify, true, false, 'entryId', 1),
        isInit: false
      }
    }
  },
  methods: {
    onEditFlowEntryClose () {
      this.showFlowEntryDesign = false;
      this.currentFlowEntry = null;
      this.refreshFormFlowEntry();
    },
    /**
     * FlowEntry数据获取函数，返回Promise
     */
    loadFlowEntryWidgetData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        flowEntryDtoFilter: {
          categoryId: this.formFlowEntry.formFilterCopy.categoryId,
          processDefinitionName: this.formFlowEntry.formFilterCopy.processDefinitionName,
          processDefinitionKey: this.formFlowEntry.formFilterCopy.processDefinitionKey,
          status: this.formFlowEntry.formFilterCopy.status
        }
      }
      return new Promise((resolve, reject) => {
        FlowEntryController.list(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * FlowEntry数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadFlowEntryVerify () {
      this.formFlowEntry.formFilterCopy.categoryId = this.formFlowEntry.formFilter.categoryId;
      this.formFlowEntry.formFilterCopy.processDefinitionName = this.formFlowEntry.formFilter.processDefinitionName;
      this.formFlowEntry.formFilterCopy.processDefinitionKey = this.formFlowEntry.formFilter.processDefinitionKey;
      this.formFlowEntry.formFilterCopy.status = this.formFlowEntry.formFilter.status;
      return true;
    },
    /**
     * 流程分类下拉数据获取函数
     */
    loadCategoryIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        FlowDictionaryController.dictFlowCategory(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 流程分类选中值改变
     */
    onCategoryIdValueChange (value) {
    },
    /**
     * 更新流程设计
     */
    refreshFormFlowEntry (reloadData = false) {
      if (reloadData) {
        this.formFlowEntry.flowEntryWidget.refreshTable(true, 1);
      } else {
        this.formFlowEntry.flowEntryWidget.refreshTable();
      }
      if (!this.formFlowEntry.isInit) {
        // 初始化下拉数据
      }
      this.formFlowEntry.isInit = true;
    },
    /**
     * 启动
     */
    onStartFlowEntryClick (row) {
      let params = {
        processDefinitionKey: row.processDefinitionKey
      }
      FlowOperationController.viewInitialTaskInfo(this, params).then(res => {
        if (res.data && res.data.taskType === this.SysFlowTaskType.USER_TASK && res.data.assignedMe) {
          this.$router.push({
            name: res.data.routerName || 'handlerFlowTask',
            query: {
              processDefinitionKey: row.processDefinitionKey,
              formId: res.data.formId,
              routerName: res.data.routerName,
              readOnly: res.data.readOnly,
              taskName: '启动流程',
              flowEntryName: row.processDefinitionName,
              operationList: (res.data.operationList || []).filter(item => item.type !== this.SysFlowTaskOperationType.CO_SIGN),
              variableList: res.data.variableList
            }
          });
        } else {
          FlowOperationController.startOnly(this, {
            processDefinitionKey: row.processDefinitionKey
          }).then(res => {
            this.$message.success('启动成功！');
          }).catch(e => {});
        }
      }).catch(e => {});
    },
    /**
     * 新建
     */
    onAddFlowEntryClick () {
      this.showFlowEntryDesign = true;
    },
    /**
     * 编辑
     */
    onEditFlowEntryClick (row) {
      this.currentFlowEntry = row;
      this.showFlowEntryDesign = true;
    },
    /**
     * 发布
     */
    onPublishedClick (row) {
      this.$confirm('是否发布当前工作流设计？').then(res => {
        if (
          row.entryId == null
        ) {
          this.$message.error('请求失败，发现必填参数为空！');
          return;
        }
        let params = {
          entryId: row.entryId
        };

        FlowEntryController.publish(this, params).then(res => {
          this.$message.success('发布成功');
          this.refreshFormFlowEntry();
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 版本管理
     */
    onPublishedEntryListClick (row) {
      this.$dialog.show('版本管理', FormPublishedFlowEntry, {
        area: ['1200px', '750px']
      }, {
        flowEntry: row
      }).then(res => {
        this.refreshFormFlowEntry();
      }).catch(e => {
        this.refreshFormFlowEntry();
      });
    },
    /**
     * 删除
     */
    onDeleteFlowEntryClick (row) {
      if (
        row.entryId == null
      ) {
        this.$message.error('请求失败，发现必填参数为空！');
        return;
      }
      let params = {
        entryId: row.entryId
      };

      this.$confirm('是否删除此流程？').then(res => {
        FlowEntryController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formFlowEntry.flowEntryWidget.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormFlowEntry();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormFlowEntry();
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
