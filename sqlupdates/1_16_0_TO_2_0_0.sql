#DATABASE UPDATE: 1.16.0 -> 2.0.0
INSERT INTO `catalog_pages`(`id`, `parent_id`, `caption_save`, `caption`, `icon_color`, `icon_image`, `visible`, `enabled`, `min_rank`, `club_only`, `order_num`, `page_layout`, `page_headline`, `page_teaser`, `page_special`, `page_text1`, `page_text2`, `page_text_details`, `page_text_teaser`, `vip_only`, `includes`, `room_id`) VALUES (null, -1, 'guilds_forum', 'Group Forums', 1, 27181, '1', '1', 1, '0', 2, 'guild_forum', 'catalog_groups_en', 'catalog_groupsteaser_en', '', 'Group Forums are here! Buy a terminal and chat to your groups!', '', 'Group Forums are here!', '', '0', '', 0);
UPDATE `catalog_items`  set page_id = LAST_INSERT_ID() WHERE `catalog_name` LIKE '%guild_forum%';
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.room.wired.norules', '0');

#END DATABASE UPDATE: 1.16.0 -> 2.0.0