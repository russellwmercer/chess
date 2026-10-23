package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.CreateGameRequest;
import requestresult.CreateGameResult;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTests {
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private AuthChecker checker;
    private CreateGameService createGameService;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        checker = new AuthChecker(authDAO);
        createGameService = new CreateGameService(checker, gameDAO);
    }

    @Test
    void createGameSuccessfully() throws Exception {
        String authToken = "authToken";
        authDAO.createAuth(new AuthData(authToken, "username"));

        CreateGameRequest request =
                new CreateGameRequest(authToken, "Test Game");

        CreateGameResult result = createGameService.createGame(request);

        assertTrue(result.gameID() > 0);

        GameData game = gameDAO.getGame(result.gameID());
        assertNotNull(game);
        assertEquals("Test Game", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertNotNull(game.game());
    }

    @Test
    void createGameWithMissingNameThrowsBadRequestException() throws Exception {
        authDAO.createAuth(new AuthData("authToken", "username"));

        CreateGameRequest request = new CreateGameRequest("authToken", null);

        assertThrows(BadRequestException.class, () -> createGameService.createGame(request)
        );
    }

    @Test
    void createGameWithInvalidTokenThrowsUnauthorizedException() {
        CreateGameRequest request = new CreateGameRequest("invalidToken", "Test Game");

        assertThrows(UnauthorizedException.class, () -> createGameService.createGame(request));
    }

    @Test
    void createGameWithMissingTokenThrowsUnauthorizedException() {
        CreateGameRequest request = new CreateGameRequest(null, "Test Game");

        assertThrows(UnauthorizedException.class, () -> createGameService.createGame(request)
        );
    }
}
