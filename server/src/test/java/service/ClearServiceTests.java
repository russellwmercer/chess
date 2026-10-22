package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.UserData;
import model.GameData;
import model.AuthData;
import static org.junit.jupiter.api.Assertions.*;


public class ClearServiceTests {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private ClearService clearService;

    // Does this before each test to set it up.
    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        clearService = new ClearService(gameDAO, userDAO, authDAO);
    }

    // This is the test that it actually runs. I can have multiple.
    @Test
    void clearRemovesEverything() throws Exception {
        userDAO.createUser(new UserData("username","password","email"));
        gameDAO.createGame(new GameData(123, "whiteUsername", "blackusername", "gameName", new ChessGame()));
        authDAO.createAuth(new AuthData("authToken","username"));

        clearService.clearAllData();

        assertNull(userDAO.getUser("username"));
        assertNull(gameDAO.getGame(123));
        assertNull(authDAO.getAuth("authToken"));
    }
}
