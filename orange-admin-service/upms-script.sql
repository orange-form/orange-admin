
-- ----------------------------
-- 请仅在下面的数据库链接中执行该脚本。
-- 主数据源 [localhost:3306/zz-orange-admin]
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
INSERT INTO `zz_sys_dept` VALUES(1264533563043221504,'公司总部',1,1264533563043221505,'管理员',CURDATE(),CURDATE(),1);
INSERT INTO `zz_sys_user` VALUES(1264533563043221505,'admin','991a762615d6c112c5bb02c9b78a4417','管理员',1264533563043221504,0,'CHANGE TO YOUR HEAD IMAGE URL!!!',0,1264533563043221505,'管理员',CURDATE(),CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部菜单数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_menu` VALUES(1248448487335399424,NULL,'系统管理',0,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394368,1248448487335399424,'用户管理',1,'formSysUser',100,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563034832914,1248448487486394368,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563034832915,1248448487486394368,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563034832916,1248448487486394368,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563034832917,1248448487486394368,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563034832918,1248448487486394368,'重置密码',3,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394369,1248448487335399424,'部门管理',1,'formSysDept',105,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027201,1248448487486394369,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027202,1248448487486394369,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027203,1248448487486394369,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027204,1248448487486394369,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394370,1248448487335399424,'角色管理',1,'formSysRole',110,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027211,1248448487486394370,'角色管理',2,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027212,1248448487486394370,'用户授权',2,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027213,1264533563039027211,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027214,1264533563039027211,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027215,1264533563039027211,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027216,1264533563039027211,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027217,1264533563039027212,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027218,1264533563039027212,'授权用户',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027219,1264533563039027212,'移除用户',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394371,1248448487335399424,'数据权限管理',1,'formSysDataPerm',115,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027229,1248448487486394371,'数据权限管理',2,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027230,1248448487486394371,'用户授权',2,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027231,1264533563039027229,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027232,1264533563039027229,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027233,1264533563039027229,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027234,1264533563039027229,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027235,1264533563039027230,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027236,1264533563039027230,'授权用户',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027237,1264533563039027230,'移除用户',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394372,1248448487335399424,'菜单管理',1,'formSysMenu',120,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027247,1248448487486394372,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027248,1248448487486394372,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027249,1248448487486394372,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027250,1248448487486394372,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027251,1248448487486394372,'权限列表',3,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394373,1248448487335399424,'权限字管理',1,'formSysPermCode',125,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027259,1248448487486394373,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027260,1248448487486394373,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027261,1248448487486394373,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027262,1248448487486394373,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394374,1248448487335399424,'权限管理',1,'formSysPerm',130,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027269,1248448487486394374,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027270,1248448487486394374,'新增模块',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027271,1248448487486394374,'编辑模块',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027272,1248448487486394374,'删除模块',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027273,1248448487486394374,'新增权限',3,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027274,1248448487486394374,'编辑权限',3,NULL,6,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027275,1248448487486394374,'删除权限',3,NULL,7,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248448487486394375,1248448487335399424,'字典管理',1,'formSysDict',135,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027285,1248448487486394375,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027286,1248448487486394375,'新增',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027287,1248448487486394375,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027288,1248448487486394375,'删除',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027289,1248448487486394375,'同步缓存',3,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248512948561776640,NULL,'业务管理',0,NULL,5,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1248512995818999808,1248512948561776640,'老师管理',1,'formTeacher',1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027298,1248512995818999808,'显示',3,NULL,1,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027299,1248512995818999808,'新建',3,NULL,2,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027300,1248512995818999808,'编辑',3,NULL,3,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027301,1248512995818999808,'统计',3,NULL,4,NULL,CURDATE(),1);
INSERT INTO `zz_sys_menu` VALUES(1264533563039027302,1248512995818999808,'删除',3,NULL,5,NULL,CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部权限字数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832896,NULL,'formTeacher',0,'老师管理',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832897,1264533563034832896,'formTeacher:formTeacher',1,'老师管理',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832898,1264533563034832897,'formTeacher:formTeacher:formCreateTeacher',2,'新建',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832899,1264533563034832897,'formTeacher:formTeacher:formEditTeacher',2,'编辑',10,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832900,1264533563034832897,'formTeacher:formTeacher:formTeacherTransStats',2,'统计',20,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832901,1264533563034832897,'formTeacher:formTeacher:delete',2,'删除',30,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832902,NULL,'formCreateTeacher',0,'新建老师',10,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832903,1264533563034832902,'formCreateTeacher:formCreateTeacher',1,'新建老师',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832904,1264533563034832903,'formCreateTeacher:formCreateTeacher:cancel',2,'取消',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832905,1264533563034832903,'formCreateTeacher:formCreateTeacher:add',2,'保存',10,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832906,NULL,'formEditTeacher',0,'编辑老师',20,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832907,1264533563034832906,'formEditTeacher:formEditTeacher',1,'编辑老师',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832908,1264533563034832907,'formEditTeacher:formEditTeacher:cancel',2,'取消',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832909,1264533563034832907,'formEditTeacher:formEditTeacher:update',2,'保存',10,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832910,NULL,'formTeacherTransStats',0,'老师个人统计',30,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832911,1264533563034832910,'formTeacherTransStats:formTeacherTransStats',1,'老师个人统计',0,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832919,NULL,'formSysUser',0,'用户管理',10000,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832920,1264533563034832919,'formSysUser:fragmentSysUser',1,'用户管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832921,1264533563034832920,'formSysUser:fragmentSysUser:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832922,1264533563034832920,'formSysUser:fragmentSysUser:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832923,1264533563034832920,'formSysUser:fragmentSysUser:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563034832924,1264533563034832920,'formSysUser:fragmentSysUser:resetPassword',2,'重置密码',4,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027205,NULL,'formSysDept',0,'部门管理',10100,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027206,1264533563039027205,'formSysDept:fragmentSysDept',1,'部门管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027207,1264533563039027206,'formSysDept:fragmentSysDept:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027208,1264533563039027206,'formSysDept:fragmentSysDept:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027209,1264533563039027206,'formSysDept:fragmentSysDept:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027220,NULL,'formSysRole',0,'角色管理',10200,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027221,1264533563039027220,'formSysRole:fragmentSysRole',1,'角色管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027222,1264533563039027220,'formSysRole:fragmentSysRoleUser',1,'用户授权',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027223,1264533563039027221,'formSysRole:fragmentSysRole:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027224,1264533563039027221,'formSysRole:fragmentSysRole:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027225,1264533563039027221,'formSysRole:fragmentSysRole:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027226,1264533563039027222,'formSysRole:fragmentSysRoleUser:addUserRole',2,'授权用户',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027227,1264533563039027222,'formSysRole:fragmentSysRoleUser:deleteUserRole',2,'移除用户',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027238,NULL,'formSysDataPerm',0,'数据权限管理',10400,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027239,1264533563039027238,'formSysDataPerm:fragmentSysDataPerm',1,'数据权限管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027240,1264533563039027238,'formSysDataPerm:fragmentSysDataPermUser',1,'用户授权',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027241,1264533563039027239,'formSysDataPerm:fragmentSysDataPerm:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027242,1264533563039027239,'formSysDataPerm:fragmentSysDataPerm:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027243,1264533563039027239,'formSysDataPerm:fragmentSysDataPerm:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027244,1264533563039027240,'formSysDataPerm:fragmentSysDataPermUser:addDataPermUser',2,'授权用户',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027245,1264533563039027240,'formSysDataPerm:fragmentSysDataPermUser:deleteDataPermUser',2,'移除用户',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027252,NULL,'formSysMenu',0,'菜单管理',10600,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027253,1264533563039027252,'formSysMenu:fragmentSysMenu',1,'菜单管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027254,1264533563039027253,'formSysMenu:fragmentSysMenu:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027255,1264533563039027253,'formSysMenu:fragmentSysMenu:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027256,1264533563039027253,'formSysMenu:fragmentSysMenu:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027257,1264533563039027253,'formSysMenu:fragmentSysMenu:listMenuPerm',2,'权限列表',4,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027263,NULL,'formSysPermCode',0,'权限字管理',10700,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027264,1264533563039027263,'formSysPermCode:fragmentSysPermCode',1,'权限字管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027265,1264533563039027264,'formSysPermCode:fragmentSysPermCode:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027266,1264533563039027264,'formSysPermCode:fragmentSysPermCode:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027267,1264533563039027264,'formSysPermCode:fragmentSysPermCode:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027276,NULL,'formSysPerm',0,'权限管理',10800,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027277,1264533563039027276,'formSysPerm:fragmentSysPerm',1,'权限管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027278,1264533563039027277,'formSysPerm:fragmentSysPerm:addPermModule',2,'新增模块',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027279,1264533563039027277,'formSysPerm:fragmentSysPerm:updatePermModule',2,'编辑模块',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027280,1264533563039027277,'formSysPerm:fragmentSysPerm:deletePermModule',2,'删除模块',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027281,1264533563039027277,'formSysPerm:fragmentSysPerm:addPerm',2,'新增权限',4,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027282,1264533563039027277,'formSysPerm:fragmentSysPerm:updatePerm',2,'编辑权限',5,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027283,1264533563039027277,'formSysPerm:fragmentSysPerm:deletePerm',2,'删除权限',6,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027290,NULL,'formSysDict',0,'字典管理',10900,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027291,1264533563039027290,'formSysDict:fragmentSysDict',1,'字典管理',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027292,1264533563039027291,'formSysDict:fragmentSysDict:add',2,'新增',1,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027293,1264533563039027291,'formSysDict:fragmentSysDict:update',2,'编辑',2,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027294,1264533563039027291,'formSysDict:fragmentSysDict:delete',2,'删除',3,CURDATE(),1);
INSERT INTO `zz_sys_perm_code` VALUES(1264533563039027295,1264533563039027291,'formSysDict:fragmentSysDict:reloadCache',2,'同步缓存',4,CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部菜单和权限字关系数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563034832914,1264533563034832920);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563034832915,1264533563034832921);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563034832916,1264533563034832922);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563034832917,1264533563034832923);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563034832918,1264533563034832924);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027201,1264533563039027206);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027202,1264533563039027207);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027203,1264533563039027208);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027204,1264533563039027209);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027213,1264533563039027221);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027217,1264533563039027222);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027214,1264533563039027223);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027215,1264533563039027224);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027216,1264533563039027225);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027218,1264533563039027226);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027219,1264533563039027227);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027231,1264533563039027239);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027235,1264533563039027240);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027232,1264533563039027241);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027233,1264533563039027242);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027234,1264533563039027243);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027236,1264533563039027244);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027237,1264533563039027245);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027247,1264533563039027253);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027248,1264533563039027254);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027249,1264533563039027255);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027250,1264533563039027256);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027251,1264533563039027257);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027259,1264533563039027264);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027260,1264533563039027265);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027261,1264533563039027266);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027262,1264533563039027267);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027269,1264533563039027277);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027270,1264533563039027278);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027271,1264533563039027279);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027272,1264533563039027280);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027273,1264533563039027281);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027274,1264533563039027282);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027275,1264533563039027283);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1264533563039027291);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1264533563039027292);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1264533563039027293);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1264533563039027294);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1248448487486394375,1264533563039027295);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027285,1264533563039027291);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027286,1264533563039027292);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027287,1264533563039027293);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027288,1264533563039027294);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027289,1264533563039027295);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027298,1264533563034832897);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027299,1264533563034832898);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027299,1264533563034832902);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027299,1264533563034832903);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027299,1264533563034832904);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027299,1264533563034832905);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027300,1264533563034832899);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027300,1264533563034832906);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027300,1264533563034832907);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027300,1264533563034832908);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027300,1264533563034832909);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027301,1264533563034832900);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027301,1264533563034832910);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027301,1264533563034832911);
INSERT INTO `zz_sys_menu_perm_code` VALUES(1264533563039027302,1264533563034832901);
COMMIT;

-- ----------------------------
-- 全部权限资源模块数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_perm_module` VALUES(1248448487645777921,NULL,'缺省分组',0,3,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248448487645777920,NULL,'系统配置',0,2,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1248448487087935491,NULL,'用户权限',0,1,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562980306944,1248448487645777921,'部门管理',1,0,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501255,1248448487645777921,'用户管理',1,5,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501264,1248448487645777921,'老师数据源',1,10,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501272,1248448487645777921,'老师流水统计',1,15,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501278,1248448487087935491,'角色管理',1,10,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501288,1248448487087935491,'数据权限管理',1,15,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501298,1248448487087935491,'菜单管理',1,20,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501305,1248448487087935491,'权限字管理',1,25,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501311,1248448487087935491,'权限模块管理',1,30,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501317,1248448487087935491,'权限资源管理',1,35,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501323,1248448487645777920,'字典管理',0,1,CURDATE(),1);
INSERT INTO `zz_sys_perm_module` VALUES(1264533562984501324,1264533562984501323,'行政区划',1,1,CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部权限资源数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_perm` VALUES(1264533562984501248,1264533562980306944,'新增','/admin/upms/sysDept/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501249,1264533562980306944,'编辑','/admin/upms/sysDept/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501250,1264533562980306944,'删除','/admin/upms/sysDept/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501251,1264533562980306944,'显示列表','/admin/upms/sysDept/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501252,1264533562980306944,'导出','/admin/upms/sysDept/export',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501253,1264533562980306944,'详情','/admin/upms/sysDept/view',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501254,1264533562980306944,'打印','/admin/upms/sysDept/print',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501256,1264533562984501255,'新增','/admin/upms/sysUser/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501257,1264533562984501255,'编辑','/admin/upms/sysUser/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501258,1264533562984501255,'删除','/admin/upms/sysUser/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501259,1264533562984501255,'显示列表','/admin/upms/sysUser/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501260,1264533562984501255,'导出','/admin/upms/sysUser/export',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501261,1264533562984501255,'详情','/admin/upms/sysUser/view',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501262,1264533562984501255,'打印','/admin/upms/sysUser/print',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501263,1264533562984501255,'重置密码','/admin/upms/sysUser/resetPassword',8,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501265,1264533562984501264,'新增','/admin/app/teacher/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501266,1264533562984501264,'编辑','/admin/app/teacher/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501267,1264533562984501264,'删除','/admin/app/teacher/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501268,1264533562984501264,'显示列表','/admin/app/teacher/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501269,1264533562984501264,'导出','/admin/app/teacher/export',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501270,1264533562984501264,'详情','/admin/app/teacher/view',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501271,1264533562984501264,'打印','/admin/app/teacher/print',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501273,1264533562984501272,'分组列表','/admin/app/teacherTransStats/listWithGroup',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501274,1264533562984501272,'显示列表','/admin/app/teacherTransStats/list',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501275,1264533562984501272,'导出','/admin/app/teacherTransStats/export',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501276,1264533562984501272,'详情','/admin/app/teacherTransStats/view',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501277,1264533562984501272,'打印','/admin/app/teacherTransStats/print',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501279,1264533562984501278,'新增','/admin/upms/sysRole/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501280,1264533562984501278,'编辑','/admin/upms/sysRole/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501281,1264533562984501278,'删除','/admin/upms/sysRole/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501282,1264533562984501278,'显示列表','/admin/upms/sysRole/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501283,1264533562984501278,'详情','/admin/upms/sysRole/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501284,1264533562984501278,'授权用户','/admin/upms/sysRole/addUserRole',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501285,1264533562984501278,'移除用户','/admin/upms/sysRole/deleteUserRole',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501286,1264533562984501278,'角色用户列表','/admin/upms/sysRole/listUserRole',8,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501287,1264533562984501278,'角色未添加用户列表','/admin/upms/sysRole/listNotInUserRole',9,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501289,1264533562984501288,'新增','/admin/upms/sysDataPerm/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501290,1264533562984501288,'编辑','/admin/upms/sysDataPerm/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501291,1264533562984501288,'删除','/admin/upms/sysDataPerm/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501292,1264533562984501288,'显示列表','/admin/upms/sysDataPerm/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501293,1264533562984501288,'详情','/admin/upms/sysDataPerm/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501294,1264533562984501288,'授权用户','/admin/upms/sysDataPerm/addDataPermUser',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501295,1264533562984501288,'移除用户','/admin/upms/sysDataPerm/deleteDataPermUser',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501296,1264533562984501288,'数据权限用户列表','/admin/upms/sysDataPerm/listDataPermUser',7,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501297,1264533562984501288,'数据权限未添加用户列表','/admin/upms/sysDataPerm/listNotInDataPermUser',8,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501299,1264533562984501298,'新增','/admin/upms/sysMenu/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501300,1264533562984501298,'编辑','/admin/upms/sysMenu/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501301,1264533562984501298,'删除','/admin/upms/sysMenu/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501302,1264533562984501298,'显示列表','/admin/upms/sysMenu/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501303,1264533562984501298,'详情','/admin/upms/sysMenu/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501304,1264533562984501298,'权限资源列表','/admin/upms/sysMenu/listMenuPerm',6,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501306,1264533562984501305,'新增','/admin/upms/sysPermCode/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501307,1264533562984501305,'编辑','/admin/upms/sysPermCode/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501308,1264533562984501305,'删除','/admin/upms/sysPermCode/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501309,1264533562984501305,'显示列表','/admin/upms/sysPermCode/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501310,1264533562984501305,'详情','/admin/upms/sysPermCode/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501312,1264533562984501311,'新增','/admin/upms/sysPermModule/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501313,1264533562984501311,'编辑','/admin/upms/sysPermModule/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501314,1264533562984501311,'删除','/admin/upms/sysPermModule/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501315,1264533562984501311,'显示列表','/admin/upms/sysPermModule/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501316,1264533562984501311,'显示全部','/admin/upms/sysPermModule/listAll',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501318,1264533562984501317,'新增','/admin/upms/sysPerm/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501319,1264533562984501317,'编辑','/admin/upms/sysPerm/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501320,1264533562984501317,'删除','/admin/upms/sysPerm/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501321,1264533562984501317,'显示列表','/admin/upms/sysPerm/list',4,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501322,1264533562984501317,'详情','/admin/upms/sysPerm/view',5,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501325,1264533562984501324,'新增','/admin/app/areaCode/add',1,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501326,1264533562984501324,'编辑','/admin/app/areaCode/update',2,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501327,1264533562984501324,'删除','/admin/app/areaCode/delete',3,CURDATE(),1);
INSERT INTO `zz_sys_perm` VALUES(1264533562984501328,1264533562984501324,'同步缓存','/admin/app/areaCode/reloadCachedData',4,CURDATE(),1);
COMMIT;

-- ----------------------------
-- 全部权限字和权限资源关系数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832897,1264533562984501268);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832901,1264533562984501267);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832905,1264533562984501265);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832907,1264533562984501270);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832909,1264533562984501266);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832911,1264533562984501273);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832911,1264533562984501274);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832920,1264533562984501259);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832920,1264533562984501260);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832921,1264533562984501256);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832921,1264533562984501251);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832921,1264533562984501292);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832921,1264533562984501282);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832922,1264533562984501261);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832922,1264533562984501257);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832922,1264533562984501262);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832922,1264533562984501251);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832922,1264533562984501292);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832922,1264533562984501282);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832923,1264533562984501258);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563034832924,1264533562984501263);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027206,1264533562984501251);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027206,1264533562984501252);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027207,1264533562984501248);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027208,1264533562984501253);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027208,1264533562984501249);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027208,1264533562984501254);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027209,1264533562984501250);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027221,1264533562984501282);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027223,1264533562984501279);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027223,1264533562984501302);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027224,1264533562984501283);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027224,1264533562984501280);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027224,1264533562984501302);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027225,1264533562984501281);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027222,1264533562984501286);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027226,1264533562984501284);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027226,1264533562984501287);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027227,1264533562984501285);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027239,1264533562984501292);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027241,1264533562984501289);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027241,1264533562984501302);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027242,1264533562984501293);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027242,1264533562984501290);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027242,1264533562984501302);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027243,1264533562984501291);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027240,1264533562984501296);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027244,1264533562984501294);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027244,1264533562984501297);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027245,1264533562984501295);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027253,1264533562984501302);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027254,1264533562984501299);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027254,1264533562984501309);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027255,1264533562984501303);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027255,1264533562984501300);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027255,1264533562984501309);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027256,1264533562984501301);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027257,1264533562984501304);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027264,1264533562984501309);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027265,1264533562984501306);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027265,1264533562984501316);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027266,1264533562984501310);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027266,1264533562984501307);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027266,1264533562984501316);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027267,1264533562984501308);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027277,1264533562984501315);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027277,1264533562984501316);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027277,1264533562984501321);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027278,1264533562984501312);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027279,1264533562984501313);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027280,1264533562984501314);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027281,1264533562984501318);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027282,1264533562984501322);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027282,1264533562984501319);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027283,1264533562984501320);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027291,1264533562984501325);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027291,1264533562984501326);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027291,1264533562984501327);
INSERT INTO `zz_sys_perm_code_perm` VALUES(1264533563039027291,1264533562984501328);
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