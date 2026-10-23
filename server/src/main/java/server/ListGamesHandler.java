package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.*;
import dataaccess.DataAccessException;

public class ListGamesHandler {
    private final ListGamesService listGamesService;

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
    }

    public void handle(Context ctx) throws DataAccessException, UnauthorizedException {
        String authToken = ctx.header("authorization");
        ListGamesResult listGamesResult = listGamesService.listGames(new ListGamesRequest(authToken));
        ctx.status(200);
        ctx.result(new Gson().toJson(listGamesResult));
    }
}
