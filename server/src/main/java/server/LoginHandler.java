package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.*;
import dataaccess.DataAccessException;

public class LoginHandler {
    private final LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    public void handle(Context ctx) throws DataAccessException, BadRequestException, UnauthorizedException {
        Gson gson = new Gson();

        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);

        LoginResult result = loginService.login(request);
        ctx.status(200);
        ctx.result(gson.toJson(result));
    }
}
