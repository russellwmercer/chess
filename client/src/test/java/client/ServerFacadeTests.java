package client;

import org.junit.jupiter.api.*;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
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

    @Test
    public void clearPositive() {
        //I keep forgetting this notation: This is a lambda. It gives no data () and instead tells it to do facade.clear().
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    public void addUserPositive() throws ServerFacadeException {
        RegisterResult result = facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));

        Assertions.assertEquals(result.username(), "Username");
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void addUserNegative() throws ServerFacadeException {
        facade.register(new RegisterRequest("Username",
                "password", "email@test.com"));

        Assertions.assertThrows(ServerFacadeException.class, () -> facade.register(
                new RegisterRequest("Username", "password", "email@test.com")));
    }
}
