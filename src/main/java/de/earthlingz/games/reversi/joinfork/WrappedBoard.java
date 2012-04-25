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
  
    public WrappedBoard(boolean skipChecks)
    {
        super(skipChecks);
    }

    private WrappedBoard(STATE[] transpose, boolean pNextPlayerBlack, boolean skipChecks) {
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
        if(string == null)
        {
            return null;
        }
        
        String local = string.toLowerCase();
        int column = local.charAt(0) - (int) 'a';
        int row = Integer.parseInt(local.substring(1, 2)) - 1;
        return getState(row,column);
    }

    public String backpose() {
        STATE[] board = getBoolboard();
        StringBuilder build = new StringBuilder("");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i*8 + j] != null) {
                    char c = (char) (i + 'a');
                    String pos = "" + c + (j + 1);
                    STATE state = board[i*8 + j];
                    if (state == STATE.BLACK) {
                        build.append(pos).append("b");
                    } else if (state == STATE.WHITE) {
                        build.append(pos).append("w");
                    } else if (state == STATE.SELECTABLE) {
                        build.append(pos).append("o");
                    }
                    build.append(",");
                }
            }

        }
        return build.toString();
    }

    private static STATE[] transpose(String board) {
        STATE[] boolBoard = new STATE[64];
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
            boolBoard[row*8 + column] = state;
        }
        return boolBoard;
    }

    public String getBoard() {
        return backpose();
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
}
