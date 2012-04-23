/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smurawski
 */
public class Board implements Serializable {


    public static enum STATE {

        WHITE, BLACK, SELECTABLE_BLACK, SELECTABLE_WHITE, NOT_SELECTABLE
    }
    protected LinkedList<BoardMove> moves = new LinkedList<BoardMove>();
    public static final String STARTING_POSITION = "e5w,d4w,e4b,d5b,";
    protected STATE[][] boolboard;
    
    protected boolean nextPlayerBlack = true;
    private int blackStones = 0;
    private int whiteStones = 0;
    protected static final Logger log = LoggerFactory.getLogger(Board.class);

    /**
     * Creates a new Othello-Board with starting positions.
     */
    public Board() {
        super();
        reset();
    }

    public final void reset() {
        nextPlayerBlack = true;
        moves.clear();
        markNextMoves(boolboard, nextPlayerBlack);
    }

    /**
     * 0-based Grid (a=0 ... h = 7, 1 = 0, 8 = 7)
     * @param row
     * @param column 
     */
    public final void makeMove(int row, int column) {
        log.info(toString(boolboard));

        String infoMove = row + "/" + column;

        if (isLegalMove(boolboard, row, column, nextPlayerBlack)) //next player means last player now
        {
            boolean flipped = flip(boolboard, row, column, nextPlayerBlack ? STATE.BLACK : STATE.WHITE);
            if (!flipped) {
                throw new IllegalStateException("Did not flip for move: " + infoMove);
            }
        } else {
            log.warn("IllegalMove: " + infoMove);
            markNextMoves(boolboard, nextPlayerBlack);
            return;
        }

        //first Move is Black
        if (nextPlayerBlack) {
            nextPlayerBlack = false;
            boolean canMove = markNextMoves(boolboard, nextPlayerBlack);
            if (!canMove) {
                canMove = markNextMoves(boolboard, !nextPlayerBlack);
                if (canMove) {
                    nextPlayerBlack = true;
                }
                //else end of game
            }

        } else {
            boolboard[row][column] = STATE.WHITE;
            nextPlayerBlack = true;
            boolean canMove = markNextMoves(boolboard, nextPlayerBlack);
            if (!canMove) {
                canMove = markNextMoves(boolboard, !nextPlayerBlack);
                if (canMove) {
                    nextPlayerBlack = false;
                }
                //else end of game
            }

        }
    }

    private boolean isLegalMove(STATE[][] board, int row, int column, boolean black) {

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
                log.debug(row + " - " + column + ": " + flipFound);
                if (flipFound) {
                    return true;
                }
            }
        }
        log.debug(row + " - " + column + ": False");
        return false;
    }

    private boolean flip(STATE[][] board, int row, int column, STATE endflip) {
        STATE flip = (endflip == STATE.BLACK) ? STATE.WHITE : STATE.BLACK;

        boolean flipped = false;
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
                log.debug("Flip candidate found for " + dir);
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

                    if (board[nextRow][nextColumn] == endflip) {
                        log.debug("Starting to Flip for " + dir);
                        while (!(row == nextRow && column == nextColumn)) //backwards flipping
                        {
                            nextRow = nextRow - dir.getHor();
                            if (nextRow == -1 || nextRow == 8) {
                                throw new IllegalStateException("Rushed over last move"); //at the end of the board
                            }
                            nextColumn = nextColumn - dir.getVer();
                            if (nextColumn == -1 || nextColumn == 8) {
                                throw new IllegalStateException("Rushed over last move"); //at the end of the board
                            }
                            log.debug("Flipped: " + nextColumn + "/" + nextRow + " to " + endflip);
                            board[nextRow][nextColumn] = endflip;
                            flipped = true;
                        }
                        break;
                    }
                }
            }

            if (!flipped) {
                log.debug(dir + " did not flip");
            }
        }
        return flipped;
    }
    
   public STATE getState(int row, int column) {
        return boolboard[row][column];
    }

    protected String toString(STATE[][] board) {
        StringBuilder build = new StringBuilder("\n");
        for (int i = 0; i < 8; i++) {
            build.append("|");
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    STATE state = board[i][j];
                    if (state == STATE.BLACK) {
                        build.append("b|");
                    } else if (state == STATE.WHITE) {
                        build.append("w|");
                    } else if (state == STATE.SELECTABLE_BLACK) {
                        build.append("x|");
                    } else if (state == STATE.SELECTABLE_WHITE) {
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

    protected String backpose(STATE[][] board) {
        int black = 0;
        int white = 0;
        StringBuilder build = new StringBuilder("");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    char c = (char) (i + 'a');
                    String pos = "" + c + (j + 1);
                    STATE state = board[i][j];
                    if (state == STATE.BLACK) {
                        black++;
                        build.append(pos).append("b");
                    } else if (state == STATE.WHITE) {
                        white++;
                        build.append(pos).append("w");
                    } else if (state == STATE.SELECTABLE_BLACK) {
                        build.append(pos).append("x");
                    } else if (state == STATE.SELECTABLE_WHITE) {
                        build.append(pos).append("o");
                    }
                    build.append(",");
                }
            }

        }

        blackStones = black;
        whiteStones = white;
        return build.toString();
    }

    protected boolean markNextMoves(STATE[][] board, boolean black) {
        boolean marked = false;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                //TODO Optimize possible?
                if (board[row][column] == null) {
                    if (isLegalMove(board, row, column, black)) {
                        STATE selState = black ? STATE.SELECTABLE_BLACK : STATE.SELECTABLE_WHITE;
                        board[row][column] = selState;
                        marked = true;
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
        return "Board{" + "moves=" + moves + ", board=" + Arrays.toString(boolboard) + ", lastmove=" + moves.getLast() + ", nextPlayerBlack=" + nextPlayerBlack + '}';
    }

    public boolean getNextPlayerBlack() {
        return nextPlayerBlack;
    }

    public int getBlackStones() {
        return blackStones;
    }

    public int getWhiteStones() {
        return whiteStones;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}