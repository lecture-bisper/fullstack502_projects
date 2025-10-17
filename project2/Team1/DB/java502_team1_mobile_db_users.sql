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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `phone` varchar(30) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `users_id` varchar(50) NOT NULL,
  `email` varchar(120) NOT NULL,
  `pass` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKpvy7x8bvot7o0sc8bgmdmevxl` (`users_id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'010-4100-2728','hsm','zz448866','test@naver.com','zz448866'),(2,'000-0000-0000','zz','zz','zz','zzzzzz'),(4,'000-0000-0000','zzzz','zzz','zzzz','zzzzzz'),(5,'010-1111-2222','황인수	','insssu','insu@naver.com','1234qwer'),(6,'000-0000-0000','진가연','jin59','jin@naver.com','000000'),(7,'010-1234-5678','홍길동	','hoho','ho123@naver.com','123456'),(8,'010-1234-5678','구원','korea','korea01@naver.com','000000'),(9,'010-1234-5678','심지애','jejulover','jejulover@naver.com','000000'),(10,'010-1234-5678','이혜리	','ktxgo','ktxgo@daum.net','000000'),(11,'010-1234-5678','이아름	','jejuair','jejuair@gmail.com','000000'),(12,'000-0000-0000','zzzz','zzzz','zzzzz','zzzzzz'),(13,'000-0000-0000','aa','aa','aa','aaaaaa'),(14,'000-0000-0000','crescent','crescent','crescent','crescent'),(15,'000-0000-0000','aaa','aaa','aaa','aaaaaa'),(16,'000','채팅','test1','f','000000'),(17,'000-0','dara','dara','dara','000000'),(18,'000','bbb','bb','bbb','000000'),(19,'010-0000-0000','테스트3','test3','test3@naver.com','000000'),(20,'010-0000-0000','테스터','test4','test4@naver.com','000000'),(21,'010-0000-0000','테스터5','test5','test5@gmail.com','000000'),(22,'010-3333-3333','test12','test12345','test12@naver.com','000000'),(23,'000-0000-0000','xxx','xxx','xxx','000000');
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

-- Dump completed on 2025-08-25 10:33:13
