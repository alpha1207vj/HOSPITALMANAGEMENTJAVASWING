package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

public class ExamPage extends JFrame implements ActionListener {

    // ================== Components ==================
    JTextField txtPatientSearch, txtPatientName, txtAge, txtGender, txtPatientNum, txtAdmissionNum;
    JComboBox<String> comboDoctor, comboExamType, comboUrgency, comboAdmission;
    JTextArea txtSymptoms, txtResultat;
    JButton btnSearchPatient, btnAddExam, btnRemoveExam, btnSave, btnUpdateResult, btnCancel, btnRefresh;
    JTable examTable;
    DefaultTableModel tableModel;
    
    private int selectedPatientNum = -1;
    private int selectedAdmissionNum = -1;

    // ================== BUTTON STYLES ==================
    private void applyButtonStyle(JButton btn, Color bg, Color fg) {
        // Force consistent rendering across Look&Feels (esp. Nimbus/Windows)
        btn.setUI(new BasicButtonUI());
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Border padding = BorderFactory.createEmptyBorder(8, 16, 8, 16);
        btn.setBorder(padding);
    }

    // ================== Constructor ==================
    public ExamPage(String username, String role) {
        setTitle("Gestion des Examens - Système de Gestion Hospitalière");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(240, 242, 245));

        // Header
        add(createHeaderPanel(username, role), BorderLayout.NORTH);

        // Main content
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);
        
        // Load data after window is visible
        try {
            loadDoctors();
            loadAdmissions();
            loadExams();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement initial des données: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== HEADER PANEL ==================
    private JPanel createHeaderPanel(String username, String role) {
        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(1200, 100));
        header.setBackground(new Color(41, 128, 185));
        header.setLayout(null);

        JLabel titleLabel = new JLabel("Gestion des Examens");
        titleLabel.setBounds(50, 30, 400, 40);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        // User info
        JLabel userLabel = new JLabel("Utilisateur: " + username + " | Rôle: " + role);
        userLabel.setBounds(800, 35, 400, 25);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(Color.WHITE);
        header.add(userLabel);

        return header;
    }

    // ================== MAIN PANEL ==================
    private JPanel createMainPanel() {
        JPanel main = new JPanel();
        main.setLayout(null);
        main.setBackground(new Color(240, 242, 245));

        // ---------- Patient Info Panel ----------
        JPanel patientPanel = new JPanel();
        patientPanel.setBounds(30, 20, 550, 200);
        patientPanel.setBackground(Color.WHITE);
        patientPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1), 
                "Informations Patient", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        patientPanel.setLayout(null);

        JLabel lblSearch = new JLabel("Rechercher Patient:");
        lblSearch.setBounds(20, 25, 150, 25);
        patientPanel.add(lblSearch);

        txtPatientSearch = new JTextField();
        txtPatientSearch.setBounds(20, 50, 250, 30);
        patientPanel.add(txtPatientSearch);

        btnSearchPatient = new JButton("🔍 Rechercher");
        btnSearchPatient.setBounds(280, 50, 120, 30);
        applyButtonStyle(btnSearchPatient, new Color(41, 128, 185), Color.WHITE);
        btnSearchPatient.addActionListener(this);
        patientPanel.add(btnSearchPatient);

        // Patient details
        JLabel lblNum = new JLabel("Num Patient:");
        lblNum.setBounds(20, 90, 100, 25);
        patientPanel.add(lblNum);

        txtPatientNum = new JTextField();
        txtPatientNum.setBounds(120, 90, 100, 25);
        txtPatientNum.setEditable(false);
        txtPatientNum.setBackground(new Color(230, 230, 230));
        patientPanel.add(txtPatientNum);

        JLabel lblName = new JLabel("Nom:");
        lblName.setBounds(20, 120, 80, 25);
        patientPanel.add(lblName);

        txtPatientName = new JTextField();
        txtPatientName.setBounds(100, 120, 200, 25);
        txtPatientName.setEditable(false);
        txtPatientName.setBackground(new Color(230, 230, 230));
        patientPanel.add(txtPatientName);

        JLabel lblAge = new JLabel("Âge:");
        lblAge.setBounds(20, 150, 80, 25);
        patientPanel.add(lblAge);

        txtAge = new JTextField();
        txtAge.setBounds(100, 150, 80, 25);
        txtAge.setEditable(false);
        txtAge.setBackground(new Color(230, 230, 230));
        patientPanel.add(txtAge);

        JLabel lblGender = new JLabel("Genre:");
        lblGender.setBounds(200, 150, 80, 25);
        patientPanel.add(lblGender);

        txtGender = new JTextField();
        txtGender.setBounds(270, 150, 80, 25);
        txtGender.setEditable(false);
        txtGender.setBackground(new Color(230, 230, 230));
        patientPanel.add(txtGender);

        main.add(patientPanel);

        // ---------- Exam Request Panel ----------
        JPanel examPanel = new JPanel();
        examPanel.setBounds(600, 20, 550, 200);
        examPanel.setBackground(Color.WHITE);
        examPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1), 
                "Demande d'Examen", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        examPanel.setLayout(null);

        JLabel lblAdmission = new JLabel("Admission:");
        lblAdmission.setBounds(20, 25, 120, 25);
        examPanel.add(lblAdmission);

        comboAdmission = new JComboBox<>();
        comboAdmission.setBounds(150, 25, 350, 25);
        comboAdmission.addActionListener(e -> {
            String selected = (String) comboAdmission.getSelectedItem();
            if (selected != null && !selected.equals("-- Sélectionner --")) {
                try {
                    selectedAdmissionNum = Integer.parseInt(selected.split(" - ")[0]);
                } catch (Exception ex) {
                    selectedAdmissionNum = -1;
                }
            }
        });
        examPanel.add(comboAdmission);

        JLabel lblExamType = new JLabel("Type d'Examen:");
        lblExamType.setBounds(20, 60, 120, 25);
        examPanel.add(lblExamType);

        comboExamType = new JComboBox<>(new String[]{
            "Analyse de sang", "Radiographie", "IRM", "ECG", "Échographie", 
            "Scanner", "Biopsie", "Endoscopie", "Autre"
        });
        comboExamType.setBounds(150, 60, 200, 25);
        examPanel.add(comboExamType);

        JLabel lblDoctor = new JLabel("Médecin:");
        lblDoctor.setBounds(20, 95, 120, 25);
        examPanel.add(lblDoctor);

        comboDoctor = new JComboBox<>();
        comboDoctor.setBounds(150, 95, 200, 25);
        examPanel.add(comboDoctor);

        JLabel lblUrgency = new JLabel("Urgence:");
        lblUrgency.setBounds(20, 130, 120, 25);
        examPanel.add(lblUrgency);

        comboUrgency = new JComboBox<>(new String[]{"Normal", "Urgent", "Critique"});
        comboUrgency.setBounds(150, 130, 150, 25);
        examPanel.add(comboUrgency);

        JLabel lblSymptoms = new JLabel("Notes / Symptômes:");
        lblSymptoms.setBounds(20, 165, 130, 25);
        examPanel.add(lblSymptoms);

        txtSymptoms = new JTextArea(2, 20);
        txtSymptoms.setBounds(150, 165, 350, 30);
        txtSymptoms.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        examPanel.add(txtSymptoms);

        main.add(examPanel);

        // ---------- Exam Table Panel ----------
        JPanel tablePanel = new JPanel();
        // Hauteur réduite pour laisser bien voir les boutons comme dans MedecinPage
        tablePanel.setBounds(30, 240, 1120, 320);
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Liste des Examens", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        tablePanel.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Admission", "Examen", "Médecin", "Date", "Urgence", "Résultat", "Prix"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examTable = new JTable(tableModel);
        examTable.setRowHeight(25);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = examTable.getSelectedRow();
                if (row >= 0) {
                    loadExamToForm(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(examTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add / Remove buttons
        JPanel tableButtons = new JPanel();
        tableButtons.setBackground(Color.WHITE);
        btnAddExam = new JButton("Ajouter Examen");
        btnRemoveExam = new JButton("Supprimer");
        btnUpdateResult = new JButton("Mettre à jour Résultat");
        btnRefresh = new JButton("Actualiser");

        applyButtonStyle(btnAddExam, new Color(46, 204, 113), Color.WHITE);
        applyButtonStyle(btnRemoveExam, new Color(192, 57, 43), Color.WHITE);
        applyButtonStyle(btnUpdateResult, new Color(52, 152, 219), Color.WHITE);
        applyButtonStyle(btnRefresh, new Color(149, 165, 166), Color.WHITE);
        
        btnAddExam.addActionListener(this); 
        btnRemoveExam.addActionListener(this);
        btnUpdateResult.addActionListener(this);
        btnRefresh.addActionListener(this);
        
        tableButtons.add(btnAddExam);
        tableButtons.add(btnRemoveExam);
        tableButtons.add(btnUpdateResult);
        tableButtons.add(btnRefresh);
        tablePanel.add(tableButtons, BorderLayout.SOUTH);

        main.add(tablePanel);

        // ---------- Result Panel ----------
        JPanel resultPanel = new JPanel();
        // Remonté pour rester visible sous le tableau et ses boutons
        resultPanel.setBounds(30, 580, 1120, 80);
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Résultat de l'Examen", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        resultPanel.setLayout(new BorderLayout());

        txtResultat = new JTextArea(3, 50);
        txtResultat.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        JScrollPane resultScroll = new JScrollPane(txtResultat);
        resultPanel.add(resultScroll, BorderLayout.CENTER);

        JPanel resultBtnPanel = new JPanel();
        btnSave = new JButton("Enregistrer");
        btnCancel = new JButton("Annuler");
        applyButtonStyle(btnSave, new Color(41, 128, 185), Color.WHITE);
        applyButtonStyle(btnCancel, new Color(192, 57, 43), Color.WHITE);
        btnSave.addActionListener(this); 
        btnCancel.addActionListener(this);
        resultBtnPanel.add(btnSave);
        resultBtnPanel.add(btnCancel);
        resultPanel.add(resultBtnPanel, BorderLayout.SOUTH);

        main.add(resultPanel);

        return main;
    }

    // ================== DATABASE METHODS ==================
    private void loadDoctors() {
        comboDoctor.removeAllItems();
        comboDoctor.addItem("-- Sélectionner --");
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données.");
                return;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT num_medecin, nom FROM MEDECIN WHERE statut='active' ORDER BY nom")) {
                
                while (rs.next()) {
                    comboDoctor.addItem(rs.getString("nom") + " (" + rs.getInt("num_medecin") + ")");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des médecins: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des médecins: " + e.getMessage());
        }
    }

    private void loadAdmissions() {
        comboAdmission.removeAllItems();
        comboAdmission.addItem("-- Sélectionner --");
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données.");
                return;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                    "SELECT a.num_admission, p.nom, p.prenom " +
                    "FROM ADMISSION a " +
                    "JOIN PATIENT p ON a.num_patient = p.num_patient " +
                    "WHERE a.statut = 'en cours' " +
                    "ORDER BY a.num_admission DESC")) {
                
                while (rs.next()) {
                    comboAdmission.addItem(rs.getInt("num_admission") + " - " + 
                        rs.getString("nom") + " " + rs.getString("prenom"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des admissions: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des admissions: " + e.getMessage());
        }
    }

    private void loadExams() {
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données.");
                return;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                    "SELECT e.num_examen, e.num_admission, e.nom_examen, e.categorie, " +
                    "e.date_examen, e.resultat, e.prix, " +
                    "a.num_patient, p.nom as patient_nom, p.prenom as patient_prenom, " +
                    "m.nom as medecin_nom " +
                    "FROM EXAMEN e " +
                    "JOIN ADMISSION a ON e.num_admission = a.num_admission " +
                    "JOIN PATIENT p ON a.num_patient = p.num_patient " +
                    "LEFT JOIN MEDECIN m ON a.num_medecin = m.num_medecin " +
                    "ORDER BY e.date_examen DESC")) {
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                
                while (rs.next()) {
                    String dateStr = "";
                    if (rs.getTimestamp("date_examen") != null) {
                        dateStr = sdf.format(rs.getTimestamp("date_examen"));
                    }
                    
                    tableModel.addRow(new Object[]{
                        rs.getInt("num_examen"),
                        rs.getString("patient_nom") + " " + rs.getString("patient_prenom"),
                        rs.getInt("num_admission"),
                        rs.getString("nom_examen"),
                        rs.getString("medecin_nom") != null ? rs.getString("medecin_nom") : "N/A",
                        dateStr,
                        rs.getString("categorie") != null ? rs.getString("categorie") : "Normal",
                        rs.getString("resultat") != null ? rs.getString("resultat") : "En attente",
                        rs.getBigDecimal("prix")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des examens: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des examens: " + e.getMessage());
        }
    }

    private void loadExamToForm(int row) {
        int examId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        txtResultat.setText(tableModel.getValueAt(row, 7).toString());
    }

    private int getDoctorIdFromCombo() {
        String selected = (String) comboDoctor.getSelectedItem();
        if (selected == null || selected.equals("-- Sélectionner --")) {
            return -1;
        }
        try {
            int start = selected.lastIndexOf("(") + 1;
            int end = selected.lastIndexOf(")");
            return Integer.parseInt(selected.substring(start, end));
        } catch (Exception e) {
            return -1;
        }
    }

    // ================== ACTION HANDLER ==================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearchPatient) {
            String searchTerm = txtPatientSearch.getText().trim();
            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nom ou numéro de patient.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT num_patient, nom, prenom, date_naissance, sexe " +
                             "FROM PATIENT WHERE num_patient = ? OR nom LIKE ? OR prenom LIKE ? LIMIT 1";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    try {
                        int num = Integer.parseInt(searchTerm);
                        pst.setInt(1, num);
                        pst.setString(2, "%" + searchTerm + "%");
                        pst.setString(3, "%" + searchTerm + "%");
                    } catch (NumberFormatException ex) {
                        pst.setInt(1, -1);
                        pst.setString(2, "%" + searchTerm + "%");
                        pst.setString(3, "%" + searchTerm + "%");
                    }
                    
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            selectedPatientNum = rs.getInt("num_patient");
                            txtPatientNum.setText(String.valueOf(selectedPatientNum));
                            txtPatientName.setText(rs.getString("nom") + " " + rs.getString("prenom"));
                            
                            // Calculate age
                            if (rs.getDate("date_naissance") != null) {
                                java.util.Date birthDate = rs.getDate("date_naissance");
                                int age = new java.util.Date().getYear() - birthDate.getYear();
                                txtAge.setText(String.valueOf(age));
                            }
                            
                            txtGender.setText(rs.getString("sexe"));
                            
                            // Load admissions for this patient
                            loadAdmissionsForPatient(selectedPatientNum);
                        } else {
                            JOptionPane.showMessageDialog(this, "Patient non trouvé.");
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
            }

        } else if (e.getSource() == btnAddExam) {
            if (selectedAdmissionNum <= 0) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une admission.");
                return;
            }

            if (comboExamType.getSelectedItem() == null || 
                comboExamType.getSelectedItem().equals("-- Sélectionner --")) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un type d'examen.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO EXAMEN (num_admission, nom_examen, categorie, prix) VALUES (?, ?, ?, ?)";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, selectedAdmissionNum);
                    pst.setString(2, comboExamType.getSelectedItem().toString());
                    pst.setString(3, comboUrgency.getSelectedItem().toString());
                    
                    // Default price based on exam type
                    double price = 0.0;
                    String examType = comboExamType.getSelectedItem().toString();
                    if (examType.contains("sang")) price = 50.0;
                    else if (examType.contains("Radiographie")) price = 100.0;
                    else if (examType.contains("IRM")) price = 500.0;
                    else if (examType.contains("Scanner")) price = 300.0;
                    else price = 150.0;
                    
                    pst.setBigDecimal(4, new java.math.BigDecimal(price));
                    
                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Examen ajouté avec succès !");
                        txtSymptoms.setText("");
                        loadExams();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage());
            }

        } else if (e.getSource() == btnRemoveExam) {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un examen à supprimer.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer cet examen ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    int examId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    String sql = "DELETE FROM EXAMEN WHERE num_examen = ?";
                    
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setInt(1, examId);
                        int rows = pst.executeUpdate();
                        if (rows > 0) {
                            JOptionPane.showMessageDialog(this, "Examen supprimé avec succès !");
                            loadExams();
                            txtResultat.setText("");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage());
                }
            }

        } else if (e.getSource() == btnUpdateResult || e.getSource() == btnSave) {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un examen pour mettre à jour le résultat.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                int examId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                String sql = "UPDATE EXAMEN SET resultat = ? WHERE num_examen = ?";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, txtResultat.getText().trim());
                    pst.setInt(2, examId);
                    
                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Résultat mis à jour avec succès !");
                        loadExams();
                        txtResultat.setText("");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour: " + ex.getMessage());
            }

        } else if (e.getSource() == btnCancel) {
            dispose();

        } else if (e.getSource() == btnRefresh) {
            loadExams();
            loadDoctors();
            loadAdmissions();
        }
    }

    private void loadAdmissionsForPatient(int patientNum) {
        comboAdmission.removeAllItems();
        comboAdmission.addItem("-- Sélectionner --");
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return;
            }
            
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT num_admission FROM ADMISSION WHERE num_patient = ? AND statut = 'en cours'")) {
                
                pst.setInt(1, patientNum);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        comboAdmission.addItem(String.valueOf(rs.getInt("num_admission")));
                    }
                }
            }
        } catch (SQLException e) {
            // Ignore
        } catch (Exception e) {
            // Ignore
        }
    }

    // ================== MAIN ==================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new ExamPage("Admin", "Administrateur");
    }
}
