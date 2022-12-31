INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_changename.user_not_found', 'The Habbo %user% does not exist or is not online.');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_changename.done', 'Successfully toggled the name change for the Habbo %user%.');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_changename.received', 'Hotel staff requests for you to change your name.\n Please click on your Habbo then click on the "Change Your Name" button.');

ALTER TABLE `achievements`
ADD `visible` enum('0','1') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT 'Is the achievement in use and/or obtainable?';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('encryption.forced', '0');
