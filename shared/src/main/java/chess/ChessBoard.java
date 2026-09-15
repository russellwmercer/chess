package chess;

import java.util.Arrays;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
        ChessPiece[][] squares = new ChessPiece[8][8];
        /** Saying 8 in this case is an allocation size - not an index, that's why we don't do 7 and 7.
         * It's like saying - give me space for 8 pieces (0-7).
         */

        public ChessBoard() {
        }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }
    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //Rather than setting each middle position to null, it is more effective to just reset the board.
        //I originally tried to create a new ChessBoard[] - but that would create a whole new object, rather than the change the one I'm editing.
        //This sets the pointer for squares to a new array of pieces.
        this.squares = new ChessPiece[8][8];
        //Build the white team:
        //Rooks:
        addPiece((new ChessPosition(1,1)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK) ));
        addPiece((new ChessPosition(1,8)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK) ));
        //Knights:
        addPiece((new ChessPosition(1,2)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT) ));
        addPiece((new ChessPosition(1,7)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT) ));
        //Bishops:
        addPiece((new ChessPosition(1,3)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP) ));
        addPiece((new ChessPosition(1,6)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP) ));
        //King:
        addPiece((new ChessPosition(1,5)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING) ));
        //Queen:
        addPiece((new ChessPosition(1,4)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN) ));
        //Pawns:
        for (int i = 1; i <= 8; i++) {
            addPiece((new ChessPosition(2,i)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN) ));
        }

        //Build the black team:
        //Rooks:
        addPiece((new ChessPosition(8,1)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK) ));
        addPiece((new ChessPosition(8,8)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK) ));
        //Knights:
        addPiece((new ChessPosition(8,2)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT) ));
        addPiece((new ChessPosition(8,7)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT) ));
        //Bishops:
        addPiece((new ChessPosition(8,3)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP) ));
        addPiece((new ChessPosition(8,6)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP) ));
        //King:
        addPiece((new ChessPosition(8,5)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING) ));
        //Queen:
        addPiece((new ChessPosition(8,4)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN) ));
        //Pawns:
        for (int i = 1; i <= 8; i++) {
            addPiece((new ChessPosition(7, i)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)));
        }
    }

    /**
     * @return a string that is an accurate representation of the chess board.
     * Uses a StringBuilder object, which is helpful because you can use .append() and .toString() when finished.
     * Iterate through all of the rows and columns, with letters for each string (from getPieceLetter) and space for blanks.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=7; i >=0; i--) {
            for (int j=0; j <= 7; j++) {
                if (squares[i][j] == null) {
                    sb.append(" ");
                }
                else {
                    sb.append(squares[i][j].getPieceLetter());
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     *
     * @param o - another chess board to compare.
     * @return boolean on if they are equal.
     * Uses deepEquals, which iterates through each row and possible position.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) { return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessBoard other = (ChessBoard) o;
        //deepEquals is in java.util.Arrays - iterates through all of the parts of them to see if they're accurate.
        return Arrays.deepEquals(this.squares, other.squares);
    }

    /**
     * @return integer hashcode.
     * Arrays also has a deepHashCode function, which iterates through NESTED arrays. - a 2d board.
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.squares);
    }
}
