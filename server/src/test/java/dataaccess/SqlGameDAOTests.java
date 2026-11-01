package dataaccess;

import chess.ChessGame;
import dataaccess.sql.SqlGameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlGameDAOTests {
    private SqlGameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new SqlGameDAO();
        gameDAO.clear(); // clear - start the tests with an empty database.
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        // Have to create a game to pass into the gameDAO in a GameData object.
        var game = new ChessGame();
        var gameRecord = new GameData(0, null, null, "gameName", game);
        int gameID = gameDAO.createGame(gameRecord);

        assertTrue(gameID > 0);
        GameData found = gameDAO.getGame(gameID);
        assertNotNull(found);
        assertEquals("gameName", found.gameName());
        assertNull(found.whiteUsername());
        assertNull(found.blackUsername());
        assertEquals(game, found.game());
    }

    @Test
    void createGameMissingGameName() throws DataAccessException {
        var gameData = new GameData(0, null, null, null, new ChessGame());

        assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData));
    }

    @Test
    void getGameSuccess() throws DataAccessException {
        var game = new ChessGame();
        var gameRecord = new GameData(0, "whiteUsername", null, "gameName", game);
        int id = gameDAO.createGame(gameRecord);

        GameData found = gameDAO.getGame(id);

        assertNotNull(found);
        assertEquals(id, found.gameID());
        assertEquals("whiteUsername", found.whiteUsername());
        assertNull(found.blackUsername());
        assertEquals("gameName", found.gameName());
        assertEquals(game, found.game());
    }

    @Test
    void getGameMissing() throws DataAccessException {
        assertNull(gameDAO.getGame(999));
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame(new GameData(0, null, null, "gameName", new ChessGame()));
        gameDAO.createGame(new GameData(0, null, null, "gameName2", new ChessGame()));

        List<GameData> all = gameDAO.listGames();

        assertEquals(2, all.size());
    }

    @Test
    void listGamesEmpty() throws DataAccessException {
        List<GameData> all = gameDAO.listGames();

        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void updateGameSuccess() throws DataAccessException {
        var game = new ChessGame();
        int id = gameDAO.createGame(new GameData(0, null, null, "test", game));

        gameDAO.updateGame(new GameData(id, "white", null, "test", game));

        GameData found = gameDAO.getGame(id);
        assertEquals("white", found.whiteUsername()); //See the change went through
        assertEquals("test", found.gameName()); //See the rest didn't change.
    }

    @Test
    void updateGameMissing() {
        var ghost = new GameData(999, "white", null, "test", new ChessGame());

        assertThrows(DataAccessException.class,
                () -> gameDAO.updateGame(ghost));
    }

    @Test
    void clearSuccess() throws DataAccessException {
        gameDAO.createGame(new GameData(0, null, null, "test", new ChessGame()));
        gameDAO.createGame(new GameData(0, null, null, "test2", new ChessGame()));

        gameDAO.clear();

        assertTrue(gameDAO.listGames().isEmpty());
    }
}