export default class OnlineVirtualColumnController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineVirtualColumn/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineVirtualColumn/view', 'get', params, axiosOption, httpOption);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineVirtualColumn/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineVirtualColumn/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineVirtualColumn/delete', 'post', params, axiosOption, httpOption);
  }
}
