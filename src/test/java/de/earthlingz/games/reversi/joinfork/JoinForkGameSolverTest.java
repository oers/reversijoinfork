/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smurawski
 */
public class JoinForkGameSolverTest {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JoinForkGameSolverTest.class);

    @BeforeClass
    public static void setUpClass() throws Exception {
        final ConsoleAppender appender = new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));

        Logger.getRootLogger().addAppender(appender);
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    @AfterClass
    public static void tearDown() {
        Logger.getRootLogger().removeAllAppenders();
    }

    @Test
    public void whiteWin() {
        //    WOC 2010, round 1
        //    ORTIZ George 	22
        //    BERG Matthias 	44
        //F5F6E6F4G5G6G4E7F3D6F7H3D8D3H4H5D7E3E2D2G3F8C5E8G8B5C4B4C3D1F2C6F1H2B3C2B1C7A5A3A4A6C8B6H7C1E1G2B7A7H6H8B2B8G1H1G7A1A2A8

        //removed last
        WrappedBoard b = WrappedBoard.replay(split("F5F6E6F4G5G6G4E7F3D6F7H3D8D3H4H5D7E3E2D2G3F8C5E8G8B5C4B4C3D1F2C6F1H2B3C2B1C7A5A3A4A6C8B6H7C1E1G2B7A7"));
        b.markNextMoves();
        MoveResult result = JoinForkGameSolver.solve(b);
        List<BoardMove> firstResult = result.getAllMoves();

        Assert.assertTrue("Still has open moves", b.markNextMoves()); //still has open moves
        Assert.assertEquals("untouched Original", 50, b.getMoves().size()); //still has open moves
        Assert.assertEquals("10 Moves to be made" + firstResult, 10, firstResult.size()); //still has open moves
        Assert.assertNotSame("No Draw" + result.getValue(), 0, result.getValue()); //still has open moves


        for (BoardMove move : firstResult) {
            Assert.assertTrue("Move was possible:" + move + "/" + b.toString(), b.makeMove(move.getRow(), move.getColumn()));
            Assert.assertTrue(b.isFinished() || b.markNextMoves());
        }

        Assert.assertTrue("Game has ended: " + b.toString(), b.isFinished()); //game Ended
        Assert.assertEquals(20, b.getBlackStones());
        Assert.assertEquals(44, b.getWhiteStones());
    }

    @Test(timeout = 10000)
    public void whiteWin2() { //10 Seconds TimeOut
        //    WOC 2010, round 1
        //    ORTIZ George 	22
        //    BERG Matthias 	44
        //F5F6E6F4G5G6G4E7F3D6F7H3D8D3H4H5D7E3E2D2G3F8C5E8G8B5C4B4C3D1F2C6F1H2B3C2B1C7A5A3A4A6C8B6H7C1E1G2B7A7H6H8B2B8G1H1G7A1A2A8

        //removed last
        WrappedBoard b = WrappedBoard.replay(split("F5F6E6F4G5G6G4E7F3D6F7H3D8D3H4H5D7E3E2D2G3F8C5E8G8B5C4B4C3D1F2C6F1H2B3C2B1C7A5A3A4A6C8B6H7C1E1G2"));
        b.markNextMoves();
        MoveResult result = JoinForkGameSolver.solve(b);
        List<BoardMove> firstResult = result.getAllMoves();

        Assert.assertTrue("Still has open moves", b.markNextMoves()); //still has open moves
        Assert.assertEquals("untouched Original", 48, b.getMoves().size()); //still has open moves
        Assert.assertEquals("12 Moves to be made" + firstResult, 12, firstResult.size()); //still has open moves
        Assert.assertNotSame("No Draw" + result.getValue(), 0, result.getValue()); //still has open moves

        for (BoardMove move : firstResult) {
            Assert.assertTrue("Move was possible:" + move + "/" + b.toString(), b.makeMove(move.getRow(), move.getColumn()));
            Assert.assertTrue(b.isFinished() || b.markNextMoves());
        }

        Assert.assertTrue("Game has ended: " + b.toString(), b.isFinished()); //game Ended
        Assert.assertEquals(20, b.getBlackStones());
        Assert.assertEquals(44, b.getWhiteStones());
    }

    @Test
    public void recursiveSolveTimeAnalysis() {
        //    WOC 2010, round 1
        //    ORTIZ George 	22
        //    BERG Matthias 	44
        //F5F6E6F4G5G6G4E7F3D6F7H3D8D3H4H5D7E3E2D2G3F8C5E8G8B5C4B4C3D1F2C6F1H2B3C2B1C7A5A3A4A6C8B6H7C1E1G2B7A7H6H8B2B8G1H1G7A1A2A8


        String board = "F5F6E6F4G5G6G4E7F3D6F7H3D8D3H4H5D7E3E2D2G3F8C5E8G8B5C4B4C3D1F2C6F1H2B3C2B1C7A5A3A4A6C8B6H7C1E1G2B7A7";
        for (int i = 0; i < 6; i++) {
            LOG.info("Round " + (i + 1) + "Begin:" + new Date());
            //removed last
            WrappedBoard b = WrappedBoard.replay(split(board));
            b.markNextMoves();
            MoveResult result = JoinForkGameSolver.solve(b);
            List<BoardMove> firstResult = result.getAllMoves();

            Assert.assertTrue("Still has open moves", b.markNextMoves()); //still has open moves
            Assert.assertEquals("untouched Original", (50 - i), b.getMoves().size()); //still has open moves
            Assert.assertEquals("10 Moves to be made" + firstResult, (10 +i), firstResult.size()); //still has open moves
            Assert.assertNotSame("No Draw" + result.getValue(), 0, result.getValue()); //still has open moves


            for (BoardMove move : firstResult) {
                Assert.assertTrue("Move was possible:" + move + "/" + b.toString(), b.makeMove(move.getRow(), move.getColumn()));
                Assert.assertTrue(b.isFinished() || b.markNextMoves());
            }

            Assert.assertTrue("Game has ended: " + b.toString(), b.isFinished()); //game Ended
            Assert.assertEquals(20, b.getBlackStones());
            Assert.assertEquals(44, b.getWhiteStones());

            board = board.substring(0, board.length() - 2);
            LOG.info("Round " + (i + 1) + "End:" + new Date());
        }

        //0 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 1Begin:Thu Apr 26 10:40:28 CEST 2012
        //188 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 1End:Thu Apr 26 10:40:29 CEST 2012
        //203 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 2Begin:Thu Apr 26 10:40:29 CEST 2012
        //1968 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 2End:Thu Apr 26 10:40:30 CEST 2012
        //1968 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 3Begin:Thu Apr 26 10:40:30 CEST 2012
        //9232 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 3End:Thu Apr 26 10:40:38 CEST 2012
        //9232 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 4Begin:Thu Apr 26 10:40:38 CEST 2012
        //93316 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 4End:Thu Apr 26 10:42:02 CEST 2012
        //93316 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 5Begin:Thu Apr 26 10:42:02 CEST 2012
        //1109695 [main] INFO de.earthlingz.games.reversi.joinfork.GameSolverTest  - Round 5End:Thu Apr 26 10:58:58 CEST 2012
    }

    private List<String> split(String game) {
        ArrayList<String> moves = new ArrayList<>(60);
        while (game.length() > 0) {
            String move = game.substring(0, 2);
            moves.add(move);
            game = game.substring(2, game.length());
        }
        return moves;
    }
}
