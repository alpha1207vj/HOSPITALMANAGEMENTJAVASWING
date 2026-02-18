package hospitalmanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO - Gestion des Patients
 * Table : PATIENT (schéma réel vérifié)
 * Corrections : numero_national (était num_national), date_creation (était date_enregistrement)
 */
public class PatientDAO {

    // ==================== MODEL ====================
    public static class Patient {
        public int    numPatient;
        public String nom;
        public String prenom;
        public String dateNaissance;    // DATE → YYYY-MM-DD
        public String sexe;             // ENUM('M','F')
        public String adresse;
        public String telephone;
        public String email;
        public String groupeSanguin;
        public String numeroNational;   // ✅ numero_national
        public String contactUrgence;
        public String antecedents;      // ✅ antecedents_medicaux
        public Timestamp dateCreation;  // ✅ date_creation

        @Override
        public String toString() {
            return nom + " " + prenom + " (#" + numPatient + ")";
        }
    }

    // ==================== CREATE ====================
    public static int addPatient(Patient p) throws SQLException {
        String sql =
            "INSERT INTO PATIENT " +
            "(nom, prenom, date_naissance, sexe, adresse, telephone, email, " +
            " groupe_sanguin, numero_national, contact_urgence, antecedents_medicaux) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, p.nom);
            pst.setString(2, p.prenom);
            pst.setString(3, nullIfEmpty(p.dateNaissance));
            pst.setString(4, nullIfEmpty(p.sexe));
            pst.setString(5, nullIfEmpty(p.adresse));
            pst.setString(6, nullIfEmpty(p.telephone));
            pst.setString(7, nullIfEmpty(p.email));
            pst.setString(8, nullIfEmpty(p.groupeSanguin));
            pst.setString(9, nullIfEmpty(p.numeroNational));
            pst.setString(10, nullIfEmpty(p.contactUrgence));
            pst.setString(11, nullIfEmpty(p.antecedents));

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
            return -1;
        }
    }

    // ==================== READ ALL ====================
    public static List<Patient> getAllPatients() throws SQLException {
        List<Patient> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM PATIENT ORDER BY num_patient DESC")) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ==================== SEARCH ====================
    public static List<Patient> searchPatients(String term) throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql =
            "SELECT * FROM PATIENT " +
            "WHERE nom LIKE ? OR prenom LIKE ? OR telephone LIKE ? " +
            "OR numero_national LIKE ? OR num_patient = ? " +
            "ORDER BY num_patient DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            String like = "%" + term + "%";
            pst.setString(1, like);
            pst.setString(2, like);
            pst.setString(3, like);
            pst.setString(4, like);
            try   { pst.setInt(5, Integer.parseInt(term)); }
            catch (NumberFormatException e) { pst.setInt(5, -1); }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ==================== GET BY ID ====================
    public static Patient getPatientById(int id) throws SQLException {
        String sql = "SELECT * FROM PATIENT WHERE num_patient = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ==================== UPDATE ====================
    public static boolean updatePatient(Patient p) throws SQLException {
        String sql =
            "UPDATE PATIENT SET nom=?, prenom=?, date_naissance=?, sexe=?, adresse=?, " +
            "telephone=?, email=?, groupe_sanguin=?, numero_national=?, " +
            "contact_urgence=?, antecedents_medicaux=? WHERE num_patient=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, p.nom);
            pst.setString(2, p.prenom);
            pst.setString(3, nullIfEmpty(p.dateNaissance));
            pst.setString(4, nullIfEmpty(p.sexe));
            pst.setString(5, nullIfEmpty(p.adresse));
            pst.setString(6, nullIfEmpty(p.telephone));
            pst.setString(7, nullIfEmpty(p.email));
            pst.setString(8, nullIfEmpty(p.groupeSanguin));
            pst.setString(9, nullIfEmpty(p.numeroNational));
            pst.setString(10, nullIfEmpty(p.contactUrgence));
            pst.setString(11, nullIfEmpty(p.antecedents));
            pst.setInt(12, p.numPatient);

            return pst.executeUpdate() > 0;
        }
    }

    // ==================== DELETE ====================
    public static boolean deletePatient(int numPatient) throws SQLException {
        String sql = "DELETE FROM PATIENT WHERE num_patient = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, numPatient);
            return pst.executeUpdate() > 0;
        }
    }

    // ==================== COUNT ====================
    public static int countPatients() throws SQLException {
        String sql = "SELECT COUNT(*) FROM PATIENT";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // ==================== MAPPING ====================
    private static Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.numPatient     = rs.getInt("num_patient");
        p.nom            = rs.getString("nom");
        p.prenom         = rs.getString("prenom");
        p.dateNaissance  = rs.getString("date_naissance");
        p.sexe           = rs.getString("sexe");
        p.adresse        = rs.getString("adresse");
        p.telephone      = rs.getString("telephone");
        p.email          = rs.getString("email");
        p.groupeSanguin  = rs.getString("groupe_sanguin");
        p.numeroNational = rs.getString("numero_national");    // ✅ corrigé
        p.contactUrgence = rs.getString("contact_urgence");
        p.antecedents    = rs.getString("antecedents_medicaux");
        p.dateCreation   = rs.getTimestamp("date_creation");   // ✅ corrigé
        return p;
    }

    private static String nullIfEmpty(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}