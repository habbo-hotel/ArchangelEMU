CREATE TABLE `users_saved_searches`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `search_code` varchar(255) NOT NULL,
  `filter` varchar(255) NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

ALTER TABLE `users_settings`
ADD COLUMN `ui_flags` int(11) NOT NULL DEFAULT 1 AFTER `forums_post_count`;

ALTER TABLE `users_settings`
ADD COLUMN `has_gotten_default_saved_searches` tinyint(1) NOT NULL DEFAULT 0 AFTER `ui_flags`;

ALTER TABLE `support_tickets`
ADD COLUMN `group_id` int(11) NOT NULL AFTER `category`,
ADD COLUMN `thread_id` int(11) NOT NULL AFTER `group_id`,
ADD COLUMN `comment_id` int(11) NOT NULL AFTER `thread_id`;

ALTER TABLE `pet_actions`
ADD COLUMN `can_swim` enum('1','0') NULL DEFAULT '0' AFTER `random_actions`;

UPDATE `pet_actions` SET `can_swim` = '1' WHERE `pet_type` = 9 OR `pet_type` = 14 OR `pet_type` = 23 OR `pet_type` = 24 OR `pet_type` = 25 OR `pet_type` = 28 OR `pet_type` = 29 OR `pet_type` = 30 OR `pet_type` = 32;

UPDATE `items_base` SET `customparams` = '30,60,120,180,300,600', `interaction_type` = 'game_timer', `interaction_modes_count` = 1 WHERE `item_name` IN ('fball_counter','bb_counter','es_counter');
