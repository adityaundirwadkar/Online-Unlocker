--<ScriptOptions statementTerminator=";"/>

ALTER TABLE `acnproject`.`acn_lock_connection_info` DROP PRIMARY KEY;

ALTER TABLE `acnproject`.`acn_user_info` DROP PRIMARY KEY;

ALTER TABLE `acnproject`.`acn_lock_info` DROP PRIMARY KEY;

ALTER TABLE `acnproject`.`acn_lock_info` DROP INDEX `acnproject`.`USERNAME`;

ALTER TABLE `acnproject`.`acn_lock_info` DROP INDEX `acnproject`.`USERNAME`;

ALTER TABLE `acnproject`.`acn_lock_connection_info` DROP INDEX `acnproject`.`IS_BEING_USED`;

DROP TABLE `acnproject`.`acn_user_info`;

DROP TABLE `acnproject`.`acn_lock_info`;

DROP TABLE `acnproject`.`acn_lock_connection_info`;

CREATE TABLE `acnproject`.`acn_user_info` (
	`USERNAME` VARCHAR(15) NOT NULL,
	`PASSWORD` VARCHAR(15) NOT NULL,
	`CREATED_AT` TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
	PRIMARY KEY (`USERNAME`)
) ENGINE=InnoDB;

CREATE TABLE `acnproject`.`acn_lock_info` (
	`USERNAME` VARCHAR(15) NOT NULL,
	`LOCKID` VARCHAR(15) NOT NULL,
	`SOFT_LOCK_STATUS` VARCHAR(8) DEFAULT LOCKED,
	`HARD_LOCK_STATUS` VARCHAR(8) DEFAULT LOCKED,
	`COMMENTS` VARCHAR(50),
	PRIMARY KEY (`LOCKID`)
) ENGINE=InnoDB;

CREATE TABLE `acnproject`.`acn_lock_connection_info` (
	`LOCKID` VARCHAR(15) NOT NULL,
	`IS_BEING_USED` CHAR(1) DEFAULT N NOT NULL,
	PRIMARY KEY (`LOCKID`)
) ENGINE=InnoDB;

CREATE UNIQUE INDEX `USERNAME` ON `acnproject`.`acn_lock_info` (`USERNAME` ASC);

CREATE UNIQUE INDEX `USERNAME` ON `acnproject`.`acn_lock_info` (`LOCKID` ASC);

CREATE INDEX `IS_BEING_USED` ON `acnproject`.`acn_lock_connection_info` (`IS_BEING_USED` ASC);

ALTER TABLE `acnproject`.`acn_lock_info` ADD PRIMARY KEY (`LOCKID`);

