# Host: localhost  (Version: 5.5.47)
# Date: 2017-04-19 00:26:11
# Generator: MySQL-Front 5.3  (Build 4.234)

/*!40101 SET NAMES utf8 */;

#
# Structure for table "datatransmission"
#

DROP TABLE IF EXISTS `datatransmission`;
CREATE TABLE `datatransmission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `telphone` varchar(11) DEFAULT '0',
  `img1` varchar(255) DEFAULT NULL,
  `img2` varchar(255) DEFAULT NULL,
  `img3` varchar(255) DEFAULT NULL,
  `img4` varchar(255) DEFAULT NULL,
  `img5` varchar(255) DEFAULT NULL,
  `data` text,
  `time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

#
# Data for table "datatransmission"
#

/*!40000 ALTER TABLE `datatransmission` DISABLE KEYS */;
INSERT INTO `datatransmission` VALUES (15,'13012461269','/img/a1.jpg','/img/a2.jpg','/img/a3.jpg','/img/a4.jpg','/img/a3.jpg',NULL,'2017-04-19 00:19:09'),(16,'13012461269','/img/a1.jpg','/img/a2.jpg','/img/a3.jpg','/img/a4.jpg','/img/a3.jpg',NULL,'2017-04-19 00:19:38'),(17,'13012461269','/img/a1.jpg','/img/a2.jpg','/img/a3.jpg','/img/a4.jpg','/img/a3.jpg',NULL,'2017-04-19 00:21:30'),(18,'13012461269','/img/a1.jpg','/img/a2.jpg','/img/a3.jpg','/img/a4.jpg','/img/a3.jpg',NULL,'2017-04-19 00:22:17'),(19,'13012461269','/img/a1.jpg','/img/a2.jpg','/img/a3.jpg','/img/a4.jpg','/img/a3.jpg',NULL,'2017-04-19 00:23:32'),(20,'13012461269','/img/a1.jpg','/img/a2.jpg','/img/a3.jpg','/img/a4.jpg','/img/a3.jpg',NULL,'2017-04-19 00:24:21');
/*!40000 ALTER TABLE `datatransmission` ENABLE KEYS */;
