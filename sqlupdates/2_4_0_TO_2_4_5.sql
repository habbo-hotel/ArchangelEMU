-- Permissions to see tent chat
ALTER TABLE `permissions` ADD `acc_see_tentchat` ENUM('0', '1') NOT NULL DEFAULT '0' AFTER `acc_see_whispers`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('hotel.room.tent.prefix', 'Tent');
