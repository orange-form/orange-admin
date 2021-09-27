<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formEditOnlinePageDatasourceRelation" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="120px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="关联名称" prop="relationName">
            <el-input class="input-item" v-model="formData.relationName" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="关联类型">
            <el-select class="input-item" v-model="formData.relationType" placeholder="关联类型" :disabled="isEdit">
              <el-option v-for="item in SysOnlineRelationType.getList()" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="关联主表">
            <el-input :value="dblinkInfo.dblinkName + ' / ' + datasource.masterTableIdDictMap.name" readonly />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="主表关联字段" prop="masterColumnId">
            <el-select class="input-item" v-model="formData.masterColumnId" :clearable="true" filterable
              placeholder="主表关联字段" :loading="masterColumnIdWidget.loading"
              @visible-change="masterColumnIdWidget.onVisibleChange"
              @change="onMasterColumnChange">
              <el-option v-for="item in masterColumnIdWidget.dropdownList" :key="item.columnId" :value="item.columnId" :label="item.columnName">
                <el-row type="flex" justify="space-between">
                <span>{{item.columnName}}</span>
                <span style="margin-left: 30px;">{{item.columnComment}}</span>
                </el-row>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="关联从表" prop="slaveTableId">
            <el-select class="input-item" v-model="formData.slaveTableId" :clearable="true" filterable
              placeholder="关联从表" :loading="slaveTableIdWidget.loading" :disabled="isEdit"
              @visible-change="slaveTableIdWidget.onVisibleChange"
              @change="onSlaveTableChange">
              <el-option v-for="item in slaveTableIdWidget.dropdownList" :key="item.id" :value="item.id" :label="item.name">
                <span>{{item.name}}</span>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="从表关联字段" prop="slaveColumnId">
            <el-select class="input-item" v-model="formData.slaveColumnId" :clearable="true" filterable
              placeholder="关联从表" :loading="slaveColumnIdWidget.loading"
              @visible-change="slaveColumnIdWidget.onVisibleChange">
              <el-option v-for="item in slaveColumnIdWidget.dropdownList" :key="item.columnId" :value="item.columnId" :label="item.columnName">
                <span>{{item.columnName}}</span>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="是否级联删除">
            <el-switch v-model="formData.cascadeDelete" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="是否左连接">
            <el-switch v-model="formData.leftJoin" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini"
              @click="onSubmit()">
              保存
            </el-button>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { findItemFromList } from '@/utils';
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
import { DropdownWidget } from '@/utils/widget.js';
import { OnlineDatasourceRelationController, OnlineDblinkController, OnlineColumnController } from '@/api/onlineController.js';

export default {
  name: 'formEditOnlinePageDatasourceRelation',
  props: {
    datasource: {
      type: Object,
      required: true
    },
    dblinkInfo: {
      type: Object,
      required: true
    },
    relationId: {
      type: String,
      default: undefined
    },
    usedTableNameList: {
      type: Array
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      // 编辑状态下老的关联从表表名
      oldSlaveTableName: undefined,
      formData: {
        relationId: undefined,
        relationName: undefined,
        relationType: this.SysOnlineRelationType.ONE_TO_ONE,
        variableName: undefined,
        masterColumnId: undefined,
        slaveTableId: undefined,
        slaveColumnId: undefined,
        cascadeDelete: false,
        leftJoin: false
      },
      // 主表关联字段
      masterColumnIdWidget: new DropdownWidget(this.loadMasterColumnDropdownList),
      // 关联从表
      slaveTableIdWidget: new DropdownWidget(this.loadSlaveTableDropdownList),
      // 从表关联字段
      slaveColumnIdWidget: new DropdownWidget(this.loadSlaveColumnDropdownList),
      rules: {
        relationName: [
          {required: true, message: '请输入关联名称', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSubmit () {
      this.$refs.formEditOnlinePageDatasourceRelation.validate((valid) => {
        if (!valid) return;
        let masterColumn = findItemFromList(this.masterColumnIdWidget.dropdownList, this.formData.masterColumnId, 'columnId');
        let slaveTable = findItemFromList(this.slaveTableIdWidget.dropdownList, this.formData.slaveTableId, 'id');
        let slaveColumn = findItemFromList(this.slaveColumnIdWidget.dropdownList, this.formData.slaveColumnId, 'columnId');
        if (!this.isEdit) this.formData.variableName = masterColumn.columnName + '_' + slaveTable.name + '_' + slaveColumn.columnName + 'Relation';

        let params = {
          onlineDatasourceRelationDto: {
            datasourceId: this.datasource.datasourceId,
            relationId: this.formData.relationId,
            relationName: this.formData.relationName,
            relationType: this.formData.relationType,
            variableName: this.formData.variableName,
            masterColumnId: this.formData.masterColumnId,
            slaveTableId: this.isEdit ? this.formData.slaveTableId : undefined,
            slaveTableName: this.isEdit ? undefined : this.formData.slaveTableId,
            slaveColumnId: this.isEdit ? this.formData.slaveColumnId : undefined,
            slaveColumnName: this.isEdit ? undefined : this.formData.slaveColumnId,
            cascadeDelete: this.formData.cascadeDelete,
            leftJoin: this.formData.leftJoin
          }
        }

        let httpCall = this.isEdit ? OnlineDatasourceRelationController.update(this, params) : OnlineDatasourceRelationController.add(this, params);
        httpCall.then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 主表关联字段改变
     */
    onMasterColumnChange (value) {
    },
    /**
     * 获取从表下拉表列表
     */
    loadSlaveTableDropdownList () {
      return Promise.resolve(this.getValidTableList.filter(item => {
        // 可选从表为未使用的数据表
        return (this.oldSlaveTableName != null && this.oldSlaveTableName === item.name) ||
          (
            item.name !== this.datasource.masterTableIdDictMap.name &&
            (this.usedTableNameList == null || this.usedTableNameList.indexOf(item.name) === -1)
          );
      }));
    },
    /**
     * 从表改变，清空从表关联字段选中
     */
    onSlaveTableChange () {
      this.formData.slaveColumnId = null;
      this.slaveColumnIdWidget.dirty = true;
      this.formData.leftJoin = false;
      this.formData.cascadeDelete = false;
    },
    getColumnTagType (status) {
      switch (status) {
        case this.SysOnlinePageDatasourceFieldStatus.USED:
        case this.SysOnlinePageDatasourceFieldStatus.UNUSED:
          return 'success';
        case this.SysOnlinePageDatasourceFieldStatus.DELETED:
          return 'danger';
      }
    },
    /**
     * 合并表字段
     */
    mergeTableColumns (onlineTableColumns, dblinkTableColumns) {
      if (!Array.isArray(onlineTableColumns)) onlineTableColumns = [];
      if (!Array.isArray(dblinkTableColumns)) dblinkTableColumns = [];

      let columnNameSet = new Set();
      // 合并字段
      let finalColumnList = dblinkTableColumns.map(dblinkColumn => {
        let onlineColumn = findItemFromList(onlineTableColumns, dblinkColumn.columnName, 'columnName');
        columnNameSet.add(dblinkColumn.columnName);
        return {
          id: onlineColumn ? onlineColumn.columnId : dblinkColumn.columnName,
          name: dblinkColumn.columnName,
          columnType: dblinkColumn.columnType,
          columnComment: dblinkColumn.columnComment,
          status: onlineColumn ? this.SysOnlinePageDatasourceFieldStatus.USED : this.SysOnlinePageDatasourceFieldStatus.UNUSED,
          tag: onlineColumn || dblinkColumn
        }
      });
      // 添加online table里存在，但是dblink table里不存在的字段，此字段可能为被删除字段或者字段名被修改
      onlineTableColumns.forEach(onlineColumn => {
        if (!columnNameSet.has(onlineColumn.columnName)) {
          finalColumnList.push({
            id: onlineColumn.columnId,
            name: onlineColumn.columnName,
            columnType: onlineColumn.columnType,
            columnComment: onlineColumn.columnComment,
            status: this.SysOnlinePageDatasourceFieldStatus.DELETED,
            tag: onlineColumn
          })
        }
      });
      columnNameSet = null;
      return finalColumnList;
    },
    /**
     * 获取主表关联字段列表
     */
    loadMasterColumnDropdownList () {
      return new Promise((resolve, reject) => {
        this.loadOnlineTableColumns(this.datasource.masterTableId).then(res => {
          resolve(res);
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 获取从表关联字段列表
     */
    loadSlaveColumnDropdownList () {
      return new Promise((resolve, reject) => {
        // 获取从表信息
        let slaveTable = findItemFromList(this.datasource.validTableList, this.formData.slaveTableId, 'id');
        if (slaveTable == null) reject();
        if (slaveTable.status === this.SysOnlinePageDatasourceFieldStatus.USED) {
          // 从导入数据表中获取数据表字段列表
          this.loadOnlineTableColumns(slaveTable.id).then(res => {
            resolve(res);
          }).catch(e => {
            reject(e);
          });
        } else {
          // 从数据库中获取数据表字段列表
          this.loadDblinkTableColumns(this.datasource.dblinkId, slaveTable.id).then(res => {
            res.forEach(item => {
              item.columnId = item.columnName;
            });
            resolve(res);
          }).catch(e => {
            reject(e);
          })
        }
      });
    },
    /**
     * 获取在线表单导入表字段列表
     */
    loadOnlineTableColumns (tableId) {
      return new Promise((resolve, reject) => {
        if (tableId == null) {
          resolve([]);
          return;
        }
        let params = {
          onlineColumnDtoFilter: {
            tableId: tableId
          }
        };
        OnlineColumnController.list(this, params).then(res => {
          resolve(res.data.dataList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 获取数据库表字段列表
     */
    loadDblinkTableColumns (dblinkId, tableName) {
      return new Promise((resolve, reject) => {
        let params = {
          dblinkId: dblinkId,
          tableName: tableName
        };
        OnlineDblinkController.listDblinkTableColumns(this, params).then(res => {
          resolve(res.data);
        }).catch(e => {
          reject(e);
        });
      });
    }
  },
  computed: {
    isEdit () {
      return this.relationId != null && this.relationId !== '';
    },
    getValidTableList () {
      return this.datasource.validTableList;
    }
  },
  mounted () {
    if (this.isEdit) {
      OnlineDatasourceRelationController.view(this, {
        relationId: this.relationId
      }).then(res => {
        this.oldSlaveTableName = res.data.slaveTable.tableName;
        this.formData = {
          ...res.data
        }
        this.masterColumnIdWidget.onVisibleChange(true);
        this.slaveTableIdWidget.onVisibleChange(true);
        this.slaveColumnIdWidget.onVisibleChange(true);
      }).catch(e => {});
    }
  }
}
</script>

<style>
</style>
