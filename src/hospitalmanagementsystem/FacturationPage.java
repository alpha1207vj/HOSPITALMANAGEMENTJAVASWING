package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FacturationPage extends JFrame {

    // ================== Palette (matches hospital system) ==================
    private static final Color PRIMARY       = new Color(41,  128, 185);
    private static final Color PRIMARY_DARK  = new Color(28,   93, 140);
    private static final Color PRIMARY_LIGHT = new Color(214, 234, 248);
    private static final Color SUCCESS       = new Color(39,  174,  96);
    private static final Color SUCCESS_LIGHT = new Color(213, 245, 227);
    private static final Color DANGER        = new Color(192,  57,  43);
    private static final Color DANGER_LIGHT  = new Color(250, 219, 216);
    private static final Color WARNING       = new Color(243, 156,  18);
    private static final Color WARNING_LIGHT = new Color(254, 243, 219);
    private static final Color BG            = new Color(245, 247, 250);
    private static final Color CARD          = Color.WHITE;
    private static final Color BORDER_COLOR  = new Color(218, 224, 232);
    private static final Color TEXT_MAIN     = new Color( 30,  39,  46);
    private static final Color TEXT_MUTED    = new Color(113, 128, 150);
    private static final Color ROW_ALT       = new Color(250, 251, 253);
    private static final Color ROW_SEL       = new Color(214, 234, 248);
    private static final Color TH_BG         = new Color( 44,  62,  80);
    private static final Color TH_FG         = Color.BLACK;

    // ================== Fonts ==================
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  24);
    private static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_MONO    = new Font("Consolas",  Font.BOLD,  13);

    // ================== Components ==================
    private JTable  table;
    private DefaultTableModel model;
    private JButton btnGenerate, btnRefresh;
    private Connection con;

    // Stat labels
    private JLabel lblTotalFactures, lblTotalMontant, lblPaye, lblNonPaye;

    // ================== Constructor ==================
    public FacturationPage() {
        setTitle("Facturation - Système de Gestion Hospitalière");
        setSize(1100, 1300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);

        connectDB();
        loadFacturations();

        setVisible(true);
    }

    // =========================================================
    // HEADER
    // =========================================================
    private JPanel buildHeader() {
        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_DARK, getWidth(), 0, PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(1100, 80));

        JLabel icon = new JLabel("🧾");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icon.setBounds(28, 16, 44, 48);
        header.add(icon);

        JLabel title = new JLabel("Gestion de la Facturation");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setBounds(80, 14, 450, 32);
        header.add(title);

        JLabel sub = new JLabel("Suivi des factures · Paiements · Totaux");
        sub.setFont(FONT_SMALL);
        sub.setForeground(new Color(255, 255, 255, 170));
        sub.setBounds(80, 46, 400, 18);
        header.add(sub);

        return header;
    }

    // =========================================================
    // CENTER  (stat cards + table + buttons)
    // =========================================================
    private JPanel buildCenter() {
        JPanel center = new JPanel(null);
        center.setBackground(BG);

        // ── STAT CARDS ROW ──────────────────────────────────
        center.add(buildStatCard("Total Factures",    "0",  PRIMARY, "📋", 20,  18, 230, 90));
        center.add(buildStatCard("Montant Total",     "0 €", new Color(142, 68, 173), "💰", 268, 18, 230, 90));
        center.add(buildStatCard("Payé",              "0 €", SUCCESS, "✅", 516, 18, 230, 90));
        center.add(buildStatCard("Non Payé",          "0 €", DANGER,  "⏳", 764, 18, 230, 90));

        // ── TABLE CARD ──────────────────────────────────────
        JPanel tableCard = roundCard();
        tableCard.setBounds(20, 126, 1050, 430);
        tableCard.setLayout(new BorderLayout(0, 0));

        // Card title bar
        JPanel titleBar = new JPanel(null);
        titleBar.setBackground(CARD);
        titleBar.setPreferredSize(new Dimension(1050, 46));

        JLabel tblTitle = new JLabel("Liste des Facturations");
        tblTitle.setFont(FONT_SECTION);
        tblTitle.setForeground(TEXT_MAIN);
        tblTitle.setBounds(18, 12, 300, 22);
        titleBar.add(tblTitle);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setBounds(0, 45, 1050, 1);
        titleBar.add(sep);

        tableCard.add(titleBar, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(
            new Object[]{"# Facture", "# Admission", "Patient", "Montant Total", "Statut Paiement"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(ROW_SEL);
                    c.setForeground(TEXT_MAIN);
                } else {
                    c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                    c.setForeground(TEXT_MAIN);
                    // Colour the statut column
                    if (col == 4) {
                        String val = getValueAt(row, col).toString();
                        if (val.equalsIgnoreCase("payé"))     { c.setForeground(SUCCESS); ((JLabel)c).setFont(FONT_SECTION); }
                        else if (val.equalsIgnoreCase("non payé")) { c.setForeground(DANGER);  ((JLabel)c).setFont(FONT_SECTION); }
                    }
                }
                return c;
            }
        };
        table.setRowHeight(34);
        table.setFont(FONT_BODY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_COLOR);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Header style
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(TH_BG);
        th.setForeground(TH_FG);
        th.setPreferredSize(new Dimension(0, 38));
        th.setBorder(BorderFactory.createEmptyBorder());
        th.setReorderingAllowed(false);

        // Column widths
        int[] colW = {100, 120, 280, 160, 160};
        for (int i = 0; i < colW.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
        }
        // Center numeric columns
        DefaultTableCellRenderer center2 = new DefaultTableCellRenderer();
        center2.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center2);
        table.getColumnModel().getColumn(1).setCellRenderer(center2);
        DefaultTableCellRenderer rightR = new DefaultTableCellRenderer();
        rightR.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightR);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(CARD);
        tableCard.add(scroll, BorderLayout.CENTER);

        center.add(tableCard);

        // ── BUTTON BAR ─────────────────────────────────────
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        btnBar.setOpaque(false);
        btnBar.setBounds(20, 566, 1050, 54);

        btnGenerate = styledBtn("⚡  Générer Facturations", SUCCESS,    Color.WHITE);
        btnRefresh  = styledBtn("🔄  Actualiser",           PRIMARY,    Color.WHITE);

        btnGenerate.addActionListener(e -> generateFacturations());
        btnRefresh.addActionListener(e  -> refreshFacturations());//refreshFacturations()

        btnBar.add(btnGenerate);
        btnBar.add(btnRefresh);
        center.add(btnBar);

        return center;
    }

    // =========================================================
    // STAT CARD BUILDER
    // =========================================================
    private JPanel buildStatCard(String label, String value, Color accent, String emoji,
                                  int x, int y, int w, int h) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                // Left accent bar
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, 5, getHeight(), 5, 5));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBounds(x, y, w, h);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(14, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        emojiLbl.setBounds(14, 12, 36, 36);
        card.add(emojiLbl);

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(TEXT_MUTED);
        lblTitle.setBounds(56, 14, 160, 18);
        card.add(lblTitle);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLbl.setForeground(accent);
        valLbl.setBounds(56, 36, 160, 30);
        card.add(valLbl);

        // Store reference by label text so we can update later
        switch (label) {
            case "Total Factures" -> lblTotalFactures = valLbl;
            case "Montant Total"  -> lblTotalMontant  = valLbl;
            case "Payé"           -> lblPaye           = valLbl;
            case "Non Payé"       -> lblNonPaye        = valLbl;
        }

        return card;
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private JPanel roundCard() {
        return new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
            }
        };
    }

    private JButton styledBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()
                    ? bg.darker()
                    : getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setUI(new BasicButtonUI());
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 40));
        return btn;
    }

    /** Custom border for rounded cards */
    static class RoundBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        RoundBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
    }

    // =========================================================
    // DATABASE
    // =========================================================
    private void connectDB() {
        try {
            String url  = "jdbc:mysql://localhost:3306/HOSPITAL?serverTimezone=UTC";
            con = DriverManager.getConnection(url, "root", "gedeon");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Connexion échouée: " + e.getMessage(),
                    "Erreur DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFacturations() {
        try {
            model.setRowCount(0);

            String query =
                "SELECT f.num_facture, f.num_admission, " +
                "CONCAT(p.nom,' ',p.prenom) AS patient, " +
                "f.montant_total, f.statut_paiement " +
                "FROM FACTURATION f " +
                "JOIN ADMISSION a ON f.num_admission = a.num_admission " +
                "JOIN PATIENT   p ON a.num_patient   = p.num_patient " +
                "ORDER BY f.num_facture";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            int countTotal = 0;
            double sumTotal = 0, sumPaye = 0, sumNonPaye = 0;

            while (rs.next()) {
                double montant  = rs.getDouble("montant_total");
                String statut   = rs.getString("statut_paiement");
                countTotal++;
                sumTotal += montant;
                if ("payé".equalsIgnoreCase(statut))     sumPaye    += montant;
                else                                      sumNonPaye += montant;

                model.addRow(new Object[]{
                    rs.getInt("num_facture"),
                    rs.getInt("num_admission"),
                    rs.getString("patient"),
                    String.format("%.2f €", montant),
                    statut
                });
            }

            // Update stat cards
            updateStats(countTotal, sumTotal, sumPaye, sumNonPaye);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

   

    private void generateFacturations() {
        try {
            String admQ = "SELECT num_admission FROM ADMISSION " +
                          "WHERE num_admission NOT IN (SELECT num_admission FROM FACTURATION)";
            ResultSet rsAdm = con.createStatement().executeQuery(admQ);

            int generated = 0;
            while (rsAdm.next()) {
                int numAdm = rsAdm.getInt("num_admission");
                double total = 0;

                // Consultations
                PreparedStatement pst = con.prepareStatement(
                    "SELECT SUM(tarif) AS t FROM CONSULTATION WHERE num_admission=?");
                pst.setInt(1, numAdm);
                ResultSet r = pst.executeQuery();
                if (r.next()) total += r.getDouble("t");

                // Examens
                pst = con.prepareStatement(
                    "SELECT SUM(prix) AS t FROM EXAMEN WHERE num_admission=?");
                pst.setInt(1, numAdm);
                r = pst.executeQuery();
                if (r.next()) total += r.getDouble("t");

                // Hospitalisation
                pst = con.prepareStatement(
                    "SELECT h.date_entree, IFNULL(h.date_sortie,CURDATE()) AS date_sortie, c.tarif_journalier " +
                    "FROM HOSPITALISATION h " +
                    "JOIN LIT l    ON h.num_lit    = l.num_lit " +
                    "JOIN CHAMBRE c ON l.num_chambre = c.num_chambre " +
                    "WHERE h.num_admission=?");
                pst.setInt(1, numAdm);
                r = pst.executeQuery();
                while (r.next()) {
                    LocalDate entree = r.getDate("date_entree").toLocalDate();
                    LocalDate sortie = r.getDate("date_sortie").toLocalDate();
                    long days = ChronoUnit.DAYS.between(entree, sortie);
                    if (days == 0) days = 1;
                    total += days * r.getDouble("tarif_journalier");
                }

                // Médicaments
                pst = con.prepareStatement(
                    "SELECT SUM(t.quantite * m.prix_unitaire) AS t " +
                    "FROM TRAITEMENT t " +
                    "JOIN MEDICAMENT m ON t.code_medicament = m.code_medicament " +
                    "JOIN CONSULTATION c ON t.num_consultation = c.num_consultation " +
                    "WHERE c.num_admission=?");
                pst.setInt(1, numAdm);
                r = pst.executeQuery();
                if (r.next()) total += r.getDouble("t");

                pst = con.prepareStatement(
                    "INSERT INTO FACTURATION(num_admission,montant_total,statut_paiement) VALUES(?,?,'non payé')");
                pst.setInt(1, numAdm);
                pst.setDouble(2, total);
                pst.executeUpdate();
                generated++;
            }

            if (generated == 0)
                JOptionPane.showMessageDialog(this,
                    "Toutes les admissions ont déjà une facturation.", "Info", JOptionPane.INFORMATION_MESSAGE);
            else
                JOptionPane.showMessageDialog(this,
                    generated + " facturation(s) générée(s) avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

            loadFacturations();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur génération: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
 private void refreshFacturations() {
        try {
            String updateQuery =
                "UPDATE FACTURATION f " +
                "JOIN montant_total_par_admission v ON f.num_admission = v.num_admission " +
                "SET f.montant_total = v.total";
            con.createStatement().executeUpdate(updateQuery);
            loadFacturations();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur actualisation: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updateStats(int count, double total, double paye, double nonPaye) {
        if (lblTotalFactures != null) lblTotalFactures.setText(String.valueOf(count));
        if (lblTotalMontant  != null) lblTotalMontant .setText(String.format("%.0f €", total));
        if (lblPaye          != null) lblPaye         .setText(String.format("%.0f €", paye));
        if (lblNonPaye       != null) lblNonPaye      .setText(String.format("%.0f €", nonPaye));
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(FacturationPage::new);
    }
}