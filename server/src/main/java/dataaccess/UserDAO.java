package dataaccess;

// Required for the UserDAO to work.
import model.UserData;

// The interface itself - specifies what each UserDAO must be like.
public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    void clear() throws DataAccessException;

}

