-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: ecommerce
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `brands`
--

DROP TABLE IF EXISTS `brands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brands` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `logo_image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mm` varchar(255) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6osclwf271vehfepcym3k1839` (`created_by`),
  KEY `FKglsdaljqdr1pagfew65a8xffc` (`updated_by`),
  KEY `FKld1befo89vttdf713rc6p87dc` (`upload_by`),
  CONSTRAINT `FK6osclwf271vehfepcym3k1839` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKglsdaljqdr1pagfew65a8xffc` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKld1befo89vttdf713rc6p87dc` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brands`
--

LOCK TABLES `brands` WRITE;
/*!40000 ALTER TABLE `brands` DISABLE KEYS */;
INSERT INTO `brands` VALUES (1,'2025-10-15 07:10:17.136000','2025-11-01 18:22:02.539000','Oppo','/ecommerce-images/brands/1/0bab3146-afbf-474a-ba0c-c6e408091bba.png','Oppo','Oppo',1,2,2,NULL),(2,'2025-11-04 17:15:20.010000','2025-11-04 17:34:03.798000',NULL,'/ecommerce-images/brands/2/e2d732d5-ed26-45b2-89b8-881a5f874ded.png','Sample Brand',NULL,NULL,NULL,NULL,2);
/*!40000 ALTER TABLE `brands` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `color_templates`
--

DROP TABLE IF EXISTS `color_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `color_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf0ylue84ec19j54hs4o1diphc` (`created_by`),
  KEY `FKbauc0kkprs2lnj0yllb1kr1i5` (`updated_by`),
  KEY `FKmsrr857hv4hval1wbpkocofvd` (`upload_by`),
  CONSTRAINT `FKbauc0kkprs2lnj0yllb1kr1i5` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKf0ylue84ec19j54hs4o1diphc` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmsrr857hv4hval1wbpkocofvd` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `color_templates`
--

LOCK TABLES `color_templates` WRITE;
/*!40000 ALTER TABLE `color_templates` DISABLE KEYS */;
INSERT INTO `color_templates` VALUES (1,'2025-10-18 17:51:25.585000','2025-10-22 09:42:23.376000','#ff3838','RED',1,1,2,2,NULL),(2,'2025-10-18 17:52:53.589000','2025-10-18 17:52:53.589000','#44bd32','Green',2,1,2,NULL,NULL),(3,'2025-10-18 17:53:15.651000','2025-10-18 17:54:43.731000','#74b9ff','Blue',3,1,2,2,NULL),(4,'2025-10-18 17:53:39.725000','2025-10-18 17:53:39.725000','#f1c40f','Yellow',4,1,2,NULL,NULL),(5,'2025-10-18 17:54:08.896000','2025-10-18 17:54:08.897000','#ffb8b8','Pink',5,1,2,NULL,NULL);
/*!40000 ALTER TABLE `color_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_no` varchar(255) DEFAULT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj5e49hu5k5m8moub9qwh49baa` (`created_by`),
  KEY `FK5fvme5alfjk2i4e4q4n7996ag` (`updated_by`),
  KEY `FKr0wllpnec5qgf2w23j8wdi4qx` (`upload_by`),
  CONSTRAINT `FK5fvme5alfjk2i4e4q4n7996ag` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKj5e49hu5k5m8moub9qwh49baa` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKr0wllpnec5qgf2w23j8wdi4qx` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'2025-12-14 21:03:07.000000',NULL,'2025-12-14','sai@gmail.com','Sai','123456','09123456789',NULL,1,1,NULL,NULL);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `default_image` varchar(255) DEFAULT NULL,
  `image1` varchar(255) DEFAULT NULL,
  `image2` varchar(255) DEFAULT NULL,
  `image3` varchar(255) DEFAULT NULL,
  `image4` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mm` varchar(255) DEFAULT NULL,
  `new_arrival_status` int DEFAULT NULL,
  `original_price` decimal(38,2) DEFAULT NULL,
  `popular_status` int DEFAULT NULL,
  `promotion_status` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `sell_price` decimal(38,2) DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `color_id` bigint DEFAULT NULL,
  `size_id` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKstbwb3fvemcf9j1yf64fey29i` (`created_by`),
  KEY `FKoo6ebdr4sxlll2v1ud97ddbbr` (`updated_by`),
  KEY `FKmtk37pxnx7d5ck7fkq2xcna4i` (`product_id`),
  KEY `FKr673r4ccjp4a27pk5aovsgd7a` (`color_id`),
  KEY `FKiwltr1jswco0vpmwddfvuf04c` (`size_id`),
  KEY `FKgkbqqoey3bjlbnevyub289gvr` (`upload_by`),
  CONSTRAINT `FKgkbqqoey3bjlbnevyub289gvr` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKiwltr1jswco0vpmwddfvuf04c` FOREIGN KEY (`size_id`) REFERENCES `size_template_items` (`id`),
  CONSTRAINT `FKmtk37pxnx7d5ck7fkq2xcna4i` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKoo6ebdr4sxlll2v1ud97ddbbr` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKr673r4ccjp4a27pk5aovsgd7a` FOREIGN KEY (`color_id`) REFERENCES `color_templates` (`id`),
  CONSTRAINT `FKstbwb3fvemcf9j1yf64fey29i` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (3,'2025-11-02 11:23:54.974000','2025-11-02 17:21:22.290000','T01','/ecommerce-images/itemss/3/68c3baed-c7c5-498c-a3ce-48a3363e5d91.png',NULL,NULL,NULL,NULL,'Item 01','အိုင်တမ် ၀၁',1,50000.00,0,0,5,45000.00,1,1,2,2,1,1,2,NULL),(6,'2025-11-02 17:43:02.283000','2025-11-02 17:55:06.324000','T02','/ecommerce-images/itemss/6/f221faee-bcb6-43a1-a9c8-9e2fdc1e583d.jpg','/ecommerce-images/itemss/6/214ea261-6995-4ff1-b56e-2c5febbd5cfb.jpg','/ecommerce-images/itemss/6/56929cf4-21ac-48f8-90f7-c72b66c4920d.png',NULL,NULL,'Item 02','အိုင်တမ် ၀၂',0,5000.00,0,0,2,4000.00,2,1,2,2,1,NULL,NULL,NULL),(7,'2025-11-04 17:15:20.471000','2025-11-04 17:34:04.023000','ITEM_001','/ecommerce-images/items/7/3f4474ce-2a45-4086-96d8-8199ad26ec22.png',NULL,NULL,NULL,NULL,'Sample Item','Sample Item MM',1,99.99,1,1,100,79.99,1,NULL,NULL,NULL,4,NULL,4,2),(8,'2025-11-04 17:15:20.479000','2025-11-04 17:15:20.479000','ITEM_002',NULL,NULL,NULL,NULL,NULL,'Basic Item',NULL,NULL,NULL,NULL,NULL,50,29.99,NULL,NULL,NULL,NULL,5,NULL,NULL,2);
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_categories`
--

DROP TABLE IF EXISTS `product_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `icon_image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `size_template_id` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4heb9iov0w7cwekl24y3v8k7r` (`created_by`),
  KEY `FK4m3i4rduvwlvpab9nkskcdmya` (`updated_by`),
  KEY `FKnhstaep8s818kydkq4teq8v4e` (`parent_id`),
  KEY `FK68uhld5b37409lpdd483c7t9s` (`size_template_id`),
  KEY `FKl0nrc4k8j8i6fnsh4sgw1f7c3` (`upload_by`),
  CONSTRAINT `FK4heb9iov0w7cwekl24y3v8k7r` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK4m3i4rduvwlvpab9nkskcdmya` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK68uhld5b37409lpdd483c7t9s` FOREIGN KEY (`size_template_id`) REFERENCES `size_templates` (`id`),
  CONSTRAINT `FKl0nrc4k8j8i6fnsh4sgw1f7c3` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKnhstaep8s818kydkq4teq8v4e` FOREIGN KEY (`parent_id`) REFERENCES `product_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_categories`
--

LOCK TABLES `product_categories` WRITE;
/*!40000 ALTER TABLE `product_categories` DISABLE KEYS */;
INSERT INTO `product_categories` VALUES (6,'2025-10-18 17:55:25.553000','2025-10-18 17:55:25.586000','Electronic Products','/ecommerce-images/product-categorys/6/01691eaf-6b08-490d-968f-328a9fc55bf4.jpeg','Electronic',1,1,2,NULL,NULL,NULL,NULL),(7,'2025-10-18 17:55:59.938000','2025-11-02 17:21:07.507000','Computer, Laptop & accessories','/ecommerce-images/product-categorys/7/2c0051a8-8f1c-45d2-b2e5-166d6a22f832.jpeg','Computer',1,1,2,2,6,1,NULL),(8,'2025-10-18 17:56:41.384000','2025-10-19 12:16:24.366000','Mobile, Android & iOS','/ecommerce-images/product-categorys/8/c8c8bdaf-3380-43b7-b282-93144c34305f.jpg','Mobile',2,1,2,2,6,3,NULL),(9,'2025-10-20 14:59:16.978000','2025-10-20 14:59:17.124000','Fashion products','/ecommerce-images/product-categorys/9/651a3535-66f6-4dcb-9851-2c59cae4d86c.jpg','Fashion',2,1,2,NULL,NULL,1,NULL),(10,'2025-10-20 15:01:38.585000','2025-10-20 15:01:38.603000','Women\'s Clothing','/ecommerce-images/product-categorys/10/298a680e-1b32-4343-8425-b393e5fcba8c.jpg','Women\'s Clothing',1,1,2,NULL,9,1,NULL),(11,'2025-10-20 15:02:11.459000','2025-10-20 15:02:11.481000','Men\'s Clothing','/ecommerce-images/product-categorys/11/872ceb51-617a-4321-97a5-9b1c874afde2.jpg','Men\'s Clothing',2,1,2,NULL,9,1,NULL),(12,'2025-11-04 17:15:20.183000','2025-11-04 17:34:03.862000',NULL,'/ecommerce-images/product-categorys/12/9840e6f9-624e-4ea6-a19b-780c28aa8ee6.png','Mobile Phones',1,1,NULL,NULL,NULL,4,2);
/*!40000 ALTER TABLE `product_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_types`
--

DROP TABLE IF EXISTS `product_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `icon_image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mm` varchar(255) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgkrl6wos59gkhsuwoo7472uvi` (`created_by`),
  KEY `FKkpjqfilbgg6dp1hxnr875o845` (`updated_by`),
  KEY `FKafi04i4o1ufrhgscse4he13nv` (`upload_by`),
  CONSTRAINT `FKafi04i4o1ufrhgscse4he13nv` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKgkrl6wos59gkhsuwoo7472uvi` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKkpjqfilbgg6dp1hxnr875o845` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_types`
--

LOCK TABLES `product_types` WRITE;
/*!40000 ALTER TABLE `product_types` DISABLE KEYS */;
INSERT INTO `product_types` VALUES (1,'2025-10-15 14:25:38.267000','2025-10-15 14:27:52.056000',NULL,'/ecommerce-images/product-typess/1/4223c756-9dd7-4a4e-aba6-3c09a60025e1.jpg','MOBILES','မိုဘိုင်း',1,2,2,NULL),(2,'2025-11-04 17:15:20.225000','2025-11-04 17:34:03.888000',NULL,'/ecommerce-images/product-types/2/0cc0f1d6-5654-402f-a833-7210af7d0138.png','Electronics',NULL,NULL,NULL,NULL,2);
/*!40000 ALTER TABLE `product_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `branch_code` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mm` varchar(255) DEFAULT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `product_image` varchar(255) DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  `specification` varchar(255) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `brand_id` bigint DEFAULT NULL,
  `color_template_id` bigint DEFAULT NULL,
  `product_category_id` bigint DEFAULT NULL,
  `product_type_id` bigint DEFAULT NULL,
  `size_template` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl0lce8i162ldn9n01t2a6lcix` (`created_by`),
  KEY `FKdeswm6d74skv6do803axl6edj` (`updated_by`),
  KEY `FKa3a4mpsfdf4d2y6r8ra3sc8mv` (`brand_id`),
  KEY `FKcg4g90roi0asmi0lhuo47nqew` (`color_template_id`),
  KEY `FKe05mpyhp4howtq8q4s65wm3q8` (`product_category_id`),
  KEY `FKrv6og3b2qlahvka0bxn7btyqd` (`product_type_id`),
  KEY `FK1s400bfrxt0d47nl2tv235ggd` (`size_template`),
  KEY `FK176fqxtq19jqfgfkqtx4rcg1i` (`upload_by`),
  CONSTRAINT `FK176fqxtq19jqfgfkqtx4rcg1i` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK1s400bfrxt0d47nl2tv235ggd` FOREIGN KEY (`size_template`) REFERENCES `size_templates` (`id`),
  CONSTRAINT `FKa3a4mpsfdf4d2y6r8ra3sc8mv` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`),
  CONSTRAINT `FKcg4g90roi0asmi0lhuo47nqew` FOREIGN KEY (`color_template_id`) REFERENCES `color_templates` (`id`),
  CONSTRAINT `FKdeswm6d74skv6do803axl6edj` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKe05mpyhp4howtq8q4s65wm3q8` FOREIGN KEY (`product_category_id`) REFERENCES `product_categories` (`id`),
  CONSTRAINT `FKl0lce8i162ldn9n01t2a6lcix` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrv6og3b2qlahvka0bxn7btyqd` FOREIGN KEY (`product_type_id`) REFERENCES `product_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'2025-10-20 15:08:34.987000','2025-11-02 16:36:24.305000','','T01','<b>Description </b><i>TEST</i>','Testing 01','စမ်းသပ်ခြင်း',20000.00,'/ecommerce-images/products/1/9a0c8f7e-a8bc-4515-85af-612cc30c2bc1.png',1,'<b>Testing <i>Specifications</i></b>',1,2,2,1,NULL,7,1,NULL,NULL),(4,'2025-11-04 17:15:20.265000','2025-11-04 17:34:03.929000',NULL,'PROD_001',NULL,'Sample Product','Sample Product MM',NULL,'/ecommerce-images/products/4/0ba7788b-b82a-4555-b48a-df03b90c6a4a.png',NULL,NULL,NULL,NULL,NULL,2,NULL,12,2,NULL,2),(5,'2025-11-04 17:15:20.273000','2025-11-04 17:15:20.273000',NULL,'PROD_002',NULL,'Basic Product',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(6,'2025-11-20 16:01:07.943000','2025-11-20 16:01:08.050000','','T05','<b>Description </b><i>TEST</i>','Testing 01 (Copy)','စမ်းသပ်ခြင်း (Copy)',20000.00,'/ecommerce-images/products/6/2e8c9db9-1a7b-4118-a249-997159306963.png',2,'<b>Testing <i>Specifications</i></b>',1,2,NULL,1,NULL,7,1,NULL,NULL),(10,'2025-11-20 16:31:58.504000','2025-11-20 16:31:58.535000','','T06','<b>Description </b><i>TEST</i>','Testing 01 (Copy1)','စမ်းသပ်ခြင်း (Copy1)',20000.00,'/ecommerce-images/products/10/b104ac16-638b-42bd-94e5-0fe47161edd1.png',7,'<b>Testing <i>Specifications</i></b>',1,2,NULL,1,NULL,7,1,NULL,NULL);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq6ium4se7bjk3mfbj3qm1gvy` (`created_by`),
  KEY `FKf0p4aw14esgr0ukams27qfl3m` (`updated_by`),
  KEY `FKa7m9ro2vfsqohkvo87hc9j4xx` (`upload_by`),
  CONSTRAINT `FKa7m9ro2vfsqohkvo87hc9j4xx` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKf0p4aw14esgr0ukams27qfl3m` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKq6ium4se7bjk3mfbj3qm1gvy` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'2025-10-10 17:36:16.685000','2025-10-14 17:12:17.056000','ROLE_ADMIN','Admin',2,NULL,NULL),(2,'2025-10-14 16:42:15.112000','2025-10-14 16:42:15.112000','ROLE_MANAGER','Manager',NULL,2,NULL);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `size_template_items`
--

DROP TABLE IF EXISTS `size_template_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `size_template_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `size_template_id` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjoqea5wqqn464qovj4ptbfqvg` (`created_by`),
  KEY `FK3cj60olvccxg5xa91esomdvtu` (`updated_by`),
  KEY `FKdvg8m8ortuc3fdy8v02y5q30r` (`size_template_id`),
  KEY `FKrkyrxcxibb6bd53unebgq7eya` (`upload_by`),
  CONSTRAINT `FK3cj60olvccxg5xa91esomdvtu` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKdvg8m8ortuc3fdy8v02y5q30r` FOREIGN KEY (`size_template_id`) REFERENCES `size_templates` (`id`),
  CONSTRAINT `FKjoqea5wqqn464qovj4ptbfqvg` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrkyrxcxibb6bd53unebgq7eya` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `size_template_items`
--

LOCK TABLES `size_template_items` WRITE;
/*!40000 ALTER TABLE `size_template_items` DISABLE KEYS */;
INSERT INTO `size_template_items` VALUES (2,'2025-10-19 11:17:05.634000','2025-10-30 16:17:36.902000',NULL,'XL',1,1,2,2,1,NULL),(3,'2025-10-19 11:17:13.968000','2025-10-19 11:17:13.968000',NULL,'X',2,1,2,2,1,NULL),(4,'2025-11-04 17:15:20.455000','2025-11-18 15:21:44.341000',NULL,'M',1,1,NULL,2,4,2);
/*!40000 ALTER TABLE `size_template_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `size_templates`
--

DROP TABLE IF EXISTS `size_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `size_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sequence` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5hg8xk8tyoq9cu0qhba9o0egl` (`created_by`),
  KEY `FKheonupilomwhdbe7ocj8dgyd1` (`updated_by`),
  KEY `FKd4fssubci05pdqb02sio98g8w` (`upload_by`),
  CONSTRAINT `FK5hg8xk8tyoq9cu0qhba9o0egl` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKd4fssubci05pdqb02sio98g8w` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKheonupilomwhdbe7ocj8dgyd1` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `size_templates`
--

LOCK TABLES `size_templates` WRITE;
/*!40000 ALTER TABLE `size_templates` DISABLE KEYS */;
INSERT INTO `size_templates` VALUES (1,'2025-10-19 09:54:41.848000','2025-10-20 15:33:14.815000','Clothing Sizes',1,1,2,2,NULL),(2,'2025-10-19 11:49:32.188000','2025-10-19 12:27:59.777000','Electronic Device Sizes',2,1,2,2,NULL),(3,'2025-10-19 11:52:04.222000','2025-10-19 12:28:04.603000','Mobile Sizes',3,1,2,2,NULL),(4,'2025-11-04 17:15:20.164000','2025-11-18 15:21:31.098000','Mobile Phones Size Template',4,1,NULL,2,2);
/*!40000 ALTER TABLE `size_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`),
  KEY `FKibk1e3kaxy5sfyeekp8hbhnim` (`created_by`),
  KEY `FKci7xr690rvyv3bnfappbyh8x0` (`updated_by`),
  KEY `FKt8t38wlhg2vmqj7jnw8y8cn0k` (`upload_by`),
  CONSTRAINT `FKci7xr690rvyv3bnfappbyh8x0` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKibk1e3kaxy5sfyeekp8hbhnim` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKt8t38wlhg2vmqj7jnw8y8cn0k` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2025-10-10 17:36:16.853000','2025-10-10 17:36:16.853000','Admin','$2a$10$t41ROtuU2KejMOD2OJESoeGR8j/kutJfwK87BBEtpgmjP1ATNTJMa','123456',1,1,NULL,NULL,NULL),(2,'2025-10-12 14:38:18.911000','2025-10-13 02:40:07.322000','Sai Zaw Myint (ADV)','$2a$10$TLGRbZsZUV6Yz1jBFkuMnuVf.Xc0snr2XVKTgBo5T0Y0VmQSL5knS','09883360492',1,1,NULL,NULL,NULL),(4,'2025-10-14 16:42:48.951000','2025-10-14 16:42:48.951000','Manager 1','$2a$10$UeDj4hNvF2Pj9t4vTnek/.T8ROEF6Q0sGY78Wm6T9piWb/eZOGlUm','09123456789',1,2,NULL,2,NULL);
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

-- Dump completed on 2026-01-01 20:27:31
