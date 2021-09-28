export default class SchoolInfoController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/schoolInfo/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/schoolInfo/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/CourseClass/schoolInfo/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/schoolInfo/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/schoolInfo/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/CourseClass/schoolInfo/delete', 'post', params, axiosOption, httpOption);
  }
}
