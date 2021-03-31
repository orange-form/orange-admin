<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formCreateSchool" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="学校名称" prop="SchoolInfo.schoolName">
            <el-input class="input-item" v-model="formData.SchoolInfo.schoolName"
              :clearable="true" placeholder="学校名称" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="所在省份" prop="SchoolInfo.provinceId">
            <el-select class="input-item" v-model="formData.SchoolInfo.provinceId" :clearable="true" filterable
              placeholder="所在省份" :loading="formCreateSchool.provinceId.impl.loading"
              @visible-change="formCreateSchool.provinceId.impl.onVisibleChange"
              @change="onProvinceIdValueChange">
              <el-option v-for="item in formCreateSchool.provinceId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="所在城市" prop="SchoolInfo.cityId">
            <el-select class="input-item" v-model="formData.SchoolInfo.cityId" :clearable="true" filterable
              placeholder="所在城市" :loading="formCreateSchool.cityId.impl.loading"
              @visible-change="formCreateSchool.cityId.impl.onVisibleChange"
              @change="onCityIdValueChange">
              <el-option v-for="item in formCreateSchool.cityId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" :disabled="!checkPermCodeExist('formCreateSchool:formCreateSchool:add')"
              @click="onAddClick()">
              保存
            </el-button>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import rules from '@/utils/validate.js';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SchoolInfoController, DictionaryController } from '@/api';

export default {
  name: 'formCreateSchool',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        SchoolInfo: {
          schoolId: undefined,
          schoolName: undefined,
          provinceId: undefined,
          cityId: undefined,
          isDatasourceInit: false
        }
      },
      rules: {
        'SchoolInfo.schoolName': [
          {required: true, message: '请输入学校名称', trigger: 'blur'}
        ],
        'SchoolInfo.cityId': [
          {required: true, message: '请输入所在城市', trigger: 'blur'}
        ],
        'SchoolInfo.provinceId': [
          {required: true, message: '请输入所在省份', trigger: 'blur'}
        ]
      },
      formCreateSchool: {
        formFilter: {
        },
        formFilterCopy: {
        },
        provinceId: {
          impl: new DropdownWidget(this.loadProvinceIdDropdownList)
        },
        cityId: {
          impl: new DropdownWidget(this.loadCityIdDropdownList)
        },
        menuBlock: {
          isInit: false
        },
        isInit: false
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess);
      }
    },
    /**
     * 所在省份下拉数据获取函数
     */
    loadProvinceIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictAreaCodeByParentId(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所在省份选中值改变
     */
    onProvinceIdValueChange (value) {
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formData.SchoolInfo.cityId = undefined;
      this.formCreateSchool.cityId.impl.dirty = true;
      this.onCityIdValueChange(this.formData.SchoolInfo.cityId);
    },
    /**
     * 所在城市下拉数据获取函数
     */
    loadCityIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {
          parentId: this.formData.SchoolInfo.provinceId
        };
        if (params.parentId == null || params.parentId === '') {
          resolve([]);
          return;
        }
        DictionaryController.dictAreaCodeByParentId(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所在城市选中值改变
     */
    onCityIdValueChange (value) {
    },
    /**
     * 更新新建校区
     */
    refreshFormCreateSchool (reloadData = false) {
      if (!this.formCreateSchool.isInit) {
        // 初始化下拉数据
      }
      this.formCreateSchool.isInit = true;
    },
    /**
     * 保存
     */
    onAddClick () {
      this.$refs.formCreateSchool.validate((valid) => {
        if (!valid) return;
        if (
          this.formData.SchoolInfo.schoolName == null ||
          this.formData.SchoolInfo.provinceId == null ||
          this.formData.SchoolInfo.cityId == null
        ) {
          this.$message.error('请求失败，发现必填参数为空！');
          return;
        }
        let params = {
          schoolInfo: {
            schoolName: this.formData.SchoolInfo.schoolName,
            provinceId: this.formData.SchoolInfo.provinceId,
            cityId: this.formData.SchoolInfo.cityId
          }
        };

        SchoolInfoController.add(this, params).then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    initFormData () {
    },
    /**
     * 重置表单数据
     */
    resetFormData () {
      this.formData = {
        SchoolInfo: {
          schoolId: undefined,
          schoolName: undefined,
          provinceId: undefined,
          cityId: undefined,
          isDatasourceInit: false
        }
      }
    },
    formInit () {
      this.refreshFormCreateSchool();
    }
  },
  computed: {
  },
  mounted () {
    // 初始化页面数据
    this.formInit();
  },
  watch: {
  }
}
</script>
