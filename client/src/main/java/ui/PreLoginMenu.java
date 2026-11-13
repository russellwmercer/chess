package ui;

import requestresult.*;
import serverfacade.*;

public class PreLoginMenu {
    private final ServerFacade facade;

    public PreLoginMenu(ServerFacade facade) {
        this.facade = facade;
    }

    // Handles any prelogin command and then chooses the right helper func.
    public ClientStateEnum handle(String command, String[] words) {
        switch (command) {
            case "help" -> printHelp();
            case "login" -> {return logIn(words);}
            case "register" -> {return registerMenu(words);}
            default -> System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Unknown command." +
                    " Type help to see options." + EscapeSequences.SET_TEXT_COLOR_YELLOW);
        }
        return ClientStateEnum.PRELOGIN; //If no other switch happened, then it needs to stay as pre login.
    }

    private void printHelp() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Since you are in the"
                + EscapeSequences.SET_TEXT_COLOR_WHITE + " PRELOGIN " + EscapeSequences.SET_TEXT_COLOR_BLUE
                + "state, here are the available commands:" + EscapeSequences.SET_TEXT_COLOR_YELLOW);
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - creates an account");
        System.out.println("login <USERNAME> <PASSWORD> - logs in to the server");
        System.out.println("help - displays available commands");
        System.out.println("quit - closes the menu");
    }

    // Helper for login
    private ClientStateEnum logIn(String[] words) {
        // Catches it if its not the right length. If its not, then it cant be right syntax.
        // Also if it is right length then in this case it is correct syntax.
        if (words.length != 3) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Usage: login <username> <password>"
                    + EscapeSequences.SET_TEXT_COLOR_YELLOW);
            return ClientStateEnum.PRELOGIN;
        }
        try {
            facade.login(new LoginRequest(words[1], words[2]));
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Signed in. Welcome, "
                    + words[1] + "!" + EscapeSequences.SET_TEXT_COLOR_YELLOW);
            return ClientStateEnum.POSTLOGIN;
        } catch (ServerFacadeException e) { // If login fails it goes to this catch block.
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_YELLOW);
            return ClientStateEnum.PRELOGIN;
        }
    }

    private ClientStateEnum registerMenu(String[] words) {
        if (words.length != 4) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Usage: register <username>" +
                    " <password> <email>" + EscapeSequences.SET_TEXT_COLOR_YELLOW);
            return ClientStateEnum.PRELOGIN;
        }
        if (!words[3].contains("@")) { // No email verification for real, but at least check for an @
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "The email must contain an @." +
                    EscapeSequences.SET_TEXT_COLOR_YELLOW);
            return ClientStateEnum.PRELOGIN;
        }
        try {
            facade.register(new RegisterRequest(words[1], words[2], words[3]));
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Welcome to Chess, " + words[1] +
                    "! You are registered and logged in." + EscapeSequences.SET_TEXT_COLOR_YELLOW);
            return ClientStateEnum.POSTLOGIN;
        } catch (ServerFacadeException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_YELLOW);
            return ClientStateEnum.PRELOGIN;
        }
    }
}
