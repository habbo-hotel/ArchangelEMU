ALTER TABLE `permissions`
ADD COLUMN `auto_credits_amount` INT DEFAULT '0',
ADD COLUMN `auto_pixels_amount` INT DEFAULT '0',
ADD COLUMN `auto_gotw_amount` INT DEFAULT '0',
ADD COLUMN `auto_points_amount` INT DEFAULT '0';

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.auto.gotwpoints.enabled', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.auto.gotwpoints.interval', '600');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.auto.gotwpoints.ignore.idled', '1');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.auto.gotwpoints.ignore.hotelview', '1');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.auto.gotwpoints.type', '4');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.auto.gotwpoints.name', 'shell');

CREATE TABLE `items_highscore_data`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(11) NOT NULL,
  `user_ids` varchar(500) NOT NULL,
  `score` int(11) NOT NULL,
  `is_win` tinyint(1) NULL DEFAULT 0,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `calendar_rewards`;
CREATE TABLE `calendar_rewards`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `custom_image` varchar(128) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `credits` int(11) NOT NULL DEFAULT 0,
  `points` int(11) NOT NULL DEFAULT 0,
  `points_type` int(3) NOT NULL DEFAULT 0,
  `badge` varchar(25) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `item_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `voucher_history`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `voucher_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

ALTER TABLE `vouchers`
ADD COLUMN `amount` int(11) NOT NULL DEFAULT 1,
ADD COLUMN `limit` int(11) NOT NULL DEFAULT -1;

ALTER TABLE `users_pets`
ADD COLUMN `mp_is_dead` tinyint(1) NOT NULL DEFAULT 0;

ALTER TABLE `items` CHARACTER SET = utf8, COLLATE = utf8_general_ci;

ALTER TABLE `items_base`
ADD COLUMN `clothing_on_walk` varchar(255) NOT NULL DEFAULT '';
