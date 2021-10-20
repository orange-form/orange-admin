export default class SysPostController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/upms/sysPost/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/upms/sysPost/view', 'get', params, axiosOption, httpOption);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/upms/sysPost/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/upms/sysPost/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/upms/sysPost/delete', 'post', params, axiosOption, httpOption);
  }
}
