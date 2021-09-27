export default class FlowEntryController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/view', 'get', params, axiosOption, httpOption);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/delete', 'post', params, axiosOption, httpOption);
  }

  static publish (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/publish', 'post', params, axiosOption, httpOption);
  }

  static listFlowEntryPublish (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/listFlowEntryPublish', 'get', params, axiosOption, httpOption);
  }

  static updateMainVersion (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/updateMainVersion', 'post', params, axiosOption, httpOption);
  }

  static suspendFlowEntryPublish (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/suspendFlowEntryPublish', 'post', params, axiosOption, httpOption);
  }

  static activateFlowEntryPublish (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/activateFlowEntryPublish', 'post', params, axiosOption, httpOption);
  }

  static viewDict (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/viewDict', 'get', params, axiosOption, httpOption);
  }

  static listDict (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/flow/flowEntry/listDict', 'get', params, axiosOption, httpOption);
  }
}
