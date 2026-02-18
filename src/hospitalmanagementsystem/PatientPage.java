package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Gestion des Patients — connectée à la base de données via PatientDAO.
 * Corrections : champ numeroNational (était numNational)
 */
public class PatientPage extends JFrame implements ActionListener {

    JTextField txtSearch, txtNum, txtNom, txtPrenom, txtDateNaissance,
               txtSexe, txtAdresse, txtTelephone, txtEmail,
               txtGroupeSanguin, txtNumeroNational, txtContactUrgence;
    JTextArea  txtAntecedents;
    JButton    btnSearch, btnAdd, btnUpdate, btnRemove, btnRefresh, btnClear;
    JTable     patientTable;
    DefaultTableModel tableModel;

    public PatientPage(String username, String role) {
        setTitle("Gestion des Patients - Système de Gestion Hospitalière");
        setSize(1300, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(240, 242, 245));

        add(createHeaderPanel(username, role), BorderLayout.NORTH);
        add(createMainPanel(),                 BorderLayout.CENTER);

        setVisible(true);
        loadPatients();
    }

    private JPanel createHeaderPanel(String username, String role) {
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(1300, 100));
        header.setBackground(new Color(41, 128, 185));

        JLabel title = new JLabel("👥 Gestion des Patients");
        title.setBounds(50, 30, 500, 40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title);

        JLabel user = new JLabel("Utilisateur: " + username + " | Rôle: " + role);
        user.setBounds(900, 35, 380, 25);
        user.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        user.setForeground(Color.WHITE);
        header.add(user);

        return header;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(null);
        main.setBackground(new Color(240, 242, 245));

        // ---- SEARCH BAR ----
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        searchPanel.setBounds(30, 15, 1230, 60);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)), "Rechercher Patient"));

        txtSearch  = new JTextField(30);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSearch  = makeBtn("🔍 Rechercher", new Color(41, 128, 185));
        btnRefresh = makeBtn("🔄 Actualiser", new Color(149, 165, 166));
        btnClear   = makeBtn("✖ Effacer",     new Color(127, 140, 141));

        btnSearch.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnClear.addActionListener(this);

        searchPanel.add(new JLabel("Nom / Téléphone / ID :"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        searchPanel.add(btnClear);
        main.add(searchPanel);

        // ---- FORM PANEL ----
        JPanel formPanel = new JPanel(null);
        formPanel.setBounds(30, 90, 550, 610);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)), "Informations Patient"));

        String[] labels = {
            "Num Patient", "Nom *", "Prénom *",
            "Date Naissance (AAAA-MM-JJ)", "Sexe (M/F)",
            "Adresse", "Téléphone", "Email",
            "Groupe Sanguin", "Numéro National", "Contact Urgence"
        };

        txtNum             = makeField(true);
        txtNom             = makeField(false);
        txtPrenom          = makeField(false);
        txtDateNaissance   = makeField(false);
        txtSexe            = makeField(false);
        txtAdresse         = makeField(false);
        txtTelephone       = makeField(false);
        txtEmail           = makeField(false);
        txtGroupeSanguin   = makeField(false);
        txtNumeroNational  = makeField(false);   // ✅ corrigé
        txtContactUrgence  = makeField(false);

        JTextField[] fields = {
            txtNum, txtNom, txtPrenom, txtDateNaissance, txtSexe,
            txtAdresse, txtTelephone, txtEmail,
            txtGroupeSanguin, txtNumeroNational, txtContactUrgence
        };

        int y = 25;
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i] + ":");
            lbl.setBounds(15, y, 200, 22);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            formPanel.add(lbl);
            fields[i].setBounds(220, y, 310, 25);
            formPanel.add(fields[i]);
            y += 38;
        }

        JLabel lblAnt = new JLabel("Antécédents Médicaux:");
        lblAnt.setBounds(15, y, 200, 22);
        lblAnt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblAnt);

        txtAntecedents = new JTextArea(3, 20);
        txtAntecedents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAntecedents.setLineWrap(true);
        txtAntecedents.setWrapStyleWord(true);
        JScrollPane spAnt = new JScrollPane(txtAntecedents);
        spAnt.setBounds(220, y, 310, 65);
        formPanel.add(spAnt);

        main.add(formPanel);

        // ---- TABLE PANEL ----
        JPanel tablePanel = new JPanel(new BorderLayout());
        // Hauteur réduite pour laisser la place aux boutons comme dans MedecinPage
        tablePanel.setBounds(600, 110, 660, 420);
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)), "Liste des Patients"));

        String[] cols = {
            "ID", "Nom", "Prénom", "Naissance", "Sexe",
            "Téléphone", "Email", "Groupe Sanguin", "N° National"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(26);
        patientTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = patientTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(true);
        header.setBackground(new Color(236, 240, 241));      // gris clair
        header.setForeground(new Color(44, 62, 80));         // texte foncé

        patientTable.setSelectionBackground(new Color(52, 152, 219));
        patientTable.setSelectionForeground(Color.WHITE);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        patientTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = patientTable.getSelectedRow();
                if (row >= 0) loadRowToForm(row);
            }
        });

        tablePanel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        // ---- CRUD BUTTONS en bas du tableau ----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnPanel.setBackground(Color.WHITE);

        // Boutons CRUD alignés sur le style de MedecinPage (sans emoji)
        btnAdd    = makeBtn("Ajouter",   new Color(46, 204, 113));
        btnUpdate = makeBtn("Modifier",  new Color(52, 152, 219));
        btnRemove = makeBtn("Supprimer", new Color(192, 57, 43));

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

    // ==================== DATA ====================

    private void loadPatients() {
        tableModel.setRowCount(0);
        try {
            for (PatientDAO.Patient p : PatientDAO.getAllPatients()) {
                tableModel.addRow(new Object[]{
                    p.numPatient, p.nom, p.prenom,
                    nvl(p.dateNaissance), nvl(p.sexe),
                    nvl(p.telephone), nvl(p.email),
                    nvl(p.groupeSanguin),
                    nvl(p.numeroNational)     // ✅ corrigé
                });
            }
        } catch (SQLException ex) {
            showError("Erreur chargement patients : " + ex.getMessage());
        }
    }

    private void searchPatients() {
        String term = txtSearch.getText().trim();
        if (term.isEmpty()) { loadPatients(); return; }
        tableModel.setRowCount(0);
        try {
            for (PatientDAO.Patient p : PatientDAO.searchPatients(term)) {
                tableModel.addRow(new Object[]{
                    p.numPatient, p.nom, p.prenom,
                    nvl(p.dateNaissance), nvl(p.sexe),
                    nvl(p.telephone), nvl(p.email),
                    nvl(p.groupeSanguin),
                    nvl(p.numeroNational)     // ✅ corrigé
                });
            }
            if (tableModel.getRowCount() == 0)
                JOptionPane.showMessageDialog(this, "Aucun patient trouvé pour : " + term);
        } catch (SQLException ex) {
            showError("Erreur recherche : " + ex.getMessage());
        }
    }

    private void loadRowToForm(int row) {
        txtNum.setText(tableModel.getValueAt(row, 0).toString());
        txtNom.setText(val(row, 1));
        txtPrenom.setText(val(row, 2));
        txtDateNaissance.setText(val(row, 3));
        txtSexe.setText(val(row, 4));
        txtTelephone.setText(val(row, 5));
        txtEmail.setText(val(row, 6));
        txtGroupeSanguin.setText(val(row, 7));
        txtNumeroNational.setText(val(row, 8));  // ✅ corrigé

        // Charger adresse, contact urgence, antécédents depuis la DB
        try {
            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            PatientDAO.Patient p = PatientDAO.getPatientById(id);
            if (p != null) {
                txtAdresse.setText(nvl(p.adresse));
                txtContactUrgence.setText(nvl(p.contactUrgence));
                txtAntecedents.setText(nvl(p.antecedents));
            }
        } catch (SQLException ex) {
            showError("Erreur chargement détails : " + ex.getMessage());
        }
    }

    // ==================== ACTIONS ====================

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnSearch)  { searchPatients(); return; }
        if (e.getSource() == btnRefresh) { loadPatients(); clearForm(); txtSearch.setText(""); return; }
        if (e.getSource() == btnClear)   { clearForm(); return; }

        if (e.getSource() == btnAdd) {
            if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le Nom et le Prénom sont obligatoires.",
                        "Champs manquants", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int newId = PatientDAO.addPatient(buildFromForm());
                if (newId > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Patient ajouté (ID : " + newId + ")");
                    clearForm();
                    loadPatients();
                }
            } catch (SQLException ex) {
                showError("Erreur ajout : " + ex.getMessage());
            }
        }

        else if (e.getSource() == btnUpdate) {
            if (txtNum.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un patient dans la liste.",
                        "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                PatientDAO.Patient p = buildFromForm();
                p.numPatient = Integer.parseInt(txtNum.getText().trim());
                if (PatientDAO.updatePatient(p)) {
                    JOptionPane.showMessageDialog(this, "✅ Patient modifié.");
                    clearForm();
                    loadPatients();
                }
            } catch (SQLException ex) {
                showError("Erreur modification : " + ex.getMessage());
            }
        }

        else if (e.getSource() == btnRemove) {
            int row = patientTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un patient à supprimer.",
                        "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id  = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            String nom = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Supprimer le patient : " + nom + " ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (PatientDAO.deletePatient(id)) {
                        JOptionPane.showMessageDialog(this, "✅ Patient supprimé.");
                        clearForm();
                        loadPatients();
                    }
                } catch (SQLException ex) {
                    if ("23000".equals(ex.getSQLState()))
                        showError("Impossible de supprimer : ce patient a des admissions liées.");
                    else
                        showError("Erreur suppression : " + ex.getMessage());
                }
            }
        }
    }

    // ==================== HELPERS ====================

    private PatientDAO.Patient buildFromForm() {
        PatientDAO.Patient p = new PatientDAO.Patient();
        p.nom            = txtNom.getText().trim();
        p.prenom         = txtPrenom.getText().trim();
        p.dateNaissance  = txtDateNaissance.getText().trim();
        p.sexe           = txtSexe.getText().trim().toUpperCase();
        p.adresse        = txtAdresse.getText().trim();
        p.telephone      = txtTelephone.getText().trim();
        p.email          = txtEmail.getText().trim();
        p.groupeSanguin  = txtGroupeSanguin.getText().trim();
        p.numeroNational = txtNumeroNational.getText().trim();  // ✅ corrigé
        p.contactUrgence = txtContactUrgence.getText().trim();
        p.antecedents    = txtAntecedents.getText().trim();
        return p;
    }

    private void clearForm() {
        for (JTextField f : new JTextField[]{
            txtNum, txtNom, txtPrenom, txtDateNaissance, txtSexe,
            txtAdresse, txtTelephone, txtEmail,
            txtGroupeSanguin, txtNumeroNational, txtContactUrgence
        }) f.setText("");
        txtAntecedents.setText("");
        patientTable.clearSelection();
    }

    private JTextField makeField(boolean readOnly) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (readOnly) {
            f.setEditable(false);
            f.setBackground(new Color(230, 230, 230));
        }
        return f;
    }

    private JButton makeBtn(String label, Color bg) {
        JButton btn = new JButton(label);
        btn.setUI(new BasicButtonUI());
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private String nvl(String s) { return s != null ? s : ""; }
    private String val(int row, int col) {
        Object v = tableModel.getValueAt(row, col);
        return v != null ? v.toString() : "";
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new PatientPage("Admin", "Administrateur");
    }
}