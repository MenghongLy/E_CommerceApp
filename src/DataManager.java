import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Product> products;
    private List<CartItem> cartItems;
    private User currentUser;
    private Connection conn;

    private DataManager() {
        initializeDatabaseConnection();
        initializeData();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void initializeDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/e_commerce";
        String user = "root";
        String password = "Password";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    private void initializeData() {
        products = new ArrayList<>();
        cartItems = new ArrayList<>();
        loadProductsFromDatabase();
    }

    private void loadProductsFromDatabase() {
        String query = "SELECT P_ID, Name, Price, Description FROM product";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int productId = rs.getInt("P_ID");
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");
                String description = rs.getString("Description");

                List<String> imagePaths = getImagePathsForProduct(productId);
                products.add(new Product(productId, name, price, description, imagePaths));
            }
        } catch (SQLException e) {
            System.out.println("Failed to load products from database!");
            e.printStackTrace();
        }
    }

    private List<String> getImagePathsForProduct(int productId) {
        return switch (productId) {
            case 1 -> List.of("image/dior_sauvage.png", "image_description/suavage.jpg", "image_description/suavaged.jpg");
            case 2 -> List.of("image/chanel_no5.jpg", "image_description/channel.jpg", "image_description/channeld.jpg");
            case 3 -> List.of("image/ysl_libre.jpg", "image_description/ysl.jpg", "image_description/ysld.jpg");
            case 4 -> List.of("image/versace_eros.jpg", "image_description/Eros_versace.jpg", "image_description/Eros_versached.jpg");
            case 5 -> List.of("image_description/gucici.jpg", "image_description/gucici.jpg", "image_description/guccid.jpg");
            case 6 -> List.of("image_description/armani.jpg", "image_description/armani.jpg", "image_description/armanid.jpg");
            case 7 -> List.of("image/tomford_noir.jpg", "image_description/noir.jpg", "image_description/noird.jpg");
            case 8 -> List.of("image/prada_luna.jpg", "image_description/luna.jpg", "image_description/lunad.jpg");
            case 9 -> List.of("image/hermes_terre.jpg", "image_description/terre.jpg", "image_description/terred.jpg");
            case 10 -> List.of("image/le_male.jpg", "image_description/jpg.jpg", "image_description/jpgd.jpg");
            default -> new ArrayList<>(); // Empty list for unknown products
        };
    }

    public User login(String email, String password) {
        if (conn == null) {
            System.err.println("Database connection is not available!");
            return null;
        }

        String sql = "SELECT User_ID, Name, Email FROM customer WHERE Email = ? AND Password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password); // Note: Use hashed passwords in production
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("User_ID");
                    String name = rs.getString("Name");
                    String userEmail = rs.getString("Email");
                    User user = new User(userId, name, userEmail, password);
                    setCurrentUser(user);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            loadCartFromDatabase();
        } else {
            cartItems.clear();
        }
    }

    private void loadCartFromDatabase() {
        if (currentUser == null || conn == null) return;
        cartItems.clear();
        try {
            String cartSql = "SELECT Cart_ID FROM cart WHERE User_ID = ? ORDER BY Cart_ID DESC LIMIT 1";
            try (PreparedStatement cartStmt = conn.prepareStatement(cartSql)) {
                cartStmt.setInt(1, currentUser.getUserId());
                try (ResultSet cartRs = cartStmt.executeQuery()) {
                    if (cartRs.next()) {
                        int cartId = cartRs.getInt("Cart_ID");
                        loadCartItemsFromDatabase(cartId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCartItemsFromDatabase(int cartId) {
        if (conn == null) return;
        try {
            String sql = "SELECT cp.P_ID, cp.Quantity, p.Name, p.Price, p.Description " +
                    "FROM cart_product cp JOIN product p ON cp.P_ID = p.P_ID WHERE cp.Cart_ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, cartId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int productId = rs.getInt("P_ID");
                        int quantity = rs.getInt("Quantity");
                        String name = rs.getString("Name");
                        double price = rs.getDouble("Price");
                        String description = rs.getString("Description");
                        Product product = new Product(productId, name, price, description, getImagePathsForProduct(productId));
                        cartItems.add(new CartItem(product, quantity));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load cart items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addToCart(Product product) {
        if (currentUser == null) {
            System.err.println("No user logged in!");
            return;
        }

        try {
            // 1. Insert into cart and get generated Cart_ID
            int cartId = -1;
            String insertCartSql = "INSERT INTO cart (User_ID) VALUES (?)";
            try (PreparedStatement cartStmt = conn.prepareStatement(insertCartSql, Statement.RETURN_GENERATED_KEYS)) {
                cartStmt.setInt(1, currentUser.getUserId());
                cartStmt.executeUpdate();

                ResultSet generatedKeys = cartStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cartId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated Cart_ID.");
                }
            }

            // 2. Check if product is already in memory cart
            for (CartItem item : cartItems) {
                if (item.getProduct().getProductId() == product.getProductId()) {
                    item.setQuantity(item.getQuantity() + 1);
                    // Update quantity in cart_product
                    String updateSql = "UPDATE cart_product SET Quantity = ? WHERE Cart_ID = ? AND P_ID = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, item.getQuantity());
                        updateStmt.setInt(2, cartId);
                        updateStmt.setInt(3, product.getProductId());
                        updateStmt.executeUpdate();
                    }
                    return;
                }
            }

            // 3. Add product to in-memory cart
            cartItems.add(new CartItem(product, 1));

            // 4. Insert into cart_product table
            String insertCartProductSql = "INSERT INTO cart_product (Cart_ID, P_ID, Quantity) VALUES (?, ?, ?)";
            try (PreparedStatement cpStmt = conn.prepareStatement(insertCartProductSql)) {
                cpStmt.setInt(1, cartId);
                cpStmt.setInt(2, product.getProductId());
                cpStmt.setInt(3, 1); // default quantity is 1
                cpStmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Failed to add to cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeFromCart(int index) {
        if (index >= 0 && index < cartItems.size()) {
            CartItem item = cartItems.get(index);
            try {
                int cartId = getCurrentCartId();
                if (cartId != -1) {
                    String deleteSql = "DELETE FROM cart_product WHERE Cart_ID = ? AND P_ID = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                        stmt.setInt(1, cartId);
                        stmt.setInt(2, item.getProduct().getProductId());
                        stmt.executeUpdate();
                    }
                    cartItems.remove(index);
                }
            } catch (SQLException e) {
                System.err.println("Failed to remove from cart: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void clearCart() {
        try {
            int cartId = getCurrentCartId();
            if (cartId != -1) {
                String deleteItemsSql = "DELETE FROM cart_product WHERE Cart_ID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteItemsSql)) {
                    stmt.setInt(1, cartId);
                    stmt.executeUpdate();
                }
                String deleteCartSql = "DELETE FROM cart WHERE Cart_ID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteCartSql)) {
                    stmt.setInt(1, cartId);
                    stmt.executeUpdate();
                }
            }
            cartItems.clear();
        } catch (SQLException e) {
            System.err.println("Failed to clear cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getCurrentCartId() throws SQLException {
        if (currentUser == null) return -1;
        String sql = "SELECT Cart_ID FROM cart WHERE User_ID = ? ORDER BY Cart_ID DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Cart_ID");
                }
            }
        }
        return -1;
    }

    public boolean checkout() {
        if (currentUser == null) {
            System.err.println("No user logged in!");
            return false;
        }
        if (cartItems.isEmpty()) {
            System.err.println("Cart is empty!");
            return false;
        }

        try {
            // Start transaction
            conn.setAutoCommit(false);

            // 1. Insert into order table and get generated Order_ID
            int orderId = -1;
            double totalAmount = getCartTotal();
            String orderSql = "INSERT INTO `order` (Order_Amount, User_ID) VALUES (?, ?)";
            try (PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setDouble(1, totalAmount);
                orderStmt.setInt(2, currentUser.getUserId());
                orderStmt.executeUpdate();

                try (ResultSet rs = orderStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve generated Order_ID.");
                    }
                }
            }

            // 2. Insert into order_product table
            String orderProductSql = "INSERT INTO order_product (Order_ID, P_ID, Quantity) VALUES (?, ?, ?)";
            try (PreparedStatement orderProductStmt = conn.prepareStatement(orderProductSql)) {
                for (CartItem item : cartItems) {
                    orderProductStmt.setInt(1, orderId);
                    orderProductStmt.setInt(2, item.getProduct().getProductId());
                    orderProductStmt.setInt(3, item.getQuantity());
                    orderProductStmt.addBatch();
                }
                orderProductStmt.executeBatch();
            }

            // 3. Insert into payment table (excluding Type)
            String paymentSql = "INSERT INTO payment (Amount, User_ID, Order_ID) VALUES (?, ?, ?)";
            try (PreparedStatement paymentStmt = conn.prepareStatement(paymentSql, Statement.RETURN_GENERATED_KEYS)) {
                paymentStmt.setDouble(1, totalAmount);
                paymentStmt.setInt(2, currentUser.getUserId());
                paymentStmt.setInt(3, orderId);
                paymentStmt.executeUpdate();
            }

            // 4. Clear cart data from cart and cart_product
            int cartId = getCurrentCartId();
            if (cartId != -1) {
                String deleteItemsSql = "DELETE FROM cart_product WHERE Cart_ID = ?";
                try (PreparedStatement deleteItemsStmt = conn.prepareStatement(deleteItemsSql)) {
                    deleteItemsStmt.setInt(1, cartId);
                    deleteItemsStmt.executeUpdate();
                }
                String deleteCartSql = "DELETE FROM cart WHERE Cart_ID = ?";
                try (PreparedStatement deleteCartStmt = conn.prepareStatement(deleteCartSql)) {
                    deleteCartStmt.setInt(1, cartId);
                    deleteCartStmt.executeUpdate();
                }
            }
            cartItems.clear();

            // Commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Checkout failed: " + e.getMessage());
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }

    public double getCartTotal() {
        return cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<Product> getAllProducts() {
        loadProductsFromDatabase(); // Refresh from DB
        return new ArrayList<>(products);
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to close database connection!");
            e.printStackTrace();
        }
    }
}