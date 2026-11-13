package ui;

import serverfacade.*;

import java.util.Scanner;

public class REPL {
    private final ServerFacade facade;
    private final PreLoginMenu preLoginMenu;
    private final PostLoginMenu postLoginMenu;
    private ClientStateEnum state = ClientStateEnum.PRELOGIN; // Starts as a prelogin

    public REPL(int port) {
        facade = new ServerFacade(port);
        preLoginMenu = new PreLoginMenu(facade);
        postLoginMenu = new PostLoginMenu(facade);

    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLUE +
                "Welcome to the Chess server. Type a command to get started (try 'help').");
        while (true) { //Does this loop over and over (the REPL) until someone types quit command
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + "[" + EscapeSequences.SET_TEXT_COLOR_WHITE
                    + state + EscapeSequences.SET_TEXT_COLOR_BLUE + "] " + EscapeSequences.SET_TEXT_COLOR_YELLOW
                    + ">>> ");
            String[] words = scanner.nextLine().trim().split("\\s+");
            String command = words[0].toLowerCase();
            if (command.equals("quit")) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Leaving the chess client...");
                break;
            }
            state = switch (state) {
                case PRELOGIN -> preLoginMenu.handle(command, words);
                case POSTLOGIN -> postLoginMenu.handle(command, words);
            };
        }
    }
}
