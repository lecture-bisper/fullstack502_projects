-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 58.239.58.243    Database: java502_team1_mobile_db
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chat_last_read`
--

DROP TABLE IF EXISTS `chat_last_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_last_read` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `last_read_at` datetime(6) NOT NULL,
  `room_id` varchar(100) NOT NULL,
  `user_id` varchar(100) NOT NULL,
  `last_read_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9mr7ap064dislqcb9ymscn3t3` (`room_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_last_read`
--

LOCK TABLES `chat_last_read` WRITE;
/*!40000 ALTER TABLE `chat_last_read` DISABLE KEYS */;
INSERT INTO `chat_last_read` VALUES (1,'2025-08-21 05:15:39.607831','zz|zzz','zzz',3),(2,'2025-08-20 23:38:14.106624','zz|zzz','zz',3),(3,'2025-08-22 01:10:40.071141','insssu|jin59','jin59',101),(4,'2025-08-22 01:10:37.469480','insssu|jin59','insssu',101),(5,'2025-08-21 02:19:49.161840','insssu|zzz','insssu',15),(6,'2025-08-22 00:52:07.180405','jin59|zz','jin59',76),(7,'2025-08-22 00:52:04.581206','jin59|zz','zz',76),(8,'2025-08-21 03:51:55.759226','insssu|zzz','zzz',15),(9,'2025-08-22 00:44:19.862926','jin59|korea','jin59',27),(10,'2025-08-22 00:00:59.234971','jin59|korea','korea',27),(11,'2025-08-22 00:51:36.977783','insssu|zz','zz',74),(12,'2025-08-22 00:51:37.974654','insssu|zz','insssu',74),(13,'2025-08-22 01:07:03.144909','crescent|insssu','insssu',99),(14,'2025-08-22 01:07:03.174836','crescent|insssu','crescent',99),(15,'2025-08-22 08:43:29.133704','38a3ab42804dca2af167aaff','insssu',321),(16,'2025-08-22 08:59:05.792762','38a3ab42804dca2af167aaff','jin59',324),(17,'2025-08-22 02:31:15.593986','b7f1183dde2c7a433c881eed','insssu',114),(18,'2025-08-22 02:31:14.649989','b7f1183dde2c7a433c881eed','crescent',114),(19,'2025-08-25 00:31:35.826258','b025825b1b3cd5bf7dc932a5','jin59',386),(20,'2025-08-25 00:30:37.084622','b025825b1b3cd5bf7dc932a5','crescent',386),(21,'2025-08-22 07:51:19.978528','2daa0b6a02e3d649c447d182','insssu',169),(22,'2025-08-22 09:56:16.399208','crescent|test1','crescent',362),(23,'2025-08-22 09:56:17.318927','crescent|test1','test1',362),(24,'2025-08-25 00:27:24.085715','bb|jejuair','bb',364),(25,'2025-08-25 00:27:23.407316','bb|jejuair','jejuair',364),(26,'2025-08-25 00:29:38.132157','bb|jin59','bb',379),(27,'2025-08-25 00:29:40.003000','bb|jin59','jin59',379),(28,'2025-08-25 00:55:05.900043','hoho|jin59','hoho',396),(29,'2025-08-25 00:54:35.440609','hoho|jin59','jin59',394),(30,'2025-08-25 00:58:59.958521','hoho|jejuair','hoho',397),(31,'2025-08-25 01:25:17.717708','jejuair|xxx','xxx',401),(32,'2025-08-25 01:25:34.390137','jejuair|xxx','jejuair',405);
/*!40000 ALTER TABLE `chat_last_read` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-25 10:33:14
