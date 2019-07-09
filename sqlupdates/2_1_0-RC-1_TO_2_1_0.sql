ALTER TABLE `permissions`
ADD COLUMN `cmd_add_youtube_playlist` enum('0','1') NOT NULL DEFAULT '0';

INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('commands.keys.cmd_add_youtube_playlist', 'add_youtube;add_playlist;add_youtube_playlist');
INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('commands.error.cmd_add_youtube_playlist.usage', 'Usage: base_item_id youtube_playlist_id');
INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('commands.error.cmd_add_youtube_playlist.no_base_item', 'A base item with that ID could not be found.');
INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('commands.error.cmd_add_youtube_playlist.failed_playlist', 'Error: unable to fetch the given YouTube playlist.');
INSERT INTO `emulator_texts`(`key`, `value`) VALUES ('commands.succes.cmd_add_youtube_playlist', 'The playlist has been added successfully!');

UPDATE `emulator_texts` SET `value` = 'Superwired Usage Information. Possible reward types:<br/>badge: BADGE CODE<br/>Credits: credits#amount<br/>Pixels: pixels#amount<br/>Points: points#amount<br/>Respect: respect#amount<br/>Furniture: furni#FurnitureID<br/>Catalog Item: cata#CatalogItemID' WHERE `key` = 'hotel.wired.superwired.info';