package service;

import dataaccess.DataAccessException;
import dataaccess.AuthDAO;

public class LogoutService {
    private final AuthDAO authDAO;


    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException {
        // Error: unauthorized 401
        if (logoutRequest.authToken() == null || authDAO.getAuth(logoutRequest.authToken()) == null) {
            throw new UnauthorizedException("unauthorized");
        }

        authDAO.deleteAuth(logoutRequest.authToken());
    }
}

