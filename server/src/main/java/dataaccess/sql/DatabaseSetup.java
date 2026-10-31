package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.DataAccessException;
import java.sql.SQLException;

public class DatabaseSetup {

    private static final String[] CREATE_STATEMENTS = {
        """ 
        CREATE TABLE IF NOT EXISTS user (
            username  VARCHAR(255)  NOT NULL,
            password  VARCHAR(60)  NOT NULL,
            email  VARCHAR(255)  NOT NULL,
            PRIMARY KEY (username)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS auth (
            authToken  VARCHAR(36)  NOT NULL,
            username  VARCHAR(255)  NOT NULL,
            PRIMARY KEY (authToken)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS game (
            gameID  INT  NOT NULL AUTO_INCREMENT,
            whiteUsername  VARCHAR(255),
            blackUsername  VARCHAR(255),
            gameName  VARCHAR(255)  NOT NULL,
            game  TEXT  NOT NULL,
            PRIMARY KEY (gameID)
        )
        """
    };

    // Note to self: Doing it this way opens and closes the connection, described by starter code.
    public static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase(); // Make the database if it is missing to be safe.
        try (var connection = DatabaseManager.getConnection()) { // open a connection
            for (String statement : CREATE_STATEMENTS) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate(); // update the database with the prepared statement.
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database", ex);
        }

    }

}
