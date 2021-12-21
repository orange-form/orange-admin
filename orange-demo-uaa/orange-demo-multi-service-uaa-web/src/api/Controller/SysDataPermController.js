export default class SysDataPermController {
  /**
   * @param params    {dataPermId, dataPermName, deptIdListString}
   */
  static add (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDataPerm/add', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params    {dataPermId, dataPermName, deptIdListString}
   */
  static update (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDataPerm/update', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params    {dataPermId}
   */
  static delete (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDataPerm/delete', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params    {dataPermName}
   */
  static list (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDataPerm/list', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params    {dataPermId}
   */
  static view (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDataPerm/view', 'get', params, axiosOption, httpOption);
  }

  /**
   * @param params    {dataPermId, searchString}
   */
  static listDataPermUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDataPerm/listDataPermUser', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params    {dataPermId, userIdListString}
   */
  static addDataPermUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDataPerm/addDataPermUser', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params    {dataPermId, userId}
   */
  static deleteDataPermUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDataPerm/deleteDataPermUser', 'post', params, axiosOption, httpOption);
  }

  static listNotInDataPermUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDataPerm/listNotInDataPermUser', 'post', params, axiosOption, httpOption);
  }
}
