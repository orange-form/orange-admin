export default class StudentClassController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/app/studentClass/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/delete', 'post', params, axiosOption, httpOption);
  }

  static listClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/listClassCourse', 'post', params, axiosOption, httpOption);
  }

  static listNotInClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/listNotInClassCourse', 'post', params, axiosOption, httpOption);
  }

  static addClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/addClassCourse', 'post', params, axiosOption, httpOption);
  }

  static deleteClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/deleteClassCourse', 'post', params, axiosOption, httpOption);
  }

  static updateClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/updateClassCourse', 'post', params, axiosOption, httpOption);
  }

  static viewClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/viewClassCourse', 'get', params, axiosOption, httpOption);
  }

  static listClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/listClassStudent', 'post', params, axiosOption, httpOption);
  }

  static listNotInClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/listNotInClassStudent', 'post', params, axiosOption, httpOption);
  }

  static addClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/addClassStudent', 'post', params, axiosOption, httpOption);
  }

  static deleteClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentClass/deleteClassStudent', 'post', params, axiosOption, httpOption);
  }
}
