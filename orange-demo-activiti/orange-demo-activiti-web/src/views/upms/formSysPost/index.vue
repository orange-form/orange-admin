<template>
  <div style="position: relative;">
    <el-form label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <filter-box :item-width="350">
        <el-form-item label="岗位名称">
          <el-input class="filter-item" v-model="formSysPost.formFilter.postName"
            :clearable="true" placeholder="岗位名称" />
        </el-form-item>
        <el-button slot="operator" type="primary" :plain="true" size="mini" @click="refreshFormSysPost(true)">查询</el-button>
        <el-button slot="operator" type="primary" size="mini" :disabled="!checkPermCodeExist('formSysPost:fragmentSysPost:add')"
          @click="onFormAddPostClick()">
          新建
        </el-button>
      </filter-box>
    </el-form>
    <el-row>
      <el-col :span="24">
        <el-table ref="sysPost" :data="formSysPost.sysPost.impl.dataList" size="mini" @sort-change="formSysPost.sysPost.impl.onSortChange"
          header-cell-class-name="table-header-gray">
          <el-table-column label="序号" header-align="center" align="center" type="index" width="55px" :index="formSysPost.sysPost.impl.getTableIndex" />
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
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button @click.stop="onFormEditPostClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formSysPost:fragmentSysPost:update')">
                编辑
              </el-button>
              <el-button class="table-btn delete" @click.stop="onDeleteClick(scope.row)" type="text" size="mini"
                :disabled="!checkPermCodeExist('formSysPost:fragmentSysPost:delete')">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formSysPost.sysPost.impl.totalCount"
            :current-page="formSysPost.sysPost.impl.currentPage"
            :page-size="formSysPost.sysPost.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formSysPost.sysPost.impl.onCurrentPageChange"
            @size-change="formSysPost.sysPost.impl.onPageSizeChange">
          </el-pagination>
        </el-row>
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
import { uploadMixin, statsDateRangeMixin, cachePageMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { SysPostController, DictionaryController } from '@/api';
import formEditPost from '@/views/upms/formEditSysPost/index.vue';

export default {
  name: 'formSysPost',
  props: {
  },
  mixins: [uploadMixin, statsDateRangeMixin, cachePageMixin],
  data () {
    return {
      formSysPost: {
        formFilter: {
          postName: undefined
        },
        formFilterCopy: {
          postName: undefined
        },
        sysPost: {
          impl: new TableWidget(this.loadSysPostWidgetData, this.loadSysPostVerify, true, false, 'level', 1)
        },
        isInit: false
      }
    }
  },
  methods: {
    /**
     * 岗位管理数据获取函数，返回Promise
     */
    loadSysPostWidgetData (params) {
      if (params == null) params = {};
      params = {
        ...params,
        sysPostDtoFilter: {
          postName: this.formSysPost.formFilterCopy.postName
        }
      }
      return new Promise((resolve, reject) => {
        SysPostController.list(this, params).then(res => {
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
     * 岗位管理数据获取检测函数，返回true正常获取数据，返回false停止获取数据
     */
    loadSysPostVerify () {
      this.formSysPost.formFilterCopy.postName = this.formSysPost.formFilter.postName;
      return true;
    },
    /**
     * 更新岗位管理
     */
    refreshFormSysPost (reloadData = false) {
      if (reloadData) {
        this.formSysPost.sysPost.impl.refreshTable(true, 1);
      } else {
        this.formSysPost.sysPost.impl.refreshTable();
      }
      if (!this.formSysPost.isInit) {
        // 初始化下拉数据
      }
      this.formSysPost.isInit = true;
    },
    /**
     * 新建
     */
    onFormAddPostClick () {
      let params = {};

      this.$dialog.show('新建', formEditPost, {
        area: '600px'
      }, params).then(res => {
        this.refreshFormSysPost();
      }).catch(e => {});
    },
    /**
     * 编辑
     */
    onFormEditPostClick (row) {
      let params = {
        postId: row.postId
      };

      this.$dialog.show('编辑', formEditPost, {
        area: '600px'
      }, params).then(res => {
        this.formSysPost.sysPost.impl.refreshTable();
      }).catch(e => {});
    },
    /**
     * 删除
     */
    onDeleteClick (row) {
      if (
        row.postId == null
      ) {
        this.$message.error('请求失败，发现必填参数为空！');
        return;
      }
      let params = {
        postId: row.postId
      };

      this.$confirm('是否删除此岗位？').then(res => {
        SysPostController.delete(this, params).then(res => {
          this.$message.success('删除成功');
          this.formSysPost.sysPost.impl.refreshTable();
        }).catch(e => {});
      }).catch(e => {});
    },
    onResume () {
      this.refreshFormSysPost();
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormSysPost();
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
