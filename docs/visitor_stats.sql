-- 访问统计表
DROP TABLE IF EXISTS `visitor_stats`;
CREATE TABLE `visitor_stats`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `page_path`   varchar(255) NOT NULL COMMENT '页面路径',
    `page_name`   varchar(100) NOT NULL COMMENT '页面名称',
    `visit_count` bigint       NOT NULL DEFAULT 0 COMMENT '访问次数(PV)',
    `unique_count` bigint      NOT NULL DEFAULT 0 COMMENT '独立访客数(UV)',
    `stat_date`   date         NOT NULL COMMENT '统计日期',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_page_date` (`page_path`, `stat_date`) USING BTREE,
    KEY `idx_stat_date` (`stat_date`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '页面访问统计表'
  ROW_FORMAT = Dynamic;

-- 访问日志表（可选，用于详细分析）
DROP TABLE IF EXISTS `visitor_log`;
CREATE TABLE `visitor_log`
(
    `id`           bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `page_path`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '页面路径',
    `visitor_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '访客标识(基于IP+UA生成)',
    `ip_address`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT 'IP地址',
    `user_agent`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '浏览器标识',
    `referer`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源页面',
    `visit_time`   datetime                                                      NOT NULL COMMENT '访问时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_page_path` (`page_path`) USING BTREE,
    KEY `idx_visit_time` (`visit_time`) USING BTREE,
    KEY `idx_visitor_id` (`visitor_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '访问日志表'
  ROW_FORMAT = Dynamic;

