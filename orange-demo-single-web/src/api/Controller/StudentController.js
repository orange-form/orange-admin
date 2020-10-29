export default class StudentController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/student/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/student/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/app/student/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/student/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/student/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/student/delete', 'post', params, axiosOption, httpOption);
  }
}
