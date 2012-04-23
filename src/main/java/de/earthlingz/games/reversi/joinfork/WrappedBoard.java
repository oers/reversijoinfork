/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author smurawski
 */
public class WrappedBoard extends Board{

    protected String board = STARTING_POSITION;
    
    public void markNextMoves() {
        STATE[][] local = transpose(board);
        markNextMoves(local, nextPlayerBlack);
        board = backpose(local);
    }

    public void makeMove(String nextMove) {
        if (nextMove == null || nextMove.length() < 1 || !nextMove.matches("[a-h][1-8]") || moves.contains(nextMove)) {
            markNextMoves();
            return;
        }
        boolboard = transpose(board);
        int row = nextMove.charAt(0) - (int) 'a';
        int column = Integer.parseInt(nextMove.substring(1, 2)) - 1;
        makeMove(row, column);
        board = backpose(boolboard);
        log.info(board);
        log.info(toString(boolboard));
        //TODO Skipped Moves
    }

    public STATE getState(String string) {
        Pattern p = Pattern.compile(string + "[w|b|o|x]?,"); //w-->white, b-->black, o-->white move, x --> black move
        Matcher m = p.matcher(board);
        if (m.find()) {
            String state = m.group().substring(2, 3);
            if ("w".equals(state)) {
                return STATE.WHITE;
            } else if ("b".equals(state)) {
                return STATE.BLACK;
            } else if ("o".equals(state)) {
                return STATE.SELECTABLE_WHITE;
            } else if ("x".equals(state)) {
                return STATE.SELECTABLE_BLACK;
            }
        }
        return STATE.NOT_SELECTABLE;
    }

    private STATE[][] transpose(String board) {
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
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getLastMove() {
        if (moves.size() > 0) {
            final BoardMove last = moves.getLast();
            char first = (char) ((char)last.getRow() + 'a');
            return "" + first + (last.getColumn()+1);
        }
        return "start";
    }
    
}
