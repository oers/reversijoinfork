/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.LoggerFactory;

/**
 * Needs a good datastructure, map based doesn't sound right. Needs to be a real decision tree or something similar.
 * @author smurawski
 */
public class GameSolver {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GameSolver.class);
    
    private final Board board;

    public GameSolver(Board b) {
        this.board = b;
    }

    //Ideas: exit any path, that leads to a loss (need to keep track of whom I analyze), may lead to no answers
    //Ideas fast tree structure, get rid of maps etc
    
    public SortedMap<Integer, Deque<BoardMove>> getBestMoves() {
        int result = -99999;
        Deque<BoardMove> moves = new LinkedList<>();
        //LOG.info("Move:" + board.getMoves().size());

        boolean wasPlayerBlack = board.isNextPlayerBlack();
        
        if(board.getPossibleMoves().isEmpty() && !board.isFinished())
        {
            LOG.warn("Marking of Moves was necessary");
            board.markNextMoves();
        }
        
        //LOG.info("Moves: " + board.getMoves().size());
        
        for (BoardMove move : board.getPossibleMoves()) {
            Board copyFor = new Board(true, board); //has to work on a real copy       
            
            copyFor.makeMove(move.getRow(), move.getColumn());
            int localResult;
            Deque<BoardMove> localMoves = new LinkedList<>();
            if (copyFor.isFinished()) {
                if(copyFor.markNextMoves())
                {
                    throw new IllegalStateException("More moves possible!: " + copyFor.toString());
                }
                
                int blackStones = copyFor.getBlackStones();
                int whiteStones = copyFor.getWhiteStones();

                localResult = blackStones - whiteStones; //positive Numbers are good for black, negative Numbers are good for white      
                localMoves = new LinkedList<>();
            } else { //Start the recursion!
                localResult = 0;
                copyFor.markNextMoves(); //mark before copy, saves time
                final SortedMap<Integer, Deque<BoardMove>> bestMoves = new GameSolver(copyFor).getBestMoves();
                
                for (Entry<Integer, Deque<BoardMove>> madeMoves : bestMoves.entrySet()) //just assume it only contains one result at the moment
                {
                    localResult = madeMoves.getKey();
                    localMoves = madeMoves.getValue();
                }
            }

            localMoves.add(move);

            if (wasPlayerBlack) {
                //find highest number
                if (result == -99999 || localResult > result) {
                    result = localResult;
                    moves = localMoves;
                }
//                    else if(localResult == result)
//                    {
//                        moves.add(move);
//                    }
            } else {
                //find the lowest number
                if (result == -99999 ||  localResult < result) {
                    result = localResult;
                    moves = localMoves;
                }
//                } else if (localResult == result) {
//                    moves.add(move);
//                }
            }

        }

        SortedMap<Integer, Deque<BoardMove>> resultMap = new TreeMap<>();
        resultMap.put(Integer.valueOf(result), moves);
        return resultMap;
    }
}
