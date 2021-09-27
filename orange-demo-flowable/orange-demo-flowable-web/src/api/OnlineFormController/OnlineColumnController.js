export default class OnlineColumnController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/online/onlineColumn/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/update', 'post', params, axiosOption, httpOption);
  }

  static refreshColumn (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/refresh', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/delete', 'post', params, axiosOption, httpOption);
  }

  static listOnlineColumnRule (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/listOnlineColumnRule', 'post', params, axiosOption, httpOption);
  }

  static listNotInOnlineColumnRule (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/listNotInOnlineColumnRule', 'post', params, axiosOption, httpOption);
  }

  static addOnlineColumnRule (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/addOnlineColumnRule', 'post', params, axiosOption, httpOption);
  }

  static deleteOnlineColumnRule (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/deleteOnlineColumnRule', 'post', params, axiosOption, httpOption);
  }

  static updateOnlineColumnRule (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/updateOnlineColumnRule', 'post', params, axiosOption, httpOption);
  }

  static viewOnlineColumnRule (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineColumn/viewOnlineColumnRule', 'get', params, axiosOption, httpOption);
  }
}
