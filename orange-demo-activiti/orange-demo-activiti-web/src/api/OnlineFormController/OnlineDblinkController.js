export default class OnlineDblinkController {
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineDblink/list', 'post', params, axiosOption, httpOption);
  }

  static listDblinkTables (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineDblink/listDblinkTables', 'get', params, axiosOption, httpOption);
  }

  static listDblinkTableColumns (sender, params, axiosOption, httpOption) {
    return sender.doUrl('/admin/online/onlineDblink/listDblinkTableColumns', 'get', params, axiosOption, httpOption);
  }
}
