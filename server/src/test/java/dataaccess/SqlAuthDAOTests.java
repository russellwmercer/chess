package dataaccess;

import dataaccess.sql.SqlAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SqlAuthDAOTests {
    private SqlAuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new SqlAuthDAO();
        authDAO.clear(); // clear - start the tests with an empty database.
    }

    @Test
    void addAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokenTest","usernameTest"));
        AuthData found = authDAO.getAuth("tokenTest");
        assertNotNull(found);
        assertEquals("tokenTest", found.authToken());
    }

    @Test
    void addAuthDuplicate() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokenTest","usernameTest"));
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(new AuthData("tokenTest","usernameTest")));
    }

    @Test
    void findAuthMissing() throws DataAccessException {
        assertNull(authDAO.getAuth("Nonexist"));
    }

    @Test
    void findAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokenTest","usernameTest"));
        AuthData found = authDAO.getAuth("tokenTest");

        assertNotNull(found);
        assertEquals("tokenTest", found.authToken());
        assertEquals("usernameTest", found.username());
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokenTest","usernameTest"));
        authDAO.createAuth(new AuthData("tokenTest2","usernameTest2"));
        authDAO.deleteAuth("tokenTest");

        assertNull(authDAO.getAuth("tokenTest"));
        assertNotNull(authDAO.getAuth("tokenTest2"));
    }

    @Test
    void deleteAuthNegativeMissing() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokenTest2","usernameTest2"));
        assertDoesNotThrow(() -> authDAO.deleteAuth("tokenTest"));
        assertNotNull(authDAO.getAuth("tokenTest2"));
    }

    @Test
    void clearAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokenTest","usernameTest"));
        authDAO.createAuth(new AuthData("tokenTest2","usernameTest2"));
        authDAO.clear();

        assertNull(authDAO.getAuth("tokenTest"));
        assertNull(authDAO.getAuth("tokenTest2"));
    }

}