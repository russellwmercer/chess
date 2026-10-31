package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import java.sql.SQLException;

public class SqlUserDAO implements UserDAO {

    // Constructor: creates the tables in the SQL in case its not there.
    public SqlUserDAO() throws DataAccessException {
        DatabaseSetup.configureDatabase();
    }

    // Add a line to the table.
    @Override
    public void createUser(UserData user) throws DataAccessException {
        var sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (var connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to add user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT username, password, email FROM user WHERE username = ?";
        try (var connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return readUser(resultSet);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to get user", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE user";
        try (var connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to clear users", ex);
        }
    }


    // Turn the result from the data query into an actual object.
    private UserData readUser(java.sql.ResultSet rs) throws SQLException {
        return new UserData(rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"));
    }
}
