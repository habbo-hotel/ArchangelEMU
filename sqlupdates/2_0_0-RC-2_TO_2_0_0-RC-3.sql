#DATABASE UPDATE: 2.0.0 RC-2 -> 2.0.0 RC-3

                                                                                                                                          INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('commands.plugins.oldstyle', '0');

                                                                                                                                          ALTER TABLE `emulator_errors`
                                                                                                                                          ADD COLUMN `version`  varchar(64) NOT NULL AFTER `timestamp`,
                                                                                                                                          ADD COLUMN `build_hash`  varchar(64) NOT NULL AFTER `version`;

                                                                                                                                          INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('scripter.modtool.tickets', '1');

ALTER TABLE `items_crackable`
ADD COLUMN `subscription_duration` int(3) NULL AFTER `required_effect`,
ADD COLUMN `subscription_type` varchar(255) NULL COMMENT 'hc for Habbo Club, bc for Builders Club' AFTER `subscription_duration`;

                                                                                                                                          #END DATABASE UPDATE: 2.0.0 RC-2 -> 2.0.0 RC-3