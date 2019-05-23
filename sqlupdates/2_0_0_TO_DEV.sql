#DATABASE UPDATE: 2.0.0 -> DEV

ALTER TABLE `guilds_forums` RENAME TO `old_guilds_forums`;
ALTER TABLE `guilds_forums_comments` RENAME TO `old_guilds_forums_comments`;

DROP TABLE IF EXISTS `guilds_forums_comments`;
CREATE TABLE `guilds_forums_comments`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `thread_id` int(11) NOT NULL DEFAULT 0,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `created_at` int(11) NOT NULL DEFAULT 0,
  `state` int(11) NOT NULL DEFAULT 0,
  `admin_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

CREATE TABLE `guilds_forums_threads`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `guild_id` int(11) NULL DEFAULT 0,
  `opener_id` int(11) NULL DEFAULT 0,
  `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '',
  `posts_count` int(11) NULL DEFAULT 0,
  `created_at` int(11) NULL DEFAULT 0,
  `updated_at` int(11) NULL DEFAULT 0,
  `state` int(11) NULL DEFAULT 0,
  `pinned` tinyint(4) NULL DEFAULT 0,
  `locked` tinyint(4) NULL DEFAULT 0,
  `admin_id` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;


DROP PROCEDURE IF EXISTS MIGRATION_FORUMS;
DELIMITER ;;

CREATE PROCEDURE MIGRATION_FORUMS()
BEGIN
	DECLARE n INT DEFAULT 0;
	DECLARE i INT DEFAULT 0;
	SELECT COUNT(*) FROM `old_guilds_forums` INTO n;
	SET i=0;
	WHILE i<n DO
		SET @old_id = (SELECT id FROM `old_guilds_forums` LIMIT i,1);
		INSERT INTO `guilds_forums_threads` (`guild_id`, `opener_id`, `subject`, `posts_count`, `created_at`, `updated_at`, `state`, `pinned`, `locked`, `admin_id`)
			SELECT `guild_id`, `user_id`, `subject`, 0 AS `posts_count`, `timestamp`, `timestamp`, IF(STRCMP(`state`,'OPEN')=0, 0, IF(STRCMP(`state`,'HIDDEN_BY_ADMIN')=0, 10, IF(STRCMP(`state`,'HIDDEN_BY_STAFF')=0, 20, 1))) AS `state`, IF(STRCMP(`pinned`,'1')=0, 1, 0), IF(STRCMP(`locked`,'1')=0, 1, 0), `admin_id` FROM `old_guilds_forums`
			LIMIT i,1;

		SET @new_id = LAST_INSERT_ID();
		INSERT INTO `guilds_forums_comments` (`thread_id`, `user_id`, `message`, `created_at`, `state`, `admin_id`)
			SELECT @new_id AS `thread_id`, `user_id`, `message`, `timestamp`, IF(STRCMP(`state`,'OPEN')=0, 0, IF(STRCMP(`state`,'HIDDEN_BY_ADMIN')=0, 10, IF(STRCMP(`state`,'HIDDEN_BY_STAFF')=0, 20, 1))) AS `state`, `admin_id` FROM `old_guilds_forums_comments`
			WHERE thread_id = @old_id;

		SET i = i + 1;
	END WHILE;
END;
;;

DELIMITER ;

CALL MIGRATION_FORUMS();
DROP PROCEDURE IF EXISTS MIGRATION_FORUMS;

UPDATE `users_pets` LEFT JOIN `rooms` ON `users_pets`.`room_id` = `rooms`.`id` SET `users_pets`.`room_id` = 0 WHERE `users_pets`.`room_id` != 0 AND `rooms`.`id` IS NULL;

ALTER TABLE `users_settings` ADD COLUMN `forums_post_count` int(11) NULL DEFAULT 0 AFTER `perk_trade`;

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('catalog.guild.hc_required', '1');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('catalog.ltd.random', '1');
UPDATE `emulator_settings` SET `value` = '0' WHERE `key` = 'hotel.banzai.points.tile.steal';
UPDATE `emulator_settings` SET `value` = '0' WHERE `key` = 'hotel.banzai.points.tile.fill';
UPDATE `emulator_settings` SET `value` = '1' WHERE `key` = 'hotel.banzai.points.tile.lock';

#END DATABASE UPDATE: 2.0.0 -> DEV