package client;

import org.junit.jupiter.api.*;
import requestresult.*;
import server.Server;
import serverfacade.ServerFacade;
import serverfacade.ServerFacadeException;

import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @Test
    public void clearPositive() {
        //I keep forgetting this notation: This is a lambda. It gives no data () and instead tells it to do facade.clear().
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    public void addUserPositive() throws ServerFacadeException {
        RegisterResult result = facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));

        Assertions.assertEquals("Username", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void addUserNegative() throws ServerFacadeException {
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
}
