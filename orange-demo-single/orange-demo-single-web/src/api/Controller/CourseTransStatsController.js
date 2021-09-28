export default class CourseTransStatsController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/courseTransStats/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/courseTransStats/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/app/courseTransStats/export', params, fileName);
  }

  static listWithGroup (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/courseTransStats/listWithGroup', 'post', params, axiosOption, httpOption);
  }
}
