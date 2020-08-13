<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formCreateClass" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
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
              placeholder="班级级别" :loading="formCreateClass.classLevel.impl.loading"
              @visible-change="formCreateClass.classLevel.impl.onVisibleChange"
              @change="onClassLevelValueChange">
              <el-option v-for="item in formCreateClass.classLevel.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属校区" prop="StudentClass.schoolId">
            <el-select class="input-item" v-model="formData.StudentClass.schoolId" :clearable="true" filterable
              placeholder="所属校区" :loading="formCreateClass.schoolId.impl.loading"
              @visible-change="formCreateClass.schoolId.impl.onVisibleChange"
              @change="onSchoolIdValueChange">
              <el-option v-for="item in formCreateClass.schoolId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="班长" prop="StudentClass.leaderId">
            <el-select class="input-item" v-model="formData.StudentClass.leaderId" :clearable="true" filterable
              placeholder="班长" :loading="formCreateClass.leaderId.impl.loading"
              @visible-change="formCreateClass.leaderId.impl.onVisibleChange"
              @change="onLeaderIdValueChange">
              <el-option v-for="item in formCreateClass.leaderId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
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
            <el-button type="primary" size="mini" :disabled="!checkPermCodeExist('formCreateClass:formCreateClass:add')"
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
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { StudentClassController, DictionaryController } from '@/api';

export default {
  name: 'formCreateClass',
  props: {
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
      formCreateClass: {
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
      this.formCreateClass.leaderId.impl.dirty = true;
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
     * 更新新建班级
     */
    refreshFormCreateClass (reloadData = false) {
      if (!this.formCreateClass.isInit) {
        // 初始化下拉数据
      }
      this.formCreateClass.isInit = true;
    },
    /**
     * 保存
     */
    onAddClick () {
      this.$refs.formCreateClass.validate((valid) => {
        if (!valid) return;
        let params = {
          studentClass: {
            className: this.formData.StudentClass.className,
            schoolId: this.formData.StudentClass.schoolId,
            leaderId: this.formData.StudentClass.leaderId,
            finishClassHour: this.formData.StudentClass.finishClassHour,
            classLevel: this.formData.StudentClass.classLevel
          }
        };

        StudentClassController.add(this, params).then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    initFormData () {
    },
    formInit () {
      this.refreshFormCreateClass();
    }
  },
  computed: {
  },
  created () {
    this.formInit();
  }
}
</script>
