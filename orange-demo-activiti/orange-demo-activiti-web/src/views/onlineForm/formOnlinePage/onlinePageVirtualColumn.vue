<template>
  <el-row type="flex" style="width: 100%;" :style="{height: height + 'px'}">
    <!-- 虚拟字段列表 -->
    <div class="table-column-list">
      <div class="title">
        <span>虚拟字段列表</span>
        <el-button class="table-btn success" size="mini" type="text"
          icon="el-icon-circle-plus-outline"
          @click.stop="onAddNewVirtualColumn">
          新增
        </el-button>
      </div>
      <el-scrollbar :style="{height: height - 51 + 'px'}">
        <div v-for="column in allVirtualColumnList" :key="column.columnId"
          class="table-column-item" :class="{'active-column': currentColumn.virtualColumnId === column.virtualColumnId}"
          :title="column.columnComment"
          @click.stop="onActiveColumnClick(column)"
        >
          <span style="margin-right: 10px;">{{column.columnPrompt}}</span>
          <el-button class="table-btn delete" size="mini" type="text" style="margin-left: 10px;"
            @click.stop="onDeleteColumn(column)"
          >
            删除
          </el-button>
        </div>
      </el-scrollbar>
    </div>
    <!-- 虚拟字段属性 -->
    <div class="column-attributes">
      <el-row type="flex" justify="end" align="middle" style="padding-right: 10px; border-bottom: 1px dashed #dcdfe6">
        <div class="attribute-title">
          <span>字段属性</span>
        </div>
        <el-button type="text" icon="el-icon-refresh" @click="onSaveColumn">保存</el-button>
        <el-button class="table-btn success" type="text" icon="el-icon-back" @click="onBack">返回</el-button>
      </el-row>
      <el-form class="full-width-input" ref="columnAttribute"
        :model="currentColumn" style="width: 100%;"
        label-width="120px" label-position="right"
        @submit.native.prevent size="small" :rules="rules">
        <div v-if="currentColumn != null" style="padding: 20px;" class="attibute-box">
          <el-col class="attribute-item">
            <el-form-item label="结果字段列名：" prop="objectFieldName">
              <el-input v-model="currentColumn.objectFieldName" placeholder="结果字段列名" />
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="结果字段显示名" prop="columnPrompt">
              <el-input v-model="currentColumn.columnPrompt" placeholder="结果字段显示名" />
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="聚合关联" prop="relationId">
              <el-select v-model="currentColumn.relationId" placeholder="聚合关联选择"
                @change="onRelationChange">
                <el-option v-for="item in relationList" :key="item.relationId"
                  :value="item.relationId" :label="item.relationName">
                  <div style="display: flex; justify-content: space-between; align-items: center">
                    <span>{{item.relationName}}</span>
                    <el-tag style="margin-left: 30px;" size="mini"
                      :type="getDatasourceTableTagType(item.relationType)">
                      {{SysOnlineRelationType.getValue(item.relationType)}}
                    </el-tag>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="聚合计算表" prop="aggregationTableId">
              <el-select v-model="currentColumn.aggregationTableId" placeholder="聚合计算表" @change="onAggregationTableIdChange">
                <el-option v-for="item in aggregationTableList" :key="item.relationId"
                  :value="item.tableId" :label="item.tableName">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="聚合计算字段" prop="aggregationColumnId">
              <el-select v-model="currentColumn.aggregationColumnId" placeholder="聚合计算表" @change="onAggregationColumnIdChange">
                <el-option v-for="item in aggregationColumnList" :key="item.relationId"
                  :value="item.columnId" :label="item.columnName">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="结果字段类型" prop="objectFieldType">
              <el-select v-model="currentColumn.objectFieldType" placeholder="结果字段类型" @change="currentColumn.aggregationType = undefined">
                <el-option v-for="item in getVirtualColumnFieldType" :key="item"
                  :value="item" :label="item"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="聚合计算规则" prop="relationId">
              <el-select v-model="currentColumn.aggregationType" placeholder="聚合字段计算规则">
                <el-option v-for="item in getAggregationTypeList" :key="item.id"
                  :value="item.id" :label="item.name"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </div>
      </el-form>
    </div>
    <!-- 过滤条件 -->
    <div class="column-filter">
      <el-row type="flex" align="middle" style="border-bottom: 1px dashed #dcdfe6; height: 40px;">
        <span style="color: #043254; font-weight: 700;">过滤条件</span>
      </el-row>
      <el-row style="margin-top: 15px;">
        <el-col :span="24" style="border-top: 1px solid #EBEEF5;">
          <el-table size="mini" :data="virtualColumnFilterList" :show-header="false" empty-text="请添加过滤条件">
            <el-table-column label="操作" width="45px">
              <template slot-scope="scope">
                <el-button class="table-btn delete" type="text" icon="el-icon-remove-outline"
                  @click="onDeleteColumnFilter(scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="参数名称">
              <template slot-scope="scope">
                <el-button class="table-btn" type="text" style="text-decoration: underline;" @click="onEditColumnFilter(scope.row)">{{(scope.row.column || {}).columnName || '未知字段'}}</el-button>
              </template>
            </el-table-column>
            <el-table-column label="所属表" width="100px">
              <template>
                <el-tag size="mini" type="success">关联从表</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="过滤类型" width="100px">
              <template slot-scope="scope">
                <span>{{SysOnlineFilterOperationType.getValue(scope.row.operatorType)}}</span>
              </template>
            </el-table-column>
            <el-table-column label="参数值" prop="value">
              <template slot-scope="scope">
                <span v-if="(scope.row.column || {}).dictInfo == null">{{scope.row.value}}</span>
                <span v-else>{{scope.row.column.dictInfo.dictName + ' / ' + scope.row.dictValueName}}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-button style="width: 100%; margin-top: 10px; border: 1px dashed #EBEEF5;"
            :disabled="currentColumn == null || aggregationRelation == null" icon="el-icon-plus"
            @click="onEditColumnFilter(null)">
            添加过滤条件
          </el-button>
        </el-col>
      </el-row>
    </div>
  </el-row>
</template>

<script>
import { findItemFromList } from '@/utils';
import { getDictDataList } from '../utils';
import { OnlineColumnController, OnlineVirtualColumnController } from '@/api/onlineController.js';
import EditVirtualColumnFilter from './editVirtualColumnFilter.vue';

const defaultVirtualColumnInfo = {
  virtualColumnId: undefined,
  datasourceId: undefined,
  objectFieldName: undefined,
  columnPrompt: undefined,
  objectFieldType: undefined,
  relationId: undefined,
  aggregationType: undefined,
  aggregationTableId: undefined,
  aggregationColumnId: undefined,
  virtualType: 0,
  whereClauseJson: undefined
}

export default {
  props: {
    datasource: {
      type: Object
    },
    relationList: {
      type: Array
    },
    height: {
      type: Number
    }
  },
  data () {
    return {
      currentColumn: {
        ...defaultVirtualColumnInfo
      },
      allVirtualColumnList: [],
      aggregationTableList: [],
      aggregationColumnList: [],
      virtualColumnFilterList: [],
      rules: {
        objectFieldName: [
          {required: true, message: '结果字段列名不能为空', trigger: 'blur'}
        ],
        objectFieldType: [
          {required: true, message: '结果字段类型不能为空', trigger: 'blur'}
        ],
        columnPrompt: [
          {required: true, message: '结果字段显示名不能为空', trigger: 'blur'}
        ],
        relationId: [
          {required: true, message: '必须选则聚合字段关联', trigger: 'blur'}
        ],
        aggregationType: [
          {required: true, message: '必须选则聚合计算规则', trigger: 'blur'}
        ],
        aggregationTableId: [
          {required: true, message: '聚合计算表不能为空', trigger: 'blur'}
        ],
        aggregationColumnId: [
          {required: true, message: '聚合计算字段不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    onBack () {
      this.$emit('close');
    },
    onActiveColumnClick (column) {
      this.currentColumn = column;
    },
    onAddNewVirtualColumn () {
      this.currentColumn = {
        ...defaultVirtualColumnInfo
      }
    },
    onDeleteColumn (column) {
      this.$confirm('是否删除此虚拟字段？').then(res => {
        let params = {
          virtualColumnId: column.virtualColumnId
        }

        OnlineVirtualColumnController.delete(this, params).then(res => {
          if (column.virtualColumnId === this.currentColumn.virtualColumnId) {
            this.currentColumn = {
              ...defaultVirtualColumnInfo
            }
          }
          this.$message.success('删除成功！');
          this.loadOnlineVirtualColumnList();
        }).catch(e => {});
      }).catch(e => {});
    },
    onSaveColumn () {
      let whereClauseJson = this.virtualColumnFilterList.map(item => {
        return {
          tableId: item.tableId,
          columnId: item.columnId,
          operatorType: item.operatorType,
          value: item.value
        }
      });
      let params = {
        onlineVirtualColumnDto: {
          ...this.currentColumn,
          datasourceId: this.datasource.datasourceId,
          whereClauseJson: JSON.stringify(whereClauseJson)
        }
      }
      let httpCall = this.currentColumn.virtualColumnId ? OnlineVirtualColumnController.update(this, params) : OnlineVirtualColumnController.add(this, params);
      httpCall.then(res => {
        if (this.currentColumn.virtualColumnId == null) this.currentColumn.virtualColumnId = res.data;
        this.$message.success('保存成功！');
        this.loadOnlineVirtualColumnList();
      }).catch(e => {});
    },
    onEditColumnFilter (row) {
      this.$dialog.show(row ? '编辑过滤' : '添加过滤', EditVirtualColumnFilter, {
        area: '500px'
      }, {
        rowData: row,
        tableList: this.aggregationTableList
      }).then(res => {
        if (row == null) {
          this.virtualColumnFilterList.push(res);
        } else {
          this.virtualColumnFilterList = this.virtualColumnFilterList.map(item => {
            return item === row ? res : item;
          });
        }
      }).catch(e => {});
    },
    onDeleteColumnFilter (row) {
      this.$confirm('是否删除此过滤条件？').then(res => {
        this.virtualColumnFilterList = this.virtualColumnFilterList.filter(item => item !== row);
      }).catch(e => {});
    },
    loadOnlineTableColumnList (tableId) {
      return new Promise((resolve, reject) => {
        if (tableId == null || tableId === '') resolve();

        let params = {
          onlineColumnDtoFilter: {
            tableId
          }
        }

        OnlineColumnController.list(this, params).then(res => {
          this.aggregationTable.columnList = res.data.dataList;
          this.aggregationColumnList = res.data.dataList;
          resolve();
        }).catch(e => {
          reject();
        });
      });
    },
    getDatasourceTableTagType (relationType) {
      if (relationType == null) return 'success';
      switch (relationType) {
        case this.SysOnlineRelationType.ONE_TO_ONE: return 'primary';
        case this.SysOnlineRelationType.ONE_TO_MANY: return 'warning';
        default:
          return 'info';
      }
    },
    onRelationChange (relationId) {
      this.aggregationTableList = [];
      this.aggregationColumnList = [];
      this.currentColumn.aggregationColumnId = undefined;
      this.currentColumn.aggregationTableId = undefined;
      this.currentColumn.aggregationType = undefined;
      this.currentColumn.objectFieldType = undefined;

      let relation = findItemFromList(this.relationList, relationId, 'relationId');
      if (relation != null) {
        this.aggregationTableList = [relation.slaveTable];
      }
    },
    onAggregationTableIdChange (tableId) {
      this.aggregationColumnList = [];
      this.currentColumn.aggregationColumnId = undefined;
      this.currentColumn.aggregationType = undefined;
      this.currentColumn.objectFieldType = undefined;

      if (this.aggregationTable != null) {
        if (Array.isArray(this.aggregationTable.columnList) && this.aggregationTable.columnList.length > 0) {
          this.aggregationColumnList = this.aggregationTable.columnList;
        } else {
          this.loadOnlineTableColumnList(this.aggregationTable.tableId).catch(e => {});
        }
      }
    },
    onAggregationColumnIdChange (columnId) {
      this.currentColumn.aggregationType = undefined;
      this.currentColumn.objectFieldType = undefined;
    },
    loadOnlineVirtualColumnList () {
      let params = {
        onlineVirtualColumnDtoFilter: {
          datasourceId: this.datasource.datasourceId
        }
      }

      OnlineVirtualColumnController.list(this, params).then(res => {
        this.allVirtualColumnList = res.data.dataList;
      }).catch(e => {});
    },
    buildVirtualColumnFilter () {
      let filterList = this.currentColumn.whereClauseJson ? JSON.parse(this.currentColumn.whereClauseJson) : [];
      filterList = filterList.map(item => {
        return {
          ...item,
          table: this.aggregationTable,
          column: findItemFromList(this.aggregationTable.columnList, item.columnId, 'columnId')

        }
      });
      // 获取过滤条件种过滤值的字典信息
      let allPromise = filterList.map(filterItem => {
        return new Promise((resolve) => {
          if (filterItem.column == null || filterItem.column.dictInfo == null) {
            resolve();
            return;
          }
          let dictInfo = filterItem.column.dictInfo;
          getDictDataList(this, dictInfo).then(dictValueList => {
            let dictValue = findItemFromList(dictValueList, filterItem.value, 'id');
            if (dictValue) {
              filterItem.dictValueName = dictValue.name;
            } else {
              filterItem.value = undefined;
            }
            resolve();
          }).catch(e => {
            filterItem.value = undefined;
            resolve(e);
          });
        });
      });
      Promise.all(allPromise).then(res => {
        this.virtualColumnFilterList = filterList;
      }).catch(e => {
        console.log(e);
      });
    }
  },
  computed: {
    aggregationRelation () {
      if (this.currentColumn.relationId == null) return null;
      return findItemFromList(this.relationList, this.currentColumn.relationId, 'relationId');
    },
    aggregationTable () {
      if (this.currentColumn.aggregationTableId == null) return null;
      return findItemFromList(this.aggregationTableList, this.currentColumn.aggregationTableId, 'tableId');
    },
    aggregationColumn () {
      if (this.currentColumn.aggregationColumnId == null) return null;
      return findItemFromList(this.aggregationColumnList, this.currentColumn.aggregationColumnId, 'columnId');
    },
    getVirtualColumnFieldType () {
      if (this.aggregationColumn == null) return [];

      switch (this.aggregationColumn.objectFieldType) {
        case 'String':
        case 'Boolean':
          return ['Integer'];
        case 'Integer':
        case 'Long':
        case 'BigDecimal':
        case 'Double':
          return ['Integer', 'Long', 'Double'];
        case 'Date':
          return ['Integer', 'Date'];
        default:
          return ['Integer', 'Long', 'Double', 'Date'];
      }
    },
    getAggregationTypeList () {
      if (this.aggregationColumn == null) return [];
      return this.SysOnlineAggregationType.getList().filter(item => {
        switch (item.id) {
          case this.SysOnlineAggregationType.SUM:
            return ['Date', 'Boolean', 'String'].indexOf(this.aggregationColumn.objectFieldType) === -1 && this.currentColumn.objectFieldType !== 'Date';
          case this.SysOnlineAggregationType.COUNT:
            return true;
          case this.SysOnlineAggregationType.AVG:
          case this.SysOnlineAggregationType.MIN:
          case this.SysOnlineAggregationType.MAX:
            if (this.aggregationColumn.objectFieldType === 'Date') {
              return this.currentColumn.objectFieldType === 'Date';
            } else {
              return ['Integer', 'Long', 'BigDecimal', 'Double'].indexOf(this.aggregationColumn.objectFieldType) !== -1;
            }
        }
      });
    }
  },
  mounted () {
    this.loadOnlineVirtualColumnList();
  },
  watch: {
    currentColumn: {
      handler (newValue) {
        if (newValue == null) {
          this.currentColumn = {
            ...defaultVirtualColumnInfo
          }
        } else {
          if (this.aggregationRelation) this.aggregationTableList = [this.aggregationRelation.slaveTable];
          if (this.aggregationTable) {
            this.loadOnlineTableColumnList(this.aggregationTable.tableId).catch(e => {});
            if (Array.isArray(this.aggregationTable.columnList) && this.aggregationTable.columnList.length > 0) {
              this.buildVirtualColumnFilter();
            } else {
              this.loadOnlineTableColumnList(this.aggregationTable.tableId).then(res => {
                this.buildVirtualColumnFilter();
              }).catch(e => {
                console.log(e);
              });
            }
          }
        }
      },
      immediate: true
    }
  }
}
</script>

<style scoped>
  .table-column-list {
    width: 300px;
    height: 100%;
    border-radius: 3px;
    padding: 0px 10px 10px 10px;
    margin-bottom: 10px;
    flex-shrink: 0;
    background: white;
  }

  .table-column-list > .title {
    font-size: 14px;
    color: #043254;
    line-height: 30px;
    height: 40px;
    margin-bottom: 10px;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px dashed #dcdfe6;
  }

  .table-column-list > .title > span {
    width: 100%;
  }

  .table-column-list .table-column-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0px 10px;
    margin-bottom: 8px;
    font-size: 12px;
    color: #043254;
    cursor: pointer;
    border-radius: 3px;
    height: 35px;
    line-height: 35px;
    width: 100%;
    background: #f4f7fa;
    border: 1px dashed #f3f9ff;
  }

  .table-column-list .table-column-item:hover {
    background: #ecf4fc;
  }

  .table-column-list .table-column-item.active-column {
    background: #ecf4fc;
    border: 1px dashed #b6d8fa;
  }

  .table-column-list .table-column-item .delete {
    display: none;
  }

  .table-column-list .table-column-item:hover .delete {
    display: block;
  }

  .column-attributes {
    height: 100%;
    width: 100%;
    padding: 0px 10px 10px 10px;
    background: white;
    margin: 0px 10px;
    border-radius: 3px;
  }

  .column-attributes .attribute-title {
    width: 100%;
    margin-left: 15px;
    color: #043254;
    font-weight: 700;
  }

  .column-attributes .attibute-box {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .column-attributes .attibute-box .attribute-item {
    width: 500px;
  }

  .column-filter {
    width: 600px;
    height: 100%;
    padding: 0px 10px 10px 10px;
    background: white;
    flex-shrink: 0;
    border-radius: 3px;
  }
</style>
