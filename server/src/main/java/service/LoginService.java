package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.UserData;
import model.AuthData;
import requestresult.LoginRequest;
import requestresult.LoginResult;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;


    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException {
        // Error: is a bad request 400
        if (loginRequest.username() == null || loginRequest.password() == null ) {
            throw new BadRequestException("bad request");
        }

        UserData user = userDAO.getUser(loginRequest.username());
        // Error: the username does not exist. 401
        if (user == null) {
            throw new UnauthorizedException("unauthorized");
        }
        // Error: the username exists, but the password is incorrect. 401
        if (!loginRequest.password().equals(user.password())) {
            throw new UnauthorizedException("unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, loginRequest.username()));

        return new LoginResult(loginRequest.username(), authToken);
    }
}

