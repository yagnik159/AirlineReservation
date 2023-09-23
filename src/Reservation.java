import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

public class Reservation{
    String id;
    String userId;
    String flightId;
    String className;
    Scanner scanner;

    public Reservation(Scanner scanner){
        this.scanner = scanner;
    }

    public Reservation(ResultSet resultSet) throws SQLException{
        this.id = resultSet.getString("Id");
        this.userId = resultSet.getString("UserId");
        this.flightId = resultSet.getString("FlightId");
        this.className = resultSet.getString("ClassName");
    }

    public void bookFlight(User user) throws SQLException{
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        AirlineReservationMain airLineReservationMain = new AirlineReservationMain(scanner);
        Flights flights = new Flights(scanner);

        String userId = user.id;
        String flightId;
        String className;

        List<Flights> flightsList = flights.viewAllAvailableFlights();
        System.out.println("\n\n\nBook Flight");
        System.out.println("==============");
        System.out.println("Available Flights");
        System.out.println("==============");
        if(flightsList.size() == 0){
            System.out.println("No flights available");
            airLineReservationMain.continueFun();
            
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
        scanner.nextLine();
        if(index > flightsList.size() || index < 1){
            System.out.println("Invalid id");
            airLineReservationMain.continueFun();
            
            return;
        }
        flightId = flightsList.get(index - 1).id;

        System.out.println("1. First Class: price = " + flightsList.get(index - 1).getFirstClassPrice());
        System.out.println("2. Business Class: price = " + (flightsList.get(index - 1).getBusinessClassPrice()));
        System.out.println("3. Economical Class: price = " + (flightsList.get(index - 1).getEconomicalClassPrice()));

        System.out.print("Enter class name: ");
        int classChoice = scanner.nextInt();
        scanner.nextLine();

        if(classChoice == 1) className = "First Class";
        else if(classChoice == 2) className = "Business Class";
        else className = "Economical Class";

        System.out.println("==============");

        String insertReservationQuery = "INSERT INTO Reservation (UserId, FlightId, ClassName) VALUES ('" + userId + "', '" + flightId + "', '" + className + "')";

        try {
            dataBaseConnection.executeUpdate(insertReservationQuery);
            System.out.println("Flight Booked Successfully");
            airLineReservationMain.continueFun();
        } catch (SQLException e) {
            System.out.println("Error while executing query");
            airLineReservationMain.continueFun();
        }

        dataBaseConnection.disconnect();
        
        
        return;
    }

    public List<Reservation> allBookings(User user) throws SQLException{
        List<Reservation> reservationList = new ArrayList<Reservation>();
        String userId = user.id;
        String selectAllFlightsQuery = "SELECT * FROM Reservation WHERE UserId = ?";
        ResultSet resultSet = null;

        DataBaseConnection dataBaseConnection = new DataBaseConnection();

        try{
            PreparedStatement preparedStatement = dataBaseConnection.preparedStatement(selectAllFlightsQuery);
            preparedStatement.setString(1, userId);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){

                Reservation reservation = new Reservation(resultSet);
                reservationList.add(reservation);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        dataBaseConnection.disconnect();

        return reservationList;
    }

    public void cancelFlight(User user) throws SQLException{
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        AirlineReservationMain airLineReservationMain = new AirlineReservationMain(scanner);
        Flights flights = new Flights(scanner);

        List<Reservation> reservationList = allBookings(user);
        System.out.println("\n\n\nCancel Flight");
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
            Flights flight = flights.getFlightFromReservation(reservations);
            fmt.format("%3s %15s %15s %15s %15s %10s %15s\n", index++, flight.fromLocation, flight.toLocation, flight.departureTime, flight.arrivalTime, flight.duration, reservations.className);
        }
        System.out.println(fmt);
        System.out.println("==============");

        System.out.print("Enter booking id: ");
        index = scanner.nextInt();
        scanner.nextLine();

        if(index > reservationList.size() || index < 1){
            System.out.println("Invalid id");
            airLineReservationMain.continueFun();
            
            return;
        }

        String reservationId = reservationList.get(index - 1).id;
        System.out.println("==============");

        String deleteReservationQuery = "DELETE FROM Reservation WHERE Id = '" + reservationId + "'";

        try {
            dataBaseConnection.executeUpdate(deleteReservationQuery);  
            System.out.println("Flight Cancelled Successfully");
            airLineReservationMain.continueFun();
        }catch (SQLException e) {
            System.out.println("Error while executing query");
            airLineReservationMain.continueFun();
        }

        dataBaseConnection.disconnect();
        
        return;
    }

    
}
