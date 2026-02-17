package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

public class MedicinesPage extends JFrame implements ActionListener {

    // ================== Components ==================
    JTextField txtSearch, txtCode, txtName, txtDosage, txtPrice, txtFabricant, txtStock;
    JComboBox<String> comboForme;
    JButton btnSearch, btnAdd, btnUpdate, btnRemove, btnRefresh, btnUpdateStock;
    JTable medicineTable;
    DefaultTableModel tableModel;

    // ================== Constructor ==================
    public MedicinesPage(String username, String role) {
        setTitle("Gestion des Médicaments - Système de Gestion Hospitalière");
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
            loadMedicines();
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

        JLabel titleLabel = new JLabel("Gestion des Médicaments");
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
                "Rechercher Médicament"
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
        JPanel infoPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        infoPanel.setBounds(30, 110, 550, 320);
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Informations Médicament"
        ));

        txtCode = new JTextField();
        txtCode.setEditable(false);
        txtCode.setBackground(new Color(230, 230, 230));

        txtName = new JTextField();
        comboForme = new JComboBox<>(new String[]{
                "Comprimé", "Sirop", "Injection", "Gélule", "Gel", "Inhalateur", "Pommade", "Collyre"
        });
        txtDosage = new JTextField();
        txtFabricant = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();

        infoPanel.add(new JLabel("Code:"));
        infoPanel.add(txtCode);
        infoPanel.add(new JLabel("Nom Commercial:"));
        infoPanel.add(txtName);
        infoPanel.add(new JLabel("Forme:"));
        infoPanel.add(comboForme);
        infoPanel.add(new JLabel("Dosage:"));
        infoPanel.add(txtDosage);
        infoPanel.add(new JLabel("Fabricant:"));
        infoPanel.add(txtFabricant);
        infoPanel.add(new JLabel("Prix Unitaire:"));
        infoPanel.add(txtPrice);
        infoPanel.add(new JLabel("Stock:"));
        infoPanel.add(txtStock);

        main.add(infoPanel);

        // ---------- TABLE PANEL ----------
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBounds(600, 110, 550, 420);
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Liste des Médicaments"
        ));

        tableModel = new DefaultTableModel(
                new Object[]{"Code", "Nom", "Forme", "Dosage", "Fabricant", "Prix", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicineTable = new JTable(tableModel);
        medicineTable.setRowHeight(28);
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = medicineTable.getSelectedRow();
                if (row >= 0) {
                    loadMedicineToForm(row);
                }
            }
        });

        // Color code low stock
        medicineTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 6) { // Stock column
                    try {
                        int stock = Integer.parseInt(value.toString());
                        if (stock < 10) {
                            c.setBackground(new Color(255, 200, 200)); // Light red for low stock
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } catch (Exception e) {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                }
                
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Ajouter");
        btnUpdate = new JButton("Modifier");
        btnRemove = new JButton("Supprimer");
        btnUpdateStock = new JButton("Mettre à jour Stock");

        styleSuccessButton(btnAdd);
        stylePrimaryButton(btnUpdate);
        styleDangerButton(btnRemove);
        stylePrimaryButton(btnUpdateStock);

        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnRemove.addActionListener(this);
        btnUpdateStock.addActionListener(this);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnRemove);
        btnPanel.add(btnUpdateStock);

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
    private void loadMedicines() {
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                    "SELECT code_medicament, nom_commercial, forme, dosage, " +
                    "fabricant, prix_unitaire, stock " +
                    "FROM MEDICAMENT " +
                    "ORDER BY nom_commercial")) {
                
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("code_medicament"),
                        rs.getString("nom_commercial"),
                        rs.getString("forme"),
                        rs.getString("dosage"),
                        rs.getString("fabricant"),
                        rs.getBigDecimal("prix_unitaire"),
                        rs.getInt("stock")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des médicaments: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des médicaments: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMedicineToForm(int row) {
        txtCode.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        
        String forme = tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "";
        for (int i = 0; i < comboForme.getItemCount(); i++) {
            if (comboForme.getItemAt(i).equals(forme)) {
                comboForme.setSelectedIndex(i);
                break;
            }
        }
        
        txtDosage.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        txtFabricant.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        txtPrice.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        txtStock.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
    }

    private void clearForm() {
        txtCode.setText("");
        txtName.setText("");
        comboForme.setSelectedIndex(0);
        txtDosage.setText("");
        txtFabricant.setText("");
        txtPrice.setText("");
        txtStock.setText("");
    }

    // ================== ACTIONS ==================
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnAdd) {
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir au moins le nom du médicament.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO MEDICAMENT (nom_commercial, forme, dosage, fabricant, prix_unitaire, stock) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, txtName.getText().trim());
                    pst.setString(2, comboForme.getSelectedItem().toString());
                    pst.setString(3, txtDosage.getText().trim().isEmpty() ? null : txtDosage.getText().trim());
                    pst.setString(4, txtFabricant.getText().trim().isEmpty() ? null : txtFabricant.getText().trim());
                    
                    try {
                        pst.setBigDecimal(5, txtPrice.getText().trim().isEmpty() ? 
                            java.math.BigDecimal.ZERO : new java.math.BigDecimal(txtPrice.getText().trim()));
                    } catch (NumberFormatException ex) {
                        pst.setBigDecimal(5, java.math.BigDecimal.ZERO);
                    }
                    
                    try {
                        pst.setInt(6, txtStock.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtStock.getText().trim()));
                    } catch (NumberFormatException ex) {
                        pst.setInt(6, 0);
                    }
                    
                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Médicament ajouté avec succès !");
                        clearForm();
                        loadMedicines();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == btnUpdate) {
            if (txtCode.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médicament à modifier.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE MEDICAMENT SET nom_commercial=?, forme=?, dosage=?, " +
                             "fabricant=?, prix_unitaire=?, stock=? WHERE code_medicament=?";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, txtName.getText().trim());
                    pst.setString(2, comboForme.getSelectedItem().toString());
                    pst.setString(3, txtDosage.getText().trim().isEmpty() ? null : txtDosage.getText().trim());
                    pst.setString(4, txtFabricant.getText().trim().isEmpty() ? null : txtFabricant.getText().trim());
                    
                    try {
                        pst.setBigDecimal(5, txtPrice.getText().trim().isEmpty() ? 
                            java.math.BigDecimal.ZERO : new java.math.BigDecimal(txtPrice.getText().trim()));
                    } catch (NumberFormatException ex) {
                        pst.setBigDecimal(5, java.math.BigDecimal.ZERO);
                    }
                    
                    try {
                        pst.setInt(6, txtStock.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtStock.getText().trim()));
                    } catch (NumberFormatException ex) {
                        pst.setInt(6, 0);
                    }
                    
                    pst.setInt(7, Integer.parseInt(txtCode.getText().trim()));
                    
                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Médicament modifié avec succès !");
                        clearForm();
                        loadMedicines();
                    } else {
                        JOptionPane.showMessageDialog(this, "Aucun médicament modifié.");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == btnRemove) {
            int row = medicineTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médicament à supprimer.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce médicament ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    int codeMedicament = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    String sql = "DELETE FROM MEDICAMENT WHERE code_medicament=?";
                    
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setInt(1, codeMedicament);
                        int rows = pst.executeUpdate();
                        if (rows > 0) {
                            JOptionPane.showMessageDialog(this, "Médicament supprimé avec succès !");
                            clearForm();
                            loadMedicines();
                        }
                    }
                } catch (SQLException ex) {
                    if (ex.getSQLState().equals("23000")) {
                        JOptionPane.showMessageDialog(this, 
                                "Impossible de supprimer ce médicament car il est référencé dans d'autres tables.",
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
                loadMedicines();
                return;
            }

            tableModel.setRowCount(0);
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT code_medicament, nom_commercial, forme, dosage, " +
                             "fabricant, prix_unitaire, stock " +
                             "FROM MEDICAMENT " +
                             "WHERE nom_commercial LIKE ? OR fabricant LIKE ? OR forme LIKE ? " +
                             "ORDER BY nom_commercial";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    String searchPattern = "%" + searchTerm + "%";
                    pst.setString(1, searchPattern);
                    pst.setString(2, searchPattern);
                    pst.setString(3, searchPattern);
                    
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            tableModel.addRow(new Object[]{
                                rs.getInt("code_medicament"),
                                rs.getString("nom_commercial"),
                                rs.getString("forme"),
                                rs.getString("dosage"),
                                rs.getString("fabricant"),
                                rs.getBigDecimal("prix_unitaire"),
                                rs.getInt("stock")
                            });
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la recherche: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == btnRefresh) {
            loadMedicines();
            clearForm();
            txtSearch.setText("");

        } else if (e.getSource() == btnUpdateStock) {
            int row = medicineTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médicament.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String input = JOptionPane.showInputDialog(this, 
                    "Entrez la nouvelle quantité de stock:", 
                    "Mise à jour du stock");
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int newStock = Integer.parseInt(input.trim());
                    int codeMedicament = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    
                    try (Connection conn = DBConnection.getConnection()) {
                        String sql = "UPDATE MEDICAMENT SET stock = ? WHERE code_medicament = ?";
                        
                        try (PreparedStatement pst = conn.prepareStatement(sql)) {
                            pst.setInt(1, newStock);
                            pst.setInt(2, codeMedicament);
                            
                            int rows = pst.executeUpdate();
                            if (rows > 0) {
                                JOptionPane.showMessageDialog(this, "Stock mis à jour avec succès !");
                                loadMedicines();
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.");
                }
            }
        }
    }

    // ================== MAIN ==================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new MedicinesPage("Admin", "Administrateur");
    }
}
