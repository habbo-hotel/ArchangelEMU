ALTER TABLE `permissions` ADD `acc_hide_mail` ENUM('0', '1') NOT NULL DEFAULT '0' AFTER `acc_hide_ip`;

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('flood.with.rights', '0');