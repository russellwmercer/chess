package server;

import io.javalin.http.Context;
import service.LogoutService;
import service.LogoutRequest;
import dataaccess.DataAccessException;
import service.UnauthorizedException;

public class LogoutHandler {
    private final LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    public void handle(Context ctx) throws DataAccessException, UnauthorizedException {
        String authToken = ctx.header("authorization");
        logoutService.logout(new LogoutRequest(authToken));
        ctx.status(200);
        ctx.result("{}");
    }
}
