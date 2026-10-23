package server;

import dataaccess.*;
import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        AuthChecker checker = new AuthChecker(authDAO);

        ClearService clearService = new ClearService(gameDAO, userDAO, authDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);

        RegisterService registerService = new RegisterService(userDAO, authDAO);
        RegisterHandler registerHandler = new RegisterHandler(registerService);

        LoginService loginService = new LoginService(userDAO, authDAO);
        LoginHandler loginHandler = new LoginHandler(loginService);

        LogoutService logoutService = new LogoutService(checker, authDAO);
        LogoutHandler logoutHandler = new LogoutHandler(logoutService);

        ListGamesService listGamesService = new ListGamesService(checker, gameDAO);
        ListGamesHandler listGamesHandler = new ListGamesHandler(listGamesService);

        //Javlin paths
        javalin.post("/user", registerHandler::handle);
        javalin.delete("/db", clearHandler::handle);
        javalin.post("/session", loginHandler::handle);
        javalin.delete("/session", logoutHandler::handle);
        javalin.get("/game", listGamesHandler::handle);

        //Exceptions - one per exception type.
        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        });
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        });
        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        });
        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
