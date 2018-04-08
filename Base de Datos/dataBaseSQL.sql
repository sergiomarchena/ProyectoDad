-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: dad
-- ------------------------------------------------------
-- Server version	5.5.5-10.1.21-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `actuadores`
--

DROP TABLE IF EXISTS `actuadores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `actuadores` (
  `id` int(11) NOT NULL,
  `fecha` bigint(20) DEFAULT NULL,
  `sentido` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actuadores`
--

LOCK TABLES `actuadores` WRITE;
/*!40000 ALTER TABLE `actuadores` DISABLE KEYS */;
INSERT INTO `actuadores` VALUES (1,20,0);
/*!40000 ALTER TABLE `actuadores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domostate`
--

DROP TABLE IF EXISTS `domostate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domostate` (
  `id` int(11) NOT NULL,
  `name` varchar(145) DEFAULT NULL,
  `value` float DEFAULT NULL,
  `state` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domostate`
--

LOCK TABLES `domostate` WRITE;
/*!40000 ALTER TABLE `domostate` DISABLE KEYS */;
INSERT INTO `domostate` VALUES (10,'name1',0,0),(20,'hola',8,1);
/*!40000 ALTER TABLE `domostate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `localizaciones`
--

DROP TABLE IF EXISTS `localizaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `localizaciones` (
  `nombre` varchar(100) NOT NULL,
  `lluvia_max` double DEFAULT NULL,
  `lluvia_min` double DEFAULT NULL,
  `luz_max` double DEFAULT NULL,
  `luz_min` double DEFAULT NULL,
  `alarma` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `localizaciones`
--

LOCK TABLES `localizaciones` WRITE;
/*!40000 ALTER TABLE `localizaciones` DISABLE KEYS */;
INSERT INTO `localizaciones` VALUES ('Cocina',100,10,150,75,NULL),('Habitacion',50,0,100,50,NULL);
/*!40000 ALTER TABLE `localizaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `luces_interior`
--

DROP TABLE IF EXISTS `luces_interior`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `luces_interior` (
  `id` int(11) NOT NULL,
  `estado` tinyint(1) DEFAULT NULL,
  `localizacion_nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `localizacion_nombre_idx` (`localizacion_nombre`),
  CONSTRAINT `localizacion_nombre_luces_interior` FOREIGN KEY (`localizacion_nombre`) REFERENCES `localizaciones` (`nombre`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `luces_interior`
--

LOCK TABLES `luces_interior` WRITE;
/*!40000 ALTER TABLE `luces_interior` DISABLE KEYS */;
INSERT INTO `luces_interior` VALUES (1,0,'Cocina'),(2,1,'Habitacion');
/*!40000 ALTER TABLE `luces_interior` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persianas`
--

DROP TABLE IF EXISTS `persianas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persianas` (
  `id` int(11) NOT NULL,
  `estado` tinyint(1) DEFAULT NULL,
  `localizacion_nombre` varchar(45) DEFAULT NULL,
  `id_actuador` int(11) NOT NULL,
  PRIMARY KEY (`id_actuador`),
  KEY `persianas_localizacion_idx` (`localizacion_nombre`),
  CONSTRAINT `persianas_actuador` FOREIGN KEY (`id_actuador`) REFERENCES `actuadores` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `persianas_localizacion` FOREIGN KEY (`localizacion_nombre`) REFERENCES `localizaciones` (`nombre`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persianas`
--

LOCK TABLES `persianas` WRITE;
/*!40000 ALTER TABLE `persianas` DISABLE KEYS */;
INSERT INTO `persianas` VALUES (1,0,'Cocina',1);
/*!40000 ALTER TABLE `persianas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensores`
--

DROP TABLE IF EXISTS `sensores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sensores` (
  `id` int(11) NOT NULL,
  `fecha` bigint(20) DEFAULT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `valor` double DEFAULT NULL,
  `localizacion_nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `localizaciones_nombre_idx` (`localizacion_nombre`),
  CONSTRAINT `localizacion_nombre_sensores` FOREIGN KEY (`localizacion_nombre`) REFERENCES `localizaciones` (`nombre`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensores`
--

LOCK TABLES `sensores` WRITE;
/*!40000 ALTER TABLE `sensores` DISABLE KEYS */;
INSERT INTO `sensores` VALUES (1,22,'luz',100,'Cocina'),(2,22,'lluvia',40,'Cocina'),(3,22,'luz',110,'Habitacion'),(4,22,'lluvia',50,'Habitacion');
/*!40000 ALTER TABLE `sensores` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-04-03 14:10:00
