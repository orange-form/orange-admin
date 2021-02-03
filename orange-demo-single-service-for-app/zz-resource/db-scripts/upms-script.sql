
-- ----------------------------
-- 请仅在下面的数据库链接中执行该脚本。
-- 主数据源 [localhost:3306/zzdemo-single]
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_user`;
CREATE TABLE `zz_sys_user` (
    `user_id` bigint(20) NOT NULL COMMENT '主键Id',
    `login_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '用户登录名称',
    `password` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
    `show_name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '用户显示名称',
    `user_type` int(11) NOT NULL COMMENT '用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)',
    `head_image_url` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户头像的Url',
    `user_status` int(11) NOT NULL COMMENT '状态(0: 正常 1: 锁定)',
    `create_user_id` bigint(20) NOT NULL COMMENT '创建者Id',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_user_id` bigint(20) NOT NULL COMMENT '更新者Id',
    `update_time` datetime NOT NULL COMMENT '最后更新时间',
    `deleted_flag` int(11) NOT NULL COMMENT '删除标记(1: 正常 -1: 已删除)',
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE KEY `uk_login_name` (`login_name`) USING BTREE,
    KEY `idx_status` (`user_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='系统用户表';

-- ----------------------------
-- 管理员账号数据
-- ----------------------------
BEGIN;
INSERT INTO `zz_sys_user` VALUES(1356949260217618433,'admin','$2a$10$kgLilf1w72o13TJJYefgH.ZZuqrxh4PYinBvl5wrrcao7ZCm5Rh.i','管理员',0,'CHANGE TO YOUR HEAD IMAGE URL!!!',0,1356949260217618433,CURDATE(),1356949260217618433,CURDATE(),1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;