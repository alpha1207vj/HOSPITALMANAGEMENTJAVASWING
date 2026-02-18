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
 * Rapports Médicaux — schéma réel vérifié.
 *
 * Corrections :
 *   - cout → tarif dans toutes les requêtes SQL
 *   - diagnostic récupéré depuis la table DIAGNOSTIC (pas une colonne de CONSULTATION)
 *   - traitement récupéré depuis la table TRAITEMENT
 */
public class ReportPage extends JFrame implements ActionListener {

    JComboBox<String> comboPatient, comboRapport;
    JTextField        txtDateDebut, txtDateFin;
    JTextArea         txtReport;
    JTable            reportTable;
    DefaultTableModel tableModel;
    JButton           btnGenerate, btnExportTxt, btnClose;
    JLabel            lblStatPatients, lblStatConsultations, lblStatAujourdhui;

    public ReportPage() {
        setTitle("Rapports Médicaux - Système de Gestion Hospitalière");
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(240, 242, 245));

        add(createHeader(),    BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);
        loadStats();
        loadPatientCombo();
    }

    // ==================== HEADER ====================
    private JPanel createHeader() {
        JPanel header = new JPanel(null);
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(1280, 90));

        JLabel title = new JLabel("📊 Rapports Médicaux");
        title.setBounds(40, 22, 500, 45);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title);

        lblStatPatients      = new JLabel("...", SwingConstants.CENTER);
        lblStatConsultations = new JLabel("...", SwingConstants.CENTER);
        lblStatAujourdhui    = new JLabel("...", SwingConstants.CENTER);

        for (JLabel l : new JLabel[]{lblStatPatients, lblStatConsultations, lblStatAujourdhui}) {
            l.setFont(new Font("Segoe UI", Font.BOLD, 26));
            l.setForeground(Color.WHITE);
        }

        header.add(statCard("Patients",      lblStatPatients,      new Color(52, 152, 219), 750));
        header.add(statCard("Consultations", lblStatConsultations, new Color(46, 204, 113), 920));
        header.add(statCard("Aujourd'hui",   lblStatAujourdhui,    new Color(230, 126, 34),  1090));
        return header;
    }

    private JPanel statCard(String title, JLabel valueLabel, Color color, int x) {
        JPanel card = new JPanel(null);
        card.setBounds(x, 15, 155, 65);
        card.setBackground(color);

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setBounds(0, 5, 155, 20);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setForeground(new Color(255, 255, 255, 200));
        card.add(t);

        valueLabel.setBounds(0, 25, 155, 35);
        card.add(valueLabel);
        return card;
    }

    // ==================== MAIN PANEL ====================
    private JPanel createMainPanel() {
        JPanel main = new JPanel(null);
        main.setBackground(new Color(240, 242, 245));

        // Options panel
        JPanel optPanel = new JPanel(null);
        optPanel.setBounds(20, 15, 370, 680);
        optPanel.setBackground(Color.WHITE);
        optPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)), "Options du Rapport"));

        int y = 25;

        JLabel lblType = new JLabel("Type de rapport :");
        lblType.setBounds(15, y, 340, 22);
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 13));
        optPanel.add(lblType);
        y += 28;

        comboRapport = new JComboBox<>(new String[]{
            "Rapport par Patient",
            "Rapport Périodique",
            "Statistiques Globales",
            "Top Médecins",
            "Consultations par Mois"
        });
        comboRapport.setBounds(15, y, 340, 28);
        comboRapport.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        optPanel.add(comboRapport);
        y += 45;

        optPanel.add(sep(15, y, 340)); y += 15;

        JLabel lblPat = new JLabel("Patient (rapport par patient) :");
        lblPat.setBounds(15, y, 340, 22);
        lblPat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        optPanel.add(lblPat);
        y += 28;

        comboPatient = new JComboBox<>();
        comboPatient.setBounds(15, y, 340, 28);
        comboPatient.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        optPanel.add(comboPatient);
        y += 45;

        optPanel.add(sep(15, y, 340)); y += 15;

        JLabel lblDates = new JLabel("Période (rapport périodique) :");
        lblDates.setBounds(15, y, 340, 22);
        lblDates.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        optPanel.add(lblDates);
        y += 28;

        JLabel lblDeb = new JLabel("Du :");
        lblDeb.setBounds(15, y, 40, 25);
        optPanel.add(lblDeb);

        txtDateDebut = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(
                new Date(System.currentTimeMillis() - 30L * 24 * 3600 * 1000)));
        txtDateDebut.setBounds(55, y, 120, 26);
        txtDateDebut.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        optPanel.add(txtDateDebut);

        JLabel lblFin = new JLabel("Au :");
        lblFin.setBounds(185, y, 40, 25);
        optPanel.add(lblFin);

        txtDateFin = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtDateFin.setBounds(225, y, 130, 26);
        txtDateFin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        optPanel.add(txtDateFin);
        y += 52;

        optPanel.add(sep(15, y, 340)); y += 20;

        btnGenerate = makeBtn("📋 Générer le rapport", new Color(41, 128, 185));
        btnGenerate.setBounds(15, y, 340, 40);
        btnGenerate.addActionListener(this);
        optPanel.add(btnGenerate);
        y += 55;

        btnExportTxt = makeBtn("💾 Exporter (TXT)", new Color(46, 204, 113));
        btnExportTxt.setBounds(15, y, 340, 35);
        btnExportTxt.addActionListener(this);
        optPanel.add(btnExportTxt);
        y += 50;

        btnClose = makeBtn("❌ Fermer", new Color(192, 57, 43));
        btnClose.setBounds(15, y, 340, 35);
        btnClose.addActionListener(this);
        optPanel.add(btnClose);

        main.add(optPanel);

        // Result panel
        JPanel resultPanel = new JPanel(new BorderLayout(0, 10));
        resultPanel.setBounds(405, 15, 855, 680);
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)), "Résultat du Rapport"));

        txtReport = new JTextArea();
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtReport.setBackground(new Color(248, 249, 250));
        txtReport.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane spText = new JScrollPane(txtReport);
        spText.setPreferredSize(new Dimension(855, 180));
        resultPanel.add(spText, BorderLayout.NORTH);

        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(26);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        reportTable.getTableHeader().setBackground(new Color(52, 73, 94));
        reportTable.getTableHeader().setForeground(Color.WHITE);
        reportTable.setSelectionBackground(new Color(52, 152, 219));
        reportTable.setSelectionForeground(Color.WHITE);
        resultPanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        main.add(resultPanel);
        return main;
    }

    // ==================== STATS ====================
    private void loadStats() {
        try {
            lblStatPatients.setText(String.valueOf(PatientDAO.countPatients()));
            lblStatConsultations.setText(String.valueOf(ConsultationDAO.countConsultations()));
            lblStatAujourdhui.setText(String.valueOf(ConsultationDAO.countConsultationsAujourdhui()));
        } catch (SQLException ex) {
            lblStatPatients.setText("?");
            lblStatConsultations.setText("?");
            lblStatAujourdhui.setText("?");
        }
    }

    private void loadPatientCombo() {
        comboPatient.removeAllItems();
        comboPatient.addItem("-- Sélectionner patient --");
        try {
            for (PatientDAO.Patient p : PatientDAO.getAllPatients())
                comboPatient.addItem(p.numPatient + " - " + p.nom + " " + p.prenom);
        } catch (SQLException ex) {
            showError("Chargement patients : " + ex.getMessage());
        }
    }

    // ==================== RAPPORTS ====================

    /** Rapport complet par patient : infos + consultations + diagnostics */
    private void genererRapportPatient() {
        String sel = (String) comboPatient.getSelectedItem();
        if (sel == null || sel.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un patient.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int numPatient = Integer.parseInt(sel.split(" - ")[0]);

        try {
            PatientDAO.Patient p = PatientDAO.getPatientById(numPatient);
            if (p == null) { showError("Patient introuvable."); return; }

            List<ConsultationDAO.Consultation> consults =
                    ConsultationDAO.getConsultationsByPatient(numPatient);

            StringBuilder sb = new StringBuilder();
            sb.append("══════════════════════════════════════════════════════\n");
            sb.append("  RAPPORT MÉDICAL — DOSSIER PATIENT\n");
            sb.append("══════════════════════════════════════════════════════\n");
            sb.append(String.format("  Généré le      : %s\n\n",
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())));
            sb.append(String.format("  Nom            : %s %s\n", p.nom, p.prenom));
            sb.append(String.format("  ID             : %d\n", p.numPatient));
            sb.append(String.format("  Naissance      : %s\n", nvl(p.dateNaissance)));
            sb.append(String.format("  Sexe           : %s\n", nvl(p.sexe)));
            sb.append(String.format("  Groupe sanguin : %s\n", nvl(p.groupeSanguin)));
            sb.append(String.format("  Téléphone      : %s\n", nvl(p.telephone)));
            sb.append(String.format("  N° National    : %s\n", nvl(p.numeroNational)));
            sb.append(String.format("  Antécédents    : %s\n", nvl(p.antecedents)));
            sb.append(String.format("──────────────────────────────────────────────────────\n"));
            sb.append(String.format("  CONSULTATIONS (%d)\n", consults.size()));
            sb.append("──────────────────────────────────────────────────────\n");
            txtReport.setText(sb.toString());

            tableModel.setRowCount(0);
            tableModel.setColumnIdentifiers(
                    new String[]{"#", "Date", "Médecin", "Symptômes", "Diagnostic", "Gravité", "Tarif (DH)"});

            int i = 1;
            for (ConsultationDAO.Consultation c : consults) {
                // Récupérer le premier diagnostic lié
                List<ConsultationDAO.Diagnostic> diags =
                        ConsultationDAO.getDiagnosticsByConsultation(c.numConsultation);
                String diagDesc   = diags.isEmpty() ? "—" : nvl(diags.get(0).description);
                String diagGravite = diags.isEmpty() ? "—" : nvl(diags.get(0).gravite);

                tableModel.addRow(new Object[]{
                    i++,
                    nvl(c.dateConsultation),
                    nvl(c.nomMedecin),
                    truncate(nvl(c.symptomes), 35),
                    truncate(diagDesc, 35),
                    diagGravite,
                    c.tarif != null ? c.tarif.toPlainString() + " DH" : "0.00 DH"  // ✅ tarif
                });
            }

        } catch (SQLException ex) {
            showError("Erreur rapport patient : " + ex.getMessage());
        }
    }

    /** Rapport périodique : consultations + tarifs sur une plage */
    private void genererRapportPeriodique() {
        String debut = txtDateDebut.getText().trim();
        String fin   = txtDateFin.getText().trim();
        if (debut.isEmpty() || fin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Entrez les dates.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Utilise tarif (✅ colonne réelle)
        String sql =
            "SELECT c.num_consultation, p.nom, p.prenom, m.nom AS nom_medecin, " +
            "       c.date_consultation, c.symptomes, c.observations, c.tarif " +
            "FROM CONSULTATION c " +
            "JOIN ADMISSION  a ON c.num_admission = a.num_admission " +
            "JOIN PATIENT    p ON a.num_patient   = p.num_patient " +
            "LEFT JOIN MEDECIN m ON c.num_medecin = m.num_medecin " +
            "WHERE DATE(c.date_consultation) BETWEEN ? AND ? " +
            "ORDER BY c.date_consultation";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, debut);
            pst.setString(2, fin);

            tableModel.setRowCount(0);
            tableModel.setColumnIdentifiers(
                    new String[]{"ID", "Patient", "Médecin", "Date", "Symptômes", "Tarif (DH)"});

            int count = 0;
            double total = 0;

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    count++;
                    double tarif = rs.getBigDecimal("tarif") != null ?
                            rs.getBigDecimal("tarif").doubleValue() : 0;
                    total += tarif;
                    tableModel.addRow(new Object[]{
                        rs.getInt("num_consultation"),
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        nvl(rs.getString("nom_medecin")),
                        rs.getString("date_consultation"),
                        truncate(nvl(rs.getString("symptomes")), 40),
                        String.format("%.2f DH", tarif)
                    });
                }
            }

            txtReport.setText(String.format(
                "══════════════════════════════════════════════════════\n" +
                "  RAPPORT PÉRIODIQUE : %s → %s\n" +
                "══════════════════════════════════════════════════════\n" +
                "  Total consultations : %d\n" +
                "  Revenus (tarifs)    : %.2f DH\n" +
                "  Généré le           : %s\n",
                debut, fin, count, total,
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())
            ));

        } catch (SQLException ex) {
            showError("Erreur rapport périodique : " + ex.getMessage());
        }
    }

    /** Statistiques globales */
    private void genererStatsGlobales() {
        try {
            int nbPat  = PatientDAO.countPatients();
            int nbCons = ConsultationDAO.countConsultations();
            int nbAuj  = ConsultationDAO.countConsultationsAujourdhui();

            int nbExam = 0, nbDiag = 0;
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM EXAMEN")) {
                    if (rs.next()) nbExam = rs.getInt(1);
                }
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM DIAGNOSTIC")) {
                    if (rs.next()) nbDiag = rs.getInt(1);
                }
            }

            txtReport.setText(String.format(
                "══════════════════════════════════════════════════════\n" +
                "  STATISTIQUES GLOBALES\n" +
                "══════════════════════════════════════════════════════\n" +
                "  Patients enregistrés    : %d\n" +
                "  Consultations totales   : %d\n" +
                "  Consultations auj.      : %d\n" +
                "  Diagnostics enregistrés : %d\n" +
                "  Examens totaux          : %d\n" +
                "  Généré le               : %s\n",
                nbPat, nbCons, nbAuj, nbDiag, nbExam,
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())
            ));

            tableModel.setRowCount(0);
            tableModel.setColumnIdentifiers(new String[]{"Indicateur", "Valeur"});
            tableModel.addRow(new Object[]{"Patients enregistrés",      nbPat});
            tableModel.addRow(new Object[]{"Consultations totales",      nbCons});
            tableModel.addRow(new Object[]{"Consultations aujourd'hui",  nbAuj});
            tableModel.addRow(new Object[]{"Diagnostics enregistrés",   nbDiag});
            tableModel.addRow(new Object[]{"Examens totaux",             nbExam});

        } catch (SQLException ex) {
            showError("Erreur statistiques : " + ex.getMessage());
        }
    }

    /** Top 5 médecins par nombre de consultations */
    private void genererTopMedecins() {
        try {
            List<Object[]> top = ConsultationDAO.getTopMedecins();
            tableModel.setRowCount(0);
            tableModel.setColumnIdentifiers(new String[]{"Rang", "Médecin", "Consultations"});
            StringBuilder sb = new StringBuilder();
            sb.append("══════════════════════════════════════════════════════\n");
            sb.append("  TOP MÉDECINS PAR CONSULTATIONS\n");
            sb.append("══════════════════════════════════════════════════════\n");
            int rank = 1;
            for (Object[] row : top) {
                tableModel.addRow(new Object[]{rank, row[0], row[1]});
                sb.append(String.format("  #%d  %-30s %d consultation(s)\n", rank++, row[0], row[1]));
            }
            sb.append(String.format("\n  Généré le : %s\n",
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())));
            txtReport.setText(sb.toString());
        } catch (SQLException ex) {
            showError("Erreur top médecins : " + ex.getMessage());
        }
    }

    /** Consultations par mois — 12 derniers mois */
    private void genererConsultationsParMois() {
        try {
            List<Object[]> data = ConsultationDAO.getConsultationsParMois();
            tableModel.setRowCount(0);
            tableModel.setColumnIdentifiers(new String[]{"Mois", "Nombre de consultations"});
            StringBuilder sb = new StringBuilder();
            sb.append("══════════════════════════════════════════════════════\n");
            sb.append("  CONSULTATIONS PAR MOIS (12 derniers mois)\n");
            sb.append("══════════════════════════════════════════════════════\n");
            for (Object[] row : data) {
                tableModel.addRow(row);
                sb.append(String.format("  %s : %s\n", row[0], row[1]));
            }
            sb.append(String.format("\n  Généré le : %s\n",
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())));
            txtReport.setText(sb.toString());
        } catch (SQLException ex) {
            showError("Erreur par mois : " + ex.getMessage());
        }
    }

    // ==================== EXPORT TXT ====================
    private void exporterTxt() {
        if (txtReport.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Générez d'abord un rapport.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("rapport_" +
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter fw = new java.io.FileWriter(fc.getSelectedFile())) {
                fw.write(txtReport.getText());
                fw.write("\n\n--- Données tabulaires ---\n");
                for (int c = 0; c < tableModel.getColumnCount(); c++)
                    fw.write(String.format("%-28s", tableModel.getColumnName(c)));
                fw.write("\n");
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < tableModel.getColumnCount(); c++)
                        fw.write(String.format("%-28s",
                                tableModel.getValueAt(r, c) != null ? tableModel.getValueAt(r, c).toString() : ""));
                    fw.write("\n");
                }
                JOptionPane.showMessageDialog(this, "✅ Exporté : " + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                showError("Erreur export : " + ex.getMessage());
            }
        }
    }

    // ==================== ACTIONS ====================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClose)    { dispose(); return; }
        if (e.getSource() == btnExportTxt) { exporterTxt(); return; }
        if (e.getSource() == btnGenerate) {
            switch (comboRapport.getSelectedIndex()) {
                case 0: genererRapportPatient();       break;
                case 1: genererRapportPeriodique();    break;
                case 2: genererStatsGlobales();        break;
                case 3: genererTopMedecins();          break;
                case 4: genererConsultationsParMois(); break;
            }
            loadStats();
        }
    }

    // ==================== UTILS ====================
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

    private JSeparator sep(int x, int y, int w) {
        JSeparator s = new JSeparator();
        s.setBounds(x, y, w, 2);
        return s;
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
        new ReportPage();
    }
}