package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import requestresult.CreateGameRequest;
import requestresult.CreateGameResult;

public class CreateGameService {
    private final GameDAO gameDAO;
    private final AuthChecker checker;


    public CreateGameService(AuthChecker checker, GameDAO gameDAO) {
        this.checker = checker;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UnauthorizedException, DataAccessException, BadRequestException {
        // 400 error
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("bad request");
        }
        // Error: unauthorized 401
        checker.verify(createGameRequest.authToken());

        GameData newGame = new GameData(0, null, null, createGameRequest.gameName(), new ChessGame());
        int newGameID = gameDAO.createGame(newGame);
        return new CreateGameResult(newGameID);
    }
}

