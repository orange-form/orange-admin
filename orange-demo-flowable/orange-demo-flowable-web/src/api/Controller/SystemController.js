export default class SystemController {
  static login (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/login/doLogin', 'post', params, axiosOption, httpOption);
  }

  static logout (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/login/doLogout', 'post', params, axiosOption, httpOption);
  }

  static changePassword (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/login/changePassword', 'post', params, axiosOption, httpOption);
  }

  static getLoginInfo (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/login/getLoginInfo', 'get', params, axiosOption, httpOption);
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

  static addDepartment (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/add', 'post', params, axiosOption, httpOption);
  }

  static deleteDepartment (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/delete', 'post', params, axiosOption, httpOption);
  }

  static updateDepartment (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/update', 'post', params, axiosOption, httpOption);
  }

  static getDepartmentList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysDept/list', 'post', params, axiosOption, httpOption);
  }

  // 菜单接口
  static getMenuPermList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/list', 'post', params, axiosOption, httpOption);
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
    return sender.doUrl('admin/upms/sysPermModule/listAll', 'post', params, axiosOption, httpOption);
  }

  static getPermGroupList (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermModule/list', 'post', params, axiosOption, httpOption);
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
  // 权限查询
  static listSysPermWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/listSysPermWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysPermCodeWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/listSysPermCodeWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysMenuWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysUser/listSysMenuWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysPermByRoleIdWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/listSysPermWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysPermCodeByRoleIdWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysRole/listSysPermCodeWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listMenuPermCode (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/listMenuPerm', 'get', params, axiosOption, httpOption);
  }

  static listSysPermByMenuIdWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/listSysPermWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysUserByMenuIdWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysMenu/listSysUserWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysUserByPermCodeIdWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermCode/listSysUserWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysRoleByPermCodeIdWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPermCode/listSysRoleWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysUserByPermIdWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPerm/listSysUserWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysRoleByPermIdWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPerm/listSysRoleWithDetail', 'get', params, axiosOption, httpOption);
  }

  static listSysMenuByPermIdWithDetail (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/sysPerm/listSysMenuWithDetail', 'get', params, axiosOption, httpOption);
  }
  // 在线用户
  static listSysLoginUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/loginUser/list', 'post', params, axiosOption, httpOption);
  }

  static deleteSysLoginUser (sender, params, axiosOption, httpOption) {
    return sender.doUrl('admin/upms/loginUser/delete', 'post', params, axiosOption, httpOption);
  }
}
