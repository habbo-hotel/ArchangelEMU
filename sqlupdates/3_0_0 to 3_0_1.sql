INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('wired.place.under', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('wired.custom.enabled', '0');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_stalk.forgot_username', 'Specify the username of the Habbo you want to follow!');

-- Enable or Disable TTY in console (Default is enabled)
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('console.mode', '1');

-- Youtube Api v3 key to YoutubeManager
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('youtube.apikey', '');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.gifts.length.max', '300');

-- Add friendship categories table
CREATE TABLE `messenger_categories` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(25) NOT NULL,
    `user_id` int NOT NULL,
    UNIQUE KEY `identifier` (`id`)
);

-- Set an ID (int) from category list items
ALTER TABLE messenger_friendships ADD category int NOT NULL DEFAULT '0' AFTER friends_since;