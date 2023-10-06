
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class DataBaseConnection{
    private String url = "jdbc:mysql://localhost:3306/airline_reservation_system";
    private String userName = "root";
    private String password = "root";
    public Connection connection;

    public DataBaseConnection() throws SQLException{
        Connection connection = DriverManager.getConnection(url, userName, password);
        this.connection = connection;
    }

    public void disconnect() throws SQLException{
        this.connection.close();
        return;
    }

    public ResultSet executeQuery(String query) throws SQLException{
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        statement.close();

        return resultSet;
    }

    public int executeUpdate(String query) throws SQLException{
        Statement statement = this.connection.createStatement();
        int resultSet =  statement.executeUpdate(query);
        statement.close();
        return resultSet;
    }

    public PreparedStatement preparedStatement(String query) throws SQLException{
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        return preparedStatement;
    }
}
