
-- ----------------------------
-- 请仅在下面的数据库链接中执行该脚本。
-- 主数据源 [101.200.178.51:3306/zzdemo-single]
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 部门管理表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_dept`;
CREATE TABLE `zz_sys_dept` (
    `dept_id` bigint(20) NOT NULL COMMENT '部门Id',
    `dept_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '部门名称',
    `show_order` int(11) NOT NULL COMMENT '兄弟部分之间的显示顺序，数字越小越靠前',
    `create_user_id` bigint(20) NOT NULL COMMENT '创建者',
    `create_username` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '创建用户名',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '最后更新时间',
    `deleted_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除标记(1: 正常 -1: 已删除)',
    PRIMARY KEY (`dept_id`) USING BTREE,
    KEY `idx_show_order` (`show_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='部门管理表';

-- ----------------------------
-- 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_user`;
CREATE TABLE `zz_sys_user` (
    `user_id` bigint(20) NOT NULL COMMENT '主键Id',
    `login_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '用户登录名称',
    `password` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
    `show_name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '用户显示名称',
    `dept_id` bigint(20) NOT NULL COMMENT '用户所在部门Id',
    `user_type` int(11) NOT NULL COMMENT '用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)',
    `head_image_url` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户头像的Url',
    `user_status` int(11) NOT NULL COMMENT '状态(0: 正常 1: 锁定)',
    `create_user_id` bigint(20) NOT NULL COMMENT '创建者',
    `create_username` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '创建用户名',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '最后更新时间',
    `deleted_flag` int(11) NOT NULL COMMENT '删除标记(1: 正常 -1: 已删除)',
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE KEY `uk_login_name` (`login_name`) USING BTREE,
    KEY `idx_dept_id` (`dept_id`) USING BTREE,
    KEY `idx_status` (`user_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='系统用户表';

-- ----------------------------
-- 系统角色表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_role`;
CREATE TABLE `zz_sys_role` (
    `role_id` bigint(20) NOT NULL COMMENT '主键Id',
    `role_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
    `create_user_id` bigint(20) NOT NULL COMMENT '创建者',
    `create_username` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '创建用户显示名',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    `deleted_flag` int(11) NOT NULL COMMENT '逻辑删除标记(1: 正常 -1: 已删除)',
    PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='系统角色表';

-- ----------------------------
-- 用户与角色对应关系表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_user_role`;
CREATE TABLE `zz_sys_user_role` (
    `user_id` bigint(20) NOT NULL COMMENT '用户Id',
    `role_id` bigint(20) NOT NULL COMMENT '角色Id',
    PRIMARY KEY (`user_id`,`role_id`) USING BTREE,
    KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='用户与角色对应关系表';

-- ----------------------------
-- 菜单和操作权限管理表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_menu`;
CREATE TABLE `zz_sys_menu` (
    `menu_id` bigint(20) NOT NULL COMMENT '主键Id',
    `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单Id，目录菜单的父菜单为null',
    `menu_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '菜单显示名称',
    `menu_type` int(11) NOT NULL COMMENT '(0: 目录 1: 菜单 2: 按钮 3: UI片段)',
    `form_router_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '前端表单路由名称，仅用于menu_type为1的菜单类型',
    `show_order` int(11) NOT NULL COMMENT '菜单显示顺序 (值越小，排序越靠前)',
    `icon` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单图标',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `deleted_flag` int(11) NOT NULL COMMENT '逻辑删除标记(1: 正常 -1: 已删除)',
    PRIMARY KEY (`menu_id`) USING BTREE,
    KEY `idx_show_order` (`show_order`) USING BTREE,
    KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='菜单和操作权限管理表';

-- ----------------------------
-- 角色与菜单对应关系表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_role_menu`;
CREATE TABLE `zz_sys_role_menu` (
    `role_id` bigint(20) NOT NULL COMMENT '角色Id',
    `menu_id` bigint(20) NOT NULL COMMENT '菜单Id',
    PRIMARY KEY (`role_id`,`menu_id`) USING BTREE,
    KEY `idx_menu_id` (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='角色与菜单对应关系表';

-- ----------------------------
-- 系统权限资源表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_perm_code`;
CREATE TABLE `zz_sys_perm_code` (
    `perm_code_id` bigint(20) NOT NULL COMMENT '主键Id',
    `parent_id` bigint(20) DEFAULT NULL COMMENT '上级权限字Id',
    `perm_code` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '权限字标识(一般为有含义的英文字符串)',
    `perm_code_type` int(11) NOT NULL COMMENT '类型(0: 表单 1: UI片段 2: 操作)',
    `show_name` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '显示名称',
    `show_order` int(11) NOT NULL COMMENT '显示顺序(数值越小，越靠前)',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `deleted_flag` int(11) NOT NULL COMMENT '逻辑删除标记(1: 正常 -1: 已删除)',
    PRIMARY KEY (`perm_code_id`),
    UNIQUE KEY `idx_perm_code` (`perm_code`) USING BTREE,
    KEY `idx_parent_id` (`parent_id`) USING BTREE,
    KEY `idx_show_order` (`show_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='系统权限资源表';

-- ----------------------------
-- 菜单和权限关系表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_menu_perm_code`;
CREATE TABLE `zz_sys_menu_perm_code` (
    `menu_id` bigint(20) NOT NULL COMMENT '关联菜单Id',
    `perm_code_id` bigint(20) NOT NULL COMMENT '关联权限字Id',
    PRIMARY KEY (`menu_id`,`perm_code_id`) USING BTREE,
    KEY `idx_perm_code_id` (`perm_code_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='菜单和权限关系表';

-- ----------------------------
-- 系统权限模块表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_perm_module`;
CREATE TABLE `zz_sys_perm_module` (
    `module_id` bigint(20) NOT NULL COMMENT '权限模块id',
    `parent_id` bigint(20) DEFAULT 0 COMMENT '上级权限模块id',
    `module_name` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '权限模块名称',
    `module_type` int(11) NOT NULL COMMENT '模块类型(0: 普通模块 1: Controller模块)',
    `show_order` int(11) NOT NULL DEFAULT '0' COMMENT '权限模块在当前层级下的顺序，由小到大',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `deleted_flag` int(11) NOT NULL COMMENT '逻辑删除标记(1: 正常 -1: 已删除)',
    PRIMARY KEY (`module_id`) USING BTREE,
    KEY `idx_show_order` (`show_order`) USING BTREE,
    KEY `idx_parent_id` (`parent_id`) USING BTREE,
    KEY `idx_module_type` (`module_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='系统权限模块表';

-- ----------------------------
-- 系统权限表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_perm`;
CREATE TABLE `zz_sys_perm` (
    `perm_id` bigint(20) NOT NULL COMMENT '权限id',
    `module_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '权限所在的权限模块id',
    `perm_name` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '权限名称',
    `url` varchar(128) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '关联的url',
    `show_order` int(11) NOT NULL DEFAULT '0' COMMENT '权限在当前模块下的顺序，由小到大',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `deleted_flag` int(11) NOT NULL COMMENT '逻辑删除标记(1: 正常 -1: 已删除)',
    PRIMARY KEY (`perm_id`) USING BTREE,
    KEY `idx_show_order` (`show_order`) USING BTREE,
    KEY `idx_module_id` (`module_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='系统权限表';

-- ----------------------------
-- 系统权限字和权限资源关联表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_perm_code_perm`;
CREATE TABLE `zz_sys_perm_code_perm` (
    `perm_code_id` bigint(20) NOT NULL COMMENT '权限字Id',
    `perm_id` bigint(20) NOT NULL COMMENT '权限id',
    PRIMARY KEY (`perm_code_id`,`perm_id`),
    KEY `idx_perm_id` (`perm_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='系统权限字和权限资源关联表';

-- ----------------------------
-- 权限资源白名单表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_perm_whitelist`;
CREATE TABLE `zz_sys_perm_whitelist` (
    `perm_url` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '权限资源的url',
    `module_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '权限资源所属模块名字(通常是Controller的名字)',
    `perm_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '权限的名称',
    PRIMARY KEY (`perm_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='权限资源白名单表(认证用户均可访问的url资源)';

-- ----------------------------
-- 数据权限表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_data_perm`;
CREATE TABLE `zz_sys_data_perm` (
    `data_perm_id` bigint(20) NOT NULL COMMENT '主键',
    `data_perm_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '显示名称',
    `rule_type` tinyint(2) NOT NULL COMMENT '数据权限规则类型(0: 全部可见 1: 只看自己 2: 只看本部门 3: 本部门及子部门 4: 多部门及子部门 5: 自定义部门列表)。',
    `dept_id_list_string` varchar(4096) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '部门Id列表(逗号分隔)',
    `create_user_id` bigint(20) NOT NULL COMMENT '创建者',
    `create_username` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '创建用户显示名',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    `deleted_flag` int(11) NOT NULL COMMENT '逻辑删除标记(1: 正常 -1: 已删除)',
    PRIMARY KEY (`data_perm_id`) USING BTREE,
    KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限表';

-- ----------------------------
-- 数据权限和菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_data_perm_menu`;
CREATE TABLE `zz_sys_data_perm_menu` (
    `data_perm_id` bigint(20) NOT NULL COMMENT '数据权限Id',
    `menu_id` bigint(20) NOT NULL COMMENT '关联菜单Id',
    PRIMARY KEY (`data_perm_id`,`menu_id`),
    KEY `idx_menu_id` (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限和部门菜单表';

-- ----------------------------
-- 数据权限和用户关联表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_data_perm_user`;
CREATE TABLE `zz_sys_data_perm_user` (
    `data_perm_id` bigint(20) NOT NULL COMMENT '数据权限Id',
    `user_id` bigint(20) NOT NULL COMMENT '用户Id',
    PRIMARY KEY (`data_perm_id`,`user_id`),
    KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限和用户关联表';

-- ----------------------------
-- 数据权限和部门关联表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_data_perm_dept`;
CREATE TABLE `zz_sys_data_perm_dept` (
    `data_perm_id` bigint(20) NOT NULL COMMENT '数据权限Id',
    `dept_id` bigint(20) NOT NULL COMMENT '部门Id',
    PRIMARY KEY (`data_perm_id`,`dept_id`),
    KEY `idx_dept_id` (`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限和部门关联表';

-- ----------------------------
-- 管理员账号数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_dept` VALUES(1248875520100143104,'公司总部',1,1248875520100143105,'管理员',CURDATE(),CURDATE(),1);
INSERT INTO `zz_sys_user` VALUES(1248875520100143105,'admin','991a762615d6c112c5bb02c9b78a4417','管理员',1248875520100143104,0,'CHANGE TO YOUR HEAD IMAGE URL!!!',0,1248875520100143105,'管理员',CURDATE(),CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部菜单数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_menu` VALUES(1248448487335399424,NULL,'系统管理',0,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394368,1248448487335399424,'用户管理',1,'formSysUser',100,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754512,1248448487486394368,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754513,1248448487486394368,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754514,1248448487486394368,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754515,1248448487486394368,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754516,1248448487486394368,'重置密码',3,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394369,1248448487335399424,'部门管理',1,'formSysDept',105,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754524,1248448487486394369,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754525,1248448487486394369,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754526,1248448487486394369,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754527,1248448487486394369,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394370,1248448487335399424,'角色管理',1,'formSysRole',110,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754534,1248448487486394370,'角色管理',2,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754535,1248448487486394370,'用户授权',2,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754536,1248875520091754534,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754537,1248875520091754534,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754538,1248875520091754534,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754539,1248875520091754534,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754540,1248875520091754535,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754541,1248875520091754535,'授权用户',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754542,1248875520091754535,'移除用户',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394371,1248448487335399424,'数据权限管理',1,'formSysDataPerm',115,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754552,1248448487486394371,'数据权限管理',2,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754553,1248448487486394371,'用户授权',2,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754554,1248875520091754552,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754555,1248875520091754552,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754556,1248875520091754552,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754557,1248875520091754552,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754558,1248875520091754553,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754559,1248875520091754553,'授权用户',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754560,1248875520091754553,'移除用户',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394372,1248448487335399424,'菜单管理',1,'formSysMenu',120,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754570,1248448487486394372,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754571,1248448487486394372,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754572,1248448487486394372,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754573,1248448487486394372,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520091754574,1248448487486394372,'权限列表',3,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394373,1248448487335399424,'权限字管理',1,'formSysPermCode',125,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948801,1248448487486394373,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948802,1248448487486394373,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948803,1248448487486394373,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948804,1248448487486394373,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394374,1248448487335399424,'权限管理',1,'formSysPerm',130,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948811,1248448487486394374,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948812,1248448487486394374,'新增模块',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948813,1248448487486394374,'编辑模块',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948814,1248448487486394374,'删除模块',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948815,1248448487486394374,'新增权限',3,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948816,1248448487486394374,'编辑权限',3,NULL,6,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948817,1248448487486394374,'删除权限',3,NULL,7,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394375,1248448487335399424,'字典管理',1,'formSysDict',135,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948827,1248448487486394375,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948828,1248448487486394375,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948829,1248448487486394375,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948830,1248448487486394375,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948831,1248448487486394375,'同步缓存',3,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248512948561776640,NULL,'业务管理',0,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248512995818999808,1248512948561776640,'老师管理',1,'formTeacher',1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948840,1248512995818999808,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948841,1248512995818999808,'新建',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948842,1248512995818999808,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948843,1248512995818999808,'统计',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248875520095948844,1248512995818999808,'删除',3,NULL,5,NULL,CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部权限字数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_perm_code` VALUES(1248875520087560192,NULL,'formTeacher',0,'老师管理',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520087560193,1248875520087560192,'formTeacher:formTeacher',1,'老师管理',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754496,1248875520087560193,'formTeacher:formTeacher:formCreateTeacher',2,'新建',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754497,1248875520087560193,'formTeacher:formTeacher:formEditTeacher',2,'编辑',10,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754498,1248875520087560193,'formTeacher:formTeacher:formTeacherTransStats',2,'统计',20,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754499,1248875520087560193,'formTeacher:formTeacher:delete',2,'删除',30,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754500,NULL,'formCreateTeacher',0,'新建老师',10,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754501,1248875520091754500,'formCreateTeacher:formCreateTeacher',1,'新建老师',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754502,1248875520091754501,'formCreateTeacher:formCreateTeacher:cancel',2,'取消',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754503,1248875520091754501,'formCreateTeacher:formCreateTeacher:add',2,'保存',10,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754504,NULL,'formEditTeacher',0,'编辑老师',20,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754505,1248875520091754504,'formEditTeacher:formEditTeacher',1,'编辑老师',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754506,1248875520091754505,'formEditTeacher:formEditTeacher:cancel',2,'取消',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754507,1248875520091754505,'formEditTeacher:formEditTeacher:update',2,'保存',10,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754508,NULL,'formTeacherTransStats',0,'老师个人统计',30,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754509,1248875520091754508,'formTeacherTransStats:formTeacherTransStats',1,'老师个人统计',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754517,NULL,'formSysUser',0,'用户管理',10000,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754518,1248875520091754517,'formSysUser:fragmentSysUser',1,'用户管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754519,1248875520091754518,'formSysUser:fragmentSysUser:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754520,1248875520091754518,'formSysUser:fragmentSysUser:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754521,1248875520091754518,'formSysUser:fragmentSysUser:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754522,1248875520091754518,'formSysUser:fragmentSysUser:resetPassword',2,'重置密码',4,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754528,NULL,'formSysDept',0,'部门管理',10100,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754529,1248875520091754528,'formSysDept:fragmentSysDept',1,'部门管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754530,1248875520091754529,'formSysDept:fragmentSysDept:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754531,1248875520091754529,'formSysDept:fragmentSysDept:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754532,1248875520091754529,'formSysDept:fragmentSysDept:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754543,NULL,'formSysRole',0,'角色管理',10200,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754544,1248875520091754543,'formSysRole:fragmentSysRole',1,'角色管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754545,1248875520091754543,'formSysRole:fragmentSysRoleUser',1,'用户授权',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754546,1248875520091754544,'formSysRole:fragmentSysRole:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754547,1248875520091754544,'formSysRole:fragmentSysRole:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754548,1248875520091754544,'formSysRole:fragmentSysRole:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754549,1248875520091754545,'formSysRole:fragmentSysRoleUser:addUserRole',2,'授权用户',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754550,1248875520091754545,'formSysRole:fragmentSysRoleUser:deleteUserRole',2,'移除用户',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754561,NULL,'formSysDataPerm',0,'数据权限管理',10400,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754562,1248875520091754561,'formSysDataPerm:fragmentSysDataPerm',1,'数据权限管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754563,1248875520091754561,'formSysDataPerm:fragmentSysDataPermUser',1,'用户授权',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754564,1248875520091754562,'formSysDataPerm:fragmentSysDataPerm:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754565,1248875520091754562,'formSysDataPerm:fragmentSysDataPerm:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754566,1248875520091754562,'formSysDataPerm:fragmentSysDataPerm:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754567,1248875520091754563,'formSysDataPerm:fragmentSysDataPermUser:addDataPermUser',2,'授权用户',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754568,1248875520091754563,'formSysDataPerm:fragmentSysDataPermUser:deleteDataPermUser',2,'移除用户',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754575,NULL,'formSysMenu',0,'菜单管理',10600,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754576,1248875520091754575,'formSysMenu:fragmentSysMenu',1,'菜单管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754577,1248875520091754576,'formSysMenu:fragmentSysMenu:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754578,1248875520091754576,'formSysMenu:fragmentSysMenu:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754579,1248875520091754576,'formSysMenu:fragmentSysMenu:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520091754580,1248875520091754576,'formSysMenu:fragmentSysMenu:listMenuPerm',2,'权限列表',4,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948805,NULL,'formSysPermCode',0,'权限字管理',10700,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948806,1248875520095948805,'formSysPermCode:fragmentSysPermCode',1,'权限字管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948807,1248875520095948806,'formSysPermCode:fragmentSysPermCode:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948808,1248875520095948806,'formSysPermCode:fragmentSysPermCode:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948809,1248875520095948806,'formSysPermCode:fragmentSysPermCode:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948818,NULL,'formSysPerm',0,'权限管理',10800,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948819,1248875520095948818,'formSysPerm:fragmentSysPerm',1,'权限管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948820,1248875520095948819,'formSysPerm:fragmentSysPerm:addPermModule',2,'新增模块',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948821,1248875520095948819,'formSysPerm:fragmentSysPerm:updatePermModule',2,'编辑模块',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948822,1248875520095948819,'formSysPerm:fragmentSysPerm:deletePermModule',2,'删除模块',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948823,1248875520095948819,'formSysPerm:fragmentSysPerm:addPerm',2,'新增权限',4,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948824,1248875520095948819,'formSysPerm:fragmentSysPerm:updatePerm',2,'编辑权限',5,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948825,1248875520095948819,'formSysPerm:fragmentSysPerm:deletePerm',2,'删除权限',6,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948832,NULL,'formSysDict',0,'字典管理',10900,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948833,1248875520095948832,'formSysDict:fragmentSysDict',1,'字典管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948834,1248875520095948833,'formSysDict:fragmentSysDict:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948835,1248875520095948833,'formSysDict:fragmentSysDict:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948836,1248875520095948833,'formSysDict:fragmentSysDict:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1248875520095948837,1248875520095948833,'formSysDict:fragmentSysDict:reloadCache',2,'同步缓存',4,CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部菜单和权限字关系数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754512,1248875520091754518);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754513,1248875520091754519);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754514,1248875520091754520);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754515,1248875520091754521);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754516,1248875520091754522);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754524,1248875520091754529);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754525,1248875520091754530);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754526,1248875520091754531);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754527,1248875520091754532);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754536,1248875520091754544);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754540,1248875520091754545);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754537,1248875520091754546);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754538,1248875520091754547);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754539,1248875520091754548);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754541,1248875520091754549);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754542,1248875520091754550);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754554,1248875520091754562);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754558,1248875520091754563);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754555,1248875520091754564);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754556,1248875520091754565);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754557,1248875520091754566);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754559,1248875520091754567);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754560,1248875520091754568);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754570,1248875520091754576);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754571,1248875520091754577);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754572,1248875520091754578);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754573,1248875520091754579);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520091754574,1248875520091754580);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948801,1248875520095948806);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948802,1248875520095948807);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948803,1248875520095948808);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948804,1248875520095948809);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948811,1248875520095948819);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948812,1248875520095948820);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948813,1248875520095948821);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948814,1248875520095948822);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948815,1248875520095948823);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948816,1248875520095948824);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948817,1248875520095948825);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1248875520095948833);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1248875520095948834);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1248875520095948835);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1248875520095948836);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1248875520095948837);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948827,1248875520095948833);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948828,1248875520095948834);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948829,1248875520095948835);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948830,1248875520095948836);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948831,1248875520095948837);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948840,1248875520087560193);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948841,1248875520091754496);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948841,1248875520091754500);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948841,1248875520091754501);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948841,1248875520091754502);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948841,1248875520091754503);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948842,1248875520091754497);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948842,1248875520091754504);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948842,1248875520091754505);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948842,1248875520091754506);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948842,1248875520091754507);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948843,1248875520091754498);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948843,1248875520091754508);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948843,1248875520091754509);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248875520095948844,1248875520091754499);
COMMIT;

-- ----------------------------
-- 全部权限资源模块数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_perm_module` VALUES(1248448487645777921,NULL,'缺省分组',0,3,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248448487645777920,NULL,'系统配置',0,2,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248448487087935491,NULL,'用户权限',0,1,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520028839936,1248448487645777921,'部门管理',1,0,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034247,1248448487645777921,'用户管理',1,5,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034256,1248448487645777921,'老师数据源',1,10,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034264,1248448487645777921,'老师流水统计',1,15,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034270,1248448487087935491,'角色管理',1,10,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034280,1248448487087935491,'数据权限管理',1,15,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034290,1248448487087935491,'菜单管理',1,20,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034297,1248448487087935491,'权限字管理',1,25,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034303,1248448487087935491,'权限模块管理',1,30,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034309,1248448487087935491,'权限资源管理',1,35,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034315,1248448487645777920,'字典管理',0,1,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248875520033034316,1248875520033034315,'行政区划',1,1,CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部权限资源数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_perm` VALUES(1248875520033034240,1248875520028839936,'新增','/admin/upms/sysDept/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034241,1248875520028839936,'编辑','/admin/upms/sysDept/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034242,1248875520028839936,'删除','/admin/upms/sysDept/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034243,1248875520028839936,'显示列表','/admin/upms/sysDept/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034244,1248875520028839936,'导出','/admin/upms/sysDept/export',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034245,1248875520028839936,'详情','/admin/upms/sysDept/view',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034246,1248875520028839936,'打印','/admin/upms/sysDept/print',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034248,1248875520033034247,'新增','/admin/upms/sysUser/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034249,1248875520033034247,'编辑','/admin/upms/sysUser/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034250,1248875520033034247,'删除','/admin/upms/sysUser/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034251,1248875520033034247,'显示列表','/admin/upms/sysUser/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034252,1248875520033034247,'导出','/admin/upms/sysUser/export',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034253,1248875520033034247,'详情','/admin/upms/sysUser/view',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034254,1248875520033034247,'打印','/admin/upms/sysUser/print',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034255,1248875520033034247,'重置密码','/admin/upms/sysUser/resetPassword',8,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034257,1248875520033034256,'新增','/admin/app/teacher/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034258,1248875520033034256,'编辑','/admin/app/teacher/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034259,1248875520033034256,'删除','/admin/app/teacher/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034260,1248875520033034256,'显示列表','/admin/app/teacher/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034261,1248875520033034256,'导出','/admin/app/teacher/export',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034262,1248875520033034256,'详情','/admin/app/teacher/view',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034263,1248875520033034256,'打印','/admin/app/teacher/print',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034265,1248875520033034264,'分组列表','/admin/app/teacherTransStats/listWithGroup',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034266,1248875520033034264,'显示列表','/admin/app/teacherTransStats/list',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034267,1248875520033034264,'导出','/admin/app/teacherTransStats/export',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034268,1248875520033034264,'详情','/admin/app/teacherTransStats/view',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034269,1248875520033034264,'打印','/admin/app/teacherTransStats/print',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034271,1248875520033034270,'新增','/admin/upms/sysRole/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034272,1248875520033034270,'编辑','/admin/upms/sysRole/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034273,1248875520033034270,'删除','/admin/upms/sysRole/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034274,1248875520033034270,'显示列表','/admin/upms/sysRole/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034275,1248875520033034270,'详情','/admin/upms/sysRole/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034276,1248875520033034270,'授权用户','/admin/upms/sysRole/addUserRole',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034277,1248875520033034270,'移除用户','/admin/upms/sysRole/deleteUserRole',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034278,1248875520033034270,'角色用户列表','/admin/upms/sysRole/listUserRole',8,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034279,1248875520033034270,'角色未添加用户列表','/admin/upms/sysRole/listNotInUserRole',9,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034281,1248875520033034280,'新增','/admin/upms/sysDataPerm/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034282,1248875520033034280,'编辑','/admin/upms/sysDataPerm/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034283,1248875520033034280,'删除','/admin/upms/sysDataPerm/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034284,1248875520033034280,'显示列表','/admin/upms/sysDataPerm/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034285,1248875520033034280,'详情','/admin/upms/sysDataPerm/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034286,1248875520033034280,'授权用户','/admin/upms/sysDataPerm/addDataPermUser',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034287,1248875520033034280,'移除用户','/admin/upms/sysDataPerm/deleteDataPermUser',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034288,1248875520033034280,'数据权限用户列表','/admin/upms/sysDataPerm/listDataPermUser',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034289,1248875520033034280,'数据权限未添加用户列表','/admin/upms/sysDataPerm/listNotInDataPermUser',8,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034291,1248875520033034290,'新增','/admin/upms/sysMenu/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034292,1248875520033034290,'编辑','/admin/upms/sysMenu/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034293,1248875520033034290,'删除','/admin/upms/sysMenu/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034294,1248875520033034290,'显示列表','/admin/upms/sysMenu/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034295,1248875520033034290,'详情','/admin/upms/sysMenu/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034296,1248875520033034290,'权限资源列表','/admin/upms/sysMenu/listMenuPerm',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034298,1248875520033034297,'新增','/admin/upms/sysPermCode/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034299,1248875520033034297,'编辑','/admin/upms/sysPermCode/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034300,1248875520033034297,'删除','/admin/upms/sysPermCode/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034301,1248875520033034297,'显示列表','/admin/upms/sysPermCode/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034302,1248875520033034297,'详情','/admin/upms/sysPermCode/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034304,1248875520033034303,'新增','/admin/upms/sysPermModule/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034305,1248875520033034303,'编辑','/admin/upms/sysPermModule/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034306,1248875520033034303,'删除','/admin/upms/sysPermModule/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034307,1248875520033034303,'显示列表','/admin/upms/sysPermModule/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034308,1248875520033034303,'显示全部','/admin/upms/sysPermModule/listAll',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034310,1248875520033034309,'新增','/admin/upms/sysPerm/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034311,1248875520033034309,'编辑','/admin/upms/sysPerm/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034312,1248875520033034309,'删除','/admin/upms/sysPerm/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034313,1248875520033034309,'显示列表','/admin/upms/sysPerm/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034314,1248875520033034309,'详情','/admin/upms/sysPerm/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034317,1248875520033034316,'新增','/admin/app/areaCode/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034318,1248875520033034316,'编辑','/admin/app/areaCode/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034319,1248875520033034316,'删除','/admin/app/areaCode/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1248875520033034320,1248875520033034316,'同步缓存','/admin/app/areaCode/reloadCachedData',4,CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部权限字和权限资源关系数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520087560193,1248875520033034260);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754499,1248875520033034259);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754503,1248875520033034257);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754505,1248875520033034262);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754507,1248875520033034258);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754509,1248875520033034265);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754509,1248875520033034266);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754518,1248875520033034251);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754518,1248875520033034252);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754519,1248875520033034248);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754519,1248875520033034243);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754519,1248875520033034284);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754519,1248875520033034274);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754520,1248875520033034253);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754520,1248875520033034249);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754520,1248875520033034254);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754520,1248875520033034243);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754520,1248875520033034284);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754520,1248875520033034274);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754521,1248875520033034250);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754522,1248875520033034255);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754529,1248875520033034243);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754529,1248875520033034244);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754530,1248875520033034240);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754531,1248875520033034245);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754531,1248875520033034241);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754531,1248875520033034246);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754532,1248875520033034242);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754544,1248875520033034274);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754546,1248875520033034271);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754546,1248875520033034294);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754547,1248875520033034275);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754547,1248875520033034272);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754547,1248875520033034294);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754548,1248875520033034273);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754545,1248875520033034278);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754549,1248875520033034276);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754549,1248875520033034279);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754550,1248875520033034277);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754562,1248875520033034284);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754564,1248875520033034281);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754564,1248875520033034294);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754565,1248875520033034285);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754565,1248875520033034282);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754565,1248875520033034294);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754566,1248875520033034283);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754563,1248875520033034288);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754567,1248875520033034286);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754567,1248875520033034289);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754568,1248875520033034287);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754576,1248875520033034294);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754577,1248875520033034291);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754577,1248875520033034301);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754578,1248875520033034295);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754578,1248875520033034292);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754578,1248875520033034301);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754579,1248875520033034293);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520091754580,1248875520033034296);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948806,1248875520033034301);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948807,1248875520033034298);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948807,1248875520033034308);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948808,1248875520033034302);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948808,1248875520033034299);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948808,1248875520033034308);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948809,1248875520033034300);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948819,1248875520033034307);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948819,1248875520033034308);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948819,1248875520033034313);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948820,1248875520033034304);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948821,1248875520033034305);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948822,1248875520033034306);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948823,1248875520033034310);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948824,1248875520033034314);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948824,1248875520033034311);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948825,1248875520033034312);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948833,1248875520033034317);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948833,1248875520033034318);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948833,1248875520033034319);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1248875520095948833,1248875520033034320);
COMMIT;

-- ----------------------------
-- 全部白名单权限资源数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_perm_whitelist` VALUES('/admin/app/areaCode/listDictAreaCode','行政区划','行政区划列表');
INSERT INTO `zz_sys_perm_whitelist` VALUES('/admin/app/areaCode/listDictAreaCodeByParentId','行政区划','行政区划过滤列表');
INSERT INTO `zz_sys_perm_whitelist` VALUES('/admin/upms/sysDept/listDictSysDept','部门管理','校区字典列表');
INSERT INTO `zz_sys_perm_whitelist` VALUES('/admin/upms/sysUser/listDictSysUser','用户管理','用户字典列表');
INSERT INTO `zz_sys_perm_whitelist` VALUES('/admin/app/teacher/listDictTeacher','老师数据源','老师字典列表');
INSERT INTO `zz_sys_perm_whitelist` VALUES('/admin/login/doLogout','登录模块','退出登陆');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;