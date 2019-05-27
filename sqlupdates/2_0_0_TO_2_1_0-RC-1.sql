CREATE TABLE `users_saved_searches`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `search_code` varchar(255) NOT NULL,
  `filter` varchar(255) NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

ALTER TABLE `users_settings`
ADD COLUMN `ui_flags` int(11) NOT NULL DEFAULT 1 AFTER `forums_post_count`;