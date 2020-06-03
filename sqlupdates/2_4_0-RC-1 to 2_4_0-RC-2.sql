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