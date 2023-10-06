import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class AirlineReservationMain {
    DataBaseConnection dataBaseConnection;
    Scanner scanner;

    public AirlineReservationMain(Scanner scanner){
        this.scanner = scanner;
    }
    //DRY - Don't Repeat Yourself
    public void continueFun(){
        System.out.println("Enter any key to continue:");
        scanner.nextLine();
    }

    public void createAdminEntry() throws SQLException{

        String checkAdminEntryQuery = "SELECT COUNT(*) FROM Admin";

        Statement statement = this.dataBaseConnection.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(checkAdminEntryQuery);
        int adminCountResult = 0;
        if(resultSet.next()){
            adminCountResult = resultSet.getInt(1);
        }
        statement.close();

        try {
            if (adminCountResult == 0) {
                String insertAdminQuery = "INSERT INTO Admin (Name, Email, Password) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = this.dataBaseConnection.preparedStatement(insertAdminQuery);
                preparedStatement.setString(1, "Admin");
                preparedStatement.setString(2, "admin@xyz.com");
                preparedStatement.setString(3, "admin");
                preparedStatement.executeUpdate();
                preparedStatement.close();
                System.out.println("Admin entry added successfully.");
            } else {
                System.out.println("Admin entry already exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    
   
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        AirlineReservationMain airlineReservationMain = new AirlineReservationMain(scanner);
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        User user = new User(scanner);
        Reservation reservation = new Reservation(scanner);
        Flights flights = new Flights(scanner);

        airlineReservationMain.dataBaseConnection = dataBaseConnection;
        
        airlineReservationMain.createAdminEntry();
        
        airlineReservationMain.continueFun();
        
        int choice;
        do {
            System.out.println("\n\n\nWelcome to Flight Reservation System:");
            System.out.println("=====================================");
            System.out.println("1. Create User Account");
            System.out.println("2. User Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.println("=====================================");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    user.createUserAccount();
                    break;
                case 2:
                    User loginUser = user.userLogin();
                    if(loginUser != null){
                        int userChoice;
                        do{
                            System.out.println("\n\n\nWelcome User");
                            System.out.println("==============");
                            System.out.println("1. Book Flight");
                            System.out.println("2. Cancel Flight");
                            System.out.println("3. View All Previous Bookings");
                            System.out.println("4. Logout");
                            System.out.println("==============");
                            System.out.println("Enter your choice: ");
                            userChoice = scanner.nextInt();
                            scanner.nextLine();
                            switch (userChoice) {
                                case 1:
                                    reservation.bookFlight(loginUser);
                                    break;
                                case 2:
                                    reservation.cancelFlight(loginUser);
                                    break;
                                case 3:
                                    flights.viewFlights(loginUser);
                                    break;
                                case 4:
                                    System.out.println("User Logout Successful");
                                    break;
                                default:
                                    System.out.println("Invalid choice");
                            }
                        }while(userChoice != 4);
                    }
                    break;
                case 3:
                    boolean adminLoginStatus = user.adminLogin();
                    if(adminLoginStatus){
                        int adminChoice;
                        do{
                            System.out.println("\n\n\nWelcome Admin");
                            System.out.println("==============");
                            System.out.println("1. Add New Flight");
                            System.out.println("2. Remove Flight");
                            System.out.println("3. Logout");
                            System.out.println("==============");
                            System.out.print("Enter your choice: ");
                            adminChoice = scanner.nextInt();
                            scanner.nextLine();
                            switch (adminChoice) {
                                case 1:
                                    flights.addNewFlight();
                                    break;
                                case 2:
                                    flights.removeFlight();
                                    break;
                                case 3:
                                    System.out.println("\nAdmin Logout Successful");
                                    break;
                                default:
                                    System.out.println("\nInvalid choice");
                            }
                        }while(adminChoice != 3);
                    }
                    break;
                case 4:
                    System.out.println("\nThank you for using Flight Reservation System");
                    break;
                default:
                    System.out.println("\nInvalid choice");
            }
        } while (choice != 4);

        scanner.close();
    }
}
