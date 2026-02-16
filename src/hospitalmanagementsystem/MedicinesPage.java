package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class MedicinesPage extends JFrame implements ActionListener {

    // ================== Components ==================
    JTextField txtSearch, txtCode, txtName, txtDosage, txtPrice, txtFabricant, txtStock;
    JComboBox<String> comboForme;
    JButton btnSearch, btnAdd, btnUpdate, btnRemove, btnSave, btnCancel;
    JTable medicineTable;
    DefaultTableModel tableModel;

    int autoCode = 1; // simulate AUTO_INCREMENT

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
        stylePrimaryButton(btnSearch);

        btnSearch.addActionListener(this);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

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
                "Comprimé", "Sirop", "Injection", "Gélule", "Gel", "Inhalateur"
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
        );
        medicineTable = new JTable(tableModel);
        medicineTable.setRowHeight(28);
        medicineTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Ajouter");
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
    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(new Color(41, 128, 185));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
    }

    private void styleSuccessButton(JButton btn) {
        btn.setBackground(new Color(46, 204, 113));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
    }

    private void styleDangerButton(JButton btn) {
        btn.setBackground(new Color(192, 57, 43));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
    }

    // ================== ACTIONS ==================
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnAdd) {
            txtCode.setText(String.valueOf(autoCode++));

            tableModel.addRow(new Object[]{
                    txtCode.getText(),
                    txtName.getText(),
                    comboForme.getSelectedItem(),
                    txtDosage.getText(),
                    txtFabricant.getText(),
                    txtPrice.getText(),
                    txtStock.getText()
            });

        } else if (e.getSource() == btnUpdate) {
            int row = medicineTable.getSelectedRow();
            if (row >= 0) {
                tableModel.setValueAt(txtName.getText(), row, 1);
                tableModel.setValueAt(comboForme.getSelectedItem(), row, 2);
                tableModel.setValueAt(txtDosage.getText(), row, 3);
                tableModel.setValueAt(txtFabricant.getText(), row, 4);
                tableModel.setValueAt(txtPrice.getText(), row, 5);
                tableModel.setValueAt(txtStock.getText(), row, 6);
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un médicament.");
            }

        } else if (e.getSource() == btnRemove) {
            int row = medicineTable.getSelectedRow();
            if (row >= 0) tableModel.removeRow(row);

        } else if (e.getSource() == btnSearch) {
            JOptionPane.showMessageDialog(this, "Recherche simulée.");
        }
    }

    // ================== MAIN ==================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new MedicinesPage("Admin", "Administrateur");
    }
}
