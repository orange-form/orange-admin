export default class SysDeptController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('admin/upms/sysDept/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/delete', 'post', params, axiosOption, httpOption);
  }

  static listNotInSysDeptPost (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/listNotInSysDeptPost', 'post', params, axiosOption, httpOption);
  }

  static listSysDeptPost (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/listSysDeptPost', 'post', params, axiosOption, httpOption);
  }

  static addSysDeptPost (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/addSysDeptPost', 'post', params, axiosOption, httpOption);
  }

  static updateSysDeptPost (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/updateSysDeptPost', 'post', params, axiosOption, httpOption);
  }

  static deleteSysDeptPost (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/deleteSysDeptPost', 'post', params, axiosOption, httpOption);
  }

  static viewSysDeptPost (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/viewSysDeptPost', 'get', params, axiosOption, httpOption);
  }
}
