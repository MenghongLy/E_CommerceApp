import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CartUI extends JPanel {
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;
    private JLabel itemCountLabel;
    private DataManager dataManager;
    private ActionListener onBackToProducts;

    public CartUI() {
        dataManager = DataManager.getInstance();
        initializeUI();
        loadCart();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 243, 250));

        // Header with gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(25, 25, 112),
                        getWidth(), getHeight(), new Color(72, 61, 139));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("🛍️ Shopping Cart");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Welcome");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        userPanel.add(userLabel);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Cart panel
        JPanel cartPanel = createCartPanel();
        add(cartPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setBackground(new Color(240, 240, 240));

        JButton backToProductsButton = createStyledButton(" Back to Shop", new Color(108, 117, 125), Color.WHITE);
        backToProductsButton.setPreferredSize(new Dimension(160, 40));
        backToProductsButton.addActionListener(e -> {
            if (onBackToProducts != null) {
                onBackToProducts.actionPerformed(e);
            }
        });
        statusPanel.add(backToProductsButton, BorderLayout.WEST);

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0, 120, 0));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(totalLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    private String getUserName() {
        User user = DataManager.getInstance().getCurrentUser();
        return (user != null && user.getName() != null) ? user.getName() : "User";
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "🛒 Shopping Cart",
                0, 0,
                new Font("Arial", Font.BOLD, 14)
        ));

        // Cart table
        String[] cartColumns = {"Product", "Price", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(25);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(90);

        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartScrollPane.setPreferredSize(new Dimension(500, 400));

        panel.add(cartScrollPane, BorderLayout.CENTER);

        // Cart info panel
        JPanel cartInfoPanel = new JPanel(new BorderLayout());

        // Cart summary
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemCountLabel = new JLabel("Items in cart: 0");
        itemCountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        summaryPanel.add(itemCountLabel);
        cartInfoPanel.add(summaryPanel, BorderLayout.NORTH);

        // Cart buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton updateQtyButton = createStyledButton("Update Qty", new Color(108, 117, 125), Color.WHITE);
        updateQtyButton.setPreferredSize(new Dimension(110, 30));
        updateQtyButton.addActionListener(e -> updateQuantity());

        JButton removeButton = createStyledButton("❌ Remove", new Color(220, 53, 69), Color.WHITE);
        removeButton.setPreferredSize(new Dimension(100, 30));
        removeButton.addActionListener(new RemoveFromCartListener());

        JButton clearCartButton = createStyledButton(" Clear All", new Color(108, 117, 125), Color.WHITE);
        clearCartButton.setPreferredSize(new Dimension(100, 30));
        clearCartButton.addActionListener(e -> clearCart());

        JButton checkoutButton = createStyledButton("💳 Checkout", new Color(0, 123, 255), Color.WHITE);
        checkoutButton.setPreferredSize(new Dimension(120, 35));
        checkoutButton.addActionListener(e -> checkout());

        buttonPanel.add(updateQtyButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearCartButton);
        buttonPanel.add(checkoutButton);

        cartInfoPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(cartInfoPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void loadCart() {
        cartTableModel.setRowCount(0);
        double total = dataManager.getCartTotal();
        List<CartItem> cartItems = dataManager.getCartItems();

        for (CartItem item : cartItems) {
            Object[] row = {
                    item.getProduct().getName(),
                    String.format("$%.2f", item.getProduct().getPrice()),
                    item.getQuantity(),
                    String.format("$%.2f", item.getTotalPrice())
            };
            cartTableModel.addRow(row);
        }

        totalLabel.setText(String.format("Total: $%.2f", total));
        itemCountLabel.setText("Items in cart: " + cartItems.size());
    }

    private void updateQuantity() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update quantity.");
            return;
        }

        String productName = (String) cartTableModel.getValueAt(selectedRow, 0);
        CartItem item = dataManager.getCartItems().stream()
                .filter(ci -> ci.getProduct().getName().equals(productName))
                .findFirst()
                .orElse(null);

        if (item != null) {
            String input = JOptionPane.showInputDialog(this,
                    "Enter new quantity for " + productName + ":",
                    item.getQuantity());

            if (input != null) {
                try {
                    int newQuantity = Integer.parseInt(input);
                    if (newQuantity > 0) {
                        item.setQuantity(newQuantity);
                        loadCart();
                        JOptionPane.showMessageDialog(this, "Quantity updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number.");
                }
            }
        }
    }

    private class RemoveFromCartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(CartUI.this,
                        "Please select an item to remove from cart.");
                return;
            }

            String productName = (String) cartTableModel.getValueAt(selectedRow, 0);
            removeFromCart(productName);
        }
    }

    private void removeFromCart(String productName) {
        int index = -1;
        List<CartItem> cartItems = dataManager.getCartItems();
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProduct().getName().equals(productName)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            dataManager.removeFromCart(index);
            JOptionPane.showMessageDialog(this,
                    String.format("❌ Removed %s from cart!", productName));
            loadCart();
        }
    }

    private void clearCart() {
        if (dataManager.getCartItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is already empty!");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all items from the cart?",
                "Clear Cart",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            dataManager.clearCart();
            JOptionPane.showMessageDialog(this, "🗑️ Cart cleared!");
            loadCart();
        }
    }

    private void checkout() {
        double total = dataManager.getCartTotal();
        String orderSummary = String.format(
                "Order Summary:\n\n" +
                        "Items: %d\n" +
                        "Total: $%.2f\n\n" +
                        "Proceed with checkout?",
                dataManager.getCartItems().size(), total);
        if (DataManager.getInstance().checkout()) {
            JOptionPane.showMessageDialog(null, "Checkout successful!");
        } else {
            JOptionPane.showMessageDialog(null, "Checkout failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        int result = JOptionPane.showConfirmDialog(this,
                orderSummary,
                "Checkout Confirmation - 💳",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            dataManager.clearCart();
            loadCart();
            JOptionPane.showMessageDialog(this,
                    "🎉 Order placed successfully!\nThank you for your purchase!",
                    "Order Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        return button;
    }

    public void setOnBackToProducts(ActionListener listener) {
        this.onBackToProducts = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, new Color(240, 240, 255),
                0, getHeight(), new Color(200, 200, 255));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("🛒 Shopping Cart");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 700);
            frame.setLocationRelativeTo(null);

            CartUI cartUI = new CartUI();
            frame.add(cartUI);
            frame.setVisible(true);
        });
    }
}