export default class CourseController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/course/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/course/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/app/course/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/course/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/course/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/course/delete', 'post', params, axiosOption, httpOption);
  }
}
