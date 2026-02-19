package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdmissionPage extends JFrame implements ActionListener {

    // ================== Components ==================
    JTextField txtSearch, txtNumAdmission, txtNumPatient, txtMotif;
    JComboBox<String> comboType, comboService, comboMedecin, comboStatut;
    JButton btnSearch, btnRefresh, btnAdd, btnUpdate, btnRemove;
    JTable admissionTable;
    DefaultTableModel tableModel;

    // ================== Constructor ==================
    public AdmissionPage(String username, String role) {
        setTitle("Gestion des Admissions - Système de Gestion Hospitalière");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(240, 242, 245));

        add(createHeaderPanel(username, role), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);

        try {
            loadServices();
            loadMedecins();
            loadAdmissions();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement initial des données: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== HEADER ==================
    private JPanel createHeaderPanel(String username, String role) {
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(1200, 100));
        header.setBackground(new Color(41, 128, 185));

        JLabel titleLabel = new JLabel("Gestion des Admissions");
        titleLabel.setBounds(50, 30, 400, 40);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        JLabel userLabel = new JLabel("Utilisateur: " + username + " | Rôle: " + role);
        userLabel.setBounds(800, 35, 350, 25);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(Color.WHITE);
        header.add(userLabel);

        return header;
    }

    // ================== MAIN PANEL ==================
    private JPanel createMainPanel() {
        JPanel main = new JPanel(null);
        main.setBackground(new Color(240, 242, 245));

        // ---------- SEARCH PANEL ----------
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchPanel.setBounds(30, 20, 1120, 70);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Rechercher Admission"
        ));

        txtSearch  = new JTextField(25);
        btnSearch  = new JButton("🔍 Rechercher");
        btnRefresh = new JButton("🔄 Actualiser");
        stylePrimaryButton(btnSearch);
        stylePrimaryButton(btnRefresh);
        btnSearch.addActionListener(this);
        btnRefresh.addActionListener(this);

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        main.add(searchPanel);

        // ---------- INFO PANEL ----------
        JPanel infoPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        infoPanel.setBounds(30, 110, 550, 320);
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Informations Admission"
        ));

        txtNumAdmission = new JTextField();
        txtNumAdmission.setEditable(false);
        txtNumAdmission.setBackground(new Color(230, 230, 230));

        txtNumPatient = new JTextField();
        comboType     = new JComboBox<>(new String[]{"normal", "urgence"});
        comboService  = new JComboBox<>();
        comboMedecin  = new JComboBox<>();
        txtMotif      = new JTextField();
        comboStatut   = new JComboBox<>(new String[]{"en cours", "terminée", "annulée"});

        infoPanel.add(new JLabel("Num Admission:")); infoPanel.add(txtNumAdmission);
        infoPanel.add(new JLabel("Num Patient:"));   infoPanel.add(txtNumPatient);
        infoPanel.add(new JLabel("Type:"));          infoPanel.add(comboType);
        infoPanel.add(new JLabel("Service:"));       infoPanel.add(comboService);
        infoPanel.add(new JLabel("Médecin:"));       infoPanel.add(comboMedecin);
        infoPanel.add(new JLabel("Motif:"));         infoPanel.add(txtMotif);
        infoPanel.add(new JLabel("Statut:"));        infoPanel.add(comboStatut);

        main.add(infoPanel);

        // ---------- TABLE PANEL ----------
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBounds(600, 110, 550, 420);
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Liste des Admissions"
        ));

        tableModel = new DefaultTableModel(
                new Object[]{"Num", "Patient", "Type", "Service", "Médecin", "Motif", "Date", "Statut"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        admissionTable = new JTable(tableModel);
        admissionTable.setRowHeight(28);
        admissionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Click row → fill form (null-safe)
        admissionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = admissionTable.getSelectedRow();
                if (row >= 0) loadAdmissionToForm(row);
            }
        });

        JScrollPane scrollPane = new JScrollPane(admissionTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons inside table panel (south)
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);

        btnAdd    = new JButton("Ajouter");
        btnUpdate = new JButton("Modifier");
        btnRemove = new JButton("Supprimer");

        styleSuccessButton(btnAdd);
        stylePrimaryButton(btnUpdate);
        styleDangerButton(btnRemove);

        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnRemove.addActionListener(this);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnRemove);

        tablePanel.add(btnPanel, BorderLayout.SOUTH);
        main.add(tablePanel);

        return main;
    }

    // ================== BUTTON STYLES ==================
    private void applyButtonStyle(JButton btn, Color bg, Color fg) {
        btn.setUI(new BasicButtonUI());
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    private void stylePrimaryButton(JButton btn) { applyButtonStyle(btn, new Color(41, 128, 185), Color.WHITE); }
    private void styleSuccessButton(JButton btn)  { applyButtonStyle(btn, new Color(46, 204, 113), Color.WHITE); }
    private void styleDangerButton(JButton btn)   { applyButtonStyle(btn, new Color(192, 57, 43),  Color.WHITE); }

    // ================== LOAD DATA ==================
    private void loadAdmissions() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) { showDbError(); return; }

            String sql = """
                    SELECT a.num_admission, a.num_patient, a.type_admission,
                           s.nom_service, m.nom, a.motif, a.date_admission, a.statut
                    FROM ADMISSION a
                    JOIN SERVICE s ON a.code_service = s.code_service
                    JOIN MEDECIN m ON a.num_medecin  = m.num_medecin
                    ORDER BY a.num_admission
                    """;
            try (PreparedStatement pst = conn.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("num_admission"),
                            rs.getInt("num_patient"),
                            rs.getString("type_admission"),
                            rs.getString("nom_service"),
                            rs.getString("nom"),
                            rs.getString("motif"),
                            rs.getTimestamp("date_admission"),
                            rs.getString("statut")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement admissions: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadServices() {
        comboService.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) { showDbError(); return; }

            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT code_service, nom_service FROM SERVICE ORDER BY nom_service");
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    comboService.addItem(rs.getInt("code_service") + " - " + rs.getString("nom_service"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement services: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMedecins() {
        comboMedecin.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) { showDbError(); return; }

            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT num_medecin, nom FROM MEDECIN ORDER BY nom");
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    comboMedecin.addItem(rs.getInt("num_medecin") + " - " + rs.getString("nom"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement médecins: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== FORM HELPERS ==================
    private void loadAdmissionToForm(int row) {
        Object numAdm  = tableModel.getValueAt(row, 0);
        Object numPat  = tableModel.getValueAt(row, 1);
        Object type    = tableModel.getValueAt(row, 2);
        Object service = tableModel.getValueAt(row, 3);
        Object medecin = tableModel.getValueAt(row, 4);
        Object motif   = tableModel.getValueAt(row, 5);
        Object statut  = tableModel.getValueAt(row, 7);

        txtNumAdmission.setText(numAdm != null ? numAdm.toString() : "");
        txtNumPatient.setText  (numPat != null ? numPat.toString() : "");
        txtMotif.setText       (motif  != null ? motif.toString()  : "");

        if (type    != null) comboType.setSelectedItem(type.toString());
        if (statut  != null) comboStatut.setSelectedItem(statut.toString());
        if (service != null) setComboByPrefix(comboService, service.toString());
        if (medecin != null) setComboByName(comboMedecin, medecin.toString());
    }

    private void clearForm() {
        txtNumAdmission.setText("");
        txtNumPatient.setText("");
        txtMotif.setText("");
        comboType.setSelectedIndex(0);
        comboStatut.setSelectedIndex(0);
        if (comboService.getItemCount() > 0) comboService.setSelectedIndex(0);
        if (comboMedecin.getItemCount() > 0) comboMedecin.setSelectedIndex(0);
    }

    /** Match "id - Name" combo where the Name part equals the given name */
    private void setComboByPrefix(JComboBox<String> combo, String name) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            String[] parts = combo.getItemAt(i).split(" - ", 2);
            if (parts.length == 2 && parts[1].equalsIgnoreCase(name)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        // fallback: contains
        setComboByName(combo, name);
    }

    /** Match combo item that contains the given substring */
    private void setComboByName(JComboBox<String> combo, String name) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).contains(name)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /** Extract numeric ID from "id - label" combo item */
    private int getSelectedId(JComboBox<String> combo) {
        if (combo.getSelectedItem() == null) return 0;
        try {
            return Integer.parseInt(combo.getSelectedItem().toString().split(" - ")[0].trim());
        } catch (NumberFormatException e) { return 0; }
    }

    private void showDbError() {
        JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // ================== ACTIONS ==================
    @Override
    public void actionPerformed(ActionEvent e) {

        // ---- SEARCH ----
        if (e.getSource() == btnSearch) {
            String term = txtSearch.getText().trim();
            if (term.isEmpty()) { loadAdmissions(); return; }

            tableModel.setRowCount(0);
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) { showDbError(); return; }

                String sql = """
                        SELECT a.num_admission, a.num_patient, a.type_admission,
                               s.nom_service, m.nom, a.motif, a.date_admission, a.statut
                        FROM ADMISSION a
                        JOIN SERVICE s ON a.code_service = s.code_service
                        JOIN MEDECIN m ON a.num_medecin  = m.num_medecin
                        WHERE a.motif LIKE ? OR m.nom LIKE ? OR s.nom_service LIKE ?
                           OR a.type_admission LIKE ? OR a.statut LIKE ?
                        ORDER BY a.num_admission
                        """;
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    String p = "%" + term + "%";
                    for (int i = 1; i <= 5; i++) pst.setString(i, p);
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            tableModel.addRow(new Object[]{
                                    rs.getInt("num_admission"),
                                    rs.getInt("num_patient"),
                                    rs.getString("type_admission"),
                                    rs.getString("nom_service"),
                                    rs.getString("nom"),
                                    rs.getString("motif"),
                                    rs.getTimestamp("date_admission"),
                                    rs.getString("statut")
                            });
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur recherche: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        // ---- REFRESH ----
        } else if (e.getSource() == btnRefresh) {
            loadServices();
            loadMedecins();
            loadAdmissions();
            clearForm();
            txtSearch.setText("");

        // ---- ADD ----
        } else if (e.getSource() == btnAdd) {
            if (txtNumPatient.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir le numéro du patient.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) { showDbError(); return; }

                int medecinId = getSelectedId(comboMedecin);
                if (medecinId == 0) {
                    JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médecin valide.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = """
                        INSERT INTO ADMISSION
                        (num_patient, code_service, num_medecin, type_admission, motif, statut)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, Integer.parseInt(txtNumPatient.getText().trim()));
                    pst.setInt(2, getSelectedId(comboService));
                    pst.setInt(3, medecinId);
                    pst.setString(4, comboType.getSelectedItem().toString());
                    pst.setString(5, txtMotif.getText().trim());
                    pst.setString(6, comboStatut.getSelectedItem().toString());

                    if (pst.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "Admission ajoutée avec succès !");
                        clearForm();
                        loadAdmissions();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Numéro patient invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        // ---- UPDATE ----
        } else if (e.getSource() == btnUpdate) {
            if (txtNumAdmission.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner une admission dans la liste avant de modifier.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) { showDbError(); return; }

                int medecinId = getSelectedId(comboMedecin);
                if (medecinId == 0) {
                    JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médecin valide.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = """
                        UPDATE ADMISSION
                        SET num_patient=?, code_service=?, num_medecin=?,
                            type_admission=?, motif=?, statut=?
                        WHERE num_admission=?
                        """;
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, Integer.parseInt(txtNumPatient.getText().trim()));
                    pst.setInt(2, getSelectedId(comboService));
                    pst.setInt(3, medecinId);
                    pst.setString(4, comboType.getSelectedItem().toString());
                    pst.setString(5, txtMotif.getText().trim());
                    pst.setString(6, comboStatut.getSelectedItem().toString());
                    pst.setInt(7, Integer.parseInt(txtNumAdmission.getText().trim()));

                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Admission modifiée avec succès !");
                        clearForm();
                        loadAdmissions();
                    } else {
                        JOptionPane.showMessageDialog(this, "Aucune ligne modifiée.");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Numéro patient invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        // ---- REMOVE ----
        } else if (e.getSource() == btnRemove) {
            int row = admissionTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner une admission à supprimer.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer cette admission ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) tableModel.getValueAt(row, 0);
                try (Connection conn = DBConnection.getConnection()) {
                    if (conn == null) { showDbError(); return; }

                    try (PreparedStatement pst = conn.prepareStatement(
                            "DELETE FROM ADMISSION WHERE num_admission=?")) {
                        pst.setInt(1, id);
                        if (pst.executeUpdate() > 0) {
                            JOptionPane.showMessageDialog(this, "Admission supprimée avec succès !");
                            clearForm();
                            loadAdmissions();
                        }
                    }
                } catch (SQLException ex) {
                    if ("23000".equals(ex.getSQLState())) {
                        JOptionPane.showMessageDialog(this,
                                "Impossible de supprimer : admission liée à d'autres données.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Erreur lors de la suppression: " + ex.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // ================== MAIN ==================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new AdmissionPage("Admin", "Administrateur");
    }
}