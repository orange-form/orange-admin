import * as staticDict from '@/staticDict'

export default class DictionaryController {
  static dictSysUserStatus () {
    return new Promise((resolve) => {
      resolve(staticDict.SysUserStatus);
    });
  }
  static dictSysUserType () {
    return new Promise((resolve) => {
      resolve(staticDict.SysUserType);
    });
  }
  static dictSubject () {
    return new Promise((resolve) => {
      resolve(staticDict.Subject);
    });
  }
  static dictStudentActionType () {
    return new Promise((resolve) => {
      resolve(staticDict.StudentActionType);
    });
  }
  static dictDeviceType () {
    return new Promise((resolve) => {
      resolve(staticDict.DeviceType);
    });
  }
  static dictGender () {
    return new Promise((resolve) => {
      resolve(staticDict.Gender);
    });
  }
  static dictExpLevel () {
    return new Promise((resolve) => {
      resolve(staticDict.ExpLevel);
    });
  }
  static dictStudentStatus () {
    return new Promise((resolve) => {
      resolve(staticDict.StudentStatus);
    });
  }
  static dictClassStatus () {
    return new Promise((resolve) => {
      resolve(staticDict.ClassStatus);
    });
  }
  static dictClassLevel () {
    return new Promise((resolve) => {
      resolve(staticDict.ClassLevel);
    });
  }
  static dictCourseDifficult () {
    return new Promise((resolve) => {
      resolve(staticDict.CourseDifficult);
    });
  }
  static dictAreaCode (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/areaCode/listDictAreaCode', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase();
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictAreaCodeByParentId (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/areaCode/listDictAreaCodeByParentId', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase();
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictAddAreaCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('', 'post', params, axiosOption, httpOption);
  }
  static dictDeleteAreaCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('', 'post', params, axiosOption, httpOption);
  }
  static dictBatchDeleteAreaCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('', 'post', params, axiosOption, httpOption);
  }
  static dictUpdateAreaCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('', 'post', params, axiosOption, httpOption);
  }
  static dictReloadAreaCodeCachedData (sender, params, axiosOption, httpOption) {
    return sender.doUrl('', 'get', params, axiosOption, httpOption);
  }
  static dictCourse (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/course/listDictCourse', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase();
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictGrade (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/grade/listDictGrade', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase();
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictAddGrade (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/grade/add', 'post', params, axiosOption, httpOption);
  }
  static dictDeleteGrade (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/grade/delete', 'post', params, axiosOption, httpOption);
  }
  static dictBatchDeleteGrade (sender, params, axiosOption, httpOption) {
    return sender.doUrl('', 'post', params, axiosOption, httpOption);
  }
  static dictUpdateGrade (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/grade/update', 'post', params, axiosOption, httpOption);
  }
  static dictReloadGradeCachedData (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/grade/reloadCachedData', 'get', params, axiosOption, httpOption);
  }
  static dictSchoolInfo (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/schoolInfo/listDictSchoolInfo', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase();
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictStudent (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/student/listDictStudent', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase();
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
}
