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

 Date: 04/05/2025 11:49:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_type` varchar(31) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `membership` enum('NORMAL','PREMIUM') CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('ARTIST', 1, 'Trí', '090219314', 'NORMAL');
INSERT INTO `user` VALUES ('CUSTOMER', 2, 'Thắng', '0933141132', 'NORMAL');
INSERT INTO `user` VALUES ('CUSTOMER', 3, NULL, NULL, 'NORMAL');
INSERT INTO `user` VALUES ('CUSTOMER', 4, NULL, NULL, 'NORMAL');
INSERT INTO `user` VALUES ('CUSTOMER', 5, NULL, NULL, 'NORMAL');
INSERT INTO `user` VALUES ('CUSTOMER', 7, NULL, NULL, 'NORMAL');

SET FOREIGN_KEY_CHECKS = 1;
