<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="岗位名称">
          <el-input class="filter-item" v-model="formSetDeptPost.formFilter.postName"
            :clearable="true" placeholder="岗位名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormSetDeptPost(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini"
          :disabled="tableSelectRowList.length <= 0 || !checkPermCodeExist('formSysDept:fragmentSysDept:editPost')"
          @click="onAddSysDeptPostClick()">
          添加岗位
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table :data="formSetDeptPost.SysPost.impl.dataList" size="mini" @sort-change="formSetDeptPost.SysPost.impl.onSortChange"
          @selection-change="onSysPostSelectionChange" header-cell-class-name="table-header-gray">
          <el-table-column label="序号" type="index" header-align="center" align="center" width="55px" :index="formSetDeptPost.SysPost.impl.getTableIndex" />
          <el-table-column type="selection" header-align="center" align="center" width="55px" />
          <el-table-column label="岗位名称" prop="postName">
          </el-table-column>
          <el-table-column label="岗位层级" prop="level" sortable="custom">
          </el-table-column>
          <el-table-column label="领导岗位" prop="leaderPost" sortable="custom">
            <template slot-scope="scope">
              <el-tag size="mini" :type="scope.row.leaderPost ? 'success' : 'danger'">
                {{scope.row.leaderPost ? '是' : '否'}}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-col :span="24">
          <el-row type="flex" justify="end" style="margin-top: 10px;">
            <el-pagination
              :total="formSetDeptPost.SysPost.impl.totalCount"
              :current-page="formSetDeptPost.SysPost.impl.currentPage"
              :page-size="formSetDeptPost.SysPost.impl.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, prev, pager, next, sizes"
              @current-change="formSetDeptPost.SysPost.impl.onCurrentPageChange"
              @size-change="formSetDeptPost.SysPost.impl.onPageSizeChange">
            </el-pagination>
          </el-row>
        </el-col>
      </el-col>
    </el-row>
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
import { SysDeptController, DictionaryController } from '@/api';

export default {
  name: 'formSetDeptPost',
  props: {
    deptId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      tableSelectRowList: [],
      formSetDeptPost: {
        formFilter: {
          postName: undefined
        },
        formFilterCopy: {
          postName: undefined
        },
        SysPost: {
          impl: new TableWidget(this.loadSysPostWidgetData, this.loadSysPostVerify, true, false, 'level', 1)
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
    onSysPostSelectionChange (values) {
      this.tableSelectRowList = values;
    },
    /**
     * 部门岗位数据获取函数，返回Promise
     */
    loadSysPostWidgetData (params) {
      if (
        this.deptId == null
      ) {
        this.formSetDeptPost.SysPost.impl.clearTable();
        return Promise.reject();
      }
      if (params == null) params = {};
      params = {
        ...params,
        deptId: this.deptId,
        sysPostDtoFilter: {
          postName: this.formSetDeptPost.formFilterCopy.postName
        }
      }
      return new Promise((resolve, reject) => {
        SysDeptController.listNotInSysDeptPost(this, params).then(res => {
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
     * 部门岗位数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysPostVerify () {
      this.formSetDeptPost.formFilterCopy.postName = this.formSetDeptPost.formFilter.postName;
      return true;
    },
    /**
     * 更新设置部门岗位
     */
    refreshFormSetDeptPost (reloadData = false) {
      if (reloadData) {
        this.formSetDeptPost.SysPost.impl.refreshTable(true, 1);
      } else {
        this.formSetDeptPost.SysPost.impl.refreshTable();
      }
      if (!this.formSetDeptPost.isInit) {
        // 初始化下拉数据
      }
      this.formSetDeptPost.isInit = true;
    },
    /**
     * 添加岗位
     */
    onAddSysDeptPostClick () {
      if (
        this.deptId == null
      ) {
        this.$message.error('请求失败，发现必填参数为空！');
        return;
      }
      let params = {
        deptId: this.deptId,
        sysDeptPostDtoList: this.tableSelectRowList.map((item) => {
          return {
            postId: item.postId,
            postShowName: item.postName
          };
        })
      };

      SysDeptController.addSysDeptPost(this, params).then(res => {
        this.$message.success('添加岗位成功');
        this.onCancel(true);
      }).catch(e => {});
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormSetDeptPost();
    }
  },
  mounted () {
    // 初始化页面数据
    this.formInit();
  },
  watch: {
  }
}
</script>
