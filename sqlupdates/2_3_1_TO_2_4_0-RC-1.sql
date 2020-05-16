INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.room.stickies.max', '200');

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('retro.style.homeroom', '1');

ALTER TABLE  `permissions` ADD  `cmd_softkick` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_kickall`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_softkick', 'softkick');
