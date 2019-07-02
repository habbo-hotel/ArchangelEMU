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

ALTER TABLE `youtube_playlists`
CHANGE COLUMN `video_id` `playlist_id` varchar(255) NOT NULL COMMENT 'YouTube playlist ID' AFTER `item_id`;

DROP TABLE `youtube_items`;

TRUNCATE TABLE `youtube_playlists`;

DROP PROCEDURE IF EXISTS DEFAULT_YTTV_PLAYLISTS;
DELIMITER ;;

CREATE PROCEDURE DEFAULT_YTTV_PLAYLISTS()
BEGIN
	DECLARE n INT DEFAULT 0;
	DECLARE i INT DEFAULT 0;
	DECLARE a INT DEFAULT 0;
	DECLARE itemId INT default 0;
	SELECT COUNT(*) FROM `items_base` WHERE `interaction_type` = 'youtube' INTO n;
	SET i=0;

	SET @defaultPlaylistIds = '["PL4YfV2mXS8WXOkxFly7YsGL8cKtqp873p","PL4F5KzcUTpEdux38c8CYunT9uNh_k2NPt","PL4F5KzcUTpEcO-1iw3P6gavJ_ALTxqNHn","PL4F5KzcUTpEfpHad_B7j_MulB3-cwtLFh","PL4F5KzcUTpEekJPbcVOaNYVV6VLSo9zRB","PL80F08DAE1B614BA9","PL4F5KzcUTpEfeS5t7EiEIYbpplZivDZTL","PL4ACB18CA629E650A","PL4F5KzcUTpEfyRBCOVKQ4qxlSoHsGDZ82","PL4F5KzcUTpEet7EMwhw0ge5n2oNMr7JY8","PL4F5KzcUTpEfTW4fkX9vrt497MEvWorwK","PL4F5KzcUTpEcit3i1q55-IFFndmo_dsR8","PL4F5KzcUTpEeJleVUhO1MWRJyYDWWp9Do","PL4F5KzcUTpEcFzCpH2_EXtwzKQH8mJGd9","PL4F5KzcUTpEcIiSOH2x3sg2jwACNbSIm9","PL4F5KzcUTpEfRxBiXwTBA7oiybPqoZD_j","PL4YfV2mXS8WUo09aevZX-b47k4PD08-i8","PL4F5KzcUTpEcFzCpH2_EXtwzKQH8mJGd9"]';

	WHILE i < n DO
		SET itemId = (SELECT id FROM `items_base` WHERE `interaction_type` = 'youtube' LIMIT i, 1);

		WHILE a<JSON_LENGTH(@defaultPlaylistIds) DO
			INSERT IGNORE INTO `youtube_playlists` (item_id, playlist_id) VALUES (itemId, TRIM(BOTH '"' FROM JSON_EXTRACT(@defaultPlaylistIds, CONCAT('$[',a,']'))));
			SET a = a + 1;
		END WHILE;

		SET a = 0;
		SET i = i + 1;
	END WHILE;
END;
;;

DELIMITER ;

CALL DEFAULT_YTTV_PLAYLISTS();
DROP PROCEDURE IF EXISTS DEFAULT_YTTV_PLAYLISTS;

ALTER TABLE `permissions`
ADD COLUMN `cmd_update_youtube_playlists` enum('0','1') NOT NULL DEFAULT '0';
INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('commands.keys.cmd_update_youtube_playlists', 'update_youtube;update_youtube_playlists');
INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('commands.succes.cmd_update_youtube_playlists', 'YouTube playlists have been refreshed!');

DROP PROCEDURE IF EXISTS UPDATE_TEAM_WIREDS;
DELIMITER ;;

CREATE PROCEDURE UPDATE_TEAM_WIREDS()
BEGIN
    IF (SELECT COUNT(*) FROM emulator_settings WHERE `key` = 'team.wired.update.rc-1') = 0 THEN
        INSERT INTO emulator_settings (`key`, `value`) VALUES ('team.wired.update.rc-1', 'DO NOT REMOVE THIS SETTING!');

        UPDATE
            items
        INNER JOIN
            items_base
            ON
                items.item_id = items_base.id
        SET items.wired_data = CONCAT(
            SUBSTRING_INDEX(items.wired_data, ';', 2),
            ';',
            CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(items.wired_data, ';', 3), ';', -1) AS SIGNED INTEGER) + 1,
            ';',
            SUBSTRING_INDEX(SUBSTRING_INDEX(items.wired_data, ';', 4), ';', -1)
        )
        WHERE
            items_base.interaction_type = 'wf_act_give_score_tm';


        UPDATE
            items
        INNER JOIN
            items_base
            ON
                items.item_id = items_base.id
        SET items.wired_data = CONCAT(
            SUBSTRING_INDEX(items.wired_data, '\t', 1),
            '\t',
            CAST(SUBSTRING_INDEX(items.wired_data, '\t', -1) AS SIGNED INTEGER) + 1
        )
        WHERE
            items_base.interaction_type = 'wf_act_join_team';

        UPDATE
            items
        INNER JOIN
            items_base
            ON
                items.item_id = items_base.id
        SET items.wired_data = CAST(items.wired_data AS SIGNED INTEGER) + 1
        WHERE
            items_base.interaction_type = 'wf_cnd_actor_in_team' OR items_base.interaction_type = 'wf_cnd_not_in_team';
    END IF;
END;
;;
DELIMITER ;

CALL UPDATE_TEAM_WIREDS();
DROP PROCEDURE IF EXISTS UPDATE_TEAM_WIREDS;
