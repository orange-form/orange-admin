export default class OnlinePageController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/list', 'post', params, axiosOption, httpOption);
  }

  static listAllPageAndForm (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/listAllPageAndForm', 'post', params, axiosOption, httpOption);
  }

  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/view', 'get', params, axiosOption, httpOption);
  }

  static export (sender, params, fileName) {
    return sender.download('/admin/online/onlinePage/export', params, fileName);
  }

  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/add', 'post', params, axiosOption, httpOption);
  }

  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/update', 'post', params, axiosOption, httpOption);
  }

  static updatePublished (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/updatePublished', 'post', params, axiosOption, httpOption);
  }

  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/delete', 'post', params, axiosOption, httpOption);
  }

  static updateStatus (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/updateStatus', 'post', params, axiosOption, httpOption);
  }

  static listOnlinePageDatasource (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/listOnlinePageDatasource', 'post', params, axiosOption, httpOption);
  }

  static listNotInOnlinePageDatasource (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/listNotInOnlinePageDatasource', 'post', params, axiosOption, httpOption);
  }

  static addOnlinePageDatasource (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/addOnlinePageDatasource', 'post', params, axiosOption, httpOption);
  }

  static deleteOnlinePageDatasource (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/deleteOnlinePageDatasource', 'post', params, axiosOption, httpOption);
  }

  static updateOnlinePageDatasource (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/updateOnlinePageDatasource', 'post', params, axiosOption, httpOption);
  }

  static viewOnlinePageDatasource (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlinePage/viewOnlinePageDatasource', 'get', params, axiosOption, httpOption);
  }
}
