INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('camera.price.points.publish', '5');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('camera.price.points.publish.type', '0');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('camera.price.points.type', '0');

ALTER TABLE `room_promotions`
ADD COLUMN `start_timestamp` int(11) NOT NULL DEFAULT -1 AFTER `end_timestamp`;
ALTER TABLE `room_promotions`
ADD COLUMN `category` int(11) NOT NULL DEFAULT 0 AFTER `start_timestamp`;

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('navigator.eventcategories', '1,Hottest Events,false;2,Parties & Music,true;3,Role Play,true;4,Help Desk,true;5,Trading,true;6,Games,true;7,Debates & Discussions,true;8,Grand Openings,true;9,Friending,true;10,Jobs,true;11,Group Events,true');

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('room.promotion.badge', 'RADZZ');

CREATE TABLE `guild_forum_views`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `guild_id` int(11) NOT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

ALTER TABLE `support_tickets`
ADD COLUMN `photo_item_id` int(11) NOT NULL DEFAULT -1 AFTER `comment_id`;
