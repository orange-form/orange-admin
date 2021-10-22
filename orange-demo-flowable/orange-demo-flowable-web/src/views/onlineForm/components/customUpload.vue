<template>
  <el-col :span="widgetConfig.span || 24">
    <el-form-item :label="widgetConfig.showName + '：'"
      :prop="(widgetConfig.relation ? (widgetConfig.relation.variableName + '__') : '') + (widgetConfig.column || {}).columnName">
      <el-upload v-if="!widgetConfig.readOnly"
        :class="{
          'upload-image-item': widgetConfig.isImage,
          'upload-image-multi': widgetConfig.column.maxFileCount !== 1
        }"
        :name="widgetConfig.fileFieldName"
        :headers="getUploadHeaders"
        :action="getActionUrl"
        :data="getUploadData"
        :on-success="onUploadSuccess"
        :on-remove="onRemoveFile"
        :on-error="onUploadError"
        :on-exceed="onUploadLimit"
        :limit="widgetConfig.column.maxFileCount"
        :show-file-list="widgetConfig.column.maxFileCount !== 1 || !widgetConfig.isImage"
        :list-type="getUploadListType"
        :file-list="getUploadFileList"
        :disabled="widgetConfig.disabled"
      >
        <!-- 上传图片 -->
        <template v-if="widgetConfig.isImage && widgetConfig.column.maxFileCount === 1">
          <div v-if="getUploadFileList && getUploadFileList[0] != null" style="position: relative">
            <img class="upload-image-show" :src="getUploadFileList[0].url" />
            <div class="upload-img-del el-icon-close" @click.stop="onRemoveFile(null, null)" />
          </div>
          <i v-else class="el-icon-plus upload-image-item"></i>
        </template>
        <!-- 上传文件 -->
        <template v-else-if="!widgetConfig.isImage">
          <el-button size="mini" type="primary">点击上传</el-button>
        </template>
      </el-upload>
      <template v-else>
        <template v-if="widgetConfig.isImage">
          <el-image v-for="item in uploadWidgetImpl.fileList"
            :preview-src-list="(uploadWidgetImpl.fileList || []).map(imgItem => imgItem.url)"
            class="table-cell-image" :key="item.url" :src="item.url" fit="fill">
          </el-image>
        </template>
        <a v-else v-for="item in uploadWidgetImpl.fileList" :key="item.url" href="javascript:void(0);" @click="downloadFile(item.url, item.name)">
          {{item.name}}
        </a>
      </template>
    </el-form-item>
  </el-col>
</template>

<script>
// import { buildGetUrl } from '@/core/http/requestUrl.js';
import { UploadWidget } from '@/utils/widget.js';
import { uploadMixin } from '@/core/mixins';

export default {
  props: {
    widgetConfig: {
      type: Object,
      required: true
    },
    flowData: {
      type: Object
    },
    value: {
      type: String
    },
    getDataId: {
      type: Function
    }
  },
  mixins: [uploadMixin],
  data () {
    return {
      uploadWidgetImpl: new UploadWidget(this.widgetConfig.column.maxFileCount)
    }
  },
  methods: {
    onValueChange () {
      this.$emit('input', this.fileListToJson(this.uploadWidgetImpl.fileList), this.widgetConfig);
    },
    onUploadSuccess (response, file, fileList) {
      if (response.success) {
        file.filename = response.data.filename;
        file.url = URL.createObjectURL(file.raw);
        this.uploadWidgetImpl.onFileChange(file, fileList);
        this.onValueChange();
      } else {
        this.$message.error(response.message);
      }
    },
    onRemoveFile (file, fileList) {
      this.uploadWidgetImpl.onFileChange(file, fileList);
      this.onValueChange();
    },
    onUploadError (e, file, fileList) {
      this.$message.error('文件上传失败');
    },
    onUploadLimit (files, fileList) {
      if (this.widgetConfig.column.maxFileCount != null && this.widgetConfig.column.maxFileCount > 0) {
        this.$message.error('已经超出最大上传个数限制');
      }
    }
  },
  computed: {
    buildFlowParam () {
      let flowParam = {};
      if (this.flowData) {
        if (this.flowData.processDefinitionKey) flowParam.processDefinitionKey = this.flowData.processDefinitionKey;
        if (this.flowData.processInstanceId) flowParam.processInstanceId = this.flowData.processInstanceId;
        if (this.flowData.taskId) flowParam.taskId = this.flowData.taskId;
      }

      return flowParam;
    },
    getActionUrl () {
      if (this.widgetConfig.actionUrl == null || this.widgetConfig.actionUrl === '') {
        if (this.widgetConfig.relation) {
          return this.getUploadActionUrl('/admin/online/onlineOperation/uploadOneToManyRelation/' + (this.widgetConfig.datasource || {}).variableName);
        } else {
          return this.getUploadActionUrl('/admin/online/onlineOperation/uploadDatasource/' + (this.widgetConfig.datasource || {}).variableName);
        }
      } else {
        return this.getUploadActionUrl(this.widgetConfig.actionUrl);
      }
    },
    getDownloadUrl () {
      if (this.widgetConfig.downloadUrl == null || this.widgetConfig.downloadUrl === '') {
        if (this.widgetConfig.relation) {
          return '/admin/online/onlineOperation/downloadOneToManyRelation/' + (this.widgetConfig.datasource || {}).variableName;
        } else {
          return '/admin/online/onlineOperation/downloadDatasource/' + (this.widgetConfig.datasource || {}).variableName;
        }
      } else {
        return this.widgetConfig.downloadUrl;
      }
    },
    getUploadData () {
      let temp = {
        ...this.buildFlowParam,
        datasourceId: (this.widgetConfig.datasource || {}).datasourceId,
        asImage: this.widgetConfig.isImage,
        fieldName: (this.widgetConfig.column || {}).columnName
      }
      if ((this.widgetConfig.relation || {}).relationId) temp.relationId = (this.widgetConfig.relation || {}).relationId;
      return temp;
    },
    getUploadListType () {
      if (this.widgetConfig.column.maxFileCount !== 1 && this.widgetConfig.isImage) {
        return 'picture-card';
      }
      return 'text';
    },
    getUploadFileList () {
      return this.uploadWidgetImpl ? this.uploadWidgetImpl.fileList : [];
    }
  },
  mounted () {
  },
  watch: {
    value: {
      handler (newValue) {
        this.uploadWidgetImpl.fileList = [];
        if (newValue != null) {
          let downloadParams = {
            ...this.buildFlowParam,
            datasourceId: this.widgetConfig.datasourceId,
            fieldName: this.widgetConfig.column.columnName,
            asImage: this.widgetConfig.isImage,
            dataId: this.getDataId(this.widgetConfig) || ''
          }
          if (this.widgetConfig.relationId) downloadParams.relationId = this.widgetConfig.relationId;
          let temp = JSON.parse(newValue).map(item => {
            return {
              ...item,
              downloadUri: this.getDownloadUrl
            }
          })
          this.uploadWidgetImpl.fileList = this.parseUploadData(JSON.stringify(temp), downloadParams);
        }
      },
      immediate: true
    }
  }
}
</script>

<style>
</style>
