/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author smurawski
 */
public final class MoveResult implements Comparable<MoveResult> {

    int value;
    BoardMove move;
    boolean playerBlack;
    ArrayList<BoardMove> moves = new ArrayList<>(20);

    public MoveResult(int value, BoardMove move, boolean playerBlack) {
        this.value = value;
        this.move = move;
        this.playerBlack = playerBlack;
        moves.add(move);
    }

    public BoardMove getMove() {
        return move;
    }

    public int getValue() {
        return value;
    }

    public boolean isPlayerBlack() {
        return playerBlack;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MoveResult other = (MoveResult) obj;
        if (this.value != other.value) {
            return false;
        }
        if (!Objects.equals(this.move, other.move)) {
            return false;
        }
        if (this.playerBlack != other.playerBlack) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.value;
        hash = 13 * hash + Objects.hashCode(this.move);
        hash = 13 * hash + (this.playerBlack ? 1 : 0);
        return hash;
    }

    @Override
    public int compareTo(MoveResult o) {
        if (playerBlack) {
            return value - o.value; //positive value is better
        }
        return (value - o.value) * -1; //negative value is better
    }

    List<BoardMove> getAllMoves() {
        ArrayList<BoardMove> result = new ArrayList<>(moves);
        Collections.reverse(result);
        return result;
    }

    void registerMove(BoardMove myMove, boolean playerBlack) {
        this.playerBlack = playerBlack;
        moves.add(myMove);
    }
}
