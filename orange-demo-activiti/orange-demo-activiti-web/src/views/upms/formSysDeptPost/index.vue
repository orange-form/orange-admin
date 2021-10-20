<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="岗位名称">
          <el-input class="filter-item" v-model="formSysDeptPost.formFilter.postName"
            :clearable="true" placeholder="岗位名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormSysDeptPost(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formSysDept:fragmentSysDept:editPost')"
          @click="onFormSetDeptPostClick()">
          岗位设置
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="sysPost" :data="formSysDeptPost.SysPost.impl.dataList" size="mini" @sort-change="formSysDeptPost.SysPost.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formSysDeptPost.SysPost.impl.getTableIndex" />
          <el-table-column label="岗位名称" prop="postName">
          </el-table-column>
          <el-table-column label="岗位别名" prop="sysDeptPost.postShowName">
            <template slot-scope="scope">
              <span v-if="formSysDeptPost.SysPost.currentRow == null || formSysDeptPost.SysPost.currentRow.postId !== scope.row.postId">{{(scope.row.sysDeptPost || {}).postShowName}}</span>
              <el-input v-else size="mini" v-model="formSysDeptPost.SysPost.currentRow.sysDeptPost.postShowName" />
            </template>
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
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button v-if="formSysDeptPost.SysPost.currentRow == null"
                @click.stop="onEditPostName(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formSysDept:fragmentSysDept:editPost')">
                修改别名
              </el-button>
              <el-button class="table-btn delete" v-if="formSysDeptPost.SysPost.currentRow == null"
                @click.stop="onDeleteSysDeptPostClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formSysDept:fragmentSysDept:editPost')">
                移除
              </el-button>
              <el-button v-if="formSysDeptPost.SysPost.currentRow != null && formSysDeptPost.SysPost.currentRow.postId === scope.row.postId"
                @click.stop="onSavePostName(scope.row)" type="text" size="mini">
                保存
              </el-button>
              <el-button v-if="formSysDeptPost.SysPost.currentRow != null && formSysDeptPost.SysPost.currentRow.postId === scope.row.postId"
                @click.stop="onCancelSavePostName(scope.row)" type="text" size="mini">
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formSysDeptPost.SysPost.impl.totalCount"
            :current-page="formSysDeptPost.SysPost.impl.currentPage"
            :page-size="formSysDeptPost.SysPost.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formSysDeptPost.SysPost.impl.onCurrentPageChange"
            @size-change="formSysDeptPost.SysPost.impl.onPageSizeChange">
          </el-pagination>
        </el-row>
      </el-col>
    </el-row>
    <label v-if="closeVisible == '1'" class="page-close-box">
      <el-button type="text" @click="onCancel(true)" icon="el-icon-close" />
    </label>
  </div>
</template>

<script>
/* eslint-disable-next-line */
import rules from '@/utils/validate.js';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin, cachePageMixin, cachedPageChildMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SysDeptController, DictionaryController } from '@/api';
import formSetDeptPost from './formSetDeptPost.vue';

export default {
  name: 'formSysDeptPost',
  props: {
    deptId: {
      default: undefined
    },
    closeVisible: {
      type: [Number, String],
      default: 0
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin, cachedPageChildMixin],
  data () {
    return {
      formSysDeptPost: {
        formFilter: {
          postName: undefined
        },
        formFilterCopy: {
          postName: undefined
        },
        SysPost: {
          currentRow: undefined,
          impl: new TableWidget(this.loadSysPostWidgetData, this.loadSysPostVerify, true, false, 'level', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    onCancel (isSuccess) {
      this.removeCachePage(this.$options.name);
      this.refreshParentCachedPage = isSuccess;
      this.$router.go(-1);
    },
    /**
     * 部门岗位数据获取函数，返回Promise
     */
    loadSysPostWidgetData (params) {
      if (
        this.deptId == null
      ) {
        this.formSysDeptPost.SysPost.impl.clearTable();
        return Promise.reject();
      }
      if (params == null) params = {};
      params = {
        ...params,
        deptId: this.deptId,
        sysPostDtoFilter: {
          postName: this.formSysDeptPost.formFilterCopy.postName
        }
      }
      return new Promise((resolve, reject) => {
        SysDeptController.listSysDeptPost(this, params).then(res => {
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
      this.formSysDeptPost.formFilterCopy.postName = this.formSysDeptPost.formFilter.postName;
      return true;
    },
    /**
     * 更新部门岗位
     */
    refreshFormSysDeptPost (reloadData = false) {
      if (reloadData) {
        this.formSysDeptPost.SysPost.impl.refreshTable(true, 1);
      } else {
        this.formSysDeptPost.SysPost.impl.refreshTable();
      }
      if (!this.formSysDeptPost.isInit) {
        // 初始化下拉数据
      }
      this.formSysDeptPost.isInit = true;
      this.formSysDeptPost.SysPost.currentRow = null;
    },
    /**
     * 修改别名
     */
    onEditPostName (row) {
      this.formSysDeptPost.SysPost.currentRow = row;
    },
    /**
     * 保存别名
     */
    onSavePostName (row) {
      SysDeptController.updateSysDeptPost(this, {
        sysDeptPostDto: {
          deptPostId: row.sysDeptPost.deptPostId,
          deptId: row.sysDeptPost.deptId,
          postId: row.postId,
          postShowName: this.formSysDeptPost.SysPost.currentRow.sysDeptPost.postShowName
        }
      }).then(res => {
        this.refreshFormSysDeptPost();
      }).catch(e => {});
    },
    onCancelSavePostName (row) {
      this.refreshFormSysDeptPost();
    },
    /**
     * 移除
     */
    onDeleteSysDeptPostClick (row) {
      if (
        this.deptId == null ||
        row.postId == null
      ) {
        this.$message.error('请求失败，发现必填参数为空！');
        return;
      }
      let params = {
        deptId: this.deptId,
        postId: row.postId
      };

      this.$confirm('是否从部门中移除此岗位？').then(res => {
        SysDeptController.deleteSysDeptPost(this, params).then(res => {
          this.$message.success('移除成功');
          this.formSysDeptPost.SysPost.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    /**
     * 添加岗位
     */
    onFormSetDeptPostClick () {
      let params = {
        deptId: this.deptId
      };

      this.$dialog.show('岗位设置', formSetDeptPost, {
        area: ['1200px', '600px']
      }, params).then(res => {
        this.refreshFormSysDeptPost();
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormSysDeptPost();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormSysDeptPost();
    }
  },
  mounted () {
    // 初始化页面数据
    this.formInit();
    console.log(this.closeVisible);
  },
  watch: {
  }
}
</script>
