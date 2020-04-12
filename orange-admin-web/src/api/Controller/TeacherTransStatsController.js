export default class TeacherTransStatsController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/teacherTransStats/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/teacherTransStats/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/app/teacherTransStats/export', params, fileName);
  }

  static listWithGroup (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/app/teacherTransStats/listWithGroup', 'post', params, axiosOption, httpOption);
  }
}
