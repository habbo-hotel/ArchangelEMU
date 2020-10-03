CREATE TABLE `users_subscriptions` (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(10) UNSIGNED NULL,
  `subscription_type` varchar(255) NULL,
  `timestamp_start` int(10) UNSIGNED NULL,
  `duration` int(10) UNSIGNED NULL,
  `active`  tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  INDEX `user_id`(`user_id`),
  INDEX `subscription_type`(`subscription_type`),
  INDEX `timestamp_start`(`timestamp_start`),
  INDEX `active`(`active`)
);

CREATE TABLE `logs_shop_purchases` (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `timestamp` int(10) UNSIGNED NULL,
  `user_id` int(10) UNSIGNED NULL,
  `catalog_item_id` int(10) UNSIGNED NULL,
  `item_ids` text DEFAULT NULL,
  `catalog_name` varchar(255) NULL,
  `cost_credits` int(10) NULL,
  `cost_points` int(10) NULL,
  `points_type` int(10) NULL,
  `amount` int(10) NULL,
  PRIMARY KEY (`id`),
  INDEX `timestamp`(`timestamp`),
  INDEX `user_id`(`user_id`)
);

CREATE TABLE `logs_hc_payday` (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `timestamp` int(10) UNSIGNED NULL,
  `user_id` int(10) UNSIGNED NULL,
  `hc_streak` int(10) UNSIGNED NULL,
  `total_coins_spent` int(10) UNSIGNED NULL,
  `reward_coins_spent` int(10) UNSIGNED NULL,
  `reward_streak` int(10) UNSIGNED NULL,
  `total_payout` int(10) UNSIGNED NULL,
  `currency` varchar(255) NULL,
  `claimed` tinyint(1) DEFAULT 0 NULL,
  PRIMARY KEY (`id`),
  INDEX `timestamp`(`timestamp`),
  INDEX `user_id`(`user_id`)
);

ALTER TABLE `emulator_settings` MODIFY COLUMN `value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL AFTER `key`;
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.hc.payday.enabled', '1');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.hc.payday.next_date', '2020-10-15 00:00:00');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.hc.payday.interval', '1 month');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.hc.payday.query', 'SELECT SUM(cost_credits) AS `amount_spent` FROM `logs_shop_purchases` WHERE `user_id` = @user_id AND `timestamp` > @timestamp_start AND `timestamp` <= @timestamp_end AND `catalog_name` NOT LIKE \'CF_%\' AND `catalog_name` NOT LIKE \'CFC_%\';');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.hc.payday.streak', '7=5;30=10;60=15;90=20;180=25;365=30');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.hc.payday.currency', 'credits');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.hc.payday.percentage', '10');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.hc.payday.creditsspent_reset_on_expire', '1');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('subscriptions.hc.payday.message', 'Woohoo HC Payday has arrived! You have received %amount% credits to your purse. Enjoy!')

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.scheduler.enabled', '1');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('subscriptions.scheduler.interval', '10');

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.clothingvalidation.onhcexpired', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.clothingvalidation.onlogin', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.clothingvalidation.onchangelooks', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.clothingvalidation.onmimic', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.clothingvalidation.onmannequin', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.clothingvalidation.onfballgate', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('gamedata.figuredata.url', 'https://habbo.com/gamedata/figuredata/0');

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.max.friends', '300');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.max.friends.hc', '1100');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.max.rooms', '50');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.users.max.rooms.hc', '75');

DELETE FROM `emulator_settings` WHERE `key` = 'hotel.max.rooms.per.user';
DELETE FROM `emulator_settings` WHERE `key` = 'hotel.max.rooms.user';
DELETE FROM `emulator_settings` WHERE `key` = 'hotel.max.rooms.vip';

DELETE FROM `emulator_settings` WHERE `key` = 'max.friends';
DELETE FROM `emulator_settings` WHERE `key` = 'max.friends';

ALTER TABLE `users_settings` ADD COLUMN `max_friends` int(10) NULL DEFAULT 300 AFTER `has_gotten_default_saved_searches`;
ALTER TABLE `users_settings` ADD COLUMN `max_rooms` int(10) NULL DEFAULT 50 AFTER `has_gotten_default_saved_searches`;
ALTER TABLE `users_settings` ADD COLUMN `last_hc_payday` int(10) NULL DEFAULT 0 AFTER `has_gotten_default_saved_searches`;

ALTER TABLE `permissions` ADD COLUMN `cmd_subscription` enum('0','1') NULL DEFAULT '0' AFTER `cmd_credits`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_subscription', 'subscription;sub');

INSERT INTO users_subscriptions SELECT NULL, user_id, 'HABBO_CLUB' as `subscription_type`, UNIX_TIMESTAMP() AS `timestamp_start`, (club_expire_timestamp - UNIX_TIMESTAMP()) AS `duration`, 1 AS `active` FROM users_settings WHERE club_expire_timestamp > UNIX_TIMESTAMP();
