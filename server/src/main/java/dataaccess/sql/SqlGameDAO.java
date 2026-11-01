package dataaccess.sql;

import com.google.gson.Gson;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    // Constructor: creates the tables in the SQL in case its not there.
    public SqlGameDAO() throws DataAccessException {
        DatabaseSetup.configureDatabase();
    }

    // Add a line to the table.
    @Override
    public int createGame(GameData game) throws DataAccessException {
        var sql = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, gson.toJson(game.game()));
            preparedStatement.executeUpdate();
            // A try block to read back the ID MySQL assigned so it can be returned.
            try (var keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new DataAccessException("Error: no ID was generated");
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to add game", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, gameID);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return readGame(resultSet);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to get game", ex);
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        var sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, gameData.whiteUsername());
            preparedStatement.setString(2, gameData.blackUsername());
            preparedStatement.setString(3, gameData.gameName());
            preparedStatement.setString(4, gson.toJson(gameData.game()));
            preparedStatement.setInt(5, gameData.gameID());
            // A small snippet to help it know if it did not update.
            int rowsChanged = preparedStatement.executeUpdate();
            if (rowsChanged == 0) {
                throw new DataAccessException("Error: game not found");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to update game", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE game";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to clear games", ex);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        var sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
        var gamesList = new ArrayList<GameData>();
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(sql);
             var resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                gamesList.add(readGame(resultSet));
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to list games", ex);
        }
        return gamesList;
    }

    // Turn the result from the data query into an actual object.
    private GameData readGame(java.sql.ResultSet rs) throws SQLException {
        return new GameData(rs.getInt("gameID"),
                rs.getString("whiteUsername"),
                rs.getString("blackUsername"),
                rs.getString("gameName"),
                gson.fromJson(rs.getString("game"), ChessGame.class));
    }
}
