export default class CourseTransStatsController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/stats/courseTransStats/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/stats/courseTransStats/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/stats/courseTransStats/export', params, fileName);
  }

  static listWithGroup (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/stats/courseTransStats/listWithGroup', 'post', params, axiosOption, httpOption);
  }
}
