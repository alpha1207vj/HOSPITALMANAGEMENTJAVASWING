package hospitalmanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO - Gestion des Consultations / Diagnostics / Traitements
 *
 * Schéma réel :
 *   CONSULTATION  : num_consultation, num_admission, num_medecin,
 *                   date_consultation (timestamp), symptomes, observations, tarif
 *   DIAGNOSTIC    : num_diagnostic, num_consultation, description, gravite
 *   TRAITEMENT    : num_traitement, num_consultation, code_medicament,
 *                   quantite, instructions, duree
 *
 * Corrections par rapport à la version précédente :
 *   - Suppression des colonnes inexistantes : diagnostic, traitement, cout
 *   - Ajout du champ : tarif (decimal)
 *   - Diagnostic et traitement gérés via leurs propres tables/méthodes
 */
public class ConsultationDAO {

    // ==================== MODELS ====================

    public static class Consultation {
        public int    numConsultation;
        public int    numAdmission;
        public int    numMedecin;
        public String dateConsultation;   // timestamp → String YYYY-MM-DD HH:mm:ss
        public String symptomes;
        public String observations;
        public java.math.BigDecimal tarif; // ✅ colonne réelle : tarif

        // Champs joints (lecture seule)
        public String nomPatient;
        public String nomMedecin;
        public int    numPatient;
    }

    public static class Diagnostic {
        public int    numDiagnostic;
        public int    numConsultation;
        public String description;
        public String gravite;   // ex: "léger", "modéré", "grave"
    }

    public static class Traitement {
        public int    numTraitement;
        public int    numConsultation;
        public int    codeMedicament;
        public int    quantite;
        public String instructions;
        public String duree;
        // Joint
        public String nomMedicament;
    }

    // ==================== CONSULTATION — CREATE ====================
    public static int addConsultation(Consultation c) throws SQLException {
        String sql =
            "INSERT INTO CONSULTATION " +
            "(num_admission, num_medecin, date_consultation, symptomes, observations, tarif) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, c.numAdmission);
            pst.setInt(2, c.numMedecin);
            pst.setString(3, nullIfEmpty(c.dateConsultation));
            pst.setString(4, nullIfEmpty(c.symptomes));
            pst.setString(5, nullIfEmpty(c.observations));
            if (c.tarif != null)
                pst.setBigDecimal(6, c.tarif);
            else
                pst.setNull(6, Types.DECIMAL);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
            return -1;
        }
    }

    // ==================== CONSULTATION — READ ALL (avec jointures) ====================
    public static List<Consultation> getAllConsultations() throws SQLException {
        List<Consultation> list = new ArrayList<>();
        String sql =
            "SELECT c.num_consultation, c.num_admission, c.num_medecin, " +
            "       c.date_consultation, c.symptomes, c.observations, c.tarif, " +
            "       p.nom AS nom_patient, p.prenom AS prenom_patient, p.num_patient, " +
            "       m.nom AS nom_medecin " +
            "FROM CONSULTATION c " +
            "JOIN ADMISSION  a ON c.num_admission = a.num_admission " +
            "JOIN PATIENT    p ON a.num_patient   = p.num_patient " +
            "LEFT JOIN MEDECIN m ON c.num_medecin = m.num_medecin " +
            "ORDER BY c.date_consultation DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapConsultRow(rs));
        }
        return list;
    }

    // ==================== CONSULTATION — READ BY PATIENT ====================
    public static List<Consultation> getConsultationsByPatient(int numPatient) throws SQLException {
        List<Consultation> list = new ArrayList<>();
        String sql =
            "SELECT c.num_consultation, c.num_admission, c.num_medecin, " +
            "       c.date_consultation, c.symptomes, c.observations, c.tarif, " +
            "       p.nom AS nom_patient, p.prenom AS prenom_patient, p.num_patient, " +
            "       m.nom AS nom_medecin " +
            "FROM CONSULTATION c " +
            "JOIN ADMISSION  a ON c.num_admission = a.num_admission " +
            "JOIN PATIENT    p ON a.num_patient   = p.num_patient " +
            "LEFT JOIN MEDECIN m ON c.num_medecin = m.num_medecin " +
            "WHERE p.num_patient = ? " +
            "ORDER BY c.date_consultation DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, numPatient);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapConsultRow(rs));
            }
        }
        return list;
    }

    // ==================== CONSULTATION — UPDATE ====================
    public static boolean updateConsultation(Consultation c) throws SQLException {
        String sql =
            "UPDATE CONSULTATION SET " +
            "num_admission=?, num_medecin=?, date_consultation=?, " +
            "symptomes=?, observations=?, tarif=? " +
            "WHERE num_consultation=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, c.numAdmission);
            pst.setInt(2, c.numMedecin);
            pst.setString(3, nullIfEmpty(c.dateConsultation));
            pst.setString(4, nullIfEmpty(c.symptomes));
            pst.setString(5, nullIfEmpty(c.observations));
            if (c.tarif != null)
                pst.setBigDecimal(6, c.tarif);
            else
                pst.setNull(6, Types.DECIMAL);
            pst.setInt(7, c.numConsultation);

            return pst.executeUpdate() > 0;
        }
    }

    // ==================== CONSULTATION — DELETE ====================
    public static boolean deleteConsultation(int numConsultation) throws SQLException {
        // Supprimer d'abord les dépendants (FK)
        try (Connection conn = DBConnection.getConnection()) {
            // Supprimer les traitements liés
            try (PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM TRAITEMENT WHERE num_consultation = ?")) {
                pst.setInt(1, numConsultation);
                pst.executeUpdate();
            }
            // Supprimer les diagnostics liés
            try (PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM DIAGNOSTIC WHERE num_consultation = ?")) {
                pst.setInt(1, numConsultation);
                pst.executeUpdate();
            }
            // Supprimer la consultation
            try (PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM CONSULTATION WHERE num_consultation = ?")) {
                pst.setInt(1, numConsultation);
                return pst.executeUpdate() > 0;
            }
        }
    }

    // ==================== DIAGNOSTIC — ADD ====================
    public static int addDiagnostic(Diagnostic d) throws SQLException {
        String sql = "INSERT INTO DIAGNOSTIC (num_consultation, description, gravite) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, d.numConsultation);
            pst.setString(2, nullIfEmpty(d.description));
            pst.setString(3, nullIfEmpty(d.gravite));
            pst.executeUpdate();
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ==================== DIAGNOSTIC — GET BY CONSULTATION ====================
    public static List<Diagnostic> getDiagnosticsByConsultation(int numConsultation) throws SQLException {
        List<Diagnostic> list = new ArrayList<>();
        String sql = "SELECT * FROM DIAGNOSTIC WHERE num_consultation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, numConsultation);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Diagnostic d = new Diagnostic();
                    d.numDiagnostic   = rs.getInt("num_diagnostic");
                    d.numConsultation = rs.getInt("num_consultation");
                    d.description     = rs.getString("description");
                    d.gravite         = rs.getString("gravite");
                    list.add(d);
                }
            }
        }
        return list;
    }

    // ==================== DIAGNOSTIC — UPDATE ====================
    public static boolean updateDiagnostic(Diagnostic d) throws SQLException {
        String sql = "UPDATE DIAGNOSTIC SET description=?, gravite=? WHERE num_diagnostic=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nullIfEmpty(d.description));
            pst.setString(2, nullIfEmpty(d.gravite));
            pst.setInt(3, d.numDiagnostic);
            return pst.executeUpdate() > 0;
        }
    }

    // ==================== TRAITEMENT — ADD ====================
    public static int addTraitement(Traitement t) throws SQLException {
        String sql =
            "INSERT INTO TRAITEMENT (num_consultation, code_medicament, quantite, instructions, duree) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, t.numConsultation);
            pst.setInt(2, t.codeMedicament);
            pst.setInt(3, t.quantite > 0 ? t.quantite : 1);
            pst.setString(4, nullIfEmpty(t.instructions));
            pst.setString(5, nullIfEmpty(t.duree));
            pst.executeUpdate();
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ==================== TRAITEMENT — GET BY CONSULTATION ====================
    public static List<Traitement> getTraitementsByConsultation(int numConsultation) throws SQLException {
        List<Traitement> list = new ArrayList<>();
        String sql =
            "SELECT t.*, m.nom_commercial AS nom_medicament " +
            "FROM TRAITEMENT t " +
            "LEFT JOIN MEDICAMENT m ON t.code_medicament = m.code_medicament " +
            "WHERE t.num_consultation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, numConsultation);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Traitement t = new Traitement();
                    t.numTraitement   = rs.getInt("num_traitement");
                    t.numConsultation = rs.getInt("num_consultation");
                    t.codeMedicament  = rs.getInt("code_medicament");
                    t.quantite        = rs.getInt("quantite");
                    t.instructions    = rs.getString("instructions");
                    t.duree           = rs.getString("duree");
                    t.nomMedicament   = rs.getString("nom_medicament");
                    list.add(t);
                }
            }
        }
        return list;
    }

    // ==================== TRAITEMENT — DELETE ====================
    public static boolean deleteTraitement(int numTraitement) throws SQLException {
        String sql = "DELETE FROM TRAITEMENT WHERE num_traitement = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, numTraitement);
            return pst.executeUpdate() > 0;
        }
    }

    // ==================== STATS pour ReportPage ====================

    public static int countConsultations() throws SQLException {
        String sql = "SELECT COUNT(*) FROM CONSULTATION";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public static int countConsultationsAujourdhui() throws SQLException {
        String sql = "SELECT COUNT(*) FROM CONSULTATION WHERE DATE(date_consultation) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /** Top 5 médecins par nombre de consultations */
    public static List<Object[]> getTopMedecins() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT m.nom, COUNT(c.num_consultation) AS total " +
            "FROM CONSULTATION c " +
            "JOIN MEDECIN m ON c.num_medecin = m.num_medecin " +
            "GROUP BY m.num_medecin, m.nom " +
            "ORDER BY total DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(new Object[]{rs.getString("nom"), rs.getInt("total")});
        }
        return list;
    }

    /** Consultations par mois — 12 derniers mois */
    public static List<Object[]> getConsultationsParMois() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT DATE_FORMAT(date_consultation, '%Y-%m') AS mois, COUNT(*) AS total " +
            "FROM CONSULTATION " +
            "WHERE date_consultation >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
            "GROUP BY mois ORDER BY mois";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(new Object[]{rs.getString("mois"), rs.getInt("total")});
        }
        return list;
    }

    // ==================== MAPPING ====================
    private static Consultation mapConsultRow(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        c.numConsultation  = rs.getInt("num_consultation");
        c.numAdmission     = rs.getInt("num_admission");
        c.numMedecin       = rs.getInt("num_medecin");
        c.dateConsultation = rs.getString("date_consultation");
        c.symptomes        = rs.getString("symptomes");
        c.observations     = rs.getString("observations");
        c.tarif            = rs.getBigDecimal("tarif");        // ✅ corrigé
        c.nomPatient       = rs.getString("nom_patient") + " " + rs.getString("prenom_patient");
        c.numPatient       = rs.getInt("num_patient");
        c.nomMedecin       = rs.getString("nom_medecin");
        return c;
    }

    private static String nullIfEmpty(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}