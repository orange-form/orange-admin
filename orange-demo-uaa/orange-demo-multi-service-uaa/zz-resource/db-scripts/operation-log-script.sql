SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 所有微服务集中存储的系统操作日志表，请在专门的操作日志数据库中执行该脚本。
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_operation_log`;
CREATE TABLE `zz_sys_operation_log` (
  `log_id` bigint(20) NOT NULL COMMENT '主键Id',
  `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '日志描述',
  `operation_type` int(11) DEFAULT NULL COMMENT '操作类型',
  `service_name` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '接口所在服务名称',
  `api_class` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '调用的controller全类名',
  `api_method` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '调用的controller中的方法',
  `session_id` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户会话sessionId',
  `trace_id` char(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '每次请求的Id',
  `elapse` int(11) DEFAULT NULL COMMENT '调用时长',
  `request_method` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'HTTP 请求方法，如GET',
  `request_url` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'HTTP 请求地址',
  `request_arguments` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'controller接口参数',
  `response_result` text(20000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'controller应答结果',
  `request_ip` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请求IP',
  `success` bit(1) DEFAULT NULL COMMENT '应答状态',
  `error_msg` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '错误信息',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户Id',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作员Id',
  `operator_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作员名称',
  `operation_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_trace_id_idx` (`trace_id`),
  KEY `idx_operation_type_idx` (`operation_type`),
  KEY `idx_operation_time_idx` (`operation_time`) USING BTREE,
  KEY `idx_success` (`success`) USING BTREE,
  KEY `idx_elapse` (`elapse`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统操作日志表';

SET FOREIGN_KEY_CHECKS = 1;
