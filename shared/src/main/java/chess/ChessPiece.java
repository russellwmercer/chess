package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

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
     * @return Collection of valid moves - NOT an array, since it is not always a fixed length.
     */
    //Generic syntax reminder: Collection<> -> the <> refers to what the collection holds.
    //To create a collection of valid moves, I created helper functions to aid with debugging.
    //I could also create them as separate classes, that would be difficult in a timed environment with constructors, etc.
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (pieceType) {
            case KING -> jumpMoves(board, myPosition, new int[][] {{1,0}, {0,1}, {-1,0}, {0,-1}, {-1,1}, {1,1}, {-1,-1}, {1,-1}});
            case QUEEN -> directionalMovement(board, myPosition, new int[][] {{-1,-1}, {1,1}, {-1,1}, {1,-1}, {-1,0}, {1,0}, {0,1}, {0,-1}});
            case BISHOP -> directionalMovement(board, myPosition, new int[][] {{-1,-1}, {1,1}, {-1,1}, {1,-1}});
            case KNIGHT -> jumpMoves(board, myPosition, new int[][] {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,-2}, {1,2}, {-1,-2}, {-1,2}});
            case ROOK -> directionalMovement(board, myPosition, new int[][] {{-1,0}, {1,0}, {0,1}, {0,-1}});
            case PAWN -> pawn(board, myPosition);
        };
    }

    private Collection<ChessMove> jumpMoves(ChessBoard board, ChessPosition myPosition, int[][] offsets) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] offset : offsets) {
            int newRow = myPosition.getRow() + offset[0];
            int newCol = myPosition.getColumn() + offset[1];

            if (newRow < 1 | newRow > 8 | newCol < 1 | newCol > 8) {
                continue;
            }

            ChessPosition possible_space = new ChessPosition(newRow, newCol);
            ChessPiece inhabitant = board.getPiece(possible_space);

            //I originally had inhabitant == null as the second choice - this was causing errors.
            //Null must be first, because otherwise you cannot call .getTeamColor() on it, and it will fail to compile.
            if (inhabitant == null) {
                moves.add(new ChessMove(myPosition, possible_space, null));
            } else if (inhabitant.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, possible_space, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> directionalMovement(ChessBoard board, ChessPosition myPosition, int[][] offsets) {
        Collection <ChessMove> moves = new ArrayList<>();
        for (int[] offset : offsets) {
            for (int i = 1; i < 8; i++) {
                int newRow = myPosition.getRow() + (i*offset[0]);
                int newCol = myPosition.getColumn() + (i*offset[1]);

                if (newRow < 1 | newRow > 8 | newCol < 1 | newCol > 8) {break;}

                ChessPosition possible_space = new ChessPosition(newRow, newCol);
                ChessPiece inhabitant = board.getPiece(possible_space);

                if (inhabitant == null) {
                    moves.add(new ChessMove(myPosition, possible_space, null));
                    continue;
                }
                if (inhabitant.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, possible_space, null));
                    break;
                }
                if (inhabitant.getTeamColor() == pieceColor) {
                    break;
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawn(ChessBoard board, ChessPosition myPosition) {
        int startRow;
        int endRow;
        int[][] normal_offsets;
        int[][] double_offsets;
        int[][] attack_offsets;

        if (pieceColor == ChessGame.TeamColor.BLACK) {
            startRow = 7;
            endRow = 1;
            normal_offsets = new int[][] {{-1,0}};
            double_offsets = new int[][] {{-1, 0}, {-2, 0}};
            attack_offsets = new int[][] {{-1, -1}, {-1, 1}};
        } else {
            startRow = 2;
            endRow = 8;
            normal_offsets = new int[][] {{1,0}};
            double_offsets = new int[][] {{1, 0}, {2, 0}};
            attack_offsets = new int[][] {{1, -1}, {1, 1}};
        }

        if (startRow == myPosition.getRow()) {
            return pawnMovements(board, myPosition, double_offsets, attack_offsets, endRow);
        } else {
            return pawnMovements(board, myPosition, normal_offsets, attack_offsets, endRow);
        }
    }

    private Collection<ChessMove> pawnMovements(ChessBoard board, ChessPosition myPosition, int[][] offsets, int[][] attack_offsets, int endRow) {
        Collection<ChessMove> moves = new ArrayList<>();
        int newRow;
        int newCol;

        for (int[] offset : offsets) {
            newRow = myPosition.getRow() + offset[0];
            newCol = myPosition.getColumn() + offset[1];

            if (newRow < 1 | newRow > 8 | newCol < 1 | newCol > 8) {
                continue;
            }

            ChessPosition possible_space = new ChessPosition(newRow, newCol);
            ChessPiece inhabitant = board.getPiece(possible_space);


            if (inhabitant != null) {
                break;
            } else if (newRow == endRow) {
                for (ChessPiece.PieceType p : ChessPiece.PieceType.values()) {
                    if (p == ChessPiece.PieceType.KING | p == ChessPiece.PieceType.PAWN) {
                        continue;
                    }
                    moves.add(new ChessMove(myPosition, possible_space, p));
                }
            } else {
                moves.add(new ChessMove(myPosition, possible_space, null));
            }
        }

        for (int[] attack_offset : attack_offsets) {
            newRow = myPosition.getRow() + attack_offset[0];
            newCol = myPosition.getColumn() + attack_offset[1];

            if (newRow < 1 | newRow > 8 | newCol < 1 | newCol > 8) {
                continue;
            }

            ChessPosition possible_space = new ChessPosition (newRow, newCol);
            ChessPiece inhabitant = board.getPiece(possible_space);

            if (inhabitant == null) {continue;}
            if (inhabitant.getTeamColor() != pieceColor && newRow == endRow) {
                for (ChessPiece.PieceType p : ChessPiece.PieceType.values()) {
                    if (p == ChessPiece.PieceType.KING | p == ChessPiece.PieceType.PAWN) {
                        continue;
                    }
                    moves.add(new ChessMove(myPosition, possible_space, p));
                }
            } else if (inhabitant.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, possible_space, null));
            }

        }

        return moves;
    }

    //Below this point are overrides and helper functions for debugging.
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
