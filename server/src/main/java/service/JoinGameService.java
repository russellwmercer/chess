package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import requestresult.JoinGameRequest;

public class JoinGameService {
    private final GameDAO gameDAO;
    private final AuthChecker checker;

    public JoinGameService(AuthChecker checker, GameDAO gameDAO) {
        this.checker = checker;
        this.gameDAO = gameDAO;
    }

    public void joinGame(JoinGameRequest joinGameRequest)
            throws BadRequestException, UnauthorizedException, DataAccessException, AlreadyTakenException {
        // Check for 400 player color doesn't exist or missing game ID
        if (joinGameRequest.playerColor() == null || joinGameRequest.gameID() == null) {
            throw new BadRequestException("bad request");
        }
        // Check for 401, auth, keep the result this time because it will know about the user
        AuthData authData = checker.verify(joinGameRequest.authToken());
        String username = authData.username();

        // Check for 400 (game doesnt exist)
        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if (gameData == null) {
            throw new BadRequestException("bad request");
        }
        GameData result;
        if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE) {
            // If the request is white, and white is already taken.
            if (gameData.whiteUsername() != null) {
                throw new AlreadyTakenException("already taken");
            }
            result = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else {
            // If the request is black, and black is already taken.
            if (gameData.blackUsername() != null) {
                throw new AlreadyTakenException("already taken");
            }
            result = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
        }
        //Save the updated game to the database.
        gameDAO.updateGame(result);
    }
}

