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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ADMIN`
--

LOCK TABLES `ADMIN` WRITE;
/*!40000 ALTER TABLE `ADMIN` DISABLE KEYS */;
INSERT INTO `ADMIN` VALUES (1,'Jean','Dupont','jdupont','pass123','Admin','active'),(2,'Marie','Durand','mdurand','pass123','Réceptionniste','active'),(3,'Alice','Martin','amartin','pass123','Réceptionniste','active'),(4,'Robert','Bernard','rbernard','pass123','Comptable','active'),(5,'Claire','Lemoine','clemoine','pass123','Admin','active'),(6,'David','Giraud','dgiraud','pass123','Réceptionniste','active'),(7,'Eva','Blanc','eblanc','pass123','Comptable','active'),(8,'Franck','Bleu','fbleu','pass123','Réceptionniste','active'),(9,'Grace','Rouge','grouge','pass123','Admin','active'),(10,'Henri','Jaune','hjaune','pass123','Comptable','active'),(11,'Ivy','Argent','iargent','pass123','Réceptionniste','active'),(12,'Jacques','Or','jor','pass123','Admin','active');
/*!40000 ALTER TABLE `ADMIN` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ADMISSION`
--

LOCK TABLES `ADMISSION` WRITE;
/*!40000 ALTER TABLE `ADMISSION` DISABLE KEYS */;
INSERT INTO `ADMISSION` VALUES (11,1,1,11,'normal','Fièvre et fatigue','2026-02-01 08:00:00','en cours'),(12,2,2,12,'urgence','Toux persistante','2026-02-02 09:15:00','en cours'),(13,3,1,13,'normal','Douleur abdominale','2026-02-03 10:30:00','en cours'),(14,4,3,14,'normal','Fatigue chronique','2026-02-04 07:45:00','en cours'),(15,5,2,15,'urgence','Éruption cutanée','2026-02-05 13:00:00','en cours'),(16,6,1,16,'normal','Mal de dos','2026-02-06 08:30:00','en cours'),(17,7,3,17,'normal','Maux de gorge','2026-02-07 09:00:00','en cours'),(18,8,2,18,'urgence','Essoufflement','2026-02-08 10:15:00','en cours'),(19,9,1,19,'normal','Nausées','2026-02-09 11:30:00','en cours'),(20,10,3,20,'normal','Douleurs articulaires','2026-02-10 12:45:00','en cours');
/*!40000 ALTER TABLE `ADMISSION` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHAMBRE`
--

LOCK TABLES `CHAMBRE` WRITE;
/*!40000 ALTER TABLE `CHAMBRE` DISABLE KEYS */;
INSERT INTO `CHAMBRE` VALUES (1,'101',1,'Standard',3,150.00,'libre'),(2,'102',1,'Standard',3,150.00,'libre'),(3,'103',1,'Standard',4,150.00,'libre'),(4,'201',2,'Standard',3,120.00,'libre'),(5,'202',2,'Standard',5,120.00,'libre'),(6,'301',3,'VIP',2,300.00,'libre'),(7,'302',3,'VIP',3,300.00,'libre');
/*!40000 ALTER TABLE `CHAMBRE` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CONSULTATION`
--

LOCK TABLES `CONSULTATION` WRITE;
/*!40000 ALTER TABLE `CONSULTATION` DISABLE KEYS */;
INSERT INTO `CONSULTATION` VALUES (1,11,11,'2026-02-01 08:30:00','Fièvre, fatigue','Patient présente un début de grippe. Prescrire repos et paracetamol.',30.00),(2,12,12,'2026-02-02 09:30:00','Toux persistante, essoufflement','Consultation pour bronchite aiguë. Antibiotique prescrit.',35.00),(3,13,13,'2026-02-03 10:45:00','Douleur abdominale','Suspect gastro-entérite. Repos et hydratation recommandés.',40.00),(4,14,14,'2026-02-04 07:50:00','Fatigue chronique','Examen sanguin recommandé pour anémie possible.',50.00),(5,15,15,'2026-02-05 13:15:00','Éruption cutanée','Dermatite suspectée. Crème locale prescrite.',25.00),(6,16,16,'2026-02-06 08:45:00','Mal de dos','Douleurs lombaires, physiothérapie recommandée.',45.00),(7,17,17,'2026-02-07 09:10:00','Maux de gorge','Infection bénigne. Antiseptique et repos.',30.00),(8,18,18,'2026-02-08 10:20:00','Essoufflement','Asthme confirmé. Inhalateur prescrit.',50.00),(9,19,19,'2026-02-09 11:40:00','Nausées','Intoxication alimentaire suspectée. Surveillance et hydratation.',35.00),(10,20,20,'2026-02-10 13:00:00','Douleurs articulaires','Arthrose suspectée. Anti-inflammatoires et suivi.',60.00);
/*!40000 ALTER TABLE `CONSULTATION` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DIAGNOSTIC`
--

LOCK TABLES `DIAGNOSTIC` WRITE;
/*!40000 ALTER TABLE `DIAGNOSTIC` DISABLE KEYS */;
INSERT INTO `DIAGNOSTIC` VALUES (1,1,'Grippe légère, traitement symptomatique recommandé.','modérée'),(2,2,'Bronchite aiguë nécessitant antibiotique.','modérée'),(3,3,'Gastro-entérite, surveiller hydratation.','modérée'),(4,4,'Fatigue chronique possible anémie, examens complémentaires.','légère'),(5,5,'Dermatite allergique, crème locale prescrite.','légère'),(6,6,'Douleurs lombaires, physiothérapie recommandée.','modérée'),(7,7,'Infection bénigne de la gorge, repos et antiseptique.','légère'),(8,8,'Asthme contrôlé, inhalateur prescrit.','modérée'),(9,9,'Intoxication alimentaire suspectée, surveillance nécessaire.','modérée'),(10,10,'Arthrose, anti-inflammatoires prescrits.','modérée'),(11,1,'Grippe légère, traitement symptomatique recommandé.','modérée'),(12,2,'Bronchite aiguë nécessitant antibiotique.','modérée'),(13,3,'Gastro-entérite, surveiller hydratation.','modérée'),(14,4,'Fatigue chronique possible anémie, examens complémentaires.','légère'),(15,5,'Dermatite allergique, crème locale prescrite.','légère'),(16,6,'Douleurs lombaires, physiothérapie recommandée.','modérée'),(17,7,'Infection bénigne de la gorge, repos et antiseptique.','légère'),(18,8,'Asthme contrôlé, inhalateur prescrit.','modérée'),(19,9,'Intoxication alimentaire suspectée, surveillance nécessaire.','modérée'),(20,10,'Arthrose, anti-inflammatoires prescrits.','modérée');
/*!40000 ALTER TABLE `DIAGNOSTIC` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EXAMEN`
--

LOCK TABLES `EXAMEN` WRITE;
/*!40000 ALTER TABLE `EXAMEN` DISABLE KEYS */;
INSERT INTO `EXAMEN` VALUES (1,11,'Analyse sanguine complète','Biologie','2026-02-01 09:00:00','Globules rouges normaux, légère anémie',50.00),(2,12,'Radiographie thorax','Imagerie','2026-02-02 10:30:00','Pas de anomalies majeures',120.00),(3,13,'Échographie abdominale','Imagerie','2026-02-03 11:15:00','Petite inflammation du foie',150.00),(4,14,'Test glycémie','Biologie','2026-02-04 08:45:00','Glycémie élevée',30.00),(5,15,'ECG','Cardiologie','2026-02-05 13:30:00','Rythme cardiaque normal',80.00),(6,16,'IRM cérébrale','Imagerie','2026-02-06 09:15:00','Aucune anomalie détectée',300.00),(7,17,'Analyse urine','Biologie','2026-02-07 10:00:00','Infection urinaire légère',40.00),(8,18,'Radiographie poumons','Imagerie','2026-02-08 11:45:00','Présence légère d’inflammation',120.00),(9,19,'Électrolytes','Biologie','2026-02-09 08:30:00','Tout est normal',35.00),(10,20,'Scanner abdominal','Imagerie','2026-02-10 12:00:00','Pas d’anomalies détectées',250.00);
/*!40000 ALTER TABLE `EXAMEN` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `FACTURATION`
--

LOCK TABLES `FACTURATION` WRITE;
/*!40000 ALTER TABLE `FACTURATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `FACTURATION` ENABLE KEYS */;
UNLOCK TABLES;

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
  `date_sortie` date DEFAULT NULL,
  PRIMARY KEY (`num_hospitalisation`),
  KEY `num_admission` (`num_admission`),
  KEY `num_lit` (`num_lit`),
  CONSTRAINT `HOSPITALISATION_ibfk_1` FOREIGN KEY (`num_admission`) REFERENCES `ADMISSION` (`num_admission`),
  CONSTRAINT `HOSPITALISATION_ibfk_2` FOREIGN KEY (`num_lit`) REFERENCES `LIT` (`num_lit`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HOSPITALISATION`
--

LOCK TABLES `HOSPITALISATION` WRITE;
/*!40000 ALTER TABLE `HOSPITALISATION` DISABLE KEYS */;
INSERT INTO `HOSPITALISATION` VALUES (1,11,1,'2026-01-31 23:00:00',NULL),(2,12,2,'2026-02-01 23:00:00',NULL),(3,13,3,'2026-02-02 23:00:00',NULL),(4,14,4,'2026-02-03 23:00:00',NULL),(5,15,5,'2026-02-04 23:00:00',NULL),(6,16,6,'2026-02-05 23:00:00',NULL),(7,17,7,'2026-02-06 23:00:00',NULL),(8,18,8,'2026-02-07 23:00:00',NULL),(9,19,9,'2026-02-08 23:00:00',NULL),(10,20,10,'2026-02-09 23:00:00',NULL);
/*!40000 ALTER TABLE `HOSPITALISATION` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LIT`
--

LOCK TABLES `LIT` WRITE;
/*!40000 ALTER TABLE `LIT` DISABLE KEYS */;
INSERT INTO `LIT` VALUES (1,'101-1',1,'libre'),(2,'101-2',1,'libre'),(3,'101-3',1,'libre'),(4,'102-1',2,'libre'),(5,'102-2',2,'libre'),(6,'102-3',2,'libre'),(7,'103-1',3,'libre'),(8,'103-2',3,'libre'),(9,'103-3',3,'libre'),(10,'103-4',3,'libre'),(11,'201-1',4,'libre'),(12,'201-2',4,'libre'),(13,'201-3',4,'libre'),(14,'202-1',5,'libre'),(15,'202-2',5,'libre'),(16,'202-3',5,'libre'),(17,'202-4',5,'libre'),(18,'202-5',5,'libre'),(19,'301-1',6,'libre'),(20,'301-2',6,'libre'),(21,'302-1',7,'libre'),(22,'302-2',7,'libre'),(23,'302-3',7,'libre');
/*!40000 ALTER TABLE `LIT` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `LOGS`
--

LOCK TABLES `LOGS` WRITE;
/*!40000 ALTER TABLE `LOGS` DISABLE KEYS */;
/*!40000 ALTER TABLE `LOGS` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MEDECIN`
--

LOCK TABLES `MEDECIN` WRITE;
/*!40000 ALTER TABLE `MEDECIN` DISABLE KEYS */;
INSERT INTO `MEDECIN` VALUES (11,'Dr. Alice Dupont','Cardiologie','MD','0601020304',1,50.00,'active'),(12,'Dr. Bernard Martin','Pédiatrie','MD','0602030405',2,40.00,'active'),(13,'Dr. Claire Petit','Neurologie','MD','0603040506',3,60.00,'active'),(14,'Dr. David Moreau','Dermatologie','MD','0604050607',4,45.00,'active'),(15,'Dr. Emma Lambert','Gynécologie','MD','0605060708',5,55.00,'active'),(16,'Dr. François Leroy','Orthopédie','MD','0606070809',6,50.00,'active'),(17,'Dr. Gabrielle Simon','Ophtalmologie','MD','0607080910',7,48.00,'active'),(18,'Dr. Hugo Richard','Psychiatrie','MD','0608091011',8,60.00,'active'),(19,'Dr. Isabelle Blanc','Gastroentérologie','MD','0609101112',9,55.00,'active'),(20,'Dr. Julien Fournier','Endocrinologie','MD','0610111213',10,50.00,'active');
/*!40000 ALTER TABLE `MEDECIN` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MEDICAMENT`
--

LOCK TABLES `MEDICAMENT` WRITE;
/*!40000 ALTER TABLE `MEDICAMENT` DISABLE KEYS */;
INSERT INTO `MEDICAMENT` VALUES (1,'Paracetamol','Comprimé','500 mg',1.50,'Sanofi',500),(2,'Amoxicilline','Gélule','500 mg',2.20,'Pfizer',300),(3,'Ibuprofène','Comprimé','400 mg',1.80,'Bayer',450),(4,'Doliprane','Sirop','2.4%',3.50,'Sanofi',200),(5,'Augmentin','Comprimé','1 g',4.75,'GlaxoSmithKline',150),(6,'Metformine','Comprimé','850 mg',2.00,'Merck',350),(7,'Aspirine','Comprimé','100 mg',1.20,'Bayer',600),(8,'Ventoline','Inhalateur','100 mcg/dose',8.50,'GlaxoSmithKline',120),(9,'Ciprofloxacine','Comprimé','500 mg',3.10,'Pfizer',180),(10,'Insuline Rapide','Injection','100 UI/ml',15.00,'Novo Nordisk',90),(11,'Omeprazole','Gélule','20 mg',2.80,'AstraZeneca',250),(12,'Ceftriaxone','Injection','1 g',6.50,'Roche',130),(13,'Loratadine','Comprimé','10 mg',1.90,'Johnson & Johnson',400),(14,'Azithromycine','Comprimé','500 mg',5.20,'Pfizer',170),(15,'Diclofénac','Gel','1%',4.00,'Novartis',220);
/*!40000 ALTER TABLE `MEDICAMENT` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `PAIEMENT`
--

LOCK TABLES `PAIEMENT` WRITE;
/*!40000 ALTER TABLE `PAIEMENT` DISABLE KEYS */;
/*!40000 ALTER TABLE `PAIEMENT` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PATIENT`
--

LOCK TABLES `PATIENT` WRITE;
/*!40000 ALTER TABLE `PATIENT` DISABLE KEYS */;
INSERT INTO `PATIENT` VALUES (1,'Durand','Léa','1991-05-12','F','14 rue des Lilas, Lyon','0611223344','lea.durand@example.com','A+','2233445566','Marc Durand 0611223345','Aucun','2026-02-15 13:36:25'),(2,'Garcia','Lucas','1987-09-21','M','7 avenue Victor Hugo, Paris','0622334455','lucas.garcia@example.com','O-','3344556677','Sophie Garcia 0622334456','Asthme','2026-02-15 13:36:25'),(3,'Morel','Chloé','1993-11-08','F','18 boulevard Saint-Germain, Paris','0633445566','chloe.morel@example.com','B+','4455667788','Jean Morel 0633445567','Allergie aux arachides','2026-02-15 13:36:25'),(4,'Roux','Nathan','1985-02-17','M','5 rue de Lyon, Marseille','0644556677','nathan.roux@example.com','AB+','5566778899','Marie Roux 0644556678','Hypertension','2026-02-15 13:36:25'),(5,'Fontaine','Emma','1990-06-25','F','12 rue des Fleurs, Lille','0655667788','emma.fontaine@example.com','A-','6677889900','Luc Fontaine 0655667789','Aucun','2026-02-15 13:36:25'),(6,'Lemoine','Hugo','1988-01-04','M','3 avenue des Champs, Paris','0666778899','hugo.lemoine@example.com','O+','7788990011','Claire Lemoine 0666778890','Diabète','2026-02-15 13:36:25'),(7,'Chevalier','Julie','1992-08-19','F','10 rue de la République, Lyon','0677889900','julie.chevalier@example.com','B-','8899001122','Pierre Chevalier 0677889901','Aucun','2026-02-15 13:36:25'),(8,'Blanc','Maxime','1986-12-11','M','8 rue du Moulin, Marseille','0688990011','maxime.blanc@example.com','AB-','9900112233','Anne Blanc 0688990012','Problèmes cardiaques','2026-02-15 13:36:25'),(9,'Dubois','Camille','1994-04-02','F','6 rue des Acacias, Paris','0699001122','camille.dubois@example.com','A+','1011121314','Julien Dubois 0699001123','Aucun','2026-02-15 13:36:25'),(10,'Petit','Antoine','1989-03-29','M','15 rue du Parc, Lyon','0610111213','antoine.petit@example.com','O-','1112131415','Isabelle Petit 0610111214','Allergie aux antibiotiques','2026-02-15 13:36:25');
/*!40000 ALTER TABLE `PATIENT` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PAVILLON`
--

LOCK TABLES `PAVILLON` WRITE;
/*!40000 ALTER TABLE `PAVILLON` DISABLE KEYS */;
INSERT INTO `PAVILLON` VALUES (1,'Pavillon Alpha',1,'Standard',10),(2,'Pavillon Beta',2,'Standard',8),(3,'Pavillon Gamma',3,'VIP',5);
/*!40000 ALTER TABLE `PAVILLON` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SERVICE`
--

LOCK TABLES `SERVICE` WRITE;
/*!40000 ALTER TABLE `SERVICE` DISABLE KEYS */;
INSERT INTO `SERVICE` VALUES (1,'Cardiologie','Médecine',NULL),(2,'Pédiatrie','Médecine',NULL),(3,'Neurologie','Médecine',NULL),(4,'Dermatologie','Médecine',NULL),(5,'Gynécologie','Médecine',NULL),(6,'Orthopédie','Chirurgie',NULL),(7,'Ophtalmologie','Médecine',NULL),(8,'Psychiatrie','Médecine',NULL),(9,'Gastroentérologie','Médecine',NULL),(10,'Endocrinologie','Médecine',NULL);
/*!40000 ALTER TABLE `SERVICE` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `TRAITEMENT`
--

LOCK TABLES `TRAITEMENT` WRITE;
/*!40000 ALTER TABLE `TRAITEMENT` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRAITEMENT` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-16 10:45:28
