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
      sender.doUrl('/admin/app/areaCode/listDict', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase('行政区划');
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictAreaCodeByParentId (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/areaCode/listDictByParentId', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase('行政区划');
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
  static dictUpdateAreaCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('', 'post', params, axiosOption, httpOption);
  }
  static dictReloadAreaCodeCachedData (sender, params, axiosOption, httpOption) {
    return sender.doUrl('', 'get', params, axiosOption, httpOption);
  }
  static dictCourse (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/course/listDict', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase('课程');
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictGrade (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/grade/listDict', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase('年级');
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictGradeAll (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/grade/listAll', 'get', params, axiosOption, httpOption).then(res => {
        resolve(res.data);
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
  static dictUpdateGrade (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/grade/update', 'post', params, axiosOption, httpOption);
  }
  static dictReloadGradeCachedData (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/grade/reloadCachedData', 'get', params, axiosOption, httpOption);
  }
  static dictStudent (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/student/listDict', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase('学生');
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictSysDept (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/upms/sysDept/listDict', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase('部门字典');
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictSysDeptByParentId (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/upms/sysDept/listDictByParentId', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase('部门字典');
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictSysDataPermType () {
    return new Promise((resolve) => {
      resolve(staticDict.SysDataPermType);
    });
  }
}
