package dataaccess;

import dataaccess.sql.SqlUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SqlUserDAOTests {
    private SqlUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SqlUserDAO();
        userDAO.clear(); // clear - start the tests with an empty database.
    }

    @Test
    void addUserSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("usernameTest", "passwordTest", "test@test"));
        UserData found = userDAO.getUser("usernameTest");
        assertNotNull(found);
        assertEquals("usernameTest", found.username());
    }

    @Test
    void addUserDuplicate() throws DataAccessException {
        userDAO.createUser(new UserData("usernameTest", "passwordTest", "test@test"));
        assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData("usernameTest", "passwordTest", "test@test")));
    }

    @Test
    void findUserMissing() throws DataAccessException {
        assertNull(userDAO.getUser("Nonexist"));
    }

    @Test
    void findUserSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("usernameTest", "passwordTest", "test@test"));
        UserData found = userDAO.getUser("usernameTest");

        assertNotNull(found);
        assertEquals("usernameTest", found.username());
        assertEquals("passwordTest", found.password());
        assertEquals("test@test", found.email());
    }

    @Test
    void clearUserSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("usernameTest", "passwordTest", "test@test"));
        userDAO.createUser(new UserData("usernameTest2", "passwordTest2", "test2@test"));
        userDAO.clear();

        assertNull(userDAO.getUser("usernameTest"));
        assertNull(userDAO.getUser("usernameTest2"));
    }

}