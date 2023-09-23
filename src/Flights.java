import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

public class Flights{
    String id;
    String fromLocation;
    String toLocation;
    String duration;
    String departureTime;
    String arrivalTime;
    int firstClassSeats;
    int businessClassSeats;
    int economicalClassSeats;
    int price;
    int status;
    Scanner scanner;

    public Flights(Scanner scanner){
        this.scanner = scanner;
    }

    public Flights(ResultSet resultSet) throws SQLException{
        this.id = resultSet.getString("Id");
        this.fromLocation = resultSet.getString("FromLocation");
        this.toLocation = resultSet.getString("ToLocation");
        this.duration = resultSet.getString("Duration");
        this.departureTime = resultSet.getString("DepartureTime");
        this.arrivalTime = resultSet.getString("ArrivalTime");
        this.firstClassSeats = resultSet.getInt("FirstClassSeats");
        this.businessClassSeats = resultSet.getInt("BusinessClassSeats");
        this.economicalClassSeats = resultSet.getInt("EconomicalClassSeats");
        this.price = resultSet.getInt("Price");
        this.status = resultSet.getInt("Status");
    }

    public int getFirstClassPrice(){
        return this.price * 9;
    }

    public int getBusinessClassPrice(){
        return this.price * 5;
    }

    public int getEconomicalClassPrice(){
        return this.price * 2;
    }

    public List<Flights> viewAllAvailableFlights() throws SQLException{
        List<Flights> flightsList = new ArrayList<Flights>();
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        String selectAllFlightsQuery = "SELECT * FROM Flights WHERE Status = ?";
        ResultSet resultSet = null;

        try{
            PreparedStatement preparedStatement = dataBaseConnection.preparedStatement(selectAllFlightsQuery);
            preparedStatement.setInt(1, 1);

            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Flights flight = new Flights(resultSet);
                flightsList.add(flight);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        dataBaseConnection.disconnect();
        return flightsList;
    }

    public Flights getFlightFromReservation(Reservation reservation) throws SQLException{
        String flightId = reservation.flightId;
        String selectFlightQuery = "SELECT * FROM Flights WHERE Id = ?";
        ResultSet resultSet = null;
        Flights flight = null;
        DataBaseConnection dataBaseConnection = new DataBaseConnection();

        try{
            PreparedStatement preparedStatement = dataBaseConnection.preparedStatement(selectFlightQuery);
            preparedStatement.setString(1, flightId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                flight = new Flights(resultSet);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        dataBaseConnection.disconnect();
        return flight;
    }

    public void viewFlights(User user) throws SQLException{
        AirlineReservationMain airLineReservationMain = new AirlineReservationMain(scanner);
        Reservation reservation = new Reservation(scanner);

        List<Reservation> reservationList = reservation.allBookings(user);
        System.out.println("\n\n\nView Flights");
        System.out.println("==============");
        System.out.println("Your Bookings");
        System.out.println("==============");
        if(reservationList.size() == 0){
            System.out.println("No bookings available");
            airLineReservationMain.continueFun();

            return;
        }
        Formatter fmt = new Formatter();
        fmt.format("%3s %15s %15s %15s %15s %10s %15s\n", "Id","From","To","Departure","Arrival","Duration","Class");
        int index = 1;
        for(Reservation reservations: reservationList){
            Flights flight = getFlightFromReservation(reservations);
            fmt.format("%3s %15s %15s %15s %15s %10s %15s\n", index++, flight.fromLocation, flight.toLocation, flight.departureTime, flight.arrivalTime, flight.duration, reservations.className);
        }
        System.out.println(fmt);
        System.out.println("==============");
        
        return;
    }

    public void addNewFlight() throws SQLException{
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        AirlineReservationMain airLineReservationMain = new AirlineReservationMain(scanner);

        String fromLocation;
        String toLocation;
        String duration;
        String departureTime;
        String arrivalTime;
        int firstClassSeats;
        int businessClassSeats;
        int economicalClassSeats;
        int price;
        int status = 1;

        System.out.println("\n\n\nAdd New Flight");
        System.out.println("==============");
        System.out.print("Enter from location: ");
        fromLocation = scanner.nextLine();
        System.out.print("Enter to location: ");
        toLocation = scanner.nextLine();
        System.out.print("Enter duration: (HH.MM)");
        duration = scanner.nextLine();
        System.out.print("Enter departure time: (YYYY/MM/DD:HH.MM) ");
        departureTime = scanner.nextLine();
        System.out.print("Enter arrival time: (YYYY/MM/DD:HH.MM) ");
        arrivalTime = scanner.nextLine();
        System.out.print("Enter Number of first class seats: ");
        firstClassSeats = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Number of business class seats: ");
        businessClassSeats = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Number of economical class seats: ");
        economicalClassSeats = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Base price: ");
        price = scanner.nextInt();
        scanner.nextLine();
        System.out.println("==============");
        
        String insertFlightQuery = "INSERT INTO Flights (FromLocation, ToLocation, Duration, DepartureTime, ArrivalTime, FirstClassSeats, BusinessClassSeats, EconomicalClassSeats, Price, Status) VALUES ('" + fromLocation + "', '" + toLocation + "', '" + duration + "', '" + departureTime + "', '" + arrivalTime + "', " + firstClassSeats + ", " + businessClassSeats + ", " + economicalClassSeats + ", " + price + ", " + status + ")";

        try {
            dataBaseConnection.executeUpdate(insertFlightQuery);
            System.out.println("Flight Added Successfully");
            airLineReservationMain.continueFun();
        } catch (SQLException e) {
            System.out.println("Error while executing query");
            airLineReservationMain.continueFun();
        }

        dataBaseConnection.disconnect();
           
    }

    public void removeFlight() throws SQLException{
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        AirlineReservationMain airLineReservationMain = new AirlineReservationMain(scanner);

        String flightId;
        System.out.println("\n\n\nRemove Flight");
        List<Flights> flightsList = viewAllAvailableFlights();
        System.out.println("==============");
        System.out.println("Available Flights");
        System.out.println("==============");
        if(flightsList.size() == 0){
            System.out.println("No flights available");
            airLineReservationMain.continueFun();
            
            dataBaseConnection.disconnect();
            return;
        }
        Formatter fmt = new Formatter();
        fmt.format("%3s %15s %15s %15s %15s %10s\n", "Id","From","To","Departure","Arrival","Duration");
        int index = 1;
        for(Flights flight: flightsList){
            fmt.format("%3s %15s %15s %15s %15s %10s\n", index++, flight.fromLocation, flight.toLocation, flight.departureTime, flight.arrivalTime, flight.duration);
        }
        System.out.println(fmt);
        System.out.println("==============");
        System.out.print("Enter flight id: ");
        index = scanner.nextInt();
        if(index > flightsList.size()){
            System.out.println("Invalid Id");
            airLineReservationMain.continueFun();
            
            dataBaseConnection.disconnect();
            return;
        }
        flightId = flightsList.get(index - 1).id;
        System.out.println("==============");

        String updateStatusOfFlight = "UPDATE Flights SET Status = 0 WHERE Id = ?";

        try {
            PreparedStatement preparedStatement = dataBaseConnection.preparedStatement(updateStatusOfFlight);
            preparedStatement.setString(1, flightId);
            int rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();
        
            if (rowsAffected > 0) {
                System.out.println("Flight status updated successfully.");
            } else {
                System.out.println("No flights were updated.");
            }
            airLineReservationMain.continueFun();
        } catch (SQLException e) {
            System.out.println("Error while executing query");
            airLineReservationMain.continueFun();
        }

        dataBaseConnection.disconnect();
        
        return;
    }

}   
