package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.AuthDAO;
import model.AuthData;
import java.sql.SQLException;

public class SqlAuthDAO implements AuthDAO {

    // Constructor: creates the tables in the SQL in case its not there.
    public SqlAuthDAO() throws DataAccessException {
        DatabaseSetup.configureDatabase();
    }

    // Add a line to the table.
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, auth.authToken());
            preparedStatement.setString(2, auth.username());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to add auth", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var sql = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, authToken);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return readAuth(resultSet);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to get auth", ex);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var sql = "DELETE FROM auth WHERE authToken = ?";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to delete auth", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE auth";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to clear auths", ex);
        }
    }


    // Turn the result from the data query into an actual object.
    private AuthData readAuth(java.sql.ResultSet rs) throws SQLException {
        return new AuthData(rs.getString("authToken"),
                rs.getString("username"));
    }
}
