package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import requestresult.CreateGameRequest;
import requestresult.CreateGameResult;
import service.*;
import dataaccess.DataAccessException;

public class CreateGameHandler {
    private final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    public void handle(Context ctx) throws DataAccessException, UnauthorizedException, BadRequestException {
        String authToken = ctx.header("authorization");
        Gson gson = new Gson();
        CreateGameRequest gameRequest = gson.fromJson(ctx.body(), CreateGameRequest.class);
        if (gameRequest == null) {
            throw new BadRequestException("bad request");
        }
        String gameName = gameRequest.gameName();
        CreateGameResult createGameResult = createGameService.createGame(new CreateGameRequest(authToken, gameName));
        ctx.status(200);
        ctx.result(gson.toJson(createGameResult));
    }
}
