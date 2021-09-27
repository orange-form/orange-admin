export default class OnlineOperation {
  static listDict (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/listDict', 'post', params, axiosOption, httpOption);
  }

  static listByDatasourceId (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/listByDatasourceId', 'post', params, axiosOption, httpOption);
  }

  static listByOneToManyRelationId (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/listByOneToManyRelationId', 'post', params, axiosOption, httpOption);
  }

  static addDatasource (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/addDatasource', 'post', params, axiosOption, httpOption);
  }

  static addOneToManyRelation (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/addOneToManyRelation', 'post', params, axiosOption, httpOption);
  }

  static updateDatasource (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/updateDatasource', 'post', params, axiosOption, httpOption);
  }

  static updateOneToManyRelation (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/updateOneToManyRelation', 'post', params, axiosOption, httpOption);
  }

  static viewByDatasourceId (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/viewByDatasourceId', 'get', params, axiosOption, httpOption);
  }

  static viewByOneToManyRelationId (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/viewByOneToManyRelationId', 'get', params, axiosOption, httpOption);
  }

  static deleteDatasource (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/deleteDatasource', 'post', params, axiosOption, httpOption);
  }

  static deleteOneToManyRelation (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineOperation/deleteOneToManyRelation', 'post', params, axiosOption, httpOption);
  }
}
