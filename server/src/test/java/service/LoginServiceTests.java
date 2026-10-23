package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.LoginRequest;
import requestresult.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTests {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();

        loginService = new LoginService(userDAO, authDAO);
    }

    @Test
    void loginSuccessfully() throws Exception {
        userDAO.createUser(
                new UserData("username", "password", "email")
        );

        LoginRequest request = new LoginRequest("username", "password");

        LoginResult result = loginService.login(request);

        assertEquals("username", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());

        AuthData auth = authDAO.getAuth(result.authToken());
        assertNotNull(auth);
        assertEquals("username", auth.username());
    }

    @Test
    void loginWithMissingUsernameThrowsBadRequestException() {
        LoginRequest request = new LoginRequest(null, "password");

        assertThrows(
                BadRequestException.class,
                () -> loginService.login(request)
        );
    }

    @Test
    void loginWithMissingPasswordThrowsBadRequestException() {
        LoginRequest request = new LoginRequest("username", null);

        assertThrows(
                BadRequestException.class,
                () -> loginService.login(request)
        );
    }

    @Test
    void loginWithNonexistentUsernameThrowsUnauthorizedException() {
        LoginRequest request = new LoginRequest("unknownUser", "password");

        assertThrows(
                UnauthorizedException.class,
                () -> loginService.login(request)
        );
    }

    @Test
    void loginWithIncorrectPasswordThrowsUnauthorizedException() throws Exception {
        userDAO.createUser(
                new UserData("username", "correctPassword", "email")
        );

        LoginRequest request =
                new LoginRequest("username", "wrongPassword");

        assertThrows(UnauthorizedException.class,
                () -> loginService.login(request)
        );
    }
}
