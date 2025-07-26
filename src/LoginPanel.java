import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private ActionListener onLoginSuccess;
    private ActionListener onRegisterClick;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/e_commerce";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Password";

    public LoginPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245)); // light background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // === LOGO ===
        ImageIcon logoIcon = new ImageIcon("images/logo.png");
        // Make sure you have a "logo.png" in an "images" folder or update path accordingly
        Image logoImg = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(logoLabel, gbc);

        // === TITLE ===
        JLabel titleLabel = new JLabel("PERFUMIA");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 50));
        titleLabel.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // === EMAIL LABEL ===
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 2;
        add(emailLabel, gbc);

        // === EMAIL FIELD ===
        emailField = createRoundedTextField();
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        add(emailField, gbc);

        // === PASSWORD LABEL ===
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 3;
        add(passwordLabel, gbc);

        // === PASSWORD FIELD ===
        passwordField = new JPasswordField(20);
        passwordField = (JPasswordField) createRoundedTextField(passwordField);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        // === BUTTONS ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setOpaque(false);

        JButton loginButton = createRoundedButton("Login", new Color(0, 123, 255), Color.WHITE);
        loginButton.addActionListener(e -> performLogin());

        JButton registerButton = createRoundedButton("Register", new Color(108, 117, 125), Color.WHITE);
        registerButton.addActionListener(e -> {
            if (onRegisterClick != null) onRegisterClick.actionPerformed(e);
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
    }

    // === Database Connection Method ===
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    // === User Authentication Method ===
    private User authenticateUser(String email, String password) {
        String query = "SELECT User_ID, Name, Email FROM customer WHERE Email = ? AND Password = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password); // In production, use hashed passwords!

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("User_ID");
                String name = rs.getString("Name");
                String userEmail = rs.getString("Email");
                return new User(userId, name, userEmail, password);
            }

        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Database connection error. Please try again later.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return null; // Authentication failed
    }

    // === Rounded Components ===
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
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both email and password!",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Perform authentication in background thread to avoid UI freezing
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return authenticateUser(email, password);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    User user = get();
                    if (user != null) {
                        // Login successful
                        DataManager.getInstance().setCurrentUser(user);
                        JOptionPane.showMessageDialog(LoginPanel.this,
                                "Welcome, " + user.getName() + "!",
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE);

                        if (onLoginSuccess != null) {
                            onLoginSuccess.actionPerformed(null);
                        }
                        clearFields();
                    } else {
                        // Login failed
                        JOptionPane.showMessageDialog(LoginPanel.this,
                                "Invalid email or password. Please try again.",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                        passwordField.setText(""); // Clear password field
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(LoginPanel.this,
                            "An error occurred during login. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    public void setOnLoginSuccess(ActionListener listener) {
        this.onLoginSuccess = listener;
    }

    public void setOnRegisterClick(ActionListener listener) {
        this.onRegisterClick = listener;
    }

    public void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }

    // === Gradient Background ===
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