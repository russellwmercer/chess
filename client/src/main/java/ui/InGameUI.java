package ui;

import chess.*;

public class InGameUI {
    private final ChessBoard board;
    private final ChessGame.TeamColor perspectiveColor;

    public InGameUI(ChessBoard board, ChessGame.TeamColor perspectiveColor) {
        this.board = board;
        this.perspectiveColor = perspectiveColor;
    }

    public void drawBoard() {
        if (perspectiveColor == ChessGame.TeamColor.WHITE) { //PERSPECTIVE - not each piece color; white or observer
            System.out.println(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_YELLOW +
                    "    A  B  C  D  E  F  G  H    " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            for (int row = 8; row >= 1; row--) {
                System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_YELLOW + " " + row + " ");
                for (int col = 1; col <= 8; col++) {
                    printSquare(row, col);
                }
                System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_YELLOW + " " + row + " ");
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                System.out.println();
            }
            System.out.println(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_YELLOW +
                    "    A  B  C  D  E  F  G  H    " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        } else { // Black perspective
            System.out.println(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_YELLOW +
                    "    H  G  F  E  D  C  B  A    " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            for (int row = 1; row <= 8; row++) {
                System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_YELLOW + " " + row + " ");
                for (int col = 8; col >= 1; col--) {
                    printSquare(row, col);
                }
                System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_YELLOW + " " + row + " ");
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                System.out.println();
            }
            System.out.println(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_YELLOW +
                    "    H  G  F  E  D  C  B  A    " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        }
    }

    private void printSquare(int row, int col) {
        ChessPosition boardPosition = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(boardPosition);
        if (!((row + col) % 2 == 0)){ // Odd - means the background of the square should be white.
            System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
        } else { // Only other option is even - square should be black
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        }
        if (piece == null) {
            System.out.print("   ");
        } else {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) { // Piece color, not perspective
                System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
            } else { // Only other option is black
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
            }
            System.out.print(" " + piece.getPieceLetter() + " ");
        }
    }
}
