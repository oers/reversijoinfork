/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import org.slf4j.LoggerFactory;

/**
 * Needs a good datastructure, map based doesn't sound right. Needs to be a real decision tree or something similar.
 * @author smurawski
 */
public class JoinForkGameSolver extends RecursiveTask<MoveResult> {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JoinForkGameSolver.class);

    public static MoveResult solve(Board b) {
        ForkJoinPool pool = new ForkJoinPool(3);
        
        b.markNextMoves(); //mark before copy, saves time
        ArrayList<JoinForkGameSolver> l = new ArrayList(b.getPossibleMoves().size());
        MoveResult localResult = null;
        for(BoardMove m: b.getPossibleMoves())
        {
            MoveResult result = pool.submit(new JoinForkGameSolver(b, m)).join();
            if(localResult == null)
            {
                localResult = result;
            }
            else if(result.compareTo(localResult) > 0)
            {
                localResult = result;
            }
        }
        
        return localResult;
        
    }
    
    private final Board myBoard;
    private final BoardMove myMove;

    public JoinForkGameSolver(Board board, BoardMove move) {
        this.myBoard = board;
        this.myMove = move;
    }

    //Ideas: exit any path, that leads to a loss (need to keep track of whom I analyze), may lead to no answers
    //Ideas fast tree structure, get rid of maps etc
    
    @Override
    public MoveResult compute() {
        //LOG.info("Move:" + board.getMoves().size());

        boolean wasPlayerBlack = myBoard.isNextPlayerBlack();
        
        if(myBoard.getPossibleMoves().isEmpty() && !myBoard.isFinished())
        {
            LOG.warn("Marking of Moves was necessary");
            myBoard.markNextMoves();
        }
        
 
        Board copyFor = new Board(true, myBoard); //has to work on a real copy       

        copyFor.makeMove(myMove.getRow(), myMove.getColumn());
        int localResult;
        
        if (copyFor.isFinished()) {              
            int blackStones = copyFor.getBlackStones();
            int whiteStones = copyFor.getWhiteStones();

            localResult = blackStones - whiteStones; //positive Numbers are good for black, negative Numbers are good for white      
            return new MoveResult(localResult, myMove, wasPlayerBlack);
        } else { //Start the recursion!
            copyFor.markNextMoves(); //mark before copy, saves time
            ArrayList<JoinForkGameSolver> l = new ArrayList(copyFor.getPossibleMoves().size());
            for(BoardMove m: copyFor.getPossibleMoves())
            {
                l.add(new JoinForkGameSolver(copyFor, m));
            }
            
            Collection<JoinForkGameSolver> results = invokeAll(l);
            
            //final SortedMap<Integer, List<BoardMove>> bestMoves = new JoinForkGameSolver(copyFor).compute();

            MoveResult localMoveResult = null;
            for (JoinForkGameSolver gResult: results) //just assume it only contains one result at the moment
            {
                MoveResult mResult = gResult.getRawResult();
                if(mResult == null)
                {
                    continue;
                }
                else if(localMoveResult == null)
                {
                    localMoveResult = mResult;
                    localMoveResult.registerMove(myMove, wasPlayerBlack);
                    
                } else if(mResult.compareTo(localMoveResult) > 0)
                {
                    localMoveResult = mResult;
                    localMoveResult.registerMove(myMove, wasPlayerBlack);
                }
            }
            return localMoveResult;
        }
    }
}
