package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private ChessMove previousMove;
    // Flags to track if any of the pieces have moved for Castling:
    private boolean whiteKingMove;
    private boolean whiteKingsideRookMove;
    private boolean whiteQueensideRookMove;
    private boolean blackKingMove;
    private boolean blackKingsideRookMove;
    private boolean blackQueensideRookMove;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
        previousMove = null;
        whiteKingMove = false;
        whiteKingsideRookMove = false;
        whiteQueensideRookMove = false;
        blackKingMove = false;
        blackKingsideRookMove = false;
        blackQueensideRookMove = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece pieceOfInterest = board.getPiece(startPosition);
        if (pieceOfInterest == null) {
            return null;
        }
        Collection<ChessMove> moves = pieceOfInterest.pieceMoves(board, startPosition);
        // Logic to check if it is valid to enpassant.
        if (pieceOfInterest.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessMove enpassantMove = enpassant(startPosition, pieceOfInterest);
            if (enpassantMove != null) {
                moves.add(enpassantMove);
            }
        }
        // Logic to check if it is valid to castle.
        if (pieceOfInterest.getPieceType() == ChessPiece.PieceType.KING) {
            moves.addAll(castle(startPosition, pieceOfInterest));
        }
        // Goes through every hypothetical move and makes sure that the new one is not in check.
        for (ChessMove move : moves) {
            ChessBoard hypotheticalBoard = applyMoves(board, move);
            if (!isInCheckHelper(hypotheticalBoard, pieceOfInterest.getTeamColor())) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> castle(ChessPosition startPosition, ChessPiece king) {
        Collection<ChessMove> castleMoves = new ArrayList<>();
        if (isInCheck(king.getTeamColor())) {return castleMoves;}
        if (king.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (!whiteKingMove && !whiteKingsideRookMove && kingAt(1,5, king.getTeamColor()) && rookAt(1,8, king.getTeamColor()) && pathOpen(1,6, 7) && checkKingPath(1, 6, 7, king.getTeamColor())) {
                castleMoves.add(new ChessMove(startPosition, new ChessPosition(1, 7), null));
            }
            if (!whiteKingMove && !whiteQueensideRookMove && kingAt(1,5, king.getTeamColor()) && rookAt(1,1, king.getTeamColor()) && pathOpen(1,2, 4) && checkKingPath(1, 3, 4, king.getTeamColor())) {
                castleMoves.add(new ChessMove(startPosition, new ChessPosition(1, 3), null));
            }
        } else {
            if (!blackKingMove && !blackKingsideRookMove && kingAt(8,5, king.getTeamColor()) && rookAt(8,8, king.getTeamColor()) && pathOpen(8,6, 7) && checkKingPath(8, 6, 7, king.getTeamColor())) {
                castleMoves.add(new ChessMove(startPosition, new ChessPosition(8, 7), null));
            }
            if (!blackKingMove && !blackQueensideRookMove && kingAt(8,5, king.getTeamColor()) && rookAt(8,1, king.getTeamColor()) && pathOpen(8,2, 4) && checkKingPath(8, 3, 4, king.getTeamColor())) {
                castleMoves.add(new ChessMove(startPosition, new ChessPosition(8, 3), null));
            }
        }
        return castleMoves;
    }

    // Determines if a rook is at a certain position, for castling.
    private boolean rookAt(int row, int col, TeamColor color) {
        ChessPiece inhabitant = board.getPiece(new ChessPosition(row, col));
        if (inhabitant == null) {return false;}
        return (inhabitant.getPieceType() == ChessPiece.PieceType.ROOK && inhabitant.getTeamColor() == color);
    }

    // Determines if the king is at a certain position, for castling.
    private boolean kingAt(int row, int col, TeamColor color) {
        ChessPiece inhabitant = board.getPiece(new ChessPosition(row, col));
        if (inhabitant == null) {return false;}
        return (inhabitant.getPieceType() == ChessPiece.PieceType.KING && inhabitant.getTeamColor() == color);
    }

    // A helper function to make sure the path is open during castling
    private boolean pathOpen(int row, int start, int end) {
        for (int col = start; col <= end; col++) {
            if (board.getPiece(new ChessPosition(row,col)) != null) {
                return false;
            }
        }
        return true;
    }

    // A helper function to make sure the path is not in check during castling
    private boolean checkKingPath(int row, int start, int end, TeamColor color) {
        for (int col = start; col <= end; col++) {
            ChessMove hypotheticalMove = new ChessMove(new ChessPosition(row,5), new ChessPosition(row,col), null);
            ChessBoard hypothetical = applyMoves(board, hypotheticalMove);
            if (isInCheckHelper(hypothetical, color)) {
                return false;
            }
        }
        return true;
    }

    // Helper function that creates a move for the pawn if it can enpassant.
    private ChessMove enpassant(ChessPosition startPosition, ChessPiece attackingPawn) {
        if (previousMove == null) {return null;}

        ChessPiece lastMover = board.getPiece(previousMove.getEndPosition());
        if (lastMover == null) {return null;}
        if (lastMover.getPieceType() != ChessPiece.PieceType.PAWN) {return null;}
        if (lastMover.getTeamColor() == attackingPawn.getTeamColor()) {return null;}

        int rowsMoved = Math.abs(previousMove.getEndPosition().getRow() - previousMove.getStartPosition().getRow());
        if (rowsMoved != 2) {
            return null;
        }
        int columnDifference = Math.abs(previousMove.getEndPosition().getColumn() - startPosition.getColumn());
        int rowDifference = Math.abs(previousMove.getEndPosition().getRow() - startPosition.getRow());
        if (rowDifference != 0 || columnDifference != 1) {
            return null;
        }
        int enpassantEndRow = ((previousMove.getStartPosition().getRow() + previousMove.getEndPosition().getRow()) / 2);
        return new ChessMove(startPosition, new ChessPosition(enpassantEndRow, previousMove.getEndPosition().getColumn()), null);
    }

    //Helper function that returns a board of the move done.
    private ChessBoard applyMoves(ChessBoard board, ChessMove move) {
        ChessBoard hypothetical = board.copy();
        ChessPiece mover = board.getPiece(move.getStartPosition());

        if (mover == null) {
            return hypothetical;
        }
        if (move.getPromotionPiece() != null) {
            mover = new ChessPiece(mover.getTeamColor(), move.getPromotionPiece());
        }

        hypothetical.addPiece(move.getStartPosition(), null);
        hypothetical.addPiece(move.getEndPosition(), mover);

        return hypothetical;
    }
    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        if (movingPiece == null) {
            throw new InvalidMoveException(move + "- There is no piece at the start position.");
        } else if (movingPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException(move + "- The wrong team is trying to move.");
        } else if (validMoves == null  || !validMoves.contains(move)) {
            throw new InvalidMoveException(move + "is not in the list of valid moves.");
        } else {
            // Logic for enpassant with pawns, specifically.
            if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(move.getEndPosition()) == null && move.getEndPosition().getColumn() != move.getStartPosition().getColumn()) {
                if (movingPiece.getTeamColor() == TeamColor.WHITE) {
                    board.addPiece(new ChessPosition(move.getEndPosition().getRow()-1, move.getEndPosition().getColumn()),null);
                } else {
                    board.addPiece(new ChessPosition(move.getEndPosition().getRow()+1, move.getEndPosition().getColumn()),null);
                }
            }
            // Logic for castling. - Keeping track on if any of these have moved.
            if (touches(move, 1, 5)) {whiteKingMove = true;}
            if (touches(move, 1, 8)) {whiteKingsideRookMove = true;}
            if (touches(move, 1, 1)) {whiteQueensideRookMove = true;}
            if (touches(move, 8, 5)) {blackKingMove = true;}
            if (touches(move, 8, 8)) {blackKingsideRookMove = true;}
            if (touches(move, 8, 1)) {blackQueensideRookMove = true;}

            // Successful turn logic
            teamTurn = (teamTurn == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
            board = applyMoves(board, move);
            // Useful for game history, particularly for doing the enpassant logic.
            previousMove = move;
        }
    }

    private boolean touches(ChessMove move, int row, int col) {
        int moveRowStart = move.getStartPosition().getRow();
        int moveRowEnd = move.getEndPosition().getRow();
        int moveColStart = move.getStartPosition().getColumn();
        int moveColEnd = move.getEndPosition().getColumn();
        return ((moveRowStart == row && moveColStart == col) || (moveRowEnd == row && moveColEnd == col));
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckHelper(board, teamColor);
    }

    //Created this because I cannot refactor or change teh parameters passed into isInCheck.
    private boolean isInCheckHelper(ChessBoard board, TeamColor teamColor) {
        ChessPosition kingPosition = findPiece(board, ChessPiece.PieceType.KING, teamColor);
        if (kingPosition == null) {return false;}
        Collection<ChessMove> opponentMoves = allOpponentMoves(board, teamColor);
        for (ChessMove opponentMove : opponentMoves) {
            if (opponentMove.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private ChessPosition findPiece(ChessBoard board, ChessPiece.PieceType piece, TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition possiblePosition = new ChessPosition(i, j);
                ChessPiece inhabitant = board.getPiece(possiblePosition);
                if (inhabitant != null && inhabitant.getPieceType() == piece && inhabitant.getTeamColor() == teamColor) {
                    return possiblePosition;
                }
            }
        }
        return null; //No king found on the board - helped for Edge Cases.
    }

    // Pass IN the TEAM color - it finds opponent moves.
    private Collection<ChessMove> allOpponentMoves(ChessBoard board, TeamColor teamColor) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece inhabitant = board.getPiece(position);
                if (inhabitant != null && inhabitant.getTeamColor() != teamColor) {
                    allMoves.addAll(inhabitant.pieceMoves(board,position));
                }
            }
        }
        return allMoves;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (!booleanValidMovesHelper(teamColor) && isInCheck(teamColor));
    }

    // Pass IN the TEAM color - it finds if there are valid moves that can be done.
    private boolean booleanValidMovesHelper(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece inhabitant = board.getPiece(position);
                if (inhabitant != null && inhabitant.getTeamColor() == teamColor) {
                    if (!validMoves(position).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (!booleanValidMovesHelper(teamColor) && !isInCheck(teamColor));
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        previousMove = null;
        this.board = board.copy();
        // Reset the flags for the Castling.
        whiteKingMove = false;
        whiteKingsideRookMove = false;
        whiteQueensideRookMove = false;
        blackKingMove = false;
        blackKingsideRookMove = false;
        blackQueensideRookMove = false;
    }
    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard(){
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        ChessGame other = (ChessGame) o;
        return (this.board.equals(other.board) && this.teamTurn == other.teamTurn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

}
