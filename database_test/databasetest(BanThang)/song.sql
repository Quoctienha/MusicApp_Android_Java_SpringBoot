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

 Date: 04/05/2025 11:48:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for song
-- ----------------------------
DROP TABLE IF EXISTS `song`;
CREATE TABLE `song`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `artist_id` bigint NULL DEFAULT NULL,
  `file_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `image_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `lyrics` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `likes` bigint NULL DEFAULT NULL,
  `dislikes` bigint NULL DEFAULT NULL,
  `views` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK7oarm0rg2cmcostoym9tovqtk`(`artist_id` ASC) USING BTREE,
  CONSTRAINT `FK7oarm0rg2cmcostoym9tovqtk` FOREIGN KEY (`artist_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of song
-- ----------------------------
INSERT INTO `song` VALUES (1, 'Một bài hát không vui mấy', 1, 'https://docs.google.com/uc?export=download&id=1-yGdQXxLPgS9gFhFQEoJsLkMmMgd-etB', 'https://i.ytimg.com/vi/6GeTOJ_2RiA/maxresdefault.jpg', 'Đã qua bao lâu\r\nMà sao cứ ngỡ như đã xa nhau chỉ vài hôm\r\nThời gian đã lấy đi em lấy đi một nụ cười\r\nDường như em đã là một ai khác\r\nChẳng còn... là của anh\r\nĐã thôi bên nhau\r\nMà trong ánh mắt em sao chẳng khác như lần đầu\r\nTừng là của nhau sao chẳng nói ', 2, 1, 2);
INSERT INTO `song` VALUES (2, 'Âm thầm bên em', 1, 'https://docs.google.com/uc?export=download&id=1Eayn5TdHvWONATAySjhkVEk-gv4fELk5', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTZTw56rP-48qvljRWDlRzaCV-LY28Ubw1jeQ&s', NULL, 0, 1, 1);
INSERT INTO `song` VALUES (3, 'Giờ thì', 1, 'https://docs.google.com/uc?export=download&id=16c7QUnAwsB3WrZ8ucrufHeiBLVr-xnoO', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRg0ZqvAZQSND__ftG8K7eHxgPeaPumPj_4DQ&s', NULL, 0, 0, 4);
INSERT INTO `song` VALUES (4, 'Mất kết nối', 1, 'https://docs.google.com/uc?export=download&id=1Ph8_3Z-I3w0DsN2ygvN53UecVzFX14SJ', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTFmo4r6BUC837_ainwtqcUd4hAiDk2tbIhMw&s', NULL, 0, 0, 3);
INSERT INTO `song` VALUES (5, 'Wrong Times', 1, 'https://docs.google.com/uc?export=download&id=1tytnnAnAM4bA-GJmijwwAsU33u5WSTas', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQYzuaQhJFoUPXpu1BywMvVTaaVeN_UzU3y4w&s', NULL, 0, 0, 2);

SET FOREIGN_KEY_CHECKS = 1;
