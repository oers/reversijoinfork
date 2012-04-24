/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author smurawski
 */
public class WrappedBoard extends Board {

    private int blackStones = 0;
    private int whiteStones = 0;

    
    public WrappedBoard(boolean skipChecks)
    {
        super(skipChecks);
    }

    private WrappedBoard(STATE[][] transpose, boolean pNextPlayerBlack, boolean skipChecks) {
        super(skipChecks, transpose, pNextPlayerBlack);
    }

    public boolean makeMove(String nextMove) {

        if (nextMove == null || nextMove.length() < 1 || !nextMove.matches("[a-hA-H][1-8]")) {
            markNextMoves();
            return false;
        }
        nextMove = nextMove.toLowerCase();

        if(log.isDebugEnabled()) {log.debug(nextMove);};
        
        int column = nextMove.charAt(0) - (int) 'a';
        int row = Integer.parseInt(nextMove.substring(1, 2)) - 1;
        boolean result = makeMove(row, column);     
        return result;
    }

    /**
     * Gets the STATE for a given field (a1 ... h8)
     * @param string
     * @return 
     */
    public STATE getState(String string) {
        Pattern p = Pattern.compile(string + "[w|b|o]?,"); //w-->white, b-->black, o-->possible move
        Matcher m = p.matcher(getBoard());
        if (m.find()) {
            String state = m.group().substring(2, 3);
            switch (state) {
                case "w":
                    return STATE.WHITE;
                case "b":
                    return STATE.BLACK;
                case "o":
                    return STATE.SELECTABLE;
            }
        }
        return null;
    }

    public String backpose(STATE[][] board) {
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
                    } else if (state == STATE.SELECTABLE) {
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

    private static STATE[][] transpose(String board) {
        STATE[][] boolBoard = new STATE[8][8];
        Pattern p = Pattern.compile("[a-h][1-8][w|b],");
        Matcher m = p.matcher(board);
        while (m.find()) {
            String sState = m.group().substring(2, 3);
            STATE state;
            if ("b".equals(sState)) {
                state = STATE.BLACK;
            } else {
                state = STATE.WHITE;
            }
            char sRow = m.group().charAt(0);
            int column = Integer.parseInt(m.group().substring(1, 2)) - 1;
            int row = sRow - (int) 'a';
            boolBoard[row][column] = state;
        }
        return boolBoard;
    }

    public String getBoard() {
        return backpose(getBoolboard());
    }

    public static WrappedBoard createBoard(String board, boolean pNextPlayerBlack) {
        return new WrappedBoard(transpose(board), pNextPlayerBlack, false);
    }
    
    public static WrappedBoard replay(List<String> moves)
    {
        WrappedBoard result = new WrappedBoard(true); //skip checks
        for(String move : moves)
        {
            boolean res = result.makeMove(move);
            if(!res)
            {
                throw new IllegalStateException(move + " was illegal.");
            }
        }
        return result;
    }

    public String getLastMove() {
        if (getMoves().size() > 0) {
            final BoardMove last = getMoves().getLast();
            char first = (char) ((char) last.getColumn() + 'a');
            return "" + first + (last.getRow() + 1);
        }
        return "start";
    }
    
    public int getBlackStones() {
        return blackStones;
    }

    public int getWhiteStones() {
        return whiteStones;
    }
}
