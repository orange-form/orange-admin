<template>
  <div class="edit-online-form">
    <el-container>
      <el-header>
        <el-row type="flex" justify="space-between">
          <el-col class="title header" :span="8">
            <i class="el-icon-orange" />
            <span>橙单流程设计</span>
          </el-col>
          <el-col :span="8">
            <el-steps :active="activeStep" finish-status="success" simple style="margin-top: 7px;">
              <el-step title="基础信息" style="max-width: 250px"></el-step>
              <el-step title="流程变量" style="max-width: 250px"></el-step>
              <el-step title="流程设计" style="max-width: 250px"></el-step>
            </el-steps>
          </el-col>
          <el-col :span="8">
            <el-row type="flex" justify="end" align="middle" style="height: 60px;">
              <el-button size="small" @click="onPrevClick"
                :disabled="activeStep === SysFlowEntryStep.BASIC"
              >
                上一步
              </el-button>
              <el-button size="small" @click="onNextClick" :disabled="activeStep === SysFlowEntryStep.PROCESS_DESIGN">下一步</el-button>
              <el-button size="small" type="primary" @click="onClose(false)">退出</el-button>
            </el-row>
          </el-col>
        </el-row>
      </el-header>
      <el-main style="background: #EBEEF5; padding: 10px;" :style="{height: getClientHeight - 60 + 'px'}">
        <el-row type="flex" justify="center" style="height: 100%;">
          <!-- 流程基础信息设置 -->
          <el-col v-if="activeStep === SysFlowEntryStep.BASIC" class="main-box" style="width: 600px;" :span="9">
            <el-form ref="entryBasicInfo" class="full-width-input" size="small" :model="formFlowEntryData" :rules="formRules"
              label-position="right" label-width="120px" @submit.native.prevent>
              <el-col :span="24">
                <el-form-item label="表单类型" prop="bindFormType">
                  <el-select v-model="formFlowEntryData.bindFormType" placeholder="" @change="onBindFormTypeChange" :disabled="isEdit">
                    <el-option :label="SysFlowEntryBindFormType.getValue(SysFlowEntryBindFormType.ROUTER_FORM)" :value="SysFlowEntryBindFormType.ROUTER_FORM" />
                    <el-option :label="SysFlowEntryBindFormType.getValue(SysFlowEntryBindFormType.ONLINE_FORM)" :value="SysFlowEntryBindFormType.ONLINE_FORM" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="流程名称" prop="processDefinitionName">
                  <el-input v-model="formFlowEntryData.processDefinitionName" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="流程标识" prop="processDefinitionKey">
                  <el-input v-model="formFlowEntryData.processDefinitionKey" :disabled="isEdit" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="流程分类" prop="categoryId">
                  <el-select v-model="formFlowEntryData.categoryId"
                    placeholder="" :loading="categoryIdWidget.loading"
                    @visible-change="categoryIdWidget.onVisibleChange">
                    <el-option v-for="item in categoryIdWidget.dropdownList"
                      :key="item.id" :value="item.id" :label="item.name" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24" v-if="formFlowEntryData.bindFormType === SysFlowEntryBindFormType.ONLINE_FORM">
                <el-form-item label="流程页面" prop="pageId">
                  <el-select v-model="formFlowEntryData.pageId"
                    :disabled="isEdit"
                    placeholder="" :loading="pageIdWidget.loading"
                    @visible-change="pageIdWidget.onVisibleChange"
                    @change="onEntryPageChange"
                  >
                    <el-option v-for="item in pageIdWidget.dropdownList"
                      :key="item.pageId" :value="item.pageId" :label="item.pageName" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24" v-if="formFlowEntryData.bindFormType === SysFlowEntryBindFormType.ONLINE_FORM">
                <el-form-item label="默认在线表单" prop="defaultFormId">
                  <el-select v-model="formFlowEntryData.defaultFormId"
                    placeholder="" :loading="defaultFormIdWidget.loading"
                    @visible-change="defaultFormIdWidget.onVisibleChange"
                  >
                    <el-option v-for="item in defaultFormIdWidget.dropdownList"
                      :key="item.formId" :value="item.formId" :label="item.formName" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24" v-if="formFlowEntryData.bindFormType === SysFlowEntryBindFormType.ROUTER_FORM">
                <el-form-item label="默认路由表单" prop="defaultRouterName">
                  <el-input v-model="formFlowEntryData.defaultRouterName" clearable />
                </el-form-item>
              </el-col>
            </el-form>
          </el-col>
          <!-- 流程变量设置 -->
          <el-col v-if="activeStep === SysFlowEntryStep.PROCESS_VARIABLE" class="main-box" :span="16">
            <el-table :data="processVariableList" header-cell-class-name="table-header-gray" key="processVariableList">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" />
              <el-table-column label="变量名称" prop="showName" />
              <el-table-column label="变量标识" prop="variableName" />
              <el-table-column label="变量类型">
                <template slot-scope="scope">
                  <el-tag size="mini" effect="dark"
                    :type="scope.row.variableType === SysFlowVariableType.TASK ? 'primary' : 'success'">
                    {{SysFlowVariableType.getValue(scope.row.variableType)}}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="内置变量">
                <template slot-scope="scope">
                  <el-tag size="mini" effect="dark" :type="scope.row.builtIn ? 'success' : 'danger'">
                    {{scope.row.builtIn ? '是' : '否'}}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                  <el-button class="table-btn success" size="mini" type="text" :disabled="scope.row.builtin" @click="editEntryVariable(scope.row)">编辑</el-button>
                  <el-button class="table-btn delete" size="mini" type="text" :disabled="scope.row.builtin" @click="deleteEntryVariable(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button class="btn-add" icon="el-icon-plus" @click="addEntryVariable">添加变量</el-button>
          </el-col>
          <!-- 流程设计 -->
          <el-col v-if="activeStep === SysFlowEntryStep.PROCESS_DESIGN" class="main-box" :span="24" style="min-width: 1100px; padding: 0px">
            <ProcessDesigner :flowEntryInfo="formFlowEntryData" @save="onSaveFlowEntry" />
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
/* eslint-disable-next-line */
import rules from '@/utils/validate.js';
/* eslint-disable-next-line */
import { DropdownWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
import ProcessDesigner from '../components/ProcessDesigner.vue';
/* eslint-disable-next-line */
import { FlowEntryController, FlowEntryVariableController, FlowDictionaryController } from '@/api/flowController.js';
import {
  OnlinePageController,
  OnlineFormController,
  OnlineColumnController,
  OnlineDatasourceRelationController,
  OnlineVirtualColumnController
} from '@/api/onlineController.js';
import EditFlowEntryVariable from './formEditFlowEntryVariable.vue';

export default {
  name: 'formEditFlowEntry',
  props: {
    flowEntry: {
      type: Object
    }
  },
  components: {
    ProcessDesigner
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  provide () {
    return {
      flowEntry: () => this.formFlowEntryData,
      formList: () => this.defaultFormIdWidget ? this.defaultFormIdWidget.dropdownList : [],
      allVariableList: () => this.processVariableList
    }
  },
  data () {
    return {
      entryDatasource: undefined,
      activeStep: this.SysFlowEntryStep.BASIC,
      formFlowEntryData: {
        entryId: undefined,
        processDefinitionName: undefined,
        processDefinitionKey: undefined,
        categoryId: undefined,
        bindFormType: this.SysFlowEntryBindFormType.ROUTER_FORM,
        pageId: undefined,
        defaultFormId: undefined,
        defaultRouterName: undefined,
        bpmnXml: undefined
      },
      categoryIdWidget: new DropdownWidget(this.loadCategoryIdDropdownList),
      pageIdWidget: new DropdownWidget(this.loadPageIdDropdownList),
      defaultFormIdWidget: new DropdownWidget(this.loadDefaultFormIdDropdownList),
      processVariableList: [],
      formRules: {
        processDefinitionKey: [
          {required: true, message: '流程标识不能为空！', trigger: 'blur'},
          {type: 'string', pattern: /^[A-Za-z0-9]+$/, message: '流程标识只允许输入字母和数字', trigger: 'blur'},
          {type: 'string', pattern: /^[A-Za-z][A-Za-z0-9]+$/, message: '流程标识不能以数字开头', trigger: 'blur'}
        ],
        processDefinitionName: [
          {required: true, message: '流程名称不能为空！', trigger: 'blur'}
        ],
        categoryId: [
          {required: true, message: '流程分类不能为空！', trigger: 'blur'}
        ],
        pageId: [
          {required: true, message: '流程页面不能为空！', trigger: 'blur'}
        ],
        defaultFormId: [
          {required: true, message: '默认在线表单不能为空！', trigger: 'blur'}
        ],
        defaultRouterName: [
          {required: true, message: '默认路由表单不能为空！', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    onClose () {
      this.$emit('close');
    },
    // 流程绑定表单类型改变
    onBindFormTypeChange () {
      this.formFlowEntryData.pageId = undefined;
      this.formFlowEntryData.defaultFormId = undefined;
      this.formFlowEntryData.defaultRouterName = undefined;
      this.entryDatasource = undefined;
    },
    // 流程绑定表单页面改变
    onEntryPageChange () {
      this.formFlowEntryData.defaultFormId = undefined;
      this.defaultFormIdWidget.dirty = true;
      this.entryDatasource = null;
    },
    // 获取流程分类
    loadCategoryIdDropdownList () {
      return new Promise((resolve, reject) => {
        FlowDictionaryController.dictFlowCategory(this, {}).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    // 获取在线表单列表
    loadPageIdDropdownList () {
      return new Promise((resolve, reject) => {
        OnlinePageController.list(this, {
          onlinePageDtoFilter: {
            pageType: this.SysOnlinePageType.FLOW
          }
        }).then(res => {
          resolve(res.data.dataList.filter(item => item.published));
        }).catch(e => {
          reject(e);
        });
      });
    },
    // 获取默认表单页面列表
    loadDefaultFormIdDropdownList () {
      return new Promise((resolve, reject) => {
        if (this.formFlowEntryData.pageId == null || this.formFlowEntryData.pageId === '') {
          resolve([]);
          return;
        }
        OnlineFormController.list(this, {
          onlineFormDtoFilter: {
            pageId: this.formFlowEntryData.pageId
          },
          orderParam: [
            {
              fieldName: 'createTime',
              asc: true
            }
          ]
        }).then(res => {
          resolve(res.data.dataList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    onPrevClick () {
      switch (this.activeStep) {
        case this.SysFlowEntryStep.PROCESS_VARIABLE:
          this.activeStep = this.SysFlowEntryStep.BASIC;
          break;
        case this.SysFlowEntryStep.PROCESS_DESIGN:
          this.activeStep = this.SysFlowEntryStep.PROCESS_VARIABLE;
          break;
      }
    },
    onNextClick () {
      switch (this.activeStep) {
        case this.SysFlowEntryStep.BASIC:
          this.$refs.entryBasicInfo.validate(valid => {
            if (!valid) return;
            // 保存流程基本信息
            this.onSaveFlowEntryBasicInfo().then(res => {
              if (!this.isEdit) this.formFlowEntryData.entryId = res.data;
              this.$message.success('保存成功');
              // 获取流程页面数据源信息
              this.initFlowDatasourceInfo().then(res => {
                // 获取流程变量
                return this.loadEntryVariableList();
              }).then(res => {
                this.activeStep = this.SysFlowEntryStep.PROCESS_VARIABLE;
              }).catch(e => {
                console.log(e);
              });
            }).catch(e => {
              console.log(e);
            });
          });
          break;
        case this.SysFlowEntryStep.PROCESS_VARIABLE:
          this.activeStep = this.SysFlowEntryStep.PROCESS_DESIGN;
          break;
      }
    },
    // 初始化流程基础信息
    initFlowEntryInfo () {
      this.formFlowEntryData = {
        processDefinitionName: undefined,
        processDefinitionKey: undefined,
        categoryId: undefined,
        bindFormType: this.SysFlowEntryBindFormType.ONLINE_FORM,
        pageId: undefined,
        defaultFormId: undefined,
        defaultRouterName: undefined,
        bpmnXml: undefined
      }
      this.activeStep = this.SysFlowEntryStep.BASIC;

      FlowEntryController.view(this, {
        entryId: this.flowEntry.entryId
      }).then(res => {
        this.formFlowEntryData = {
          ...this.formFlowEntryData,
          ...res.data
        }
        if (this.formFlowEntryData.bindFormType === this.SysFlowEntryBindFormType.ONLINE_FORM) {
          this.defaultFormIdWidget.onVisibleChange(true);
        }
      }).catch(e => {});
    },
    // 保存流程基础信息
    saveFlowEntryInfo (step) {
      let params = {
        flowEntryDto: {
          ...this.formFlowEntryData,
          step
        }
      }

      return this.isEdit ? FlowEntryController.update(this, params) : FlowEntryController.add(this, params);
    },
    onSaveFlowEntry (xml) {
      this.formFlowEntryData.bpmnXml = xml;
      this.onSaveFlowEntryBasicInfo().then(res => {
        this.$message.success('保存成功');
      }).catch(e => {});
    },
    onSaveFlowEntryBasicInfo () {
      return this.saveFlowEntryInfo(this.SysFlowEntryStep.DATASOURCE);
    },
    // 获取流程绑定页面数据源信息
    initFlowEntryDatasourceInfo (entryId) {
      return FlowEntryController.viewDatasource(this, {
        entryId
      });
    },
    /**
     * 获取数据模型关联信息
     */
    loadDatasourceRelation () {
      if (this.entryDatasource == null) return Promise.resolve();
      return OnlineDatasourceRelationController.list(this, {
        onlineDatasourceRelationDtoFilter: {
          datasourceId: this.entryDatasource.datasourceId
        }
      });
    },
    // 获取在线表单数据表字段列表
    loadOnlineTableColumns (tableId, owner) {
      if (tableId == null || tableId === '') return Promise.reject();

      return new Promise((resolve, reject) => {
        let params = {
          onlineColumnDtoFilter: {
            tableId
          }
        }

        OnlineColumnController.list(this, params).then(res => {
          owner.columnList = res.data.dataList;
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 获取数据源下所有表字段
     */
    loadDatasourceAllColumnList () {
      if (this.entryDatasource == null) return Promise.resolve();
      let allHttpCalls = [
        this.loadOnlineTableColumns(this.entryDatasource.masterTableId, this.entryDatasource)
      ];
      this.entryDatasource.relationList.forEach(relation => {
        if (relation.relationType === this.SysOnlineRelationType.ONE_TO_ONE) {
          allHttpCalls.push(this.loadOnlineTableColumns(relation.slaveTableId, relation));
        }
      });

      return Promise.all(allHttpCalls);
    },
    // 获取流程绑定页面数据源信息
    initFlowDatasourceInfo () {
      return new Promise((resolve, reject) => {
        if (this.entryDatasource != null) return resolve();
        if (this.formFlowEntryData.bindFormType === this.SysFlowEntryBindFormType.ONLINE_FORM) {
          OnlinePageController.listOnlinePageDatasource(this, {
            pageId: this.formFlowEntryData.pageId
          }).then(res => {
            this.entryDatasource = res.data.dataList[0];
            return this.loadDatasourceRelation();
          }).then(res => {
            this.entryDatasource.relationList = res.data.dataList || [];
            return this.loadDatasourceAllColumnList();
          }).then(res => {
            return OnlineVirtualColumnController.list(this, {
              onlineVirtualColumnDtoFilter: {
                datasourceId: this.entryDatasource.datasourceId
              }
            });
          }).then(res => {
            let virtualColumnList = res.data.dataList;
            if (Array.isArray(virtualColumnList)) {
              if (!Array.isArray(this.entryDatasource.columnList)) this.entryDatasource.columnList = [];
              this.entryDatasource.columnList.push(...virtualColumnList.map(item => {
                return {
                  ...item,
                  columnId: item.virtualColumnId,
                  columnName: item.objectFieldName,
                  columnComment: item.columnPrompt
                }
              }));
            }
            resolve();
          }).catch(e => {
            reject(e);
          });
        } else {
          this.entryDatasource = null;
          resolve();
        }
      });
    },
    // 获取流程变量列表
    loadEntryVariableList () {
      return new Promise((resolve, reject) => {
        let params = {
          flowEntryVariableDtoFilter: {
            entryId: this.formFlowEntryData.entryId || this.flowEntry.entryId
          }
        }

        FlowEntryVariableController.list(this, params).then(res => {
          this.processVariableList = res.data.dataList;
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    addEntryVariable () {
      this.$dialog.show('添加变量', EditFlowEntryVariable, {
        area: '500px'
      }, {
        entryId: this.formFlowEntryData.entryId || this.flowEntry.entryId,
        datasource: this.entryDatasource
      }).then(res => {
        this.loadEntryVariableList();
      }).catch(e => {});
    },
    editEntryVariable (row) {
      this.$dialog.show('编辑变量', EditFlowEntryVariable, {
        area: '500px'
      }, {
        entryId: this.formFlowEntryData.entryId || this.flowEntry.entryId,
        datasource: this.entryDatasource,
        rowData: row
      }).then(res => {
        this.loadEntryVariableList();
      }).catch(e => {});
    },
    deleteEntryVariable (row) {
      this.$confirm('是否删除此流程变量？').then(res => {
        let params = {
          variableId: row.variableId
        }

        return FlowEntryVariableController.delete(this, params);
      }).then(res => {
        this.$message.success('删除成功！');
        this.loadEntryVariableList();
      }).catch(e => {});
    }
  },
  computed: {
    isEdit () {
      return this.flowEntry != null || this.formFlowEntryData.entryId != null;
    },
    ...mapGetters(['getClientHeight'])
  },
  mounted () {
    // 初始化页面数据
    this.categoryIdWidget.onVisibleChange(true);
    this.pageIdWidget.onVisibleChange(true);
    if (this.isEdit) {
      this.initFlowEntryInfo();
    }
  },
  watch: {
  }
}
</script>

<style scoped>
  .edit-online-form {
    position: fixed;
    width: 100vw;
    height: 100vh;
    background: white;
    top: 0px;
    left: 0px;
    z-index: 100
  }
  .edit-online-form >>> .el-steps--simple {
    background: white!important;
    padding: 13px 0px;
    width: 800px;
  }
  .edit-online-form >>> .el-table th,.edit-online-form  >>> .el-table td {
    padding: 6px 0px;
  }
  .edit-online-form >>> .el-scrollbar__bar {
    display: none;
  }
  .edit-online-form .header {
    height: 60px;
    line-height: 60px;
  }
  .edit-online-form .title {
    font-size: 24px
  }

  .edit-online-form .title > i {
    color: #FDA834;
    margin-right: 10px;
  }

  .edit-online-form .main-box {
    padding: 20px;
    background: white;
    height: 100%;
  }

  .edit-online-form .btn-add {
    width: 100%;
    margin-top: 10px;
    border: 1px dashed #EBEEF5;
  }
</style>
