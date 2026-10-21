package server;

import dataaccess.*;
import io.javalin.*;
import service.ClearService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        ClearService clearService = new ClearService(gameDAO, userDAO, authDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin.delete("/db", clearHandler::handle);
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
