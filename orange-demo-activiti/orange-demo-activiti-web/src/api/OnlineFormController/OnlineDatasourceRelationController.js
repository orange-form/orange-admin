export default class OnlineDatasourceRelationController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineDatasourceRelation/list', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineDatasourceRelation/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/online/onlineDatasourceRelation/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineDatasourceRelation/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineDatasourceRelation/update', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineDatasourceRelation/delete', 'post', params, axiosOption, httpOption);
  }
}
