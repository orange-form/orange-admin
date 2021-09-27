export default class FlowEntryVariableController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntryVariable/list', 'post', params, axiosOption, httpOption);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntryVariable/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntryVariable/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntryVariable/delete', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntryVariable/view', 'get', params, axiosOption, httpOption);
  }
}
