/*
 Navicat Premium Data Transfer

 Source Server         : music_app
 Source Server Type    : MySQL
 Source Server Version : 100432 (10.4.32-MariaDB)
 Source Host           : localhost:3306
 Source Schema         : music_app

 Target Server Type    : MySQL
 Target Server Version : 100432 (10.4.32-MariaDB)
 File Encoding         : 65001

 Date: 04/05/2025 11:47:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `refresh_token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKh6dr47em6vg85yuwt4e2roca4`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK7m8ru44m93ukyb61dfxw0apf6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES (2, 'test@gmail.com', b'0', '$2a$10$LbKWvO8FtGXlOzKnNRfzgOqYEiPE69wMCaQLpnl8zTer3pk.id6A2', 'testName', 1, NULL);
INSERT INTO `account` VALUES (3, 'test2@gmail.com', b'0', '$2a$10$VJtZwK/H92zzRwZF1pbmo.yumSKZBPZA5sXyuenu0u1iRSeJt5nrK', 'testName2', 2, NULL);
INSERT INTO `account` VALUES (4, 'test3@gmail.com', b'0', '$2a$10$hp2Z0y8ZTIayVmlwskayx./krOIsexnyPWyNgKZ5BRzUFMoFUKM7i', 'testName3', 3, NULL);
INSERT INTO `account` VALUES (5, 'testENV@gmail.com', b'0', '$2a$10$fZDaShJaDiZMLcs5.16Up.bU6PRk0NwWYi2DDNntYLhHEtG3/GD6.', 'testNameENV', 4, NULL);
INSERT INTO `account` VALUES (6, 'tinntunn4ever@gmail.com', b'1', '$2a$10$BTVXvCC0bRKkf5eqmh.7COEvAZkR6nXmtaink/tIWdBihPFHA1zLG', 'Thang1', 5, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUaGFuZzEiLCJpYXQiOjE3NDYxODIyNzIsImV4cCI6MTc0Njc4NzA3MiwidG9rZW5UeXBlIjoicmVmcmVzaCJ9.R0VCchrD4rmPqom3bW5muAForWoNci6FzWRfFLP4zoA');
INSERT INTO `account` VALUES (8, '22110070@student.hcmute.edu.vn', b'1', '$2a$10$T7UuZGGD4xF08se1oxPay.0xX.mDa7BcCi2ieEd3D3/.6SZVIR1sS', '1', 7, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ2MTgyNDEzLCJleHAiOjE3NDY3ODcyMTMsInRva2VuVHlwZSI6InJlZnJlc2gifQ.gkTV7gBQ-CqHNgQDhW_dQR4370bwYbf03PIoHybgzpw');

SET FOREIGN_KEY_CHECKS = 1;
