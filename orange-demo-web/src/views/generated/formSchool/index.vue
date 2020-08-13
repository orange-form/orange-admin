<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="所属省份">
          <el-select class="filter-item" v-model="formSchool.formFilter.provinceId" :clearable="true" filterable
            placeholder="所属省份" :loading="formSchool.provinceId.impl.loading"
            @visible-change="formSchool.provinceId.impl.onVisibleChange"
            @change="onProvinceIdValueChange">
            <el-option v-for="item in formSchool.provinceId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属城市">
          <el-select class="filter-item" v-model="formSchool.formFilter.cityId" :clearable="true" filterable
            placeholder="所属城市" :loading="formSchool.cityId.impl.loading"
            @visible-change="formSchool.cityId.impl.onVisibleChange"
            @change="onCityIdValueChange">
            <el-option v-for="item in formSchool.cityId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="校区名称">
          <el-input class="filter-item" v-model="formSchool.formFilter.schoolName"
            :clearable="true" placeholder="校区名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormSchool(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formSchool:formSchool:formCreateClass')"
          @click="onFormCreateClassClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formSchool.SchoolInfo.impl.dataList" size="mini" @sort-change="formSchool.SchoolInfo.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formSchool.SchoolInfo.impl.getTableIndex" />
          <el-table-column label="学校名称" prop="schoolName">
          </el-table-column>
          <el-table-column label="所在省份" prop="provinceIdDictMap.name">
          </el-table-column>
          <el-table-column label="所在城市" prop="cityIdDictMap.name">
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click="onFormEditSchoolClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formSchool:formSchool:formEditSchool')">
                编辑
              </el-button>
              <el-button @click="onDeleteClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formSchool:formSchool:delete')">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formSchool.SchoolInfo.impl.totalCount"
            :current-page="formSchool.SchoolInfo.impl.currentPage"
            :page-size="formSchool.SchoolInfo.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formSchool.SchoolInfo.impl.onCurrentPageChange"
            @size-change="formSchool.SchoolInfo.impl.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SchoolInfoController, DictionaryController } from '@/api';
import formCreateSchool from '@/views/generated/formCreateSchool';
import formEditSchool from '@/views/generated/formEditSchool';

export default {
  name: 'formSchool',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formSchool: {
        formFilter: {
          provinceId: undefined,
          cityId: undefined,
          schoolName: undefined
        },
        formFilterCopy: {
          provinceId: undefined,
          cityId: undefined,
          schoolName: undefined
        },
        provinceId: {
          impl: new DropdownWidget(this.loadProvinceIdDropdownList)
        },
        cityId: {
          impl: new DropdownWidget(this.loadCityIdDropdownList)
        },
        SchoolInfo: {
          impl: new TableWidget(this.loadSchoolInfoData, this.loadSchoolInfoVerify, true)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 校区数据数据获取函数，返回Primise
     */
    loadSchoolInfoData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        schoolInfoFilter: {
          schoolName: this.formSchool.formFilterCopy.schoolName,
          provinceId: this.formSchool.formFilterCopy.provinceId,
          cityId: this.formSchool.formFilterCopy.cityId
        }
      }
      return new Promise((resolve, reject) => {
        SchoolInfoController.list(this, params).then(res => {
          resolve({
            dataList: res.data.dataList,
            totalCount: res.data.totalCount
          });
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 校区数据数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSchoolInfoVerify () {
      this.formSchool.formFilterCopy.schoolName = this.formSchool.formFilter.schoolName;
      this.formSchool.formFilterCopy.provinceId = this.formSchool.formFilter.provinceId;
      this.formSchool.formFilterCopy.cityId = this.formSchool.formFilter.cityId;
      return true;
    },
    /**
     * 所属省份下拉数据获取函数
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
     * 所属省份选中值改变
     */
    onProvinceIdValueChange (value) {
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formSchool.formFilter.cityId = undefined;
      this.formSchool.cityId.impl.dirty = true;
      this.onCityIdValueChange(this.formSchool.formFilter.cityId);
    },
    /**
     * 所属城市下拉数据获取函数
     */
    loadCityIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {
          parentId: this.formSchool.formFilter.provinceId
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
     * 所属城市选中值改变
     */
    onCityIdValueChange (value) {
    },
    /**
     * 更新校区管理
     */
    refreshFormSchool (reloadData = false) {
      if (reloadData) {
        this.formSchool.SchoolInfo.impl.refreshTable(true, 1);
      } else {
        this.formSchool.SchoolInfo.impl.refreshTable();
      }
      if (!this.formSchool.isInit) {
        // 初始化下拉数据
      }
      this.formSchool.isInit = true;
    },
    /**
     * 新建
     */
    onFormCreateClassClick () {
      let params = {};

      this.$dialog.show('新建', formCreateSchool, {
        area: ['600px']
      }, params).then(res => {
        this.refreshFormSchool();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onFormEditSchoolClick (row) {
      let params = {
        schoolId: row.schoolId
      };

      this.$dialog.show('编辑', formEditSchool, {
        area: ['600px']
      }, params).then(res => {
        this.formSchool.SchoolInfo.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      let params = {
        schoolId: row.schoolId
      };

      this.$confirm('是否删除此校区？').then(res => {
        SchoolInfoController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formSchool.SchoolInfo.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormSchool();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormSchool();
    }
  },
  created () {
    this.formInit();
  }
}
</script>
