# 🛒 Java Swing E-Commerce Application

This is a Java-based desktop e-commerce application built using **Java Swing** for the frontend and **MySQL** for the backend. The app manages products, shopping cart functionality, and supports future features like user authentication and persistent cart storage.

---

## 📖 Overview

This application uses a **MySQL database** to store product information and implements a singleton `DataManager` class to manage data operations like fetching product details and cart management.

Product details (ID, name, price, and description) are loaded from the database, while image paths are currently hardcoded in the Java class. This structure allows for easy future integration with external storage or a database table.

---

## ✨ Features

- **Product Management**: Fetch product data from the database.
- **Shopping Cart**: Add, remove, and clear cart items (in-memory).
- **Database Integration**: Uses JDBC to connect to a MySQL `e_commerce` database.
- **Singleton Pattern**: Ensures a single instance of `DataManager` throughout the app.
- **User Support**: Includes a `currentUser` object (for future personalization support).

---

## 📁 Project Structure
src/
├── DataManager.java # Singleton for DB access and data handling
├── DBConnect.java # MySQL connection utility (optional)
├── Product.java # Product data model
├── CartItem.java # Cart item model
├── User.java # User model (assumed)
├── ShopPanel.java # UI for displaying products
├── ECommerceApp.java # GUI launcher (assumed)
└── Main.java # Entry point of the app

---

## ✅ Prerequisites

- Java 8 or higher
- MySQL Server 8.0+
- MySQL JDBC Driver (via Maven or manual JAR)
- IntelliJ IDEA / Eclipse / NetBeans
- (Optional) Maven for dependency management

---

## ⚙️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/MenghongLy/E_CommerceApp.git

🛠 Troubleshooting
Issue	Solution
SQLSyntaxErrorException	Verify that the product table exists. Use DESCRIBE product;
Connection error	Check MySQL is running and credentials are correct
No product shown	Make sure products are inserted via SELECT * FROM product;
Image not loading	Ensure image paths point to actual files on your system

🔮 Future Improvements
Add a cart table for persistent cart storage

Store image paths in DB

Add user login and personalized cart support

Use a connection pool (e.g., HikariCP)

Integrate SLF4J or Log4j for better logging

Improve GUI with product image display

🤝 Contributing
Pull requests are welcome! Please open issues for suggestions, bugs, or improvements.

📄 License
This project is licensed under the MIT License. See the LICENSE file for details.

yaml
Copy
Edit

---

Let me know if you also want:
- A separate `CONTRIBUTING.md` file
- SQL file exports of your schema and data
- `.env` support for credentials setup
- A PowerPoint/slide version for presenting this project

Just ask and I’ll help you format it!
