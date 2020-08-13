export default class ClassController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/CourseClass/class/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/delete', 'post', params, axiosOption, httpOption);
  }

  static listClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/listClassCourse', 'post', params, axiosOption, httpOption);
  }

  static listNotInClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/listNotInClassCourse', 'post', params, axiosOption, httpOption);
  }

  static addClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/addClassCourse', 'post', params, axiosOption, httpOption);
  }

  static updateClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/updateClassCourse', 'post', params, axiosOption, httpOption);
  }

  static deleteClassCourse (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/deleteClassCourse', 'post', params, axiosOption, httpOption);
  }

  static listClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/listClassStudent', 'post', params, axiosOption, httpOption);
  }

  static listNotInClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/listNotInClassStudent', 'post', params, axiosOption, httpOption);
  }

  static addClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/addClassStudent', 'post', params, axiosOption, httpOption);
  }

  static updateClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/updateClassStudent', 'post', params, axiosOption, httpOption);
  }

  static deleteClassStudent (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/class/deleteClassStudent', 'post', params, axiosOption, httpOption);
  }
}
