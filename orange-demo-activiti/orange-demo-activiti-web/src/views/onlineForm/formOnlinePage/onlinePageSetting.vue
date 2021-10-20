<template>
  <div class="edit-online-form">
    <el-container>
      <el-header>
        <el-row>
          <el-col class="title header" :span="6" style="">
            <i class="el-icon-orange" />
            <span>橙单在线表单</span>
          </el-col>
          <el-col :span="12">
            <el-steps :active="activeStep" finish-status="success" simple style="margin-top: 7px;">
              <el-step title="基础信息"></el-step>
              <el-step title="数据模型"></el-step>
              <el-step title="表单设计"></el-step>
            </el-steps>
          </el-col>
          <el-col :span="6">
            <el-row type="flex" justify="end" align="middle" style="height: 60px;" v-if="virtualColumnTable == null && currentTable == null && currentForm == null">
              <el-button size="small" @click="onPrevClick"
                :disabled="activeStep === SysOnlinePageSettingStep.BASIC"
              >
                上一步
              </el-button>
              <el-button size="small" @click="onNextClick" :disabled="activeStep === SysOnlinePageSettingStep.FORM_DESIGN">下一步</el-button>
              <el-button size="small" type="primary" @click="onClose(false)">退出</el-button>
            </el-row>
          </el-col>
        </el-row>
      </el-header>
      <el-main style="background: #EBEEF5; padding: 10px;" :style="{height: getClientHeight - 60 + 'px'}">
        <el-row type="flex" justify="center" style="height: 100%;">
          <!-- 在线表单基础信息设置 -->
          <el-col v-if="activeStep === SysOnlinePageSettingStep.BASIC" class="main-box" style="width: 600px;" :span="9">
            <el-form ref="pageBasicInfo" class="full-width-input" size="small" :model="formPageData" :rules="formRules"
              label-position="right" label-width="100px" @submit.native.prevent>
              <el-col :span="24">
                <el-form-item label="页面类型">
                  <el-select v-model="formPageData.pageType" :disabled="isEdit">
                    <el-option :value="SysOnlinePageType.BIZ" :label="SysOnlinePageType.getValue(SysOnlinePageType.BIZ)" />
                    <el-option :value="SysOnlinePageType.FLOW" :label="SysOnlinePageType.getValue(SysOnlinePageType.FLOW)" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="页面编码" prop="pageCode">
                  <el-input v-model="formPageData.pageCode" :disabled="isEdit" @change="dirty = true" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="页面名称" prop="pageName">
                  <el-input v-model="formPageData.pageName" @change="dirty = true" />
                </el-form-item>
              </el-col>
            </el-form>
          </el-col>
          <!-- 在线表单数据模型配置 -->
          <el-col v-if="activeStep === SysOnlinePageSettingStep.DATASOURCE && currentTable == null && virtualColumnTable == null" class="main-box" :span="24" style="min-width: 1100px;">
            <el-table :data="getPageDatasourceTableList" header-cell-class-name="table-header-gray">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" />
              <el-table-column label="数据表名" prop="tableName" />
              <el-table-column label="关联类型" prop="relationType">
                <template slot-scope="scope">
                  <el-tag size="mini" :type="getDatasourceTableTagType(scope.row.relationType)" effect="dark">
                    {{getDatasourceTableTagName(scope.row.relationType)}}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="主表关联字段" prop="masterColumnName" />
              <el-table-column label="从表关联字段" prop="slaveColumnName" />
              <el-table-column label="级联删除" prop="cascadeDelete">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.relationType != null" size="mini"
                    :type="scope.row.cascadeDelete ? 'success' : 'danger'" effect="dark">
                    {{scope.row.cascadeDelete ? '是' : '否'}}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="是否左连接" prop="leftJoin">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.relationType != null" size="mini"
                    :type="scope.row.leftJoin ? 'success' : 'danger'" effect="dark">
                    {{scope.row.leftJoin ? '是' : '否'}}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="250px" fixed="right">
                <template slot-scope="scope">
                  <!-- 数据源主表只有当没有任何关联的时候才可以编辑 -->
                  <el-button class="table-btn success" size="mini" type="text"
                    @click="onEditDatasourceTable(scope.row)"
                    :disabled="scope.row.relationType == null && Array.isArray(scope.row.relationList) && scope.row.relationList.length > 0"
                  >
                    编辑
                  </el-button>
                  <el-button size="mini" type="text"
                    @click="onEditTableColumn(scope.row)">
                    字段管理
                  </el-button>
                  <el-button size="mini" type="text"
                    :disabled="scope.row.relationType != null"
                    @click="onEditVirtualColumn(scope.row)">
                    聚合计算
                  </el-button>
                  <el-button class="table-btn delete" size="mini" type="text"
                    :disabled="scope.row.relationType == null && (!Array.isArray(scope.row.relationList) || scope.row.relationList.length <= 0)"
                    @click="onDeleteDatasourceTable(scope.row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button class="btn-add" icon="el-icon-plus" @click="onAddDatasourceClick">新增数据表</el-button>
          </el-col>
          <!-- 编辑数据表字段验证规则 -->
          <el-col v-if="activeStep === SysOnlinePageSettingStep.DATASOURCE && currentTable != null" :span="24">
            <OnlinePageTableColumnRule
              :isMasterTable="currentTable.relationType == null"
              :tableId="currentTable.tableId"
              :dblinkId="getPageDatasource.dblinkId"
              :tableName="currentTable.tableName"
              :dictList="dictList"
              :height="getClientHeight - 80"
              @close="onRuleClose"
            />
          </el-col>
          <!-- 数据源虚拟字段设置 -->
          <el-col v-if="activeStep === SysOnlinePageSettingStep.DATASOURCE && virtualColumnTable != null" :span="24">
            <OnlinePageVirtualColumn
              :datasource="virtualColumnTable.tag"
              :relationList="virtualColumnTable.tag.relationList"
              :height="getClientHeight - 80"
              @close="onVirtualColumnClose"
            />
          </el-col>
          <!-- 在线表单子表单列表 -->
          <el-col v-if="activeStep === SysOnlinePageSettingStep.FORM_DESIGN && currentForm == null" class="main-box" :span="16">
            <el-table :data="formList" header-cell-class-name="table-header-gray" key="onlineForm">
              <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" />
              <el-table-column label="表单标识" prop="formCode" />
              <el-table-column label="表单名称" prop="formName" />
              <el-table-column label="表单类型" prop="formType">
                <template slot-scope="scope">
                  <el-tag size="mini" effect="dark"
                    :type="getFormTypeTag(scope.row.formType)"
                  >
                    {{SysOnlineFormType.getValue(scope.row.formType)}}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150px">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" @click="onDesignPageForm(scope.row)">页面布局</el-button>
                  <el-button class="table-btn success" size="mini" type="text" @click="onEditPageForm(scope.row)">编辑</el-button>
                  <el-button class="table-btn delete" size="mini" type="text" style="color: #F56C6C;" @click="onDeletePageForm(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button class="btn-add" icon="el-icon-plus" @click="onEditPageForm()">添加表单</el-button>
          </el-col>
          <!-- 在线表单子表单设计 -->
          <FormGenerator v-if="currentForm != null" style="width: 100%;"
            :pageType="formPageData.pageType"
            :height="getClientHeight - 80"
            :form="currentForm"
            :tableList="getTableInfo"
            :datasourceTableList="getPageDatasourceTableList"
            :formList="formList"
            :formDatasourceList="pageDatasourceList"
            @tableCreate="onQueryCreateClick"
            @close="onCloseFormDesign"
          />
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { findItemFromList } from '@/utils';
import OnlinePageTableColumnRule from './onlinePageTableColumnRule.vue';
import OnlinePageVirtualColumn from './onlinePageVirtualColumn.vue';
import FormGenerator from '../components/formGenerator.vue';
import {
  OnlinePageController,
  OnlineDatasourceController,
  OnlineDatasourceRelationController,
  OnlineFormController,
  OnlineDblinkController,
  OnlineColumnController,
  OnlineDictController,
  OnlineVirtualColumnController
} from '@/api/onlineController.js';
import EditOnlinePageDatasource from './editOnlinePageDatasource.vue';
import EditOnlinePageDatasourceRelation from './editOnlinePageDatasourceRelation.vue';
import EditOnlineForm from './editOnlineForm.vue';

export default {
  name: 'onlinePageSetting',
  props: {
    pageId: {
      type: String,
      default: undefined
    }
  },
  components: {
    FormGenerator,
    OnlinePageTableColumnRule,
    OnlinePageVirtualColumn
  },
  data () {
    return {
      dirty: false,
      activeStep: this.SysOnlinePageSettingStep.BASIC,
      // 页面基础信息
      formPageData: {
        pageId: undefined,
        pageCode: undefined,
        pageName: undefined,
        published: false,
        pageType: this.SysOnlinePageType.BIZ,
        status: this.SysOnlinePageStatus.BASIC
      },
      dictList: [],
      // 页面数据模型列表
      pageDatasourceList: [],
      // 子表单列表
      formList: [],
      // 数据库信息
      dblinkInfo: {},
      // 当前选中表单
      currentForm: undefined,
      currentTable: undefined,
      virtualColumnTable: undefined,
      formRules: {
        pageCode: [
          {required: true, message: '页面编码不能为空！', trigger: 'blur'},
          {type: 'string', pattern: /^[A-Za-z0-9]+$/, message: '页面编码只允许输入字母和数字', trigger: 'blur'}
        ],
        pageName: [
          {required: true, message: '页面名称不能为空！', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    onClose () {
      this.$emit('close');
    },
    onCloseFormDesign () {
      this.currentForm = null;
      this.initPageFormList(this.formPageData.pageId).catch(e => {});
    },
    onQueryCreateClick () {

    },
    getFormTypeTag (formType) {
      switch (formType) {
        case this.SysOnlineFormType.QUERY: return 'success';
        case this.SysOnlineFormType.FORM: return 'primary';
        case this.SysOnlinePageType.FLOW: return 'warning';
        default: return 'info';
      }
    },
    /**
     * 点击上一步
     */
    onPrevClick () {
      switch (this.activeStep) {
        case this.SysOnlinePageSettingStep.DATASOURCE:
          this.onSavePageDatasourceInfo(true).then(res => {
            this.activeStep = this.SysOnlinePageSettingStep.BASIC;
          }).catch(e => {});
          break;
        case this.SysOnlinePageSettingStep.FORM_DESIGN:
          this.dirty = false;
          this.activeStep = this.SysOnlinePageSettingStep.DATASOURCE;
          break;
      }
    },
    /**
     * 点击下一步
     */
    onNextClick () {
      switch (this.activeStep) {
        case this.SysOnlinePageSettingStep.BASIC:
          this.onSavePageBasicInfo().then(res => {
            return this.initPageDatasourceInfo(this.formPageData.pageId);
          }).then(res => {
            this.activeStep = this.SysOnlinePageSettingStep.DATASOURCE;
          }).catch(e => {});
          break;
        case this.SysOnlinePageSettingStep.DATASOURCE:
          this.onSavePageDatasourceInfo(false).then(res => {
            // Step 1 获取数据源所有用到的数据表的字段列表
            let httpCalls = [];
            this.getPageDatasourceTableList.forEach(item => {
              httpCalls.push(this.loadOnlineTableColumns(item.tableId))
            });
            return Promise.all(httpCalls)
          }).then(res => {
            res.forEach((item, index) => {
              this.getPageDatasourceTableList[index].columnList = item;
              return this.getPageDatasourceTableList[index];
            });
            // Step 2 获取表单列表
            return this.initPageFormList(this.formPageData.pageId);
          }).then(res => {
            // Step 3 进入表单设计页面
            this.activeStep = this.SysOnlinePageSettingStep.FORM_DESIGN;
          }).catch(e => {});
      }
    },
    /**
     * 保存页面基础信息
     */
    savePageInfo (status) {
      let params = {
        onlinePageDto: {
          ...this.formPageData,
          status: status,
          published: false
        }
      }
      return this.isEdit ? OnlinePageController.update(this, params) : OnlinePageController.add(this, params);
    },
    /**
     * 保存页面基础信息
     */
    onSavePageBasicInfo () {
      return new Promise((resolve, reject) => {
        this.$refs.pageBasicInfo.validate(valid => {
          if (!valid || !this.dirty) {
            valid ? resolve() : reject();
            return;
          }
          this.savePageInfo(this.SysOnlinePageStatus.DATASOURCE).then(res => {
            this.$message.success('保存页面基础信息成功！');
            if (!this.isEdit) this.formPageData.pageId = res.data;
            this.dirty = false;
            resolve();
          }).catch(e => {
            reject(e);
          });
        });
      });
    },
    /**
     * 保存页面数据模型信息
     */
    onSavePageDatasourceInfo (isPrev = false) {
      // 如果是上一步，直接返回上一步
      if (isPrev) return Promise.resolve();
      // 下一步需判断是否添加了数据源
      if (this.getPageDatasource == null) {
        this.$message.error('请设置页面数据模型！');
        if (this.formPageData.status !== this.SysOnlinePageStatus.DATASOURCE) {
          this.savePageInfo(this.SysOnlinePageStatus.DATASOURCE).catch(e => {});
        }
        return Promise.reject();
      } else {
        if (this.formPageData.status === this.SysOnlinePageStatus.DESIGNING) {
          return Promise.resolve();
        } else {
          return this.savePageInfo(this.SysOnlinePageStatus.DESIGNING);
        }
      }
    },
    /**
     * 初始化页面基础信息
     */
    initPageInfo () {
      this.formPageData = {
        pageId: undefined,
        pageCode: undefined,
        pageName: undefined,
        pageType: this.SysOnlinePageType.BIZ,
        status: this.SysOnlinePageStatus.BASIC
      };
      this.activeStep = this.SysOnlinePageSettingStep.BASIC;

      if (this.pageId != null || this.formPageData.pageId != null) {
        let params = {
          pageId: this.pageId || this.formPageData.pageId
        }

        return OnlinePageController.view(this, params);
      } else {
        return Promise.resolve();
      }
    },
    /**
     * 获取数据模型关联信息
     */
    loadDatasourceRelation () {
      if (this.getPageDatasource == null) return Promise.resolve();

      return OnlineDatasourceRelationController.list(this, {
        onlineDatasourceRelationDtoFilter: {
          datasourceId: this.getPageDatasource.datasourceId
        }
      });
    },
    /**
     * 初始化页面数据模型信息
     */
    initPageDatasourceInfo (pageId) {
      return new Promise((resolve, reject) => {
        OnlinePageController.listOnlinePageDatasource(this, {
          pageId: pageId
        }).then(res => {
          this.pageDatasourceList = res.data.dataList;
          return this.loadDatasourceRelation();
        }).then(res => {
          this.pageDatasourceList = this.pageDatasourceList.map(item => {
            if (item.datasourceId === this.getPageDatasource.datasourceId) {
              item.relationList = res.data.dataList || [];
            }
            return item;
          });
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 获取页面子表单列表
     */
    initPageFormList (pageId) {
      return new Promise((resolve, reject) => {
        OnlineFormController.list(this, {
          onlineFormDtoFilter: {
            pageId: pageId
          },
          orderParam: [
            {
              fieldName: 'createTime',
              asc: true
            }
          ]
        }).then(res => {
          this.formList = res.data.dataList;
          this.formList.forEach(item => {
            item.formData = JSON.parse(item.widgetJson);
          });
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 获取数据库信息
     */
    loadDblinkList () {
      return new Promise((resolve, reject) => {
        this.dblinkInfo = {};
        OnlineDblinkController.list(this, {}).then(res => {
          res.data.dataList.forEach(item => {
            this.dblinkInfo[item.dblinkId] = {
              ...item,
              tableList: undefined
            }
          });
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 获取数据库下数据表列表
     */
    loadDblinkTableList (dblinkId) {
      return new Promise((resolve, reject) => {
        // 如果此数据库下数据表已经获取过，直接返回数据表列表
        if (Array.isArray(this.dblinkInfo[dblinkId].tableList) && this.dblinkInfo[dblinkId].tableList.length > 0) {
          resolve(this.dblinkInfo[dblinkId].tableList);
          return;
        }
        // 获取数据库下数据表列表
        OnlineDblinkController.listDblinkTables(this, {
          dblinkId: dblinkId
        }).then(res => {
          this.dblinkInfo[dblinkId].tableList = res.data.dataList;
          resolve(res.data);
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 获取本数据源下所有导入的表
     */
    loadDatasourceTableList (datasourceId) {
      return new Promise((resolve, reject) => {
        let params = {
          datasourceId: datasourceId
        }
        OnlineDatasourceController.view(this, params).then(res => {
          this.datasourceTableList = res.data.tableList || [];
          resolve(this.datasourceTableList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    loadOnlineDictList () {
      return new Promise((resolve, reject) => {
        OnlineDictController.list(this, {}).then(res => {
          this.dictList = res.data.dataList;
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    /*************************************************************
     ************************ 数据模型操作 ************************
     *************************************************************/
    getDatasourceTableTagType (relationType) {
      if (relationType == null) return 'success';
      switch (relationType) {
        case this.SysOnlineRelationType.ONE_TO_ONE: return 'primary';
        case this.SysOnlineRelationType.ONE_TO_MANY: return 'warning';
        default:
          return 'info';
      }
    },
    getDatasourceTableTagName (relationType) {
      if (relationType == null) return '数据主表';
      return this.SysOnlineRelationType.getValue(relationType) || '未知类型';
    },
    /**
     * 新增数据模型表
     */
    onAddDatasourceClick () {
      if (this.getPageDatasource == null) {
        // 新增数据模型
        this.$dialog.show('新建数据源', EditOnlinePageDatasource, {
          area: '500px'
        }, {
          pageId: this.formPageData.pageId,
          dblinkInfo: this.dblinkInfo
        }).then(res => {
          return this.initPageDatasourceInfo(this.formPageData.pageId);
        }).catch(e => {});
      } else {
        // 新增数据模型关联
        this.loadDatasourceValidTableList(this.getPageDatasource).then(res => {
          this.$dialog.show('添加关联', EditOnlinePageDatasourceRelation, {
            area: '600px'
          }, {
            datasource: this.getPageDatasource,
            dblinkInfo: this.dblinkInfo[this.getPageDatasource.dblinkId],
            usedTableNameList: this.getPageDatasourceTableList.map(item => item.tableName)
          }).then(res => {
            return this.initPageDatasourceInfo(this.formPageData.pageId);
          }).catch(e => {
            console.log(e);
          });
        }).catch(e => {
          console.log(e);
        });
      }
    },
    /**
     * 编辑数据模型表
     */
    onEditDatasourceTable (row) {
      this.loadDatasourceValidTableList(this.getPageDatasource).then(res => {
        if (row.relationType == null) {
          // 编辑数据模型
          this.$dialog.show('编辑数据源', EditOnlinePageDatasource, {
            area: '500px'
          }, {
            pageId: this.formPageData.pageId,
            dblinkInfo: this.dblinkInfo,
            datasourceId: row.tag.datasourceId
          }).then(res => {
            return this.initPageDatasourceInfo(this.formPageData.pageId);
          }).catch(e => {});
        } else {
          // 编辑关联
          this.$dialog.show('编辑关联', EditOnlinePageDatasourceRelation, {
            area: '600px'
          }, {
            relationId: row.id,
            datasource: this.getPageDatasource,
            dblinkInfo: this.dblinkInfo[this.getPageDatasource.dblinkId],
            usedTableNameList: this.getPageDatasourceTableList.map(item => item.tableName)
          }).then(res => {
            return this.initPageDatasourceInfo(this.formPageData.pageId);
          }).catch(e => {
            console.log(e);
          });
        }
      }).catch(e => {});
    },
    /**
     * 删除数据模型表
     */
    onDeleteDatasourceTable (row) {
      if (row.relationType == null) {
        // 删除数据模型
        let warningMsg = '是否删除此数据模型？'
        if (Array.isArray(row.relationList) && row.relationList.length > 0) {
          warningMsg = '此数据模型下还存在关联，如果删除关联数据也将同时删除，是否继续？'
        }
        this.$confirm(warningMsg).then(res => {
          let params = {
            datasourceId: row.id
          }
          return OnlineDatasourceController.delete(this, params);
        }).then(res => {
          return this.initPageDatasourceInfo(this.formPageData.pageId);
        }).catch(e => {});
      } else {
        // 删除关联数据
        this.$confirm('是否删除此关联数据？').then(res => {
          let params = {
            relationId: row.id
          }

          return OnlineDatasourceRelationController.delete(this, params);
        }).then(res => {
          return this.initPageDatasourceInfo(this.formPageData.pageId);
        }).catch(e => {});
      }
    },
    /**
     * 关闭字段规则编辑
     */
    onRuleClose () {
      this.currentTable = undefined;
    },
    /**
     * 编辑数据模型表字段规则信息
     */
    onEditTableColumn (row) {
      this.currentTable = row;
    },
    /**
     * 关闭聚合计算管理
     */
    onVirtualColumnClose () {
      this.virtualColumnTable = undefined;
    },
    /**
     * 编辑数据源虚拟字段
     */
    onEditVirtualColumn (row) {
      this.virtualColumnTable = row;
    },
    /**
     * 获得数据源所有可用的数据表列表，包含已经导入的数据表和未导入的数据表
     */
    loadDatasourceValidTableList (datasource) {
      if (datasource == null) return Promise.reject();
      if (Array.isArray(datasource.validTableList) && datasource.validTableList.length > 0) {
        return Promise.resolve();
      }
      let httpCalls = [
        this.loadDatasourceTableList(datasource.datasourceId),
        this.loadDblinkTableList(datasource.dblinkId)
      ];

      return new Promise((resolve, reject) => {
        Promise.all(httpCalls).then(res => {
          let datasourceTableList = res[0];
          let dblinkTableList = res[1];
          // 合并两个数据表
          let tableNameSet = new Set();
          datasource.validTableList = dblinkTableList.map(table => {
            tableNameSet.add(table.tableName);
            let temp = findItemFromList(datasourceTableList, table.tableName, 'tableName');
            tableNameSet.add(table.tableName);
            return {
              id: temp ? temp.tableId : table.tableName,
              name: table.tableName,
              desc: temp ? temp.tableComment : table.tableComment,
              status: temp ? this.SysOnlinePageDatasourceFieldStatus.USED : this.SysOnlinePageDatasourceFieldStatus.UNUSED,
              dblinkTable: temp == null,
              tag: temp || table
            }
          });
          // 添加被使用但是已经从数据库中删除的数据表
          datasourceTableList.forEach(table => {
            if (!tableNameSet.has(table.tableName)) {
              datasource.validTableList.push({
                id: table.tableId,
                name: table.tableName,
                desc: table.tableComment,
                status: this.SysOnlinePageDatasourceFieldStatus.DELETED,
                dblinkTable: false,
                tag: table
              });
            }
          });
          tableNameSet = null;
          resolve();
        }).catch(e => {
          reject(e);
        });
      });
    },
    /*************************************************************
     ************************ 表单设计操作 ************************
     *************************************************************/
    /**
     * 编辑子表单
     */
    onEditPageForm (row) {
      this.$dialog.show('编辑表单', EditOnlineForm, {
        area: '600px'
      }, {
        pageId: this.formPageData.pageId,
        pageType: this.formPageData.pageType,
        datasourceId: this.getPageDatasource.datasourceId,
        datasourceTableList: this.getPageDatasourceTableList,
        form: row
      }).then(res => {
        this.initPageFormList(this.formPageData.pageId);
      }).catch(e => {});
    },
    loadOnlineTableColumns (tableId) {
      if (tableId == null || tableId === '') return Promise.reject();

      return new Promise((resolve, reject) => {
        let params = {
          onlineColumnDtoFilter: {
            tableId
          }
        }

        OnlineColumnController.list(this, params).then(res => {
          resolve(res.data.dataList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    loadOnlineVirtualColumnList () {
      return new Promise((resolve, reject) => {
        let params = {
          onlineVirtualColumnDtoFilter: {
            datasourceId: this.getPageDatasource.datasourceId
          }
        }

        OnlineVirtualColumnController.list(this, params).then(res => {
          res.data.dataList.forEach(item => {
            item.columnId = item.virtualColumnId;
            item.columnName = item.objectFieldName;
            item.columnComment = item.columnPrompt;
            item.isVirtualColumn = true;
          });
          resolve(res.data.dataList);
        }).catch(e => {
          reject(e);
        });
      });
    },
    onDesignPageForm (row) {
      let masterTable = findItemFromList(this.getPageDatasourceTableList, row.masterTableId, 'tableId');
      if (masterTable) {
        if (masterTable && masterTable.relationType == null) {
          // 主表获取虚拟字段列
          this.loadOnlineVirtualColumnList().then(res => {
            let virtualColumnList = res;
            // 数据源主表的查询以及编辑页面
            let httpCalls = [];
            // 返回主表和一对一从表，编辑页面同时返回一对多从表
            let templateList = this.getPageDatasourceTableList.filter(item => {
              return (
                item.relationType == null ||
                item.relationType === this.SysOnlineRelationType.ONE_TO_ONE ||
                (row.formType !== this.SysOnlineFormType.QUERY && item.relationType === this.SysOnlineRelationType.ONE_TO_MANY)
              );
            });
            httpCalls = templateList.map(item => {
              return this.loadOnlineTableColumns(item.tableId);
            });
            Promise.all(httpCalls).then(res => {
              let tableList = res.map((item, index) => {
                templateList[index].columnList = item;
                // 数据源主表，添加虚拟字段
                if (templateList[index].tableId === row.masterTableId) {
                  templateList[index].columnList.push(...virtualColumnList);
                }
                return templateList[index];
              });
              row.tableList = tableList;
              this.currentForm = row;
            }).catch(e => {
              console.log(e);
            });
          }).catch(e => {
            console.log(e);
          });
        } else {
          this.loadOnlineTableColumns(masterTable.tableId).then(res => {
            masterTable.columnList = res;
            row.tableList = [masterTable];
            this.currentForm = row;
          }).catch(e => {
            console.log(e);
          });
        }
      }
      // this.currentForm = row;
    },
    onDeletePageForm (row) {
      this.$confirm('是否删除此表单？').then(res => {
        let params = {
          formId: row.formId
        }

        return OnlineFormController.delete(this, params);
      }).then(res => {
        this.$message.success('删除成功！');
        this.initPageFormList(this.formPageData.pageId);
      }).catch(e => {});
    }
  },
  computed: {
    isEdit () {
      return this.formPageData.pageId != null && this.formPageData.pageId !== '';
    },
    getPageDatasource () {
      return this.pageDatasourceList[0];
    },
    getPageDatasourceTableList () {
      if (this.getPageDatasource == null) return [];
      
      let tableList = [];
      // 添加主表信息
      tableList.push({
        id: this.getPageDatasource.datasourceId,
        tableName: this.getPageDatasource.masterTableIdDictMap.name,
        tableId: this.getPageDatasource.masterTableId,
        relationType: undefined,
        masterColumnName: undefined,
        slaveColumnName: undefined,
        cascadeDelete: false,
        leftJoin: false,
        tag: this.getPageDatasource
      });
      // 添加关联从表信息
      if (Array.isArray(this.getPageDatasource.relationList)) {
        this.getPageDatasource.relationList.forEach(relation => {
          tableList.push({
            id: relation.relationId,
            tableName: relation.slaveTable.tableName,
            tableId: relation.slaveTableId,
            relationType: relation.relationType,
            masterColumnName: (relation.masterColumn || {}).columnName || '未知字段',
            slaveColumnName: (relation.slaveColumn || {}).columnName || '未知字段',
            cascadeDelete: relation.cascadeDelete,
            leftJoin: relation.leftJoin,
            tag: relation
          });
        });
      }
      return tableList;
    },
    getTableInfo () {
      return this.currentForm.tableList;
    },
    ...mapGetters(['getClientHeight'])
  },
  mounted () {
    this.loadOnlineDictList().catch(e => {});
    this.loadDblinkList().then(res => {
      return this.initPageInfo();
    }).then(res => {
      this.formPageData = {
        pageId: res.data.pageId,
        pageCode: res.data.pageCode,
        pageName: res.data.pageName,
        published: res.data.published,
        pageType: res.data.pageType,
        status: res.data.status
      }
      this.activeStep = this.SysOnlinePageSettingStep.BASIC;
    }).catch(e => {});
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
