package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.ListGamesRequest;
import requestresult.ListGamesResult;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTests {
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private AuthChecker checker;
    private ListGamesService listGamesService;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        checker = new AuthChecker(authDAO);
        listGamesService = new ListGamesService(checker, gameDAO);
    }

    @Test
    void listGamesSuccessfully() throws Exception {
        String authToken = "authToken";
        authDAO.createAuth(new AuthData(authToken, "username"));

        gameDAO.createGame(new GameData(1, null, null, "Game One", new ChessGame()));
        gameDAO.createGame(new GameData(2, null, null, "Game Two", new ChessGame()));

        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = listGamesService.listGames(request);

        assertNotNull(result.games());
        assertEquals(2, result.games().size());
        assertTrue(result.games().stream().anyMatch(game -> game.gameName().equals("Game One")));
        assertTrue(result.games().stream().anyMatch(game -> game.gameName().equals("Game Two")));
    }

    @Test
    void listGamesWhenNoGamesExist() throws Exception {
        authDAO.createAuth(new AuthData("authToken", "username"));

        ListGamesRequest request = new ListGamesRequest("authToken");

        ListGamesResult result = listGamesService.listGames(request);

        assertNotNull(result.games());
        assertTrue(result.games().isEmpty());
    }

    @Test
    void listGamesWithInvalidTokenThrowsUnauthorizedException() {
        ListGamesRequest request = new ListGamesRequest("invalidToken");

        assertThrows(UnauthorizedException.class, () -> listGamesService.listGames(request)
        );
    }

    @Test
    void listGamesWithMissingTokenThrowsUnauthorizedException() {
        ListGamesRequest request = new ListGamesRequest(null);

        assertThrows(UnauthorizedException.class, () -> listGamesService.listGames(request)
        );
    }
}
