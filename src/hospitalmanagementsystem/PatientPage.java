package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class PatientPage extends JFrame implements ActionListener {

    // ================== Components ==================
    JTextField txtSearch, txtNum, txtNom, txtPrenom, txtDateNaissance, txtSexe, txtAdresse, txtTelephone, txtEmail, txtGroupeSanguin, txtNumNational, txtContactUrgence, txtAntecedents;
    JButton btnSearch, btnAdd, btnUpdate, btnRemove;
    JTable patientTable;
    DefaultTableModel tableModel;

    int autoNumPatient = 1; // simulate AUTO_INCREMENT

    // ================== Constructor ==================
    public PatientPage(String username, String role) {
        setTitle("Gestion des Patients - Système de Gestion Hospitalière");
        setSize(1300, 750);
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
        header.setPreferredSize(new Dimension(1300, 100));
        header.setBackground(new Color(41, 128, 185));

        JLabel titleLabel = new JLabel("Gestion des Patients");
        titleLabel.setBounds(50, 30, 400, 40);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        JLabel userLabel = new JLabel("Utilisateur: " + username + " | Rôle: " + role);
        userLabel.setBounds(900, 35, 350, 25);
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
        searchPanel.setBounds(30, 20, 1220, 70);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Rechercher Patient"
        ));

        txtSearch = new JTextField(30);
        btnSearch = new JButton("🔍 Rechercher");
        stylePrimaryButton(btnSearch);

        btnSearch.addActionListener(this);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        main.add(searchPanel);

        // ---------- INFO PANEL ----------
        JPanel infoPanel = new JPanel(new GridLayout(13, 2, 10, 10));
        infoPanel.setBounds(30, 110, 550, 520);
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Informations Patient"
        ));

        txtNum = new JTextField();
        txtNum.setEditable(false);
        txtNum.setBackground(new Color(230, 230, 230));

        txtNom = new JTextField();
        txtPrenom = new JTextField();
        txtDateNaissance = new JTextField();
        txtSexe = new JTextField();
        txtAdresse = new JTextField();
        txtTelephone = new JTextField();
        txtEmail = new JTextField();
        txtGroupeSanguin = new JTextField();
        txtNumNational = new JTextField();
        txtContactUrgence = new JTextField();
        txtAntecedents = new JTextField();

        infoPanel.add(new JLabel("Num Patient:"));
        infoPanel.add(txtNum);
        infoPanel.add(new JLabel("Nom:"));
        infoPanel.add(txtNom);
        infoPanel.add(new JLabel("Prénom:"));
        infoPanel.add(txtPrenom);
        infoPanel.add(new JLabel("Date de Naissance:"));
        infoPanel.add(txtDateNaissance);
        infoPanel.add(new JLabel("Sexe (M/F):"));
        infoPanel.add(txtSexe);
        infoPanel.add(new JLabel("Adresse:"));
        infoPanel.add(txtAdresse);
        infoPanel.add(new JLabel("Téléphone:"));
        infoPanel.add(txtTelephone);
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(txtEmail);
        infoPanel.add(new JLabel("Groupe Sanguin:"));
        infoPanel.add(txtGroupeSanguin);
        infoPanel.add(new JLabel("Numéro National:"));
        infoPanel.add(txtNumNational);
        infoPanel.add(new JLabel("Contact Urgence:"));
        infoPanel.add(txtContactUrgence);
        infoPanel.add(new JLabel("Antécédents Médicaux:"));
        infoPanel.add(txtAntecedents);

        main.add(infoPanel);

        // ---------- TABLE PANEL ----------
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBounds(600, 110, 650, 520);
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Liste des Patients"
        ));

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Num", "Nom", "Prénom", "Date Naissance", "Sexe", "Adresse", "Téléphone", "Email", 
                        "Groupe Sanguin", "Numéro National", "Contact Urgence", "Antécédents"
                }, 0
        );
        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(28);
        patientTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(patientTable);
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
            txtNum.setText(String.valueOf(autoNumPatient++));

            tableModel.addRow(new Object[]{
                    txtNum.getText(),
                    txtNom.getText(),
                    txtPrenom.getText(),
                    txtDateNaissance.getText(),
                    txtSexe.getText(),
                    txtAdresse.getText(),
                    txtTelephone.getText(),
                    txtEmail.getText(),
                    txtGroupeSanguin.getText(),
                    txtNumNational.getText(),
                    txtContactUrgence.getText(),
                    txtAntecedents.getText()
            });

        } else if (e.getSource() == btnUpdate) {
            int row = patientTable.getSelectedRow();
            if (row >= 0) {
                tableModel.setValueAt(txtNom.getText(), row, 1);
                tableModel.setValueAt(txtPrenom.getText(), row, 2);
                tableModel.setValueAt(txtDateNaissance.getText(), row, 3);
                tableModel.setValueAt(txtSexe.getText(), row, 4);
                tableModel.setValueAt(txtAdresse.getText(), row, 5);
                tableModel.setValueAt(txtTelephone.getText(), row, 6);
                tableModel.setValueAt(txtEmail.getText(), row, 7);
                tableModel.setValueAt(txtGroupeSanguin.getText(), row, 8);
                tableModel.setValueAt(txtNumNational.getText(), row, 9);
                tableModel.setValueAt(txtContactUrgence.getText(), row, 10);
                tableModel.setValueAt(txtAntecedents.getText(), row, 11);
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez un patient.");
            }

        } else if (e.getSource() == btnRemove) {
            int row = patientTable.getSelectedRow();
            if (row >= 0) tableModel.removeRow(row);

        } else if (e.getSource() == btnSearch) {
            JOptionPane.showMessageDialog(this, "Recherche simulée (à connecter à la base de données).");
        }
    }

    // ================== MAIN ==================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new PatientPage("Admin", "Administrateur");
    }
}
