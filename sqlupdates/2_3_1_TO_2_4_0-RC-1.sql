INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.room.stickies.max', '200');

ALTER TABLE `users_settings` ADD COLUMN `last_purchase_timestamp` int(11) NOT NULL DEFAULT UNIX_TIMESTAMP();

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('retro.style.homeroom', '1');
