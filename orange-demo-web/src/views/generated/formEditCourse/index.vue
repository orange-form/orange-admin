<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formEditCourse" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="课程名称" prop="Course.courseName">
            <el-input class="input-item" v-model="formData.Course.courseName"
              :clearable="true" placeholder="课程名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程价格" prop="Course.price">
            <el-input-number class="input-item" v-model="formData.Course.price"
              :clearable="true" controls-position="right" placeholder="课程价格" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程难度" prop="Course.difficulty">
            <el-select class="input-item" v-model="formData.Course.difficulty" :clearable="true" filterable
              placeholder="课程难度" :loading="formEditCourse.difficulty.impl.loading"
              @visible-change="formEditCourse.difficulty.impl.onVisibleChange"
              @change="onDifficultyValueChange">
              <el-option v-for="item in formEditCourse.difficulty.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属年级" prop="Course.gradeId">
            <el-select class="input-item" v-model="formData.Course.gradeId" :clearable="true" filterable
              placeholder="所属年级" :loading="formEditCourse.gradeId.impl.loading"
              @visible-change="formEditCourse.gradeId.impl.onVisibleChange"
              @change="onGradeIdValueChange">
              <el-option v-for="item in formEditCourse.gradeId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属学科" prop="Course.subjectId">
            <el-select class="input-item" v-model="formData.Course.subjectId" :clearable="true" filterable
              placeholder="所属学科" :loading="formEditCourse.subjectId.impl.loading"
              @visible-change="formEditCourse.subjectId.impl.onVisibleChange"
              @change="onSubjectIdValueChange">
              <el-option v-for="item in formEditCourse.subjectId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课时数量" prop="Course.classHour">
            <el-input-number class="input-item" v-model="formData.Course.classHour"
              :clearable="true" controls-position="right" placeholder="课时数量" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="课程描述" prop="Course.description">
            <el-input class="input-item" v-model="formData.Course.description"
              :clearable="true" placeholder="课程描述" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="课程图片" prop="Course.pictureUrl">
            <el-upload class="upload-image-item upload-image-multi" name="uploadFile" :headers="getUploadHeaders"
              :action="getUploadActionUrl('/admin/CourseClass/course/upload')"
              :data="{fieldName: 'pictureUrl', asImage: true}"
              :on-success="onPictureUrlUploadSuccess"
              :on-remove="onPictureUrlRemoveFile"
              :before-upload="pictureFile"
              :on-error="onUploadError" :on-exceed="onUploadLimit"
              list-type="picture-card" :file-list="formEditCourse.pictureUrl.impl.fileList" :limit="formEditCourse.pictureUrl.impl.maxCount"
              :show-file-list="true">
              <i class="el-icon-plus upload-image-item"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" :disabled="!checkPermCodeExist('formEditCourse:formEditCourse:update')"
              @click="onUpdateClick()">
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
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { CourseController, DictionaryController } from '@/api';

export default {
  name: 'formEditCourse',
  props: {
    courseId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        Course: {
          courseId: undefined,
          courseName: undefined,
          price: undefined,
          description: undefined,
          difficulty: undefined,
          gradeId: undefined,
          subjectId: undefined,
          classHour: undefined,
          pictureUrl: undefined,
          createUserId: undefined,
          createTime: undefined,
          updateTime: undefined,
          isDatasourceInit: false
        }
      },
      rules: {
        'Course.courseName': [
          {required: true, message: '请输入课程名称', trigger: 'blur'}
        ],
        'Course.price': [
          {required: true, message: '请输入课程价格', trigger: 'blur'},
          {type: 'number', message: '课程价格只允许输入数字', trigger: 'blur', transform: (value) => Number(value)},
          {type: 'number', min: 0, message: '课程价格必须大于0', trigger: 'blur', transform: (value) => Number(value)}
        ],
        'Course.difficulty': [
          {required: true, message: '请输入课程难度', trigger: 'blur'}
        ],
        'Course.gradeId': [
          {required: true, message: '请输入所属年级', trigger: 'blur'}
        ],
        'Course.subjectId': [
          {required: true, message: '请输入所属学科', trigger: 'blur'}
        ],
        'Course.classHour': [
          {required: true, message: '请输入课时数量', trigger: 'blur'},
          {type: 'integer', message: '课时数量只允许输入整数', trigger: 'blur', transform: (value) => Number(value)},
          {type: 'number', min: 1, message: '课时数量必须大于1', trigger: 'blur', transform: (value) => Number(value)}
        ],
        'Course.pictureUrl': [
          {required: true, message: '请输入课程图片', trigger: 'blur'}
        ]
      },
      formEditCourse: {
        formFilter: {
        },
        formFilterCopy: {
        },
        difficulty: {
          impl: new DropdownWidget(this.loadDifficultyDropdownList)
        },
        gradeId: {
          impl: new DropdownWidget(this.loadGradeIdDropdownList)
        },
        subjectId: {
          impl: new DropdownWidget(this.loadSubjectIdDropdownList)
        },
        pictureUrl: {
          impl: new UploadWidget(4)
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
     * 课程难度下拉数据获取函数
     */
    loadDifficultyDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictCourseDifficult(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 课程难度选中值改变
     */
    onDifficultyValueChange (value) {
    },
    /**
     * 所属年级下拉数据获取函数
     */
    loadGradeIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictGrade(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所属年级选中值改变
     */
    onGradeIdValueChange (value) {
    },
    /**
     * 所属学科下拉数据获取函数
     */
    loadSubjectIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSubject(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所属学科选中值改变
     */
    onSubjectIdValueChange (value) {
    },
    /**
     * 更新编辑课程
     */
    refreshFormEditCourse (reloadData = false) {
      this.loadCourseData().then(res => {
        if (!this.formEditCourse.isInit) {
          // 初始化下拉数据
        }
        this.formEditCourse.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onUpdateClick () {
      this.$refs.formEditCourse.validate((valid) => {
        if (!valid) return;
        let params = {
          course: {
            courseId: this.courseId,
            courseName: this.formData.Course.courseName,
            price: this.formData.Course.price,
            description: this.formData.Course.description,
            difficulty: this.formData.Course.difficulty,
            gradeId: this.formData.Course.gradeId,
            subjectId: this.formData.Course.subjectId,
            classHour: this.formData.Course.classHour,
            pictureUrl: this.formData.Course.pictureUrl
          }
        };

        CourseController.update(this, params).then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 获取课程数据详细信息
     */
    loadCourseData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.Course.isDatasourceInit) {
          let params = {
            courseId: this.courseId
          };
          CourseController.view(this, params).then(res => {
            this.formData.Course = {...res.data, isDatasourceInit: true};
            if (this.formData.Course.difficultyDictMap) this.formEditCourse.difficulty.impl.dropdownList = [this.formData.Course.difficultyDictMap];
            if (this.formData.Course.gradeIdDictMap) this.formEditCourse.gradeId.impl.dropdownList = [this.formData.Course.gradeIdDictMap];
            if (this.formData.Course.subjectIdDictMap) this.formEditCourse.subjectId.impl.dropdownList = [this.formData.Course.subjectIdDictMap];
            let pictureUrlDownloadParams = {
              courseId: this.formData.Course.courseId,
              fieldName: 'pictureUrl',
              asImage: true
            }
            this.formEditCourse.pictureUrl.impl.fileList = this.parseUploadData(this.formData.Course.pictureUrl, pictureUrlDownloadParams);
            resolve();
          }).catch(e => {
            reject();
          });
        } else {
          resolve();
        }
      });
    },
    initFormData () {
    },
    /**
     * 课程图片上传成功
     */
    onPictureUrlUploadSuccess (response, file, fileList) {
      if (response.success) {
        file.downloadUri = response.data.downloadUri;
        file.filename = response.data.filename;
        file.url = URL.createObjectURL(file.raw);
        this.formEditCourse.pictureUrl.impl.onFileChange(file, fileList);
        this.formData.Course.pictureUrl = this.fileListToJson(this.formEditCourse.pictureUrl.impl.fileList);
      } else {
        this.$message.error(response.message);
      }
    },
    /**
     * 移除课程图片
     */
    onPictureUrlRemoveFile (file, fileList) {
      this.formEditCourse.pictureUrl.impl.onFileChange(file, fileList);
      this.formData.Course.pictureUrl = this.fileListToJson(this.formEditCourse.pictureUrl.impl.fileList);
    },
    onUploadError (e, file, fileList) {
      this.$message.error('文件上传失败');
    },
    onUploadLimit (files, fileList) {
      this.$message.error('已经超出最大上传个数限制');
    },
    formInit () {
      this.refreshFormEditCourse();
    }
  },
  computed: {
  },
  created () {
    this.formInit();
  }
}
</script>
