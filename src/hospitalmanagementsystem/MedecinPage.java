package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class MedecinPage extends JFrame implements ActionListener {

    // ================== Components ==================
    JTextField txtSearch, txtNum, txtNom, txtSpecialite, txtQualification, txtContact, txtTarif;
    JComboBox<String> comboStatut, comboService;
    JButton btnSearch, btnAdd, btnUpdate, btnRemove;
    JTable medecinTable;
    DefaultTableModel tableModel;

    int autoNumMedecin = 11; // simulate AUTO_INCREMENT start

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
        stylePrimaryButton(btnSearch);

        btnSearch.addActionListener(this);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

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
        comboService.addItem("Cardiology");
        comboService.addItem("Pediatrics");
        comboService.addItem("Neurology");
        comboService.addItem("Dermatology");
        comboService.addItem("Gynecology");

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
        );
        medecinTable = new JTable(tableModel);
        medecinTable.setRowHeight(28);
        medecinTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(medecinTable);
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
            txtNum.setText(String.valueOf(autoNumMedecin++));

            tableModel.addRow(new Object[]{
                    txtNum.getText(),
                    txtNom.getText(),
                    txtSpecialite.getText(),
                    txtQualification.getText(),
                    txtContact.getText(),
                    comboService.getSelectedItem(),
                    txtTarif.getText(),
                    comboStatut.getSelectedItem()
            });

        } else if (e.getSource() == btnUpdate) {
            int row = medecinTable.getSelectedRow();
            if (row >= 0) {
                tableModel.setValueAt(txtNom.getText(), row, 1);
                tableModel.setValueAt(txtSpecialite.getText(), row, 2);
                tableModel.setValueAt(txtQualification.getText(), row, 3);
                tableModel.setValueAt(txtContact.getText(), row, 4);
                tableModel.setValueAt(comboService.getSelectedItem(), row, 5);
                tableModel.setValueAt(txtTarif.getText(), row, 6);
                tableModel.setValueAt(comboStatut.getSelectedItem(), row, 7);
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un médecin.");
            }

        } else if (e.getSource() == btnRemove) {
            int row = medecinTable.getSelectedRow();
            if (row >= 0) tableModel.removeRow(row);

        } else if (e.getSource() == btnSearch) {
            JOptionPane.showMessageDialog(this, "Recherche simulée (à connecter à la base de données).");
        }
    }

    // ================== MAIN ==================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new MedecinPage("Admin", "Administrateur");
    }
}
