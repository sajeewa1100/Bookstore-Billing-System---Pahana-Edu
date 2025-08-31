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
-- Temporary view structure for view `invoice_summary`
--

DROP TABLE IF EXISTS `invoice_summary`;
/*!50001 DROP VIEW IF EXISTS `invoice_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `invoice_summary` AS SELECT 
 1 AS `id`,
 1 AS `invoice_number`,
 1 AS `invoice_date`,
 1 AS `client_name`,
 1 AS `client_phone`,
 1 AS `tier_level`,
 1 AS `staff_name`,
 1 AS `subtotal`,
 1 AS `loyalty_discount`,
 1 AS `total_amount`,
 1 AS `loyalty_points_earned`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `client_details`
--

DROP TABLE IF EXISTS `client_details`;
/*!50001 DROP VIEW IF EXISTS `client_details`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `client_details` AS SELECT 
 1 AS `id`,
 1 AS `account_number`,
 1 AS `full_name`,
 1 AS `first_name`,
 1 AS `last_name`,
 1 AS `email`,
 1 AS `phone`,
 1 AS `full_address`,
 1 AS `loyalty_points`,
 1 AS `tier_level`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `invoice_item_details`
--

DROP TABLE IF EXISTS `invoice_item_details`;
/*!50001 DROP VIEW IF EXISTS `invoice_item_details`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `invoice_item_details` AS SELECT 
 1 AS `id`,
 1 AS `invoice_id`,
 1 AS `invoice_number`,
 1 AS `book_title`,
 1 AS `book_author`,
 1 AS `isbn`,
 1 AS `quantity`,
 1 AS `unit_price`,
 1 AS `total_price`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `invoice_summary`
--

/*!50001 DROP VIEW IF EXISTS `invoice_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `invoice_summary` AS select `i`.`id` AS `id`,`i`.`invoice_number` AS `invoice_number`,`i`.`invoice_date` AS `invoice_date`,(case when (`c`.`first_name` is not null) then concat(`c`.`first_name`,' ',`c`.`last_name`) else 'Walk-in Customer' end) AS `client_name`,`c`.`phone` AS `client_phone`,`c`.`tier_level` AS `tier_level`,concat(`s`.`first_name`,' ',`s`.`last_name`) AS `staff_name`,`i`.`subtotal` AS `subtotal`,`i`.`loyalty_discount` AS `loyalty_discount`,`i`.`total_amount` AS `total_amount`,`i`.`loyalty_points_earned` AS `loyalty_points_earned`,`i`.`created_at` AS `created_at` from ((`invoices` `i` left join `clients` `c` on((`i`.`client_id` = `c`.`id`))) join `staff` `s` on((`i`.`staff_id` = `s`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `client_details`
--

/*!50001 DROP VIEW IF EXISTS `client_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `client_details` AS select `c`.`id` AS `id`,`c`.`account_number` AS `account_number`,concat(`c`.`first_name`,' ',`c`.`last_name`) AS `full_name`,`c`.`first_name` AS `first_name`,`c`.`last_name` AS `last_name`,`c`.`email` AS `email`,`c`.`phone` AS `phone`,concat(`c`.`address_street`,', ',`c`.`address_city`,', ',`c`.`address_state`,' ',`c`.`address_zip`) AS `full_address`,`c`.`loyalty_points` AS `loyalty_points`,`c`.`tier_level` AS `tier_level`,`c`.`created_at` AS `created_at` from `clients` `c` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `invoice_item_details`
--

/*!50001 DROP VIEW IF EXISTS `invoice_item_details`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `invoice_item_details` AS select `ii`.`id` AS `id`,`ii`.`invoice_id` AS `invoice_id`,`i`.`invoice_number` AS `invoice_number`,`b`.`title` AS `book_title`,`b`.`author` AS `book_author`,`b`.`isbn` AS `isbn`,`ii`.`quantity` AS `quantity`,`ii`.`unit_price` AS `unit_price`,`ii`.`total_price` AS `total_price` from ((`invoice_items` `ii` join `invoices` `i` on((`ii`.`invoice_id` = `i`.`id`))) join `books` `b` on((`ii`.`book_id` = `b`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-30 23:27:58
