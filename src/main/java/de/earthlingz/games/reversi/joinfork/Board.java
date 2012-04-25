/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.*;
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
    private Deque<BoardMove> moves = new LinkedList<>();
    private STATE[] boolboard;
    private boolean nextPlayerBlack;
    protected static final Logger log = LoggerFactory.getLogger(Board.class);
    private final boolean skipCheck; //determine whether to check for bad/illegal moves (used for performance)
    private Set<BoardMove> possibleMoves = new HashSet<>();
    
    private int blackStones;
    private int whiteStones;
    private boolean finished = false;

    /**
     * Creates a new Othello-Board with starting positions.
     */
    public Board(boolean pSkipChecks) {
        super();
        skipCheck = pSkipChecks;
        nextPlayerBlack = true;
        boolboard = new STATE[64];
        boolboard[4*8 + 4]=STATE.WHITE;//e5
        boolboard[3*8 + 3]=STATE.WHITE;//d4
        boolboard[3*8 + 4]=STATE.BLACK;//d5
        boolboard[4*8 + 3]=STATE.BLACK;//e4
        blackStones = 2;
        whiteStones = 2;
    }
    
    public Board(boolean pSkipChecks, Board toCopy) {
        super();
        skipCheck = pSkipChecks;
        nextPlayerBlack = toCopy.isNextPlayerBlack();
        boolboard =deepCopy(toCopy.boolboard);
        moves = new LinkedList<>(toCopy.moves);
        blackStones = toCopy.getBlackStones();
        whiteStones = toCopy.getWhiteStones();
        finished = toCopy.isFinished();
        possibleMoves = Collections.unmodifiableSet(toCopy.getPossibleMoves());
    }
    
    public Board(boolean pSkipChecks, STATE[] pBoard, boolean pNextPlayerBlack) {
        blackStones = 0;
        whiteStones = 0;
        skipCheck=pSkipChecks;
        nextPlayerBlack = pNextPlayerBlack;
        boolboard = deepCopy(pBoard);
        for(int i = 0; i < 8; i++)
        {
            for(int j = 0; j < 8; j++)
            {
                if(boolboard[i*8 + j] == STATE.BLACK)
                {
                    blackStones++;
                }
                else if(boolboard[i*8 + j] == STATE.WHITE)
                {
                    whiteStones++;
                }
            }
        }
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
            markNextMoves();
            return false;
        } else if (skipCheck || isLegalMove(row, column)) //next player means last player now
        {
            moves.add(move);
            boolean flipped = flip(row, column);
            if (!flipped) {
                return false;
            }
            if(log.isDebugEnabled()) {log.debug("Flipped: " + toString(boolboard));}
        } else {
            log.warn("IllegalMove: " + move.toString());
            markNextMoves();
            return false;
        }

        //first Move is Black
        if (nextPlayerBlack) {
            nextPlayerBlack = false;
            boolean canMove = markNextMoves();
            if (!canMove) {
                nextPlayerBlack = true;
                canMove = markNextMoves();
                if (canMove) {
                    if(log.isDebugEnabled()) {log.debug("White has to skip");}
                    
                }
                else
                {
                    finished = true;
                    if(log.isDebugEnabled()) {log.debug("End of Game");}
                }
            }

        } else {
            nextPlayerBlack = true;
            boolean canMove = markNextMoves();
            if (!canMove) {
                nextPlayerBlack = false;
                canMove = markNextMoves();
                if (canMove) {
                    if(log.isDebugEnabled()) {log.debug("Black has to skip");}                  
                }
                else
                {
                    finished = true;
                    if(log.isDebugEnabled()) {log.debug("End of Game");}
                }
            }

        }
        
        if(log.isDebugEnabled()) {log.debug("NextPlayer: " + (nextPlayerBlack?"black":"white"));};
        
        return true;
    }

    public boolean isLegalMove(int row, int column)
    {
        return flip(row, column, nextPlayerBlack ? STATE.BLACK : STATE.WHITE, false); //don't flip just check  
    }

    public boolean flip(int row, int column) {
        return flip(row, column, nextPlayerBlack ? STATE.BLACK : STATE.WHITE, true);
    }
    
    /**
     * Flips all Stones on the board for the given move.
     * @param board
     * @param row the row of the move
     * @param column the column of the move
     * @param endflip the desired stone that makes the flip
     * @param executeFlip if false then only a check is performed, no actual flipping is done
     * @return 
     */
    private boolean flip( int row, int column, STATE endflip, boolean executeFlip) {
        STATE flip = (endflip == STATE.BLACK) ? STATE.WHITE : STATE.BLACK;

        int flipped = 0;
        //look for flip in every direction
        for (Direction dir : Direction.getCachedValues()) {
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
            
            if (boolboard[nextRow*8 + nextColumn] == flip) { //the direction is right, stone of opposite colour in that direction
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
                    if (boolboard[nextRow*8 + nextColumn] != STATE.BLACK && boolboard[nextRow*8 + nextColumn] != STATE.WHITE) {
                        break;
                    }

                    if (boolboard[nextRow*8 + nextColumn] == endflip) { //found a stone of same colour, lines between can be flipped
                        if(!executeFlip) //don't change the board, just check
                        {
                            return true;
                        }
                        if (log.isTraceEnabled()) {
                            log.trace("Starting to Flip for " + dir);
                        }
                        while (!(row == nextRow && column == nextColumn)) //backwards flipping, flip till we reach the start
                        {
                            nextRow = nextRow - dir.getHor();
                            nextColumn = nextColumn - dir.getVer();
                            
                            if (log.isTraceEnabled()) {
                                log.trace("Flipped: " + nextColumn + "/" + nextRow + " to " + endflip);
                            }
                            
                            //flip and count stones that are not already flipped
                            if(boolboard[nextRow*8 + nextColumn] != endflip)
                            {
                                boolboard[nextRow*8 + nextColumn] = endflip;
                                flipped++;
                            }
                        }
                        break;
                    }
                }
            }

            if (flipped == 0) {
                if (log.isTraceEnabled()) {
                    log.trace(dir + " did not flip");
                }
            }
        }
        
        if(executeFlip)
        {
            if(flip == STATE.WHITE)
            {
                blackStones += flipped;
                whiteStones -= (flipped - 1); //flipped contains the new stone
            }
            else
            {
                blackStones -= (flipped - 1); //flipped contains the new stone
                whiteStones += flipped;
            }
        }
        
        return flipped > 0;
    }

    public STATE getState(int row, int column) {
        return boolboard[row*8 + column];
    }

    public String toString(STATE[] board) {
        StringBuilder build = new StringBuilder("\n");
        build.append("_|a|b|c|d|e|f|g|h|\n");
        for (int i = 0; i < 8; i++) {
            build.append(i+1).append("|");
            for (int j = 0; j < 8; j++) {
                if (board[i*8 + j] != null) {
                    STATE state = board[i*8 + j];
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

    public boolean markNextMoves() {
        boolean marked = false;
        possibleMoves = new HashSet<>();
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (boolboard[row*8 + column] == null || boolboard[row*8 + column] == STATE.SELECTABLE) {
                    if (isLegalMove(row, column)) {
                        boolboard[row*8 + column] = STATE.SELECTABLE;
                        possibleMoves.add(new BoardMove(row, column));
                        marked = true;
                    }
                    else
                    {
                        boolboard[row*8 + column] = null; //unsets fields that were selectable
                    }
                            
                } else {
                    continue; //Field is occupied
                }
            }
        }

        return marked;
    }

    public Deque<BoardMove> getMoves() {
        return new LinkedList<>(moves);
    }

    @Override
    public String toString() {
        return "Board{" + "moves=" + moves + ", finished=" + finished +", board=" + toString(boolboard) + (moves.size() > 0 ? ", lastmove=" + moves.getLast():"") + ", nextPlayerBlack=" + nextPlayerBlack + '}';
    }

    public boolean isNextPlayerBlack() {
        return nextPlayerBlack;
    }

    public STATE[] getBoolboard() {
        return deepCopy(boolboard);
    }
    
    public int getBlackStones() {
        return blackStones;
    }

    public int getWhiteStones() {
        return whiteStones;
    }
    
    public Set<BoardMove> getPossibleMoves()
    {
        return Collections.unmodifiableSet(possibleMoves);
    }
 
    public boolean isFinished()
    {
        return finished;
    }
    
    private STATE[] deepCopy(STATE[] boolboard) {
        STATE[] result = new STATE[64];

        System.arraycopy(boolboard, 0, result, 0, 64);

        return result;
    }
}