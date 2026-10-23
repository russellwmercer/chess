package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
import service.*;
import dataaccess.DataAccessException;

public class RegisterHandler {
    private final RegisterService registerService;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    public void handle(Context ctx) throws DataAccessException, BadRequestException, AlreadyTakenException {
        Gson gson = new Gson();

        RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);

        RegisterResult result = registerService.register(request);
        ctx.status(200);
        ctx.result(gson.toJson(result));
    }
}
