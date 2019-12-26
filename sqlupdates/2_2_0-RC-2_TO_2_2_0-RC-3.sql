INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.room.furni.max', '2500');
UPDATE items_base SET interaction_type = 'vote_counter' WHERE item_name = 'vote_count_add';