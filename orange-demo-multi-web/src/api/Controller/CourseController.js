export default class CourseController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/course/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/course/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/CourseClass/course/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/course/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/course/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/course/delete', 'post', params, axiosOption, httpOption);
  }
}
