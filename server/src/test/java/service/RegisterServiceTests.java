package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTests {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();

        registerService = new RegisterService(userDAO, authDAO);
    }

    @Test
    void registerSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("username", "password", "email");

        RegisterResult result = registerService.register(request);

        assertEquals("username", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());

        UserData user = userDAO.getUser("username");
        assertNotNull(user);
        assertEquals("username", user.username());
        assertEquals("password", user.password());
        assertEquals("email", user.email());

        assertNotNull(authDAO.getAuth(result.authToken()));
        assertEquals("username", authDAO.getAuth(result.authToken()).username());
    }

    @Test
    void registerDuplicateUsernameThrowsException() throws Exception {
        userDAO.createUser(new UserData("username", "password", "email"));

        RegisterRequest request =
                new RegisterRequest("username", "differentPassword", "other@email.com");

        assertThrows(
                AlreadyTakenException.class,
                () -> registerService.register(request)
        );
    }

    @Test
    void registerMissingUsernameThrowsException() {
        RegisterRequest request =
                new RegisterRequest(null, "password", "email");

        assertThrows(
                BadRequestException.class,
                () -> registerService.register(request)
        );
    }

    @Test
    void registerMissingPasswordThrowsException() {
        RegisterRequest request =
                new RegisterRequest("username", null, "email");

        assertThrows(
                BadRequestException.class,
                () -> registerService.register(request)
        );
    }

    @Test
    void registerMissingEmailThrowsException() {
        RegisterRequest request =
                new RegisterRequest("username", "password", null);

        assertThrows(
                BadRequestException.class,
                () -> registerService.register(request)
        );
    }
}