package service;

import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.LogoutRequest;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTests {
    private MemoryAuthDAO authDAO;
    private AuthChecker checker;
    private LogoutService logoutService;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();

        checker = new AuthChecker(authDAO);
        logoutService = new LogoutService(checker, authDAO);
    }

    @Test
    void logoutSuccessfully() throws Exception {
        String authToken = "authToken";
        authDAO.createAuth(new AuthData(authToken, "username"));

        LogoutRequest request = new LogoutRequest(authToken);

        assertDoesNotThrow(() -> logoutService.logout(request));
        assertNull(authDAO.getAuth(authToken));
    }

    @Test
    void logoutWithInvalidTokenThrowsUnauthorizedException() {
        LogoutRequest request = new LogoutRequest("invalidToken");

        assertThrows(
                UnauthorizedException.class,
                () -> logoutService.logout(request)
        );
    }

    @Test
    void logoutWithMissingTokenThrowsUnauthorizedException() {
        LogoutRequest request = new LogoutRequest(null);

        assertThrows(
                UnauthorizedException.class,
                () -> logoutService.logout(request)
        );
    }
}
