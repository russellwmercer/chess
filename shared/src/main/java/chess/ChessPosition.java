package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;

    // This is the constructor: It is used to instantiate the class.
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {return false;}
        if (o == this) {return true;}
        ChessPosition other = (ChessPosition) o;
        return (other.getRow() == this.row && other.getColumn() == this.col);
    }

    /** Override the hashCode from the class Objects. Note that Objects is different than the Object super class.
     .hash() returns a unique number that resembles whatever you pass into it. So it should have the definiting characteristics as variables.
     Objects is a later class that adds things like equals() and hashCode().
     Note that we use Objects. because we are calling the class.
     In Equals, as shown above Object refers to the type of thing beign passed into the function.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

}
