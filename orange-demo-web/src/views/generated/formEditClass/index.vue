<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formEditClass" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="班级名称" prop="StudentClass.className">
            <el-input class="input-item" v-model="formData.StudentClass.className"
              :clearable="true" placeholder="班级名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="班级级别" prop="StudentClass.classLevel">
            <el-select class="input-item" v-model="formData.StudentClass.classLevel" :clearable="true" filterable
              placeholder="班级级别" :loading="formEditClass.classLevel.impl.loading"
              @visible-change="formEditClass.classLevel.impl.onVisibleChange"
              @change="onClassLevelValueChange">
              <el-option v-for="item in formEditClass.classLevel.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属校区" prop="StudentClass.schoolId">
            <el-select class="input-item" v-model="formData.StudentClass.schoolId" :clearable="true" filterable
              placeholder="所属校区" :loading="formEditClass.schoolId.impl.loading"
              @visible-change="formEditClass.schoolId.impl.onVisibleChange"
              @change="onSchoolIdValueChange">
              <el-option v-for="item in formEditClass.schoolId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="班长" prop="StudentClass.leaderId">
            <el-select class="input-item" v-model="formData.StudentClass.leaderId" :clearable="true" filterable
              placeholder="班长" :loading="formEditClass.leaderId.impl.loading"
              @visible-change="formEditClass.leaderId.impl.onVisibleChange"
              @change="onLeaderIdValueChange">
              <el-option v-for="item in formEditClass.leaderId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="已完成课时" prop="StudentClass.finishClassHour">
            <el-input-number class="input-item" v-model="formData.StudentClass.finishClassHour"
              :clearable="true" controls-position="right" placeholder="已完成课时" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" :disabled="!checkPermCodeExist('formEditClass:formEditClass:update')"
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
import { StudentClassController, DictionaryController } from '@/api';

export default {
  name: 'formEditClass',
  props: {
    classId: {
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
            updateTime: undefined
          },
          student: {
            studentId: undefined,
            loginMobile: undefined,
            studentName: undefined,
            provinceId: undefined,
            cityId: undefined,
            districtId: undefined,
            gender: undefined,
            birthday: undefined,
            experienceLevel: undefined,
            totalCoin: undefined,
            leftCoin: undefined,
            gradeId: undefined,
            schoolId: undefined,
            registerTime: undefined,
            status: undefined
          },
          isDatasourceInit: false
        }
      },
      rules: {
        'StudentClass.className': [
          {required: true, message: '请输入班级名称', trigger: 'blur'}
        ],
        'StudentClass.classLevel': [
          {required: true, message: '请输入班级级别', trigger: 'blur'}
        ],
        'StudentClass.schoolId': [
          {required: true, message: '请输入所属校区', trigger: 'blur'}
        ],
        'StudentClass.leaderId': [
          {required: true, message: '请输入班长', trigger: 'blur'}
        ],
        'StudentClass.finishClassHour': [
          {required: true, message: '请输入已完成课时', trigger: 'blur'},
          {type: 'integer', message: '已完成课时只允许输入整数', trigger: 'blur', transform: (value) => Number(value)},
          {type: 'number', min: 0, max: 9999, message: '已完成课时必须在0 - 9999之间', trigger: 'blur', transform: (value) => Number(value)}
        ]
      },
      formEditClass: {
        formFilter: {
        },
        formFilterCopy: {
        },
        classLevel: {
          impl: new DropdownWidget(this.loadClassLevelDropdownList)
        },
        schoolId: {
          impl: new DropdownWidget(this.loadSchoolIdDropdownList)
        },
        leaderId: {
          impl: new DropdownWidget(this.loadLeaderIdDropdownList)
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
     * 班级级别下拉数据获取函数
     */
    loadClassLevelDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictClassLevel(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 班级级别选中值改变
     */
    onClassLevelValueChange (value) {
    },
    /**
     * 所属校区下拉数据获取函数
     */
    loadSchoolIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSchoolInfo(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 所属校区选中值改变
     */
    onSchoolIdValueChange (value) {
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formData.StudentClass.leaderId = undefined;
      this.formEditClass.leaderId.impl.dirty = true;
      this.onLeaderIdValueChange(this.formData.StudentClass.leaderId);
    },
    /**
     * 班长下拉数据获取函数
     */
    loadLeaderIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {
          schoolId: this.formData.StudentClass.schoolId
        };
        DictionaryController.dictStudent(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 班长选中值改变
     */
    onLeaderIdValueChange (value) {
    },
    /**
     * 更新编辑班级
     */
    refreshFormEditClass (reloadData = false) {
      this.loadStudentClassData().then(res => {
        if (!this.formEditClass.isInit) {
          // 初始化下拉数据
        }
        this.formEditClass.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onUpdateClick () {
      this.$refs.formEditClass.validate((valid) => {
        if (!valid) return;
        let params = {
          studentClass: {
            classId: this.classId,
            className: this.formData.StudentClass.className,
            schoolId: this.formData.StudentClass.schoolId,
            leaderId: this.formData.StudentClass.leaderId,
            finishClassHour: this.formData.StudentClass.finishClassHour,
            classLevel: this.formData.StudentClass.classLevel
          }
        };

        StudentClassController.update(this, params).then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 获取班级数据详细信息
     */
    loadStudentClassData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.StudentClass.isDatasourceInit) {
          let params = {
            classId: this.classId
          };
          StudentClassController.view(this, params).then(res => {
            this.formData.StudentClass = {...res.data, isDatasourceInit: true};
            if (this.formData.StudentClass.classLevelDictMap) this.formEditClass.classLevel.impl.dropdownList = [this.formData.StudentClass.classLevelDictMap];
            if (this.formData.StudentClass.schoolIdDictMap) this.formEditClass.schoolId.impl.dropdownList = [this.formData.StudentClass.schoolIdDictMap];
            if (this.formData.StudentClass.leaderIdDictMap) this.formEditClass.leaderId.impl.dropdownList = [this.formData.StudentClass.leaderIdDictMap];
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
    formInit () {
      this.refreshFormEditClass();
    }
  },
  computed: {
  },
  created () {
    this.formInit();
  }
}
</script>
