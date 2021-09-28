<template>
  <el-form ref="form" :model="formData" :rules="rules" label-width="110px" size="mini" label-position="right" @submit.native.prevent>
    <el-row :gutter="20" class="full-width-input">
      <el-col :span="24">
        <el-form-item label="所属权限字">
          <el-cascader :options="permCodeTree" v-model="parentPermCodePath" :props="permCodeProps" filterable
            :disabled="formData.permCodeId != null || formData.parenId == null" placeholder="选择父权限字" :clearable="true" :change-on-select="true" size="mini" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="权限字名称" prop="showName">
          <el-input v-model="formData.showName" placeholder="权限字名称" clearable ></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="权限字标识" prop="permCode">
          <el-input v-model="formData.permCode" placeholder="权限字标识" clearable ></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="权限字类型" prop="permCode">
          <el-select v-model="formData.permCodeType" placeholder="权限字类型" :disabled="true">
            <el-option v-for="item in SysPermCodeType.getList()" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="显示顺序" prop="showOrder">
          <el-input-number v-model="formData.showOrder" controls-position="right"
            :min="1" :max="99999" placeholder="请输入显示顺序"></el-input-number>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-card shadow="never">
          <div slot="header" class="card-header">
            <span>权限列表</span>
            <el-input size="mini" v-model="permNameFilter" placeholder="输入权限名称过滤" style="width: 250px;" clearable suffix-icon="el-icon-search" />
          </div>
          <el-scrollbar style="height: 215px;" wrap-class="scrollbar_dropdown__wrap">
            <el-tree ref="permTree" :data="formData.permCodeType === SysPermCodeType.FORM ? [] : getPermTree" :props="treeProps" show-checkbox
              node-key="id" empty-text="暂无权限资源" :filter-node-method="filterPermNode"
              :default-expanded-keys="defaultExpandedKeys">
              <div slot-scope="{ data }" style="display: flex; justify-content: space-between; width: 100%">
                <span>{{ data.name }}</span>
                <span style="margin-right: 10px;">{{ data.url }}</span>
              </div>
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>
    <!-- 弹窗按钮 -->
    <el-row type="flex" justify="end" class="dialog-btn-layer mt20">
      <el-button size="mini" @click="onCancel(false)" >取消</el-button>
      <el-button type="primary" size="mini" @click="onSubmit"
        :disabled="!(checkPermCodeExist('formSysPermCode:fragmentSysPermCode:add') || checkPermCodeExist('formSysPermCode:fragmentSysPermCode:update'))">
        确定
      </el-button>
    </el-row>
  </el-form>
</template>

<script>
import { treeDataTranslate, findTreeNodePath } from '@/utils'
import { SystemController } from '@/api'

export default {
  props: {
    permCodeTree: {
      type: Array,
      default: function () {
        return [];
      }
    },
    permCodeType: {
      type: Number,
      default: undefined
    },
    rowData: {
      type: Object
    }
  },
  data () {
    return {
      permNameFilter: undefined,
      allowParentList: [],
      formData: {
        permCodeId: undefined,
        parentId: undefined,
        permCode: undefined,
        permCodeType: this.permCodeType || this.SysPermCodeType.FORM,
        showName: undefined,
        showOrder: undefined
      },
      parentPermCodePath: [],
      permList: [],
      defaultExpandedKeys: [],
      treeProps: {
        label: 'name',
        isLeaf: function (data, node) {
          return data.isPerm;
        }
      },
      permCodeProps: {
        label: 'showName',
        value: 'permCodeId'
      },
      rules: {
        showName: [{required: true, message: '权限字名称不能为空', trigger: 'blur'}],
        permCode: [{required: true, message: '权限字标识不能为空', trigger: 'blur'}],
        showOrder: [{required: true, message: '请输入权限字显示顺序', trigger: 'blur'}],
        permCodeType: [{required: true, message: '请选择权限字类型', trigger: 'blur'}]
      }
    }
  },
  methods: {
    filterPermNode (value, data) {
      if (!value) return true;
      if (data.name.indexOf(value) !== -1) {
        this.allowParentList.push(data.id);
        return true;
      } else {
        return this.allowParentList.indexOf(data.parentId) !== -1;
      }
    },
    getTreeLeafKeys () {
      let selectPermNodeList = this.$refs.permTree.getCheckedNodes();
      let tempList = [];
      selectPermNodeList.forEach((item) => {
        if (item.isPerm) {
          tempList.push(item.id);
        }
      });

      return tempList;
    },
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSubmit () {
      return new Promise((resolve, reject) => {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            let selectedPermList = this.getTreeLeafKeys();
            let params = {};
            params.sysPermCodeDto = {...this.formData};
            delete params.sysPermCodeDto.children;
            params.sysPermCodeDto.permCodeType = (this.permCodeType == null) ? this.SysPermCodeType.FORM : this.permCodeType;
            if (this.parentPermCodePath.length > 0) {
              params.sysPermCodeDto.parentId = this.parentPermCodePath[this.parentPermCodePath.length - 1];
            }
            params.permIdListString = selectedPermList.join(',');
            if (params.sysPermCodeDto.permCodeId != null) {
              SystemController.updatePermCode(this, params).then(res => {
                resolve(res);
                this.$message.success('编辑成功');
                this.onCancel(true);
              }).catch(e => {
                reject(e);
              });
            } else {
              SystemController.addPermCode(this, params).then(res => {
                resolve(res);
                this.$message.success('添加成功');
                this.onCancel(true);
              }).catch(e => {
                reject(e);
              });
            }
          } else {
            reject();
          }
        });
      });
    },
    initData () {
      SystemController.getAllPermList(this, {}).then(res => {
        res.data.forEach((item) => {
          item.id = item.id + '';
          item.parentId = item.parentId + '';
        });
        this.permList = res.data;
        this.defaultExpandedKeys = this.formData.permIdList;
        if (Array.isArray(this.formData.permIdList)) {
          this.$refs.permTree.setCheckedKeys(this.formData.permIdList, true);
        }
      }).catch(e => {});
    }
  },
  computed: {
    getPermTree () {
      return treeDataTranslate(this.permList.map((item) => {
        return {...item};
      }), 'id', 'parentId');
    }
  },
  mounted () {
    if (this.rowData != null) {
      this.formData = {...this.formData, ...this.rowData};
      if (Array.isArray(this.formData.sysPermCodePermList)) {
        this.formData.permIdList = this.formData.sysPermCodePermList.map(item => item.permId);
      }
      if (this.formData.parentId != null && this.permCodeTree != null && Array.isArray(this.permCodeTree)) {
        this.parentPermCodePath = findTreeNodePath(this.permCodeTree, this.formData.parentId, 'permCodeId');
      } else {
        this.parentPermCodePath = [];
      }
    }

    this.initData();
  },
  watch: {
    permNameFilter (val) {
      this.allowParentList = [];
      this.$refs.permTree.filter(val);
    }
  }
}
</script>
