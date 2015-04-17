-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 10, 2014 at 04:56 AM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `acnproject`
--
CREATE DATABASE IF NOT EXISTS `acnproject` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `acnproject`;

-- --------------------------------------------------------

--
-- Table structure for table `acn_lock_connection_info`
--

CREATE TABLE IF NOT EXISTS `acn_lock_connection_info` (
  `LOCKID` varchar(15) NOT NULL DEFAULT '',
  `IS_BEING_USED` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`LOCKID`),
  KEY `IS_BEING_USED` (`IS_BEING_USED`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `acn_lock_connection_info`
--

INSERT INTO `acn_lock_connection_info` (`LOCKID`, `IS_BEING_USED`) VALUES
('LOCK_1', 'N'),
('LOCK_2', 'N'),
('LOCK_3', 'Y');

-- --------------------------------------------------------

--
-- Table structure for table `acn_lock_info`
--

CREATE TABLE IF NOT EXISTS `acn_lock_info` (
  `USERNAME` varchar(15) NOT NULL,
  `LOCKID` varchar(15) NOT NULL,
  `SOFT_LOCK_STATUS` varchar(8) DEFAULT 'LOCKED',
  `HARD_LOCK_STATUS` varchar(8) DEFAULT 'LOCKED',
  `IS_ONLINE` varchar(3) NOT NULL DEFAULT 'NO',
  `STATUS_CHANGED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `COMMENTS` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`LOCKID`),
  UNIQUE KEY `USERNAME` (`USERNAME`,`LOCKID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `acn_lock_info`
--

INSERT INTO `acn_lock_info` (`USERNAME`, `LOCKID`, `SOFT_LOCK_STATUS`, `HARD_LOCK_STATUS`, `IS_ONLINE`, `STATUS_CHANGED`, `COMMENTS`) VALUES
('foo', 'LOCK_1', 'LOCKED', 'LOCKED', 'NO', '2014-04-25 17:43:54', 'Front Door'),
('foo', 'LOCK_2', 'UNLOCKED', 'LOCKED', 'NO', '2014-05-07 16:48:31', 'Living Room'),
('foo', 'LOCK_3', 'LOCKED', 'LOCKED', 'YES', '2014-05-10 04:13:45', 'Phase 8 - Club House');

-- --------------------------------------------------------

--
-- Table structure for table `acn_lock_location_info`
--

CREATE TABLE IF NOT EXISTS `acn_lock_location_info` (
  `USERNAME` varchar(15) NOT NULL,
  `LOCKID` varchar(15) NOT NULL,
  `SOURCE` varchar(1) NOT NULL,
  `HOME_LONGITUDE` double DEFAULT NULL,
  `HOME_LATITUDE` double DEFAULT NULL,
  UNIQUE KEY `USERNAME` (`USERNAME`,`LOCKID`,`SOURCE`),
  KEY `FK_LOCKID` (`LOCKID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `acn_lock_location_info`
--

INSERT INTO `acn_lock_location_info` (`USERNAME`, `LOCKID`, `SOURCE`, `HOME_LONGITUDE`, `HOME_LATITUDE`) VALUES
('foo', 'LOCK_1', 'M', NULL, NULL),
('foo', 'LOCK_1', 'P', NULL, NULL),
('foo', 'LOCK_2', 'M', NULL, NULL),
('foo', 'LOCK_2', 'P', NULL, NULL),
('foo', 'LOCK_3', 'M', -96.61839499999999, 32.846175),
('foo', 'LOCK_3', 'P', -96.753505, 32.988796666666666);

-- --------------------------------------------------------

--
-- Table structure for table `acn_user_info`
--

CREATE TABLE IF NOT EXISTS `acn_user_info` (
  `USERNAME` varchar(15) NOT NULL,
  `PASSWORD` varchar(15) NOT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `acn_user_info`
--

INSERT INTO `acn_user_info` (`USERNAME`, `PASSWORD`, `CREATED_AT`) VALUES
('aditya', '1234', '2014-04-13 18:17:06'),
('foo', 'baar', '2014-04-13 18:17:49');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `acn_lock_connection_info`
--
ALTER TABLE `acn_lock_connection_info`
  ADD CONSTRAINT `ADD CONSTRAINT_FK_ACN_LOCK_CONNECTION_INFO` FOREIGN KEY (`LOCKID`) REFERENCES `acn_lock_info` (`LOCKID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `acn_lock_location_info`
--
ALTER TABLE `acn_lock_location_info`
  ADD CONSTRAINT `FK_LOCKID` FOREIGN KEY (`LOCKID`) REFERENCES `acn_lock_info` (`LOCKID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_USERNAME` FOREIGN KEY (`USERNAME`) REFERENCES `acn_user_info` (`USERNAME`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
