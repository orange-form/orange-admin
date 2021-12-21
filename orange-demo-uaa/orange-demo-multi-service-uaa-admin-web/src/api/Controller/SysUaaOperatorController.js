export default class SysUaaOperatorController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaOperator/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaOperator/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/uaaadmin/sysUaaOperator/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaOperator/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaOperator/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaOperator/delete', 'post', params, axiosOption, httpOption);
  }

  static resetPassword (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaOperator/resetPassword', 'post', params, axiosOption, httpOption);
  }
}
