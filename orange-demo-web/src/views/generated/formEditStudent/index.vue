<template>
  <div class="form-single-fragment" style="position: relative;">
    <el-form ref="formEditStudent" :model="formData" class="full-width-input" :rules="rules" style="width: 100%;"
      label-width="100px" size="mini" label-position="right" @submit.native.prevent>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="姓名" prop="Student.studentName">
            <el-input class="input-item" v-model="formData.Student.studentName"
              :clearable="true" placeholder="学生姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="Student.gender">
            <el-select class="input-item" v-model="formData.Student.gender" :clearable="true" filterable
              placeholder="性别" :loading="formEditStudent.gender.impl.loading"
              @visible-change="formEditStudent.gender.impl.onVisibleChange"
              @change="onGenderValueChange">
              <el-option v-for="item in formEditStudent.gender.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出生日期" prop="Student.birthday">
            <el-date-picker class="input-item" v-model="formData.Student.birthday" :clearable="true"
              placeholder="出生日期" type="date" align="left"
              format="yyyy-MM-dd" value-format="yyyy-MM-dd hh:mm:ss" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号码" prop="Student.loginMobile">
            <el-input class="input-item" v-model="formData.Student.loginMobile"
              :clearable="true" placeholder="手机号码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所在年级" prop="Student.gradeId">
            <el-select class="input-item" v-model="formData.Student.gradeId" :clearable="true" filterable
              placeholder="所在年级" :loading="formEditStudent.gradeId.impl.loading"
              @visible-change="formEditStudent.gradeId.impl.onVisibleChange"
              @change="onGradeIdValueChange">
              <el-option v-for="item in formEditStudent.gradeId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="经验等级" prop="Student.experienceLevel">
            <el-select class="input-item" v-model="formData.Student.experienceLevel" :clearable="true" filterable
              placeholder="经验等级" :loading="formEditStudent.experienceLevel.impl.loading"
              @visible-change="formEditStudent.experienceLevel.impl.onVisibleChange"
              @change="onExperienceLevelValueChange">
              <el-option v-for="item in formEditStudent.experienceLevel.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所在省份" prop="Student.provinceId">
            <el-select class="input-item" v-model="formData.Student.provinceId" :clearable="true" filterable
              placeholder="所在省份" :loading="formEditStudent.provinceId.impl.loading"
              @visible-change="formEditStudent.provinceId.impl.onVisibleChange"
              @change="onProvinceIdValueChange">
              <el-option v-for="item in formEditStudent.provinceId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所在城市" prop="Student.cityId">
            <el-select class="input-item" v-model="formData.Student.cityId" :clearable="true" filterable
              placeholder="所在城市" :loading="formEditStudent.cityId.impl.loading"
              @visible-change="formEditStudent.cityId.impl.onVisibleChange"
              @change="onCityIdValueChange">
              <el-option v-for="item in formEditStudent.cityId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所在区县" prop="Student.districtId">
            <el-select class="input-item" v-model="formData.Student.districtId" :clearable="true" filterable
              placeholder="所在区县" :loading="formEditStudent.districtId.impl.loading"
              @visible-change="formEditStudent.districtId.impl.onVisibleChange"
              @change="onDistrictIdValueChange">
              <el-option v-for="item in formEditStudent.districtId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属校区" prop="Student.schoolId">
            <el-select class="input-item" v-model="formData.Student.schoolId" :clearable="true" filterable
              placeholder="所属校区" :loading="formEditStudent.schoolId.impl.loading"
              @visible-change="formEditStudent.schoolId.impl.onVisibleChange"
              @change="onSchoolIdValueChange">
              <el-option v-for="item in formEditStudent.schoolId.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="充值学币" prop="Student.totalCoin">
            <el-input-number class="input-item" v-model="formData.Student.totalCoin"
              :clearable="true" controls-position="right" placeholder="充值学币" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="剩余学币" prop="Student.leftCoin">
            <el-input-number class="input-item" v-model="formData.Student.leftCoin"
              :clearable="true" controls-position="right" placeholder="剩余学币" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学生状态 " prop="Student.status">
            <el-select class="input-item" v-model="formData.Student.status" :clearable="true" filterable
              placeholder="学生状态 " :loading="formEditStudent.status.impl.loading"
              @visible-change="formEditStudent.status.impl.onVisibleChange"
              @change="onStatusValueChange">
              <el-option v-for="item in formEditStudent.status.impl.dropdownList" :key="item.id" :value="item.id" :label="item.name" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" size="mini" :plain="true"
              @click="onCancel(false)">
              取消
            </el-button>
            <el-button type="primary" size="mini" :disabled="!checkPermCodeExist('formEditStudent:formEditStudent:update')"
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
import rules from '@/utils/validate.js';
/* eslint-disable-next-line */
import { DropdownWidget, TableWidget, UploadWidget, ChartWidget } from '@/utils/widget.js';
/* eslint-disable-next-line */
import { uploadMixin, statsDateRangeMixin } from '@/core/mixins';
/* eslint-disable-next-line */
import { StudentController, DictionaryController } from '@/api';

export default {
  name: 'formEditStudent',
  props: {
    studentId: {
      default: undefined
    }
  },
  mixins: [uploadMixin, statsDateRangeMixin],
  data () {
    return {
      formData: {
        Student: {
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
          status: undefined,
          isDatasourceInit: false
        }
      },
      rules: {
        'Student.studentName': [
          {required: true, message: '请输入姓名', trigger: 'blur'}
        ],
        'Student.gender': [
          {required: true, message: '请输入性别', trigger: 'blur'}
        ],
        'Student.birthday': [
          {required: true, message: '请输入出生日期', trigger: 'blur'}
        ],
        'Student.loginMobile': [
          {required: true, message: '请输入手机号码', trigger: 'blur'},
          {type: 'string', pattern: rules.pattern.mobie, message: '请输入正确的手机号码', trigger: 'blur'}
        ],
        'Student.gradeId': [
          {required: true, message: '请输入所在年级', trigger: 'blur'}
        ],
        'Student.experienceLevel': [
          {required: true, message: '请输入经验等级', trigger: 'blur'}
        ],
        'Student.provinceId': [
          {required: true, message: '请输入所在省份', trigger: 'blur'}
        ],
        'Student.cityId': [
          {required: true, message: '请输入所在城市', trigger: 'blur'}
        ],
        'Student.districtId': [
          {required: true, message: '请输入所在区县', trigger: 'blur'}
        ],
        'Student.schoolId': [
          {required: true, message: '请输入所属校区', trigger: 'blur'}
        ],
        'Student.totalCoin': [
          {required: true, message: '请输入充值学币', trigger: 'blur'},
          {type: 'integer', message: '充值学币只允许输入整数', trigger: 'blur', transform: (value) => Number(value)},
          {type: 'number', min: 0, message: '充值学币必须大于0', trigger: 'blur', transform: (value) => Number(value)}
        ],
        'Student.leftCoin': [
          {required: true, message: '请输入剩余学币', trigger: 'blur'},
          {type: 'integer', message: '剩余学币只允许输入整数', trigger: 'blur', transform: (value) => Number(value)},
          {type: 'number', min: 0, message: '剩余学币必须大于0', trigger: 'blur', transform: (value) => Number(value)}
        ],
        'Student.status': [
          {required: true, message: '请输入学生状态 ', trigger: 'blur'}
        ]
      },
      formEditStudent: {
        formFilter: {
        },
        formFilterCopy: {
        },
        gender: {
          impl: new DropdownWidget(this.loadGenderDropdownList)
        },
        gradeId: {
          impl: new DropdownWidget(this.loadGradeIdDropdownList)
        },
        experienceLevel: {
          impl: new DropdownWidget(this.loadExperienceLevelDropdownList)
        },
        provinceId: {
          impl: new DropdownWidget(this.loadProvinceIdDropdownList)
        },
        cityId: {
          impl: new DropdownWidget(this.loadCityIdDropdownList)
        },
        districtId: {
          impl: new DropdownWidget(this.loadDistrictIdDropdownList)
        },
        schoolId: {
          impl: new DropdownWidget(this.loadSchoolIdDropdownList)
        },
        status: {
          impl: new DropdownWidget(this.loadStatusDropdownList)
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
     * 所在年级下拉数据获取函数
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
     * 所在年级选中值改变
     */
    onGradeIdValueChange (value) {
    },
    /**
     * 经验等级下拉数据获取函数
     */
    loadExperienceLevelDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictExpLevel(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 经验等级选中值改变
     */
    onExperienceLevelValueChange (value) {
    },
    /**
     * 所在省份下拉数据获取函数
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
     * 所在省份选中值改变
     */
    onProvinceIdValueChange (value) {
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formData.Student.cityId = undefined;
      this.formEditStudent.cityId.impl.dirty = true;
      this.onCityIdValueChange(this.formData.Student.cityId);
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formData.Student.schoolId = undefined;
      this.formEditStudent.schoolId.impl.dirty = true;
      this.onSchoolIdValueChange(this.formData.Student.schoolId);
    },
    /**
     * 所在城市下拉数据获取函数
     */
    loadCityIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {
          parentId: this.formData.Student.provinceId
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
     * 所在城市选中值改变
     */
    onCityIdValueChange (value) {
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formData.Student.districtId = undefined;
      this.formEditStudent.districtId.impl.dirty = true;
      this.onDistrictIdValueChange(this.formData.Student.districtId);
      // 清除被过滤组件选中值，并且将被过滤组件的状态设置为dirty
      this.formData.Student.schoolId = undefined;
      this.formEditStudent.schoolId.impl.dirty = true;
      this.onSchoolIdValueChange(this.formData.Student.schoolId);
    },
    /**
     * 所在区县下拉数据获取函数
     */
    loadDistrictIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {
          parentId: this.formData.Student.cityId
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
     * 所在区县选中值改变
     */
    onDistrictIdValueChange (value) {
    },
    /**
     * 所属校区下拉数据获取函数
     */
    loadSchoolIdDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {
          provinceId: this.formData.Student.provinceId,
          cityId: this.formData.Student.cityId
        };
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
    },
    /**
     * 学生状态 下拉数据获取函数
     */
    loadStatusDropdownList () {
      return new Promise((resolve, reject) => {
        let params = {};
        DictionaryController.dictStudentStatus(this, params).then(res => {
          resolve(res.getList());
        }).catch(e => {
          reject(e);
        });
      });
    },
    /**
     * 学生状态 选中值改变
     */
    onStatusValueChange (value) {
    },
    /**
     * 更新编辑学生
     */
    refreshFormEditStudent (reloadData = false) {
      this.loadStudentData().then(res => {
        if (!this.formEditStudent.isInit) {
          // 初始化下拉数据
        }
        this.formEditStudent.isInit = true;
      }).catch(e => {});
    },
    /**
     * 保存
     */
    onUpdateClick () {
      this.$refs.formEditStudent.validate((valid) => {
        if (!valid) return;
        let params = {
          student: {
            studentId: this.studentId,
            loginMobile: this.formData.Student.loginMobile,
            studentName: this.formData.Student.studentName,
            provinceId: this.formData.Student.provinceId,
            cityId: this.formData.Student.cityId,
            districtId: this.formData.Student.districtId,
            gender: this.formData.Student.gender,
            birthday: this.formData.Student.birthday,
            experienceLevel: this.formData.Student.experienceLevel,
            totalCoin: this.formData.Student.totalCoin,
            leftCoin: this.formData.Student.leftCoin,
            gradeId: this.formData.Student.gradeId,
            schoolId: this.formData.Student.schoolId,
            status: this.formData.Student.status
          }
        };

        StudentController.update(this, params).then(res => {
          this.$message.success('保存成功');
          this.onCancel(true);
        }).catch(e => {});
      });
    },
    /**
     * 获取学生数据详细信息
     */
    loadStudentData () {
      return new Promise((resolve, reject) => {
        if (!this.formData.Student.isDatasourceInit) {
          let params = {
            studentId: this.studentId
          };
          StudentController.view(this, params).then(res => {
            this.formData.Student = {...res.data, isDatasourceInit: true};
            if (this.formData.Student.genderDictMap) this.formEditStudent.gender.impl.dropdownList = [this.formData.Student.genderDictMap];
            if (this.formData.Student.gradeIdDictMap) this.formEditStudent.gradeId.impl.dropdownList = [this.formData.Student.gradeIdDictMap];
            if (this.formData.Student.experienceLevelDictMap) this.formEditStudent.experienceLevel.impl.dropdownList = [this.formData.Student.experienceLevelDictMap];
            if (this.formData.Student.provinceIdDictMap) this.formEditStudent.provinceId.impl.dropdownList = [this.formData.Student.provinceIdDictMap];
            if (this.formData.Student.cityIdDictMap) this.formEditStudent.cityId.impl.dropdownList = [this.formData.Student.cityIdDictMap];
            if (this.formData.Student.districtIdDictMap) this.formEditStudent.districtId.impl.dropdownList = [this.formData.Student.districtIdDictMap];
            if (this.formData.Student.schoolIdDictMap) this.formEditStudent.schoolId.impl.dropdownList = [this.formData.Student.schoolIdDictMap];
            if (this.formData.Student.statusDictMap) this.formEditStudent.status.impl.dropdownList = [this.formData.Student.statusDictMap];
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
      this.refreshFormEditStudent();
    }
  },
  computed: {
  },
  created () {
    this.formInit();
  }
}
</script>
