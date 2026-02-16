package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class HospitalizationPage extends JFrame {
    
    // Dummy data structures
    private Map<String, Map<String, List<Bed>>> hospitalData = new LinkedHashMap<>();
    
    // Main components
    private JComboBox<String> pavillonCombo;
    private JComboBox<String> chambreCombo;
    private JPanel bedGridPanel;
    private JTable activeHospitalizationsTable;
    private DefaultTableModel tableModel;
    
    // Stats labels
    private JLabel totalBedsLabel;
    private JLabel occupiedBedsLabel;
    private JLabel freeBedsLabel;
    private JLabel occupancyRateLabel;
    
    // Bed class
    private static class Bed {
        int id;
        String number;
        boolean occupied;
        String patientName;
        String dateEntree;
        int daysHospitalized;
        String status; // "libre", "occupé", "maintenance", "réservé"
        
        Bed(int id, String number) {
            this.id = id;
            this.number = number;
            this.occupied = false;
            this.patientName = "";
            this.dateEntree = "";
            this.daysHospitalized = 0;
            this.status = "libre";
        }
    }
    
    public HospitalizationPage() {
        setTitle("Gestion des Hospitalisations");
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(240, 242, 245));
        
        initDummyData();
        initComponents();
        loadData();
        
        setVisible(true);
    }
    
    // ==================== INIT DUMMY DATA ====================
    private void initDummyData() {
        Random rand = new Random();
        String[] firstNames = {"Jean", "Marie", "Pierre", "Sophie", "Luc", "Anne", "Paul", "Claire", "Marc", "Julie"};
        String[] lastNames = {"Dupont", "Martin", "Bernard", "Dubois", "Thomas", "Robert", "Petit", "Richard", "Durand", "Leroy"};
        
        int bedCounter = 1;
        
        // Create 3 pavillons
        for (int p = 1; p <= 3; p++) {
            String pavillonName = "Pavillon " + (char)('A' + p - 1);
            Map<String, List<Bed>> chambres = new LinkedHashMap<>();
            
            // Create 5 chambres per pavillon
            for (int c = 1; c <= 5; c++) {
                String chambreName = "Chambre " + (100 + c);
                List<Bed> beds = new ArrayList<>();
                
                // Create 3 beds per chambre
                for (int b = 1; b <= 3; b++) {
                    Bed bed = new Bed(bedCounter++, "Lit " + (char)('A' + b - 1));
                    
                    // Randomly occupy some beds (60% occupancy)
                    int randomStatus = rand.nextInt(100);
                    if (randomStatus < 60) {
                        bed.occupied = true;
                        bed.status = "occupé";
                        bed.patientName = firstNames[rand.nextInt(firstNames.length)] + " " + 
                                         lastNames[rand.nextInt(lastNames.length)];
                        bed.daysHospitalized = rand.nextInt(10) + 1;
                        
                        // Generate random date
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH, -bed.daysHospitalized);
                        bed.dateEntree = String.format("%02d/%02d/%04d", 
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.YEAR));
                    } else if (randomStatus < 70) {
                        bed.status = "maintenance";
                    } else if (randomStatus < 75) {
                        bed.status = "réservé";
                    }
                    
                    beds.add(bed);
                }
                
                chambres.put(chambreName, beds);
            }
            
            hospitalData.put(pavillonName, chambres);
        }
    }
    
    // ==================== INIT COMPONENTS ====================
    private void initComponents() {
        // Header
        add(createHeader(), BorderLayout.NORTH);
        
        // Main content (split)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(900);
        splitPane.setDividerSize(2);
        splitPane.setBorder(null);
        
        // Left side: Bed map
        splitPane.setLeftComponent(createBedMapPanel());
        
        // Right side: Active hospitalizations list
        splitPane.setRightComponent(createActiveListPanel());
        
        add(splitPane, BorderLayout.CENTER);
        
        // Footer
        add(createFooter(), BorderLayout.SOUTH);
    }
    
    // ==================== HEADER ====================
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(null);
        header.setPreferredSize(new Dimension(1400, 120));
        header.setBackground(new Color(41, 128, 185));
        
        // Title
        JLabel titleLabel = new JLabel("🛏️ Gestion des Hospitalisations");
        titleLabel.setBounds(40, 20, 600, 40);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Vue d'ensemble des lits et patients hospitalisés");
        subtitleLabel.setBounds(40, 65, 500, 25);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        header.add(subtitleLabel);
        
        // Statistics cards
        int cardX = 700;
        
        JPanel totalCard = createStatCard("Total Lits", "0", new Color(52, 152, 219));
        totalCard.setBounds(cardX, 20, 150, 80);
        totalBedsLabel = (JLabel) totalCard.getComponent(1);
        header.add(totalCard);
        
        JPanel occupiedCard = createStatCard("Occupés", "0", new Color(231, 76, 60));
        occupiedCard.setBounds(cardX + 170, 20, 150, 80);
        occupiedBedsLabel = (JLabel) occupiedCard.getComponent(1);
        header.add(occupiedCard);
        
        JPanel freeCard = createStatCard("Libres", "0", new Color(46, 204, 113));
        freeCard.setBounds(cardX + 340, 20, 150, 80);
        freeBedsLabel = (JLabel) freeCard.getComponent(1);
        header.add(freeCard);
        
        JPanel rateCard = createStatCard("Taux", "0%", new Color(155, 89, 182));
        rateCard.setBounds(cardX + 510, 20, 150, 80);
        occupancyRateLabel = (JLabel) rateCard.getComponent(1);
        header.add(rateCard);
        
        return header;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBounds(10, 5, 130, 20);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(255, 255, 255, 200));
        card.add(titleLabel);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setBounds(10, 25, 130, 35);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        card.add(valueLabel);
        
        return card;
    }
    
    // ==================== BED MAP PANEL ====================
    private JPanel createBedMapPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
        
        // Filter panel with management buttons
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(null);
        filterPanel.setPreferredSize(new Dimension(880, 120));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel filterTitle = new JLabel("📍 Gestion des Emplacements:");
        filterTitle.setBounds(0, 0, 300, 25);
        filterTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterTitle.setForeground(new Color(44, 62, 80));
        filterPanel.add(filterTitle);
        
        JLabel pavLabel = new JLabel("Pavillon:");
        pavLabel.setBounds(20, 35, 80, 25);
        pavLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(pavLabel);
        
        pavillonCombo = new JComboBox<>();
        pavillonCombo.setBounds(100, 35, 200, 30);
        pavillonCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pavillonCombo.addActionListener(e -> onPavillonChanged());
        filterPanel.add(pavillonCombo);
        
        JLabel chamLabel = new JLabel("Chambre:");
        chamLabel.setBounds(320, 35, 80, 25);
        chamLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(chamLabel);
        
        chambreCombo = new JComboBox<>();
        chambreCombo.setBounds(400, 35, 200, 30);
        chambreCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chambreCombo.addActionListener(e -> loadBedGrid());
        filterPanel.add(chambreCombo);
        
        JButton refreshBtn = new JButton("🔄");
        refreshBtn.setBounds(620, 35, 50, 30);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setToolTipText("Actualiser");
        refreshBtn.addActionListener(e -> loadData());
        filterPanel.add(refreshBtn);
        
        // Management buttons
        JButton addPavillonBtn = new JButton("➕ Pavillon");
        addPavillonBtn.setBounds(20, 75, 130, 30);
        addPavillonBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addPavillonBtn.setBackground(new Color(46, 204, 113));
        addPavillonBtn.setForeground(Color.WHITE);
        addPavillonBtn.setFocusPainted(false);
        addPavillonBtn.setBorderPainted(false);
        addPavillonBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addPavillonBtn.addActionListener(e -> addPavillon());
        filterPanel.add(addPavillonBtn);
        
        JButton addChambreBtn = new JButton("➕ Chambre");
        addChambreBtn.setBounds(160, 75, 130, 30);
        addChambreBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addChambreBtn.setBackground(new Color(52, 152, 219));
        addChambreBtn.setForeground(Color.WHITE);
        addChambreBtn.setFocusPainted(false);
        addChambreBtn.setBorderPainted(false);
        addChambreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addChambreBtn.addActionListener(e -> addChambre());
        filterPanel.add(addChambreBtn);
        
        JButton addBedBtn = new JButton("➕ Lit");
        addBedBtn.setBounds(300, 75, 130, 30);
        addBedBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBedBtn.setBackground(new Color(155, 89, 182));
        addBedBtn.setForeground(Color.WHITE);
        addBedBtn.setFocusPainted(false);
        addBedBtn.setBorderPainted(false);
        addBedBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBedBtn.addActionListener(e -> addBed());
        filterPanel.add(addBedBtn);
        
        JButton assignPatientBtn = new JButton("👤 Assigner Patient");
        assignPatientBtn.setBounds(450, 75, 160, 30);
        assignPatientBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        assignPatientBtn.setBackground(new Color(230, 126, 34));
        assignPatientBtn.setForeground(Color.WHITE);
        assignPatientBtn.setFocusPainted(false);
        assignPatientBtn.setBorderPainted(false);
        assignPatientBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        assignPatientBtn.addActionListener(e -> assignPatientToBed());
        filterPanel.add(assignPatientBtn);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Bed grid scroll pane
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(240, 242, 245));
        
        bedGridPanel = new JPanel();
        bedGridPanel.setLayout(new GridLayout(0, 4, 15, 15));
        bedGridPanel.setBackground(new Color(240, 242, 245));
        bedGridPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        scrollPane.setViewportView(bedGridPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ==================== ACTIVE LIST PANEL ====================
    private JPanel createActiveListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        
        JLabel titleLabel = new JLabel("📋 Hospitalisations Actives");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Patient", "Lit", "Depuis", "Jours"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        activeHospitalizationsTable = new JTable(tableModel);
        activeHospitalizationsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activeHospitalizationsTable.setRowHeight(35);
        activeHospitalizationsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        activeHospitalizationsTable.getTableHeader().setBackground(new Color(52, 73, 94));
        activeHospitalizationsTable.getTableHeader().setForeground(Color.WHITE);
        activeHospitalizationsTable.setSelectionBackground(new Color(52, 152, 219));
        activeHospitalizationsTable.setSelectionForeground(Color.WHITE);
        activeHospitalizationsTable.setGridColor(new Color(189, 195, 199));
        
        JScrollPane scrollPane = new JScrollPane(activeHospitalizationsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 242, 245));
        
        JButton dischargeBtn = new JButton("🏠 Libérer Patient");
        dischargeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dischargeBtn.setBackground(new Color(230, 126, 34));
        dischargeBtn.setForeground(Color.WHITE);
        dischargeBtn.setFocusPainted(false);
        dischargeBtn.setBorderPainted(false);
        dischargeBtn.setPreferredSize(new Dimension(180, 40));
        dischargeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dischargeBtn.addActionListener(e -> dischargeFromTable());
        buttonPanel.add(dischargeBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== FOOTER ====================
    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setPreferredSize(new Dimension(1400, 35));
        footer.setBackground(new Color(44, 62, 80));
        footer.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JLabel label = new JLabel("Gestion des Hospitalisations - Système Hospitalier");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(189, 195, 199));
        footer.add(label);
        
        return footer;
    }
    
    // ==================== LOAD DATA ====================
    private void loadData() {
        loadPavillons();
        loadActiveHospitalizations();
        updateStatistics();
    }
    
    private void loadPavillons() {
        pavillonCombo.removeAllItems();
        for (String pavillon : hospitalData.keySet()) {
            pavillonCombo.addItem(pavillon);
        }
        
        if (pavillonCombo.getItemCount() > 0) {
            pavillonCombo.setSelectedIndex(0);
        }
    }
    
    private void onPavillonChanged() {
        chambreCombo.removeAllItems();
        String selectedPavillon = (String) pavillonCombo.getSelectedItem();
        
        if (selectedPavillon != null) {
            Map<String, List<Bed>> chambres = hospitalData.get(selectedPavillon);
            for (String chambre : chambres.keySet()) {
                chambreCombo.addItem(chambre);
            }
            
            if (chambreCombo.getItemCount() > 0) {
                chambreCombo.setSelectedIndex(0);
            }
        }
    }
    
    private void loadBedGrid() {
        bedGridPanel.removeAll();
        
        String selectedPavillon = (String) pavillonCombo.getSelectedItem();
        String selectedChambre = (String) chambreCombo.getSelectedItem();
        
        if (selectedPavillon != null && selectedChambre != null) {
            List<Bed> beds = hospitalData.get(selectedPavillon).get(selectedChambre);
            
            for (Bed bed : beds) {
                JPanel bedCard = createBedCard(bed);
                bedGridPanel.add(bedCard);
            }
        }
        
        bedGridPanel.revalidate();
        bedGridPanel.repaint();
    }
    
    // ==================== CREATE BED CARD ====================
    private JPanel createBedCard(Bed bed) {
        Color borderColor;
        Color statusColor;
        String statusIcon;
        
        switch (bed.status) {
            case "occupé":
                borderColor = new Color(231, 76, 60);
                statusColor = new Color(231, 76, 60);
                statusIcon = "🔴 OCCUPÉ";
                break;
            case "maintenance":
                borderColor = new Color(243, 156, 18);
                statusColor = new Color(243, 156, 18);
                statusIcon = "🔧 MAINTENANCE";
                break;
            case "réservé":
                borderColor = new Color(52, 152, 219);
                statusColor = new Color(52, 152, 219);
                statusIcon = "📌 RÉSERVÉ";
                break;
            default: // libre
                borderColor = new Color(46, 204, 113);
                statusColor = new Color(46, 204, 113);
                statusIcon = "🟢 LIBRE";
        }
        
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(200, 180));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Status indicator
        JPanel statusDot = new JPanel();
        statusDot.setBounds(10, 10, 15, 15);
        statusDot.setBackground(statusColor);
        statusDot.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        card.add(statusDot);
        
        // Bed number
        JLabel bedLabel = new JLabel(bed.number);
        bedLabel.setBounds(30, 5, 160, 25);
        bedLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bedLabel.setForeground(new Color(44, 62, 80));
        card.add(bedLabel);
        
        // Status
        JLabel statusLabel = new JLabel(statusIcon);
        statusLabel.setBounds(10, 35, 180, 20);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(statusColor);
        card.add(statusLabel);
        
        if (bed.occupied) {
            JLabel patientLabel = new JLabel("<html>" + bed.patientName + "</html>");
            patientLabel.setBounds(10, 60, 180, 40);
            patientLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            patientLabel.setForeground(new Color(52, 73, 94));
            card.add(patientLabel);
            
            JLabel dateLabel = new JLabel("Depuis: " + bed.dateEntree);
            dateLabel.setBounds(10, 105, 180, 20);
            dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            dateLabel.setForeground(new Color(127, 140, 141));
            card.add(dateLabel);
            
            // Action buttons
            JButton editBtn = new JButton("✏️");
            editBtn.setBounds(10, 135, 45, 30);
            editBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            editBtn.setBackground(new Color(52, 152, 219));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editBtn.setToolTipText("Modifier");
            editBtn.addActionListener(e -> editBed(bed));
            card.add(editBtn);
            
            JButton dischargeBtn = new JButton("Libérer");
            dischargeBtn.setBounds(60, 135, 130, 30);
            dischargeBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            dischargeBtn.setBackground(new Color(230, 126, 34));
            dischargeBtn.setForeground(Color.WHITE);
            dischargeBtn.setFocusPainted(false);
            dischargeBtn.setBorderPainted(false);
            dischargeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            dischargeBtn.addActionListener(e -> dischargeBed(bed));
            card.add(dischargeBtn);
            
        } else {
            String message = bed.status.equals("maintenance") ? "En maintenance" :
                           bed.status.equals("réservé") ? "Réservé" : "Disponible";
            
            JLabel availableLabel = new JLabel(message);
            availableLabel.setBounds(10, 70, 180, 20);
            availableLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            availableLabel.setForeground(new Color(127, 140, 141));
            card.add(availableLabel);
            
            // Action buttons
            JButton editBtn = new JButton("✏️ Modifier");
            editBtn.setBounds(10, 135, 85, 30);
            editBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            editBtn.setBackground(new Color(52, 152, 219));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editBtn.addActionListener(e -> editBed(bed));
            card.add(editBtn);
            
            JButton deleteBtn = new JButton("🗑️");
            deleteBtn.setBounds(100, 135, 45, 30);
            deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            deleteBtn.setBackground(new Color(231, 76, 60));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.setToolTipText("Supprimer");
            deleteBtn.addActionListener(e -> deleteBed(bed));
            card.add(deleteBtn);
            
            if (bed.status.equals("libre")) {
                JButton assignBtn = new JButton("👤");
                assignBtn.setBounds(150, 135, 40, 30);
                assignBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                assignBtn.setBackground(new Color(46, 204, 113));
                assignBtn.setForeground(Color.WHITE);
                assignBtn.setFocusPainted(false);
                assignBtn.setBorderPainted(false);
                assignBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                assignBtn.setToolTipText("Assigner patient");
                assignBtn.addActionListener(e -> assignPatient(bed));
                card.add(assignBtn);
            }
        }
        
        return card;
    }
    
    // ==================== CRUD OPERATIONS ====================
    
    // ADD PAVILLON
    private void addPavillon() {
        String name = JOptionPane.showInputDialog(this, 
            "Nom du nouveau pavillon:", 
            "Ajouter Pavillon", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (name != null && !name.trim().isEmpty()) {
            if (hospitalData.containsKey(name)) {
                JOptionPane.showMessageDialog(this, 
                    "Ce pavillon existe déjà!", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                hospitalData.put(name, new LinkedHashMap<>());
                JOptionPane.showMessageDialog(this, 
                    "Pavillon ajouté avec succès!", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        }
    }
    
    // ADD CHAMBRE
    private void addChambre() {
        String selectedPavillon = (String) pavillonCombo.getSelectedItem();
        if (selectedPavillon == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un pavillon!", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String name = JOptionPane.showInputDialog(this, 
            "Numéro de la nouvelle chambre:", 
            "Ajouter Chambre", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (name != null && !name.trim().isEmpty()) {
            Map<String, List<Bed>> chambres = hospitalData.get(selectedPavillon);
            if (chambres.containsKey(name)) {
                JOptionPane.showMessageDialog(this, 
                    "Cette chambre existe déjà!", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                chambres.put(name, new ArrayList<>());
                JOptionPane.showMessageDialog(this, 
                    "Chambre ajoutée avec succès!", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                onPavillonChanged();
            }
        }
    }
    
    // ADD BED
    private void addBed() {
        String selectedPavillon = (String) pavillonCombo.getSelectedItem();
        String selectedChambre = (String) chambreCombo.getSelectedItem();
        
        if (selectedPavillon == null || selectedChambre == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un pavillon et une chambre!", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String name = JOptionPane.showInputDialog(this, 
            "Numéro du nouveau lit:", 
            "Ajouter Lit", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (name != null && !name.trim().isEmpty()) {
            List<Bed> beds = hospitalData.get(selectedPavillon).get(selectedChambre);
            
            // Check if bed already exists
            for (Bed bed : beds) {
                if (bed.number.equals(name)) {
                    JOptionPane.showMessageDialog(this, 
                        "Ce lit existe déjà!", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            Bed newBed = new Bed(beds.size() + 1, name);
            beds.add(newBed);
            JOptionPane.showMessageDialog(this, 
                "Lit ajouté avec succès!", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            loadBedGrid();
            updateStatistics();
        }
    }
    
    // EDIT BED
    private void editBed(Bed bed) {
        String[] options = {"Libre", "Occupé", "Maintenance", "Réservé"};
        String currentStatus = bed.status.substring(0, 1).toUpperCase() + bed.status.substring(1);
        
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Modifier le statut du lit:",
            "Modifier Lit - " + bed.number,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            currentStatus
        );
        
        if (newStatus != null) {
            bed.status = newStatus.toLowerCase();
            
            // If changing from occupied to something else, clear patient
            if (!bed.status.equals("occupé")) {
                bed.occupied = false;
                bed.patientName = "";
                bed.dateEntree = "";
                bed.daysHospitalized = 0;
            }
            
            JOptionPane.showMessageDialog(this, 
                "Statut modifié avec succès!", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            loadBedGrid();
            updateStatistics();
        }
    }
    
    // DELETE BED
    private void deleteBed(Bed bed) {
        if (bed.occupied) {
            JOptionPane.showMessageDialog(this, 
                "Impossible de supprimer un lit occupé!", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer ce lit?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String selectedPavillon = (String) pavillonCombo.getSelectedItem();
            String selectedChambre = (String) chambreCombo.getSelectedItem();
            
            List<Bed> beds = hospitalData.get(selectedPavillon).get(selectedChambre);
            beds.remove(bed);
            
            JOptionPane.showMessageDialog(this, 
                "Lit supprimé avec succès!", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            loadBedGrid();
            updateStatistics();
        }
    }
    
    // ASSIGN PATIENT TO BED
    private void assignPatient(Bed bed) {
        JTextField nameField = new JTextField();
        JTextField daysField = new JTextField("1");
        
        Object[] message = {
            "Nom du patient:", nameField,
            "Jours d'hospitalisation:", daysField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, 
            "Assigner Patient - " + bed.number, 
            JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String patientName = nameField.getText().trim();
            String daysStr = daysField.getText().trim();
            
            if (patientName.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez entrer le nom du patient!", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                int days = Integer.parseInt(daysStr);
                
                bed.occupied = true;
                bed.status = "occupé";
                bed.patientName = patientName;
                bed.daysHospitalized = days;
                
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -days);
                bed.dateEntree = String.format("%02d/%02d/%04d", 
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR));
                
                JOptionPane.showMessageDialog(this, 
                    "Patient assigné avec succès!", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadData();
                loadBedGrid();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Nombre de jours invalide!", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Quick assign from button
    private void assignPatientToBed() {
        String selectedPavillon = (String) pavillonCombo.getSelectedItem();
        String selectedChambre = (String) chambreCombo.getSelectedItem();
        
        if (selectedPavillon == null || selectedChambre == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un pavillon et une chambre!", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Bed> beds = hospitalData.get(selectedPavillon).get(selectedChambre);
        List<Bed> freeBeds = new ArrayList<>();
        
        for (Bed bed : beds) {
            if (bed.status.equals("libre")) {
                freeBeds.add(bed);
            }
        }
        
        if (freeBeds.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun lit libre dans cette chambre!", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] bedNames = freeBeds.stream().map(b -> b.number).toArray(String[]::new);
        String selectedBedName = (String) JOptionPane.showInputDialog(
            this,
            "Sélectionnez un lit:",
            "Assigner Patient",
            JOptionPane.QUESTION_MESSAGE,
            null,
            bedNames,
            bedNames[0]
        );
        
        if (selectedBedName != null) {
            for (Bed bed : freeBeds) {
                if (bed.number.equals(selectedBedName)) {
                    assignPatient(bed);
                    break;
                }
            }
        }
    }
    
    // ==================== OTHER OPERATIONS ====================
    
    private void loadActiveHospitalizations() {
        tableModel.setRowCount(0);
        
        for (String pavillon : hospitalData.keySet()) {
            Map<String, List<Bed>> chambres = hospitalData.get(pavillon);
            for (String chambre : chambres.keySet()) {
                List<Bed> beds = chambres.get(chambre);
                for (Bed bed : beds) {
                    if (bed.occupied) {
                        String litInfo = pavillon + " - " + chambre + " - " + bed.number;
                        tableModel.addRow(new Object[]{
                            bed.patientName,
                            litInfo,
                            bed.dateEntree,
                            bed.daysHospitalized
                        });
                    }
                }
            }
        }
    }
    
    private void updateStatistics() {
        int total = 0;
        int occupied = 0;
        
        for (Map<String, List<Bed>> chambres : hospitalData.values()) {
            for (List<Bed> beds : chambres.values()) {
                total += beds.size();
                for (Bed bed : beds) {
                    if (bed.occupied) occupied++;
                }
            }
        }
        
        int free = total - occupied;
        double rate = total > 0 ? (occupied * 100.0 / total) : 0;
        
        totalBedsLabel.setText(String.valueOf(total));
        occupiedBedsLabel.setText(String.valueOf(occupied));
        freeBedsLabel.setText(String.valueOf(free));
        occupancyRateLabel.setText(String.format("%.0f%%", rate));
    }
    
    private void dischargeBed(Bed bed) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Libérer le lit pour le patient: " + bed.patientName + " ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            bed.occupied = false;
            bed.status = "libre";
            bed.patientName = "";
            bed.dateEntree = "";
            bed.daysHospitalized = 0;
            
            JOptionPane.showMessageDialog(this, 
                "Patient libéré avec succès!", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            
            loadData();
            loadBedGrid();
        }
    }
    
    private void dischargeFromTable() {
        int selectedRow = activeHospitalizationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un patient dans la liste!",
                "Attention", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String patientName = (String) tableModel.getValueAt(selectedRow, 0);
        
        for (Map<String, List<Bed>> chambres : hospitalData.values()) {
            for (List<Bed> beds : chambres.values()) {
                for (Bed bed : beds) {
                    if (bed.occupied && bed.patientName.equals(patientName)) {
                        dischargeBed(bed);
                        return;
                    }
                }
            }
        }
    }
    
    // ==================== MAIN ====================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        new HospitalizationPage();
    }
}