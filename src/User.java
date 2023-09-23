import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    String id;
    String name;
    String email;
    String contactNumber;
    Scanner scanner;

    public User(Scanner scanner){
        this.scanner = scanner;
    }

    public User(ResultSet resultSet) throws SQLException{
        this.id = resultSet.getString("Id");
        this.name = resultSet.getString("Name");
        this.email = resultSet.getString("Email");
        this.contactNumber = resultSet.getString("ContactNumber");
    }

    public void createUserAccount() throws SQLException{

        AirlineReservationMain airLineReservationMain = new AirlineReservationMain(scanner);
        DataBaseConnection dataBaseConnection = new DataBaseConnection();

        String name;
        String email;
        String password;
        String contactNumber;

        System.out.println("\n\n\nCreate User Account");
        System.out.println("====================");
        System.out.print("Enter your name: ");
        name = scanner.nextLine();
        System.out.print("Enter your email: ");
        email = scanner.nextLine();
        System.out.print("Enter your password: ");
        password = scanner.nextLine();
        System.out.print("Enter your contact number: ");
        contactNumber = scanner.nextLine();
        System.out.println("====================");

        String insertUserQuery = "INSERT INTO Users (Name, Email, Password, ContactNumber)  VALUES ('" + name + "', '" + email + "', '" + password + "', '" + contactNumber + "')";

        try {
            int result = dataBaseConnection.executeUpdate(insertUserQuery);
            if(result == 1){
                System.out.println("User Account Created Successfully");
                airLineReservationMain.continueFun();
            }else{
                System.out.println("User Account is not created");
                airLineReservationMain.continueFun();
            }
        } catch (SQLException e) {
            System.out.println("Exception at executing query");
            System.out.println(e.getMessage());

            airLineReservationMain.continueFun();
        }

        dataBaseConnection.disconnect();
        
        return;
    }

    public User userLogin() throws SQLException{

        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        AirlineReservationMain airLineReservationMain = new AirlineReservationMain(scanner);

        User user = null;
        String email;
        String password;

        System.out.println("\n\n\nUser Login");
        System.out.println("==========");
        System.out.print("Enter your email: ");
        email = scanner.nextLine();
        System.out.print("Enter your password: ");
        password = scanner.nextLine();
        System.out.println("==========");

        String selectUserQuery = "SELECT * FROM Users WHERE Email = ? AND Password = ?";
        ResultSet resultSet = null;

        try {
            PreparedStatement preparedStatement = dataBaseConnection.preparedStatement(selectUserQuery);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User(resultSet);
                System.out.println("User Login Successful");
                airLineReservationMain.continueFun();
                return user;
            } else {
                System.out.println("Invalid email or password");
                airLineReservationMain.continueFun();
            }

        } catch (SQLException e) {
            System.out.println("Error while executing query");
            airLineReservationMain.continueFun();
        }

        
        dataBaseConnection.disconnect();
        return user;
    }

    public boolean adminLogin() throws SQLException{
        String email;
        String password;
        AirlineReservationMain airLineReservationMain = new AirlineReservationMain(scanner);
        DataBaseConnection dataBaseConnection = new DataBaseConnection();

        System.out.println("\n\n\nAdmin Login");
        System.out.println("===========");
        System.out.print("Enter your email: ");
        email = scanner.nextLine();
        System.out.print("Enter your password: ");
        password = scanner.nextLine();
        System.out.println("===========");
        String selectAdminQuery = "SELECT * FROM Admin WHERE Email = ? AND Password = ?";
        ResultSet resultSet = null;
        
        try {
            PreparedStatement preparedStatement = dataBaseConnection.preparedStatement(selectAdminQuery);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Admin Login Successful");
                airLineReservationMain.continueFun();
                return true;
            } else {
                System.out.println("Invalid email or password");
                airLineReservationMain.continueFun();
            }

        } catch (SQLException e) {
            System.out.println("Error while executing query");
            airLineReservationMain.continueFun();
        }
        
        dataBaseConnection.disconnect();
        return false;
    }
}