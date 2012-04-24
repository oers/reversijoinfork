/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smurawski
 */
public class Board {

    public static enum STATE {

        WHITE, BLACK, SELECTABLE
    }
    protected LinkedList<BoardMove> moves = new LinkedList<>();
    protected STATE[][] boolboard;
    protected boolean nextPlayerBlack = true;
    protected static final Logger log = LoggerFactory.getLogger(Board.class);
    private final boolean skipCheck; //determine whether to check for bad/illegal moves (used for performance)

    /**
     * Creates a new Othello-Board with starting positions.
     */
    public Board(boolean pSkipChecks) {
        super();
        skipCheck = pSkipChecks;
        nextPlayerBlack = true;
        boolboard = new STATE[8][8];
        boolboard[4][4]=STATE.WHITE;//e5
        boolboard[3][3]=STATE.WHITE;//d4
        boolboard[3][4]=STATE.BLACK;//d5
        boolboard[4][3]=STATE.BLACK;//e4
    }
    
    public Board(boolean pSkipChecks, STATE[][] pBoard, boolean pNextPlayerBlack) {
        skipCheck=pSkipChecks;
        nextPlayerBlack = pNextPlayerBlack;
        boolboard = pBoard.clone(); //clone is okay, because the values in the arrays are immutable
    }

    /**
     * 0-based Grid (a=0 ... h = 7, 1 = 0, 8 = 7)
     *
     * @param row
     * @param column
     */
    public final boolean makeMove(int row, int column) {
        if(log.isDebugEnabled()) {log.debug("Before: " + toString(boolboard));}

        BoardMove move = new BoardMove(row, column);
        if(log.isDebugEnabled()) {log.debug(move.toString() + "-" + (nextPlayerBlack?"black":"white"));}

        //assume that skipCheck = true is only used when you are sure that values passed here are legal Moves
        if (!skipCheck && moves.contains(move)) {
            log.warn("Move already made: " + move.toString());
            markNextMoves(boolboard, nextPlayerBlack);
            return false;
        } else if (skipCheck || isLegalMove(boolboard, row, column, nextPlayerBlack)) //next player means last player now
        {
            moves.add(move);
            boolean flipped = flip(boolboard, row, column, nextPlayerBlack ? STATE.BLACK : STATE.WHITE);
            if (!flipped) {
                return false;
            }
            if(log.isDebugEnabled()) {log.debug("Flipped: " + toString(boolboard));}
        } else {
            log.warn("IllegalMove: " + move.toString());
            markNextMoves(boolboard, nextPlayerBlack);
            return false;
        }

        //first Move is Black
        if (nextPlayerBlack) {
            nextPlayerBlack = false;
            boolean canMove = markNextMoves(boolboard, nextPlayerBlack);
            if (!canMove) {
                canMove = markNextMoves(boolboard, !nextPlayerBlack);
                if (canMove) {
                    if(log.isDebugEnabled()) {log.debug("White has to skip");}
                    nextPlayerBlack = true;
                }
                else
                {
                    if(log.isDebugEnabled()) {log.debug("End of Game");}
                }
            }

        } else {
            nextPlayerBlack = true;
            boolean canMove = markNextMoves(boolboard, nextPlayerBlack);
            if (!canMove) {
                canMove = markNextMoves(boolboard, !nextPlayerBlack);
                if (canMove) {
                    if(log.isDebugEnabled()) {log.debug("Black has to skip");}
                    nextPlayerBlack = false;
                }
                else
                {
                    if(log.isDebugEnabled()) {log.debug("End of Game");}
                }
            }

        }
        
        if(log.isDebugEnabled()) {log.debug("NextPlayer: " + (nextPlayerBlack?"black":"white"));};
        
        return true;
    }

    public static boolean isLegalMove(STATE[][] board, int row, int column, boolean black) {

        STATE flip;
        STATE endFlip;
        if (black) {
            flip = STATE.WHITE;
            endFlip = STATE.BLACK;
        } else {
            flip = STATE.BLACK;
            endFlip = STATE.WHITE;
        }

        for (Direction dir : Direction.values()) {
            int nextRow = row + dir.getHor();
            if (nextRow == -1 || nextRow == 8) {
                continue; //at the end of the board
            }
            int nextColumn = column + dir.getVer();
            if (nextColumn == -1 || nextColumn == 8) {
                continue; //at the end of the board
            }
            if (board[nextRow][nextColumn] == flip) {
                boolean flipFound = false;
                //can be flipped, if we can find a beginning i.e. stone of other colour in same direction
                while (true) //can't think of an appropiate recursion end right now
                {
                    nextRow = nextRow + dir.getHor();
                    if (nextRow == -1 || nextRow == 8) {
                        break; //at the end of the board
                    }
                    nextColumn = nextColumn + dir.getVer();
                    if (nextColumn == -1 || nextColumn == 8) {
                        break; //at the end of the board
                    }
                    //if we find an empty field break;
                    if (board[nextRow][nextColumn] != STATE.BLACK && board[nextRow][nextColumn] != STATE.WHITE) {
                        break;
                    }

                    if (board[nextRow][nextColumn] == endFlip) {
                        flipFound = true;
                        break;
                    }
                }
                if(log.isTraceEnabled()){log.trace(row + " - " + column + ": " + flipFound);}
                if (flipFound) {
                    return true;
                }
            }
        }
        if(log.isTraceEnabled()){log.trace(row + " - " + column + ": False");}
        return false;
    }

    private boolean flip(STATE[][] board, int row, int column, STATE endflip) {
        STATE flip = (endflip == STATE.BLACK) ? STATE.WHITE : STATE.BLACK;

        boolean flipped = false;
        //look for flip in every direction
        for (Direction dir : Direction.values()) {
            int nextRow = row + dir.getHor();
            
            //we must jump over at least one stone, so certain flips don't need to be evaluated
            //if the next stone in direction is on directly on the edge rows/columns skip evaluation
            
            //read as: if this or the next move in the same vertical direction will be out of bounds --> ignore
            if (nextRow + dir.getHor() < 0 || nextRow + dir.getHor() > 7) { 
                continue; //at the end of the board
            }
            int nextColumn = column + dir.getVer();
            
            //read as: if this or the next move in the same horizontal direction will be out of bounds --> ignore
            if (nextColumn + dir.getVer() < 0 || nextColumn + dir.getVer() > 7) {
                continue; //at the end of the board
            }
            
            if (board[nextRow][nextColumn] == flip) { //the direction is right, stone of opposite colour in that direction
                if (log.isTraceEnabled()) {
                    log.trace("Flip candidate found for " + dir);
                }
                
                //can be flipped, if we can find a beginning i.e. stone of other colour in same direction
                while (true) //can't think of an appropiate recursion end right now
                {
                    nextRow = nextRow + dir.getHor();
                    if (nextRow == -1 || nextRow == 8) {
                        break; //at the end of the board
                    }
                    nextColumn = nextColumn + dir.getVer();
                    if (nextColumn == -1 || nextColumn == 8) {
                        break; //at the end of the board
                    }
                    //if we find an empty field break;
                    if (board[nextRow][nextColumn] != STATE.BLACK && board[nextRow][nextColumn] != STATE.WHITE) {
                        break;
                    }

                    if (board[nextRow][nextColumn] == endflip) { //found a stone of same colour, lines between can be flipped
                        if (log.isTraceEnabled()) {
                            log.trace("Starting to Flip for " + dir);
                        }
                        while (!(row == nextRow && column == nextColumn)) //backwards flipping, flip till we reach the start
                        {
                            nextRow = nextRow - dir.getHor();
                            if (nextRow == -1 || nextRow == 8) {
                                throw new IllegalStateException("Couldn't find starting point backflipping"); //at the end of the board
                            }
                            nextColumn = nextColumn - dir.getVer();
                            if (nextColumn == -1 || nextColumn == 8) {
                                throw new IllegalStateException("Rushed over last move"); //at the end of the board
                            }
                            if (log.isTraceEnabled()) {
                                log.trace("Flipped: " + nextColumn + "/" + nextRow + " to " + endflip);
                            }
                            board[nextRow][nextColumn] = endflip;
                            flipped = true;
                        }
                        break;
                    }
                }
            }

            if (!flipped) {
                if (log.isTraceEnabled()) {
                    log.trace(dir + " did not flip");
                }
            }
        }
        return flipped;
    }

    public STATE getState(int row, int column) {
        return boolboard[row][column];
    }

    public String toString(STATE[][] board) {
        StringBuilder build = new StringBuilder("\n");
        build.append("_|a|b|c|d|e|f|g|h|\n");
        for (int i = 0; i < 8; i++) {
            build.append(i+1).append("|");
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    STATE state = board[i][j];
                    if (state == STATE.BLACK) {
                        build.append("b|");
                    } else if (state == STATE.WHITE) {
                        build.append("w|");
                    } else if (state == STATE.SELECTABLE) {
                        build.append("o|");
                    }
                } else {
                    build.append("_|");
                }
            }
            build.append("\n");

        }
        return build.toString();
    }

    public static boolean markNextMoves(STATE[][] board, boolean black) {
        boolean marked = false;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                //TODO Optimize possible?
                if (board[row][column] == null || board[row][column] == STATE.SELECTABLE) {
                    if (isLegalMove(board, row, column, black)) {
                        board[row][column] = STATE.SELECTABLE;
                        marked = true;
                    }
                    else
                    {
                        board[row][column] = null; //unsets fields that were selectable
                    }
                            
                } else {
                    continue; //Field is occupied
                }
            }
        }

        return marked;
    }

    public LinkedList<BoardMove> getMoves() {
        return moves;
    }

    public void setMoves(LinkedList<BoardMove> moves) {
        this.moves = moves;
    }

    @Override
    public String toString() {
        return "Board{" + "moves=" + moves + ", board=" + toString(boolboard) + ", lastmove=" + moves.getLast() + ", nextPlayerBlack=" + nextPlayerBlack + '}';
    }

    public boolean isNextPlayerBlack() {
        return nextPlayerBlack;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}