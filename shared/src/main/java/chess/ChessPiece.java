package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;

    //Constructor
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        ChessPiece other = (ChessPiece) o; // This line tells the compiler that the object passed into the function in this case is a ChessPiece.
        return (other.getPieceType() == this.getPieceType() && other.getTeamColor() == this.getTeamColor()); // Make sure not to forget one of the object properties.
    }

    /**
     * Uses Objects built in .hash() function to create a hashCode for the pieceColor and pieceType.
     * @return an int hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    /**
     * Used by ChessBoard.java for generating strings of what the board looks like.
     * @return a letter that signifies the pieceType and pieceColor.
     */
    public String getPieceLetter() {
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            return switch (pieceType) {
                case KING -> "K";
                case QUEEN -> "Q";
                case ROOK -> "R";
                case KNIGHT -> "N";
                case BISHOP -> "B";
                case PAWN -> "P";
            };
        }
        else {
            return switch (pieceType) {
                case KING -> "k";
                case QUEEN -> "q";
                case ROOK -> "r";
                case KNIGHT -> "n";
                case BISHOP -> "b";
                case PAWN -> "p";
            };
        }
    }
}
