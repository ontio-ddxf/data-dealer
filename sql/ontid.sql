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
CREATE TABLE `tbl_ontid`
(
  `id`       int(10) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `ontid`    varchar(255) DEFAULT NULL,
  `phone`    varchar(255) DEFAULT NULL,
  `keystore` text,
  `pwd`      varchar(255) DEFAULT NULL,
  `mail`     varchar(255) DEFAULT NULL,
  `method`   varchar(255) DEFAULT NULL,
  `tx`       text,
  `type`     int(2)       DEFAULT NULL COMMENT '1-数据需求方;2-数据提供方',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = latin1;

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
CREATE TABLE `tbl_order`
(
  `order_id`         varchar(255) NOT NULL,
  `exchange_id`      varchar(255) DEFAULT NULL,
  `buyer_ontid`      varchar(255) DEFAULT NULL,
  `seller_ontid`     varchar(255) DEFAULT NULL,
  `buy_tx`           text,
  `sell_tx`          text,
  `recv_token_tx`    text,
  `recv_msg_tx`      text,
  `cancel_tx`        text,
  `buy_event`        text,
  `sell_event`       text,
  `recv_token_event` text,
  `recv_msg_event`   text,
  `cancel_event`     text,
  `buy_date`         datetime     DEFAULT NULL,
  `sell_date`        datetime     DEFAULT NULL,
  `recv_token_date`  datetime     DEFAULT NULL,
  `recv_msg_date`    datetime     DEFAULT NULL,
  `cancel_date`      datetime     DEFAULT NULL,
  `state`            varchar(255) DEFAULT NULL COMMENT 'bought;boughtOnchain;buyerCancel;buyerCancelOnchain;delivered;deliveredOnchain;sellerRecvToken;sellerRecvTokenOnchain;buyerRecvMsg;buyerRecvMsgOnchain',
  `check_time`       datetime     DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


-- ----------------------------
-- Table structure for tbl_event
-- ----------------------------
DROP TABLE IF EXISTS `tbl_event`;
CREATE TABLE `tbl_event`
(
  `id`    varchar(255) CHARACTER SET latin1 NOT NULL,
  `ontid` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `tx`    text CHARACTER SET latin1,
  `event` text CHARACTER SET latin1,
  `date`  datetime                          DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

-- ----------------------------
-- Table structure for tbl_order_data
-- ----------------------------
DROP TABLE IF EXISTS `tbl_order_data`;
CREATE TABLE `tbl_order_data`
(
  `id`       varchar(255) CHARACTER SET latin1 NOT NULL,
  `order_id` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `data_id`  varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;