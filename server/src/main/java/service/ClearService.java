package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

public class ClearService {
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;


    public ClearService(GameDAO gameDAO, UserDAO userDAO,AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clearAllData() throws DataAccessException {
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }
}
