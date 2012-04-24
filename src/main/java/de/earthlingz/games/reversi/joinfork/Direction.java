package de.earthlingz.games.reversi.joinfork;

/**
 *
 * @author smurawski
 */
 enum Direction {
    N(1, 0), NE(1, 1), E(0, 1), SE(-1, 1), S(-1, 0), SW(-1, -1), W(0, -1), NW(1, -1);
    int hor;
    int ver;

    Direction(int v, int h) {
        ver = v;
        hor = h;
    }

    public int getHor() {
        return hor;
    }

    public int getVer() {
        return ver;
    } 
}
