export default class SchoolInfoController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/schoolInfo/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/schoolInfo/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/app/schoolInfo/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/schoolInfo/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/schoolInfo/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/schoolInfo/delete', 'post', params, axiosOption, httpOption);
  }
}
