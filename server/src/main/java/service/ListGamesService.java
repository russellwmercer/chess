package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import requestresult.ListGamesRequest;
import requestresult.ListGamesResult;

import java.util.List;

public class ListGamesService {
    private final GameDAO gameDAO;
    private final AuthChecker checker;


    public ListGamesService(AuthChecker checker, GameDAO gameDAO) {
        this.checker = checker;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws UnauthorizedException, DataAccessException {
        // Error: unauthorized 401
        checker.verify(listGamesRequest.authToken());
        List<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }
}

