import java.sql.*;
import java.util.Scanner;

import com.mysql.*;

public class App {
    public static void main(String[] args) throws Exception {
        Connection connection = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("********************");
        System.out.println("*   Hotel Orange   *");
        System.out.println("********************");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelbookingapp", "root", "12345");
            Statement statement = connection.createStatement();

            while (true) {
                System.out.println("Welcome to Hotel Booking App!");
                System.out.println("1. Customer login");
                System.out.println("2. Employee login");
                System.out.println("3. Admin login");
                System.out.println("4. Customer registration");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        customerLogin(connection, scanner);
                        break;
                    case 2:
                        employeeLogin(connection, scanner);
                        break;
                    case 3:
                        adminLogin(connection, scanner);
                        break;
                    case 4:
                        customerRegistration(connection, scanner);
                        break;

                    case 5:
                        System.out.println("Thank you for using Hotel Booking App!");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void customerRegistration(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Customer Registration");
        System.out.print("Enter Name: ");
        String Customer_Name = scanner.nextLine();
        System.out.print("Enter Customer_id: ");
        String Customer_id = scanner.nextLine();
        System.out.print("Enter Phonenumber: ");
        int Customer_Phonenumber = scanner.nextInt();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "INSERT INTO Customer (Customer_Name,Customer_id,Customer_Phonenumber,password ) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, Customer_Name);
        stmt.setString(2, Customer_id);
        stmt.setInt(3, Customer_Phonenumber);
        stmt.setString(4, password);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed!");
        }
    }

    private static void customerLogin(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Customer login");
        System.out.print("Enter Customer_id: ");
        String Customer_id = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM Customer WHERE Customer_id='"+Customer_id+"' AND Password='"+password+"'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println(resultSet);
        if (resultSet.next()) {
            System.out.println("Login sucessful!!!");
            System.out.print("Enter check in date:");
            String Check_in = scanner.nextLine();
            System.out.print("Enter check out date:");
            String Check_out = scanner.nextLine();
            System.out.print("Enter number of guests: ");
            int No_of_Guests = scanner.nextInt();

            PreparedStatement stmt= conn.prepareStatement(
                "SELECT Rooms_id, Rooms_status, Room_type, Room_sharing, 'price' " +
                "FROM Rooms " +
                "WHERE Rooms_status = true AND Room_sharing >= ? " +
                "AND NOT EXISTS (" +
                    "SELECT * FROM Booking_Details " +
                    "WHERE Booking_Details.No_of_Guests = Rooms.Room_sharing " +
                    "AND ((Check_in <= ? AND Check_out >= ?) " +
                    "OR (Check_in <= ? AND Check_out >= ?) " +
                    "OR (Check_in >= ? AND Check_out <= ?))" +
                ") "  +
                "ORDER BY price ASC " +
                "LIMIT 1"
            );
            stmt.setInt(1, No_of_Guests);
            stmt.setString(2, Check_in);
            stmt.setString(3, Check_in);
            stmt.setString(4, Check_out);
            stmt.setString(5, Check_out);
            stmt.setString(6, Check_in);
            stmt.setString(7, Check_in);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int roomId = rs.getInt("room_id");
                double pricePerNight = rs.getDouble("price_per_night");
                double totalPrice = pricePerNight * No_of_Guests * getNumNights(Check_in, Check_out);

                PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO bookings (Room_id, Check_in, Check_out, No_of_Gests, total_price) " +
                    "VALUES (?, ?, ?, ?, ?)"
                );
                insertStmt.setInt(1, roomId);
                insertStmt.setString(2, Check_in);
                insertStmt.setString(3, Check_out);
                insertStmt.setInt(4, No_of_Guests);
                insertStmt.setDouble(5, totalPrice);
                insertStmt.executeUpdate();

                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE rooms " +
                    "SET available = false " +
                    "WHERE room_id = ?"
                );
                updateStmt.setInt(1, roomId);
                updateStmt.executeUpdate();

                System.out.println("Room " + rs.getString("room_number") + " booked for " + No_of_Guests + " guests " +
                    "from " + Check_in + " to " + Check_out + " for a total price of $" + totalPrice);
            }
            
        } else {
            System.out.println("Invalid Id or password");
        }
    }

    private static double getNumNights(String check_in, String check_out) {
        return 0;
    }

    private static void employeeLogin(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Employee login");
        System.out.print("Enter Employee_id: ");
        String Employee_id = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM Employee WHERE Customer_id ='"+ Employee_id +"'AND password ='"+ password+"'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println(resultSet);
        if (resultSet.next()) {
            System.out.println("Login sucessful!!!");
            


        } else {
            System.out.println("Invalid Id or password");
        }
    }

    private static void adminLogin(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Admin login");
        System.out.print("Enter Admin_id: ");
        String Admin_id = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM Employee WHERE Customer_id ='"+Admin_id+"'AND password ='"+password+"'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println(resultSet);
        if (resultSet.next()) {
            System.out.println("Login sucessful!!!");
        } else {
            System.out.println("Invalid Id or password");
        }

    }
}