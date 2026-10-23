
package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.JoinGameRequest;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTests {
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private AuthChecker checker;
    private JoinGameService joinGameService;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        checker = new AuthChecker(authDAO);
        joinGameService = new JoinGameService(checker, gameDAO);
    }

    @Test
    void joinGameAsWhiteSuccessfully() throws Exception {
        String authToken = "authToken";
        authDAO.createAuth(new AuthData(authToken, "username"));

        int gameID = gameDAO.createGame(new GameData(0, null, null, "Test Game", new ChessGame()));

        JoinGameRequest request = new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, gameID);

        assertDoesNotThrow(() -> joinGameService.joinGame(request));

        GameData game = gameDAO.getGame(gameID);
        assertNotNull(game);
        assertEquals("username", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameAsBlackSuccessfully() throws Exception {
        authDAO.createAuth(new AuthData("authToken", "username"));

        int gameID = gameDAO.createGame(new GameData(0, null, null, "Test Game", new ChessGame()));

        JoinGameRequest request = new JoinGameRequest("authToken", ChessGame.TeamColor.BLACK, gameID);

        assertDoesNotThrow(() -> joinGameService.joinGame(request));

        GameData game = gameDAO.getGame(gameID);
        assertNotNull(game);
        assertNull(game.whiteUsername());
        assertEquals("username", game.blackUsername());
    }

    @Test
    void joinGameWithMissingColorThrowsBadRequestException() throws Exception {;
        authDAO.createAuth(new AuthData("authToken", "username"));

        int gameID = gameDAO.createGame(new GameData(0, null, null, "Test Game", new ChessGame()));

        JoinGameRequest request = new JoinGameRequest("authToken", null, gameID);

        assertThrows(BadRequestException.class, () -> joinGameService.joinGame(request));
    }

    @Test
    void joinGameWithMissingGameIDThrowsBadRequestException() throws Exception {
        authDAO.createAuth(new AuthData("authToken", "username"));

        JoinGameRequest request = new JoinGameRequest("authToken", ChessGame.TeamColor.WHITE, null);

        assertThrows(BadRequestException.class, () -> joinGameService.joinGame(request));
    }

    @Test
    void joinGameWithInvalidTokenThrowsUnauthorizedException() throws Exception {
        int gameID = gameDAO.createGame(new GameData(0, null, null, "Test Game", new ChessGame()));

        JoinGameRequest request = new JoinGameRequest("invalidToken", ChessGame.TeamColor.WHITE, gameID);
        assertThrows(UnauthorizedException.class, () -> joinGameService.joinGame(request));
    }

    @Test
    void joinGameWithNonexistentGameThrowsBadRequestException() throws Exception {
        authDAO.createAuth(new AuthData("authToken", "username"));

        JoinGameRequest request = new JoinGameRequest("authToken", ChessGame.TeamColor.WHITE, 99999);

        assertThrows(BadRequestException.class, () -> joinGameService.joinGame(request));
    }

    @Test
    void joinGameAsWhiteWhenWhiteIsTakenThrowsAlreadyTakenException() throws Exception {
        authDAO.createAuth(new AuthData("authToken", "username"));

        int gameID = gameDAO.createGame(new GameData(0, "existingWhite", null, "Test Game", new ChessGame()));
        JoinGameRequest request = new JoinGameRequest("authToken", ChessGame.TeamColor.WHITE, gameID);

        assertThrows(AlreadyTakenException.class, () -> joinGameService.joinGame(request));
    }

    @Test
    void joinGameAsBlackWhenBlackIsTakenThrowsAlreadyTakenException() throws Exception {
        authDAO.createAuth(new AuthData("authToken", "username"));

        int gameID = gameDAO.createGame(new GameData(0, null, "existingBlack", "Test Game", new ChessGame()));

        JoinGameRequest request = new JoinGameRequest("authToken", ChessGame.TeamColor.BLACK, gameID);

        assertThrows(AlreadyTakenException.class, () -> joinGameService.joinGame(request));
    }
}
