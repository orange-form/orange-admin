export default class SystemController {
  static login (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/login/doLogin', 'post', params, axiosOption, httpOption);
  }

  static logout (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/login/doLogout', 'post', params, axiosOption, httpOption);
  }

  static changePassword (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/login/changePassword', 'post', params, axiosOption, httpOption);
  }
}
