ALTER TABLE `users_pets`
ADD COLUMN `saddle_item_id` int(11) NULL;

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.bot.placement.messages', 'Yo!;Hello I\'m a real party animal!;Hello!');

UPDATE `items_base` SET `customparams` = '1,true' WHERE `item_name` = 'wf_blob';
UPDATE `items_base` SET `customparams` = '5,false' WHERE `item_name` = 'wf_blob2';
