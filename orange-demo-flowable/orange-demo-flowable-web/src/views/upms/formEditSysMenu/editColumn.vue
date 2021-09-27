<template>
  <el-form ref="form" :model="formData" :rules="rules" label-width="110px" size="mini" label-position="right" @submit.native.prevent>
    <el-row :gutter="20" class="full-width-input">
      <el-col :span="24" >
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="formData.menuName" placeholder="菜单名称" clearable />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="显示顺序" prop="showOrder">
          <el-input-number v-model="formData.showOrder" controls-position="right"
            :min="1" :max="99999" placeholder="请输入显示顺序"></el-input-number>
        </el-form-item>
      </el-col>
    </el-row>
    <!-- 弹窗按钮 -->
    <el-row type="flex" justify="end" class="dialog-btn-layer mt20">
      <el-button size="mini" @click="onCancel(false)" >取消</el-button>
      <el-button type="primary" size="mini" @click="onSubmit">确定</el-button>
    </el-row>
  </el-form>
</template>

<script>
import { SystemController } from '@/api';

export default {
  props: {
    columnId: {
      type: String
    },
    columnName: {
      type: String
    },
    showOrder: {
      type: Number
    }
  },
  data () {
    return {
      // 是否自动用上级菜单的名称过滤权限字列表，当这个开关打开后，会使用getAutoFilterString返回的字符串当做过滤字符串
      autoFilter: false,
      permCodeNameFilter: undefined,
      allowParentList: [],
      formData: {
        menuId: this.columnId,
        parentId: undefined,
        menuName: this.columnName,
        showOrder: this.showOrder,
        menuType: this.SysMenuType.DIRECTORY,
        icon: undefined,
        formRouterName: undefined
      },
      rules: {
        menuName: [{required: true, message: '请输入栏目名称', trigger: 'blur'}],
        showOrder: [{required: true, message: '请输入栏目显示顺序', trigger: 'blur'}]
      }
    };
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    onSubmit () {
      return new Promise((resolve, reject) => {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            let params = {};
            params.sysMenuDto = {...this.formData};
            
            if (this.isEdit) {
              SystemController.updateMenu(this, params).then(res => {
                resolve(res);
                this.$message.success('编辑成功');
                this.onCancel(true);
              }).catch(e => {
                reject(e);
              });
            } else {
              SystemController.addMenu(this, params).then(res => {
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
    }
  },
  computed: {
    isEdit () {
      return this.formData.menuId != null;
    }
  },
  mounted () {
    this.initData();
  }
};
</script>
