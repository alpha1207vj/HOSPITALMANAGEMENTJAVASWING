-- MySQL dump 10.13  Distrib 8.0.44, for Linux (x86_64)
--
-- Host: localhost    Database: HOSPITAL
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ADMIN`
--

DROP TABLE IF EXISTS `ADMIN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ADMIN` (
  `id_admin` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  `statut` varchar(20) DEFAULT 'active',
  PRIMARY KEY (`id_admin`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ADMISSION`
--

DROP TABLE IF EXISTS `ADMISSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ADMISSION` (
  `num_admission` int NOT NULL AUTO_INCREMENT,
  `num_patient` int NOT NULL,
  `code_service` int NOT NULL,
  `num_medecin` int NOT NULL,
  `type_admission` enum('normal','urgence') DEFAULT 'normal',
  `motif` text,
  `date_admission` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `statut` varchar(20) DEFAULT 'en cours',
  PRIMARY KEY (`num_admission`),
  KEY `num_patient` (`num_patient`),
  KEY `code_service` (`code_service`),
  KEY `num_medecin` (`num_medecin`),
  CONSTRAINT `ADMISSION_ibfk_1` FOREIGN KEY (`num_patient`) REFERENCES `PATIENT` (`num_patient`),
  CONSTRAINT `ADMISSION_ibfk_2` FOREIGN KEY (`code_service`) REFERENCES `SERVICE` (`code_service`),
  CONSTRAINT `ADMISSION_ibfk_3` FOREIGN KEY (`num_medecin`) REFERENCES `MEDECIN` (`num_medecin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CHAMBRE`
--

DROP TABLE IF EXISTS `CHAMBRE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CHAMBRE` (
  `num_chambre` int NOT NULL AUTO_INCREMENT,
  `numero_chambre` varchar(20) NOT NULL,
  `num_pavillon` int DEFAULT NULL,
  `type_chambre` varchar(50) DEFAULT NULL,
  `nombre_lits` int DEFAULT '1',
  `tarif_journalier` decimal(10,2) DEFAULT '0.00',
  `statut` varchar(20) DEFAULT 'libre',
  PRIMARY KEY (`num_chambre`),
  KEY `num_pavillon` (`num_pavillon`),
  CONSTRAINT `CHAMBRE_ibfk_1` FOREIGN KEY (`num_pavillon`) REFERENCES `PAVILLON` (`num_pavillon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CONSULTATION`
--

DROP TABLE IF EXISTS `CONSULTATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CONSULTATION` (
  `num_consultation` int NOT NULL AUTO_INCREMENT,
  `num_admission` int NOT NULL,
  `num_medecin` int NOT NULL,
  `date_consultation` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `symptomes` text,
  `observations` text,
  `tarif` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`num_consultation`),
  KEY `num_admission` (`num_admission`),
  KEY `num_medecin` (`num_medecin`),
  CONSTRAINT `CONSULTATION_ibfk_1` FOREIGN KEY (`num_admission`) REFERENCES `ADMISSION` (`num_admission`),
  CONSTRAINT `CONSULTATION_ibfk_2` FOREIGN KEY (`num_medecin`) REFERENCES `MEDECIN` (`num_medecin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DIAGNOSTIC`
--

DROP TABLE IF EXISTS `DIAGNOSTIC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DIAGNOSTIC` (
  `num_diagnostic` int NOT NULL AUTO_INCREMENT,
  `num_consultation` int NOT NULL,
  `description` text,
  `gravite` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`num_diagnostic`),
  KEY `num_consultation` (`num_consultation`),
  CONSTRAINT `DIAGNOSTIC_ibfk_1` FOREIGN KEY (`num_consultation`) REFERENCES `CONSULTATION` (`num_consultation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `EXAMEN`
--

DROP TABLE IF EXISTS `EXAMEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `EXAMEN` (
  `num_examen` int NOT NULL AUTO_INCREMENT,
  `num_admission` int NOT NULL,
  `nom_examen` varchar(100) NOT NULL,
  `categorie` varchar(50) DEFAULT NULL,
  `date_examen` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `resultat` text,
  `prix` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`num_examen`),
  KEY `num_admission` (`num_admission`),
  CONSTRAINT `EXAMEN_ibfk_1` FOREIGN KEY (`num_admission`) REFERENCES `ADMISSION` (`num_admission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FACTURATION`
--

DROP TABLE IF EXISTS `FACTURATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `FACTURATION` (
  `num_facture` int NOT NULL AUTO_INCREMENT,
  `num_admission` int NOT NULL,
  `montant_total` decimal(10,2) DEFAULT '0.00',
  `date_facturation` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `statut_paiement` varchar(20) DEFAULT 'non payé',
  PRIMARY KEY (`num_facture`),
  KEY `num_admission` (`num_admission`),
  CONSTRAINT `FACTURATION_ibfk_1` FOREIGN KEY (`num_admission`) REFERENCES `ADMISSION` (`num_admission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `HOSPITALISATION`
--

DROP TABLE IF EXISTS `HOSPITALISATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `HOSPITALISATION` (
  `num_hospitalisation` int NOT NULL AUTO_INCREMENT,
  `num_admission` int NOT NULL,
  `num_lit` int NOT NULL,
  `date_entree` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `date_sortie` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`num_hospitalisation`),
  KEY `num_admission` (`num_admission`),
  KEY `num_lit` (`num_lit`),
  CONSTRAINT `HOSPITALISATION_ibfk_1` FOREIGN KEY (`num_admission`) REFERENCES `ADMISSION` (`num_admission`),
  CONSTRAINT `HOSPITALISATION_ibfk_2` FOREIGN KEY (`num_lit`) REFERENCES `LIT` (`num_lit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LIT`
--

DROP TABLE IF EXISTS `LIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LIT` (
  `num_lit` int NOT NULL AUTO_INCREMENT,
  `numero_lit` varchar(20) NOT NULL,
  `num_chambre` int DEFAULT NULL,
  `statut` varchar(20) DEFAULT 'libre',
  PRIMARY KEY (`num_lit`),
  KEY `num_chambre` (`num_chambre`),
  CONSTRAINT `LIT_ibfk_1` FOREIGN KEY (`num_chambre`) REFERENCES `CHAMBRE` (`num_chambre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LOGS`
--

DROP TABLE IF EXISTS `LOGS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LOGS` (
  `id_log` int NOT NULL AUTO_INCREMENT,
  `id_admin` int DEFAULT NULL,
  `action` varchar(100) DEFAULT NULL,
  `table_affectee` varchar(50) DEFAULT NULL,
  `ancienne_valeur` text,
  `nouvelle_valeur` text,
  `date_action` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_log`),
  KEY `id_admin` (`id_admin`),
  CONSTRAINT `LOGS_ibfk_1` FOREIGN KEY (`id_admin`) REFERENCES `ADMIN` (`id_admin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MEDECIN`
--

DROP TABLE IF EXISTS `MEDECIN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MEDECIN` (
  `num_medecin` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  `specialite` varchar(50) DEFAULT NULL,
  `qualification` varchar(50) DEFAULT NULL,
  `contact` varchar(50) DEFAULT NULL,
  `code_service` int DEFAULT NULL,
  `tarif_consultation` decimal(10,2) DEFAULT '0.00',
  `statut` varchar(20) DEFAULT 'active',
  PRIMARY KEY (`num_medecin`),
  KEY `code_service` (`code_service`),
  CONSTRAINT `MEDECIN_ibfk_1` FOREIGN KEY (`code_service`) REFERENCES `SERVICE` (`code_service`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MEDICAMENT`
--

DROP TABLE IF EXISTS `MEDICAMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MEDICAMENT` (
  `code_medicament` int NOT NULL AUTO_INCREMENT,
  `nom_commercial` varchar(100) DEFAULT NULL,
  `forme` varchar(50) DEFAULT NULL,
  `dosage` varchar(50) DEFAULT NULL,
  `prix_unitaire` decimal(10,2) DEFAULT '0.00',
  `fabricant` varchar(100) DEFAULT NULL,
  `stock` int DEFAULT '0',
  PRIMARY KEY (`code_medicament`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PAIEMENT`
--

DROP TABLE IF EXISTS `PAIEMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PAIEMENT` (
  `num_paiement` int NOT NULL AUTO_INCREMENT,
  `num_facture` int NOT NULL,
  `montant_paye` decimal(10,2) DEFAULT '0.00',
  `methode_paiement` varchar(50) DEFAULT NULL,
  `reference_transaction` varchar(100) DEFAULT NULL,
  `date_paiement` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `recu_par` int DEFAULT NULL,
  PRIMARY KEY (`num_paiement`),
  KEY `num_facture` (`num_facture`),
  KEY `recu_par` (`recu_par`),
  CONSTRAINT `PAIEMENT_ibfk_1` FOREIGN KEY (`num_facture`) REFERENCES `FACTURATION` (`num_facture`),
  CONSTRAINT `PAIEMENT_ibfk_2` FOREIGN KEY (`recu_par`) REFERENCES `ADMIN` (`id_admin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PATIENT`
--

DROP TABLE IF EXISTS `PATIENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PATIENT` (
  `num_patient` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `date_naissance` date DEFAULT NULL,
  `sexe` enum('M','F') DEFAULT 'M',
  `adresse` varchar(255) DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `groupe_sanguin` varchar(5) DEFAULT NULL,
  `numero_national` varchar(20) DEFAULT NULL,
  `contact_urgence` varchar(100) DEFAULT NULL,
  `antecedents_medicaux` text,
  `date_creation` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`num_patient`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PAVILLON`
--

DROP TABLE IF EXISTS `PAVILLON`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PAVILLON` (
  `num_pavillon` int NOT NULL AUTO_INCREMENT,
  `nom_pavillon` varchar(100) NOT NULL,
  `code_service` int DEFAULT NULL,
  `type_pavillon` varchar(50) DEFAULT NULL,
  `capacite_totale` int DEFAULT '0',
  PRIMARY KEY (`num_pavillon`),
  KEY `code_service` (`code_service`),
  CONSTRAINT `PAVILLON_ibfk_1` FOREIGN KEY (`code_service`) REFERENCES `SERVICE` (`code_service`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SERVICE`
--

DROP TABLE IF EXISTS `SERVICE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SERVICE` (
  `code_service` int NOT NULL AUTO_INCREMENT,
  `nom_service` varchar(100) NOT NULL,
  `type_service` varchar(50) DEFAULT NULL,
  `chef_service` int DEFAULT NULL,
  PRIMARY KEY (`code_service`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TRAITEMENT`
--

DROP TABLE IF EXISTS `TRAITEMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TRAITEMENT` (
  `num_traitement` int NOT NULL AUTO_INCREMENT,
  `num_consultation` int NOT NULL,
  `code_medicament` int NOT NULL,
  `quantite` int DEFAULT '1',
  `instructions` text,
  `duree` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`num_traitement`),
  KEY `num_consultation` (`num_consultation`),
  KEY `code_medicament` (`code_medicament`),
  CONSTRAINT `TRAITEMENT_ibfk_1` FOREIGN KEY (`num_consultation`) REFERENCES `CONSULTATION` (`num_consultation`),
  CONSTRAINT `TRAITEMENT_ibfk_2` FOREIGN KEY (`code_medicament`) REFERENCES `MEDICAMENT` (`code_medicament`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-07  1:44:52
