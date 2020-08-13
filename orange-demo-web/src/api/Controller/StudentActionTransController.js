export default class StudentActionTransController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/stats/studentActionTrans/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/stats/studentActionTrans/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/stats/studentActionTrans/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/stats/studentActionTrans/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/stats/studentActionTrans/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/stats/studentActionTrans/delete', 'post', params, axiosOption, httpOption);
  }
}
