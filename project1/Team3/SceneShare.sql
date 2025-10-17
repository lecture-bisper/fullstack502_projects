-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: first_project_sceneshare_db
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `board_table`
--

DROP TABLE IF EXISTS `board_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `board_table` (
  `board_id` int NOT NULL AUTO_INCREMENT,
  `movie_id` int DEFAULT NULL,
  `rating` double NOT NULL,
  `create_date` datetime(6) DEFAULT NULL,
  `update_date` datetime(6) DEFAULT NULL,
  `contents` varchar(255) DEFAULT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `reply` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `user_img` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`board_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `board_table`
--

LOCK TABLES `board_table` WRITE;
/*!40000 ALTER TABLE `board_table` DISABLE KEYS */;
INSERT INTO `board_table` VALUES (1,4154664,3.5,'2025-10-15 16:57:53.746447','2025-10-15 16:57:53.746447','마블영화는 재밌어요 !',NULL,NULL,'[추천] Captain Marvel','test3',NULL),(2,4154664,3.5,'2025-10-15 16:58:13.694092','2025-10-15 16:58:13.693091','캡틴 마블 잘 봤어요~',NULL,NULL,'[추천] Captain Marvel','test3',NULL),(4,16027014,4.5,'2025-10-15 17:08:48.080698','2025-10-15 17:08:48.080699','마블 좀비영화도 재밌어요!',NULL,NULL,'[추천] Marvel Zombies','test3',NULL),(5,2011109,3.5,'2025-10-15 17:09:19.412757','2025-10-15 17:09:19.412758','믿고보는 마블영화',NULL,NULL,'[추천] Marvel One-Shot: A Funny Thing Happened on the Way to Thor\'s Hammer','test3',NULL),(6,2011118,1.5,'2025-10-15 17:09:53.916570','2025-10-15 17:09:53.916571','무슨 내용인지 모르겠어요..',NULL,NULL,'[추천] Marvel One-Shot: The Consultant','test3',NULL),(7,2247732,3.5,'2025-10-15 17:10:19.344211','2025-10-15 17:10:19.343199','재미있었습니다 ! ',NULL,NULL,'[추천] Marvel One-Shot: Item 47','test3',NULL);
/*!40000 ALTER TABLE `board_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmark`
--

DROP TABLE IF EXISTS `bookmark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmark` (
  `bookmark_id` int NOT NULL AUTO_INCREMENT,
  `movie_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`bookmark_id`),
  UNIQUE KEY `uk_bookmark_user_movie` (`user_id`,`movie_id`),
  KEY `idx_bookmark_user` (`user_id`),
  KEY `idx_bookmark_movie` (`movie_id`),
  CONSTRAINT `FKk8ut4pe182f0j78ufarsuelcd` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`movie_id`),
  CONSTRAINT `FKo4vbqvq5trl11d85bqu5kl870` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_idx`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmark`
--

LOCK TABLES `bookmark` WRITE;
/*!40000 ALTER TABLE `bookmark` DISABLE KEYS */;
INSERT INTO `bookmark` VALUES (12,NULL,NULL),(13,NULL,NULL),(14,NULL,NULL),(15,NULL,NULL),(1,NULL,1),(2,NULL,1),(3,NULL,1),(4,NULL,1),(5,NULL,1),(6,NULL,1),(7,NULL,1),(8,NULL,1),(11,NULL,1),(9,NULL,2),(10,NULL,2);
/*!40000 ALTER TABLE `bookmark` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movie`
--

DROP TABLE IF EXISTS `movie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie` (
  `movie_id` int NOT NULL AUTO_INCREMENT,
  `rating_avg` double DEFAULT NULL,
  `release_date` datetime(6) NOT NULL,
  `movie_actors` varchar(255) DEFAULT NULL,
  `movie_age` varchar(255) DEFAULT NULL,
  `movie_country` varchar(255) DEFAULT NULL,
  `movie_description` varchar(255) NOT NULL,
  `movie_director` varchar(255) NOT NULL,
  `movie_genre` varchar(255) NOT NULL,
  `movie_time` varchar(255) DEFAULT NULL,
  `movie_title` varchar(255) NOT NULL,
  `poster_url` varchar(255) NOT NULL,
  `sub_top_img_url` varchar(255) NOT NULL,
  PRIMARY KEY (`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie`
--

LOCK TABLES `movie` WRITE;
/*!40000 ALTER TABLE `movie` DISABLE KEYS */;
/*!40000 ALTER TABLE `movie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reply`
--

DROP TABLE IF EXISTS `reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reply` (
  `reply_id` int NOT NULL AUTO_INCREMENT,
  `board_id` int NOT NULL,
  `contents` varchar(255) DEFAULT NULL,
  `create_date` datetime(6) DEFAULT NULL,
  `update_date` datetime(6) DEFAULT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`reply_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reply`
--

LOCK TABLES `reply` WRITE;
/*!40000 ALTER TABLE `reply` DISABLE KEYS */;
INSERT INTO `reply` VALUES (1,1,'캡틴 마블 영화의 댓글','2025-10-14 14:14:17.230966',NULL,'test2'),(2,1,'두번째 댓글','2025-10-14 14:19:33.263901',NULL,'test2'),(3,1,'세번째 댓글','2025-10-14 14:56:41.817769',NULL,'test2'),(4,2,'캡틴 마블영화 게시글 댓글','2025-10-14 15:57:57.991542',NULL,'test2'),(9,4,'재미써용','2025-10-14 16:37:27.026009',NULL,'test1'),(10,4,'ㅇㅇㅇㅇ..','2025-10-14 16:52:35.856918',NULL,'test1'),(11,5,'저도 재밌게 봤어요 ~','2025-10-15 17:11:02.866105',NULL,'test2'),(12,6,'이 작품은 안보는게 나을거 같네요','2025-10-15 17:11:44.172053',NULL,'test2'),(13,7,'히히','2025-10-15 17:12:59.425983',NULL,'test1'),(14,7,'희희','2025-10-15 17:13:06.643391',NULL,'test1'),(15,7,'히힛','2025-10-15 17:13:11.578709',NULL,'test1'),(16,7,':D','2025-10-15 17:13:33.885025',NULL,'test1'),(17,5,':D','2025-10-15 17:13:53.631243',NULL,'test1'),(18,6,':ㅇ','2025-10-15 17:14:06.622460',NULL,'test1');
/*!40000 ALTER TABLE `reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `status` int DEFAULT NULL,
  `user_idx` int NOT NULL AUTO_INCREMENT,
  `gender` varchar(255) NOT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) NOT NULL,
  `user_img` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) NOT NULL,
  `user_pw` varchar(255) NOT NULL,
  `role` varbinary(255) DEFAULT NULL,
  PRIMARY KEY (`user_idx`),
  UNIQUE KEY `UK6efs5vmce86ymf5q7lmvn2uuf` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (NULL,1,'Male','test@test.com','test1',NULL,'테스터','1234',NULL),(NULL,2,'Female','test2@test.com','test2',NULL,'테스터2','1234',NULL),(NULL,3,'Male','','',NULL,'','',NULL),(NULL,4,'Male','test3@test.com','test3',NULL,'테스터3','1234',NULL),(NULL,5,'Male','test5@test.com','test5',NULL,'테스터5','1234',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-15 22:40:23
