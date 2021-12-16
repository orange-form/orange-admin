<template>
  <el-col :span="widgetConfig.span">
    <el-row :style="{'margin-bottom': widgetConfig.supportBottom === 1 ? '20px' : undefined}">
      <el-col :span="24" style="margin-bottom: 10px;" v-if="formType !== SysOnlineFormType.QUERY && formType !== SysOnlineFormType.WORK_ORDER">
        <el-row type="flex" justify="space-between">
          <div class="table-title" :style="{'border-left': '3px solid ' + widgetConfig.titleColor}">
            {{widgetConfig.showName}}
          </div>
          <div>
            <el-button size="mini"
              v-for="operation in getTableOperation(false)" :key="operation.id"
              :plain="operation.plain"
              :type="operation.btnType"
              @click.stop="onOperationClick(operation)">
              {{operation.name}}
            </el-button>
          </div>
        </el-row>
      </el-col>
      <el-col :span="24">
        <el-table size="mini" header-cell-class-name="table-header-gray" ref="tableImpl"
          :style="{height: (widgetConfig.tableInfo.height != null && widgetConfig.tableInfo.height !== '') ? widgetConfig.tableInfo.height + 'px' : undefined}"
          :height="(widgetConfig.tableInfo.height != null && widgetConfig.tableInfo.height !== '') ? widgetConfig.tableInfo.height + 'px' : undefined"
          :data="tableWidget.dataList" :row-key="primaryColumnName"
          @sort-change="tableWidget.onSortChange">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="tableWidget.getTableIndex" />
          <template v-for="tableColumn in widgetConfig.tableColumnList">
            <!-- Boolean类型的字段，使用el-tag去显示 -->
            <el-table-column v-if="(tableColumn.column || {}).objectFieldType === 'Boolean'" :key="tableColumn.dataFieldName"
              :label="tableColumn.showName" :width="tableColumn.columnWidth + 'px'"
              :prop="tableColumn.column.columnName"
              :sortable="tableColumn.sortable ? 'custom' : false"
            >
              <template slot-scope="scope">
                <el-tag size="mini" :type="scope.row[tableColumn.dataFieldName] ? 'success' : 'danger'">
                  {{scope.row[tableColumn.dataFieldName] ? '是' : '否'}}
                </el-tag>
              </template>
            </el-table-column>
            <!-- 普通非字典字段 -->
            <template v-else-if="(tableColumn.column || {}).dictInfo == null">
              <el-table-column :label="tableColumn.showName"
                v-if="tableColumn.column &&
                  (tableColumn.column.fieldKind === SysOnlineFieldKind.UPLOAD ||
                  tableColumn.column.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE)"
                :key="(tableColumn.column || {}).objectFieldName"
                :width="tableColumn.columnWidth + 'px'"
              >
                <template slot-scope="scope">
                  <template v-if="tableColumn.column.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE">
                    <el-image
                      v-for="item in parseTableUploadData(tableColumn, scope.row)"
                      :preview-src-list="getTablePictureList(tableColumn, scope.row)"
                      class="table-cell-image" :key="item.url" :src="item.url" fit="fill">
                      <div slot="error" class="table-cell-image">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                  </template>
                  <template v-else>
                    <a v-for="item in parseTableUploadData(tableColumn, scope.row)"
                      :key="item.url" href="javascript:void(0);" @click="downloadFile(item.url, item.name)">
                      {{item.name}}
                    </a>
                  </template>
                </template>
              </el-table-column>
              <el-table-column v-else :key="(tableColumn.column || {}).objectFieldName"
                :label="tableColumn.showName" :prop="tableColumn.dataFieldName"
                :width="tableColumn.columnWidth + 'px'"
                :sortable="tableColumn.sortable ? 'custom' : false"
              />
            </template>
            <!-- 字典字段 -->
            <el-table-column v-else :key="(tableColumn.column || {}).objectFieldName"
              :label="tableColumn.showName" :width="tableColumn.columnWidth + 'px'"
              :prop="tableColumn.column.columnName"
              :sortable="tableColumn.sortable ? 'custom' : false"
            >
              <template slot-scope="scope">
                <span>
                  {{getDictValue(tableColumn, scope.row)}}
                </span>
              </template>
            </el-table-column>
          </template>
          <el-table-column v-if="formType === SysOnlineFormType.WORK_ORDER" label="当前任务" prop="(runtimeTaskInfo || {}).taskName" />
          <el-table-column v-if="formType === SysOnlineFormType.WORK_ORDER" label="流程创建时间" width="180px" prop="createTime" />
          <el-table-column v-if="formType === SysOnlineFormType.WORK_ORDER" label="流程状态" width="100px" prop="flowStatus" />
          <el-table-column
            v-if="getTableOperation(true).length > 0 || formType === SysOnlineFormType.WORK_ORDER"
            label="操作" :width="(widgetConfig.tableInfo.optionColumnWidth || 150) + 'px'" fixed="right"
          >
            <template slot-scope="scope">
              <el-button v-for="operation in getTableOperation(true)" :key="operation.id"
                :class="operation.btnClass"
                type="text" size="mini"
                @click.stop="onOperationClick(operation, scope.row)"
              >
                {{operation.name}}
              </el-button>
              <el-button type="text" size="mini"
                v-if="formType === SysOnlineFormType.WORK_ORDER && (scope.row.initTaskInfo || {}).taskKey !== (scope.row.runtimeTaskInfo || {}).taskKey"
                @click.stop="onViewWorkOrder(scope.row)">
                详情
              </el-button>
              <el-button type="text" size="mini"
                v-if="formType === SysOnlineFormType.WORK_ORDER && (scope.row.initTaskInfo || {}).taskKey === (scope.row.runtimeTaskInfo || {}).taskKey"
                @click.stop="onHandlerWorkOrder(scope.row)">
                办理
              </el-button>
              <el-button type="text" size="mini"
              v-if="formType === SysOnlineFormType.WORK_ORDER"
                @click.stop="onHandlerRemindClick(scope.row)">
                催办
              </el-button>
              <el-button type="text" size="mini" class="table-btn error"
                v-if="formType === SysOnlineFormType.WORK_ORDER"
                @click.stop="onCancelWorkOrder(scope.row)">
                撤销
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <!-- 分页组件 -->
      <el-col :span="24" v-if="widgetConfig.tableInfo.paged && !this.isNew"
        :style="{'margin-bottom': widgetConfig.supportBottom === 1 ? '20px' : undefined}"
      >
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="tableWidget.totalCount"
            :current-page="tableWidget.currentPage"
            :page-size="tableWidget.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="tableWidget.onCurrentPageChange"
            @size-change="tableWidget.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </el-col>
</template>

<script>
import { mapGetters } from 'vuex';
import { TableWidget } from '@/utils/widget.js';
import { uploadMixin } from '@/core/mixins';
import { getDictDataList, getDictDataByUrl } from '../utils/index.js';

export default {
  props: {
    formType: {
      type: Number,
      required: true
    },
    widgetConfig: {
      type: Object,
      required: true
    },
    getTableQueryParams: {
      type: Function
    },
    loadTableDataFunc: {
      type: Function
    },
    flowData: {
      type: Object
    },
    // 是否是新建表格，如果为新建，增删改不会调用后台接口，直接把数据放到表格中
    isNew: {
      type: Boolean,
      default: false
    }
  },
  inject: ['preview'],
  mixins: [uploadMixin],
  data () {
    return {
      // 表格用到的字典数据
      tableDictValueListMap: new Map(),
      tableWidget: new TableWidget(
        this.loadTableData,
        this.loadTableVerify,
        this.widgetConfig.tableInfo.paged && !this.isNew,
        false,
        this.widgetConfig.tableInfo.orderFieldName,
        this.widgetConfig.tableInfo.ascending
      )
    }
  },
  methods: {
    onViewWorkOrder (row) {
      this.$emit('viewWOrkOrder', row, this.widgetConfig);
    },
    onHandlerWorkOrder (row) {
      this.$emit('handlerWOrkOrder', row, this.widgetConfig);
    },
    onHandlerRemindClick (row) {
      this.$emit('handlerRemind', row, this.widgetConfig);
    },
    onCancelWorkOrder (row) {
      this.$emit('cancelWOrkOrder', row, this.widgetConfig);
    },
    getTableWidget () {
      return this.tableWidget;
    },
    setTableWidget (tableWidget) {
      // 如果正在读取数据，等待读取完毕再刷新
      let timer = setInterval(() => {
        if (!this.tableWidget.loading) {
          this.tableWidget.totalCount = tableWidget.totalCount;
          this.tableWidget.currentPage = tableWidget.currentPage;
          this.tableWidget.pageSize = tableWidget.pageSize;
          this.tableWidget.refreshTable();
          clearInterval(timer);
        }
      }, 30);
    },
    onOperationClick (operation, row) {
      this.$emit('operationClick', operation, row, this.widgetConfig);
    },
    loadTableDictValue (tableData) {
      return new Promise((resolve, reject) => {
        // URL字典数据获取列表
        let httpCallInfoList = [];
        let dictIdMap = new Map();
        this.widgetConfig.tableColumnList.forEach(tableColumn => {
          let column = tableColumn.column;
          // URL字典字典值数据分组
          if (column && column.dictInfo && column.dictInfo.dictType === this.SysOnlineDictType.URL) {
            let fieldName = tableColumn.dataFieldName;
            let urlDictIdInfo = dictIdMap.get(column.dictInfo.dictId);
            if (urlDictIdInfo == null) {
              let dictValueMap = this.tableDictValueListMap.get(column.dictInfo.dictId);
              urlDictIdInfo = {
                column,
                dictInfo: column.dictInfo,
                dictValueMap,
                paramSet: new Set()
              }
              httpCallInfoList.push(urlDictIdInfo);
              dictIdMap.set(column.dictInfo.dictId, urlDictIdInfo)
            }
            if (Array.isArray(tableData.dataList)) {
              tableData.dataList.forEach(row => {
                let value = row[fieldName];
                urlDictIdInfo.paramSet.add(value);
              });
            }
          }
        });
        Promise.all(httpCallInfoList.map(dictInfoItem => {
          if (dictInfoItem.dictInfo.dictIdsUrl == null || this.preview()) {
            if (!this.preview()) {
              this.$message.error('数据表字段 [' + dictInfoItem.column.columnName + '] 绑定字典 [' + dictInfoItem.dictInfo.dictName + '] 未发现获取指定ids数据url！');
            }
            resolve([]);
          } else {
            let dictIds = [];
            dictInfoItem.paramSet.forEach(item => {
              dictIds.push(item);
            });
            let params = {
              dictIds: dictIds
            }
            return getDictDataByUrl(dictInfoItem.dictInfo.dictIdsUrl, params, dictInfoItem.dictInfo, 'post');
          }
        })).then((dictValueDataList) => {
          dictValueDataList.forEach((dictDataList, index) => {
            dictDataList.forEach(data => {
              httpCallInfoList[index].dictValueMap.set(data.id, data);
            });
          });
          resolve(tableData);
        }).catch(e => {
          reject(e);
        });
      });
    },
    loadTableData (params) {
      if (this.widgetConfig.datasource == null || this.isNew) {
        return Promise.resolve({
          dataList: this.tableWidget.dataList,
          totalCount: 0
        });
      }
      if (params == null) params = {};
      let queryParams = this.getTableQueryParams ? this.getTableQueryParams(this.widgetConfig) : undefined;
      params = {
        datasourceId: this.widgetConfig.datasource.datasourceId,
        relationId: this.widgetConfig.relation ? this.widgetConfig.relation.relationId : undefined,
        ...params,
        filterDtoList: queryParams
      }
      return new Promise((resolve, reject) => {
        if (typeof this.loadTableDataFunc === 'function') {
          return this.loadTableDataFunc(params).then(res => {
            // 获取表格字典数据
            return this.loadTableDictValue(res);
          }).then(res => {
            resolve(res);
          }).catch(e => {
            reject(e);
          });
        }
        let httpCall = null;
        if (params.relationId) {
          httpCall = this.doUrl('/admin/online/onlineOperation/listByOneToManyRelationId/' + this.widgetConfig.datasource.variableName, 'post', params);
        } else {
          httpCall = this.doUrl('/admin/online/onlineOperation/listByDatasourceId/' + this.widgetConfig.datasource.variableName, 'post', params);
        }
        httpCall.then(res => {
          // 获取表格字典数据
          return this.loadTableDictValue(res.data);
        }).then(res => {
          resolve({
            dataList: res.dataList,
            totalCount: res.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    loadTableVerify () {
      return true;
    },
    // 页面是否是返回状态
    isResume () {
      let key = this.$route.fullPath;
      let cacheFormData = this.getOnlineFormCache[key];
      return cacheFormData != null;
    },
    /**
     * 刷新表格数据
     * row：表格数据，如果表格数据为空，则重新获取
     * operatorType：表格数据操作类型
     */
    refresh (row, operatorType) {
      if (this.isResume()) return;
      if (!this.isNew) {
        // 重新获取表格数据
        this.tableWidget.refreshTable();
      } else {
        if (operatorType === this.SysCustomWidgetOperationType.ADD) this.tableWidget.dataList.push(row);
        if (operatorType === this.SysCustomWidgetOperationType.EDIT) {
          this.tableWidget.dataList = this.tableWidget.dataList.map(item => {
            if (item.__cascade_add_id__ == null ? item[this.primaryColumnName] === row[this.primaryColumnName] : item.__cascade_add_id__ === row.__cascade_add_id__) {
              return {
                ...row
              }
            } else {
              return item;
            }
          });
        }
        if (operatorType === this.SysCustomWidgetOperationType.DELETE) {
          this.tableWidget.dataList = this.tableWidget.dataList.filter(item => {
            return item !== row;
          });
        }
      }
    },
    getTableOperation (rowOperation) {
      if (this.widgetConfig.readOnly) return [];
      let tempList = this.widgetConfig.operationList.filter(operation => {
        return operation.rowOperation === rowOperation && operation.enabled;
      });
      // 自定义操作前置
      if (rowOperation) {
        let customOperation = [];
        return customOperation.concat(tempList.filter(item => {
          if (item.type === this.SysCustomWidgetOperationType.CUSTOM) customOperation.push(item);
          return item.type !== this.SysCustomWidgetOperationType.CUSTOM;
        }));
      }
      return tempList;
    },
    /**
     * 获取表格列字段在后端返回的接口中字段名
     */
    getTableColumnFieldName (tableColumn) {
      if (tableColumn.column == null) return null;
      let fieldName = tableColumn.column.columnName || tableColumn.column.objectFieldName;
      if (tableColumn.relation != null) {
        fieldName = tableColumn.relation.variableName + '__' + fieldName;
      }
      // 工单列表
      if (this.formType === this.SysOnlineFormType.WORK_ORDER) {
        fieldName = 'masterTable__' + fieldName;
      }
      return fieldName;
    },
    /**
     * 获取表格cell数据字典值
     */
    getDictValue (tableColumn, row) {
      let dictData = null;
      let value = row[tableColumn.dataFieldName];
      // 如果有DictMap信息，优先使用DictMap信息
      if (tableColumn.column.dictInfo != null) {
        if (row[tableColumn.dataFieldName + '__DictMap']) {
          return row[tableColumn.dataFieldName + '__DictMap'].name;
        }
      }
      let dictValueList = this.tableDictValueListMap.get(tableColumn.column.dictInfo.dictId);
      if (dictValueList != null) {
        dictData = dictValueList.get(value);
      }

      return dictData ? dictData.name : undefined;
    },
    getDownloadUrl (tableColumn) {
      let downloadUrl = null;
      if (this.flowData != null) {
        downloadUrl = '/admin/flow/flowOnlineOperation/download';
      } else {
        if (tableColumn.relationId) {
          downloadUrl = '/admin/online/onlineOperation/downloadOneToManyRelation/' + (this.widgetConfig.datasource || {}).variableName;
        } else {
          downloadUrl = '/admin/online/onlineOperation/downloadDatasource/' + (this.widgetConfig.datasource || {}).variableName;
        }
      }

      return downloadUrl;
    },
    parseTableUploadData (tableColumn, row) {
      let jsonData = row[tableColumn.dataFieldName];
      if (jsonData == null) return [];
      let downloadParams = {
        ...this.buildFlowParam,
        datasourceId: this.widgetConfig.datasource.datasourceId,
        relationId: tableColumn.relationId,
        fieldName: tableColumn.column.columnName,
        asImage: tableColumn.column.fieldKind === this.SysOnlineFieldKind.UPLOAD_IMAGE
      }

      if (this.primaryColumnName != null) {
        downloadParams.dataId = row[this.primaryColumnName] || '';
      }

      let downloadUrl = this.getDownloadUrl(tableColumn);
      
      let temp = JSON.parse(jsonData).map(item => {
        return {
          ...item,
          downloadUri: downloadUrl
        }
      })
      return this.parseUploadData(JSON.stringify(temp), downloadParams);
    },
    getTablePictureList (tableColumn, row) {
      let temp = this.parseTableUploadData(tableColumn, row);
      if (Array.isArray(temp)) {
        return temp.map(item => item.url);
      }
    }
  },
  mounted () {
    if (Array.isArray(this.widgetConfig.tableColumnList)) {
      // 字典数据获取信息，包含静态字典和自定义字典信息
      let httpCallInfoList = [];
      this.widgetConfig.tableColumnList.forEach(tableColumn => {
        tableColumn.dataFieldName = this.getTableColumnFieldName(tableColumn);
        let column = tableColumn.column;
        if (column && column.dictInfo && column.dictInfo.dictType !== this.SysOnlineDictType.TABLE) {
          if (!this.tableDictValueListMap.has(column.dictInfo.dictId)) {
            let tempMap = new Map();
            this.tableDictValueListMap.set(column.dictInfo.dictId, tempMap);
            if (column.dictInfo.dictType !== this.SysOnlineDictType.URL) {
              httpCallInfoList.push({
                valueMap: tempMap,
                httpCall: getDictDataList(this, column.dictInfo)
              });
            }
          }
        }
      });
      // 获取所有静态字典和自定义字典数据
      Promise.all(httpCallInfoList.map(item => item.httpCall)).then(dictValueList => {
        dictValueList.forEach((dictDataList, index) => {
          if (Array.isArray(dictDataList)) {
            dictDataList.forEach(data => {
              httpCallInfoList[index].valueMap.set(data.id, data);
            });
          }
        });
        this.refresh();
      }).catch(e => {
        this.$message.error('获取表格字典数据失败！');
      });
    } else {
      this.refresh();
    }
  },
  computed: {
    buildFlowParam () {
      let flowParam = {};
      if (this.flowData) {
        if (this.flowData.processDefinitionKey) flowParam.processDefinitionKey = this.flowData.processDefinitionKey;
        if (this.flowData.processInstanceId) flowParam.processInstanceId = this.flowData.processInstanceId;
        if (this.flowData.taskId) flowParam.taskId = this.flowData.taskId;
      }

      return flowParam;
    },
    primaryColumnName () {
      if (this.widgetConfig && this.widgetConfig.table && Array.isArray(this.widgetConfig.table.columnList)) {
        for (let i = 0; i < this.widgetConfig.table.columnList.length; i++) {
          let column = this.widgetConfig.table.columnList[i];
          if (column.primaryKey) {
            return (this.widgetConfig.relation ? this.widgetConfig.relation.variableName + '__' : '') + column.columnName;
          }
        }
      }
      return null;
    },
    ...mapGetters(['getOnlineFormCache'])
  }
}
</script>

<style scoped>
  .table-title {
    padding: 0px 5px;
    height: 22px;
    line-height: 22px;
    margin: 3px 0px;
  }
</style>
