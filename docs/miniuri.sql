SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `url_mapping`;
CREATE TABLE `url_mapping`
(
    `id`          bigint                                                         NOT NULL AUTO_INCREMENT,
    `short_url`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '短链接',
    `long_url`    varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '长链接',
    `build_type`  tinyint                                                        NOT NULL COMMENT '生成类型',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `short_url` (`short_url`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
