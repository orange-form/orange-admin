export default class SystemController {
  static login (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/login/doLogin', 'get', params, axiosOption, httpOption);
  }

  static logout (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/login/doLogout', 'post', params, axiosOption, httpOption);
  }

  static changePassword (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/login/changePassword', 'post', params, axiosOption, httpOption);
  }

  static getDictList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDict/list', 'post', params, axiosOption, httpOption);
  }

  static getRoleList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/list', 'post', params, axiosOption, httpOption);
  }

  static getRole (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/view', 'get', params, axiosOption, httpOption);
  }

  static deleteRole (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/delete', 'post', params, axiosOption, httpOption);
  }

  static addRole (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/add', 'post', params, axiosOption, httpOption);
  }

  static updateRole (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/update', 'post', params, axiosOption, httpOption);
  }

  static getUserList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/list', 'post', params, axiosOption, httpOption);
  }

  static getUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/view', 'get', params, axiosOption, httpOption);
  }

  static resetUserPassword (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/resetPassword', 'post', params, axiosOption, httpOption);
  }

  static deleteUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/delete', 'post', params, axiosOption, httpOption);
  }

  static addUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/add', 'post', params, axiosOption, httpOption);
  }

  static updateUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/update', 'post', params, axiosOption, httpOption);
  }

  // 菜单接口
  static getMenuPermList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/list', 'get', params, axiosOption, httpOption);
  }

  static addMenu (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/add', 'post', params, axiosOption, httpOption);
  }

  static updateMenu (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/update', 'post', params, axiosOption, httpOption);
  }

  static deleteMenu (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/delete', 'post', params, axiosOption, httpOption);
  }

  static viewMenu (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/view', 'get', params, axiosOption, httpOption);
  }

  static listMenuPerm (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/listMenuPerm', 'get', params, axiosOption, httpOption);
  }

  // 权限字接口
  static getPermCodeList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermCode/list', 'post', params, axiosOption, httpOption);
  }

  static addPermCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermCode/add', 'post', params, axiosOption, httpOption);
  }

  static updatePermCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermCode/update', 'post', params, axiosOption, httpOption);
  }

  static deletePermCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermCode/delete', 'post', params, axiosOption, httpOption);
  }

  static viewPermCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermCode/view', 'get', params, axiosOption, httpOption);
  }

  // 权限资源接口
  static getAllPermList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermModule/listAll', 'get', params, axiosOption, httpOption);
  }

  static getPermGroupList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermModule/list', 'get', params, axiosOption, httpOption);
  }

  static addPermGroup (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermModule/add', 'post', params, axiosOption, httpOption);
  }

  static updatePermGroup (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermModule/update', 'post', params, axiosOption, httpOption);
  }

  static deletePermGroup (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermModule/delete', 'post', params, axiosOption, httpOption);
  }

  static getPermList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPerm/list', 'post', params, axiosOption, httpOption);
  }

  static viewPerm (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPerm/view', 'get', params, axiosOption, httpOption);
  }

  static addPerm (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPerm/add', 'post', params, axiosOption, httpOption);
  }

  static updatePerm (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPerm/update', 'post', params, axiosOption, httpOption);
  }

  static deletePerm (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPerm/delete', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params    {roleId, searchString}
   */
  static listRoleUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/listUserRole', 'post', params, axiosOption, httpOption);
  }

  static listNotInUserRole (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/listNotInUserRole', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params    {roleId, userIdListString}
   */
  static addRoleUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/addUserRole', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params    {roleId, userId}
   */
  static deleteRoleUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/deleteUserRole', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params {}
   */
  static queryRoleByPermCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/listAllRolesByPermCode', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params {}
   */
  static queryRoleByURL (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/listAllRolesByPerm', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params {}
   */
  static queryPerm (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/listAllPerms', 'post', params, axiosOption, httpOption);
  }

  /**
   * @param params {}
   */
  static queryPermCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/deleteUserRole', 'post', params, axiosOption, httpOption);
  }
}
