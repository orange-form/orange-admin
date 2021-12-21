
-- ----------------------------
-- 缺省情况下，我们将uaa的数据和upms的数据放到同一个数据库中。
-- 用户可根据实际应用场景，在实际的数据库链接中执行该脚本。
-- 主数据源 [localhost:3306/zzdemo-multi]
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 应用客户端详情表
-- ----------------------------
DROP TABLE IF EXISTS `zz_auth_client_details`;
CREATE TABLE `zz_auth_client_details` (
  `client_id` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用标识',
  `client_secret` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '应用密钥(bcyt) 加密',
  `client_secret_plain` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '应用密钥(明文)',
  `client_desc` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '应用名称',
  `authorized_grant_types` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '5种oauth授权方式(authorization_code,password,refresh_token,client_credentials)',
  `web_server_redirect_uri` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'UAA回调前端服务器的地址',
  `access_token_validity` int DEFAULT NULL COMMENT 'access_token有效期',
  `refresh_token_validity` int DEFAULT NULL COMMENT 'refresh_token有效期',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建者Id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint(20) NOT NULL COMMENT '更新者Id',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `deleted_flag` int NOT NULL COMMENT '删除标记(1: 正常 -1: 已删除)',
  PRIMARY KEY (`client_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_deleted_flag` (`deleted_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- 应用服务授权用户表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_uaa_user`;
CREATE TABLE `zz_sys_uaa_user` (
  `user_id` bigint(20) NOT NULL COMMENT '主键Id',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户登录名称',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `show_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户显示名称',
  `locked` int NOT NULL COMMENT '状态(0: 正常 1: 锁定)',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建者Id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint(20) NOT NULL COMMENT '更新者Id',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `deleted_flag` int NOT NULL COMMENT '是否删除(1: 正常 -1: 已删除)',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_deleted_flag` (`deleted_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统用户表';

-- ----------------------------
-- UAA系统操作员表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_uaa_operator`;
CREATE TABLE `zz_sys_uaa_operator` (
  `operator_id` bigint(20) NOT NULL COMMENT '主键Id',
  `login_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户登录名称',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `show_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户显示名称',
  `operator_type` int NOT NULL COMMENT '用户类型(0: 管理员 1: 普通操作员)',
  `head_image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户头像的Url',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建者Id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint(20) NOT NULL COMMENT '更新者Id',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `deleted_flag` int NOT NULL COMMENT '删除标记(1: 正常 -1: 已删除)',
  PRIMARY KEY (`operator_id`) USING BTREE,
  UNIQUE KEY `uk_login_name` (`login_name`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_deleted_flag` (`deleted_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统用户表';

-- ----------------------------
-- 插入缺省数据
-- ----------------------------
BEGIN;
-- 插入测试应用客户端。client_id/client_secret = app/app
INSERT INTO `zz_auth_client_details` VALUES ('app', '$2a$10$i3F515wEDiB4Gvj9ym9Prui0dasRttEUQ9ink4Wpgb4zEDCAlV8zO', 'app', '测试应用客户端', 'authorization_code,password,refresh_token', 'http://localhost:8085', 3600, NULL, 1293376634887344128, CURDATE(), 1293376634887344128, CURDATE(), 1);
-- 插入uaa测试用户。username/password: admin/123456
INSERT INTO `zz_sys_uaa_user` VALUES (1293376634887344128, 'admin', '$2a$10$DDltNLYmTOfpb.LBxOxHB.dsihIkSHLcu2iKpjlC3ArCHnhZ1wlqS', '管理员', 0, 1293376634887344128, CURDATE(), 1293376634887344128, CURDATE(), 1);
-- 插入uaa管理操作员。username/password: admin/123456
INSERT INTO `zz_sys_uaa_operator` VALUES(1293376634887344128, 'admin', '$2a$10$.x6y2shml9GkUNxeZ/hwH..dvbRyYSa18ivVkJvOQIvFRm8Yc97v6', '管理员', 0, NULL, 1293376634887344128, CURDATE(), 1293376634887344128, CURDATE(), 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;