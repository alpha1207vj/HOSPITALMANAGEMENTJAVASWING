package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

public class MedecinPage extends JFrame implements ActionListener {

    // ================== Components ==================
    JTextField txtSearch, txtNum, txtNom, txtSpecialite, txtQualification, txtContact, txtTarif;
    JComboBox<String> comboStatut, comboService;
    JButton btnSearch, btnAdd, btnUpdate, btnRemove, btnRefresh, btnManageSpecialites;
    JTable medecinTable;
    DefaultTableModel tableModel;

    // ================== Constructor ==================
    public MedecinPage(String username, String role) {
        setTitle("Gestion des Médecins - Système de Gestion Hospitalière");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(240, 242, 245));

        add(createHeaderPanel(username, role), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);
        
        // Load data after window is visible
        try {
            loadServices();
            loadMedecins();
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

        JLabel titleLabel = new JLabel("Gestion des Médecins");
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

    // ================== MAIN ==================
    private JPanel createMainPanel() {
        JPanel main = new JPanel(null);
        main.setBackground(new Color(240, 242, 245));

        // ---------- SEARCH PANEL ----------
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchPanel.setBounds(30, 20, 1120, 70);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Rechercher Médecin"
        ));

        txtSearch = new JTextField(25);
        btnSearch = new JButton("🔍 Rechercher");
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
        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        infoPanel.setBounds(30, 110, 550, 350);
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Informations Médecin"
        ));

        txtNum = new JTextField();
        txtNum.setEditable(false);
        txtNum.setBackground(new Color(230, 230, 230));

        txtNom = new JTextField();
        txtSpecialite = new JTextField();
        txtQualification = new JTextField();
        txtContact = new JTextField();
        txtTarif = new JTextField();
        comboStatut = new JComboBox<>(new String[]{"active", "inactive"});

        // Combo box for services
        comboService = new JComboBox<>();
        comboService.addItem("-- Sélectionner --");

        infoPanel.add(new JLabel("Num Médecin:"));
        infoPanel.add(txtNum);
        infoPanel.add(new JLabel("Nom:"));
        infoPanel.add(txtNom);
        infoPanel.add(new JLabel("Spécialité:"));
        infoPanel.add(txtSpecialite);
        infoPanel.add(new JLabel("Qualification:"));
        infoPanel.add(txtQualification);
        infoPanel.add(new JLabel("Contact:"));
        infoPanel.add(txtContact);
        infoPanel.add(new JLabel("Service:"));
        infoPanel.add(comboService);
        infoPanel.add(new JLabel("Tarif Consultation:"));
        infoPanel.add(txtTarif);
        infoPanel.add(new JLabel("Statut:"));
        infoPanel.add(comboStatut);

        main.add(infoPanel);

        // ---------- TABLE PANEL ----------
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBounds(600, 110, 550, 420);
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Liste des Médecins"
        ));

        tableModel = new DefaultTableModel(
                new Object[]{"Num", "Nom", "Spécialité", "Qualification", "Contact", "Service", "Tarif", "Statut"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medecinTable = new JTable(tableModel);
        medecinTable.setRowHeight(28);
        medecinTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medecinTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = medecinTable.getSelectedRow();
                if (row >= 0) {
                    loadMedecinToForm(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(medecinTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Ajouter");
        btnUpdate = new JButton("Modifier");
        btnRemove = new JButton("Supprimer");
        btnManageSpecialites = new JButton("Gérer Spécialités");

        styleSuccessButton(btnAdd);
        stylePrimaryButton(btnUpdate);
        styleDangerButton(btnRemove);
        stylePrimaryButton(btnManageSpecialites);

        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnRemove.addActionListener(this);
        btnManageSpecialites.addActionListener(this);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnRemove);
        btnPanel.add(btnManageSpecialites);

        tablePanel.add(btnPanel, BorderLayout.SOUTH);
        main.add(tablePanel);

        return main;
    }

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

    private void stylePrimaryButton(JButton btn) {
        applyButtonStyle(btn, new Color(41, 128, 185), Color.WHITE);
    }

    private void styleSuccessButton(JButton btn) {
        applyButtonStyle(btn, new Color(46, 204, 113), Color.WHITE);
    }

    private void styleDangerButton(JButton btn) {
        applyButtonStyle(btn, new Color(192, 57, 43), Color.WHITE);
    }

    // ================== DATABASE METHODS ==================
    private void loadServices() {
        comboService.removeAllItems();
        comboService.addItem("-- Sélectionner --");
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT code_service, nom_service FROM SERVICE ORDER BY nom_service")) {
                
                while (rs.next()) {
                    comboService.addItem(rs.getString("nom_service") + " (" + rs.getInt("code_service") + ")");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des services: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des services: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMedecins() {
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                    "SELECT m.num_medecin, m.nom, m.specialite, m.qualification, m.contact, " +
                    "s.nom_service, m.tarif_consultation, m.statut " +
                    "FROM MEDECIN m " +
                    "LEFT JOIN SERVICE s ON m.code_service = s.code_service " +
                    "ORDER BY m.num_medecin")) {
                
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("num_medecin"),
                        rs.getString("nom"),
                        rs.getString("specialite"),
                        rs.getString("qualification"),
                        rs.getString("contact"),
                        rs.getString("nom_service") != null ? rs.getString("nom_service") : "N/A",
                        rs.getBigDecimal("tarif_consultation"),
                        rs.getString("statut")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des médecins: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des médecins: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMedecinToForm(int row) {
        txtNum.setText(tableModel.getValueAt(row, 0).toString());
        txtNom.setText(tableModel.getValueAt(row, 1).toString());
        txtSpecialite.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        txtQualification.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        txtContact.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        txtTarif.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
        comboStatut.setSelectedItem(tableModel.getValueAt(row, 7).toString());
        
        // Set service in combo
        String serviceName = tableModel.getValueAt(row, 5).toString();
        for (int i = 0; i < comboService.getItemCount(); i++) {
            if (comboService.getItemAt(i).contains(serviceName)) {
                comboService.setSelectedIndex(i);
                break;
            }
        }
    }

    private int getServiceCodeFromCombo() {
        String selected = (String) comboService.getSelectedItem();
        if (selected == null || selected.equals("-- Sélectionner --")) {
            return 0;
        }
        try {
            // Extract code from "Service Name (code)"
            int start = selected.lastIndexOf("(") + 1;
            int end = selected.lastIndexOf(")");
            return Integer.parseInt(selected.substring(start, end));
        } catch (Exception e) {
            return 0;
        }
    }

    private void clearForm() {
        txtNum.setText("");
        txtNom.setText("");
        txtSpecialite.setText("");
        txtQualification.setText("");
        txtContact.setText("");
        txtTarif.setText("");
        comboStatut.setSelectedIndex(0);
        comboService.setSelectedIndex(0);
    }

    // ================== ACTIONS ==================
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnAdd) {
            if (txtNom.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir au moins le nom du médecin.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO MEDECIN (nom, specialite, qualification, contact, code_service, tarif_consultation, statut) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, txtNom.getText().trim());
                    pst.setString(2, txtSpecialite.getText().trim().isEmpty() ? null : txtSpecialite.getText().trim());
                    pst.setString(3, txtQualification.getText().trim().isEmpty() ? null : txtQualification.getText().trim());
                    pst.setString(4, txtContact.getText().trim().isEmpty() ? null : txtContact.getText().trim());
                    
                    int serviceCode = getServiceCodeFromCombo();
                    if (serviceCode > 0) {
                        pst.setInt(5, serviceCode);
                    } else {
                        pst.setNull(5, Types.INTEGER);
                    }
                    
                    try {
                        pst.setBigDecimal(6, txtTarif.getText().trim().isEmpty() ? 
                            java.math.BigDecimal.ZERO : new java.math.BigDecimal(txtTarif.getText().trim()));
                    } catch (NumberFormatException ex) {
                        pst.setBigDecimal(6, java.math.BigDecimal.ZERO);
                    }
                    
                    pst.setString(7, comboStatut.getSelectedItem().toString());
                    
                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Médecin ajouté avec succès !");
                        clearForm();
                        loadMedecins();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == btnUpdate) {
            if (txtNum.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médecin à modifier.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE MEDECIN SET nom=?, specialite=?, qualification=?, contact=?, " +
                             "code_service=?, tarif_consultation=?, statut=? WHERE num_medecin=?";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, txtNom.getText().trim());
                    pst.setString(2, txtSpecialite.getText().trim().isEmpty() ? null : txtSpecialite.getText().trim());
                    pst.setString(3, txtQualification.getText().trim().isEmpty() ? null : txtQualification.getText().trim());
                    pst.setString(4, txtContact.getText().trim().isEmpty() ? null : txtContact.getText().trim());
                    
                    int serviceCode = getServiceCodeFromCombo();
                    if (serviceCode > 0) {
                        pst.setInt(5, serviceCode);
                    } else {
                        pst.setNull(5, Types.INTEGER);
                    }
                    
                    try {
                        pst.setBigDecimal(6, txtTarif.getText().trim().isEmpty() ? 
                            java.math.BigDecimal.ZERO : new java.math.BigDecimal(txtTarif.getText().trim()));
                    } catch (NumberFormatException ex) {
                        pst.setBigDecimal(6, java.math.BigDecimal.ZERO);
                    }
                    
                    pst.setString(7, comboStatut.getSelectedItem().toString());
                    pst.setInt(8, Integer.parseInt(txtNum.getText().trim()));
                    
                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Médecin modifié avec succès !");
                        clearForm();
                        loadMedecins();
                    } else {
                        JOptionPane.showMessageDialog(this, "Aucun médecin modifié.");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == btnRemove) {
            int row = medecinTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médecin à supprimer.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce médecin ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    int numMedecin = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    String sql = "DELETE FROM MEDECIN WHERE num_medecin=?";
                    
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setInt(1, numMedecin);
                        int rows = pst.executeUpdate();
                        if (rows > 0) {
                            JOptionPane.showMessageDialog(this, "Médecin supprimé avec succès !");
                            clearForm();
                            loadMedecins();
                        }
                    }
                } catch (SQLException ex) {
                    if (ex.getSQLState().equals("23000")) {
                        JOptionPane.showMessageDialog(this, 
                                "Impossible de supprimer ce médecin car il est référencé dans d'autres tables.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } else if (e.getSource() == btnSearch) {
            String searchTerm = txtSearch.getText().trim();
            if (searchTerm.isEmpty()) {
                loadMedecins();
                return;
            }

            tableModel.setRowCount(0);
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT m.num_medecin, m.nom, m.specialite, m.qualification, m.contact, " +
                             "s.nom_service, m.tarif_consultation, m.statut " +
                             "FROM MEDECIN m " +
                             "LEFT JOIN SERVICE s ON m.code_service = s.code_service " +
                             "WHERE m.nom LIKE ? OR m.specialite LIKE ? OR m.qualification LIKE ? " +
                             "ORDER BY m.num_medecin";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    String searchPattern = "%" + searchTerm + "%";
                    pst.setString(1, searchPattern);
                    pst.setString(2, searchPattern);
                    pst.setString(3, searchPattern);
                    
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            tableModel.addRow(new Object[]{
                                rs.getInt("num_medecin"),
                                rs.getString("nom"),
                                rs.getString("specialite"),
                                rs.getString("qualification"),
                                rs.getString("contact"),
                                rs.getString("nom_service") != null ? rs.getString("nom_service") : "N/A",
                                rs.getBigDecimal("tarif_consultation"),
                                rs.getString("statut")
                            });
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la recherche: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == btnRefresh) {
            loadMedecins();
            loadServices();
            clearForm();
            txtSearch.setText("");

        } else if (e.getSource() == btnManageSpecialites) {
            showSpecialitesDialog();
        }
    }

    private void showSpecialitesDialog() {
        JDialog dialog = new JDialog(this, "Gestion des Spécialités", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // List of specialties
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> specialitesList = new JList<>(listModel);
        
        // Load existing specialties
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT specialite FROM MEDECIN WHERE specialite IS NOT NULL AND specialite != '' ORDER BY specialite")) {
            
            while (rs.next()) {
                listModel.addElement(rs.getString("specialite"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Erreur: " + e.getMessage());
        }
        
        JScrollPane scrollPane = new JScrollPane(specialitesList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel btnPanel = new JPanel();
        JTextField txtNewSpecialite = new JTextField(20);
        JButton btnAdd = new JButton("Ajouter");
        JButton btnRemove = new JButton("Supprimer");
        JButton btnClose = new JButton("Fermer");
        
        btnAdd.addActionListener(e -> {
            String newSpec = txtNewSpecialite.getText().trim();
            if (!newSpec.isEmpty() && !listModel.contains(newSpec)) {
                listModel.addElement(newSpec);
                txtNewSpecialite.setText("");
            }
        });
        
        btnRemove.addActionListener(e -> {
            int index = specialitesList.getSelectedIndex();
            if (index >= 0) {
                listModel.remove(index);
            }
        });
        
        btnClose.addActionListener(e -> dialog.dispose());
        
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Nouvelle spécialité:"));
        inputPanel.add(txtNewSpecialite);
        inputPanel.add(btnAdd);
        inputPanel.add(btnRemove);
        inputPanel.add(btnClose);
        
        panel.add(inputPanel, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    // ================== MAIN ==================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new MedecinPage("Admin", "Administrateur");
    }
}
