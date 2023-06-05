/*
 Navicat Premium Data Transfer

 Source Server         : phpmyadmin
 Source Server Type    : MySQL
 Source Server Version : 100121
 Source Host           : localhost:3306
 Source Schema         : aurora

 Target Server Type    : MySQL
 Target Server Version : 100121
 File Encoding         : 65001

 Date: 03/06/2023 00:32:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for permission_commands
-- ----------------------------
DROP TABLE IF EXISTS `permission_commands`;
CREATE TABLE `permission_commands`  (
  `name` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NULL DEFAULT NULL,
  `keys` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = ascii COLLATE = ascii_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of permission_commands
-- ----------------------------
INSERT INTO `permission_commands` VALUES ('cmd_add_youtube_playlist', ':update_youtube - Update YouTube playlist.', 'update_youtube;update_youtube_playlists');
INSERT INTO `permission_commands` VALUES ('cmd_alert', ':alert - Alert an user with a message.', 'alert;warning');
INSERT INTO `permission_commands` VALUES ('cmd_allow_trading', ':tradelock - Toggle the tradelock for a user.', 'tradelock;blocktrading;disabletrade');
INSERT INTO `permission_commands` VALUES ('cmd_badge', ':badge - Give or remove a badge from a user.', 'badge;givebadge');
INSERT INTO `permission_commands` VALUES ('cmd_ban', ':ban - Ban a user, you may specify time.', 'ban');
INSERT INTO `permission_commands` VALUES ('cmd_block_alert', ':ignore_alerts - Hotel alerts don\'t show anymore.', 'blockalerts;blockalert;ignorealerts;ignore_alerts');
INSERT INTO `permission_commands` VALUES ('cmd_bots', ':bots_info - Get bots info from current room.', 'bots;bots_info');
INSERT INTO `permission_commands` VALUES ('cmd_bundle', ':bundle - Bundle this room', 'bundle;roombundle');
INSERT INTO `permission_commands` VALUES ('cmd_calendar', ':calendar - Open calendar', 'calendar');
INSERT INTO `permission_commands` VALUES ('cmd_changename', ':change_name - Change username.', 'changename;flagme;change_name;namechange');
INSERT INTO `permission_commands` VALUES ('cmd_chatcolor', ':chat <chat_id> - Change chat bubble', 'chat;chatcolor');
INSERT INTO `permission_commands` VALUES ('cmd_control', ':control <username> - Take control over other user.', 'control');
INSERT INTO `permission_commands` VALUES ('cmd_coords', ':coords - Show your coordinates', 'coords;cordinates;coordinates;position');
INSERT INTO `permission_commands` VALUES ('cmd_credits', ':credits <username> <amount> - Give credits to user.', 'credits;coins');
INSERT INTO `permission_commands` VALUES ('cmd_diagonal', ':diagonal - Toggle diagonal walk.', 'diagonal;disablediagonal;diagonally');
INSERT INTO `permission_commands` VALUES ('cmd_disconnect', ':disconnect <username>- Disconnect a user', 'dc;disconnect');
INSERT INTO `permission_commands` VALUES ('cmd_duckets', ':duckets <username> <amount> - Give duckets to user.', 'pixels;duckets');
INSERT INTO `permission_commands` VALUES ('cmd_ejectall', ':ejectall', 'ejectall;ejectfurni');
INSERT INTO `permission_commands` VALUES ('cmd_empty', ':empty <username>', 'empty');
INSERT INTO `permission_commands` VALUES ('cmd_empty_bots', ':emptybots', 'emptybots;empty_bots;deletebots');
INSERT INTO `permission_commands` VALUES ('cmd_empty_pets', ':emptypets', 'emptypets;empty_pets;deletepets');
INSERT INTO `permission_commands` VALUES ('cmd_enable', ':enable <effect id>', 'enable;effect');
INSERT INTO `permission_commands` VALUES ('cmd_event', ':event <message>', 'event;roomevent');
INSERT INTO `permission_commands` VALUES ('cmd_faceless', ':faceless', 'faceless;face');
INSERT INTO `permission_commands` VALUES ('cmd_fastwalk', ':fastwalk', 'fastwalk;supersonic');
INSERT INTO `permission_commands` VALUES ('cmd_filterword', ':filter <word> [replacement]', 'filter;banword;filterword');
INSERT INTO `permission_commands` VALUES ('cmd_freeze', ':freeze <username>', 'freeze');
INSERT INTO `permission_commands` VALUES ('cmd_freeze_bots', ':freezebots', 'freeze_bot;freezebot;freezebots;freeze_bots');
INSERT INTO `permission_commands` VALUES ('cmd_gift', ':gift <username> <itemid>', 'gift');
INSERT INTO `permission_commands` VALUES ('cmd_give_rank', ':giverank <username> <rank>', 'giverank;setrank;give_rank;set_rank');
INSERT INTO `permission_commands` VALUES ('cmd_ha', ':ha <message>', 'hotelalert;ha');
INSERT INTO `permission_commands` VALUES ('cmd_hal', ':hal <url> <message>', 'hal;halink');
INSERT INTO `permission_commands` VALUES ('cmd_hand_item', ':handitem <itemid>', 'handitem;item;hand');
INSERT INTO `permission_commands` VALUES ('cmd_happy_hour', ':happyhour', 'happyhour;happy_hour');
INSERT INTO `permission_commands` VALUES ('cmd_hide_wired', ':hide_wired - Hide wired from room.', 'hidewired;hidemywired;wiredbegone;hide_wired');
INSERT INTO `permission_commands` VALUES ('cmd_invisible', ':invisible', 'invisible;hideme');
INSERT INTO `permission_commands` VALUES ('cmd_ip_ban', ':ipban <username> [reason]', 'ipban;banip;ip_ban;ban_ip');
INSERT INTO `permission_commands` VALUES ('cmd_machine_ban', ':machineban <username> [reason]', 'machineban;banmachine;banmac;macban');
INSERT INTO `permission_commands` VALUES ('cmd_mass_badge', ':mass_badge <badge>', 'massbadge;hotelbadge;mass_badge');
INSERT INTO `permission_commands` VALUES ('cmd_mass_credits', ':mass_credits <amount>', 'mass_credits;masscredits');
INSERT INTO `permission_commands` VALUES ('cmd_mass_duckets', ':mass_duckets <amount>', 'mass_duckets;massduckets;mass_pixels;masspixels');
INSERT INTO `permission_commands` VALUES ('cmd_mass_gift', ':mass_gift <itemid>', 'massgift;mass_gift');
INSERT INTO `permission_commands` VALUES ('cmd_mass_points', ':mass_points <amount> [type]', 'mass_points;masspoints');
INSERT INTO `permission_commands` VALUES ('cmd_mimic', ':mimic <username>', 'mimic;copy');
INSERT INTO `permission_commands` VALUES ('cmd_moonwalk', ':moonwalk', 'moonwalk;mj;moon_walk');
INSERT INTO `permission_commands` VALUES ('cmd_multi', ':multi', 'multi');
INSERT INTO `permission_commands` VALUES ('cmd_mute', ':mute <username>', 'mute;shutup');
INSERT INTO `permission_commands` VALUES ('cmd_pet_info', ':petinfo <petname>', 'pet;pet_info;petinfo');
INSERT INTO `permission_commands` VALUES ('cmd_pick_all', ':pickall - Pick all furni from current room.', 'pickall;pickupall');
INSERT INTO `permission_commands` VALUES ('cmd_points', ':points <username> <amount> [type]', 'diamonds;points');
INSERT INTO `permission_commands` VALUES ('cmd_promote_offer', ':promoteoffer <offer_id> [info]', 'promoteoffer;promotetargetoffer;promote_offer');
INSERT INTO `permission_commands` VALUES ('cmd_pull', ':pull <username>', 'pull');
INSERT INTO `permission_commands` VALUES ('cmd_push', ':push <username>', 'push');
INSERT INTO `permission_commands` VALUES ('cmd_redeem', ':redeem', 'redeem;exchange');
INSERT INTO `permission_commands` VALUES ('cmd_reload_room', ':reload_room', 'reload_room;reload;reloadroom');
INSERT INTO `permission_commands` VALUES ('cmd_required_custom', 'My description', '1');
INSERT INTO `permission_commands` VALUES ('cmd_rights', ':rights - Show my rights.', 'rights;acc_rights');
INSERT INTO `permission_commands` VALUES ('cmd_room_alert', ':room_alert <message>', 'roomalert;room_alert;ra');
INSERT INTO `permission_commands` VALUES ('cmd_room_badge', ':room_badge <badge>', 'roombadge;room_badge');
INSERT INTO `permission_commands` VALUES ('cmd_room_credits', ':room_credits <amount>', 'roomcredits;room_credits;roomcoins;room_coins');
INSERT INTO `permission_commands` VALUES ('cmd_room_dance', ':room_dance - Make everybody dance in the room.', 'danceall;room_dance;roomdance');
INSERT INTO `permission_commands` VALUES ('cmd_room_duckets', ':room_duckets <amount>', 'roompixels;room_pixels;roomduckets;room_duckets');
INSERT INTO `permission_commands` VALUES ('cmd_room_effect', ':room_effect [effect id]', 'roomeffect;room_effect');
INSERT INTO `permission_commands` VALUES ('cmd_room_gift', ':room_gift <item_id> [message]', 'roomgift;room_gift');
INSERT INTO `permission_commands` VALUES ('cmd_room_item', ':room_item [itemid]', 'roomitem;room_item');
INSERT INTO `permission_commands` VALUES ('cmd_room_kick', ':room_kick <message>', 'room_kick;kickall;roomkick;kick_all');
INSERT INTO `permission_commands` VALUES ('cmd_room_mute', ':room_mute', 'roommute;room_mute');
INSERT INTO `permission_commands` VALUES ('cmd_room_points', ':room_points <amount>', 'roompoints;room_points');
INSERT INTO `permission_commands` VALUES ('cmd_say', ':say <username> <text>', 'say;makesay');
INSERT INTO `permission_commands` VALUES ('cmd_say_all', ':sayall <message>', 'sayall;say_all');
INSERT INTO `permission_commands` VALUES ('cmd_set_max', ':setmax <amount>', 'setmax;set_max');
INSERT INTO `permission_commands` VALUES ('cmd_set_poll', ':setpoll <id>', 'setpoll;set_poll');
INSERT INTO `permission_commands` VALUES ('cmd_set_speed', ':setspeed <speed>', 'speed;setspeed');
INSERT INTO `permission_commands` VALUES ('cmd_shout', ':makeshout <username> <text>', 'makeshout');
INSERT INTO `permission_commands` VALUES ('cmd_shout_all', ':shoutall <message>', 'shoutall;shout_all;roomshout;room_shout');
INSERT INTO `permission_commands` VALUES ('cmd_shutdown', ':shutdown', 'stop;shutdown');
INSERT INTO `permission_commands` VALUES ('cmd_sitdown', ':sitdown', 'sitall;sitdown');
INSERT INTO `permission_commands` VALUES ('cmd_soft_kick', ':softkick', 'softkick');
INSERT INTO `permission_commands` VALUES ('cmd_staff_alert', ':staff_alert <message>', 'sa;staffalert;staff_alert');
INSERT INTO `permission_commands` VALUES ('cmd_staff_online', ':staff_online <min_rank>', 'staffonline;staffs;staff_online');
INSERT INTO `permission_commands` VALUES ('cmd_staff_summon', ':staff_summon <rank_id>', 'staffsummon;staff_summon;summonrank');
INSERT INTO `permission_commands` VALUES ('cmd_stalk', ':stalk <username>', 'stalk;follow;rape');
INSERT INTO `permission_commands` VALUES ('cmd_subscription', ':subscription', 'subscription;sub');
INSERT INTO `permission_commands` VALUES ('cmd_summon', ':summon <username>', 'summon');
INSERT INTO `permission_commands` VALUES ('cmd_super_ban', ':superban <username> [reason]', 'superban;megaban');
INSERT INTO `permission_commands` VALUES ('cmd_super_pull', ':super_pull <username>', 'spull;superpull;super_pull');
INSERT INTO `permission_commands` VALUES ('cmd_take_badge', ':takebadge <username> <badge>', 'takebadge;take_badge;remove_badge;removebadge');
INSERT INTO `permission_commands` VALUES ('cmd_talk', ':talk <username> <message>', 'talk');
INSERT INTO `permission_commands` VALUES ('cmd_teleport', ':teleport', 'tele;teleport');
INSERT INTO `permission_commands` VALUES ('cmd_transform', ':transform <name> <race> <color>', 'transform;becomepet');
INSERT INTO `permission_commands` VALUES ('cmd_unban', ':unban <username>', 'unban');
INSERT INTO `permission_commands` VALUES ('cmd_unload', ':unload', 'crash;unload');
INSERT INTO `permission_commands` VALUES ('cmd_unmute', ':unmute <username>', 'unmute');
INSERT INTO `permission_commands` VALUES ('cmd_update_achievements', ':update_achievements', 'uach;update_achievements');
INSERT INTO `permission_commands` VALUES ('cmd_update_bots', ':update_bots', 'update_bots;updatebots');
INSERT INTO `permission_commands` VALUES ('cmd_update_catalogue', ':update_catalog', 'reload_catalogue;reload_cata;update_catalogue;update_cata;update_catalog;update_shop');
INSERT INTO `permission_commands` VALUES ('cmd_update_config', ':update_config', 'update_config;update_configuration');
INSERT INTO `permission_commands` VALUES ('cmd_update_guildparts', ':update_guild_parts', 'update_guildparts;update_guild_parts');
INSERT INTO `permission_commands` VALUES ('cmd_update_hotel_view', ':update_hotel_view', 'update_view;update_hotel_view;update_hotelview');
INSERT INTO `permission_commands` VALUES ('cmd_update_items', ':update_items', 'update_items;reload_items');
INSERT INTO `permission_commands` VALUES ('cmd_update_navigator', ':update_navigator', 'update_navigator;update_nav');
INSERT INTO `permission_commands` VALUES ('cmd_update_permissions', ':update_permissions', 'update_permissions;update_perms');
INSERT INTO `permission_commands` VALUES ('cmd_update_pet_data', ':update_petdata', 'update_pet_data;update_petdata');
INSERT INTO `permission_commands` VALUES ('cmd_update_plugins', ':update_plugins', 'update_plugins;updateplugins');
INSERT INTO `permission_commands` VALUES ('cmd_update_polls', ':update_polls', 'update_polls;reload_polls');
INSERT INTO `permission_commands` VALUES ('cmd_update_texts', ':update_texts', 'update_texts;reload_texts');
INSERT INTO `permission_commands` VALUES ('cmd_update_wordfilter', ':update_word_filter', 'update_wordfilter;update_filter;update_word_filter');
INSERT INTO `permission_commands` VALUES ('cmd_user_info', ':user_info <username>', 'userinfo;user_info');
INSERT INTO `permission_commands` VALUES ('cmd_word_quiz', ':wordquiz <question>', 'wordquiz;quiz');

-- ----------------------------
-- Table structure for permission_group_commands
-- ----------------------------
DROP TABLE IF EXISTS `permission_group_commands`;
CREATE TABLE `permission_group_commands`  (
  `group_id` int(11) NOT NULL,
  `command_name` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  `setting_type` enum('0','1','2') CHARACTER SET ascii COLLATE ascii_general_ci NULL DEFAULT '1',
  PRIMARY KEY (`group_id`, `command_name`) USING BTREE,
  INDEX `FK_GroupCommands`(`command_name`) USING BTREE,
  CONSTRAINT `FK_GroupCommands` FOREIGN KEY (`command_name`) REFERENCES `permission_commands` (`name`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_Groups` FOREIGN KEY (`group_id`) REFERENCES `permission_groups` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = ascii COLLATE = ascii_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for permission_group_rights
-- ----------------------------
DROP TABLE IF EXISTS `permission_group_rights`;
CREATE TABLE `permission_group_rights`  (
  `group_id` int(11) NOT NULL,
  `right_name` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  `setting_type` enum('0','1','2') CHARACTER SET ascii COLLATE ascii_general_ci NULL DEFAULT '1',
  PRIMARY KEY (`group_id`, `right_name`) USING BTREE,
  INDEX `FK_GroupRights`(`right_name`) USING BTREE,
  CONSTRAINT `FK_GroupRights` FOREIGN KEY (`right_name`) REFERENCES `permission_rights` (`name`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_Group` FOREIGN KEY (`group_id`) REFERENCES `permission_groups` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = ascii COLLATE = ascii_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for permission_group_timers
-- ----------------------------
DROP TABLE IF EXISTS `permission_group_timers`;
CREATE TABLE `permission_group_timers`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NULL DEFAULT NULL,
  `currency_type` int(11) NULL DEFAULT NULL,
  `amount` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = ascii COLLATE = ascii_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for permission_groups
-- ----------------------------
DROP TABLE IF EXISTS `permission_groups`;
CREATE TABLE `permission_groups`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL DEFAULT '',
  `description` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NULL DEFAULT '',
  `level` int(11) NULL DEFAULT 1,
  `prefix` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NULL DEFAULT '',
  `prefix_color` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NULL DEFAULT '',
  `badge` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NULL DEFAULT '',
  `room_effect` int(11) NULL DEFAULT 0,
  `log_enabled` enum('0','1') CHARACTER SET ascii COLLATE ascii_general_ci NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = ascii COLLATE = ascii_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for permission_rights
-- ----------------------------
DROP TABLE IF EXISTS `permission_rights`;
CREATE TABLE `permission_rights`  (
  `name` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = ascii COLLATE = ascii_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of permission_rights
-- ----------------------------
INSERT INTO `permission_rights` VALUES ('acc_ads_background', NULL);
INSERT INTO `permission_rights` VALUES ('acc_ambassador', NULL);
INSERT INTO `permission_rights` VALUES ('acc_anychatcolor', NULL);
INSERT INTO `permission_rights` VALUES ('acc_anyroomowner', NULL);
INSERT INTO `permission_rights` VALUES ('acc_calendar_force', NULL);
INSERT INTO `permission_rights` VALUES ('acc_camera', NULL);
INSERT INTO `permission_rights` VALUES ('acc_can_stalk', NULL);
INSERT INTO `permission_rights` VALUES ('acc_catalog_ids', NULL);
INSERT INTO `permission_rights` VALUES ('acc_chat_no_filter', NULL);
INSERT INTO `permission_rights` VALUES ('acc_chat_no_flood', NULL);
INSERT INTO `permission_rights` VALUES ('acc_chat_no_limit', NULL);
INSERT INTO `permission_rights` VALUES ('acc_empty_others', NULL);
INSERT INTO `permission_rights` VALUES ('acc_enable_others', NULL);
INSERT INTO `permission_rights` VALUES ('acc_enteranyroom', NULL);
INSERT INTO `permission_rights` VALUES ('acc_floorplan_editor', NULL);
INSERT INTO `permission_rights` VALUES ('acc_fullrooms', NULL);
INSERT INTO `permission_rights` VALUES ('acc_guildgate', NULL);
INSERT INTO `permission_rights` VALUES ('acc_guild_admin', NULL);
INSERT INTO `permission_rights` VALUES ('acc_helper_give_guide_tours', NULL);
INSERT INTO `permission_rights` VALUES ('acc_helper_judge_chat_reviews', NULL);
INSERT INTO `permission_rights` VALUES ('acc_helper_use_guide_tool', NULL);
INSERT INTO `permission_rights` VALUES ('acc_hide_ip', NULL);
INSERT INTO `permission_rights` VALUES ('acc_hide_mail', NULL);
INSERT INTO `permission_rights` VALUES ('acc_infinite_credits', NULL);
INSERT INTO `permission_rights` VALUES ('acc_infinite_friends', NULL);
INSERT INTO `permission_rights` VALUES ('acc_infinite_pixels', NULL);
INSERT INTO `permission_rights` VALUES ('acc_infinite_points', NULL);
INSERT INTO `permission_rights` VALUES ('acc_mimic_unredeemed', NULL);
INSERT INTO `permission_rights` VALUES ('acc_modtool_room_info', NULL);
INSERT INTO `permission_rights` VALUES ('acc_modtool_room_logs', NULL);
INSERT INTO `permission_rights` VALUES ('acc_modtool_ticket_q', NULL);
INSERT INTO `permission_rights` VALUES ('acc_modtool_user_alert', NULL);
INSERT INTO `permission_rights` VALUES ('acc_modtool_user_ban', NULL);
INSERT INTO `permission_rights` VALUES ('acc_modtool_user_logs', NULL);
INSERT INTO `permission_rights` VALUES ('acc_moverotate', NULL);
INSERT INTO `permission_rights` VALUES ('acc_nomute', NULL);
INSERT INTO `permission_rights` VALUES ('acc_not_mimiced', NULL);
INSERT INTO `permission_rights` VALUES ('acc_no_mute', NULL);
INSERT INTO `permission_rights` VALUES ('acc_placefurni', NULL);
INSERT INTO `permission_rights` VALUES ('acc_see_tentchat', NULL);
INSERT INTO `permission_rights` VALUES ('acc_see_whispers', NULL);
INSERT INTO `permission_rights` VALUES ('acc_staff_pick', NULL);
INSERT INTO `permission_rights` VALUES ('acc_superwired', NULL);
INSERT INTO `permission_rights` VALUES ('acc_supporttool', NULL);
INSERT INTO `permission_rights` VALUES ('acc_trade_anywhere', NULL);
INSERT INTO `permission_rights` VALUES ('acc_unkickable', NULL);
INSERT INTO `permission_rights` VALUES ('acc_unlimited_bots', NULL);
INSERT INTO `permission_rights` VALUES ('acc_unlimited_pets', NULL);

INSERT INTO `aurora`.`emulator_texts`(`key`, `value`) VALUES ('commands.generic.cmd_rights.text', 'Your Rights');

SET FOREIGN_KEY_CHECKS = 1;
