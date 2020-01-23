CREATE TABLE `sanctions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `habbo_id` int(11) NOT NULL DEFAULT '0',
  `sanction_level` int(11) NOT NULL DEFAULT '0',
  `probation_timestamp` int(32) NOT NULL DEFAULT '0',
  `reason` varchar(255) NOT NULL DEFAULT '',
  `trade_locked_until` int(32) NOT NULL DEFAULT '0',
  `is_muted` tinyint(1) NOT NULL DEFAULT '0',
  `mute_duration` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `sanction_levels` (
  `level` int(1) NOT NULL,
  `type` enum('ALERT', 'BAN', 'MUTE') NOT NULL,
  `hour_length` int(12) NOT NULL,
  `probation_days` int(12) NOT NULL,
  PRIMARY KEY (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `sanction_levels`
ADD CONSTRAINT `level` CHECK (`level`<=7);

INSERT INTO `sanction_levels` VALUES (1, 'ALERT', 0, 30);
INSERT INTO `sanction_levels` VALUES (2, 'MUTE', 1, 30);
INSERT INTO `sanction_levels` VALUES (3, 'BAN', 18, 30);
INSERT INTO `sanction_levels` VALUES (4, 'BAN', 168, 30);
INSERT INTO `sanction_levels` VALUES (5, 'BAN', 720, 60);
INSERT INTO `sanction_levels` VALUES (6, 'BAN', 720, 60);
INSERT INTO `sanction_levels` VALUES (7, 'BAN', 876581, 876581);

INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('hotel.sanctions.enabled', '1');
