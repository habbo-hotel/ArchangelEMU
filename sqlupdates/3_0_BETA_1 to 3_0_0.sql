-- Wired variables for bots
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('wired.variable.name', '%name%');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('wired.variable.roomname', '%roomname%');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('wired.variable.user_count', '%user_count%');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('wired.variable.owner', '%owner%');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('wired.variable.item_count', '%item_count%');

-- Enable bubble alerts
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('bubblealerts.enabled', '1');

-- Enable or Disable TTY in console (Default is enabled)
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('console.mode', '0');
