<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formEditTeacher" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="教师名称" prop="Teacher.teacherName">
            <el-input class="input-item" v-model="formData.Teacher.teacherName"
              :clearable="true" placeholder="教师名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出生日期" prop="Teacher.birthday">
            <el-date-picker class="input-item" v-model="formData.Teacher.birthday" :clearable="true"
              placeholder="出生日期" type="date" align="left"
              format="yyyy-MM-dd" value-format="yyyy-MM-dd hh:mm:ss" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="Teacher.gender">
            <el-select class="input-item" v-model="formData.Teacher.gender" :clearable="true" filterable
              placeholder="性别" :loading="formEditTeacher.gender.impl.loading"
              @visible-change="formEditTeacher.gender.impl.onVisibleChange"
              @change="onGenderValueChange">
              <el-option v-for="item in formEditTeacher.gender.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所教科目" prop="Teacher.subjectId">
            <el-select class="input-item" v-model="formData.Teacher.subjectId" :clearable="true" filterable
              placeholder="所教科目" :loading="formEditTeacher.subjectId.impl.loading"
              @visible-change="formEditTeacher.subjectId.impl.onVisibleChange"
              @change="onSubjectIdValueChange">
              <el-option v-for="item in formEditTeacher.subjectId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="职级" prop="Teacher.level">
            <el-select class="input-item" v-model="formData.Teacher.level" :clearable="true" filterable
              placeholder="职级" :loading="formEditTeacher.level.impl.loading"
              @visible-change="formEditTeacher.level.impl.onVisibleChange"
              @change="onLevelValueChange">
              <el-option v-for="item in formEditTeacher.level.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="鲜花数量" prop="Teacher.flowerCount">
            <el-input-number class="input-item" v-model="formData.Teacher.flowerCount"
              :clearable="true" controls-position="right" placeholder="鲜花数量" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属校区" prop="Teacher.schoolId">
            <el-select class="input-item" v-model="formData.Teacher.schoolId" :clearable="true" filterable
              placeholder="所属校区" :loading="formEditTeacher.schoolId.impl.loading"
              @visible-change="formEditTeacher.schoolId.impl.onVisibleChange"
              @change="onSchoolIdValueChange">
              <el-option v-for="item in formEditTeacher.schoolId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="绑定用户" prop="Teacher.userId">
            <el-select class="input-item" v-model="formData.Teacher.userId" :clearable="true" filterable
              placeholder="绑定用户" :loading="formEditTeacher.userId.impl.loading"
              @visible-change="formEditTeacher.userId.impl.onVisibleChange"
              @change="onUserIdValueChange">
              <el-option v-for="item in formEditTeacher.userId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否在职" prop="Teacher.available">
            <el-select class="input-item" v-model="formData.Teacher.available" :clearable="true" filterable
              placeholder="是否在职" :loading="formEditTeacher.available.impl.loading"
              @visible-change="formEditTeacher.available.impl.onVisibleChange"
              @change="onAvailableValueChange">
              <el-option v-for="item in formEditTeacher.available.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" :disabled="!checkPermCodeExist('formEditTeacher:formEditTeacher:update')"
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
import { TeacherController, DictionaryController } from '@/api';

export default {
  name: 'formEditTeacher',
  props: {
    teacherId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        Teacher: {
          teacherId: undefined,
          teacherName: undefined,
          birthday: undefined,
          gender: undefined,
          subjectId: undefined,
          level: undefined,
          flowerCount: undefined,
          schoolId: undefined,
          userId: undefined,
          registerDate: undefined,
          available: undefined,
          sysDept: {
            deptId: undefined,
            deptName: undefined,
            showOrder: undefined,
            deletedFlag: undefined,
            createUserId: undefined,
            createUsername: undefined,
            createTime: undefined,
            updateTime: undefined
          },
          isDatasourceInit: false
        }
      },
      rules: {
        'Teacher.teacherName': [
          {required: true, message: '请输入教师名称', trigger: 'blur'}
        ],
        'Teacher.birthday': [
          {required: true, message: '请输入出生日期', trigger: 'blur'}
        ],
        'Teacher.gender': [
          {required: true, message: '请输入性别', trigger: 'blur'}
        ],
        'Teacher.subjectId': [
          {required: true, message: '请输入所教科目', trigger: 'blur'}
        ],
        'Teacher.level': [
          {required: true, message: '请输入职级', trigger: 'blur'}
        ],
        'Teacher.flowerCount': [
          {type: 'integer', message: '鲜花数量只允许输入整数', trigger: 'blur', transform: (value) => Number(value)},
          {type: 'number', min: 0, message: '鲜花数量必须大于0', trigger: 'blur', transform: (value) => Number(value)}
        ],
        'Teacher.schoolId': [
          {required: true, message: '请输入所属校区', trigger: 'blur'}
        ],
        'Teacher.userId': [
          {required: true, message: '请输入绑定用户', trigger: 'blur'}
        ],
        'Teacher.available': [
          {required: true, message: '请输入是否在职', trigger: 'blur'}
        ]
      },
      formEditTeacher: {
        formFilter: {
        },
        formFilterCopy: {
        },
        gender: {
          impl: new DropdownWidget(this.loadGenderDropdownList)
        },
        subjectId: {
          impl: new DropdownWidget(this.loadSubjectIdDropdownList)
        },
        level: {
          impl: new DropdownWidget(this.loadLevelDropdownList)
        },
        schoolId: {
          impl: new DropdownWidget(this.loadSchoolIdDropdownList)
        },
        userId: {
          impl: new DropdownWidget(this.loadUserIdDropdownList)
        },
        available: {
          impl: new DropdownWidget(this.loadAvailableDropdownList)
        },
        menu: {
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
     * 性别下拉数据获取函数
     */
    loadGenderDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictGender(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 性别选中值改变
     */
    onGenderValueChange (value) {
    },
    /**
     * 所教科目下拉数据获取函数
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
     * 所教科目选中值改变
     */
    onSubjectIdValueChange (value) {
    },
    /**
     * 职级下拉数据获取函数
     */
    loadLevelDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictTeacherLevelType(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 职级选中值改变
     */
    onLevelValueChange (value) {
    },
    /**
     * 所属校区下拉数据获取函数
     */
    loadSchoolIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictSysDept(this, params).then(res => {
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
      this.formData.Teacher.userId = undefined;
      this.formEditTeacher.userId.impl.dirty = true;
      this.onUserIdValueChange(this.formData.Teacher.userId);
    },
    /**
     * 绑定用户下拉数据获取函数
     */
    loadUserIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {
          deptId: this.formData.Teacher.schoolId
        };
        DictionaryController.dictSysUser(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 绑定用户选中值改变
     */
    onUserIdValueChange (value) {
    },
    /**
     * 是否在职下拉数据获取函数
     */
    loadAvailableDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictYesNo(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 是否在职选中值改变
     */
    onAvailableValueChange (value) {
    },
    /**
     * 更新编辑老师
     */
    refreshFormEditTeacher (reloadData = false) {
      this.loadTeacherData().then(res => {
        if (!this.formEditTeacher.isInit) {
          // 初始化下拉数据
        }
        this.formEditTeacher.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onUpdateClick () {
      this.$refs.formEditTeacher.validate((valid) => {
        if (!valid) return;
        let params = {
          teacher: {
            teacherId: this.teacherId,
            teacherName: this.formData.Teacher.teacherName,
            birthday: this.formData.Teacher.birthday,
            gender: this.formData.Teacher.gender,
            subjectId: this.formData.Teacher.subjectId,
            level: this.formData.Teacher.level,
            flowerCount: this.formData.Teacher.flowerCount,
            schoolId: this.formData.Teacher.schoolId,
            userId: this.formData.Teacher.userId,
            available: this.formData.Teacher.available
          }
        };

        TeacherController.update(this, params).then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 获取老师数据源详细信息
     */
    loadTeacherData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.Teacher.isDatasourceInit) {
          let params = {
            teacherId: this.teacherId
          };
          TeacherController.view(this, params).then(res => {
            this.formData.Teacher = {...res.data, isDatasourceInit: true};
            if (this.formData.Teacher.genderDictMap) this.formEditTeacher.gender.impl.dropdownList = [this.formData.Teacher.genderDictMap];
            if (this.formData.Teacher.subjectIdDictMap) this.formEditTeacher.subjectId.impl.dropdownList = [this.formData.Teacher.subjectIdDictMap];
            if (this.formData.Teacher.levelDictMap) this.formEditTeacher.level.impl.dropdownList = [this.formData.Teacher.levelDictMap];
            if (this.formData.Teacher.schoolIdDictMap) this.formEditTeacher.schoolId.impl.dropdownList = [this.formData.Teacher.schoolIdDictMap];
            if (this.formData.Teacher.userIdDictMap) this.formEditTeacher.userId.impl.dropdownList = [this.formData.Teacher.userIdDictMap];
            if (this.formData.Teacher.availableDictMap) this.formEditTeacher.available.impl.dropdownList = [this.formData.Teacher.availableDictMap];
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
      this.refreshFormEditTeacher();
    }
  },
  computed: {
  },
  created () {
    this.formInit();
  }
}
</script>
