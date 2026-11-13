package ui;

import chess.ChessGame;
import model.GameData;
import serverfacade.ServerFacade;
import serverfacade.ServerFacadeException;
import java.util.ArrayList;
import java.util.List;

public class PostLoginMenu {
    private final ServerFacade facade;
    private List<GameData> listGames = new ArrayList<>();

    public PostLoginMenu(ServerFacade facade) {
        this.facade = facade;
    }

    // Handles any ostlogin command and then chooses the right helper func.
    public ClientStateEnum handle(String command, String[] words) {
        switch (command) {
            case "help" -> printHelp();
            case "create" -> {return createMenu(words);}
            case "list" -> listMenu();
            case "join" -> joinMenu(words);
            case "observe" -> {return observeMenu(words);}
            case "logout" -> {return logoutMenu();}
            default -> System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Unknown command." +
                    " Type help to see options." + EscapeSequences.SET_TEXT_COLOR_YELLOW);
        }
        return ClientStateEnum.POSTLOGIN; // If no switch, it stays the same.
    }

    private ClientStateEnum logoutMenu() {
        try {
            facade.logout();
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Signed out.");
            return ClientStateEnum.PRELOGIN;
        } catch (ServerFacadeException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage());
            return ClientStateEnum.POSTLOGIN; // If it fails they're still logged in.
        }
    }

    private void listMenu() {
        try {
            listGames = new ArrayList<>(facade.listGames().games());
            if (listGames.isEmpty()) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "No games yet.");
            }
            for (int i = 0; i < listGames.size(); i++) {
                GameData game = listGames.get(i);
                System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + (i+1) + ". " +
                        EscapeSequences.SET_TEXT_COLOR_BLUE + game.gameName() + " - White User: " +
                        EscapeSequences.SET_TEXT_COLOR_YELLOW + userOrEmptyString(game.whiteUsername()) +
                        EscapeSequences.SET_TEXT_COLOR_BLUE + " - Black User: " +
                        EscapeSequences.SET_TEXT_COLOR_YELLOW + userOrEmptyString(game.blackUsername()));
            }
        } catch (ServerFacadeException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage());
        }
    }

    // A helper that just returns GameData from a number. Used by observe and join commands
    private GameData gameDataFromNumber(String typedNumber) { //Starts a string because its from words[]
        int number;
        try {
            number = Integer.parseInt(typedNumber);
        } catch (NumberFormatException e) { //A common exception for if the number is wrong
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "That is not a valid number. " +
                    "Type list to see game options");
            return null;
        }
        if (number < 1 || number > listGames.size()) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "No game with that number. " +
                    "Type list to see game options");
            return null;
        }
        return listGames.get(number - 1);
    }

    private ChessGame.TeamColor colorFromWord(String word) {
        return switch (word.toLowerCase()) {
            case "white", "w" -> ChessGame.TeamColor.WHITE;
            case "black", "b" -> ChessGame.TeamColor.BLACK;
            default -> null;
        };
    }

    private String userOrEmptyString(String username) {
        if (username == null) {
            return "Empty";
        } else {
            return username;
        }
    }

    private void joinMenu(String[] words) {
        if (words.length != 3) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Usage: join <NUMBER> <WHITE|BLACK>");
            return;
        }
        GameData game = gameDataFromNumber(words[1]);
        if (game == null) {
            return;
        } // There is no game number, the helper already sent an error message to terminal
        ChessGame.TeamColor color = colorFromWord(words[2]);
        if (color == null) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Format of team color must be 'white'," +
                    " 'w', 'black', or 'b'.");
            return;
        }

        try {
            facade.joinGame(color, game.gameID());
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Joined game " +
                    EscapeSequences.SET_TEXT_COLOR_YELLOW + game.gameName() +
                    EscapeSequences.SET_TEXT_COLOR_GREEN + " as " + EscapeSequences.SET_TEXT_COLOR_YELLOW
                    + color);
        } catch (ServerFacadeException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Since you are in the"
                + EscapeSequences.SET_TEXT_COLOR_WHITE + " POSTLOGIN " + EscapeSequences.SET_TEXT_COLOR_BLUE
                + "state, here are the available commands:" + EscapeSequences.SET_TEXT_COLOR_YELLOW);
        System.out.println("create <NAME> - Creates a chess game under a new name");
        System.out.println("list - list the active games in order of number");
        System.out.println("join <NUMBER> <WHITE|BLACK> - join an active game");
        System.out.println("observe <NUMBER> - watch an active game");
        System.out.println("help - displays available commands");
        System.out.println("logout - logs out of the Chess server");
    }
}