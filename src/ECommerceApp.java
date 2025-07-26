import javax.swing.*;
import java.awt.*;

public class ECommerceApp {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegistrationPanel registrationPanel;
    private ShopPanel shopPanel;
    private CartUI cartUI;

    public ECommerceApp() {
        initializeGUI();
    }

    private void initializeGUI() {
        mainFrame = new JFrame("E-Commerce Platform");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels
        createPanels();
        setupPanelListeners();

        // Add panels to main panel
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registrationPanel, "REGISTER");
        mainPanel.add(shopPanel, "SHOP");
        mainPanel.add(cartUI, "CART");

        mainFrame.add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");

        mainFrame.setVisible(true);
    }

    private void createPanels() {
        loginPanel = new LoginPanel();
        registrationPanel = new RegistrationPanel();
        shopPanel = new ShopPanel();
        cartUI = new CartUI();
    }

    private void setupPanelListeners() {
        // Login panel listeners
        loginPanel.setOnLoginSuccess(e -> {
            shopPanel.updateCartButton();
            cardLayout.show(mainPanel, "SHOP");
        });

        loginPanel.setOnRegisterClick(e -> {
            registrationPanel.clearFields();
            cardLayout.show(mainPanel, "REGISTER");
        });

        // Registration panel listeners
        registrationPanel.setOnRegistrationSuccess(e -> {
            cardLayout.show(mainPanel, "LOGIN");
        });

        registrationPanel.setOnBackToLogin(e -> {
            cardLayout.show(mainPanel, "LOGIN");
        });

        // Shop panel listeners
        shopPanel.setOnViewCart(e -> {
            cartUI.loadCart(); // Refresh cart data
            cardLayout.show(mainPanel, "CART");
        });

        shopPanel.setOnLogout(e -> {
            DataManager.getInstance().setCurrentUser(null);
            DataManager.getInstance().clearCart();
            loginPanel.clearFields();
            shopPanel.updateCartButton();
            cardLayout.show(mainPanel, "LOGIN");
        });

        // CartUI listener for back to products
        cartUI.setOnBackToProducts(e -> {
            shopPanel.updateCartButton(); // Refresh shop panel
            cardLayout.show(mainPanel, "SHOP");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ECommerceApp();
        });
    }
}