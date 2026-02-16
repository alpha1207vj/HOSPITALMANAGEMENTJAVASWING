package hospitalmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {
    JTextField textField;
    JPasswordField jPasswordField;
    JButton b1, b2;

    public Login() {
        setTitle("Connexion - Système de Gestion Hospitalière");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(new Color(236, 240, 241));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 500, 120);
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setLayout(null);

        JLabel hospitalLabel = new JLabel("🏥 Hôpital");
        hospitalLabel.setBounds(0, 20, 500, 40);
        hospitalLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        hospitalLabel.setForeground(Color.WHITE);
        hospitalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(hospitalLabel);

        JLabel subtitleLabel = new JLabel("Système de Gestion Hospitalier");
        subtitleLabel.setBounds(0, 65, 500, 30);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitleLabel);

        add(headerPanel);

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setBounds(50, 160, 400, 320);
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(null);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel loginTitle = new JLabel("Connexion");
        loginTitle.setBounds(130, 20, 140, 35);
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginTitle.setForeground(new Color(52, 73, 94));
        loginPanel.add(loginTitle);

        JLabel userIcon = new JLabel("👤");
        userIcon.setBounds(30, 80, 30, 30);
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        loginPanel.add(userIcon);

        JLabel namelabel = new JLabel("Nom d'utilisateur");
        namelabel.setBounds(30, 110, 150, 25);
        namelabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        namelabel.setForeground(new Color(52, 73, 94));
        loginPanel.add(namelabel);

        textField = new JTextField();
        textField.setBounds(30, 135, 340, 40);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(textField);

        JLabel passIcon = new JLabel("🔒");
        passIcon.setBounds(30, 185, 30, 30);
        passIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        loginPanel.add(passIcon);

        JLabel password = new JLabel("Mot de passe");
        password.setBounds(30, 215, 150, 25);
        password.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        password.setForeground(new Color(52, 73, 94));
        loginPanel.add(password);

        jPasswordField = new JPasswordField();
        jPasswordField.setBounds(30, 240, 340, 40);
        jPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        jPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(jPasswordField);

        add(loginPanel);

        // Buttons
        b1 = new JButton("Se connecter");
        b1.setBounds(50, 500, 180, 45);
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b1.setBackground(new Color(41, 128, 185));
        b1.setForeground(Color.WHITE);
        b1.setFocusPainted(false);
        b1.setBorderPainted(false);
        b1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b1.addActionListener(this);
        add(b1);

        b2 = new JButton("Annuler");
        b2.setBounds(270, 500, 180, 45);
        b2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b2.setBackground(new Color(192, 57, 43));
        b2.setForeground(Color.WHITE);
        b2.setFocusPainted(false);
        b2.setBorderPainted(false);
        b2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b2.addActionListener(this);
        add(b2);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            String user = textField.getText();
            String pass = new String(jPasswordField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez remplir tous les champs!",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Database check
            String query = "SELECT * FROM ADMIN WHERE login = ? AND password = ? AND statut = 'active'";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(query)) {

                pst.setString(1, user);
                pst.setString(2, pass);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");

                    // ✅ Open MainDashboard with username & role
                    new MainDashboard(user, role);
                    this.dispose(); // properly closes login

                } else {
                    JOptionPane.showMessageDialog(this,
                            "Nom d'utilisateur ou mot de passe invalide !",
                            "Erreur de connexion",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur de connexion à la base de données !\n" + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

        } else if (e.getSource() == b2) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment quitter ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        new Login();
    }
}
