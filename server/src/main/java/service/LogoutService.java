package service;

import dataaccess.DataAccessException;
import dataaccess.AuthDAO;

public class LogoutService {
    private final AuthDAO authDAO;
    private final AuthChecker checker;


    public LogoutService(AuthChecker checker, AuthDAO authDAO) {
        this.checker = checker;
        this.authDAO = authDAO;
    }

    public void logout(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException {
        // Error: unauthorized 401
        checker.verify(logoutRequest.authToken());
        authDAO.deleteAuth(logoutRequest.authToken());
    }
}

