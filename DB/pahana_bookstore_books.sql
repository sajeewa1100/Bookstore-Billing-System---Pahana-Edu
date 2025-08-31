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
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `id` int NOT NULL AUTO_INCREMENT,
  `isbn` varchar(20) NOT NULL,
  `title` varchar(200) NOT NULL,
  `author` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `cost_price` decimal(10,2) NOT NULL,
  `stock_quantity` int DEFAULT '0',
  `category` varchar(50) DEFAULT NULL,
  `publisher` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `isbn` (`isbn`),
  KEY `idx_books_isbn` (`isbn`),
  KEY `idx_books_title` (`title`),
  KEY `idx_books_author` (`author`)
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'978-955-20-6789','Madol Duwa','Martin Wickramasinghe',850.00,600.00,6,'Literature','Tisara Prakasakayo','2025-08-26 06:28:16','2025-08-28 03:45:47'),(2,'978-955-30-1234','Viragaya','W.A. Silva',950.00,700.00,7,'Literature','Gunasena Publishers','2025-08-26 06:28:16','2025-08-27 17:07:46'),(3,'978-981-23-4567','The Guide','R.K. Narayan',1200.00,900.00,30,'Literature','Penguin Books','2025-08-26 06:28:16','2025-08-26 06:28:16'),(4,'978-0-14-044913','To Kill a Mockingbird','Harper Lee',1650.00,1200.00,7,'Literature','Penguin Classics','2025-08-26 06:28:16','2025-08-30 14:30:01'),(5,'978-955-25-7890','Sinhala Grammar','Prof. J.B. Disanayaka',750.00,550.00,40,'Education','Educational Publications','2025-08-26 06:28:16','2025-08-26 06:28:16'),(6,'978-955-35-2345','Advanced Mathematics Grade 12','Dr. K.A.S. Perera',1400.00,1000.00,36,'Education','Sarasavi Publishers','2025-08-26 06:28:16','2025-08-27 05:24:43'),(7,'978-0-07-352344','Principles of Management','Harold Koontz',2850.00,2200.00,12,'Business','McGraw-Hill Education','2025-08-26 06:28:16','2025-08-26 06:28:16'),(8,'978-955-40-3456','Sri Lankan History','Prof. K.M. de Silva',1350.00,1000.00,22,'History','Vijitha Yapa Publications','2025-08-26 06:28:16','2025-08-27 05:24:30'),(9,'978-0-13-110362','Java Programming','Deitel & Deitel',3500.00,2800.00,9,'Technology','Pearson Education','2025-08-26 06:28:16','2025-08-27 05:24:43'),(10,'978-955-45-4567','Ayurveda Basics','Dr. Anton Jayasuriya',1100.00,800.00,23,'Health','Ayurveda Medical College','2025-08-26 06:28:16','2025-08-28 03:45:47'),(11,'978-0-19-280551','Oxford English Dictionary','Oxford University Press',4500.00,3500.00,5,'Reference','Oxford University Press','2025-08-26 06:28:16','2025-08-26 06:28:16'),(12,'978-955-50-5678','Buddhist Philosophy','Ven. Walpola Rahula',1250.00,900.00,16,'Religion','Buddhist Cultural Centre','2025-08-26 06:28:16','2025-08-27 05:24:30'),(13,'978-955-55-6789','Cooking with Coconut','Chandra Dissanayake',980.00,720.00,28,'Cookbook','Sarasavi Publishers','2025-08-26 06:28:16','2025-08-26 06:28:16'),(14,'978-0-452-28423','1984','George Orwell',1550.00,1150.00,16,'Literature','Penguin Books','2025-08-26 06:28:16','2025-08-28 12:55:51'),(15,'978-955-60-7890','Sri Lankan Birds','Dr. Deepal Warakagoda',2200.00,1650.00,10,'Nature','Field Ornithology Group','2025-08-26 06:28:16','2025-08-26 06:28:16'),(23,'9789555057777','Sajeewa','Rathnayake',1800.00,1000.00,-1,NULL,NULL,'2025-08-28 10:50:35','2025-08-29 04:48:13');
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-30 23:27:58
