-- MySQL dump 10.13  Distrib 8.0.42, for macos15 (arm64)
--
-- Host: 58.239.58.243    Database: java502_team2_final_db
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
-- Table structure for table `agency`
--

DROP TABLE IF EXISTS `agency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agency` (
  `ag_key` int NOT NULL AUTO_INCREMENT,
  `ag_code` tinyint NOT NULL DEFAULT '3',
  `ag_name` varchar(100) NOT NULL,
  `ag_ceo` varchar(50) NOT NULL,
  `ag_id` varchar(50) NOT NULL,
  `ag_pw` varchar(255) NOT NULL,
  `ag_address` varchar(255) DEFAULT NULL,
  `ag_zip` varchar(20) DEFAULT NULL,
  `ag_phone` varchar(20) DEFAULT NULL,
  `ag_email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ag_key`),
  UNIQUE KEY `ag_id` (`ag_id`),
  UNIQUE KEY `ag_email` (`ag_email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agency`
--

LOCK TABLES `agency` WRITE;
/*!40000 ALTER TABLE `agency` DISABLE KEYS */;
INSERT INTO `agency` VALUES (1,3,'찬우명가','정찬우','agency','$2a$10$3iqs/tccGRkjE0mBP2YucODRJfAG58ZtbSbCfc3m4A6U6ON55KZ4O','부산 사상구 가야대로 1 1234','46990','01012345678','agency@agency.com'),(7,3,'미니허브가야1호점','김가야','gaya','$2a$10$mo74P88R29VgUywR5e4hMOmROpE8XUeyhKbAEt3vAzg78BUkAKCrK','부산 부산진구 가야대로 406 12-2','47319','01012345678','zzzfire11@gmail.com'),(9,3,'부산대리점1호점','김부산','kimbu','$2a$10$eJ20eZz4nNvdLWOYKcZ6z.35lPex1K7pZOs5TzJ1ARl76dWnvSnr6','부산 강서구 경전철로 28 12-2','46719','01-2234-5409','busan@gmail.com');
/*!40000 ALTER TABLE `agency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agencyorder`
--

DROP TABLE IF EXISTS `agencyorder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agencyorder` (
  `or_key` int NOT NULL AUTO_INCREMENT,
  `ag_key` int NOT NULL,
  `pd_key` int DEFAULT NULL,
  `dv_key` int DEFAULT NULL,
  `or_status` varchar(50) NOT NULL,
  `or_products` varchar(1000) DEFAULT NULL,
  `or_quantity` int NOT NULL,
  `or_total` bigint NOT NULL,
  `or_date` date NOT NULL DEFAULT (curdate()),
  `or_reserve` date NOT NULL,
  `or_gu` varchar(100) NOT NULL,
  `or_price` int NOT NULL,
  `order_number` varchar(20) NOT NULL,
  `dv_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`or_key`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `fk_order_agency` (`ag_key`),
  KEY `fk_order_product` (`pd_key`),
  KEY `fk_order_delivery` (`dv_key`),
  CONSTRAINT `fk_order_agency` FOREIGN KEY (`ag_key`) REFERENCES `agency` (`ag_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_delivery` FOREIGN KEY (`dv_key`) REFERENCES `delivery` (`dv_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_product` FOREIGN KEY (`pd_key`) REFERENCES `product` (`pd_key`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=355 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agencyorder`
--

LOCK TABLES `agencyorder` WRITE;
/*!40000 ALTER TABLE `agencyorder` DISABLE KEYS */;
INSERT INTO `agencyorder` VALUES (333,1,NULL,4,'배송완료','까르보불닭볶음면, 꼬꼬면, 너구리, 무파마',24,36000,'2025-10-15','2025-10-18','부산',6000,'25101501','이영희'),(334,7,NULL,10,'배송완료','까르보불닭볶음면, 꼬꼬면, 너구리, 무파마, 배홍동비빔면',25,37000,'2025-10-15','2025-10-18','부산',7400,'25101502','강동원'),(341,1,NULL,NULL,'승인 대기중','까르보불닭볶음면, 꼬꼬면',30,0,'2025-10-15','2025-10-18','부산',3300,'25101503',NULL),(342,1,NULL,NULL,'승인 대기중','까르보불닭볶음면, 꼬꼬면',10,16500,'2025-10-15','2025-10-19','부산',3300,'25101504',NULL),(347,1,NULL,NULL,'승인 대기중','꼬꼬면, 무파마',2,3100,'2025-10-15','2025-10-19','부산',3100,'25101505',NULL),(348,1,NULL,NULL,'승인 대기중','해물안성탕면, 틈새라면',0,0,'2025-10-15','2025-10-14','부산',3300,'25101506',NULL),(349,7,NULL,NULL,'승인 대기중','배홍동비빔면',10,14000,'2025-10-15','2025-10-14','부산',1400,'25101507',NULL),(350,9,NULL,NULL,'승인 대기중','까르보불닭볶음면, 꼬꼬면, 너구리',3,0,'2025-10-15','2025-10-19','부산',4700,'25101508',NULL),(351,9,NULL,1,'배송완료','까르보불닭볶음면, 꼬꼬면, 너구리',3,4700,'2025-10-15','2025-10-19','부산',4700,'25101509','홍길동'),(352,7,NULL,NULL,'승인 대기중','배홍동비빔면, 무파마, 너구리, 꼬꼬면, 까르보불닭볶음면',50,74000,'2025-10-15','2025-10-14','부산',7400,'25101510',NULL),(353,7,NULL,NULL,'승인 대기중','배홍동비빔면',5,7000,'2025-10-15','2025-10-14','부산',1400,'25101511',NULL),(354,7,NULL,NULL,'승인 대기중','배홍동비빔면',20,28000,'2025-10-15','2025-10-14','부산',1400,'25101512',NULL);
/*!40000 ALTER TABLE `agencyorder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agencyorder_item`
--

DROP TABLE IF EXISTS `agencyorder_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agencyorder_item` (
  `oi_key` int NOT NULL AUTO_INCREMENT,
  `or_key` int NOT NULL,
  `pd_key` int NOT NULL,
  `oi_products` varchar(100) NOT NULL,
  `oi_price` int NOT NULL,
  `oi_quantity` int NOT NULL,
  `or_delivery` tinyint(1) DEFAULT NULL,
  `oi_total` int GENERATED ALWAYS AS ((`oi_price` * `oi_quantity`)) STORED,
  PRIMARY KEY (`oi_key`),
  KEY `fk_item_order` (`or_key`),
  KEY `fk_item_product` (`pd_key`),
  CONSTRAINT `fk_item_order` FOREIGN KEY (`or_key`) REFERENCES `agencyorder` (`or_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_item_product` FOREIGN KEY (`pd_key`) REFERENCES `product` (`pd_key`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=858 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agencyorder_item`
--

LOCK TABLES `agencyorder_item` WRITE;
/*!40000 ALTER TABLE `agencyorder_item` DISABLE KEYS */;
INSERT INTO `agencyorder_item` (`oi_key`, `or_key`, `pd_key`, `oi_products`, `oi_price`, `oi_quantity`, `or_delivery`) VALUES (807,333,1,'까르보불닭볶음면',1500,6,0),(808,333,2,'꼬꼬면',1800,6,0),(809,333,3,'너구리',1400,6,0),(810,333,4,'무파마',1300,6,0),(811,334,1,'까르보불닭볶음면',1500,5,0),(812,334,2,'꼬꼬면',1800,5,0),(813,334,3,'너구리',1400,5,0),(814,334,4,'무파마',1300,5,0),(815,334,5,'배홍동비빔면',1400,5,0),(828,341,1,'까르보불닭볶음면',1500,10,0),(829,341,2,'꼬꼬면',1800,20,0),(830,342,1,'까르보불닭볶음면',1500,5,0),(831,342,2,'꼬꼬면',1800,5,0),(840,347,2,'꼬꼬면',1800,1,0),(841,347,4,'무파마',1300,1,0),(842,348,28,'해물안성탕면',1500,0,0),(843,348,26,'틈새라면',1800,0,0),(844,349,5,'배홍동비빔면',1400,10,0),(845,350,1,'까르보불닭볶음면',1500,1,0),(846,350,2,'꼬꼬면',1800,1,0),(847,350,3,'너구리',1400,1,0),(848,351,1,'까르보불닭볶음면',1500,1,0),(849,351,2,'꼬꼬면',1800,1,0),(850,351,3,'너구리',1400,1,0),(851,352,5,'배홍동비빔면',1400,10,0),(852,352,4,'무파마',1300,10,0),(853,352,3,'너구리',1400,10,0),(854,352,2,'꼬꼬면',1800,10,0),(855,352,1,'까르보불닭볶음면',1500,10,0),(856,353,5,'배홍동비빔면',1400,5,0),(857,354,5,'배홍동비빔면',1400,20,0);
/*!40000 ALTER TABLE `agencyorder_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agencyproduct`
--

DROP TABLE IF EXISTS `agencyproduct`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agencyproduct` (
  `ap_key` int NOT NULL AUTO_INCREMENT,
  `pd_key` int NOT NULL,
  `ag_key` int NOT NULL,
  `ap_store` date NOT NULL,
  `stock` int NOT NULL,
  PRIMARY KEY (`ap_key`),
  UNIQUE KEY `unique_agency_product` (`pd_key`,`ag_key`),
  KEY `fk_agencyproduct_agency` (`ag_key`),
  CONSTRAINT `fk_agencyproduct_agency` FOREIGN KEY (`ag_key`) REFERENCES `agency` (`ag_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_agencyproduct_product` FOREIGN KEY (`pd_key`) REFERENCES `product` (`pd_key`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agencyproduct`
--

LOCK TABLES `agencyproduct` WRITE;
/*!40000 ALTER TABLE `agencyproduct` DISABLE KEYS */;
INSERT INTO `agencyproduct` VALUES (80,1,1,'2025-10-10',9),(81,2,1,'2025-10-10',12),(82,3,1,'2025-10-10',12),(83,4,1,'2025-10-10',8),(84,5,1,'2025-10-10',2),(85,6,1,'2025-10-10',2),(86,7,1,'2025-10-10',2),(87,8,1,'2025-10-10',2),(88,9,1,'2025-10-10',2),(89,10,1,'2025-10-10',2),(93,11,1,'2025-10-10',2),(95,12,1,'2025-10-10',2),(97,13,1,'2025-10-10',2),(98,14,1,'2025-10-10',2),(101,15,1,'2025-10-10',2),(110,16,1,'2025-10-10',2),(111,17,1,'2025-10-10',2),(112,18,1,'2025-10-10',2),(113,19,1,'2025-10-10',2),(115,20,1,'2025-10-10',2),(119,21,1,'2025-10-10',2),(121,22,1,'2025-10-10',2),(124,23,1,'2025-10-10',2),(127,24,1,'2025-10-10',2),(128,25,1,'2025-10-10',2),(131,26,1,'2025-10-10',2),(133,27,1,'2025-10-10',2),(134,28,1,'2025-10-10',2),(170,29,1,'2025-10-13',0),(172,31,1,'2025-10-13',0),(180,1,7,'2025-10-14',10),(181,2,7,'2025-10-14',10),(182,3,7,'2025-10-14',10),(183,4,7,'2025-10-14',10),(184,5,7,'2025-10-14',10),(185,1,9,'2025-10-15',1),(186,2,9,'2025-10-15',1),(187,3,9,'2025-10-15',1);
/*!40000 ALTER TABLE `agencyproduct` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery`
--

DROP TABLE IF EXISTS `delivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery` (
  `dv_key` int NOT NULL AUTO_INCREMENT,
  `dv_name` varchar(50) NOT NULL,
  `dv_car` varchar(20) NOT NULL,
  `dv_phone` varchar(20) DEFAULT NULL,
  `dv_status` varchar(100) NOT NULL,
  `dv_delivery` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`dv_key`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery`
--

LOCK TABLES `delivery` WRITE;
/*!40000 ALTER TABLE `delivery` DISABLE KEYS */;
INSERT INTO `delivery` VALUES (1,'홍길동','12가3456','010-1234-5678','대기중',1),(2,'22222','12가3456','010-1234-5678','대기중',1),(3,'김철수','12가3456','010-1111-2222','대기중',1),(4,'이영희','34나5678','010-2222-3333','대기중',1),(5,'박민수','56다7890','010-3333-4444','대기중',1),(6,'최지훈','78라1234','010-4444-5555','대기중',1),(7,'한서준','90마5678','010-5555-6666','대기중',1),(8,'오은지','11바2345','010-6666-7777','대기중',1),(9,'정하늘','22사6789','010-7777-8888','대기중',1),(10,'강동원','33아4567','010-8888-9999','대기중',1),(11,'유재석','44자1234','010-9999-0000','대기중',1),(12,'신민아','55차6789','010-0000-1111','대기중',1);
/*!40000 ALTER TABLE `delivery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `head`
--

DROP TABLE IF EXISTS `head`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `head` (
  `hd_key` int NOT NULL AUTO_INCREMENT,
  `hd_code` tinyint NOT NULL DEFAULT '1',
  `hd_auth` varchar(255) DEFAULT NULL,
  `hd_name` varchar(255) DEFAULT NULL,
  `hd_id` varchar(255) DEFAULT NULL,
  `hd_pw` varchar(255) DEFAULT NULL,
  `hd_phone` varchar(255) DEFAULT NULL,
  `hd_email` varchar(255) DEFAULT NULL,
  `hd_profile` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`hd_key`),
  UNIQUE KEY `hd_id` (`hd_id`),
  UNIQUE KEY `hd_email` (`hd_email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `head`
--

LOCK TABLES `head` WRITE;
/*!40000 ALTER TABLE `head` DISABLE KEYS */;
INSERT INTO `head` VALUES (1,1,'부장','관리자','admin','$2a$10$YZi3OjmkXmeMN2xsuryCpu.PGPbGhxyugzGH0YkTRyv4tHFyp467i','01012341234','rojh9018@naver.com','c755a370-909d-4033-bd8f-1f9dae49da91_qwer2.jpeg'),(5,1,'사원','김철수','kim0123','$2a$10$Yvnkt0vZ5kV8lM72x2k9nebBhLui/Nk7JnmcB0/1WrD2IcmV5JbfG','01012345678','zzzfire10@gmail.com','cd298b8a-9df0-409c-a91a-56d29976cf4b_cat05.jpeg');
/*!40000 ALTER TABLE `head` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logistic`
--

DROP TABLE IF EXISTS `logistic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logistic` (
  `lg_key` int NOT NULL AUTO_INCREMENT,
  `lg_code` tinyint NOT NULL DEFAULT '2',
  `lg_name` varchar(255) DEFAULT NULL,
  `lg_ceo` varchar(255) DEFAULT NULL,
  `lg_id` varchar(255) DEFAULT NULL,
  `lg_pw` varchar(255) NOT NULL,
  `lg_address` varchar(255) DEFAULT NULL,
  `lg_zip` varchar(255) DEFAULT NULL,
  `lg_phone` varchar(255) DEFAULT NULL,
  `lg_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`lg_key`),
  UNIQUE KEY `lg_id` (`lg_id`),
  UNIQUE KEY `lg_email` (`lg_email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logistic`
--

LOCK TABLES `logistic` WRITE;
/*!40000 ALTER TABLE `logistic` DISABLE KEYS */;
INSERT INTO `logistic` VALUES (1,2,'부산푸드물류','김부산','busan','$2a$10$clN2LByIvxPmfBrxS/QGle4lrCPUMdiXXk8JuAuIsxz6SwGPnkul6','부산 강서구 경등중앙길 159-8 .','46717','051-123-4567','busan@foodlogi.com'),(5,2,'대구델리푸드익스프레스','정대구','daegu','$2a$10$Dqrf2daot38XbDpP12okuuUKh922.NTjax4UyZxGfKSQzSufgKf1C','대구 군위군 효령면 간동유원지길 5 .','43126','053-567-8901','daegu@foodlogi.com'),(7,2,'서울프레시로지스','이서울','seoul','$2a$10$AIS8DQ/qZoXHT6epCZI9jugOHK9n.cJSpvDStIfOzfb5nUJ3/D/8G','서울 강남구 가로수길 5 502-1','06035','02-234-5678','seoul@foodlogi.com'),(8,2,'울산그린푸드센터','박울산','ulsan','$2a$10$34HgdIT0YJNPKYn8Uag.3uiQUWf7vWMI5TG8IDqnMKnw6vg5WPgXG','울산 남구 강남로 11 391','44679','052-345-6789','ulsan@foodlogi.com'),(9,2,'강원프레시푸드물류','최강원','gangwon','$2a$10$s8wWS1G3cetgdGUsoW67pOfWA2Z59iGFJnljJSwnM52r2jPjWVInS','강원특별자치도 강릉시 사천면 가마골길 10 502-2','25436','033-456-7890','gangwon@foodlogi.com');
/*!40000 ALTER TABLE `logistic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logisticproduct`
--

DROP TABLE IF EXISTS `logisticproduct`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logisticproduct` (
  `lp_key` int NOT NULL AUTO_INCREMENT,
  `pd_key` int NOT NULL,
  `lg_key` int NOT NULL,
  `lp_store` date DEFAULT NULL,
  `lp_delivery` date DEFAULT NULL,
  `stock` int NOT NULL,
  PRIMARY KEY (`lp_key`),
  KEY `fk_logisticproduct_product` (`pd_key`),
  KEY `fk_logisticproduct_logistic` (`lg_key`),
  CONSTRAINT `fk_logisticproduct_logistic` FOREIGN KEY (`lg_key`) REFERENCES `logistic` (`lg_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_logisticproduct_product` FOREIGN KEY (`pd_key`) REFERENCES `product` (`pd_key`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1316 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logisticproduct`
--

LOCK TABLES `logisticproduct` WRITE;
/*!40000 ALTER TABLE `logisticproduct` DISABLE KEYS */;
INSERT INTO `logisticproduct` VALUES (210,1,1,NULL,NULL,72),(214,1,5,NULL,NULL,100),(215,2,1,NULL,NULL,83),(219,2,5,NULL,NULL,99),(220,3,1,NULL,NULL,83),(224,3,5,NULL,NULL,99),(225,4,1,'2025-10-13',NULL,88),(229,4,5,NULL,NULL,0),(230,5,1,NULL,NULL,94),(234,5,5,NULL,NULL,0),(235,6,1,NULL,NULL,99),(239,6,5,NULL,NULL,0),(240,7,1,NULL,NULL,49),(244,7,5,NULL,NULL,0),(245,8,1,NULL,NULL,49),(249,8,5,NULL,NULL,0),(250,9,1,NULL,NULL,99),(254,9,5,NULL,NULL,0),(255,10,1,NULL,NULL,49),(259,10,5,NULL,NULL,0),(260,11,1,NULL,NULL,49),(264,11,5,NULL,NULL,0),(265,12,1,NULL,NULL,29),(269,12,5,NULL,NULL,0),(270,13,1,NULL,NULL,49),(274,13,5,NULL,NULL,0),(275,14,1,NULL,NULL,99),(279,14,5,NULL,NULL,0),(280,15,1,NULL,NULL,29),(284,15,5,NULL,NULL,0),(285,16,1,NULL,NULL,49),(289,16,5,NULL,NULL,0),(290,17,1,NULL,NULL,99),(294,17,5,NULL,NULL,0),(295,18,1,NULL,NULL,49),(299,18,5,NULL,NULL,0),(300,19,1,NULL,NULL,99),(304,19,5,NULL,NULL,0),(305,20,1,NULL,NULL,99),(309,20,5,NULL,NULL,0),(310,21,1,NULL,NULL,49),(314,21,5,NULL,NULL,0),(315,22,1,NULL,NULL,49),(319,22,5,NULL,NULL,0),(320,23,1,NULL,NULL,99),(324,23,5,NULL,NULL,0),(325,24,1,NULL,NULL,49),(329,24,5,NULL,NULL,0),(330,25,1,NULL,NULL,199),(334,25,5,NULL,NULL,0),(335,26,1,NULL,NULL,49),(339,26,5,NULL,NULL,0),(340,27,1,NULL,NULL,49),(344,27,5,NULL,NULL,0),(345,28,1,NULL,NULL,49),(349,28,5,NULL,NULL,0),(350,29,1,NULL,NULL,100),(354,29,5,NULL,NULL,0),(355,30,1,NULL,NULL,100),(359,30,5,NULL,NULL,0),(360,31,1,NULL,NULL,100),(364,31,5,NULL,NULL,0),(365,32,1,NULL,NULL,100),(369,32,5,NULL,NULL,0),(370,33,1,NULL,NULL,100),(374,33,5,NULL,NULL,0),(375,34,1,NULL,NULL,100),(379,34,5,NULL,NULL,0),(380,35,1,NULL,NULL,100),(384,35,5,NULL,NULL,0),(385,36,1,NULL,NULL,100),(389,36,5,NULL,NULL,0),(390,37,1,NULL,NULL,100),(394,37,5,NULL,NULL,0),(395,38,1,NULL,NULL,100),(399,38,5,NULL,NULL,0),(400,39,1,NULL,NULL,100),(404,39,5,NULL,NULL,0),(405,40,1,NULL,NULL,100),(409,40,5,NULL,NULL,0),(410,41,1,NULL,NULL,100),(414,41,5,NULL,NULL,0),(415,42,1,NULL,NULL,100),(419,42,5,NULL,NULL,0),(420,43,1,NULL,NULL,100),(424,43,5,NULL,NULL,0),(425,44,1,NULL,NULL,100),(429,44,5,NULL,NULL,0),(430,45,1,NULL,NULL,50),(434,45,5,NULL,NULL,0),(435,46,1,NULL,NULL,50),(439,46,5,NULL,NULL,0),(440,47,1,NULL,NULL,50),(444,47,5,NULL,NULL,0),(445,48,1,NULL,NULL,50),(449,48,5,NULL,NULL,0),(450,49,1,NULL,NULL,50),(454,49,5,NULL,NULL,0),(455,50,1,NULL,NULL,50),(459,50,5,NULL,NULL,0),(460,51,1,NULL,NULL,100),(464,51,5,NULL,NULL,0),(465,52,1,NULL,NULL,50),(469,52,5,NULL,NULL,0),(470,53,1,NULL,NULL,50),(474,53,5,NULL,NULL,0),(475,54,1,NULL,NULL,50),(479,54,5,NULL,NULL,0),(480,55,1,NULL,NULL,50),(484,55,5,NULL,NULL,0),(485,56,1,NULL,NULL,50),(489,56,5,NULL,NULL,0),(490,57,1,NULL,NULL,50),(494,57,5,NULL,NULL,0),(495,58,1,NULL,NULL,50),(499,58,5,NULL,NULL,0),(500,59,1,NULL,NULL,50),(504,59,5,NULL,NULL,0),(505,60,1,NULL,NULL,50),(509,60,5,NULL,NULL,0),(510,61,1,NULL,NULL,200),(514,61,5,NULL,NULL,0),(515,62,1,NULL,NULL,100),(519,62,5,NULL,NULL,0),(520,63,1,NULL,NULL,100),(524,63,5,NULL,NULL,0),(525,64,1,NULL,NULL,100),(529,64,5,NULL,NULL,0),(530,65,1,NULL,NULL,100),(534,65,5,NULL,NULL,0),(535,66,1,NULL,NULL,100),(539,66,5,NULL,NULL,0),(540,67,1,NULL,NULL,99),(544,67,5,NULL,NULL,0),(545,68,1,NULL,NULL,100),(549,68,5,NULL,NULL,0),(550,69,1,NULL,NULL,100),(554,69,5,NULL,NULL,0),(555,70,1,NULL,NULL,100),(559,70,5,NULL,NULL,0),(560,71,1,NULL,NULL,100),(564,71,5,NULL,NULL,0),(565,72,1,NULL,NULL,100),(569,72,5,NULL,NULL,0),(570,73,1,NULL,NULL,100),(574,73,5,NULL,NULL,0),(575,74,1,NULL,NULL,100),(579,74,5,NULL,NULL,0),(580,75,1,NULL,NULL,100),(584,75,5,NULL,NULL,0),(585,76,1,NULL,NULL,100),(589,76,5,NULL,NULL,0),(590,77,1,NULL,NULL,100),(594,77,5,NULL,NULL,0),(595,78,1,NULL,NULL,100),(599,78,5,NULL,NULL,0),(600,79,1,NULL,NULL,100),(604,79,5,NULL,NULL,0),(605,80,1,NULL,NULL,100),(609,80,5,NULL,NULL,0),(610,81,1,NULL,NULL,100),(614,81,5,NULL,NULL,0),(615,82,1,NULL,NULL,100),(619,82,5,NULL,NULL,0),(620,83,1,NULL,NULL,100),(624,83,5,NULL,NULL,0),(625,84,1,NULL,NULL,100),(629,84,5,NULL,NULL,0),(630,85,1,NULL,NULL,100),(634,85,5,NULL,NULL,0),(638,87,1,NULL,NULL,100),(644,87,5,NULL,NULL,0),(655,90,1,NULL,NULL,100),(659,90,5,NULL,NULL,0),(660,91,1,NULL,NULL,50),(664,91,5,NULL,NULL,0),(665,92,1,NULL,NULL,30),(669,92,5,NULL,NULL,0),(670,93,1,NULL,NULL,30),(674,93,5,NULL,NULL,0),(675,94,1,NULL,NULL,30),(679,94,5,NULL,NULL,0),(680,95,1,NULL,NULL,100),(684,95,5,NULL,NULL,0),(685,96,1,NULL,NULL,100),(689,96,5,NULL,NULL,0),(690,97,1,NULL,NULL,100),(694,97,5,NULL,NULL,0),(695,98,1,NULL,NULL,100),(699,98,5,NULL,NULL,0),(700,99,1,NULL,NULL,100),(704,99,5,NULL,NULL,0),(705,100,1,NULL,NULL,100),(709,100,5,NULL,NULL,0),(710,101,1,NULL,NULL,100),(714,101,5,NULL,NULL,0),(715,102,1,NULL,NULL,100),(719,102,5,NULL,NULL,0),(720,103,1,NULL,NULL,100),(724,103,5,NULL,NULL,0),(725,104,1,NULL,NULL,100),(729,104,5,NULL,NULL,0),(730,105,1,NULL,NULL,100),(734,105,5,NULL,NULL,0),(735,106,1,NULL,NULL,100),(739,106,5,NULL,NULL,0),(740,107,1,NULL,NULL,100),(744,107,5,NULL,NULL,0),(745,108,1,NULL,NULL,100),(749,108,5,NULL,NULL,0),(750,109,1,'2025-10-13',NULL,50),(754,109,5,NULL,NULL,0),(755,110,1,NULL,NULL,100),(759,110,5,NULL,NULL,0),(760,111,1,NULL,NULL,100),(764,111,5,NULL,NULL,0),(765,112,1,NULL,NULL,100),(769,112,5,NULL,NULL,0),(770,113,1,'2025-10-13',NULL,100),(774,113,5,NULL,NULL,0),(775,114,1,NULL,NULL,100),(779,114,5,NULL,NULL,0),(780,115,1,NULL,NULL,100),(784,115,5,NULL,NULL,0),(785,116,1,'2025-10-13',NULL,50),(789,116,5,NULL,NULL,0),(790,117,1,'2025-10-13',NULL,50),(794,117,5,NULL,NULL,0),(926,1,7,NULL,NULL,0),(927,2,7,NULL,NULL,0),(928,3,7,NULL,NULL,0),(929,4,7,NULL,NULL,0),(930,5,7,NULL,NULL,0),(931,6,7,NULL,NULL,0),(932,7,7,NULL,NULL,0),(933,8,7,NULL,NULL,0),(934,9,7,NULL,NULL,0),(935,10,7,NULL,NULL,0),(936,11,7,NULL,NULL,0),(937,12,7,NULL,NULL,0),(938,13,7,NULL,NULL,0),(939,14,7,NULL,NULL,0),(940,15,7,NULL,NULL,0),(941,16,7,NULL,NULL,0),(942,17,7,NULL,NULL,0),(943,18,7,NULL,NULL,0),(944,19,7,NULL,NULL,0),(945,20,7,NULL,NULL,0),(946,21,7,NULL,NULL,0),(947,22,7,NULL,NULL,0),(948,23,7,NULL,NULL,0),(949,24,7,NULL,NULL,0),(950,25,7,NULL,NULL,0),(951,26,7,NULL,NULL,0),(952,27,7,NULL,NULL,0),(953,28,7,NULL,NULL,0),(954,29,7,NULL,NULL,0),(955,30,7,NULL,NULL,0),(956,31,7,NULL,NULL,0),(957,32,7,NULL,NULL,0),(958,33,7,NULL,NULL,0),(959,34,7,NULL,NULL,0),(960,35,7,NULL,NULL,0),(961,36,7,NULL,NULL,0),(962,37,7,NULL,NULL,0),(963,38,7,NULL,NULL,0),(964,39,7,NULL,NULL,0),(965,40,7,NULL,NULL,0),(966,41,7,NULL,NULL,0),(967,42,7,NULL,NULL,0),(968,43,7,NULL,NULL,0),(969,44,7,NULL,NULL,0),(970,45,7,NULL,NULL,0),(971,46,7,NULL,NULL,0),(972,47,7,NULL,NULL,0),(973,48,7,NULL,NULL,0),(974,49,7,NULL,NULL,0),(975,50,7,NULL,NULL,0),(976,51,7,NULL,NULL,0),(977,52,7,NULL,NULL,0),(978,53,7,NULL,NULL,0),(979,54,7,NULL,NULL,0),(980,55,7,NULL,NULL,0),(981,56,7,NULL,NULL,0),(982,57,7,NULL,NULL,0),(983,58,7,NULL,NULL,0),(984,59,7,NULL,NULL,0),(985,60,7,NULL,NULL,0),(986,61,7,NULL,NULL,0),(987,62,7,NULL,NULL,0),(988,63,7,NULL,NULL,0),(989,64,7,NULL,NULL,0),(990,65,7,NULL,NULL,0),(991,66,7,NULL,NULL,0),(992,67,7,NULL,NULL,0),(993,68,7,NULL,NULL,0),(994,69,7,NULL,NULL,0),(995,70,7,NULL,NULL,0),(996,71,7,NULL,NULL,0),(997,72,7,NULL,NULL,0),(998,73,7,NULL,NULL,0),(999,74,7,NULL,NULL,0),(1000,75,7,NULL,NULL,0),(1001,76,7,NULL,NULL,0),(1002,77,7,NULL,NULL,0),(1003,78,7,NULL,NULL,0),(1004,79,7,NULL,NULL,0),(1005,80,7,NULL,NULL,0),(1006,81,7,NULL,NULL,0),(1007,82,7,NULL,NULL,0),(1008,83,7,NULL,NULL,0),(1009,84,7,NULL,NULL,0),(1010,85,7,NULL,NULL,0),(1011,87,7,NULL,NULL,0),(1012,90,7,NULL,NULL,0),(1013,91,7,NULL,NULL,0),(1014,92,7,NULL,NULL,0),(1015,93,7,NULL,NULL,0),(1016,94,7,NULL,NULL,0),(1017,95,7,NULL,NULL,0),(1018,96,7,NULL,NULL,0),(1019,97,7,NULL,NULL,0),(1020,98,7,NULL,NULL,0),(1021,99,7,NULL,NULL,0),(1022,100,7,NULL,NULL,0),(1023,101,7,NULL,NULL,0),(1024,102,7,NULL,NULL,0),(1025,103,7,NULL,NULL,0),(1026,104,7,NULL,NULL,0),(1027,105,7,NULL,NULL,0),(1028,106,7,NULL,NULL,0),(1029,107,7,NULL,NULL,0),(1030,108,7,NULL,NULL,0),(1031,109,7,NULL,NULL,0),(1032,110,7,NULL,NULL,0),(1033,111,7,NULL,NULL,0),(1034,112,7,NULL,NULL,0),(1035,113,7,NULL,NULL,0),(1036,114,7,NULL,NULL,0),(1037,115,7,NULL,NULL,0),(1038,116,7,NULL,NULL,0),(1039,117,7,NULL,NULL,0),(1053,1,8,NULL,NULL,0),(1054,2,8,NULL,NULL,0),(1055,3,8,NULL,NULL,0),(1056,4,8,NULL,NULL,0),(1057,5,8,NULL,NULL,0),(1058,6,8,NULL,NULL,0),(1059,7,8,NULL,NULL,0),(1060,8,8,NULL,NULL,0),(1061,9,8,NULL,NULL,0),(1062,10,8,NULL,NULL,0),(1063,11,8,NULL,NULL,0),(1064,12,8,NULL,NULL,0),(1065,13,8,NULL,NULL,0),(1066,14,8,NULL,NULL,0),(1067,15,8,NULL,NULL,0),(1068,16,8,NULL,NULL,0),(1069,17,8,NULL,NULL,0),(1070,18,8,NULL,NULL,0),(1071,19,8,NULL,NULL,0),(1072,20,8,NULL,NULL,0),(1073,21,8,NULL,NULL,0),(1074,22,8,NULL,NULL,0),(1075,23,8,NULL,NULL,0),(1076,24,8,NULL,NULL,0),(1077,25,8,NULL,NULL,0),(1078,26,8,NULL,NULL,0),(1079,27,8,NULL,NULL,0),(1080,28,8,NULL,NULL,0),(1081,29,8,NULL,NULL,0),(1082,30,8,NULL,NULL,0),(1083,31,8,NULL,NULL,0),(1084,32,8,NULL,NULL,0),(1085,33,8,NULL,NULL,0),(1086,34,8,NULL,NULL,0),(1087,35,8,NULL,NULL,0),(1088,36,8,NULL,NULL,0),(1089,37,8,NULL,NULL,0),(1090,38,8,NULL,NULL,0),(1091,39,8,NULL,NULL,0),(1092,40,8,NULL,NULL,0),(1093,41,8,NULL,NULL,0),(1094,42,8,NULL,NULL,0),(1095,43,8,NULL,NULL,0),(1096,44,8,NULL,NULL,0),(1097,45,8,NULL,NULL,0),(1098,46,8,NULL,NULL,0),(1099,47,8,NULL,NULL,0),(1100,48,8,NULL,NULL,0),(1101,49,8,NULL,NULL,0),(1102,50,8,NULL,NULL,0),(1103,51,8,NULL,NULL,0),(1104,52,8,NULL,NULL,0),(1105,53,8,NULL,NULL,0),(1106,54,8,NULL,NULL,0),(1107,55,8,NULL,NULL,0),(1108,56,8,NULL,NULL,0),(1109,57,8,NULL,NULL,0),(1110,58,8,'2025-10-14',NULL,100),(1111,59,8,NULL,NULL,0),(1112,60,8,'2025-10-14',NULL,10),(1113,61,8,'2025-10-14',NULL,100),(1114,62,8,NULL,NULL,0),(1115,63,8,'2025-10-14',NULL,100),(1116,64,8,'2025-10-14',NULL,30),(1117,65,8,NULL,NULL,0),(1118,66,8,NULL,NULL,0),(1119,67,8,NULL,NULL,0),(1120,68,8,NULL,NULL,0),(1121,69,8,NULL,NULL,0),(1122,70,8,NULL,NULL,0),(1123,71,8,NULL,NULL,0),(1124,72,8,NULL,NULL,0),(1125,73,8,NULL,NULL,0),(1126,74,8,NULL,NULL,0),(1127,75,8,NULL,NULL,0),(1128,76,8,NULL,NULL,0),(1129,77,8,NULL,NULL,0),(1130,78,8,NULL,NULL,0),(1131,79,8,NULL,NULL,0),(1132,80,8,NULL,NULL,0),(1133,81,8,NULL,NULL,0),(1134,82,8,NULL,NULL,0),(1135,83,8,NULL,NULL,0),(1136,84,8,NULL,NULL,0),(1137,85,8,NULL,NULL,0),(1138,87,8,NULL,NULL,0),(1139,90,8,NULL,NULL,0),(1140,91,8,NULL,NULL,0),(1141,92,8,NULL,NULL,0),(1142,93,8,NULL,NULL,0),(1143,94,8,NULL,NULL,0),(1144,95,8,NULL,NULL,0),(1145,96,8,NULL,NULL,0),(1146,97,8,NULL,NULL,0),(1147,98,8,NULL,NULL,0),(1148,99,8,NULL,NULL,0),(1149,100,8,NULL,NULL,0),(1150,101,8,NULL,NULL,0),(1151,102,8,NULL,NULL,0),(1152,103,8,NULL,NULL,0),(1153,104,8,NULL,NULL,0),(1154,105,8,NULL,NULL,0),(1155,106,8,NULL,NULL,0),(1156,107,8,NULL,NULL,0),(1157,108,8,NULL,NULL,0),(1158,109,8,NULL,NULL,0),(1159,110,8,NULL,NULL,0),(1160,111,8,NULL,NULL,0),(1161,112,8,NULL,NULL,0),(1162,113,8,NULL,NULL,0),(1163,114,8,NULL,NULL,0),(1164,115,8,NULL,NULL,0),(1165,116,8,NULL,NULL,0),(1166,117,8,NULL,NULL,0),(1180,1,9,NULL,NULL,0),(1181,2,9,NULL,NULL,0),(1182,3,9,NULL,NULL,0),(1183,4,9,NULL,NULL,0),(1184,5,9,NULL,NULL,0),(1185,6,9,NULL,NULL,0),(1186,7,9,NULL,NULL,0),(1187,8,9,NULL,NULL,0),(1188,9,9,NULL,NULL,0),(1189,10,9,NULL,NULL,0),(1190,11,9,NULL,NULL,0),(1191,12,9,NULL,NULL,0),(1192,13,9,NULL,NULL,0),(1193,14,9,NULL,NULL,0),(1194,15,9,NULL,NULL,0),(1195,16,9,NULL,NULL,0),(1196,17,9,NULL,NULL,0),(1197,18,9,NULL,NULL,0),(1198,19,9,NULL,NULL,0),(1199,20,9,NULL,NULL,0),(1200,21,9,NULL,NULL,0),(1201,22,9,NULL,NULL,0),(1202,23,9,NULL,NULL,0),(1203,24,9,NULL,NULL,0),(1204,25,9,NULL,NULL,0),(1205,26,9,NULL,NULL,0),(1206,27,9,NULL,NULL,0),(1207,28,9,NULL,NULL,0),(1208,29,9,NULL,NULL,0),(1209,30,9,NULL,NULL,0),(1210,31,9,NULL,NULL,0),(1211,32,9,NULL,NULL,0),(1212,33,9,NULL,NULL,0),(1213,34,9,NULL,NULL,0),(1214,35,9,NULL,NULL,0),(1215,36,9,NULL,NULL,0),(1216,37,9,NULL,NULL,0),(1217,38,9,NULL,NULL,0),(1218,39,9,NULL,NULL,0),(1219,40,9,NULL,NULL,0),(1220,41,9,NULL,NULL,0),(1221,42,9,NULL,NULL,0),(1222,43,9,NULL,NULL,0),(1223,44,9,NULL,NULL,0),(1224,45,9,NULL,NULL,0),(1225,46,9,NULL,NULL,0),(1226,47,9,NULL,NULL,0),(1227,48,9,NULL,NULL,0),(1228,49,9,NULL,NULL,0),(1229,50,9,NULL,NULL,0),(1230,51,9,NULL,NULL,0),(1231,52,9,NULL,NULL,0),(1232,53,9,NULL,NULL,0),(1233,54,9,NULL,NULL,0),(1234,55,9,NULL,NULL,0),(1235,56,9,NULL,NULL,0),(1236,57,9,NULL,NULL,0),(1237,58,9,NULL,NULL,0),(1238,59,9,NULL,NULL,0),(1239,60,9,NULL,NULL,0),(1240,61,9,NULL,NULL,0),(1241,62,9,NULL,NULL,0),(1242,63,9,NULL,NULL,0),(1243,64,9,NULL,NULL,0),(1244,65,9,NULL,NULL,0),(1245,66,9,NULL,NULL,0),(1246,67,9,NULL,NULL,0),(1247,68,9,NULL,NULL,0),(1248,69,9,NULL,NULL,0),(1249,70,9,NULL,NULL,0),(1250,71,9,NULL,NULL,0),(1251,72,9,NULL,NULL,0),(1252,73,9,NULL,NULL,0),(1253,74,9,NULL,NULL,0),(1254,75,9,NULL,NULL,0),(1255,76,9,NULL,NULL,0),(1256,77,9,NULL,NULL,0),(1257,78,9,NULL,NULL,0),(1258,79,9,NULL,NULL,0),(1259,80,9,NULL,NULL,0),(1260,81,9,NULL,NULL,0),(1261,82,9,NULL,NULL,0),(1262,83,9,NULL,NULL,0),(1263,84,9,NULL,NULL,0),(1264,85,9,NULL,NULL,0),(1265,87,9,NULL,NULL,0),(1266,90,9,NULL,NULL,0),(1267,91,9,NULL,NULL,0),(1268,92,9,NULL,NULL,0),(1269,93,9,NULL,NULL,0),(1270,94,9,NULL,NULL,0),(1271,95,9,NULL,NULL,0),(1272,96,9,NULL,NULL,0),(1273,97,9,NULL,NULL,0),(1274,98,9,NULL,NULL,0),(1275,99,9,NULL,NULL,0),(1276,100,9,NULL,NULL,0),(1277,101,9,NULL,NULL,0),(1278,102,9,NULL,NULL,0),(1279,103,9,NULL,NULL,0),(1280,104,9,NULL,NULL,0),(1281,105,9,NULL,NULL,0),(1282,106,9,NULL,NULL,0),(1283,107,9,NULL,NULL,0),(1284,108,9,NULL,NULL,0),(1285,109,9,NULL,NULL,0),(1286,110,9,NULL,NULL,0),(1287,111,9,NULL,NULL,0),(1288,112,9,NULL,NULL,0),(1289,113,9,NULL,NULL,0),(1290,114,9,NULL,NULL,0),(1291,115,9,NULL,NULL,0),(1292,116,9,NULL,NULL,0),(1293,117,9,NULL,NULL,0);
/*!40000 ALTER TABLE `logisticproduct` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logisticstore`
--

DROP TABLE IF EXISTS `logisticstore`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logisticstore` (
  `st_key` int NOT NULL AUTO_INCREMENT,
  `pd_key` int NOT NULL,
  `lg_key` int NOT NULL,
  `lp_key` int NOT NULL,
  `st_store` int NOT NULL,
  `store_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`st_key`),
  KEY `fk_logisticstore_product` (`pd_key`),
  KEY `fk_logisticstore_logistic` (`lg_key`),
  KEY `fk_logisticstore_logisticproduct` (`lp_key`),
  CONSTRAINT `fk_logisticstore_logistic` FOREIGN KEY (`lg_key`) REFERENCES `logistic` (`lg_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_logisticstore_logisticproduct` FOREIGN KEY (`lp_key`) REFERENCES `logisticproduct` (`lp_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_logisticstore_product` FOREIGN KEY (`pd_key`) REFERENCES `product` (`pd_key`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logisticstore`
--

LOCK TABLES `logisticstore` WRITE;
/*!40000 ALTER TABLE `logisticstore` DISABLE KEYS */;
/*!40000 ALTER TABLE `logisticstore` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice` (
  `nt_code` int NOT NULL,
  `nt_key` int NOT NULL AUTO_INCREMENT,
  `at_created` datetime(6) DEFAULT NULL,
  `at_updated` datetime(6) DEFAULT NULL,
  `nt_category` varchar(255) NOT NULL,
  `nt_content` text NOT NULL,
  `start_date` date NOT NULL DEFAULT (curdate()),
  `end_date` date NOT NULL DEFAULT ((curdate() + interval 2 month)),
  PRIMARY KEY (`nt_key`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
INSERT INTO `notice` VALUES (1,8,'2025-09-23 15:19:02.723170','2025-09-24 03:08:26.910919','배송','수정 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다. 전체 테스트 중입니다.111 전체 테스트 중입니다.111 전체 테스트 중입니다. ','2025-09-25','2025-11-25'),(2,13,'2025-09-24 05:01:40.561273','2025-09-24 05:01:40.561273','배송','물류 공지사항 테스트 중입니다.','2025-09-25','2025-11-25'),(2,30,'2025-10-02 07:07:36.606774','2025-10-02 07:07:36.606774','출고','물류 공지사항 테스트1 중입니다.','2025-10-02','2025-12-02'),(0,32,'2025-10-10 10:21:37.004084','2025-10-11 21:10:57.296000','배송','공지사항 테스트 중입니다.','2025-10-11','2025-12-10'),(0,33,'2025-10-10 10:21:37.046231','2025-10-11 11:50:39.379184','출고','공지사항 테스트 중입니다. 공지사항 테스트 중입니다. 공지사항 테스트 중입니다. 공지사항 테스트 중입니다. 공지사항 테스트 중입니다. 공지사항 테스트 중입니다. 공지사항 테스트 중입니다. 공지사항 테스트 중입니다. 공지사항 테스트 중입니다.','2025-10-11','2025-12-10'),(3,34,'2025-10-10 10:21:37.075617','2025-10-10 13:06:19.030467','제품현황','공지사항 테스트 중입니다.','2025-10-10','2025-12-10'),(2,35,'2025-10-10 10:22:26.085406','2025-10-10 10:36:13.898194','주문','미니허브5호점 14일 배송일을 오늘 배송합니다.','2025-10-10','2025-12-10'),(3,36,'2025-10-12 10:14:43.093019','2025-10-12 10:14:43.093019','배송','2025년 10월 12일 오늘 발송 예정이었던 상품이 2025년 10월 13일 내일 발송되오니, 영업에 차질없으시길 바랍니다.','2025-10-12','2025-12-12'),(1,39,'2025-10-13 11:41:51.385927','2025-10-13 11:43:04.036776','주문','금일 주문 건은 모두 확정되었으나, 일부 상품은 판매량 급증으로 인해 생산이 지연되고 있습니다.\n이에 따라 해당 상품의 배송이 다소 늦어질 예정이오니, 업무에 참고하여 주시기 바랍니다.','2025-10-13','2025-10-31'),(0,40,'2025-10-13 15:04:19.699597','2025-10-13 15:04:19.699597','출고','2025년 10월 10일 주문 건에 대하여 모두 출고 완료되었습니다.\n업무에 참고하여 주시기 바랍니다.','2025-10-13','2025-12-13');
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `pd_key` int NOT NULL AUTO_INCREMENT,
  `pd_category` varchar(50) NOT NULL,
  `pd_num` varchar(50) NOT NULL,
  `pd_products` varchar(100) NOT NULL,
  `pd_price` int NOT NULL,
  `pd_image` varchar(500) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `pd_stock` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`pd_key`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'라면류','R0001','까르보불닭볶음면',1500,'/uploads/product/1760324651974_1.jpeg','2025-10-10 13:56:43.812721',0),(2,'라면류','R0002','꼬꼬면',1800,'/uploads/product/1760324657089_2.jpeg','2025-10-10 13:57:07.737705',0),(3,'라면류','R0003','너구리',1400,'/uploads/product/1760324663253_3.jpeg','2025-10-10 13:57:34.256214',0),(4,'라면류','R0004','무파마',1300,'/uploads/product/1760324669616_4.jpeg','2025-10-10 13:57:45.406597',0),(5,'라면류','R0005','배홍동비빔면',1400,'/uploads/product/1760324674209_5.jpeg','2025-10-10 13:57:56.830177',0),(6,'라면류','R0006','배홍동칼빔면',1800,'/uploads/product/1760324678301_6.jpeg','2025-10-10 13:58:09.766320',0),(7,'라면류','R0007','볶음간짬뽕',1600,'/uploads/product/1760324683682_7.jpeg','2025-10-10 13:58:23.292473',0),(8,'라면류','R0008','볶음너구리',1800,'/uploads/product/1760324688640_8.jpeg','2025-10-10 13:58:37.508339',0),(9,'라면류','R0009','불닭볶음면',1500,'/uploads/product/1760324699321_9.jpeg','2025-10-10 13:58:58.835706',0),(10,'라면류','R0010','사천짜파게티',1800,'/uploads/product/1760324704810_10.jpeg','2025-10-10 13:59:13.874953',0),(11,'라면류','R0011','삼양라면',1200,'/uploads/product/1760324710266_11.jpeg','2025-10-10 13:59:25.162625',0),(12,'라면류','R0012','순하군안성탕면',1300,'/uploads/product/1760324714836_12.jpeg','2025-10-10 13:59:39.320559',0),(13,'라면류','R0013','스낵면',1000,'/uploads/product/1760324722318_13.jpeg','2025-10-10 13:59:51.853700',0),(14,'라면류','R0014','신라면',1600,'/uploads/product/1760324726971_14.jpeg','2025-10-10 14:00:03.690420',0),(15,'라면류','R0015','신라면더레드',2100,'/uploads/product/1760324731284_15.jpeg','2025-10-10 14:00:16.706542',0),(16,'라면류','R0016','신라면블랙',2500,'/uploads/product/1760324736291_16.jpeg','2025-10-10 14:00:28.422265',0),(17,'라면류','R0017','안성탕면',1300,'/uploads/product/1760324740395_17.jpeg','2025-10-10 14:00:43.400970',0),(18,'라면류','R0018','열라면',1600,'/uploads/product/1760324744273_18.jpeg','2025-10-10 14:00:57.815969',0),(19,'라면류','R0019','오징어짬뽕',1600,'/uploads/product/1760324748539_19.jpeg','2025-10-10 14:01:10.828088',0),(20,'라면류','R0020','진라면매운맛',1600,'/uploads/product/1760324753141_20.jpeg','2025-10-10 14:01:27.409424',0),(21,'라면류','R0021','진라면순한맛',1600,'/uploads/product/1760324758461_21.jpeg','2025-10-10 14:01:40.369320',0),(22,'라면류','R0022','진비빔면',1600,'/uploads/product/1760324763168_22.jpeg','2025-10-10 14:01:58.969978',0),(23,'라면류','R0023','짜파게티',1800,'/uploads/product/1760324768525_23.jpeg','2025-10-10 14:02:16.033266',0),(24,'라면류','R0024','참깨라면',1700,'/uploads/product/1760324774271_24.jpeg','2025-10-10 14:02:32.711152',0),(25,'라면류','R0025','치즈불닭볶음면',1500,'/uploads/product/1760324779356_25.jpeg','2025-10-10 14:02:56.644202',0),(26,'라면류','R0026','틈새라면',1800,'/uploads/product/1760324785580_26.jpeg','2025-10-10 14:03:08.207025',0),(27,'라면류','R0027','팔도비빔면',1400,'/uploads/product/1760324790179_27.jpeg','2025-10-10 14:03:20.258657',0),(28,'라면류','R0028','해물안성탕면',1500,'/uploads/product/1760324794666_28.jpeg','2025-10-10 14:03:37.481396',0),(29,'즉석식품류','F0001','비비고왕교자',4000,'/uploads/product/1760324806917_1.jpeg','2025-10-13 08:45:01.769436',0),(30,'즉석식품류','F0002','비비고진한고기만두',4400,'/uploads/product/1760324811238_2.jpeg','2025-10-13 08:45:25.590988',0),(31,'즉석식품류','F0003','비비고찐만두',4200,'/uploads/product/1760324815651_3.jpeg','2025-10-13 08:45:39.572558',0),(32,'즉석식품류','F0004','비비고통새우만두',4400,'/uploads/product/1760324823270_4.jpeg','2025-10-13 08:45:54.407181',0),(33,'즉석식품류','F0005','오뚜기뚝배기불고기덮밥',3300,'/uploads/product/1760324827943_5.jpeg','2025-10-13 08:46:35.985363',0),(34,'즉석식품류','F0006','오뚜기밥',1500,'/uploads/product/1760324833295_6.jpeg','2025-10-13 08:47:19.951449',0),(35,'즉석식품류','F0007','오뚜기불닭마요덮밥',3300,'/uploads/product/1760324836665_7.jpeg','2025-10-13 08:47:39.805315',0),(36,'즉석식품류','F0008','오뚜기진한쇠고기미역국밥',3300,'/uploads/product/1760324840599_8.jpeg','2025-10-13 08:47:52.520793',0),(37,'즉석식품류','F0009','오뚜기참치마요덮밥',3300,'/uploads/product/1760324844782_9.jpeg','2025-10-13 08:48:28.942471',0),(38,'즉석식품류','F0010','cj김치날치알밥',3300,'/uploads/product/1760324849830_10.jpeg','2025-10-13 08:48:40.982037',0),(39,'즉석식품류','F0011','cj미역국밥',3300,'/uploads/product/1760324854127_11.jpeg','2025-10-13 08:48:55.861941',0),(40,'즉석식품류','F0012','cj불닭마요덮밥',3300,'/uploads/product/1760324859253_12.jpeg','2025-10-13 08:49:08.784430',0),(41,'즉석식품류','F0013','cj직화불고기덮밥',3300,'/uploads/product/1760324864126_13.jpeg','2025-10-13 08:49:21.699802',0),(42,'즉석식품류','F0014','cj참치마요덮밥',3300,'/uploads/product/1760324870855_14.jpeg','2025-10-13 08:49:41.202042',0),(43,'즉석식품류','F0015','cj햇반',1500,'/uploads/product/1760324874953_15.jpeg','2025-10-13 08:49:55.001725',0),(44,'즉석식품류','F0016','오뚜기톡톡김치알밥',3300,'/uploads/product/1760324878444_16.jpeg','2025-10-13 08:54:54.726024',0),(45,'과자류','S0001','꽃게랑',1500,'/uploads/product/1760324340113_1.jpeg','2025-10-13 08:55:36.760587',0),(46,'과자류','S0002','꽃게랑고추냉이',1700,'/uploads/product/1760324352119_2.jpeg','2025-10-13 08:55:58.362710',0),(47,'과자류','S0003','눈을감자',2200,'/uploads/product/1760324359075_3.jpeg','2025-10-13 08:58:39.699281',0),(48,'과자류','S0004','맛동산',2400,'/uploads/product/1760324364626_4.jpeg','2025-10-13 08:58:52.500372',0),(49,'과자류','S0005','매운새우깡',1500,'/uploads/product/1760324371738_5.jpeg','2025-10-13 08:59:02.518350',0),(50,'과자류','S0006','벌집핏자',1400,'/uploads/product/1760324379737_6.jpeg','2025-10-13 08:59:12.047941',0),(51,'과자류','S0007','새우깡',1500,'/uploads/product/1760324388090_7.jpeg','2025-10-13 08:59:24.411577',0),(52,'과자류','S0008','새우깡와사비',1700,'/uploads/product/1760324393933_8.jpeg','2025-10-13 08:59:40.090916',0),(53,'과자류','S0009','스모키베이컨칩',1800,'/uploads/product/1760324400519_9.jpeg','2025-10-13 08:59:52.635432',0),(54,'과자류','S0010','야채타임',1600,'/uploads/product/1760324405413_10.jpeg','2025-10-13 09:00:05.310918',0),(55,'과자류','S0011','오감자감자그라랑맛',1900,'/uploads/product/1760324410741_11.jpeg','2025-10-13 09:00:20.172016',0),(56,'과자류','S0012','오감자딥',1900,'/uploads/product/1760324416586_12.jpeg','2025-10-13 09:00:33.537405',0),(57,'과자류','S0013','오감자크리미칠리소스맛',2100,'/uploads/product/1760324423176_13.webp','2025-10-13 09:00:50.479182',0),(58,'과자류','S0014','쫄병스낵매콤한맛',1800,'/uploads/product/1760325076342_14.jpeg','2025-10-13 09:01:06.273658',0),(59,'과자류','S0015','쫄병스낵숯불바베큐맛',1800,'/uploads/product/1760325082074_15.jpeg','2025-10-13 09:01:20.534274',0),(60,'과자류','S0016','쫄병스낵짜파게티맛',1800,'/uploads/product/1760324435535_16.jpeg','2025-10-13 09:01:34.803662',0),(61,'과자류','S0017','칸쵸',2000,'/uploads/product/1760324440556_17.jpeg','2025-10-13 09:01:46.563122',0),(62,'과자류','S0018','포카칩스윗치즈맛',2300,'/uploads/product/1760324468268_18.jpeg','2025-10-13 09:02:17.498549',0),(63,'과자류','S0019','포카칩어니언맛',2300,'/uploads/product/1760324474463_19.jpeg','2025-10-13 09:02:33.259717',0),(64,'과자류','S0020','포카칩오리지널',2300,'/uploads/product/1760324479275_20.jpeg','2025-10-13 09:02:51.523131',0),(65,'음료류','D0001','밀키스250ml',1800,'/uploads/product/1760324890826_1.jpeg','2025-10-13 09:05:33.438276',0),(66,'음료류','D0002','밀키스340ml',2400,'/uploads/product/1760324895070_2.jpeg','2025-10-13 09:06:03.094010',0),(67,'음료류','D0003','밀키스제로250ml',1800,'/uploads/product/1760324899502_3.jpeg','2025-10-13 09:06:28.504282',0),(68,'음료류','D0004','밀키스제로딸기바나나250ml',1800,'/uploads/product/1760324905013_4.jpg','2025-10-13 09:06:45.260668',0),(69,'음료류','D0005','스프라이트250ml',1800,'/uploads/product/1760324909914_5.png','2025-10-13 09:07:00.162369',0),(70,'음료류','D0006','스프라이트355ml',2400,'/uploads/product/1760324913370_6.jpeg','2025-10-13 09:07:17.713234',0),(71,'음료류','D0007','스프라이트제로칠355ml',2400,'/uploads/product/1760324917364_7.jpeg','2025-10-13 09:07:33.116935',0),(72,'음료류','D0008','칠성사이다250ml',1800,'/uploads/product/1760324921531_8.jpeg','2025-10-13 09:07:52.122510',0),(73,'음료류','D0009','칠성사이다355ml',2400,'/uploads/product/1760324925816_9.jpeg','2025-10-13 09:08:06.741466',0),(74,'음료류','D0010','칠성사이다제로250ml',1800,'/uploads/product/1760324930192_10.jpeg','2025-10-13 09:08:20.768832',0),(75,'음료류','D0011','칠성사이다제로355ml',2400,'/uploads/product/1760324934782_11.jpeg','2025-10-13 09:08:35.783111',0),(76,'음료류','D0012','코카콜라250ml',1800,'/uploads/product/1760324943307_12.jpeg','2025-10-13 09:09:02.803895',0),(77,'음료류','D0013','코카콜라355ml',2400,'/uploads/product/1760324946957_13.jpeg','2025-10-13 09:09:17.288995',0),(78,'음료류','D0014','코카콜라제로250ml',1800,'/uploads/product/1760324950515_14.jpeg','2025-10-13 09:09:33.723209',0),(79,'음료류','D0015','코카콜라제로355ml',2400,'/uploads/product/1760324954822_15.jpeg','2025-10-13 09:09:50.277981',0),(80,'음료류','D0016','펩시콜라250ml',1800,'/uploads/product/1760324959433_16.jpeg','2025-10-13 09:10:06.090899',0),(81,'음료류','D0017','펩시콜라355ml',2400,'/uploads/product/1760324963498_17.jpeg','2025-10-13 09:10:21.547945',0),(82,'음료류','D0018','펩시콜라제로250ml',1800,'/uploads/product/1760324967355_18.jpeg','2025-10-13 09:10:33.354259',0),(83,'음료류','D0019','펩시콜라제로355ml',2400,'/uploads/product/1760324972051_19.jpeg','2025-10-13 09:10:49.773185',0),(84,'음료류','D0020','환타오렌지250ml',1800,'/uploads/product/1760324975333_20.jpeg','2025-10-13 09:11:13.728771',0),(85,'음료류','D0021','환타오렌지355ml',2400,'/uploads/product/1760324978940_21.jpeg','2025-10-13 09:11:25.904044',0),(87,'음료류','D0022','환타파인애플250ml',1800,'/uploads/product/1760324982858_22.jpeg','2025-10-13 09:11:48.868410',0),(90,'음료류','D0023','환타파인애플355ml',2400,'/uploads/product/1760324986346_23.jpeg','2025-10-13 09:12:25.709279',0),(91,'빵류','B0001','롯데미니샌드땅콩크림',2000,'/uploads/product/1760324996136_1.jpeg','2025-10-13 09:12:49.094606',0),(92,'빵류','B0002','연세우유단팥생크림빵',3000,'/uploads/product/1760325000308_2.jpeg','2025-10-13 09:13:29.403265',0),(93,'빵류','B0003','연세우유레드벨벳생크림빵',3000,'/uploads/product/1760325004744_3.jpeg','2025-10-13 09:13:47.842699',0),(94,'빵류','B0004','연세우유옥수수생크림빵',3000,'/uploads/product/1760325008119_4.jpeg','2025-10-13 09:14:02.437118',0),(95,'빵류','B0005','연세우유생크림빵',3000,'/uploads/product/1760325012106_5.jpeg','2025-10-13 09:14:15.719099',0),(96,'빵류','B0006','연세우유초코생크림빵',3000,'/uploads/product/1760325016501_6.jpeg','2025-10-13 09:14:26.964905',0),(97,'빵류','B0007','연세우유쿠키앤크림빵',3000,'/uploads/product/1760325021196_7.jpeg','2025-10-13 09:14:39.168873',0),(98,'빵류','B0008','연세우유한라봉생크림빵',3000,'/uploads/product/1760325027204_8.jpeg','2025-10-13 09:15:07.000007',0),(99,'빵류','B0009','정통크림빵',1500,'/uploads/product/1760325031344_9.jpeg','2025-10-13 09:15:42.541214',0),(100,'빵류','B0010','포켓몬빵돌아온고오스초코케익',2000,'/uploads/product/1760325035009_10.jpeg','2025-10-13 09:16:00.456689',0),(101,'빵류','B0011','포켓몬빵돌아온로켓단초코볼',2000,'/uploads/product/1760325038919_11.jpeg','2025-10-13 09:16:17.356768',0),(102,'빵류','B0012','포켓몬빵파이리의화르륵핫소스빵',2000,'/uploads/product/1760325043661_12.jpeg','2025-10-13 09:16:31.176430',0),(103,'생활용품','L0001','2080치약',2000,'/uploads/product/1760325102950_1.jpeg','2025-10-13 09:16:50.240694',0),(104,'생활용품','L0002','2080치약칫솔세트',3000,'/uploads/product/1760325107910_2.jpg','2025-10-13 09:17:16.291130',0),(105,'생활용품','L0003','2080칫솔',1600,'/uploads/product/1760325112882_3.jpg','2025-10-13 09:17:27.154619',0),(106,'생활용품','L0004','깨끗한나라물티슈',2500,'/uploads/product/1760325116854_4.jpg','2025-10-13 09:17:40.503328',0),(107,'생활용품','L0005','도루코페이스4일회용면도기',3000,'/uploads/product/1760325121242_5.jpg','2025-10-13 09:17:55.034476',0),(108,'생활용품','L0006','리뉴후레쉬',4000,'/uploads/product/1760325125043_6.jpeg','2025-10-13 09:18:08.068218',0),(109,'생활용품','L0007','여행용티슈',1100,'/uploads/product/1760325129158_7.jpeg','2025-10-13 09:18:19.663083',0),(110,'생활용품','L0008','오랄비칫솔',1800,'/uploads/product/1760325132735_8.png','2025-10-13 09:18:42.692082',0),(111,'생활용품','L0009','질레트블루심플3일회용면도기',2200,'/uploads/product/1760325138420_9.jpg','2025-10-13 09:19:04.586713',0),(112,'생활용품','L0010','콜게이트치약',2500,'/uploads/product/1760325141643_10.jpeg','2025-10-13 09:19:18.537398',0),(113,'생활용품','L0011','페리오치약',2000,'/uploads/product/1760325147285_11.jpeg','2025-10-13 09:19:32.839776',0),(114,'생활용품','L0012','페브리즈다우니향',3600,'/uploads/product/1760325151683_12.jpg','2025-10-13 09:19:45.319236',0),(115,'생활용품','L0013','페브리즈라벤더유칼립투스향',3600,'/uploads/product/1760325155434_13.avif','2025-10-13 09:20:01.941348',0),(116,'생활용품','L0014','페브리즈상쾌한향',3600,'/uploads/product/1760325158539_14.jpg','2025-10-13 09:20:13.105051',0),(117,'생활용품','L0015','페브리즈프레시클린향',3600,'/uploads/product/1760325162640_15.jpg','2025-10-13 09:20:27.999539',0);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ready`
--

DROP TABLE IF EXISTS `ready`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ready` (
  `rd_key` int NOT NULL AUTO_INCREMENT,
  `ag_key` int DEFAULT NULL,
  `pd_key` int NOT NULL,
  `rd_status` varchar(50) DEFAULT NULL,
  `rd_products` varchar(100) NOT NULL,
  `rd_quantity` int NOT NULL,
  `rd_price` int NOT NULL,
  `rd_total` int NOT NULL,
  `rd_date` date NOT NULL DEFAULT (curdate()),
  `rd_reserve` date DEFAULT NULL,
  `rd_price_current` int DEFAULT NULL,
  `rd_price_changed` bit(1) NOT NULL DEFAULT b'0',
  `rd_created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `rd_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`rd_key`),
  KEY `fk_ready_agency` (`ag_key`),
  KEY `fk_ready_product` (`pd_key`),
  CONSTRAINT `fk_ready_product` FOREIGN KEY (`pd_key`) REFERENCES `product` (`pd_key`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=588 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ready`
--

LOCK TABLES `ready` WRITE;
/*!40000 ALTER TABLE `ready` DISABLE KEYS */;
/*!40000 ALTER TABLE `ready` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reset_token`
--

DROP TABLE IF EXISTS `reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reset_token` (
  `token_id` int NOT NULL AUTO_INCREMENT,
  `user_type` tinyint NOT NULL,
  `user_id` int NOT NULL,
  `token` varchar(500) NOT NULL,
  `expire_at` datetime NOT NULL,
  `used` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`token_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reset_token`
--

LOCK TABLES `reset_token` WRITE;
/*!40000 ALTER TABLE `reset_token` DISABLE KEYS */;
INSERT INTO `reset_token` VALUES (1,1,3,'6730bee4-2e64-4061-a27b-21a597853a00','2025-09-24 02:09:38',0,'2025-09-24 01:39:38'),(2,1,3,'98e8e929-f59d-40dd-bb50-57b818c37e17','2025-09-24 02:09:40',0,'2025-09-24 01:39:40'),(3,1,3,'5a70805e-d34a-4513-99a7-5903334d2ae1','2025-09-24 02:09:40',0,'2025-09-24 01:39:40'),(4,1,3,'7003cb3f-6cc1-42a0-8e9c-aeeb2502b39d','2025-09-24 02:09:41',0,'2025-09-24 01:39:41'),(5,1,3,'88d620b7-e7b5-4875-9d74-aeae1bdef201','2025-09-24 02:09:48',0,'2025-09-24 01:39:48'),(6,1,3,'0892c315-cab4-4cfc-9ec6-a551fd55fde6','2025-09-24 02:20:42',0,'2025-09-24 01:50:42'),(7,1,3,'1ebd6229-9612-4eff-9081-2fc4bd431eeb','2025-09-24 02:29:00',1,'2025-09-24 01:59:00'),(8,1,3,'ff179071-9583-4cee-b08d-189ab1b767d1','2025-09-24 02:30:04',1,'2025-09-24 02:00:04'),(9,1,3,'6301524d-f17d-4eef-b9bb-5caa5d96588c','2025-09-25 05:18:42',1,'2025-09-25 04:48:42'),(10,1,3,'5ab176df-2b75-44aa-aee7-42e482eab1ab','2025-09-26 01:46:41',1,'2025-09-26 01:16:41'),(11,1,3,'3a8536d2-7f8b-4454-94e2-55685b81e8fd','2025-09-30 00:30:44',1,'2025-09-30 00:00:44'),(12,1,3,'45a7f1c1-e92d-40d4-99c5-5c6b72f25f00','2025-10-02 01:05:59',1,'2025-10-02 00:35:59'),(13,1,3,'79b1b9a8-ff71-459b-a382-18412b466c60','2025-10-02 06:14:33',1,'2025-10-02 05:44:33'),(14,1,1,'1fb53fff-c52d-4269-abd2-a4faefc347b5','2025-10-13 09:53:46',1,'2025-10-13 09:23:46'),(15,1,3,'6544cb0f-7f5f-4f5b-87f5-76fbe4c855a9','2025-10-14 09:50:23',1,'2025-10-14 09:20:23');
/*!40000 ALTER TABLE `reset_token` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-15 16:22:35
