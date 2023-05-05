import java.sql.*;
import java.util.Scanner;

import com.mysql.*;

public class ex {
    

    // Database connection parameters
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/hotelbookingapp";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "password";

    public static void main(String[] args) {
        Connection conn = null;
        Scanner scanner = new Scanner(System.in);
        try {
            // Establish database connection
            conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
            while (true) {
                System.out.println("Welcome to Hotel Booking App!");
                System.out.println("1. Customer login");
                System.out.println("2. Employee login");
                System.out.println("3. Admin login");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        customerLogin(conn, scanner);
                        break;
                    case 2:
                        employeeLogin(conn, scanner);
                        break;
                    case 3:
                        adminLogin(conn, scanner);
                        break;
                    case 4:
                        System.out.println("Thank you for using Hotel Booking App!");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void customerLogin(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Customer Login");
        System.out.print("Enter Name: ");
        String username = scanner.nextLine();
        System.out.print("Enter User_id: ");
        String user_id = scanner.nextLine();
        System.out.print("Enter Phonenumber: ");
        int phonenumber = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM Customers WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            // Successful login
            int customerId = rs.getInt("customer_id");
            String customerName = rs.getString("customer_name");
            System.out.println("Welcome, " + customerName + "!");
            while (true) {
                System.out.println("1. View available rooms");
                System.out.println("2. Book a room");
                System.out.println("3. View my bookings");
                System.out.println("4. Logout");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        viewAvailableRooms(conn);
                        break;
                    case 2:
                        bookRoom(conn, scanner, customerId);
                        break;
                    case 3:
                        viewMyBookings(conn, customerId);
                        break;
                    case 4:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } else {
            // Failed login
            System.out.println("Invalid username or password!");
        }
    }

    private static void employeeLogin(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Employee Login");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM employees WHERE username = ? AND password = ?";
       
    
}
}
