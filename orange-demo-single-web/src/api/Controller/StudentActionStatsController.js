export default class StudentActionStatsController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentActionStats/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentActionStats/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/app/studentActionStats/export', params, fileName);
  }

  static listWithGroup (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/studentActionStats/listWithGroup', 'post', params, axiosOption, httpOption);
  }
}
