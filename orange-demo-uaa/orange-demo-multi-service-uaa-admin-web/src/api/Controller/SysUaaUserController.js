export default class SysUaaUserController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaUser/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaUser/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/uaaadmin/sysUaaUser/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaUser/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaUser/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaUser/delete', 'post', params, axiosOption, httpOption);
  }

  static resetPassword (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/sysUaaUser/resetPassword', 'post', params, axiosOption, httpOption);
  }
}
