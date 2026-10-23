package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.UserData;
import model.AuthData;

import java.util.UUID;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;


    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException, AlreadyTakenException, DataAccessException {
        // Error: is a bad request
        if (registerRequest.username() == null || registerRequest.email() == null || registerRequest.password() == null ) {
            throw new BadRequestException("bad request");
        }
        // Error: the name is already taken.
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException("already taken");
        }

        userDAO.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));

        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, registerRequest.username()));

        return new RegisterResult(registerRequest.username(), authToken);
    }
}

