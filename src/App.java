import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import com.mysql.*;
import java.sql.Date;

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
                        System.out.println("THANK YOU!!!!!!!!");
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
            System.out.println("Login sucessful!!!");
            System.out.print("Enter check in date:");
            String Check_in = scanner.nextLine();
            System.out.print("Enter check out date:");
            String Check_out = scanner.nextLine();
            System.out.print("Enter number of guests: ");
            int No_of_Guests = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter type of room: ");
            String Room_type = scanner.nextLine();
            int room_Price = 0;
            String Price = "SELECT price FROM Rooms WHERE Room_type = ? AND Room_sharing = ?";
            PreparedStatement pstmt = conn.prepareStatement(Price);
            pstmt.setString(1, Room_type);
            pstmt.setInt(2, No_of_Guests);
            ResultSet rs = pstmt.executeQuery();

            

            if (rs.next()) {
                room_Price =rs.getInt("price");
                System.out.println(
                        "Price for " + Room_type + " room with " + No_of_Guests + " sharing is: " + room_Price);
            } else {
                System.out.println("No rooms found");
            }
            java.util.Date utilDate1 = dateFormat.parse(Check_in);
            Date checkindate = new Date(utilDate1.getTime());
            java.util.Date utilDate2 = dateFormat.parse(Check_out);
            Date checkoutdate = new Date(utilDate2.getTime());
            System.out.printf("|%-10s| %-21s| %-21s| %-21s| %-21s| %-21s|\n", "Customer_id", "Check_in", "Check_out","No_of_Guests","Room_type","room_Price");
            System.out.println("");
            System.out.printf("|%-11s| %-21s| %-21s| %-21s| %-21s| %-21s|\n",Customer_id,Check_in,Check_out,No_of_Guests,Room_type,room_Price);

            String query = "INSERT INTO Booking_Details(Customer_id, Check_in, Check_out, No_of_Guests, Room_type, Price) VALUES ('"
            +Customer_id+ "','" + checkindate + "','" +checkoutdate + "','" + No_of_Guests+ "','"+Room_type+"','"+room_Price+"')";
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
        System.out.println("Employee login");
        System.out.print("Enter Employee_id: ");
        String Employee_id = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM Employee WHERE Customer_id ='" + Employee_id + "'AND password ='" + password + "'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println(resultSet);
        if (resultSet.next()) {
            System.out.println("Login sucessful!!!"); 
    }
        else {
            System.out.println("Invalid Id or password");
          }
    }


    private static void adminLogin(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Admin login");
        System.out.print("Enter Admin_id: ");
        String Admin_id = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        String sql = "SELECT * FROM Employee WHERE Customer_id ='" + Admin_id + "'AND password ='" + password + "'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println(resultSet);
        if (resultSet.next()) {
            System.out.println("Login sucessful!!!");
            System.out.println("1.Admin access");
            System.out.println("2. Register employee");
            int option = scanner.nextInt();
            switch(option){
                case 1: 
                System.out.println("1.Employee access");
                System.out.println("2.Customer access");
                System.out.println("choose:");
                int choose = scanner.nextInt();
                
                if (choose==1){
                    String emp = "SELECT * FROM Employee";
                    PreparedStatement pstmt = conn.prepareStatement(emp);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        
                        int Emp_id = rs.getInt("Employee_id");
                        String  Employee_Name= rs.getString(" Employee_Name");
                        int Employee_phonenumber = rs.getInt("Employee_phonenumber");
                        String  Employee_Mail = rs.getString(" Employee_Mail");
                        
                    }
                    rs.close();
                    pstmt.close();
                    conn.close();

            }
            else if (choose==2){
                String emp = "SELECT * FROM Customer";
                PreparedStatement pstmt = conn.prepareStatement(emp);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    
                    int Customer_id = rs.getInt("Customer_id");
                    String Customer_Name= rs.getString("Customer_Name");
                    int Customer_phonenumber = rs.getInt("Customer_phonenumber");
                    String  Employee_Mail = rs.getString(" Employee_Mail");
                    String  Password = rs.getString(" Password");
                }
                rs.close();
                pstmt.close();
                conn.close();
            }

            

        }

        } else {
            System.out.println("Invalid Id or password");
        }

    }
}