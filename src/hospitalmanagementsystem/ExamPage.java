package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class ExamPage extends JFrame implements ActionListener {

    // ================== Components ==================
    JTextField txtPatientSearch, txtPatientName, txtAge, txtGender;
    JComboBox<String> comboDoctor, comboExamType, comboUrgency;
    JTextArea txtSymptoms;
    JButton btnSearchPatient, btnAddExam, btnRemoveExam, btnSave, btnPrint, btnCancel;
    JTable examTable;
    DefaultTableModel tableModel;

    // ================== Constructor ==================
    public ExamPage(String username, String role) {
        setTitle("Gestion des Examens - Système de Gestion Hospitalière");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(240, 242, 245));

        // Header
        add(createHeaderPanel(username, role), BorderLayout.NORTH);

        // Main content
        add(createMainPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    // ================== HEADER PANEL ==================
    private JPanel createHeaderPanel(String username, String role) {
        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(1200, 100));
        header.setBackground(new Color(41, 128, 185));
        header.setLayout(null);

        JLabel titleLabel = new JLabel("Gestion des Examens");
        titleLabel.setBounds(50, 30, 400, 40);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        // User info
        JLabel userLabel = new JLabel("Utilisateur: " + username + " | Rôle: " + role);
        userLabel.setBounds(800, 35, 400, 25);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(Color.WHITE);
        header.add(userLabel);

        return header;
    }

    // ================== MAIN PANEL ==================
    private JPanel createMainPanel() {
        JPanel main = new JPanel();
        main.setLayout(null);
        main.setBackground(new Color(240, 242, 245));

        // ---------- Patient Info Panel ----------
        JPanel patientPanel = new JPanel();
        patientPanel.setBounds(30, 20, 550, 180);
        patientPanel.setBackground(Color.WHITE);
        patientPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1), 
                "Patient Info", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        patientPanel.setLayout(null);

        txtPatientSearch = new JTextField();
        txtPatientSearch.setBounds(20, 30, 250, 30);
        patientPanel.add(txtPatientSearch);

        btnSearchPatient = new JButton("🔍 Rechercher");
        btnSearchPatient.setBounds(280, 30, 120, 30);
        btnSearchPatient.setBackground(new Color(41, 128, 185));
        btnSearchPatient.setForeground(Color.WHITE);
        btnSearchPatient.setFocusPainted(false);
        btnSearchPatient.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchPatient.addActionListener(this);
        patientPanel.add(btnSearchPatient);

        // Patient details
        JLabel lblName = new JLabel("Nom:");
        lblName.setBounds(20, 70, 80, 25);
        patientPanel.add(lblName);

        txtPatientName = new JTextField();
        txtPatientName.setBounds(100, 70, 200, 25);
        patientPanel.add(txtPatientName);

        JLabel lblAge = new JLabel("Âge:");
        lblAge.setBounds(20, 100, 80, 25);
        patientPanel.add(lblAge);

        txtAge = new JTextField();
        txtAge.setBounds(100, 100, 80, 25);
        patientPanel.add(txtAge);

        JLabel lblGender = new JLabel("Genre:");
        lblGender.setBounds(200, 100, 80, 25);
        patientPanel.add(lblGender);

        txtGender = new JTextField();
        txtGender.setBounds(270, 100, 80, 25);
        patientPanel.add(txtGender);

        main.add(patientPanel);

        // ---------- Exam Request Panel ----------
        JPanel examPanel = new JPanel();
        examPanel.setBounds(600, 20, 550, 180);
        examPanel.setBackground(Color.WHITE);
        examPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1), 
                "Demande d'Examen", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        examPanel.setLayout(null);

        JLabel lblExamType = new JLabel("Type d'Examen:");
        lblExamType.setBounds(20, 30, 120, 25);
        examPanel.add(lblExamType);

        comboExamType = new JComboBox<>(new String[]{"Sang", "Radiographie", "IRM", "ECG", "Autre"});
        comboExamType.setBounds(150, 30, 150, 25);
        examPanel.add(comboExamType);

        JLabel lblDoctor = new JLabel("Médecin:");
        lblDoctor.setBounds(20, 65, 120, 25);
        examPanel.add(lblDoctor);

        comboDoctor = new JComboBox<>(new String[]{"Dr. Dupont", "Dr. Martin", "Dr. Bernard"});
        comboDoctor.setBounds(150, 65, 150, 25);
        examPanel.add(comboDoctor);

        JLabel lblUrgency = new JLabel("Urgence:");
        lblUrgency.setBounds(20, 100, 120, 25);
        examPanel.add(lblUrgency);

        comboUrgency = new JComboBox<>(new String[]{"Normal", "Urgent", "Critique"});
        comboUrgency.setBounds(150, 100, 150, 25);
        examPanel.add(comboUrgency);

        JLabel lblSymptoms = new JLabel("Symptômes / Notes:");
        lblSymptoms.setBounds(20, 135, 130, 25);
        examPanel.add(lblSymptoms);

        txtSymptoms = new JTextArea();
        txtSymptoms.setBounds(150, 135, 350, 35);
        txtSymptoms.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        examPanel.add(txtSymptoms);

        main.add(examPanel);

        // ---------- Exam Table Panel ----------
        JPanel tablePanel = new JPanel();
        tablePanel.setBounds(30, 220, 1120, 350);
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Examens Demandés", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        tablePanel.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Examen", "Médecin", "Urgence", "Statut", "Résultat"}, 0);
        examTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(examTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add / Remove buttons
        JPanel tableButtons = new JPanel();
        tableButtons.setBackground(Color.WHITE);
        btnAddExam = new JButton("Ajouter Examen");
        btnRemoveExam = new JButton("Supprimer Examen");
        btnAddExam.setBackground(new Color(46, 204, 113)); btnAddExam.setForeground(Color.WHITE);
        btnRemoveExam.setBackground(new Color(192, 57, 43)); btnRemoveExam.setForeground(Color.WHITE);
        btnAddExam.setFocusPainted(false); btnRemoveExam.setFocusPainted(false);
        btnAddExam.addActionListener(this); btnRemoveExam.addActionListener(this);
        tableButtons.add(btnAddExam);
        tableButtons.add(btnRemoveExam);
        tablePanel.add(tableButtons, BorderLayout.SOUTH);

        main.add(tablePanel);

        // ---------- Bottom Action Buttons ----------
        JPanel actionPanel = new JPanel();
        actionPanel.setBounds(30, 580, 1120, 50);
        actionPanel.setBackground(new Color(240, 242, 245));
        btnSave = new JButton("Enregistrer");
        btnPrint = new JButton("Imprimer");
        btnCancel = new JButton("Annuler");
        btnSave.setBackground(new Color(41, 128, 185)); btnSave.setForeground(Color.WHITE);
        btnPrint.setBackground(new Color(241, 196, 15)); btnPrint.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(192, 57, 43)); btnCancel.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false); btnPrint.setFocusPainted(false); btnCancel.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(this); btnPrint.addActionListener(this); btnCancel.addActionListener(this);
        actionPanel.add(btnSave); actionPanel.add(btnPrint); actionPanel.add(btnCancel);
        main.add(actionPanel);

        return main;
    }

    // ================== ACTION HANDLER ==================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddExam) {
            tableModel.addRow(new Object[]{
                comboExamType.getSelectedItem(),
                comboDoctor.getSelectedItem(),
                comboUrgency.getSelectedItem(),
                "En attente",
                ""
            });
        } else if (e.getSource() == btnRemoveExam) {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow >= 0) tableModel.removeRow(selectedRow);
            else JOptionPane.showMessageDialog(this, "Sélectionnez un examen à supprimer.");
        } else if (e.getSource() == btnSave) {
            JOptionPane.showMessageDialog(this, "Examen(s) enregistré(s) avec succès !");
        } else if (e.getSource() == btnPrint) {
            JOptionPane.showMessageDialog(this, "Impression du rapport...");
        } else if (e.getSource() == btnCancel) {
            dispose();
        } else if (e.getSource() == btnSearchPatient) {
            JOptionPane.showMessageDialog(this, "Recherche patient simulée...");
        }
    }

    // ================== MAIN ==================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }
        new ExamPage("Admin", "Administrateur");
    }
}
