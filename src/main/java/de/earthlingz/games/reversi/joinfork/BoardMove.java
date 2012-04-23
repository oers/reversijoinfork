/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

/**
 *
 * @author smurawski
 */
public final class BoardMove {
    int row;
    int column;

    public BoardMove(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public String toString() {
        return "BoardMove{" + "row=" + row + ", column=" + column + '}';
    }
}
