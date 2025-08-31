-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: pahana_bookstore
-- ------------------------------------------------------
-- Server version	9.1.0

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
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clients` (
  `id` int NOT NULL AUTO_INCREMENT,
  `account_number` varchar(20) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(15) NOT NULL,
  `address_street` varchar(200) DEFAULT NULL,
  `address_city` varchar(50) DEFAULT NULL,
  `address_state` varchar(50) DEFAULT NULL,
  `address_zip` varchar(10) DEFAULT NULL,
  `send_mail_auto` tinyint(1) DEFAULT '1',
  `loyalty_points` int DEFAULT '0',
  `tier_level` enum('SILVER','GOLD','PLATINUM') DEFAULT 'SILVER',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_number` (`account_number`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_clients_phone` (`phone`),
  KEY `idx_clients_account_number` (`account_number`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
INSERT INTO `clients` VALUES (1,'ACC001','Nimal','Silva','nimal.silva@gmail.com','+94711234567','123, Galle Road','Colombo','Western','00100',1,6759,'GOLD','2025-08-26 06:28:16','2025-08-27 06:31:03'),(2,'ACC002','Kamala','Mendis','kamala.mendis@yahoo.com','+94722345678','45, Kandy Road','Kandy','Central','20000',1,2708,'SILVER','2025-08-26 06:28:16','2025-08-27 07:38:11'),(3,'ACC003','Suresh','Gunasekara','suresh.g@hotmail.com','+94733456789','67, Main Street','Galle','Southern','80000',1,18500,'PLATINUM','2025-08-26 06:28:16','2025-08-26 06:28:16'),(4,'ACC004','Malini','Rajapaksa','malini.raja@gmail.com','+94744567890','89, Temple Road','Negombo','Western','11500',1,1294,'SILVER','2025-08-26 06:28:16','2025-08-27 06:31:03'),(5,'ACC005','Rohitha','Amarasinghe','rohitha.amara@sltnet.lk','+94755678901','234, Hospital Road','Matara','Southern','81000',1,8872,'GOLD','2025-08-26 06:28:16','2025-08-27 05:24:43'),(6,'ACC006','Shanti','Dissanayake','shanti.dissa@gmail.com','+94766789012','56, School Lane','Anuradhapura','North Central','50000',1,750,'SILVER','2025-08-26 06:28:16','2025-08-26 06:28:16'),(7,'ACC007','Arun','Thilaka','pathumpc1998@gmail.com','94777890123',NULL,NULL,NULL,NULL,1,3200,'SILVER','2025-08-26 06:28:16','2025-08-28 13:13:02'),(8,'ACC008','Geethika','Herath','geethika.herath@gmail.com','+94788901234','78, Lake Road','Kurunegala','North Western','60000',1,12755,'PLATINUM','2025-08-26 06:28:16','2025-08-27 05:24:29'),(9,'ACC009','Chaminda','Bandara','chaminda.b@sltnet.lk','+94799012345','90, New Town Road','Ratnapura','Sabaragamuwa','70000',1,450,'SILVER','2025-08-26 06:28:16','2025-08-26 06:28:16'),(10,'ACC010','Dilani','Senanayake','dilani.sena@hotmail.com','+94710123456','123, Hill Street','Badulla','Uva','90000',1,21000,'PLATINUM','2025-08-26 06:28:16','2025-08-26 06:28:16'),(20,'AC00000001','Sajeewa','Rathnayake','pathumpc1100@gmail.com','0743359190',NULL,NULL,NULL,NULL,1,306,'SILVER','2025-08-28 06:45:13','2025-08-30 14:30:01');
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-30 23:27:57
