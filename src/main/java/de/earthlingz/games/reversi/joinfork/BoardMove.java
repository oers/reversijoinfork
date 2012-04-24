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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BoardMove other = (BoardMove) obj;
        if (this.row != other.row) {
            return false;
        }
        if (this.column != other.column) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.row;
        hash = 89 * hash + this.column;
        return hash;
    }
}
