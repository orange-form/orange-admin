export default class OnlineFormController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineForm/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineForm/view', 'get', params, axiosOption, httpOption);
  }

  static render (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineForm/render', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/online/onlineForm/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineForm/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineForm/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineForm/delete', 'post', params, axiosOption, httpOption);
  }
}
