
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ShopPanel extends JPanel {
    private ActionListener onViewCart;
    private ActionListener onLogout;
    private JButton cartButton;
    private JPanel productsPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private int quantity;

    public ShopPanel() {
        initializeUI();
        loadProducts();
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

        JLabel welcomeLabel = new JLabel("Welcome to Perfumia");
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Welcome, " + getName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        cartButton = createStyledButton("View Cart", new Color(255, 165, 0), Color.BLACK);
        cartButton.addActionListener(e -> {
            if (onViewCart != null) onViewCart.actionPerformed(e);
        });

        JButton logoutButton = createStyledButton("Logout", new Color(220, 20, 60), Color.WHITE);
        logoutButton.addActionListener(e -> {
            if (onLogout != null) onLogout.actionPerformed(e);
        });

        userPanel.add(userLabel);
        userPanel.add(cartButton);
        userPanel.add(logoutButton);

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 255));

        // Products grid
        productsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        productsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        productsPanel.setBackground(new Color(245, 247, 255));

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(scrollPane, "Products");

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private String getUserName() {
        User user = DataManager.getInstance().getCurrentUser();
        return (user != null && user.getName() != null) ? user.getName() : "User";
    }

    private void loadProducts() {
        productsPanel.removeAll();
        for (Product product : DataManager.getInstance().getProducts()) {
            JPanel productCard = createProductCard(product, quantity);
            productsPanel.add(productCard);
        }
        productsPanel.revalidate();
        productsPanel.repaint();
        cardLayout.show(contentPanel, "Products");
    }

    private JPanel createProductCard(Product product, int quantity) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(300, 200));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Product image
        ImageIcon originalIcon = product.getImageIcon();
        Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(resizedIcon, SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(120, 120));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(250, 250, 250));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Hover effect for image
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                imageLabel.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            }
        });

        // Product info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        priceLabel.setForeground(new Color(34, 139, 34));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html>" + product.getDescription() + "</html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLabel.setForeground(new Color(80, 80, 80));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(descLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setOpaque(false);

        JButton addButton = createStyledButton("Add to Cart", new Color(34, 139, 34), Color.WHITE);
        addButton.setPreferredSize(new Dimension(110, 30));
        addButton.addActionListener(e -> {
            DataManager.getInstance().addToCart(product);
            updateCartButton();
            JOptionPane.showMessageDialog(this, product.getName() + " added to cart!");
        });

        JButton detailsButton = createStyledButton("View Details", new Color(0, 123, 255), Color.WHITE);
        detailsButton.setPreferredSize(new Dimension(110, 30));
        detailsButton.addActionListener(e -> showProductDetails(product));

        buttonPanel.add(addButton);
        buttonPanel.add(detailsButton);

        card.add(imageLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Hover effect for card
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true),
                        new EmptyBorder(10, 10, 10, 10)
                ));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return card;
    }

    private void showProductDetails(Product product) {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(new Color(245, 247, 255));

        // Product info panel with vertical BoxLayout
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Add primary image (index 0)
        List<ImageIcon> imageIcons = product.getImageIcons();
        ImageIcon primaryIcon = imageIcons.isEmpty() ? product.createPlaceholderImage() : imageIcons.get(0);
        Image scaledPrimaryImage = primaryIcon.getImage().getScaledInstance(200, 200, Image.SCALE_FAST);
        ImageIcon scaledPrimaryIcon = new ImageIcon(scaledPrimaryImage);
        JLabel primaryImageLabel = new JLabel(scaledPrimaryIcon);
        primaryImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(primaryImageLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Product name
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Product price
        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        priceLabel.setForeground(new Color(34, 139, 34));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Description section
        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
        descPanel.setOpaque(true);
        descPanel.setBackground(new Color(250, 250, 255));
        descPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Description title
        JLabel descTitle = new JLabel("Description:");
        descTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        descTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        descPanel.add(descTitle);
        descPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Description text area
        JTextArea descArea = new JTextArea(product.getDescription());
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setOpaque(false);
        descArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descArea.setForeground(new Color(80, 80, 80));
        descPanel.add(descArea);
        descPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Secondary and tertiary images in 2x1 grid (two rows, one column)
        JPanel imagesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        imagesPanel.setOpaque(false);
        // Secondary image (index 1)
        if (imageIcons.size() > 1 && imageIcons.get(1) != null) {
            ImageIcon secondaryIcon = imageIcons.get(1);
            Image scaledSecondaryImage = secondaryIcon.getImage().getScaledInstance(1000, 1000, Image.SCALE_FAST);
            JLabel secondaryImageLabel = new JLabel(new ImageIcon(scaledSecondaryImage));
            secondaryImageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            imagesPanel.add(secondaryImageLabel);
        } else {
            Image placeholderImage = product.createPlaceholderImage().getImage().getScaledInstance(150, 150, Image.SCALE_FAST);
            JLabel placeholderLabel = new JLabel(new ImageIcon(placeholderImage));
            placeholderLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            imagesPanel.add(placeholderLabel);
        }
        // Tertiary image (index 2)
        if (imageIcons.size() > 2 && imageIcons.get(2) != null) {
            ImageIcon tertiaryIcon = imageIcons.get(2);
            Image scaledTertiaryImage = tertiaryIcon.getImage().getScaledInstance(1200, 1000, Image.SCALE_FAST);
            JLabel tertiaryImageLabel = new JLabel(new ImageIcon(scaledTertiaryImage));
            tertiaryImageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            imagesPanel.add(tertiaryImageLabel);
        } else {
            Image placeholderImage = product.createPlaceholderImage().getImage().getScaledInstance(150, 150, Image.SCALE_FAST);
            JLabel placeholderLabel = new JLabel(new ImageIcon(placeholderImage));
            placeholderLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            imagesPanel.add(placeholderLabel);
        }
        descPanel.add(imagesPanel);

        // Add description panel to info panel
        infoPanel.add(descPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Back button
        JButton backButton = createStyledButton("Back to Products", new Color(108, 117, 125), Color.WHITE);
        backButton.setPreferredSize(new Dimension(160, 40));
        backButton.addActionListener(e -> cardLayout.show(contentPanel, "Products"));

        // Assemble details panel
        detailsPanel.add(infoPanel, BorderLayout.CENTER);
        detailsPanel.add(backButton, BorderLayout.SOUTH);

        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setBorder(null);
        detailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(detailsScrollPane, "Details_" + product.getProductId());
        cardLayout.show(contentPanel, "Details_" + product.getProductId());
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

    public void updateCartButton() {
        int itemCount = DataManager.getInstance().getCartItems().size();
        cartButton.setText("View Cart (" + itemCount + ")");
    }

    public void setOnViewCart(ActionListener listener) {
        this.onViewCart = listener;
    }

    public void setOnLogout(ActionListener listener) {
        this.onLogout = listener;
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
}