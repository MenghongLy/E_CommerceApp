import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistrationPanel extends JPanel {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private ActionListener onRegistrationSuccess;
    private ActionListener onBackToLogin;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/e_commerce";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Password";

    public RegistrationPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245)); // light background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // === Title ===
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // === Form Fields ===
        gbc.gridwidth = 1;

        JLabel nameLabel = new JLabel("Name:");
        styleLabel(nameLabel);
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        add(nameLabel, gbc);

        nameField = createRoundedTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        styleLabel(emailLabel);
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        add(emailLabel, gbc);

        emailField = createRoundedTextField();
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        styleLabel(passwordLabel);
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField = (JPasswordField) createRoundedTextField(passwordField);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        styleLabel(confirmPasswordLabel);
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField = (JPasswordField) createRoundedTextField(confirmPasswordField);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST;
        add(confirmPasswordField, gbc);

        // === Buttons ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setOpaque(false);

        JButton createButton = createRoundedButton("Create Account", new Color(0, 123, 255), Color.WHITE);
        createButton.addActionListener(e -> performRegistration());

        JButton backButton = createRoundedButton("Back to Login", new Color(108, 117, 125), Color.WHITE);
        backButton.addActionListener(e -> {
            if (onBackToLogin != null) onBackToLogin.actionPerformed(e);
        });

        buttonPanel.add(createButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
    }

    // === Styles ===
    private void styleLabel(JLabel label) {
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
    }

    private JTextField createRoundedTextField() {
        return createRoundedTextField(new JTextField(20));
    }

    private JTextField createRoundedTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(fg);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(160, 40));
        return button;
    }

    // === Database Connection ===
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    // === Email Validation ===
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    // === Password Hashing (disabled for compatibility) ===
    private String hashPassword(String password) {
        // Return plain text password for compatibility with existing login system
        return password;
    }

    // === Check if email already exists ===
    private boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM customers WHERE Email = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }

    // === Insert user into database ===
    private boolean insertUser(String name, String email, String password) {
        String query = "INSERT INTO customer (Name, Email, Password) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // Store plain text password

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // === Registration Logic ===
    private void performRegistration() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if email already exists
        if (emailExists(email)) {
            JOptionPane.showMessageDialog(this, "An account with this email already exists!", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Try to insert user into database
        if (insertUser(name, email, password)) {
            JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            if (onRegistrationSuccess != null) {
                onRegistrationSuccess.actionPerformed(null);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create account. Please try again.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setOnRegistrationSuccess(ActionListener listener) {
        this.onRegistrationSuccess = listener;
    }

    public void setOnBackToLogin(ActionListener listener) {
        this.onBackToLogin = listener;
    }

    public void clearFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    // === Gradient Background (optional for premium feel) ===
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(240, 240, 255),
                0, getHeight(), new Color(200, 200, 255));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}