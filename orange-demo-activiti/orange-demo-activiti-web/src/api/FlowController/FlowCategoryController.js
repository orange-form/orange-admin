export default class FlowCategoryController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowCategory/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowCategory/view', 'get', params, axiosOption, httpOption);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowCategory/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowCategory/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowCategory/delete', 'post', params, axiosOption, httpOption);
  }
}
