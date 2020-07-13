-- Hide email from specific ranks.
ALTER TABLE `permissions` ADD `acc_hide_mail` ENUM('0', '1') NOT NULL DEFAULT '0' AFTER `acc_hide_ip`;

-- Flood with rights.
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('flood.with.rights', '0');

-- Softkick command.
ALTER TABLE `permissions` ADD `cmd_softkick` ENUM('0', '1') NOT NULL DEFAULT '0' AFTER `cmd_kickall`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_softkick', 'softkick');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_softkick_not_found', '%user% not found');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_softkick_error_self', 'You can not softkick yourself!');

-- Rank ignoring
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('generic.error.ignore_higher_rank', 'You can\'t ignore this user.');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.allow.ignore.staffs', '1');

-- Inventory Limiting
INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('error.bots.max.inventory', 'You can\'t buy or pickup anymore bots until you place some, the maximum amount of bots you are allowed is %amount%.');

UPDATE `emulator_texts` SET `value` = 'You\'ve reached the maximum amount of pets in your inventory! The Limit is %amount%!' WHERE `key` = 'error.pets.max.inventory';

-- Tradelock counter
ALTER TABLE `users_settings` ADD `tradelock_amount` INT(11) NOT NULL DEFAULT '0' AFTER `helper_level`;

-- Gift wrapping configuration
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.gifts.box_types', '0,1,2,3,4,5,6,8');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.gifts.ribbon_types', '0,1,2,3,4,5,6,7,8,9,10');

-- Configurable mute time
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.flood.mute.time', '30');

-- TraxManager Jukebox Limits
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.jukebox.limit.large', '20');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.jukebox.limit.normal', '10');

-- Background Toner Scripter Warning
INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('scripter.warning.item.bgtoner.permission', '%username% tried to change the background toner without rights in %room% owned by %owner%');

-- Table structure for room_trax
DROP TABLE IF EXISTS `room_trax`;
CREATE TABLE `room_trax`  (
  `room_id` int(11) NOT NULL,
  `trax_item_id` int(11) NOT NULL
) ENGINE = InnoDB CHARACTER SET = armscii8 COLLATE = armscii8_general_ci ROW_FORMAT = Compact;

-- Table structure for trax_playlist
DROP TABLE IF EXISTS `trax_playlist`;
CREATE TABLE `trax_playlist`  (
  `trax_item_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL
) ENGINE = InnoDB CHARACTER SET = armscii8 COLLATE = armscii8_general_ci ROW_FORMAT = Compact;

