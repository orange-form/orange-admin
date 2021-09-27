<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formEditOnlinePageDatasource" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="数据源名称" prop="datasourceName">
            <el-input class="input-item" v-model="formData.datasourceName"
              :clearable="true" placeholder="数据源名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="数据源标识" prop="variableName">
            <el-input class="input-item" v-model="formData.variableName"
              :clearable="true" placeholder="数据源标识" />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="!isEdit">
          <el-form-item label="数据源主表" prop="masterTableId">
            <el-cascader v-model="masterTablePath"
              filterable
              :disabled="isEdit"
              :props="masterTableProps"
              @change="onMasterTableChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-else>
          <el-form-item label="数据源主表">
            <el-input class="input-item" v-model="(masterTableIdDictMap || {}).name" :disabled="true" />
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
import { OnlineDatasourceController, OnlineDblinkController } from '@/api/onlineController.js';

export default {
  name: 'formEditOnlinePageDatasource',
  props: {
    pageId: {
      type: String,
      required: true
    },
    datasourceId: {
      type: String,
      default: undefined
    },
    dblinkInfo: {
      type: Object
    }
  },
  data () {
    return {
      masterTableIdDictMap: {},
      formData: {
        datasourceId: this.datasourceId,
        datasourceName: undefined,
        variableName: undefined,
        dblinkId: undefined,
        masterTableId: undefined
      },
      masterTablePath: [],
      rules: {
        datasourceName: [
          {required: true, message: '数据源名称不能为空！', trigger: 'blur'}
        ],
        variableName: [
          {required: true, message: '数据源标识不能为空！', trigger: 'blur'},
          {type: 'string', pattern: /^[a-z][a-zA-Z]+$/, message: '数据源标识必须是小驼峰命名', trigger: 'blur'}
        ],
        masterTableId: [
          {required: true, message: '数据源主表不能为空！', trigger: 'blur'}
        ]
      },
      masterTableProps: {
        lazy: true,
        lazyLoad: this.loadMasterTableData
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
      this.$refs.formEditOnlinePageDatasource.validate((valid) => {
        if (!valid) return;

        let params = {
          pageId: this.pageId,
          onlineDatasourceDto: {
            datasourceId: this.formData.datasourceId,
            datasourceName: this.formData.datasourceName,
            variableName: this.formData.variableName,
            dblinkId: this.formData.dblinkId,
            masterTableName: this.formData.masterTableId,
            masterTableId: this.isEdit ? this.formData.masterTableId : undefined
          }
        }

        let httpCall = this.isEdit ? OnlineDatasourceController.update(this, params) : OnlineDatasourceController.add(this, params);
        httpCall.then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    onMasterTableChange (values) {
      this.formData.dblinkId = values[0];
      this.formData.masterTableId = values[1];
    },
    /**
     * 获取字典表级联数据
     */
    loadMasterTableData (node, resolve) {
      if (node.level === 0) {
        // 获取dblink信息
        if (this.dblinkInfo == null) {
          OnlineDblinkController.list(this, {}).then(res => {
            resolve(res.data.dataList.map(item => {
              return {
                value: item.dblinkId,
                label: item.dblinkName,
                leaf: false
              }
            }));
          }).catch(e => {
            node.loaded = false;
            node.loading = false;
          });
        } else {
          resolve(Object.keys(this.dblinkInfo).map(key => {
            let dblinkItem = this.dblinkInfo[key];
            return {
              value: dblinkItem.dblinkId,
              label: dblinkItem.dblinkName,
              leaf: false
            }
          }));
        }
      } else if (node.level === 1) {
        OnlineDblinkController.listDblinkTables(this, {
          dblinkId: node.data.value
        }).then(res => {
          resolve(res.data.map(item => {
            return {
              value: item.tableName,
              label: item.tableName,
              leaf: true
            }
          }));
        }).catch(e => {
          node.loaded = false;
          node.loading = false;
        });
      }
    }
  },
  computed: {
    isEdit () {
      return this.datasourceId != null && this.datasourceId !== '';
    }
  },
  mounted () {
    if (this.datasourceId != null) {
      OnlineDatasourceController.view(this, {
        datasourceId: this.datasourceId
      }).then(res => {
        this.formData.datasourceId = res.data.datasourceId;
        this.formData.datasourceName = res.data.datasourceName;
        this.formData.dblinkId = res.data.dblinkId;
        this.formData.masterTableId = res.data.masterTableId;
        this.formData.variableName = res.data.variableName;
        this.masterTablePath = [this.formData.dblinkId, this.formData.masterTableId];
        this.masterTableIdDictMap = res.data.masterTableIdDictMap;
      }).catch(e => {});
    }
  }
}
</script>

<style>
</style>
