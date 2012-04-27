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
    
    STATE h8 = null; //save h8 in an additional state (hurray for no unsigned longs in java)
    
    private List<BoardMove> moves = new ArrayList<>(60);
    private long blackFields = 0; //nothing is set (negative values represent no stone/stone and positive values black/white)
    private long whiteFields = 0;
    private boolean nextPlayerBlack;
    protected static final Logger log = LoggerFactory.getLogger(Board.class);
    private final boolean skipCheck; //determine whether to check for bad/illegal moves (used for performance)
    private Set<BoardMove> possibleMoves;
    
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

        setStone(4, 4, 0);//e5 --> White (Field Occupied)
        setStone(3, 3, 0);//d4 --> White (Field Occupied)
        setStone(3, 4, 1);//d5 --> Black (Field Occupied)
        setStone(4, 3, 1);//e4 --> Black (Field Occupied)

        blackStones = 2;
        whiteStones = 2;
    }
    
    public Board(boolean pSkipChecks, Board toCopy) {
        super();
        skipCheck = pSkipChecks;
        nextPlayerBlack = toCopy.isNextPlayerBlack();
        blackFields = toCopy.blackFields;
        whiteFields = toCopy.whiteFields;
        h8 = toCopy.h8;
        moves = new ArrayList<>();
        blackStones = toCopy.getBlackStones();
        whiteStones = toCopy.getWhiteStones();
        finished = toCopy.isFinished();
        possibleMoves = Collections.unmodifiableSet(toCopy.getPossibleMoves());
    }
    
    public Board(boolean pSkipChecks, long pBoard, long fields, boolean pNextPlayerBlack) {
        blackStones = 0;
        whiteStones = 0;
        skipCheck=pSkipChecks;
        nextPlayerBlack = pNextPlayerBlack;
        blackFields = pBoard;
        whiteFields = fields;
        for(int i = 0; i < 8; i++)
        {
            for(int j = 0; j < 8; j++)
            {
                if(!isEmpty(i, j) && isStone(i, j, 1))
                {
                    blackStones++;
                }
                else if(!isEmpty(i, j) && isStone(i, j, 0))
                {
                    whiteStones++;
                }
            }
        }
    }
    
    protected final boolean isEmpty(int row, int column)
    {
        if(row == 7 && column == 7) //h8 is special
        {
            return h8 == null;
        }
        return ((whiteFields | blackFields) & (1L << (row * 8 + column))) == 0;
    }
    
    protected final boolean isStone( int row, int column, long stone)
    {
        if(row == 7 && column == 7) //h8 is special
        {
            return h8 == (stone == 1?STATE.BLACK:STATE.WHITE);
        }
        else if(stone == 0)
        {
           return (whiteFields & (1L << (row * 8 + column))) > 0;
        }
        return (blackFields & (1L << (row * 8 + column))) > 0;
    }
    
    protected final void setStone(int row, int column, long flip)
    {
        if(row == 7 && column == 7)
        {
            h8 = (flip == 1?STATE.BLACK:STATE.WHITE);
        }
        else if(flip == 1)
        {
            whiteFields = whiteFields & ~(1L << (row * 8 + column));
            blackFields = blackFields | (1L << (row * 8 + column)); //turn both flags on
        }
        else
        {
            whiteFields = whiteFields | (1L << (row * 8 + column));
            blackFields = blackFields & ~(1L << (row * 8 + column));//turn first flag on and second flag off
        }
    }
    

    /**
     * 0-based Grid (a=0 ... h = 7, 1 = 0, 8 = 7)
     *
     * @param row
     * @param column
     */
    public final boolean makeMove(int row, int column) {
//        if(log.isDebugEnabled()) {log.debug("Before: " + toStringBoard());}

        BoardMove move = new BoardMove(row, column);
//        if(log.isDebugEnabled()) {log.debug(move.toString() + "-" + (nextPlayerBlack?"black":"white"));}

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
//            if(log.isDebugEnabled()) {log.debug("Flipped: " + toStringBoard());}
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
                    //if(log.isDebugEnabled()) {log.debug("White has to skip");}
                    
                }
                else
                {
                    finished = true;
                    //if(log.isDebugEnabled()) {log.debug("End of Game");}
                }
            }

        } else {
            nextPlayerBlack = true;
            boolean canMove = markNextMoves();
            if (!canMove) {
                nextPlayerBlack = false;
                canMove = markNextMoves();
                if (canMove) {
                    //if(log.isDebugEnabled()) {log.debug("Black has to skip");}                  
                }
                else
                {
                    finished = true;
                    //if(log.isDebugEnabled()) {log.debug("End of Game");}
                }
            }

        }
        
        //if(log.isDebugEnabled()) {log.debug("NextPlayer: " + (nextPlayerBlack?"black":"white"));};
        
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
    private boolean flip( int row, int column, STATE pEndflip, boolean executeFlip) {
        long toFlip = (pEndflip == STATE.BLACK) ? 0 : 1;
        long endflip = (pEndflip == STATE.BLACK) ? 1 : 0;

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
            
//            log.info(nextRow + "/" + nextRow + "/" + isEmpty(nextRow, nextColumn) + "/" + isStone(nextRow, nextColumn, toFlip) + "/" + isStone(nextRow, nextColumn, endflip));
            if (isStone(nextRow, nextColumn, toFlip)) { //the direction is right, stone of opposite colour in that direction
//                if (log.isDebugEnabled()) {
//                    log.debug("Flip candidate found for " + dir);
//                }
                
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
                    if (isEmpty(nextRow, nextColumn)) {
                        break;
                    }

                    if (isStone(nextRow, nextColumn, endflip)) { //found a stone of same colour, lines between can be flipped
//                       if (log.isDebugEnabled()) {
//                            log.debug("Possible Move found for Flip " + dir);
//                        }
                        if(!executeFlip) //don't change the board, just check
                        {
                            return true;
                        }

                        while (!(row == nextRow && column == nextColumn)) //backwards flipping, flip till we reach the start
                        {
                            nextRow = nextRow - dir.getHor();
                            nextColumn = nextColumn - dir.getVer();
                            
//                            if (log.isDebugEnabled()) {
//                                log.debug("Flipped: " + nextColumn + "/" + nextRow + " to " + endflip);
//                            }
                            
                            //flip and count stones that are not already flipped
                            if(!isStone(nextRow, nextColumn, endflip))
                            {
                                setStone(nextRow, nextColumn, endflip);
                                flipped++;
                            }
                        }
                        break;
                    }
                }
            }

            if (flipped == 0) {
//                if (log.isDebugEnabled()) {
//                    log.debug(dir + " did not flip");
//                }
            }
        }
        
        if(executeFlip)
        {
            if(toFlip == 0) //WHITE
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
        if (isStone(row, column, 1)) {
            return STATE.BLACK;
        } else if (isStone(row, column, 0)) {
            return STATE.WHITE;
        } else {
            if(possibleMoves != null && possibleMoves.contains(new BoardMove(row, column)))
            {
                return STATE.SELECTABLE;
            }
            return null;
        }
    }

    public String toStringBoard() {
        StringBuilder build = new StringBuilder("\n");
        build.append("_|a|b|c|d|e|f|g|h|\n");
        for (int i = 0; i < 8; i++) {
            build.append(i+1).append("|");
            for (int j = 0; j < 8; j++) {
                STATE state = getState(i, j);
                if (state == STATE.BLACK) {
                    build.append("b|");
                } else if (state == STATE.WHITE) {
                    build.append("w|");
                } else if (state == STATE.SELECTABLE) {
                    build.append("o|");
                } else
                {
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
        for (int i = 0; i < 64; i++) {
            if (isEmpty(i/8, i%8) && isLegalMove(i/8, i%8)) {
               possibleMoves.add(new BoardMove(i/8, i%8));
               marked = true;
            }
        }

        return marked;
    }

    public List<BoardMove> getMoves() {
        return Collections.unmodifiableList(moves);
    }
    
    public void addMoves(List<BoardMove> pMoves) {
        moves.addAll(pMoves);
    }

    @Override
    public String toString() {
        return "Board{" + "moves=" + moves + ", finished=" + finished +", board=" + toStringBoard() + (moves.size() > 0 ? ", lastmove=" + moves.get(moves.size() - 1) :"") + ", nextPlayerBlack=" + nextPlayerBlack + '}';
    }

    public boolean isNextPlayerBlack() {
        return nextPlayerBlack;
    }

    public long getBoolboard() {
        return blackFields;
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
}