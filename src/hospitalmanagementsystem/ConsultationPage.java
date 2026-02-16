package hospitalmanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class ConsultationPage extends JFrame implements ActionListener {

    JTextField txtAdmission, txtDate;
    JTextArea txtSymptoms, txtDiagnosis, txtObservations;
    JComboBox<String> comboDoctor;

    JTable consultationTable;
    DefaultTableModel tableModel;

    JButton btnAdd, btnModify, btnCancel;

    public ConsultationPage() {

        setTitle("Système de Gestion Hospitalière - Consultation");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        getContentPane().setBackground(new Color(240, 242, 245));

        // ===== HEADER =====
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(1200, 90));
        header.setLayout(null);

        JLabel title = new JLabel("📋 Gestion des Consultations");
        title.setBounds(40, 20, 600, 40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title);

        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 242, 245));

        // ================= LEFT PANEL (FORM) =================
        JPanel formPanel = new JPanel();
        formPanel.setBounds(20, 20, 550, 540);
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Nouvelle Consultation"));

        JLabel lblAdmission = new JLabel("Num Admission:");
        lblAdmission.setBounds(20, 40, 150, 25);
        formPanel.add(lblAdmission);

        txtAdmission = new JTextField();
        txtAdmission.setBounds(170, 40, 150, 25);
        formPanel.add(txtAdmission);

        JLabel lblDoctor = new JLabel("Médecin:");
        lblDoctor.setBounds(20, 80, 150, 25);
        formPanel.add(lblDoctor);

        comboDoctor = new JComboBox<>(new String[]{
                "11 - Dr. Dupont",
                "12 - Dr. Martin",
                "13 - Dr. Leroy"
        });
        comboDoctor.setBounds(170, 80, 200, 25);
        formPanel.add(comboDoctor);

        JLabel lblDate = new JLabel("Date Consultation:");
        lblDate.setBounds(20, 120, 150, 25);
        formPanel.add(lblDate);

        txtDate = new JTextField("15/02/2026");
        txtDate.setBounds(170, 120, 150, 25);
        formPanel.add(txtDate);

        JLabel lblSymptoms = new JLabel("Symptômes:");
        lblSymptoms.setBounds(20, 170, 150, 25);
        formPanel.add(lblSymptoms);

        txtSymptoms = new JTextArea();
        JScrollPane spSymptoms = new JScrollPane(txtSymptoms);
        spSymptoms.setBounds(20, 200, 500, 80);
        formPanel.add(spSymptoms);

        JLabel lblDiagnosis = new JLabel("Diagnostic:");
        lblDiagnosis.setBounds(20, 290, 150, 25);
        formPanel.add(lblDiagnosis);

        txtDiagnosis = new JTextArea();
        JScrollPane spDiagnosis = new JScrollPane(txtDiagnosis);
        spDiagnosis.setBounds(20, 320, 500, 80);
        formPanel.add(spDiagnosis);

        JLabel lblObservations = new JLabel("Observations:");
        lblObservations.setBounds(20, 410, 150, 25);
        formPanel.add(lblObservations);

        txtObservations = new JTextArea();
        JScrollPane spObservations = new JScrollPane(txtObservations);
        spObservations.setBounds(20, 440, 500, 60);
        formPanel.add(spObservations);

        mainPanel.add(formPanel);

        // ================= RIGHT PANEL (TABLE + BUTTONS) =================
        JPanel tablePanel = new JPanel();
        tablePanel.setBounds(600, 20, 550, 540);
        tablePanel.setLayout(null);
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Liste des Consultations"));

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{
                "ID", "Admission", "Médecin ID", "Date", "Symptômes", "Diagnostic", "Observations"
        });

        consultationTable = new JTable(tableModel);
        JScrollPane spTable = new JScrollPane(consultationTable);
        spTable.setBounds(20, 30, 500, 350);
        tablePanel.add(spTable);

        // ===== Buttons under table =====
        btnAdd = new JButton("➕ Ajouter");
        btnAdd.setBounds(50, 420, 120, 30);
        tablePanel.add(btnAdd);

        btnModify = new JButton("✏ Modifier");
        btnModify.setBounds(200, 420, 120, 30);
        tablePanel.add(btnModify);

        btnCancel = new JButton("❌ Annuler");
        btnCancel.setBounds(350, 420, 120, 30);
        tablePanel.add(btnCancel);

        mainPanel.add(tablePanel);

        add(mainPanel, BorderLayout.CENTER);

        // Listeners
        btnAdd.addActionListener(this);
        btnModify.addActionListener(this);
        btnCancel.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnCancel) {
            dispose();
        }

        if (e.getSource() == btnAdd) {

            String admission = txtAdmission.getText();
            String selectedDoctor = comboDoctor.getSelectedItem().toString();
            String medecinID = selectedDoctor.split(" - ")[0];

            String date = txtDate.getText();
            String symptoms = txtSymptoms.getText();
            String diagnosis = txtDiagnosis.getText();
            String observations = txtObservations.getText();

            tableModel.addRow(new Object[]{
                    tableModel.getRowCount() + 1,
                    admission,
                    medecinID,
                    date,
                    symptoms,
                    diagnosis,
                    observations
            });
        }

        if (e.getSource() == btnModify) {

            int selectedRow = consultationTable.getSelectedRow();

            if (selectedRow >= 0) {

                String selectedDoctor = comboDoctor.getSelectedItem().toString();
                String medecinID = selectedDoctor.split(" - ")[0];

                tableModel.setValueAt(txtAdmission.getText(), selectedRow, 1);
                tableModel.setValueAt(medecinID, selectedRow, 2);
                tableModel.setValueAt(txtDate.getText(), selectedRow, 3);
                tableModel.setValueAt(txtSymptoms.getText(), selectedRow, 4);
                tableModel.setValueAt(txtDiagnosis.getText(), selectedRow, 5);
                tableModel.setValueAt(txtObservations.getText(), selectedRow, 6);

            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une consultation à modifier.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new ConsultationPage();
    }
}
