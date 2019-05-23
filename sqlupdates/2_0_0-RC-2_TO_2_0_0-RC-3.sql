#DATABASE UPDATE: 2.0.0 RC-2 -> 2.0.0 RC-3

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('commands.plugins.oldstyle', '0');

ALTER TABLE `emulator_errors`
ADD COLUMN `version`  varchar(64) NOT NULL AFTER `timestamp`,
ADD COLUMN `build_hash`  varchar(64) NOT NULL AFTER `version`;

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('scripter.modtool.tickets', '1');

ALTER TABLE `items_crackable`
ADD COLUMN `subscription_duration` int(3) NULL AFTER `required_effect`,
ADD COLUMN `subscription_type` varchar(255) NULL COMMENT 'hc for Habbo Club, bc for Builders Club' AFTER `subscription_duration`;

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('invisible.prevent.chat', '0');
INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('invisible.prevent.chat.error', 'While being invisible you cannot talk.');

INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('commands.succes.cmd_invisible.updated.back', 'You are now visible again.');

INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('commands.error.cmd_mimic.forbidden_clothing', 'The other user has clothing that you do not own yet.');
ALTER TABLE `permissions`
ADD COLUMN `acc_mimic_unredeemed` enum('0','1') NOT NULL DEFAULT '0';

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('discount.max.allowed.items', '100');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('discount.batch.size', '6');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('discount.batch.free.items', '1');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('discount.bonus.min.discounts', '1');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('discount.additional.thresholds', '40;99');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('clothing.strip_unowned', '0');

#END DATABASE UPDATE: 2.0.0 RC-2 -> 2.0.0 RC-3
