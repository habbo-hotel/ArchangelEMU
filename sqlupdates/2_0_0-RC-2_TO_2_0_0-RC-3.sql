#DATABASE UPDATE: 2.0.0 RC-2 -> 2.0.0 RC-3

                                                                                                                                          INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('commands.plugins.oldstyle', '0');

                                                                                                                                          ALTER TABLE `emulator_errors`
                                                                                                                                          ADD COLUMN `version`  varchar(64) NOT NULL AFTER `timestamp`,
                                                                                                                                          ADD COLUMN `build_hash`  varchar(64) NOT NULL AFTER `version`;

                                                                                                                                          INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('scripter.modtool.tickets', '1');

                                                                                                                                          #END DATABASE UPDATE: 2.0.0 RC-2 -> 2.0.0 RC-3