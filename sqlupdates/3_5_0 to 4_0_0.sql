INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_changename.user_not_found', 'The Habbo %user% does not exist or is not online.');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_changename.done', 'Successfully toggled the name change for the Habbo %user%.');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_changename.received', 'Hotel staff requests for you to change your name.\n Please click on your Habbo then click on the "Change Your Name" button.');

ALTER TABLE `achievements` ADD `visible` enum('0','1') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT 'Is the achievement in use and/or obtainable?';

UPDATE `pet_commands_data` SET `required_level` = '1' WHERE  `command_id` = '35';

INSERT IGNORE INTO `emulator_settings` (`key`, `value`) VALUES ('encryption.forced', '0');

UPDATE `emulator_texts` SET `key` = 'generic.pet.happiness', `value` = 'Happiness' WHERE `key` = 'generic.pet.happyness';

ALTER TABLE `pet_commands_data` CHANGE `cost_happyness` `cost_happiness` int(11) NOT NULL DEFAULT '0';

ALTER TABLE `users_pets` CHANGE `happyness` `happiness` int(11) NOT NULL DEFAULT '100';

UPDATE `items_base` SET `interaction_type` = 'spinning_bottle', `interaction_modes_count` = '8' WHERE `item_name` = 'bottle';

--New bot walking settings
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.limit.walking.distance', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.limit.walking.distance.radius', '5');
