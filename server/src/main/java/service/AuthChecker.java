package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class AuthChecker {
    private final AuthDAO authDAO;

    public AuthChecker(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData verify(String authToken) throws UnauthorizedException, DataAccessException {
        if (authToken == null) {
            throw new UnauthorizedException("unauthorized");
        }

        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new UnauthorizedException("unauthorized");
        }

        return auth;
    }
}
