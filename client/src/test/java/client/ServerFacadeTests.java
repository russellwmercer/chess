package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import requestresult.*;
import server.Server;
import serverfacade.ServerFacade;
import serverfacade.ServerFacadeException;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clearAll() throws ServerFacadeException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // Commented this out because it has been replaced by new tests.
//    @Test
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }

    @Test void registerNegativeServerDown() {
        Server deadServer = new Server();
        int deadPort = deadServer.run(0);
        deadServer.stop();

        ServerFacade offline = new ServerFacade(deadPort);

        ServerFacadeException e = Assertions.assertThrows(ServerFacadeException.class,
                () -> offline.register(new RegisterRequest("Test", "password",
                        "bademail@email.com")));
        Assertions.assertEquals("Could not reach the server.", e.getMessage());
    }

    @Test
    public void clearPositive() {
        //I keep forgetting this notation: This is a lambda. It gives no data () and instead tells it to do facade.clear().
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    public void registerPositive() throws ServerFacadeException {
        RegisterResult result = facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));

        Assertions.assertEquals("Username", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void registerNegative() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));

        Assertions.assertThrows(ServerFacadeException.class, () -> facade.register(
                new RegisterRequest("Username", "password", "email@test.com")));
    }

    @Test
    public void loginPositive() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));
        LoginResult result = facade.login(new LoginRequest("Username","password"));

        Assertions.assertEquals("Username", result.username());
        Assertions.assertNotNull(result.authToken());
        Assertions.assertFalse(result.authToken().isEmpty());
    }

    @Test
    public void loginNegativeWrongPassword() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));

        ServerFacadeException e = Assertions.assertThrows(ServerFacadeException.class, () -> facade.login(
                new LoginRequest("Username","passwordAHH")));
        Assertions.assertEquals("Wrong username or password.", e.getMessage());
    }

    @Test
    public void loginNegativeUsernameDNE() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));

        ServerFacadeException e = Assertions.assertThrows(ServerFacadeException.class, () -> facade.login(
                new LoginRequest("UsernameAhhh","password")));
        Assertions.assertEquals("Wrong username or password.", e.getMessage());
    }

    @Test
    public void logoutPositive() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));
        Assertions.assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    public void logoutNegativeNotLoggedIn() {
        Assertions.assertThrows(ServerFacadeException.class, () -> facade.logout());
    }

    @Test
    public void createGamePositive() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));
        CreateGameResult result = facade.createGame("gameNameTest");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.gameID() >= 0);
    }

    @Test
    public void createGameNegative() {
        Assertions.assertThrows(ServerFacadeException.class, () -> facade.createGame("gameNameTest"));
    }

    @Test
    public void listGamesPositive() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));
        facade.createGame("gameNameTest");
        CreateGameResult createGameResult2 = facade.createGame("gameNameTest2");
        ListGamesResult result = facade.listGames();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.games().size());
        Assertions.assertEquals("gameNameTest", result.games().getFirst().gameName());
        Assertions.assertEquals("gameNameTest2", result.games().get(1).gameName());
        Assertions.assertNull(result.games().getFirst().whiteUsername());
        Assertions.assertNull(result.games().getFirst().blackUsername());
        Assertions.assertEquals(createGameResult2.gameID(), result.games().get(1).gameID());
    }

    @Test
    public void listGamesNegative() {
        Assertions.assertThrows(ServerFacadeException.class, () -> facade.listGames());
    }

    @Test
    public void joinGamePositive() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));
        CreateGameResult result = facade.createGame("gameNameTest");
        int gameID = result.gameID();

        Assertions.assertDoesNotThrow(() -> facade.joinGame(ChessGame.TeamColor.WHITE, gameID));

        ListGamesResult listResult = facade.listGames();

        Assertions.assertEquals("Username", listResult.games().getFirst().whiteUsername());
        Assertions.assertNull(listResult.games().getFirst().blackUsername());
    }

    @Test
    public void joinGameNegativeNotLoggedIn() {
        Assertions.assertThrows(ServerFacadeException.class, () -> facade.joinGame(ChessGame.TeamColor.WHITE, 1000));
    }

    @Test
    public void joinGameNegativeColorTaken() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
            "password", "email@test.com"));
        CreateGameResult result = facade.createGame("gameNameTest");
        int gameID = result.gameID();

        Assertions.assertDoesNotThrow(() -> facade.joinGame(ChessGame.TeamColor.WHITE, gameID));

        ListGamesResult listResult = facade.listGames();

        Assertions.assertEquals("Username", listResult.games().getFirst().whiteUsername());
        Assertions.assertNull(listResult.games().getFirst().blackUsername());

        Assertions.assertThrows(ServerFacadeException.class, () -> facade.joinGame(ChessGame.TeamColor.WHITE, gameID));
    }

    @Test
    public void joinGameNegativeGameDNE() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));
        CreateGameResult result = facade.createGame("gameNameTest");
        int gameID = result.gameID();

        Assertions.assertDoesNotThrow(() -> facade.joinGame(ChessGame.TeamColor.WHITE, gameID));
        Assertions.assertThrows(ServerFacadeException.class, () -> facade.joinGame(ChessGame.TeamColor.WHITE, 9999));
    }

}
