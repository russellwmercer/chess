package server;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.http.Context;
import requestresult.JoinGameRequest;
import service.*;
import dataaccess.DataAccessException;

public class JoinGameHandler {
    private final JoinGameService joinGameService;

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }

    public void handle(Context ctx) throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        String authToken = ctx.header("authorization");
        Gson gson = new Gson();
        JoinGameRequest gameRequest = gson.fromJson(ctx.body(), JoinGameRequest.class);
        if (gameRequest == null) {
            throw new BadRequestException("bad request");
        }
        Integer gameID = gameRequest.gameID();
        ChessGame.TeamColor teamColor = gameRequest.playerColor();
        joinGameService.joinGame(new JoinGameRequest(authToken, teamColor, gameID));
        ctx.status(200);
        ctx.result("{}");
    }
}
