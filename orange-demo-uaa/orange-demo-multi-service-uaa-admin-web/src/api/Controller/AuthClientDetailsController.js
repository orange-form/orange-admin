export default class AuthClientDetailsController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/authClientDetails/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/authClientDetails/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/uaaadmin/authClientDetails/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/authClientDetails/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/authClientDetails/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/uaaadmin/authClientDetails/delete', 'post', params, axiosOption, httpOption);
  }
}
