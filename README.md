E-Commerce Application
Overview
The Java Swing E-Commerce App is a desktop-based shopping system designed for learning and demonstration purposes. It simulates a basic online store where users can register, browse products, add items to a cart, place orders, and make payments—all within a graphical user interface built using Java Swing. The backend is fully managed with MySQL Workbench, and data is connected using JDBC.
This project is ideal for students and beginners who want to understand how real-world e-commerce systems manage users, products, carts, orders, and payments using relational databases.tures

Product Management: Fetches product data (ID, name, price, description) from a MySQL database.
Shopping Cart: Allows adding, removing, and clearing cart items, with in-memory cart management.
Database Integration: Connects to a MySQL database (e_commerce) using JDBC.
Singleton Pattern: Uses a single DataManager instance for data access across the application.
User Support: Supports a currentUser object for potential user-specific features (e.g., personalized carts).

Project Structure

src/
DataManager.java: Singleton class that manages product and cart data, including database connectivity.
DBConnect.java: Utility class for establishing a MySQL database connection (used as a reference for DataManager).
Product.java: Represents a product with fields for productId, name, price, description, and imagePaths.
CartItem.java: Represents an item in the shopping cart with a product and quantity.
User.java: Represents a user (assumed; implementation not provided).
ShopPanel.java: UI component for displaying products (assumed; referenced in stack trace).
ECommerceApp.java: Main application class for GUI initialization (assumed; referenced in stack trace).
Main.java: Entry point for the application.



Prerequisites

Java: JDK 8 or higher.
MySQL: MySQL Server 8.0 or compatible version.
MySQL JDBC Driver: Included as a Maven dependency.
IDE: IntelliJ IDEA, Eclipse, or any Java-compatible IDE.
Maven: For dependency management (optional; can use manual JAR inclusion).

Setup Instructions
1. Clone the Repository
git clone https://github.com/MenghongLy/E_CommerceApp.git

2. Configure MySQL Database

Install MySQL Server if not already installed.
Create a database named e_commerce:CREATE DATABASE e_commerce;


Create the product table:CREATE TABLE product (
    P_ID INT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Price DOUBLE NOT NULL,
    Description TEXT
);

3. Configure Dependencies
Add the MySQL JDBC driver to your project. For Maven, include in pom.xml:
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

Alternatively, download the MySQL Connector/J JAR and add it to your project’s classpath.
4. Configure Database Credentials
The DataManager class uses the following default credentials:

URL: jdbc:mysql://localhost:3306/e_commerce
User: root
Password: menghong15

Update these in DataManager.java if your MySQL setup uses different credentials:
String url = "jdbc:mysql://localhost:3306/e_commerce";
String user = "your-username";
String password = "your-password";

For security, consider using environment variables:
String url = System.getenv("DB_URL");
String user = System.getenv("DB_USER");
String password = System.getenv("DB_PASSWORD");

5. Run the Application

Compile and run the application using your IDE or command line:mvn clean install
java -cp target/your-app.jar Main


The application initializes the DataManager singleton, connects to the MySQL database, and loads products into the ShopPanel UI.

Usage

Initialize DataManager: Access the singleton instance:DataManager dm = DataManager.getInstance();


Fetch Products: Retrieve the product list:List<Product> products = dm.getAllProducts();


Manage Cart: Add or remove items:dm.addToCart(product);
dm.removeFromCart(index);
dm.clearCart();


Close Connection: Ensure the database connection is closed on application shutdown:dm.closeConnection();

Consider adding a shutdown hook:Runtime.getRuntime().addShutdownHook(new Thread(() -> DataManager.getInstance().closeConnection()));



Image Handling
Product images are not stored in the database. Instead, the DataManager class hardcodes image paths in the getImagePathsForProduct method:
private List<String> getImagePathsForProduct(int productId) {
    return switch (productId) {
        case 1 -> List.of("image/dior_sauvage.png", "image_description/suavage.jpg", "image_description/suavaged.jpg");
        // ... other cases
        default -> new ArrayList<>();
    };
}

Ensure these paths point to valid image files in your application’s file system or web server. To use a different approach (e.g., a single default image or external storage), modify the getImagePathsForProduct method.
Notes

Database Schema: The product table has columns P_ID (int), Name (varchar), Price (double), and Description (text). No separate table is used for images.
Cart Persistence: Cart items are stored in memory. To persist cart data, add a cart table and update addToCart, removeFromCart, and clearCart methods.
Security: Avoid hardcoding database credentials in production. Use environment variables or a configuration file.
Performance: For production, consider using a connection pool (e.g., HikariCP) instead of a single Connection.
Error Handling: The current implementation uses System.out.println for errors. Replace with a logging framework (e.g., SLF4J) for better debugging.

Troubleshooting

SQLSyntaxErrorException: Ensure the product table exists with the correct columns (P_ID, Name, Price, Description). Run DESCRIBE product; in MySQL to verify.
Connection Issues:
Confirm MySQL is running on localhost:3306.
Verify the e_commerce database exists.
Check credentials (root/menghong15) are correct.


Image Issues: Ensure image paths in getImagePathsForProduct are accessible (e.g., in a resources/images/ directory or via a web server).
Empty Product List: Verify the product table is populated with SELECT * FROM product;.

Future Improvements

Add a cart table to persist cart items.
Store image paths in the database (e.g., add an ImagePaths column to product or create a product_images table).
Implement user authentication and link carts to specific users.
Use a connection pool for better database performance.
Enhance the UI in ShopPanel to display product images and details dynamically.

Contributing
Contributions are welcome! Please submit a pull request or open an issue on GitHub for bugs, features, or improvements.
License
This project is licensed under the MIT License. See the LICENSE file for details.
