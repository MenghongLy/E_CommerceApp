# E-Commerce Application

## Overview
This is a Java-based e-commerce application designed to manage products and shopping cart functionality. The application uses a MySQL database to store product information and implements a singleton `DataManager` class to handle data operations. The project supports a product catalog with details such as ID, name, price, and description, fetched from a MySQL database. Image paths for products are currently hardcoded in the `DataManager` class, but the structure allows for future integration with a database or external storage.

## Features
- **Product Management**: Fetches product data (ID, name, price, description) from a MySQL database.
- **Shopping Cart**: Allows adding, removing, and clearing cart items, with in-memory cart management.
- **Database Integration**: Connects to a MySQL database (`e_commerce`) using JDBC.
- **Singleton Pattern**: Uses a single `DataManager` instance for data access across the application.
- **User Support**: Supports a `currentUser` object for potential user-specific features (e.g., personalized carts).

## Project Structure
- **src/**
  - `DataManager.java`: Singleton class that manages product and cart data, including database connectivity.
  - `DBConnect.java`: Utility class for establishing a MySQL database connection (used as a reference for `DataManager`).
  - `Product.java`: Represents a product with fields for `productId`, `name`, `price`, `description`, and `imagePaths`.
  - `CartItem.java`: Represents an item in the shopping cart with a product and quantity.
  - `User.java`: Represents a user (assumed; implementation not provided).
  - `ShopPanel.java`: UI component for displaying products (assumed; referenced in stack trace).
  - `ECommerceApp.java`: Main application class for GUI initialization (assumed; referenced in stack trace).
  - `Main.java`: Entry point for the application.

## Prerequisites
- **Java**: JDK 8 or higher.
- **MySQL**: MySQL Server 8.0 or compatible version.
- **MySQL JDBC Driver**: Included as a Maven dependency.
- **IDE**: IntelliJ IDEA, Eclipse, or any Java-compatible IDE.
- **Maven**: For dependency management (optional; can use manual JAR inclusion).

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/MenghongLy/E_CommerceApp.git
```

### 2. Configure MySQL Database
1. Install MySQL Server if not already installed.
2. Create a database named `e_commerce`:
   ```sql
   CREATE DATABASE e_commerce;
   ```
3. Create the `product` table:
   ```sql
   CREATE TABLE product (
       P_ID INT PRIMARY KEY,
       Name VARCHAR(100) NOT NULL,
       Price DOUBLE NOT NULL,
       Description TEXT
   );
   ```
4. Populate the `product` table with sample data:
   ```sql
   INSERT INTO product (P_ID, Name, Price, Description) VALUES
   (1, 'Dior Sauvage', 99.99, 'Dior Sauvage is a bold and masculine fragrance that opens with a burst of fresh bergamot and pepper, leading into a heart of spicy Sichuan pepper and lavender. It''s perfect for the modern man who wants to make a statement.'),
   (2, 'Chanel No.5', 129.99, 'Chanel No.5 is a timeless classic that has captivated women for generations. Its blend of floral aldehydes, jasmine, and rose creates a sophisticated and elegant scent that''s perfect for any occasion.'),
   (3, 'YSL Libre', 85.50, 'YSL Libre is a daring and modern fragrance that combines the unexpected notes of lavender and vanilla. It''s a scent for the free-spirited woman who isn''t afraid to stand out.'),
   (4, 'Versace Eros', 78.99, 'Versace Eros is a vibrant and energetic fragrance that opens with a refreshing burst of mint and green apple, settling into a warm and sensual base of vanilla and cedarwood. It''s ideal for the confident man who wants to exude charisma.'),
   (5, 'Gucci Bloom', 95.00, 'Gucci Bloom is a lush and romantic fragrance that captures the essence of a blooming garden. With notes of tuberose, jasmine, and Rangoon creeper, it''s a feminine and sophisticated scent that''s perfect for special occasions.'),
   (6, 'Armani Code', 88.99, 'Armani Code is a rich and seductive fragrance that wraps you in a warm embrace of wood, tonka bean, and leather. It''s a scent for the sophisticated man who wants to leave a lasting impression.'),
   (7, 'Tom Ford Noir', 120.00, 'Tom Ford Noir is a dark and mysterious fragrance that''s perfect for evening wear. With notes of black pepper, nutmeg, and patchouli, it''s a scent that exudes luxury and intrigue.'),
   (8, 'Prada Luna Rossa', 89.90, 'Prada Luna Rossa is a crisp and invigorating fragrance that combines the freshness of citrus with the calming scent of lavender. It''s a perfect choice for the active man who wants to stay fresh throughout the day.'),
   (9, 'Hermès Terre', 110.00, 'Hermès Terre is a grounded and earthy fragrance that blends the zest of citrus with the depth of vetiver and cedarwood. It''s a scent for the man who appreciates the beauty of nature.'),
   (10, 'Jean Paul Le Male', 92.50, 'Jean Paul Le Male is an iconic fragrance that daringly combines the coolness of mint with the warmth of vanilla. It''s a scent that''s both refreshing and comforting, perfect for the man who likes to keep things interesting.');
   ```

### 3. Configure Dependencies
Add the MySQL JDBC driver to your project. For Maven, include in `pom.xml`:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

Alternatively, download the MySQL Connector/J JAR and add it to your project’s classpath.

### 4. Configure Database Credentials
The `DataManager` class uses the following default credentials:
- **URL**: `jdbc:mysql://localhost:3306/e_commerce`
- **User**: `root`
- **Password**: `password`

Update these in `DataManager.java` if your MySQL setup uses different credentials:
```java
String url = "jdbc:mysql://localhost:3306/e_commerce";
String user = "your-username";
String password = "your-password";
```

For security, consider using environment variables:
```java
String url = System.getenv("DB_URL");
String user = System.getenv("DB_USER");
String password = System.getenv("DB_PASSWORD");
```

### 5. Run the Application
1. Compile and run the application using your IDE or command line:
   ```bash
   mvn clean install
   java -cp target/your-app.jar Main
   ```
2. The application initializes the `DataManager` singleton, connects to the MySQL database, and loads products into the `ShopPanel` UI.

## Usage
- **Initialize DataManager**: Access the singleton instance:
  ```java
  DataManager dm = DataManager.getInstance();
  ```
- **Fetch Products**: Retrieve the product list:
  ```java
  List<Product> products = dm.getAllProducts();
  ```
- **Manage Cart**: Add or remove items:
  ```java
  dm.addToCart(product);
  dm.removeFromCart(index);
  dm.clearCart();
  ```
- **Close Connection**: Ensure the database connection is closed on application shutdown:
  ```java
  dm.closeConnection();
  ```
  Consider adding a shutdown hook:
  ```java
  Runtime.getRuntime().addShutdownHook(new Thread(() -> DataManager.getInstance().closeConnection()));
  ```

## Image Handling
Product images are not stored in the database. Instead, the `DataManager` class hardcodes image paths in the `getImagePathsForProduct` method:
```java
private List<String> getImagePathsForProduct(int productId) {
    return switch (productId) {
        case 1 -> List.of("image/dior_sauvage.png", "image_description/suavage.jpg", "image_description/suavaged.jpg");
        // ... other cases
        default -> new ArrayList<>();
    };
}
```

Ensure these paths point to valid image files in your application’s file system or web server. To use a different approach (e.g., a single default image or external storage), modify the `getImagePathsForProduct` method.

## Notes
- **Database Schema**: The `product` table has columns `P_ID` (int), `Name` (varchar), `Price` (double), and `Description` (text). No separate table is used for images.
- **Cart Persistence**: Cart items are stored in memory. To persist cart data, add a `cart` table and update `addToCart`, `removeFromCart`, and `clearCart` methods.
- **Security**: Avoid hardcoding database credentials in production. Use environment variables or a configuration file.
- **Performance**: For production, consider using a connection pool (e.g., HikariCP) instead of a single `Connection`.
- **Error Handling**: The current implementation uses `System.out.println` for errors. Replace with a logging framework (e.g., SLF4J) for better debugging.

## Troubleshooting
- **SQLSyntaxErrorException**: Ensure the `product` table exists with the correct columns (`P_ID`, `Name`, `Price`, `Description`). Run `DESCRIBE product;` in MySQL to verify.
- **Connection Issues**:
  - Confirm MySQL is running on `localhost:3306`.
  - Verify the `e_commerce` database exists.
  - Check credentials (`root`/`password`) are correct.
- **Image Issues**: Ensure image paths in `getImagePathsForProduct` are accessible (e.g., in a `resources/images/` directory or via a web server).
- **Empty Product List**: Verify the `product` table is populated with `SELECT * FROM product;`.

## Future Improvements
- Add a `cart` table to persist cart items.
- Store image paths in the database (e.g., add an `ImagePaths` column to `product` or create a `product_images` table).
- Implement user authentication and link carts to specific users.
- Use a connection pool for better database performance.
- Enhance the UI in `ShopPanel` to display product images and details dynamically.

## Contributing
Contributions are welcome! Please submit a pull request or open an issue on GitHub for bugs, features, or improvements.

## License
This project is licensed under the ITE License.
