SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ================================
-- 短链接相关表
-- ================================
DROP TABLE IF EXISTS `url_mapping`;
CREATE TABLE `url_mapping`
(
    `id`          bigint                                                         NOT NULL AUTO_INCREMENT,
    `short_url`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '短链接',
    `long_url`    varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '长链接',
    `build_type`  tinyint                                                        NOT NULL COMMENT '生成类型',
    `user_id`     bigint DEFAULT NULL COMMENT '用户ID，匿名用户为NULL',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `short_url` (`short_url`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;

-- ================================
-- 用户认证相关表
-- ================================

-- 用户基础信息表
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '用户名',
    `nickname`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '昵称',
    `email`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
    `phone`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '手机号',
    `avatar_url`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像URL',
    `status`      tinyint                                                       NOT NULL DEFAULT 1 COMMENT '用户状态：0-禁用，1-正常',
    `user_type`   tinyint                                                       NOT NULL DEFAULT 1 COMMENT '用户类型：1-免费用户，2-付费用户，3-VIP用户',
    `expire_time` datetime                                                      DEFAULT NULL COMMENT '付费到期时间',
    `create_time` datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                                      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_username` (`username`) USING BTREE,
    UNIQUE INDEX `uk_email` (`email`) USING BTREE,
    UNIQUE INDEX `uk_phone` (`phone`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic
  COMMENT = '用户基础信息表';

-- 用户认证信息表
DROP TABLE IF EXISTS `user_auth`;
CREATE TABLE `user_auth`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint                                                        NOT NULL COMMENT '用户ID',
    `auth_type`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '认证类型：password-密码，github-GitHub，wechat-微信，sms-短信',
    `auth_key`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '认证标识：用户名/邮箱/手机号/第三方ID',
    `auth_value`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '认证值：密码hash/第三方token等',
    `extra_info`  json                                                          DEFAULT NULL COMMENT '额外信息：第三方用户信息等',
    `status`      tinyint                                                       NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                                      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_auth_type_key` (`auth_type`, `auth_key`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic
  COMMENT = '用户认证信息表';

-- 用户会话表
DROP TABLE IF EXISTS `user_session`;
CREATE TABLE `user_session`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint                                                        NOT NULL COMMENT '用户ID',
    `session_id`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID',
    `device_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设备信息',
    `ip_address`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT 'IP地址',
    `user_agent`  varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户代理',
    `expire_time` datetime                                                      NOT NULL COMMENT '过期时间',
    `create_time` datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                                      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_session_id` (`session_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_expire_time` (`expire_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic
  COMMENT = '用户会话表';

-- 短信验证码表
DROP TABLE IF EXISTS `sms_code`;
CREATE TABLE `sms_code`
(
    `id`          bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `phone`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号',
    `code`        varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
    `code_type`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码类型：login-登录，register-注册',
    `used`        tinyint                                                      NOT NULL DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
    `expire_time` datetime                                                     NOT NULL COMMENT '过期时间',
    `create_time` datetime                                                     DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_phone_type` (`phone`, `code_type`) USING BTREE,
    INDEX `idx_expire_time` (`expire_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic
  COMMENT = '短信验证码表';

-- 用户权限表（为收费功能预留）
DROP TABLE IF EXISTS `user_permission`;
CREATE TABLE `user_permission`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint                                                        NOT NULL COMMENT '用户ID',
    `permission`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '权限标识',
    `resource`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源标识',
    `expire_time` datetime                                                      DEFAULT NULL COMMENT '权限过期时间',
    `create_time` datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                                      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_permission` (`user_id`, `permission`, `resource`) USING BTREE,
    INDEX `idx_expire_time` (`expire_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic
  COMMENT = '用户权限表';

-- 用户订单表（收费功能）
DROP TABLE IF EXISTS `user_order`;
CREATE TABLE `user_order`
(
    `id`           bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`      bigint                                                        NOT NULL COMMENT '用户ID',
    `order_no`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '订单号',
    `product_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '产品类型：premium-高级版，vip-VIP版',
    `amount`       decimal(10, 2)                                                NOT NULL COMMENT '订单金额',
    `status`       tinyint                                                       NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已取消，3-已退款',
    `pay_type`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '支付方式：alipay-支付宝，wechat-微信支付',
    `pay_time`     datetime                                                      DEFAULT NULL COMMENT '支付时间',
    `expire_time`  datetime                                                      DEFAULT NULL COMMENT '服务到期时间',
    `create_time`  datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_time`  datetime                                                      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_order_no` (`order_no`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic
  COMMENT = '用户订单表';

-- 短链访问统计表（收费功能-监控）
DROP TABLE IF EXISTS `url_access_log`;
CREATE TABLE `url_access_log`
(
    `id`          bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `short_url`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '短链接',
    `user_id`     bigint                                                       DEFAULT NULL COMMENT '短链创建者用户ID',
    `ip_address`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '访问IP',
    `user_agent`  varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户代理',
    `referer`     varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源页面',
    `access_time` datetime                                                     NOT NULL COMMENT '访问时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_short_url` (`short_url`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_access_time` (`access_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic
  COMMENT = '短链访问统计表';

SET FOREIGN_KEY_CHECKS = 1;
