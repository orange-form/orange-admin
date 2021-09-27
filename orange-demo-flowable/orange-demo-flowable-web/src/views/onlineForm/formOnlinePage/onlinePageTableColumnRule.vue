<template>
  <el-row type="flex" style="width: 100%;" :style="{height: height + 'px'}">
    <!-- 字段列表 -->
    <div class="table-column-list">
      <div class="title">
        <span>{{tableName}}</span>
        <el-dropdown trigger="click" @command="onAddNewColumn">
          <el-button class="table-btn success" size="mini" type="text"
            icon="el-icon-circle-plus-outline" :disabled="getNewColumnList.length <= 0"
          >
            新增
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item v-for="newColumn in getNewColumnList" :key="newColumn.columnName"
              :command="newColumn">
              {{newColumn.columnName}}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <el-scrollbar :style="{height: height - 51 + 'px'}">
        <div v-for="column in getAllColumnList" :key="column.columnId"
          class="table-column-item" :class="{'active-column': currentColumn === column}"
          :title="column.columnComment"
          @click.stop="onActiveColumnClick(column)"
        >
          <div>
            <span style="margin-right: 10px;">{{column.columnName}}</span>
            <el-tag v-if="column.deletedFlag" size="mini" type="danger">已删除</el-tag>
          </div>
          <div class="refresh" style="margin-left: 10px;">
            <el-button class="table-btn success" size="mini" type="text" v-if="getNewColumnList.length <= 0"
              @click.stop="onRefreshOnlineColumn(column, column)"
            >
              刷新
            </el-button>
            <el-dropdown v-else trigger="click" @command="onRefreshNewColumn">
              <el-button class="table-btn success" size="mini" type="text" @click.stop="() => {}">
                刷新
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-for="newColumn in getNewColumnList" :key="newColumn.columnName"
                  :command="{column, newColumn}">
                  {{newColumn.columnName}}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <el-button v-if="column.deletedFlag" class="table-btn delete" size="mini" type="text" style="margin-left: 10px;"
              @click.stop="onDeleteColumn(column)"
            >
              删除
            </el-button>
          </div>
        </div>
      </el-scrollbar>
    </div>
    <!-- 字段属性 -->
    <div class="column-attributes">
      <el-row type="flex" justify="end" align="middle" style="padding-right: 10px; border-bottom: 1px dashed #dcdfe6">
        <div class="attribute-title">
          <span>字段属性</span>
        </div>
        <el-button type="text" icon="el-icon-refresh" @click="onSaveColumn">保存</el-button>
        <el-button type="text" icon="el-icon-back" style="color: #67C23A;" @click="onBack">返回</el-button>
      </el-row>
      <el-form ref="columnAttribute" :model="currentColumn" class="full-width-input" style="width: 100%;"
        label-width="120px" label-position="right" @submit.native.prevent size="small">
        <div v-if="currentColumn != null" style="padding: 20px;" class="attibute-box">
          <el-col class="attribute-item">
            <el-form-item label="字段名：">
              <span :title="currentColumn.columnComment">{{currentColumn.columnName}}</span>
              <el-tag size="mini" type="warning" v-if="currentColumn.primaryKey" style="margin-left: 20px;">主键</el-tag>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="字段类型：">
              <span>{{currentColumn.fullColumnType}}</span>
              <el-tag size="mini" type="success" effect="dark" style="margin-left: 10px;">{{currentColumn.tag.objectFieldType}}</el-tag>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="是否必填：">
              <el-switch v-model="currentColumn.required" disabled />
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="过滤支持：">
              <el-radio-group v-model="currentColumn.filterType" @change="dirty = true">
                <el-radio-button v-for="item in SysOnlineColumnFilterType.getList()"
                  :key="item.id" :label="item.id">
                  {{SysOnlineColumnFilterType.getValue(item.id)}}
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="显示名称：">
              <el-input v-model="currentColumn.columnComment" style="width: 278px;" @change="dirty = true" placeholder="字段在表单上的显示名称" />
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="字典数据：">
              <el-select v-model="currentColumn.dictId" placeholder="选择字段绑定的字典" style="width: 278px;"
                clearable filterable :disabled="currentColumn.tag.objectFieldType === 'Boolean'">
                <el-option v-for="item in dictList" :key="item.dictId" :value="item.dictId" :label="item.dictName" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="数据权限过滤：">
              <el-select v-model="dataPermFilterType" placeholder="字段用于数据权限过滤规则"
                style="width: 278px;" clearable @change="dirty = true">
                <el-option v-for="item in SysOnlineDataPermFilterType.getList()" :key="item.id"
                  :label="item.name" :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item">
            <el-form-item label="字段类别：">
              <el-select v-model="currentColumn.fieldKind" clearable placeholder="字段的业务类别" @change="dirty = true" style="width: 278px;">
                <el-option v-for="item in getValidColumnKind" :key="item.id"
                  :label="item.name" :value="item.id" :disabled="columnKindItemDisabled(item)"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col class="attribute-item" v-if="currentColumn.fieldKind === SysOnlineFieldKind.UPLOAD || currentColumn.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE">
            <el-form-item label="上传文件数量：">
              <el-input-number v-model="currentColumn.maxFileCount" style="width: 278px;" placeholder="不填或者0则代表无限制" @change="dirty = true" />
            </el-form-item>
          </el-col>
        </div>
      </el-form>
    </div>
    <!-- 验证规则 -->
    <div class="column-rules">
      <el-row type="flex" align="middle" style="border-bottom: 1px dashed #dcdfe6; height: 40px;">
        <span style="color: #043254; font-weight: 700;">验证规则</span>
      </el-row>
      <el-row style="margin-top: 15px;">
        <el-col :span="24" style="border-top: 1px solid #EBEEF5;">
          <el-table size="mini" :data="columnRuleList" :show-header="false" empty-text="请添加验证规则">
            <el-table-column type="index" width="45px" />
            <el-table-column label="规则名称" prop="ruleName" width="150px" />
            <el-table-column label="校验错误信息" prop="columnRuleInfo.message" />
            <el-table-column label="操作" width="110px">
              <template slot-scope="scope">
                <el-button class="table-btn success" type="text" @click="onEditColumnRule(scope.row)">编辑</el-button>
                <el-button class="table-btn delete" type="text" @click="onDeleteColumnRule(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button style="width: 100%; margin-top: 10px; border: 1px dashed #EBEEF5;"
            :disabled="currentColumn == null" icon="el-icon-plus" @click="onSetColumnRule">
            添加验证规则
          </el-button>
        </el-col>
      </el-row>
    </div>
  </el-row>
</template>

<script>
import SetOnlineTableColumnRule from './setOnlineTableColumnRule.vue';
import { OnlineColumnController, OnlineDblinkController } from '@/api/onlineController.js';

export default {
  props: {
    isMasterTable: {
      type: Boolean,
      default: false
    },
    tableId: {
      type: String,
      required: true
    },
    dblinkId: {
      type: [String, Number],
      required: true
    },
    tableName: {
      type: String,
      required: true
    },
    dictList: {
      type: Array
    },
    height: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      dirty: false,
      dataPermFilterType: undefined,
      columnMap: undefined,
      dblinkTableColumnMap: undefined,
      columnRuleList: [],
      currentColumn: undefined,
      storeColumn: undefined
    }
  },
  methods: {
    onBack () {
      this.$emit('close');
    },
    /**
     * 添加字段
     */
    onAddNewColumn (newColumn) {
      let params = {
        tableId: this.tableId,
        dblinkId: this.dblinkId,
        tableName: this.tableName,
        columnName: newColumn.columnName
      }

      OnlineColumnController.add(this, params).then(res => {
        this.$message.success('添加成功！');
        this.dirty = false;
        this.loadOnlineTableColumns();
      }).catch(e => {});
    },
    /**
     * 刷新字段
     */
    onRefreshOnlineColumn (column, newColumn) {
      this.$confirm('刷新字段将替换现有字段有所的属性，是否继续？').then(res => {
        let params = {
          columnId: column.columnId,
          dblinkId: this.dblinkId,
          tableName: this.tableName,
          columnName: newColumn.columnName
        }

        return OnlineColumnController.refreshColumn(this, params);
      }).then(res => {
        this.$message.success('刷新成功！');
        this.dirty = false;
        this.loadOnlineTableColumns();
      }).catch(e => {});
    },
    onDeleteColumn (column) {
      this.$confirm('是否删除字段？').then(res => {
        let params = {
          columnId: column.columnId
        }

        return OnlineColumnController.delete(this, params);
      }).then(res => {
        this.$message.success('删除成功!');
        this.loadOnlineTableColumns();
      }).catch(e => {});
    },
    onRefreshNewColumn (command) {
      this.onRefreshOnlineColumn(command.column, command.newColumn)
    },
    loadColumnRuleList (columnId) {
      this.columnRuleList = [];
      OnlineColumnController.listOnlineColumnRule(this, {
        columnId: columnId
      }).then(res => {
        res.data.dataList.forEach(item => {
          item.columnRuleInfo = {
            ...JSON.parse(item.onlineColumnRule.propDataJson)
          }
        });
        this.columnRuleList = res.data.dataList;
      }).catch(e => {});
    },
    onDeleteColumnRule (rule) {
      this.$confirm('是否从当前字段中删除此规则？').then(() => {
        let params = {
          columnId: this.currentColumn.columnId,
          ruleId: rule.ruleId
        }

        return OnlineColumnController.deleteOnlineColumnRule(this, params);
      }).then(res => {
        this.$message.success('删除成功！');
        this.loadColumnRuleList(this.currentColumn.columnId);
      }).catch(e => {});
    },
    onEditColumnRule (rule) {
      this.$dialog.show('编辑字段验证规则', SetOnlineTableColumnRule, {
        area: '600px'
      }, {
        column: this.currentColumn,
        columnRule: rule
      }).then(res => {
        this.loadColumnRuleList(this.currentColumn.columnId);
      }).catch(e => {});
    },
    setCurrentColumn (column) {
      this.currentColumn = column;
      this.dataPermFilterType = undefined;
      if (this.currentColumn.tag.userFilter) this.dataPermFilterType = this.SysOnlineDataPermFilterType.USER_FILTER;
      if (this.currentColumn.tag.deptFilter) this.dataPermFilterType = this.SysOnlineDataPermFilterType.DEPT_FILTER;
      this.storeColumn = { ...column };
      this.loadColumnRuleList(this.currentColumn.columnId);
    },
    onActiveColumnClick (column) {
      if (this.dirty) {
        this.$confirm('字段信息已修改，是否保存？').then(res => {
          this.onSaveColumn().then(res => {
            this.setCurrentColumn(column);
          }).catch(e => {});
        }).catch(e => {
          this.dirty = false;
          // 恢复字段属性
          if (this.storeColumn) {
            this.currentColumn.maxFileCount = this.storeColumn.maxFileCount;
            this.currentColumn.fieldKind = this.storeColumn.fieldKind;
            this.currentColumn.filterType = this.storeColumn.filterType;
          }
          // 设置当前字段为新字段
          this.setCurrentColumn(column);
        });
      } else {
        this.dirty = false;
        this.setCurrentColumn(column);
      }
    },
    onSetColumnRule () {
      this.$dialog.show('设置字段验证规则', SetOnlineTableColumnRule, {
        area: '600px'
      }, {
        column: this.currentColumn
      }).then(res => {
        this.loadColumnRuleList(this.currentColumn.columnId);
      }).catch(e => {});
    },
    onSaveColumn () {
      if (this.currentColumn == null) return Promise.reject();

      return new Promise((resolve, reject) => {
        let params = {
          onlineColumnDto: {
            ...this.currentColumn.tag,
            dictId: this.currentColumn.tag.objectFieldType === 'Boolean' ? undefined : this.currentColumn.dictId,
            fieldKind: this.currentColumn.fieldKind,
            filterType: this.currentColumn.filterType,
            maxFileCount: this.currentColumn.maxFileCount,
            columnComment: this.currentColumn.columnComment,
            userFilter: this.dataPermFilterType === this.SysOnlineDataPermFilterType.USER_FILTER,
            deptFilter: this.dataPermFilterType === this.SysOnlineDataPermFilterType.DEPT_FILTER
          }
        }

        OnlineColumnController.update(this, params).then(res => {
          this.dirty = false;
          this.$message.success('保存成功！');
          this.loadOnlineTableColumns();
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 获取在线表单导入表字段列表
     */
    loadOnlineTableColumns () {
      let params = {
        onlineColumnDtoFilter: {
          tableId: this.tableId
        }
      };
      OnlineColumnController.list(this, params).then(res => {
        this.columnMap = new Map();
        res.data.dataList.forEach(item => {
          let temp = {
            columnId: item.columnId,
            columnName: item.columnName,
            columnComment: item.columnComment,
            fullColumnType: item.fullColumnType,
            primaryKey: item.primaryKey,
            required: !item.nullable,
            fieldKind: item.fieldKind,
            maxFileCount: item.maxFileCount,
            filterType: item.filterType,
            dictId: item.dictId,
            tag: item
          }
          this.columnMap.set(temp.columnName, temp);
        });
        if (this.currentColumn != null) {
          this.$nextTick(() => {
            this.currentColumn = this.columnMap ? this.columnMap.get(this.currentColumn.columnName) : undefined;
          });
        }
      }).catch(e => {});
    },
    /**
     * 获取数据库数据表字段列表
     */
    loadDblinkTableColumns () {
      let params = {
        dblinkId: this.dblinkId,
        tableName: this.tableName
      }
      
      OnlineDblinkController.listDblinkTableColumns(this, params).then(res => {
        this.dblinkTableColumnMap = new Map();
        res.data.forEach(item => {
          this.dblinkTableColumnMap.set(item.columnName, item);
        });
      }).catch(e => {});
    },
    columnKindItemDisabled (columnKind) {
      switch (columnKind.id) {
        case this.SysOnlineFieldKind.CREATE_TIME:
        case this.SysOnlineFieldKind.UPDATE_TIME:
          return this.currentColumn.tag.objectFieldType !== 'Date';
        case this.SysOnlineFieldKind.UPLOAD:
        case this.SysOnlineFieldKind.UPLOAD_IMAGE:
        case this.SysOnlineFieldKind.RICH_TEXT:
          return this.currentColumn.tag.objectFieldType !== 'String';
        case this.SysOnlineFieldKind.LOGIC_DELETE:
        case this.SysOnlineFieldKind.FLOW_STATUS:
          return this.currentColumn.tag.objectFieldType !== 'Integer';
        default:
          return false;
      }
    }
  },
  computed: {
    getValidColumnKind () {
      return this.SysOnlineFieldKind.getList().filter(item => {
        return this.isMasterTable ? true : item.id !== this.SysOnlineFieldKind.FLOW_STATUS;
      });
    },
    getAllColumnList () {
      let columnList = [];
      if (this.columnMap != null) {
        this.columnMap.forEach((column) => {
          if (this.dblinkTableColumnMap != null) {
            let dblinkColumn = this.dblinkTableColumnMap.get(column.columnName);
            column.deletedFlag = (dblinkColumn == null);
          } else {
            column.deletedFlag = false;
          }
          columnList.push(column);
        });
      }
      return columnList;
    },
    getNewColumnList () {
      let columnList = [];
      if (this.dblinkTableColumnMap != null && this.columnMap != null) {
        this.dblinkTableColumnMap.forEach(column => {
          let onlineColumn = this.columnMap.get(column.columnName);
          if (onlineColumn == null) {
            columnList.push(column);
          }
        });
      }

      return columnList;
    }
  },
  mounted () {
    this.loadOnlineTableColumns();
    this.loadDblinkTableColumns();
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

  .table-column-list .table-column-item .refresh {
    display: none;
  }

  .table-column-list .table-column-item:hover .refresh {
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

  .column-rules {
    width: 600px;
    height: 100%;
    padding: 0px 10px 10px 10px;
    background: white;
    flex-shrink: 0;
    border-radius: 3px;
  }
</style>>
