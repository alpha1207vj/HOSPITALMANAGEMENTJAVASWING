package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Gestion des Consultations — schéma réel CONSULTATION + DIAGNOSTIC.
 *
 * Colonnes réelles CONSULTATION :
 *   num_consultation, num_admission, num_medecin,
 *   date_consultation (timestamp), symptomes, observations, tarif
 *
 * Corrections :
 *   - cout → tarif
 *   - diagnostic → table séparée DIAGNOSTIC (description, gravite)
 *   - traitement → table séparée TRAITEMENT (géré via ConsultationDAO)
 */
public class ConsultationPage extends JFrame implements ActionListener {

    // ======= Form fields =======
    JComboBox<String> comboMedecin, comboAdmission, comboGravite;
    JTextField        txtDate, txtTarif;
    JTextArea         txtSymptomes, txtObservations, txtDiagnosticDesc;

    // ======= Table =======
    JTable            consultationTable;
    DefaultTableModel tableModel;

    // ======= Boutons =======
    JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear, btnCancel;

    // ======= État =======
    private int selectedConsultationId = -1;

    // ==================== Constructor ====================
    public ConsultationPage() {
        setTitle("Système de Gestion Hospitalière - Consultations");
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(240, 242, 245));

        add(createHeader(),    BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);

        loadMedecins();
        loadAdmissions();
        loadConsultations();
    }

    // ==================== HEADER ====================
    private JPanel createHeader() {
        JPanel header = new JPanel(null);
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(1280, 90));

        JLabel title = new JLabel("🩺 Gestion des Consultations");
        title.setBounds(40, 22, 600, 45);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title);
        return header;
    }

    // ==================== MAIN PANEL ====================
    private JPanel createMainPanel() {
        JPanel main = new JPanel(null);
        main.setBackground(new Color(240, 242, 245));

        // ---- SEARCH / FILTER BAR ----
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        searchPanel.setBounds(20, 10, 1230, 52);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)), "Filtrer"));

        JTextField txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnSearch = makeBtn("🔍 Rechercher", new Color(41, 128, 185));
        btnRefresh = makeBtn("🔄 Tout afficher", new Color(149, 165, 166));
        btnSearch.addActionListener(ev -> filterConsultations(txtSearch.getText()));
        btnRefresh.addActionListener(this);

        searchPanel.add(new JLabel("Patient / Médecin :"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        main.add(searchPanel);

        // ---- FORM PANEL ----
        JPanel formPanel = new JPanel(null);
        formPanel.setBounds(20, 72, 560, 615);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Détail Consultation + Diagnostic"));

        int y = 20;

        addLabel(formPanel, "Admission :", 15, y);
        comboAdmission = new JComboBox<>();
        comboAdmission.setBounds(165, y, 375, 26);
        formPanel.add(comboAdmission);
        y += 38;

        addLabel(formPanel, "Médecin :", 15, y);
        comboMedecin = new JComboBox<>();
        comboMedecin.setBounds(165, y, 280, 26);
        formPanel.add(comboMedecin);
        y += 38;

        addLabel(formPanel, "Date (AAAA-MM-JJ) :", 15, y);
        txtDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtDate.setBounds(165, y, 150, 26);
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtDate);
        y += 38;

        addLabel(formPanel, "Tarif (DH) :", 15, y);
        txtTarif = new JTextField("0.00");
        txtTarif.setBounds(165, y, 100, 26);
        txtTarif.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtTarif);
        y += 38;

        // Séparateur
        JSeparator sep = new JSeparator();
        sep.setBounds(15, y, 525, 2);
        formPanel.add(sep);
        y += 12;

        JLabel lblConsult = new JLabel("── Données CONSULTATION ──");
        lblConsult.setBounds(15, y, 300, 22);
        lblConsult.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblConsult.setForeground(new Color(52, 73, 94));
        formPanel.add(lblConsult);
        y += 30;

        addLabel(formPanel, "Symptômes :", 15, y);
        txtSymptomes = new JTextArea();
        txtSymptomes.setLineWrap(true);
        txtSymptomes.setWrapStyleWord(true);
        JScrollPane sp1 = new JScrollPane(txtSymptomes);
        sp1.setBounds(165, y, 375, 70);
        formPanel.add(sp1);
        y += 82;

        addLabel(formPanel, "Observations :", 15, y);
        txtObservations = new JTextArea();
        txtObservations.setLineWrap(true);
        txtObservations.setWrapStyleWord(true);
        JScrollPane sp2 = new JScrollPane(txtObservations);
        sp2.setBounds(165, y, 375, 65);
        formPanel.add(sp2);
        y += 78;

        // Séparateur Diagnostic
        JSeparator sep2 = new JSeparator();
        sep2.setBounds(15, y, 525, 2);
        formPanel.add(sep2);
        y += 12;

        JLabel lblDiag = new JLabel("── Données DIAGNOSTIC (table séparée) ──");
        lblDiag.setBounds(15, y, 400, 22);
        lblDiag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDiag.setForeground(new Color(155, 89, 182));
        formPanel.add(lblDiag);
        y += 30;

        addLabel(formPanel, "Description :", 15, y);
        txtDiagnosticDesc = new JTextArea();
        txtDiagnosticDesc.setLineWrap(true);
        txtDiagnosticDesc.setWrapStyleWord(true);
        JScrollPane sp3 = new JScrollPane(txtDiagnosticDesc);
        sp3.setBounds(165, y, 375, 65);
        formPanel.add(sp3);
        y += 78;

        addLabel(formPanel, "Gravité :", 15, y);
        comboGravite = new JComboBox<>(new String[]{"léger", "modéré", "grave", "critique"});
        comboGravite.setBounds(165, y, 150, 26);
        formPanel.add(comboGravite);

        main.add(formPanel);

        // ---- TABLE PANEL ----
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBounds(600, 72, 660, 615);
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)), "Liste des Consultations"));

        String[] cols = {"ID", "Patient", "Médecin", "Date", "Symptômes", "Tarif (DH)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        consultationTable = new JTable(tableModel);
        consultationTable.setRowHeight(26);
        consultationTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        consultationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        consultationTable.getTableHeader().setBackground(new Color(52, 73, 94));
        consultationTable.getTableHeader().setForeground(Color.WHITE);
        consultationTable.setSelectionBackground(new Color(52, 152, 219));
        consultationTable.setSelectionForeground(Color.WHITE);
        consultationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        consultationTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = consultationTable.getSelectedRow();
                if (row >= 0) loadRowToForm(row);
            }
        });

        tablePanel.add(new JScrollPane(consultationTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnPanel.setBackground(Color.WHITE);
        btnAdd    = makeBtn("➕ Ajouter",   new Color(46, 204, 113));
        btnUpdate = makeBtn("✏ Modifier",   new Color(52, 152, 219));
        btnDelete = makeBtn("🗑 Supprimer", new Color(192, 57, 43));
        btnClear  = makeBtn("✖ Effacer",   new Color(127, 140, 141));
        btnCancel = makeBtn("❌ Fermer",    new Color(52, 73, 94));

        for (JButton b : new JButton[]{btnAdd, btnUpdate, btnDelete, btnClear, btnCancel})
            b.addActionListener(this);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnCancel);
        tablePanel.add(btnPanel, BorderLayout.SOUTH);

        main.add(tablePanel);
        return main;
    }

    // ==================== LOAD FROM DB ====================

    private void loadMedecins() {
        comboMedecin.removeAllItems();
        comboMedecin.addItem("-- Sélectionner médecin --");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT num_medecin, nom FROM MEDECIN WHERE statut='active' ORDER BY nom")) {
            while (rs.next())
                comboMedecin.addItem(rs.getInt("num_medecin") + " - " + rs.getString("nom"));
        } catch (SQLException ex) {
            showError("Chargement médecins : " + ex.getMessage());
        }
    }

    private void loadAdmissions() {
        comboAdmission.removeAllItems();
        comboAdmission.addItem("-- Sélectionner admission --");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT a.num_admission, p.nom, p.prenom " +
                     "FROM ADMISSION a " +
                     "JOIN PATIENT p ON a.num_patient = p.num_patient " +
                     "ORDER BY a.num_admission DESC LIMIT 200")) {
            while (rs.next())
                comboAdmission.addItem(rs.getInt("num_admission") + " - " +
                        rs.getString("nom") + " " + rs.getString("prenom"));
        } catch (SQLException ex) {
            showError("Chargement admissions : " + ex.getMessage());
        }
    }

    private void loadConsultations() {
        tableModel.setRowCount(0);
        selectedConsultationId = -1;
        try {
            for (ConsultationDAO.Consultation c : ConsultationDAO.getAllConsultations()) {
                tableModel.addRow(new Object[]{
                    c.numConsultation,
                    nvl(c.nomPatient),
                    nvl(c.nomMedecin),
                    nvl(c.dateConsultation),
                    truncate(nvl(c.symptomes), 45),
                    c.tarif != null ? c.tarif.toPlainString() : "0.00"  // ✅ tarif
                });
            }
        } catch (SQLException ex) {
            showError("Chargement consultations : " + ex.getMessage());
        }
    }

    private void filterConsultations(String term) {
        if (term.trim().isEmpty()) { loadConsultations(); return; }
        String low = term.toLowerCase();
        tableModel.setRowCount(0);
        try {
            for (ConsultationDAO.Consultation c : ConsultationDAO.getAllConsultations()) {
                if (nvl(c.nomPatient).toLowerCase().contains(low) ||
                    nvl(c.nomMedecin).toLowerCase().contains(low) ||
                    nvl(c.symptomes).toLowerCase().contains(low)) {
                    tableModel.addRow(new Object[]{
                        c.numConsultation, c.nomPatient, nvl(c.nomMedecin),
                        nvl(c.dateConsultation), truncate(nvl(c.symptomes), 45),
                        c.tarif != null ? c.tarif.toPlainString() : "0.00"
                    });
                }
            }
        } catch (SQLException ex) {
            showError("Filtre : " + ex.getMessage());
        }
    }

    private void loadRowToForm(int row) {
        selectedConsultationId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        try {
            for (ConsultationDAO.Consultation c : ConsultationDAO.getAllConsultations()) {
                if (c.numConsultation == selectedConsultationId) {
                    selectCombo(comboAdmission, String.valueOf(c.numAdmission));
                    selectCombo(comboMedecin,   String.valueOf(c.numMedecin));
                    txtDate.setText(nvl(c.dateConsultation));
                    txtTarif.setText(c.tarif != null ? c.tarif.toPlainString() : "0.00"); // ✅ tarif
                    txtSymptomes.setText(nvl(c.symptomes));
                    txtObservations.setText(nvl(c.observations));

                    // Charger le diagnostic lié (premier de la liste)
                    List<ConsultationDAO.Diagnostic> diags =
                            ConsultationDAO.getDiagnosticsByConsultation(selectedConsultationId);
                    if (!diags.isEmpty()) {
                        txtDiagnosticDesc.setText(nvl(diags.get(0).description));
                        comboGravite.setSelectedItem(nvl(diags.get(0).gravite));
                    } else {
                        txtDiagnosticDesc.setText("");
                        comboGravite.setSelectedIndex(0);
                    }
                    break;
                }
            }
        } catch (SQLException ex) {
            showError("Chargement consultation : " + ex.getMessage());
        }
    }

    // ==================== ACTIONS ====================

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnRefresh) {
            loadConsultations(); loadMedecins(); loadAdmissions();
            clearForm(); return;
        }
        if (e.getSource() == btnClear)  { clearForm(); return; }
        if (e.getSource() == btnCancel) { dispose(); return; }

        if (e.getSource() == btnAdd) {
            ConsultationDAO.Consultation c = buildFromForm();
            if (c == null) return;
            try {
                int newId = ConsultationDAO.addConsultation(c);
                if (newId > 0) {
                    // Ajouter le diagnostic si renseigné
                    String diagDesc = txtDiagnosticDesc.getText().trim();
                    if (!diagDesc.isEmpty()) {
                        ConsultationDAO.Diagnostic d = new ConsultationDAO.Diagnostic();
                        d.numConsultation = newId;
                        d.description     = diagDesc;
                        d.gravite         = comboGravite.getSelectedItem().toString();
                        ConsultationDAO.addDiagnostic(d);
                    }
                    JOptionPane.showMessageDialog(this, "✅ Consultation enregistrée (ID : " + newId + ")");
                    clearForm();
                    loadConsultations();
                }
            } catch (SQLException ex) {
                showError("Erreur ajout : " + ex.getMessage());
            }
        }

        else if (e.getSource() == btnUpdate) {
            if (selectedConsultationId < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez une consultation.",
                        "Attention", JOptionPane.WARNING_MESSAGE); return;
            }
            ConsultationDAO.Consultation c = buildFromForm();
            if (c == null) return;
            c.numConsultation = selectedConsultationId;
            try {
                ConsultationDAO.updateConsultation(c);

                // Mettre à jour ou créer le diagnostic
                String diagDesc = txtDiagnosticDesc.getText().trim();
                List<ConsultationDAO.Diagnostic> diags =
                        ConsultationDAO.getDiagnosticsByConsultation(selectedConsultationId);
                if (!diagDesc.isEmpty()) {
                    ConsultationDAO.Diagnostic d = new ConsultationDAO.Diagnostic();
                    d.numConsultation = selectedConsultationId;
                    d.description     = diagDesc;
                    d.gravite         = comboGravite.getSelectedItem().toString();
                    if (!diags.isEmpty()) {
                        d.numDiagnostic = diags.get(0).numDiagnostic;
                        ConsultationDAO.updateDiagnostic(d);
                    } else {
                        ConsultationDAO.addDiagnostic(d);
                    }
                }
                JOptionPane.showMessageDialog(this, "✅ Consultation modifiée.");
                clearForm();
                loadConsultations();
            } catch (SQLException ex) {
                showError("Erreur modification : " + ex.getMessage());
            }
        }

        else if (e.getSource() == btnDelete) {
            int row = consultationTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez une consultation.",
                        "Attention", JOptionPane.WARNING_MESSAGE); return;
            }
            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Supprimer la consultation #" + id + " (et son diagnostic / traitement) ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (ConsultationDAO.deleteConsultation(id)) {
                        JOptionPane.showMessageDialog(this, "✅ Consultation supprimée.");
                        clearForm();
                        loadConsultations();
                    }
                } catch (SQLException ex) {
                    showError("Erreur suppression : " + ex.getMessage());
                }
            }
        }
    }

    // ==================== HELPERS ====================

    private ConsultationDAO.Consultation buildFromForm() {
        String admStr = (String) comboAdmission.getSelectedItem();
        String medStr = (String) comboMedecin.getSelectedItem();
        if (admStr == null || admStr.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une admission.", "Attention", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (medStr == null || medStr.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un médecin.", "Attention", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        ConsultationDAO.Consultation c = new ConsultationDAO.Consultation();
        c.numAdmission     = Integer.parseInt(admStr.split(" - ")[0]);
        c.numMedecin       = Integer.parseInt(medStr.split(" - ")[0]);
        c.dateConsultation = txtDate.getText().trim();
        c.symptomes        = txtSymptomes.getText().trim();
        c.observations     = txtObservations.getText().trim();
        try {
            c.tarif = new java.math.BigDecimal(txtTarif.getText().trim().isEmpty() ? "0" : txtTarif.getText().trim());
        } catch (NumberFormatException ex) {
            c.tarif = java.math.BigDecimal.ZERO;
        }
        return c;
    }

    private void clearForm() {
        comboAdmission.setSelectedIndex(0);
        comboMedecin.setSelectedIndex(0);
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtTarif.setText("0.00");
        txtSymptomes.setText("");
        txtObservations.setText("");
        txtDiagnosticDesc.setText("");
        comboGravite.setSelectedIndex(0);
        selectedConsultationId = -1;
        consultationTable.clearSelection();
    }

    private void selectCombo(JComboBox<String> combo, String prefix) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).startsWith(prefix + " ")) {
                combo.setSelectedIndex(i); return;
            }
        }
    }

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 150, 25);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbl);
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
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return btn;
    }

    private String nvl(String s) { return s != null ? s : ""; }
    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new ConsultationPage();
    }
}