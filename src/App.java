import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import com.mysql.*;
import java.sql.Date;

public class App {
    public static void main(String[] args) throws Exception {
        Connection connection = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("\033[38;2;255;165;0m********************");
        System.out.println("*   Hotel Orange   *");
        System.out.println("********************\033[0m");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelbookingapp", "root", "12345");
            Statement statement = connection.createStatement();

            while (true) {
                System.out.println("\033[37mWelcome to HOTEL ORANGE!");
                System.out.println("\033[32m1. Customer login");
                System.out.println("\033[36m2. Employee login");
                System.out.println("\033[33m3. Admin login");
                System.out.println("\033[34m4. Customer registration");
                System.out.println("\033[31m5. Exit");
                System.out.print("\033[38;2;255;165;0mEnter your choice: ");
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
                        System.out.println("\033[38;2;255;105;180mTHANK YOU!!!!!!!!\033[0m");
                        return;
                    default:
                        System.out.println("\u001B[31mInvalid choice!\u001B[0m");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void customerRegistration(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("\033[33mCustomer Registration");
        System.out.print("\033[37mEnter Name: ");
        String Customer_Name = scanner.nextLine();
        System.out.print("\033[37mEnter Customer_id: ");
        String Customer_id = scanner.nextLine();
        System.out.print("\033[37mEnter Phonenumber: ");
        int Customer_Phonenumber = scanner.nextInt();
        System.out.print("\033[37mEnter password: ");
        String password = scanner.nextLine();
        String sql = "INSERT INTO Customer (Customer_Name,Customer_id,Customer_Phonenumber,password ) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, Customer_Name);
        stmt.setString(2, Customer_id);
        stmt.setInt(3, Customer_Phonenumber);
        stmt.setString(4, password);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("\033[34mRegistration successful!\033[0m");
        } else {
            System.out.println("\033[33mRegistration failed!\033[0m");
        }
    }

    private static void customerLogin(Connection conn, Scanner scanner) throws SQLException, ParseException {
        Date DateOfJoining;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Customer login");
        System.out.print("Enter Customer_id: ");
        String Customer_id = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM Customer WHERE Customer_id='" + Customer_id + "' AND Password='" + password + "'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println(resultSet);
        if (resultSet.next()) {
            System.out.println("\033[31mLogin successful!!!");
            System.out.println("\033[91mEnter check-in date:");
            String Check_in = scanner.nextLine();
            System.out.println("\033[33mEnter check-out date:");
            String Check_out = scanner.nextLine();
            System.out.println("\033[32mEnter number of guests:");
            int No_of_Guests = Integer.parseInt(scanner.nextLine());
            System.out.println("\033[36mEnter type of room:");
            String Room_type = scanner.nextLine();
            int room_Price = 0;
            String Price = "SELECT price FROM Rooms WHERE Room_type = ? AND Room_sharing = ?";
            PreparedStatement pstmt = conn.prepareStatement(Price);
            pstmt.setString(1, Room_type);
            pstmt.setInt(2, No_of_Guests);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                room_Price = rs.getInt("price");
                System.out.println(
                        "Price for " + Room_type + " room with " + No_of_Guests + " sharing is: " + room_Price);
            } else {
                System.out.println("\u001B[31mNo rooms found");
            }
            java.util.Date utilDate1 = dateFormat.parse(Check_in);
            Date checkindate = new Date(utilDate1.getTime());
            java.util.Date utilDate2 = dateFormat.parse(Check_out);
            Date checkoutdate = new Date(utilDate2.getTime());
            System.out.printf("|%-10s| %-21s| %-21s| %-21s| %-21s| %-21s|\n", "Customer_id", "Check_in", "Check_out",
                    "No_of_Guests", "Room_type", "room_Price");
            System.out.println("");
            System.out.printf("|%-11s| %-21s| %-21s| %-21s| %-21s| %-21s|\n", Customer_id, Check_in, Check_out,
                    No_of_Guests, Room_type, room_Price);

            String query = "INSERT INTO Booking_Details(Customer_id, Check_in, Check_out, No_of_Guests, Room_type, Price) VALUES ('"
                    + Customer_id + "','" + checkindate + "','" + checkoutdate + "','" + No_of_Guests + "','"
                    + Room_type + "','" + room_Price + "')";
            try {
                Statement stmt = conn.createStatement();
                stmt.execute(query);
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    private static Connection sqlconnect() {
        return null;
    }

    private static void employeeLogin(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("\u001B[35mEmployee login");
        System.out.print("\u001B[33mEnter Employee_id: ");
        String Employee_id = scanner.nextLine();
        System.out.print("\u001B[36mEnter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM Employee WHERE Employee_id ='" + Employee_id + "'AND password ='" + password + "'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println(resultSet);
        if (resultSet.next()) {
            System.out.println("\u001B[32mLogin successful!!!");
        } else {
            System.out.println("\u001B[31mInvalid Id or password");
        }
    }

    private static void adminLogin(Connection conn, Scanner scanner) throws SQLException {
        PreparedStatement pstmt;
        ResultSet rs;
        System.out.println("\u001B[35mAdmin login");
        System.out.print("\u001B[33mEnter Admin_id: ");
        String Admin_id = scanner.nextLine();
        System.out.print("\u001B[36mEnter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM Admin WHERE Admin_id ='" + Admin_id + "'AND password ='" + password + "'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println(resultSet);
        if (resultSet.next()) {
            System.out.println("\u001B[32mLogin successful!!!");
            System.out.println("\u001B[36mAdmin Access");
            System.out.println("");
            System.out.println("\u001B[33m1. Employee access");
            System.out.println("\u001B[36m2. Customer Booking Details");
            int option = Integer.parseInt(scanner.nextLine());
            if (option == 1) {
                System.out.println("\u001B[36ma. Employee Details");
                System.out.println("\u001B[36mb. Register employee");
                System.out.println("\u001B[35mc. Remove employee");
                System.out.println("\u001B[37m Choice: ");
                String choice = scanner.nextLine();
                System.out.println("");
                try {
                    if (choice.equals("a")) {
                        statement = conn.createStatement();
                        resultSet = statement.executeQuery("SELECT * FROM Employee");
                        while (resultSet.next()) {
                            System.out.println(resultSet.getInt(1) + "\t" + resultSet.getString(2) + "\t"
                                    + resultSet.getInt(3) + "\t" + resultSet.getString(4) + "\t"
                                    + resultSet.getString(5)
                                    + "\t" + resultSet.getInt(6) + "\t" + resultSet.getDate(7).toLocalDate());
                        }

                    } else if (choice.equals("b")) {
                        System.out.println("\u001B[36mRegistering Employee");
                        System.out.println(" ");
                        System.out.print("\033[37mEnter Employee_id: ");
                        int Employee_id = Integer.parseInt(scanner.nextLine());
                        System.out.print("\033[37mEnter Name: ");
                        String Employee_Name = scanner.nextLine();
                        System.out.print("\033[37mEnter Phonenumber: ");
                        int Employee_Phonenumber = Integer.parseInt(scanner.nextLine());
                        System.out.println("\033[37mEnter Mail_id:");
                        String Employee_Mail = scanner.nextLine();
                        System.out.print("\033[37mEnter password: ");
                        String Password = scanner.nextLine();
                        System.out.println("\033[37mSalary:");
                        int Salary = Integer.parseInt(scanner.nextLine());
                        System.out.println("\033[37mDate of Joining:");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String Date_of_joining = scanner.nextLine();

                        String query = "INSERT INTO Employee(Employee_id, Employee_Name, Employee_Phonenumber, Employee_Mail, Password, Salary, Date_of_joining) VALUES ('"
                                + Employee_id + "','" + Employee_Name + "','" + Employee_Phonenumber + "','"
                                + Employee_Mail
                                + "','"
                                + Password + "','" + Salary + "','" + Date_of_joining + "')";

                        System.out.printf("|%-10s| %-21s| %-21s| %-21s| %-21s| %-21s| %-21s|\n", "Employee_id",
                                "Employee_Name", "Employee_Phonenumber",
                                "Employee_Mail", "Password", "Salary", "Joining_date");
                        System.out.println("");
                        System.out.printf("|%-11s| %-21s| %-21s| %-21s| %-21s| %-21s| %-21s|\n", Employee_id,
                                Employee_Name,
                                Employee_Phonenumber,
                                Employee_Mail, Password, Salary, Date_of_joining);

                        try {
                            Statement stmt = conn.createStatement();
                            stmt.execute(query);
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                    } else if (choice.equals("c")) {
                        System.out.println("Removing Employee");
                        System.out.println("");
                        System.out.println("Choose from the Table");
                        System.out.println("");
                        statement = conn.createStatement();
                        resultSet = statement.executeQuery("SELECT * FROM Employee");
                        while (resultSet.next()) {
                            System.out.println(resultSet.getInt(1) + "\t" + resultSet.getString(2) + "\t"
                                    + resultSet.getInt(3) + "\t" + resultSet.getString(4) + "\t"
                                    + resultSet.getString(5)
                                    + "\t" + resultSet.getInt(6) + "\t" + resultSet.getDate(7).toLocalDate());
                        }
                        System.out.println("");
                        System.out.println("Employee_id");
                        System.out.println("EMployee_Name");
                        System.out.println("");
                        System.out.print("choice:");
                        choice = scanner.nextLine();
                        if (choice.equals("Employee_Name")) {
                            System.out.print("Enter Name:");
                            String Name = scanner.nextLine();

                            statement = conn.createStatement();
                            statement.execute("Delete from Employee where Employee_Name ='"+Name+"'");
                            while (resultSet.next()) {
                                System.out.println(resultSet.getInt(1) + "\t" + resultSet.getString(2) + "\t"
                                        + resultSet.getInt(3) + "\t" + resultSet.getString(4) + "\t"
                                        + resultSet.getString(5)
                                        + "\t" + resultSet.getInt(6) + "\t" + resultSet.getDate(7).toLocalDate());
                            }
                        } else if (choice.equals("Employee_id")) {
                            System.out.print("Enter Employee_id:");
                            int Employee_id = Integer.parseInt(scanner.nextLine());
                            statement = conn.createStatement();
                            statement.execute("Delete from Employee where Employee_id ='"+Employee_id+"'");
                            
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                    // Handle the exception or provide feedback to the user
                }
            } else if (option == 2) {
                String Room_type;
                String BD = "SELECT * FROM Booking_Details";
                pstmt = conn.prepareStatement(BD);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String Customer_id = rs.getString("Customer_id");
                    Date Check_in = rs.getDate("Check_in");
                    Date Check_out = rs.getDate("Check_out");
                    int guests = rs.getInt("No_of_guests");
                    Room_type = rs.getString("Room_type");
                    int Price_per_day = rs.getInt("Price_per_day");
                    System.out.println("");
                    System.out.println("\u001B[35mBooking Details");
                    System.out.println("");
                    System.out.printf("\u001B[33m|%-10s| %-21s| %-21s| %-21s| %-21s| %-21s|\n", "Customer_id",
                            "Check_in",
                            "Check_out",
                            "No_of_Guests", "Room_type", "Room_Price");

                    System.out.println("");

                    System.out.printf("\u001B[33m|%-11s| %-21s| %-21s| %-21s| %-21s| %-21s|\n", Customer_id,
                            Check_in,
                            Check_out,
                            guests, Room_type, Price_per_day);
                }
                String Room = "Select * from Rooms;";
                pstmt = conn.prepareStatement(Room);
                rs = pstmt.executeQuery();
                while (rs.next()) {

                    int Rooms_id = rs.getInt("Rooms_id");
                    Room_type = rs.getString("Room_type");
                    int Room_sharing = rs.getInt("Room_sharing");
                    int Availability = rs.getInt("Availability");
                    System.out.println("");
                    System.out.println("\u001B[35mRoom Details");
                    System.out.println("");
                    System.out.printf("\u001B[34m|%-10s| %-21s| %-21s| %-21s|\n", "Room_id", "Room_type",
                            "Room_sharing",
                            "Availability");

                    System.out.println("");
                    System.out.printf("\u001B[34m|%-11s| %-21s| %-21s| %-21s|\n", Rooms_id, Room_type,
                            Room_sharing,
                            Availability);
                    break;
                }
                rs.close();
                pstmt.close();
                conn.close();
            } else {
                System.out.println("Invalid Id or password");
            }
        }
    }
}