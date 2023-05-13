import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.sql.Date;
import java.time.temporal.ChronoUnit;

public class App {
    public static void main(String[] args) throws Exception {
        Connection connection = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("\033[38;2;255;165;0m********************");
        System.out.println("*   Hotel Orange   *");
        System.out.println("********************\033[0m");

        // Class.forName("com.mysql.cj.jdbc.Driver");
        // connection =
        // DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelbooking",
        // "root", "jeevansql@6729");
        // Statement statement = connection.createStatement();
        sqlconnect();

        while (true) {
            System.out.println("\033[37mWelcome to HOTEL ORANGE!");
            System.out.println("\033[32m1. Customer Registration");
            System.out.println("\033[36m2. Customer login");
            System.out.println("\033[33m3. Employee login");
            System.out.println("\033[34m4. Admin Login");
            System.out.println("\033[31m5. Exit");
            System.out.print("\033[38;2;255;165;0mEnter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    // Customer_registration
                    customerRegistration(connection, scanner);
                    break;
                case 2:
                    // Customer Login
                    String id = customerLogin(connection, scanner);
                    String booking[] = Customerinputs(scanner);
                    getBookingInfo(scanner, booking, id);
                    break;
                case 3:
                    // Employee Login
                    employeeLogin(connection, scanner);
                    break;
                case 4:
                    // Admin Login
                    adminLogin(connection, scanner);

                    break;

                case 5:
                    System.out.println("\033[38;2;255;105;180mTHANK YOU!!!!!!!!\033[0m");
                    return;
                default:
                    System.out.println("\u001B[31mInvalid choice!\u001B[0m");
            }
        }

    }

    private static void customerRegistration(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("\033[33mCustomer Registration");
        System.out.print("\033[37mEnter Name: ");
        String Customer_Name = scanner.nextLine();
        System.out.print("\033[37mEnter Customer_id: ");
        String Customer_id = scanner.nextLine();
        System.out.print("\033[37mEnter Phonenumber: ");
        int Customer_Phonenumber = Integer.parseInt(scanner.nextLine());
        System.out.print("\033[37mEnter password: ");
        String Password = scanner.nextLine();
        try {
            conn = sqlconnect();
            String sql = "INSERT INTO Customer(Customer_Name, Customer_id, Customer_Phonenumber, Password) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, Customer_Name);
            stmt.setString(2, Customer_id);
            stmt.setInt(3, Customer_Phonenumber);
            stmt.setString(4, Password);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("\033[34mRegistration successful!\033[0m");
            } else {
                System.out.println("\033[33mRegistration failed!\033[0m");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static String[] Customerinputs(Scanner scanner) {
        DateTimeFormatter formatter;
        System.out.print("Room Type:");
        String type = scanner.nextLine();
        System.out.print("No Of Guests: ");
        int guests = Integer.parseInt(scanner.nextLine());
        System.out.print("Check_in Date (yyyy-MM-dd): ");
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate indate = LocalDate.parse(scanner.nextLine());
        String Indate = indate.format(formatter);
        System.out.print("Check_out Date (yyyy-MM-dd): ");
        LocalDate outdate = LocalDate.parse(scanner.nextLine());
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String Outdate = outdate.format(formatter);
        String[] booking = new String[4];

        booking[0] = type;
        booking[1] = String.valueOf(guests);
        booking[2] = Indate;
        booking[3] = Outdate;
        return booking;
    }

    public static void getBookingInfo(Scanner scanner, String[] booking, String id) {
        long totalPrice;
        System.out.println("Payment Method:");
        System.out.println("1.UPI Payment");
        System.out.println("2.Cash");
        int method = Integer.parseInt(scanner.nextLine());

        if (method == 1) {
            System.out.println("Payment has been sent to Your UPI App");
            String[] loadingChars = { "|", "/", "-", "\\" };
            try {
                for (int i = 0; i < 10; i++) {
                    String loadingStr = "Loading " + loadingChars[i % loadingChars.length];
                    System.out.print(loadingStr);
                    Thread.sleep(500);
                    for (int j = 0; j < loadingStr.length(); j++) {
                        System.out.print("\b");
                    }
                }
                System.out.println("Payment Done");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(" You need come to hotel!!!");
        }
        // booking[0] = type;
        // booking[1] = String.valueOf(guests);
        // booking[2] = Indate;
        // booking[3] = Outdate;
        totalPrice = calculatePrice(booking[0], booking[1], booking[2], booking[3]);
        try {
            Connection connection = sqlconnect();
            Statement statement = connection.createStatement();
            statement.execute("Insert into BookIng_details values('" + id + "','" + booking[2] + "','"
                    + booking[3] + "','"
                    + booking[1] + "','" + booking[0] + "','" + totalPrice + "');");
            statement.close();

            System.out.printf("|%-10s| %-21s| %-21s| %-21s| %-21s| %-21s|\n", "Customer_id", "Type", "Guests",
                    "Check_in", "Check_out", "Room_Price");
            System.out.println("");
            System.out.printf("|%-11s| %-21s| %-21s| %-21s| %-21s| %-21s|\n", id, booking[2], booking[3],
                    booking[1], booking[0], totalPrice);

        } catch (Exception e) {
            System.out.println(e);
        }
        Availabilitycheck(booking[0], booking[1]);

    }

    public static long calculatePrice(String type, String guests, String Indate, String Outdate) {

        ResultSet resultSet;
        long TotalPrice = 0;
        LocalDate startDate = LocalDate.parse(Indate);
        LocalDate endDate = LocalDate.parse(Outdate);

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        // int a = Integer.parseInt(outdate[2]) - Integer.parseInt(indate[2]);
        // System.out.println("No Of Days:" + a);
        try {
            Connection connection = sqlconnect();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT Room_price FROM Rooms WHERE Room_type = '" + type + "' AND Room_sharing = '" + guests
                            + "'");

            if (resultSet.next()) {
                int Priceperday = resultSet.getInt("Room_price");
                TotalPrice = Priceperday * daysBetween;

                System.out.println("Total price for the booking: " + TotalPrice);
            } else {
                System.out.println("Room not found or insufficient capacity.");
            }
            resultSet.close();
            return TotalPrice;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void Availabilitycheck(String type, String guests) {

        try {
            Connection connection = sqlconnect();
            Statement statement = connection.createStatement();
            ResultSet resultSet2 = statement.executeQuery("SELECT Availability FROM Rooms WHERE Room_type = '"
                    + type + "' AND Room_sharing = '" + guests + "'");

            if (resultSet2.next()) {
                int c = resultSet2.getInt("Availability");
                int availablity = c - 1;
                statement = connection.createStatement();
                statement.execute("update Rooms set Availability = '" + availablity + "'where Room_type = '" + type
                        + "' and Room_sharing = '" + guests + "'");
                if (availablity <= 3) {
                    System.out.println("Room Booked");
                } else {
                    System.out.println("Rooms not available");
                }
            }

            resultSet2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static String customerLogin(Connection conn, Scanner scanner) throws SQLException, ParseException {

        System.out.println("Customer login");
        System.out.print("Enter Customer_id: ");
        String Customer_id = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        try {
            conn = sqlconnect();
            String sql = "SELECT * FROM Customer WHERE Customer_id='" + Customer_id + "' AND Password='" + password
                    + "'";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            // System.out.println(resultSet);
            if (resultSet.next()) {
                System.out.println("\033[31mLogin successful!!!");
            } else {
                System.out.println("Login Failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Customer_id;
    }

    private static Connection sqlconnect() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelbookingapp", "root",
                    "jeevansql@6729");
            return connection;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private static void employeeLogin(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("\u001B[35mEmployee login");
        System.out.print("\u001B[33mEnter Employee_id: ");
        String Employee_id = scanner.nextLine();
        System.out.print("\u001B[36mEnter password: ");
        String password = scanner.nextLine();
        try {
            conn = sqlconnect();
            String sql = "SELECT * FROM Employee WHERE Employee_id ='" + Employee_id + "'AND Password ='" + password
                    + "'";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println(resultSet);
            if (resultSet.next()) {
                System.out.println("\u001B[32mLogin successful!!!");
            } else {
                System.out.println("\u001B[31mInvalid Id or password");
            }
        } catch (Exception e) {
            System.out.println(e);
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
        try {
            conn = sqlconnect();
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
                                statement.execute("Delete from Employee where Employee_Name ='" + Name + "'");
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
                                statement.execute("Delete from Employee where Employee_id ='" + Employee_id + "'");

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
            } else {
                System.out.println("Wrong Password");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}