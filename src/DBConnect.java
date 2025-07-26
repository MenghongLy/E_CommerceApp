import java.sql.*;

public class DBConnect {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/e_commerce";
        String user = "root";
        String password = "Password";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");

            conn.close();
        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
