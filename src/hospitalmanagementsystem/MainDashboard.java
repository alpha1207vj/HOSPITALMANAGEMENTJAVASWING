package hospitalmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainDashboard extends JFrame implements ActionListener {
    
    // User info
    private String username;
    private String userRole;
    
    // Menu buttons
    JButton btnPatient, btnDoctor, btnAdmission, btnHospitalization;
    JButton btnConsultation, btnExamen, btnMedicine, btnBilling;
    JButton btnReports, btnSettings, btnLogout;
    
    // Welcome label
    JLabel welcomeLabel, roleLabel;
    
    public MainDashboard(String username, String role) {
        this.username = username;
        this.userRole = role;
        
        setTitle("Système de Gestion Hospitalière - Dashboard");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        
        // Main background
        getContentPane().setBackground(new Color(240, 242, 245));
        
        // ==================== TOP HEADER ====================
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // ==================== MAIN CONTENT ====================
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        
        // ==================== FOOTER ====================
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    // ==================== HEADER PANEL ====================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(1200, 140));
        header.setBackground(new Color(41, 128, 185));
        header.setLayout(null);
        
        // Hospital icon and title
        JLabel iconLabel = new JLabel("🏥");
        iconLabel.setBounds(50, 30, 80, 80);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        header.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Système de Gestion Hospitalière");
        titleLabel.setBounds(140, 35, 600, 40);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Plateforme de gestion complète");
        subtitleLabel.setBounds(140, 75, 400, 25);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        header.add(subtitleLabel);
        
        // User info panel (top right)
        JPanel userPanel = new JPanel();
        userPanel.setBounds(900, 30, 250, 80);
        userPanel.setBackground(new Color(52, 152, 219));
        userPanel.setLayout(null);
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setBounds(10, 10, 30, 30);
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        userPanel.add(userIcon);
        
        JLabel userLabel = new JLabel(username);
        userLabel.setBounds(45, 10, 190, 25);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);
        
        JLabel roleLabel = new JLabel("Rôle: " + userRole);
        roleLabel.setBounds(45, 35, 190, 20);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(236, 240, 241));
        userPanel.add(roleLabel);
        
        header.add(userPanel);
        
        return header;
    }
    
    // ==================== MAIN PANEL ====================
    private JPanel createMainPanel() {
        JPanel main = new JPanel();
        main.setBackground(new Color(240, 242, 245));
        main.setLayout(null);
        
        // Welcome section
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBounds(50, 20, 1100, 80);
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setLayout(null);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        welcomeLabel = new JLabel("Bienvenue, " + username + " !");
        welcomeLabel.setBounds(20, 10, 600, 30);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(44, 62, 80));
        welcomePanel.add(welcomeLabel);
        
        JLabel infoLabel = new JLabel("Sélectionnez une option ci-dessous pour commencer");
        infoLabel.setBounds(20, 45, 600, 20);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(127, 140, 141));
        welcomePanel.add(infoLabel);
        
        main.add(welcomePanel);
        
        // Menu cards section
        JPanel menuPanel = new JPanel();
        menuPanel.setBounds(50, 120, 1100, 430);
        menuPanel.setBackground(new Color(240, 242, 245));
        menuPanel.setLayout(new GridLayout(3, 4, 20, 20));
        
        // Create menu cards
        btnPatient = createMenuCard("👥", "Gestion Patients", 
            "Ajouter, modifier et consulter les patients", new Color(46, 204, 113));
        
        btnDoctor = createMenuCard("👨‍⚕️", "Gestion Médecins", 
            "Gérer les médecins et spécialités", new Color(52, 152, 219));
        
        btnAdmission = createMenuCard("📋", "Admissions", 
            "Créer et gérer les admissions", new Color(155, 89, 182));
        
        btnHospitalization = createMenuCard("🛏️", "Hospitalisation", 
            "Gérer les lits et chambres", new Color(230, 126, 34));
        
        btnConsultation = createMenuCard("🩺", "Consultations", 
            "Enregistrer les consultations", new Color(26, 188, 156));
        
        btnExamen = createMenuCard("🔬", "Examens", 
            "Commander et gérer les tests", new Color(52, 73, 94));
        
        btnMedicine = createMenuCard("💊", "Pharmacie", 
            "Gérer les médicaments", new Color(231, 76, 60));
        
        btnBilling = createMenuCard("💰", "Facturation", 
            "Factures et paiements", new Color(241, 196, 15));
        
        btnReports = createMenuCard("📊", "Rapports", 
            "Statistiques et rapports", new Color(149, 165, 166));
        
        btnSettings = createMenuCard("⚙️", "Paramètres", 
            "Configuration du système", new Color(127, 140, 141));
        
        btnLogout = createMenuCard("🚪", "Déconnexion", 
            "Se déconnecter du système", new Color(192, 57, 43));
        
        // Add empty panel for layout (3x4 grid, only 11 buttons)
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(new Color(240, 242, 245));
        
        // Add all to menu panel
        menuPanel.add(btnPatient);
        menuPanel.add(btnDoctor);
        menuPanel.add(btnAdmission);
        menuPanel.add(btnHospitalization);
        menuPanel.add(btnConsultation);
        menuPanel.add(btnExamen);
        menuPanel.add(btnMedicine);
        menuPanel.add(btnBilling);
        menuPanel.add(btnReports);
        menuPanel.add(btnSettings);
        menuPanel.add(btnLogout);
        menuPanel.add(emptyPanel);
        
        main.add(menuPanel);
        
        return main;
    }
    
    // ==================== CREATE MENU CARD ====================
    private JButton createMenuCard(String icon, String title, String description, Color color) {
        JButton card = new JButton();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setFocusPainted(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addActionListener(this);
        
        // Icon label
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBounds(10, 10, 50, 50);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        card.add(iconLabel);
        
        // Colored bar
        JPanel colorBar = new JPanel();
        colorBar.setBounds(0, 0, 5, 130);
        colorBar.setBackground(color);
        card.add(colorBar);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBounds(15, 65, 220, 25);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        card.add(titleLabel);
        
        // Description
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setBounds(15, 90, 220, 35);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(127, 140, 141));
        card.add(descLabel);
        
        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });
        
        return card;
    }
    
    // ==================== FOOTER PANEL ====================
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel();
        footer.setPreferredSize(new Dimension(1200, 40));
        footer.setBackground(new Color(44, 62, 80));
        footer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        
        JLabel footerLabel = new JLabel("© 2026 Système de Gestion Hospitalière | Version 1.0");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(189, 195, 199));
        footer.add(footerLabel);
        
        return footer;
    }
    
    // ==================== ACTION HANDLER ====================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment vous déconnecter ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                setVisible(false);
                new Login();
            }
        } 
        else if (e.getSource() == btnPatient) {
            JOptionPane.showMessageDialog(this, 
                "Module Gestion Patients en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new PatientManagement();
        }
        else if (e.getSource() == btnDoctor) {
            JOptionPane.showMessageDialog(this, 
                "Module Gestion Médecins en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new DoctorManagement();
        }
        else if (e.getSource() == btnAdmission) {
            JOptionPane.showMessageDialog(this, 
                "Module Admissions en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new AdmissionManagement();
        }
        else if (e.getSource() == btnHospitalization) {
            JOptionPane.showMessageDialog(this, 
                "Module Hospitalisation en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new HospitalizationManagement();
        }
        else if (e.getSource() == btnConsultation) {
            JOptionPane.showMessageDialog(this, 
                "Module Consultations en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new ConsultationManagement();
        }
        else if (e.getSource() == btnExamen) {
            JOptionPane.showMessageDialog(this, 
                "Module Examens en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new ExamenManagement();
        }
        else if (e.getSource() == btnMedicine) {
            JOptionPane.showMessageDialog(this, 
                "Module Pharmacie en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new MedicineManagement();
        }
        else if (e.getSource() == btnBilling) {
            JOptionPane.showMessageDialog(this, 
                "Module Facturation en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new BillingManagement();
        }
        else if (e.getSource() == btnReports) {
            JOptionPane.showMessageDialog(this, 
                "Module Rapports en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new ReportsManagement();
        }
        else if (e.getSource() == btnSettings) {
            JOptionPane.showMessageDialog(this, 
                "Module Paramètres en cours de développement...", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            // TODO: new SettingsManagement();
        }
    }
    
    // ==================== MAIN ====================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Test with dummy data
        new MainDashboard("Admin", "Administrateur");
    }
}