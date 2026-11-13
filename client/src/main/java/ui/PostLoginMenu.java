package ui;

import requestresult.*;
import serverfacade.*;

public class PostLoginMenu {
    private final ServerFacade facade;

    public PostLoginMenu(ServerFacade facade) {
        this.facade = facade;
    }

    // Handles any prelogin command and then chooses the right helper func.
    public ClientStateEnum handle(String command, String[] words) {
        return ClientStateEnum.PRELOGIN; //If no other switch happened, then it needs to stay as pre login.
    }
}