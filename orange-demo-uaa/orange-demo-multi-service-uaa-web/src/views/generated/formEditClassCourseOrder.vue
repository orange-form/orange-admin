<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formEditClassCourseOrder" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="课程顺序" prop="StudentClass.course.classCourse.courseOrder">
            <el-input-number class="input-item" v-model="formData.StudentClass.course.classCourse.courseOrder"
              :clearable="true"
              placeholder="课程顺序(数值越小越靠前)"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" :disabled="!checkPermCodeExist('formEditClassCourseOrder:formEditClassCourseOrder:updateClassCourse')"
              @click="onUpdateClassCourseClick()">
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
import { findTreeNode, findTreeNodePath, findItemFromList } from '@/utils';
/* eslint-disable-next-line */
import rules from '@/utils/validate.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { StudentClassController, DictionaryController } from '@/api';

export default {
  name: 'formEditClassCourseOrder',
  props: {
    classId: {
      default: undefined
    },
    courseId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        StudentClass: {
          classId: undefined,
          className: undefined,
          schoolId: undefined,
          leaderId: undefined,
          finishClassHour: undefined,
          classLevel: undefined,
          createUserId: undefined,
          createTime: undefined,
          status: undefined,
          course: {
            classCourse: {
              classId: undefined,
              courseId: undefined,
              courseOrder: undefined
            }
          },
          student: {
          },
          isDatasourceInit: false
        }
      },
      rules: {
        'StudentClass.course.classCourse.courseOrder': [
          {required: true, message: '请输入课程顺序', trigger: 'blur'}
        ]
      },
      formEditClassCourseOrder: {
        formFilter: {
        },
        formFilterCopy: {
        },
        menuBlock: {
          isInit: false
        },
        isInit: false
      }
    }
  },
  methods: {
    onCancel (isSuccess, data) {
      if (this.observer != null) {
        this.observer.cancel(isSuccess, data);
      }
    },
    /**
     * 更新编辑课程顺序
     */
    refreshFormEditClassCourseOrder (reloadData = false) {
      this.loadStudentClassData().then(res => {
        if (!this.formEditClassCourseOrder.isInit) {
          // 初始化下拉数据
        }
        this.formEditClassCourseOrder.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onUpdateClassCourseClick () {
      let params = {
        classCourseDto: {
          classId: this.classId,
          courseId: this.courseId,
          courseOrder: this.formData.StudentClass.course.classCourse.courseOrder
        }
      };

      StudentClassController.updateClassCourse(this, params).then(res => {
        this.$message.success('保存成功');
        this.onCancel(true);
      }).catch(e => {});
    },
    /**
     * 获取班级数据详细信息
     */
    loadStudentClassData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.StudentClass.isDatasourceInit) {
          if (
            this.classId == null ||
            this.courseId == null
          ) {
            this.resetFormData();
            reject();
            return;
          }
          let params = {
            classId: this.classId,
            courseId: this.courseId
          };
          StudentClassController.viewClassCourse(this, params).then(res => {
            this.formData.StudentClass.course.classCourse = {...res.data, isDatasourceInit: true};
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
     * 重置表单数据
     */
    resetFormData () {
      this.formData = {
        StudentClass: {
          classId: undefined,
          className: undefined,
          schoolId: undefined,
          leaderId: undefined,
          finishClassHour: undefined,
          classLevel: undefined,
          createUserId: undefined,
          createTime: undefined,
          status: undefined,
          course: {
            classCourse: {
              classId: undefined,
              courseId: undefined,
              courseOrder: undefined
            }
          },
          student: {
          },
          isDatasourceInit: false
        }
      }
    },
    formInit () {
      this.refreshFormEditClassCourseOrder();
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
