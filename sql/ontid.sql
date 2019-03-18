/*
Navicat MySQL Data Transfer

Source Server         : prod
Source Server Version : 50725
Source Host           : 139.219.136.188:3306
Source Database       : ontid

Target Server Type    : MYSQL
Target Server Version : 50725
File Encoding         : 65001

Date: 2019-03-11 17:24:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_ont_developer
-- ----------------------------
DROP TABLE IF EXISTS `tbl_ont_developer`;
CREATE TABLE `tbl_ont_developer` (
  `id` int(11) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) DEFAULT '',
  `app_id` varchar(255) DEFAULT '',
  `app_secret` varchar(255) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tbl_ontid
-- ----------------------------
DROP TABLE IF EXISTS `tbl_ontid`;
CREATE TABLE `tbl_ontid` (
  `id` int(10) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `ontid` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `keystore` text,
  `pwd` varchar(255) DEFAULT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `tx` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for tbl_secure
-- ----------------------------
DROP TABLE IF EXISTS `tbl_secure`;
CREATE TABLE `tbl_secure` (
  `id` int(10) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `ontid` varchar(255) DEFAULT NULL,
  `secure` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for tbl_sms
-- ----------------------------
DROP TABLE IF EXISTS `tbl_sms`;
CREATE TABLE `tbl_sms` (
  `id` int(20) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `phone` varchar(255) DEFAULT NULL,
  `verify_code` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for tbl_order
-- ----------------------------
DROP TABLE IF EXISTS `tbl_order`;
CREATE TABLE `tbl_order` (
`id` int(10) unsigned zerofill NOT NULL AUTO_INCREMENT,
`buyer_ontid` varchar(255) DEFAULT NULL,
`supply_ontid` varchar(255) DEFAULT NULL,
`buyer_tx` text,
`supply_tx` text,
`buyer_event` text,
`supply_event` text,
`buyer_height` int(10) DEFAULT NULL,
`supply_height` int(10) DEFAULT NULL,
`state` int(2) DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;