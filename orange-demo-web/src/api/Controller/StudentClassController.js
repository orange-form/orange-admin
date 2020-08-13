export default class StudentClassController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/CourseClass/studentClass/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/delete', 'post', params, axiosOption, httpOption);
  }

  static listClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/listClassCourse', 'post', params, axiosOption, httpOption);
  }

  static listNotInClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/listNotInClassCourse', 'post', params, axiosOption, httpOption);
  }

  static addClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/addClassCourse', 'post', params, axiosOption, httpOption);
  }

  static updateClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/updateClassCourse', 'post', params, axiosOption, httpOption);
  }

  static deleteClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/deleteClassCourse', 'post', params, axiosOption, httpOption);
  }

  static listClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/listClassStudent', 'post', params, axiosOption, httpOption);
  }

  static listNotInClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/listNotInClassStudent', 'post', params, axiosOption, httpOption);
  }

  static addClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/addClassStudent', 'post', params, axiosOption, httpOption);
  }

  static updateClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/updateClassStudent', 'post', params, axiosOption, httpOption);
  }

  static deleteClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/studentClass/deleteClassStudent', 'post', params, axiosOption, httpOption);
  }
}
