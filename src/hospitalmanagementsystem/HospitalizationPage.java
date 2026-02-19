package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class HospitalizationPage extends JFrame {

    // ── DB-backed data ──────────────────────────────────────
    // pavillonId → pavillonName
    private final Map<Integer, String> pavillonMap = new LinkedHashMap<>();
    // chambreId  → chambreLabel  (for the currently selected pavillon)
    private final Map<Integer, String> chambreMap  = new LinkedHashMap<>();

    // ── UI ──────────────────────────────────────────────────
    private JComboBox<String> pavillonCombo, chambreCombo;
    private JPanel            bedGridPanel;
    private JTable            activeTable;
    private DefaultTableModel tableModel;

    // Stat labels (set via createStatCard)
    private JLabel lblTotalBeds, lblOccupied, lblFree, lblRate;

    // ── Combo index → DB id helpers ─────────────────────────
    private final List<Integer> pavillonIds = new ArrayList<>();
    private final List<Integer> chambreIds  = new ArrayList<>();

    // ============================================================
    public HospitalizationPage() {
        setTitle("Gestion des Hospitalisations");
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(240, 242, 245));

        initComponents();
        loadData();
        setVisible(true);
    }

    // ============================================================
    // UI CONSTRUCTION (unchanged structure, same look)
    // ============================================================
    private void initComponents() {
        add(createHeader(),  BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(900);
        split.setDividerSize(2);
        split.setBorder(null);
        split.setLeftComponent(createBedMapPanel());
        split.setRightComponent(createActiveListPanel());
        add(split, BorderLayout.CENTER);

        add(createFooter(), BorderLayout.SOUTH);
    }

    // ── HEADER ──────────────────────────────────────────────
    private JPanel createHeader() {
        JPanel h = new JPanel(null);
        h.setPreferredSize(new Dimension(1400, 120));
        h.setBackground(new Color(41, 128, 185));

        JLabel title = new JLabel("🛏️ Gestion des Hospitalisations");
        title.setBounds(40, 20, 600, 40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        h.add(title);

        JLabel sub = new JLabel("Vue d'ensemble des lits et patients hospitalisés");
        sub.setBounds(40, 65, 500, 25);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setForeground(new Color(236, 240, 241));
        h.add(sub);

        int x = 700;
        JPanel c1 = statCard("Total Lits",  "0", new Color(52, 152, 219));  c1.setBounds(x,       20, 150, 80); lblTotalBeds = (JLabel) c1.getComponent(1); h.add(c1);
        JPanel c2 = statCard("Occupés",     "0", new Color(231,  76,  60)); c2.setBounds(x+170,   20, 150, 80); lblOccupied  = (JLabel) c2.getComponent(1); h.add(c2);
        JPanel c3 = statCard("Libres",      "0", new Color(46,  204, 113)); c3.setBounds(x+340,   20, 150, 80); lblFree      = (JLabel) c3.getComponent(1); h.add(c3);
        JPanel c4 = statCard("Taux",        "0%",new Color(155,  89, 182)); c4.setBounds(x+510,   20, 150, 80); lblRate      = (JLabel) c4.getComponent(1); h.add(c4);

        return h;
    }

    private JPanel statCard(String title, String val, Color col) {
        JPanel p = new JPanel(null);
        p.setBackground(col);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,50), 1),
            BorderFactory.createEmptyBorder(10,10,10,10)));

        JLabel t = new JLabel(title);
        t.setBounds(10, 5, 130, 20);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(new Color(255,255,255,200));
        p.add(t);

        JLabel v = new JLabel(val);
        v.setBounds(10, 25, 130, 35);
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(Color.WHITE);
        p.add(v);

        return p;
    }

    // ── BED MAP PANEL ────────────────────────────────────────
    private JPanel createBedMapPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        // ---- Filter / action bar ----
        JPanel filterPanel = new JPanel(null);
        filterPanel.setPreferredSize(new Dimension(880, 120));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189,195,199), 1),
            BorderFactory.createEmptyBorder(15,20,15,20)));

        JLabel ft = new JLabel("📍 Gestion des Emplacements:");
        ft.setBounds(0, 0, 300, 25);
        ft.setFont(new Font("Segoe UI", Font.BOLD, 16));
        ft.setForeground(new Color(44,62,80));
        filterPanel.add(ft);

        addLabel(filterPanel, "Pavillon:", 20, 35);
        pavillonCombo = new JComboBox<>();
        pavillonCombo.setBounds(100, 35, 200, 30);
        pavillonCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pavillonCombo.addActionListener(e -> onPavillonChanged());
        filterPanel.add(pavillonCombo);

        addLabel(filterPanel, "Chambre:", 320, 35);
        chambreCombo = new JComboBox<>();
        chambreCombo.setBounds(400, 35, 200, 30);
        chambreCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chambreCombo.addActionListener(e -> loadBedGrid());
        filterPanel.add(chambreCombo);

        JButton btnRefresh = actionBtn("🔄", new Color(52,152,219));
        btnRefresh.setBounds(620, 35, 50, 30);
        btnRefresh.setToolTipText("Actualiser");
        btnRefresh.addActionListener(e -> loadData());
        filterPanel.add(btnRefresh);

        // ---- Management buttons ----
        JButton btnAddPav = actionBtn("➕ Pavillon",    new Color(46,204,113));  btnAddPav.setBounds(20,  75,130,30); btnAddPav.setForeground(Color.BLACK); btnAddPav.addActionListener(e -> addPavillon());    filterPanel.add(btnAddPav);
        JButton btnAddCh  = actionBtn("➕ Chambre",     new Color(52,152,219));  btnAddCh .setBounds(160, 75,130,30); btnAddCh .setForeground(Color.BLACK); btnAddCh .addActionListener(e -> addChambre());     filterPanel.add(btnAddCh);
        JButton btnAddLit = actionBtn("➕ Lit",         new Color(155,89,182));  btnAddLit.setBounds(300, 75,130,30); btnAddLit.setForeground(Color.BLACK); btnAddLit.addActionListener(e -> addBed());         filterPanel.add(btnAddLit);
        JButton btnAssign = actionBtn("👤 Assigner",    new Color(230,126,34));  btnAssign.setBounds(450, 75,130,30); btnAssign.setForeground(Color.BLACK); btnAssign.addActionListener(e -> assignPatientToBed()); filterPanel.add(btnAssign);

        panel.add(filterPanel, BorderLayout.NORTH);

        // ---- Bed grid ----
        bedGridPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        bedGridPanel.setBackground(new Color(240,242,245));
        bedGridPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JScrollPane scroll = new JScrollPane(bedGridPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(240,242,245));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ── ACTIVE LIST PANEL ────────────────────────────────────
    private JPanel createActiveListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBackground(new Color(240,242,245));
        panel.setBorder(BorderFactory.createEmptyBorder(20,10,20,20));

        JLabel title = new JLabel("📋 Hospitalisations Actives");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(44,62,80));
        title.setBorder(BorderFactory.createEmptyBorder(0,5,10,0));
        panel.add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Patient","Lit","Chambre","Entrée","Jours"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        activeTable = new JTable(tableModel);
        activeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activeTable.setRowHeight(35);
        JTableHeader th = activeTable.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(new Color(52,73,94));
        th.setForeground(Color.WHITE);
        activeTable.setSelectionBackground(new Color(52,152,219));
        activeTable.setSelectionForeground(Color.WHITE);
        activeTable.setGridColor(new Color(189,195,199));

        panel.add(new JScrollPane(activeTable), BorderLayout.CENTER);

        JButton btnDischarge = actionBtn("🏠 Libérer Patient", new Color(230,126,34));
        btnDischarge.setPreferredSize(new Dimension(180, 40));
        btnDischarge.addActionListener(e -> dischargeFromTable());

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bp.setBackground(new Color(240,242,245));
        bp.add(btnDischarge);
        panel.add(bp, BorderLayout.SOUTH);

        return panel;
    }

    // ── FOOTER ──────────────────────────────────────────────
    private JPanel createFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.CENTER));
        f.setPreferredSize(new Dimension(1400, 35));
        f.setBackground(new Color(44,62,80));
        JLabel l = new JLabel("Gestion des Hospitalisations - Système Hospitalier");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(189,195,199));
        f.add(l);
        return f;
    }

    // ============================================================
    // DATA LOADING  (all from DB)
    // ============================================================
    private void loadData() {
        loadPavillons();
        loadActiveHospitalizations();
        updateStatistics();
    }

    /** Fill pavillonCombo from PAVILLON table */
    private void loadPavillons() {
        pavillonIds.clear();
        pavillonCombo.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT num_pavillon, nom_pavillon FROM PAVILLON ORDER BY num_pavillon")) {
            while (rs.next()) {
                pavillonIds.add(rs.getInt("num_pavillon"));
                pavillonCombo.addItem(rs.getString("nom_pavillon"));
            }
        } catch (Exception e) {
            showErr("Erreur chargement pavillons: " + e.getMessage());
        }
        if (pavillonCombo.getItemCount() > 0) pavillonCombo.setSelectedIndex(0);
    }

    /** Reload chambreCombo when pavillon changes */
    private void onPavillonChanged() {
        chambreIds.clear();
        chambreCombo.removeAllItems();
        int pavId = selectedPavillonId();
        if (pavId < 0) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "SELECT num_chambre, numero_chambre, type_chambre, statut " +
                     "FROM CHAMBRE WHERE num_pavillon=? ORDER BY num_chambre")) {
            pst.setInt(1, pavId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    chambreIds.add(rs.getInt("num_chambre"));
                    chambreCombo.addItem(
                        rs.getString("numero_chambre") + " — " +
                        nullStr(rs.getString("type_chambre"), "Standard") + " [" +
                        nullStr(rs.getString("statut"), "libre") + "]");
                }
            }
        } catch (Exception e) {
            showErr("Erreur chargement chambres: " + e.getMessage());
        }
        if (chambreCombo.getItemCount() > 0) chambreCombo.setSelectedIndex(0);
    }

    /** Draw bed cards for selected chambre.
     *  Auto-syncs LIT.statut from HOSPITALISATION truth before drawing. */
    private void loadBedGrid() {
        bedGridPanel.removeAll();
        int chId = selectedChambreId();
        if (chId < 0) { bedGridPanel.revalidate(); bedGridPanel.repaint(); return; }

        try (Connection conn = DBConnection.getConnection()) {
            // Auto-sync: if an open HOSPITALISATION exists → 'occupé'; if statut='occupé' but no open hosp → 'libre'
            try (PreparedStatement sync = conn.prepareStatement(
                    "UPDATE LIT l SET l.statut = CASE " +
                    "  WHEN EXISTS (SELECT 1 FROM HOSPITALISATION h WHERE h.num_lit = l.num_lit AND h.date_sortie IS NULL) THEN 'occupé' " +
                    "  WHEN l.statut = 'occupé' THEN 'libre' " +
                    "  ELSE l.statut END " +
                    "WHERE l.num_chambre = ?")) {
                sync.setInt(1, chId);
                sync.executeUpdate();
            }

            // Draw cards from the now-accurate data
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT l.num_lit, l.numero_lit, l.statut, " +
                    "p.nom AS pnom, p.prenom AS pprenom, " +
                    "h.date_entree, h.num_hospitalisation " +
                    "FROM LIT l " +
                    "LEFT JOIN HOSPITALISATION h ON h.num_lit = l.num_lit AND h.date_sortie IS NULL " +
                    "LEFT JOIN ADMISSION a ON a.num_admission = h.num_admission " +
                    "LEFT JOIN PATIENT p ON p.num_patient = a.num_patient " +
                    "WHERE l.num_chambre = ? ORDER BY l.num_lit")) {
                pst.setInt(1, chId);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) bedGridPanel.add(createBedCard(rs));
                }
            }
        } catch (Exception e) {
            showErr("Erreur chargement lits: " + e.getMessage());
        }
        bedGridPanel.revalidate();
        bedGridPanel.repaint();
    }

    /** Load active hospitalizations into the right-side table */
    private void loadActiveHospitalizations() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT CONCAT(p.nom,' ',p.prenom) AS patient, " +
                     "l.numero_lit, c.numero_chambre, " +
                     "h.date_entree, " +
                     "DATEDIFF(CURDATE(), DATE(h.date_entree)) AS jours " +
                     "FROM HOSPITALISATION h " +
                     "JOIN LIT l ON h.num_lit = l.num_lit " +
                     "JOIN CHAMBRE c ON l.num_chambre = c.num_chambre " +
                     "JOIN ADMISSION a ON h.num_admission = a.num_admission " +
                     "JOIN PATIENT p ON a.num_patient = p.num_patient " +
                     "WHERE h.date_sortie IS NULL " +
                     "ORDER BY h.date_entree DESC")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("patient"),
                    rs.getString("numero_lit"),
                    rs.getString("numero_chambre"),
                    rs.getString("date_entree"),
                    rs.getInt("jours")
                });
            }
        } catch (Exception e) {
            showErr("Erreur hospitalisations actives: " + e.getMessage());
        }
    }

    /** Update the four stat cards */
    private void updateStatistics() {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT " +
                     "COUNT(*) AS total, " +
                     "SUM(statut='occupé') AS occ " +
                     "FROM LIT")) {
            if (rs.next()) {
                int total = rs.getInt("total");
                int occ   = rs.getInt("occ");
                int free  = total - occ;
                double rate = total > 0 ? occ * 100.0 / total : 0;
                lblTotalBeds.setText(String.valueOf(total));
                lblOccupied .setText(String.valueOf(occ));
                lblFree     .setText(String.valueOf(free));
                lblRate     .setText(String.format("%.0f%%", rate));
            }
        } catch (Exception e) {
            // stats are not critical
        }
    }

    // ============================================================
    // BED CARD  (built from ResultSet row)
    // ============================================================
    private JPanel createBedCard(ResultSet rs) throws SQLException {
        int    litId      = rs.getInt("num_lit");
        String litNum     = rs.getString("numero_lit");
        String statut     = nullStr(rs.getString("statut"), "libre");
        String pnom       = rs.getString("pnom");
        String pprenom    = rs.getString("pprenom");
        String dateEntree = rs.getString("date_entree");
        int    hospId     = rs.getInt("num_hospitalisation"); // 0 if LEFT JOIN found nothing

        // TRUTH: a bed is occupied if and only if an open HOSPITALISATION row joined
        boolean occupied  = (hospId != 0);
        // If occupied by JOIN but statut says otherwise, treat as occupé visually
        String  effectiveStatut = occupied ? "occupé" : statut;
        String  patient   = (pnom != null) ? pnom + " " + nullStr(pprenom, "") : "";

        Color borderCol, statusCol;
        String statusIcon;
        switch (effectiveStatut.toLowerCase()) {
            case "occupé"      -> { borderCol = new Color(231,76,60);   statusCol = borderCol; statusIcon = "🔴 OCCUPÉ";      }
            case "maintenance" -> { borderCol = new Color(243,156,18);  statusCol = borderCol; statusIcon = "🔧 MAINTENANCE"; }
            case "réservé"     -> { borderCol = new Color(52,152,219);  statusCol = borderCol; statusIcon = "📌 RÉSERVÉ";     }
            default            -> { borderCol = new Color(46,204,113);  statusCol = borderCol; statusIcon = "🟢 LIBRE";       }
        }

        JPanel card = new JPanel(null);
        card.setPreferredSize(new Dimension(200, 180));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderCol, 3),
            BorderFactory.createEmptyBorder(10,10,10,10)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel dot = new JPanel();
        dot.setBounds(10, 10, 15, 15);
        dot.setBackground(statusCol);
        card.add(dot);

        JLabel lNum = new JLabel(litNum);
        lNum.setBounds(30, 5, 160, 25);
        lNum.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lNum.setForeground(new Color(44,62,80));
        card.add(lNum);

        JLabel lStat = new JLabel(statusIcon);
        lStat.setBounds(10, 35, 180, 20);
        lStat.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lStat.setForeground(statusCol);
        card.add(lStat);

        if (occupied) {
            // ── OCCUPIED bed ─────────────────────────────────
            JLabel lPat = new JLabel("<html>" + (!patient.isBlank() ? patient : "Patient inconnu") + "</html>");
            lPat.setBounds(10, 60, 180, 40);
            lPat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lPat.setForeground(new Color(52,73,94));
            card.add(lPat);

            JLabel lDate = new JLabel("Depuis: " + (dateEntree != null ? dateEntree.substring(0,10) : "?"));
            lDate.setBounds(10, 105, 180, 20);
            lDate.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lDate.setForeground(new Color(127,140,141));
            card.add(lDate);

            // "Libérer" discharges properly (date_sortie + LIT.statut='libre')
            JButton btnFree = actionBtn("🏠 Libérer", new Color(230,126,34));
            btnFree.setBounds(10, 135, 180, 30);
            btnFree.addActionListener(e -> libereLit(litId, hospId, patient.isBlank() ? "ce patient" : patient));
            card.add(btnFree);

        } else {
            // ── FREE / RESERVED / MAINTENANCE bed ────────────
            JLabel msg = new JLabel(
                effectiveStatut.equals("maintenance") ? "En maintenance" :
                effectiveStatut.equals("réservé")     ? "Réservé"       : "Disponible");
            msg.setBounds(10, 65, 180, 20);
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            msg.setForeground(new Color(127,140,141));
            card.add(msg);

            // ✏️ change statut between libre / réservé / maintenance
            JButton btnEdit = actionBtn("✏️ Statut", new Color(52,152,219));
            btnEdit.setBounds(10, 135, 95, 30);
            btnEdit.addActionListener(e -> editLitStatut(litId, effectiveStatut));
            card.add(btnEdit);

            JButton btnDel = actionBtn("🗑️", new Color(231,76,60));
            btnDel.setBounds(110, 135, 40, 30);
            btnDel.addActionListener(e -> deleteLit(litId));
            card.add(btnDel);

            // 👤 assign only on truly free beds
            if ("libre".equalsIgnoreCase(effectiveStatut)) {
                JButton btnAssign = actionBtn("👤", new Color(46,204,113));
                btnAssign.setBounds(155, 135, 35, 30);
                btnAssign.setToolTipText("Assigner patient");
                btnAssign.addActionListener(e -> assignPatientToLit(litId));
                card.add(btnAssign);
            }
        }

        return card;
    }

    // ============================================================
    // CRUD — PAVILLON
    // ============================================================
    private void addPavillon() {
        JTextField fNom  = new JTextField();
        JTextField fType = new JTextField("Standard");
        JTextField fCap  = new JTextField("0");

        JComboBox<String> svcCombo = new JComboBox<>();
        List<Integer> svcIds = new ArrayList<>();
        svcCombo.addItem("-- Aucun --"); svcIds.add(0);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT code_service, nom_service FROM SERVICE ORDER BY nom_service")) {
            while (rs.next()) { svcIds.add(rs.getInt("code_service")); svcCombo.addItem(rs.getString("nom_service")); }
        } catch (Exception ignored) {}

        Object[] msg = {"Nom du pavillon:", fNom, "Type:", fType, "Capacité totale:", fCap, "Service associé:", svcCombo};
        if (JOptionPane.showConfirmDialog(this, msg, "Ajouter Pavillon", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String nom = fNom.getText().trim();
        if (nom.isEmpty()) { showErr("Nom requis."); return; }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "INSERT INTO PAVILLON(nom_pavillon, type_pavillon, capacite_totale, code_service) VALUES(?,?,?,?)")) {
            pst.setString(1, nom);
            pst.setString(2, fType.getText().trim());
            pst.setInt   (3, parseInt(fCap.getText(), 0));
            int svcId = svcIds.get(svcCombo.getSelectedIndex());
            if (svcId > 0) pst.setInt(4, svcId); else pst.setNull(4, Types.INTEGER);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Pavillon ajouté !");
            loadData();
        } catch (Exception e) { showErr("Erreur ajout pavillon: " + e.getMessage()); }
    }

    // ============================================================
    // CRUD — CHAMBRE
    // ============================================================
    private void addChambre() {
        int pavId = selectedPavillonId();
        if (pavId < 0) { showErr("Sélectionnez un pavillon."); return; }

        JTextField fNum  = new JTextField();
        JComboBox<String> fType = new JComboBox<>(new String[]{"Standard","VIP","Isolement","Soins intensifs"});
        JTextField fLits = new JTextField("2");
        JTextField fTarif= new JTextField("100");
        JComboBox<String> fStat = new JComboBox<>(new String[]{"libre","occupé","maintenance"});

        Object[] msg = {"Numéro de chambre:", fNum, "Type:", fType, "Nb lits max:", fLits, "Tarif journalier (€):", fTarif, "Statut:", fStat};
        if (JOptionPane.showConfirmDialog(this, msg, "Ajouter Chambre", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String num = fNum.getText().trim();
        if (num.isEmpty()) { showErr("Numéro requis."); return; }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "INSERT INTO CHAMBRE(numero_chambre, num_pavillon, type_chambre, nombre_lits, tarif_journalier, statut) VALUES(?,?,?,?,?,?)")) {
            pst.setString(1, num);
            pst.setInt   (2, pavId);
            pst.setString(3, fType.getSelectedItem().toString());
            pst.setInt   (4, parseInt(fLits.getText(), 1));
            pst.setDouble(5, parseDouble(fTarif.getText(), 0));
            pst.setString(6, fStat.getSelectedItem().toString());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Chambre ajoutée !");
            onPavillonChanged();
        } catch (Exception e) { showErr("Erreur ajout chambre: " + e.getMessage()); }
    }

    /** Edit chambre statut (right-click style — accessible from the combo label) */
    private void editChambreStatut() {
        int chId = selectedChambreId();
        if (chId < 0) { showErr("Sélectionnez une chambre."); return; }

        String[] options = {"libre","occupé","maintenance"};
        String cur = getChambreStatut(chId);
        String newStat = (String) JOptionPane.showInputDialog(this,
            "Modifier le statut de la chambre:", "Statut Chambre",
            JOptionPane.QUESTION_MESSAGE, null, options, cur);
        if (newStat == null) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "UPDATE CHAMBRE SET statut=? WHERE num_chambre=?")) {
            pst.setString(1, newStat);
            pst.setInt   (2, chId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Statut chambre mis à jour !");
            onPavillonChanged(); // refresh combo labels
        } catch (Exception e) { showErr("Erreur: " + e.getMessage()); }
    }

    private String getChambreStatut(int chId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT statut FROM CHAMBRE WHERE num_chambre=?")) {
            pst.setInt(1, chId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getString("statut");
            }
        } catch (Exception ignored) {}
        return "libre";
    }

    // ============================================================
    // CRUD — LIT
    // ============================================================
    private void addBed() {
        int chId = selectedChambreId();
        if (chId < 0) { showErr("Sélectionnez une chambre."); return; }

        JTextField fNum  = new JTextField();
        JComboBox<String> fStat = new JComboBox<>(new String[]{"libre","réservé","maintenance"});

        Object[] msg = {"Numéro du lit:", fNum, "Statut initial:", fStat};
        if (JOptionPane.showConfirmDialog(this, msg, "Ajouter Lit", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String num = fNum.getText().trim();
        if (num.isEmpty()) { showErr("Numéro requis."); return; }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "INSERT INTO LIT(numero_lit, num_chambre, statut) VALUES(?,?,?)")) {
            pst.setString(1, num);
            pst.setInt   (2, chId);
            pst.setString(3, fStat.getSelectedItem().toString());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Lit ajouté !");
            loadBedGrid();
            updateStatistics();
        } catch (Exception e) { showErr("Erreur ajout lit: " + e.getMessage()); }
    }

    /** Change lit statut — only between libre / réservé / maintenance (occupé is set by assigning a patient) */
    private void editLitStatut(int litId, String currentStatut) {
        // Do not allow manually setting 'occupé' — that must go through "Assigner Patient"
        String[] options = {"libre", "réservé", "maintenance"};
        String sel = currentStatut.equals("occupé") ? "libre" : currentStatut;
        String newStat = (String) JOptionPane.showInputDialog(this,
            "Modifier le statut du lit\n(Pour marquer occupé, utilisez '👤 Assigner Patient') :",
            "Statut Lit", JOptionPane.QUESTION_MESSAGE, null, options, sel);
        if (newStat == null) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "UPDATE LIT SET statut=? WHERE num_lit=?")) {
            pst.setString(1, newStat);
            pst.setInt(2, litId);
            pst.executeUpdate();
            syncChambreStatut(conn, litId);  // keep chambre statut in sync
            JOptionPane.showMessageDialog(this, "Statut mis à jour !");
            loadBedGrid();
            updateStatistics();
        } catch (Exception e) { showErr("Erreur: " + e.getMessage()); }
    }

    /** Auto-update CHAMBRE.statut based on its beds reality */
    private void syncChambreStatut(Connection conn, int litId) {
        try {
            // Find num_chambre for this lit
            PreparedStatement p = conn.prepareStatement(
                "SELECT num_chambre FROM LIT WHERE num_lit=?");
            p.setInt(1, litId);
            ResultSet r = p.executeQuery();
            if (!r.next()) return;
            int chId = r.getInt("num_chambre");

            // Count beds by statut in this chambre
            p = conn.prepareStatement(
                "SELECT " +
                "SUM(statut='occupé') AS occ, " +
                "SUM(statut='maintenance') AS maint, " +
                "COUNT(*) AS total " +
                "FROM LIT WHERE num_chambre=?");
            p.setInt(1, chId);
            r = p.executeQuery();
            if (!r.next()) return;

            int occ   = r.getInt("occ");
            int maint = r.getInt("maint");
            int total = r.getInt("total");

            String chStatut;
            if (occ == total)       chStatut = "occupé";
            else if (occ > 0)       chStatut = "occupé";   // at least one occupied
            else if (maint == total) chStatut = "maintenance";
            else                     chStatut = "libre";

            p = conn.prepareStatement("UPDATE CHAMBRE SET statut=? WHERE num_chambre=?");
            p.setString(1, chStatut);
            p.setInt(2, chId);
            p.executeUpdate();
        } catch (Exception ignored) {}
    }

    /** Delete a free lit */
    private void deleteLit(int litId) {
        if (JOptionPane.showConfirmDialog(this, "Supprimer ce lit ?", "Confirmation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM LIT WHERE num_lit=?")) {
            pst.setInt(1, litId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Lit supprimé !");
            loadBedGrid();
            updateStatistics();
        } catch (Exception e) { showErr("Impossible de supprimer (lit lié à des données): " + e.getMessage()); }
    }

    // ============================================================
    // HOSPITALISATION — assign / discharge
    // ============================================================
    private void assignPatientToLit(int litId) {
        // Load active admissions without current hospitalization
        List<Integer> admIds  = new ArrayList<>();
        List<String>  admLabels = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT a.num_admission, CONCAT(p.nom,' ',p.prenom) AS pat " +
                     "FROM ADMISSION a " +
                     "JOIN PATIENT p ON a.num_patient = p.num_patient " +
                     "WHERE a.statut='en cours' " +
                     "AND a.num_admission NOT IN (" +
                     "  SELECT num_admission FROM HOSPITALISATION WHERE date_sortie IS NULL) " +
                     "ORDER BY a.num_admission DESC")) {
            while (rs.next()) {
                admIds.add(rs.getInt("num_admission"));
                admLabels.add("#" + rs.getInt("num_admission") + " — " + rs.getString("pat"));
            }
        } catch (Exception e) { showErr("Erreur chargement admissions: " + e.getMessage()); return; }

        if (admIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune admission en cours sans hospitalisation.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] labels = admLabels.toArray(new String[0]);
        String chosen = (String) JOptionPane.showInputDialog(this,
            "Sélectionner l'admission à hospitaliser:", "Assigner Patient",
            JOptionPane.QUESTION_MESSAGE, null, labels, labels[0]);
        if (chosen == null) return;

        int admId = admIds.get(admLabels.indexOf(chosen));

        try (Connection conn = DBConnection.getConnection()) {
            // Insert HOSPITALISATION
            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO HOSPITALISATION(num_admission, num_lit, date_entree) VALUES(?,?,NOW())");
            pst.setInt(1, admId);
            pst.setInt(2, litId);
            pst.executeUpdate();

            // Update LIT statut
            pst = conn.prepareStatement("UPDATE LIT SET statut='occupé' WHERE num_lit=?");
            pst.setInt(1, litId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Patient assigné au lit !");
            loadData();
            loadBedGrid();
        } catch (Exception e) { showErr("Erreur assignation: " + e.getMessage()); }
    }

    /** Discharge from bed card button */
    private void libereLit(int litId, int hospId, String patientName) {
        if (JOptionPane.showConfirmDialog(this,
            "Libérer le lit pour " + patientName + " ?", "Confirmation",
            JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(
                "UPDATE HOSPITALISATION SET date_sortie=CURDATE() WHERE num_hospitalisation=?");
            pst.setInt(1, hospId);
            pst.executeUpdate();

            pst = conn.prepareStatement("UPDATE LIT SET statut='libre' WHERE num_lit=?");
            pst.setInt(1, litId);
            pst.executeUpdate();

            syncChambreStatut(conn, litId);  // keep chambre in sync

            JOptionPane.showMessageDialog(this, "Patient libéré avec succès !");
            loadData();
            loadBedGrid();
        } catch (Exception e) { showErr("Erreur libération: " + e.getMessage()); }
    }

    /** Discharge from the right-side active table */
    private void dischargeFromTable() {
        int row = activeTable.getSelectedRow();
        if (row < 0) { showErr("Sélectionnez un patient dans la liste."); return; }

        String litNum   = tableModel.getValueAt(row, 1).toString();
        String chambreNum = tableModel.getValueAt(row, 2).toString();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "SELECT l.num_lit, h.num_hospitalisation " +
                     "FROM LIT l " +
                     "JOIN CHAMBRE c ON l.num_chambre = c.num_chambre " +
                     "JOIN HOSPITALISATION h ON h.num_lit = l.num_lit " +
                     "WHERE l.numero_lit=? AND c.numero_chambre=? AND h.date_sortie IS NULL")) {
            pst.setString(1, litNum);
            pst.setString(2, chambreNum);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    libereLit(rs.getInt("num_lit"), rs.getInt("num_hospitalisation"),
                              tableModel.getValueAt(row, 0).toString());
                }
            }
        } catch (Exception e) { showErr("Erreur: " + e.getMessage()); }
    }

    /** Quick assign triggered from the toolbar button */
    private void assignPatientToBed() {
        int chId = selectedChambreId();
        if (chId < 0) { showErr("Sélectionnez une chambre."); return; }

        // Find a free lit in that chambre
        List<Integer> freeIds    = new ArrayList<>();
        List<String>  freeLabels = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "SELECT num_lit, numero_lit FROM LIT WHERE num_chambre=? AND statut='libre'")) {
            pst.setInt(1, chId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    freeIds.add(rs.getInt("num_lit"));
                    freeLabels.add(rs.getString("numero_lit"));
                }
            }
        } catch (Exception e) { showErr(e.getMessage()); return; }

        if (freeIds.isEmpty()) { JOptionPane.showMessageDialog(this, "Aucun lit libre dans cette chambre."); return; }

        String[] labels = freeLabels.toArray(new String[0]);
        String chosen = (String) JOptionPane.showInputDialog(this,
            "Sélectionner le lit:", "Assigner Patient",
            JOptionPane.QUESTION_MESSAGE, null, labels, labels[0]);
        if (chosen == null) return;

        assignPatientToLit(freeIds.get(freeLabels.indexOf(chosen)));
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private JButton actionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void addLabel(JPanel p, String txt, int x, int y) {
        JLabel l = new JLabel(txt);
        l.setBounds(x, y, 80, 25);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l);
    }

    private int selectedPavillonId() {
        int i = pavillonCombo.getSelectedIndex();
        return (i >= 0 && i < pavillonIds.size()) ? pavillonIds.get(i) : -1;
    }

    private int selectedChambreId() {
        int i = chambreCombo.getSelectedIndex();
        return (i >= 0 && i < chambreIds.size()) ? chambreIds.get(i) : -1;
    }

    private String nullStr(String s, String def) { return s != null ? s : def; }
    private int    parseInt   (String s, int    def) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; } }
    private double parseDouble(String s, double def) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return def; } }
    private void   showErr(String msg) { JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE); }

    // ============================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(HospitalizationPage::new);
    }
}