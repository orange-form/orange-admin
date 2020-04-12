import * as staticDict from '@/staticDict'

export default class DictionaryController {
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
  static dictGender () {
    return new Promise((resolve) => {
      resolve(staticDict.Gender);
    });
  }
  static dictSubject () {
    return new Promise((resolve) => {
      resolve(staticDict.Subject);
    });
  }
  static dictSysDept (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/upms/sysDept/listDictSysDept', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase();
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictSysUser (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/upms/sysUser/listDictSysUser', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase();
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
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
  static dictTeacher (sender, params, axiosOption, httpOption) {
    return new Promise((resolve, reject) => {
      sender.doUrl('/admin/app/teacher/listDictTeacher', 'get', params, axiosOption, httpOption).then(res => {
        let dictData = new staticDict.DictionaryBase();
        dictData.setList(res.data);
        resolve(dictData);
      }).catch(err => {
        reject(err);
      });
    });
  }
  static dictTeacherLevelType () {
    return new Promise((resolve) => {
      resolve(staticDict.TeacherLevelType);
    });
  }
  static dictYesNo () {
    return new Promise((resolve) => {
      resolve(staticDict.YesNo);
    });
  }
  static dictSysDataPermType () {
    return new Promise((resolve) => {
      resolve(staticDict.SysDataPermType);
    });
  }
}
