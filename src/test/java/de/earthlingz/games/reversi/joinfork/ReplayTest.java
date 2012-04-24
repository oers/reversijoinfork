/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author smurawski
 */
public class ReplayTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        //final ConsoleAppender appender = new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
        AppenderSkeleton appender = new AppenderSkeletonImpl();

        Logger.getRootLogger().addAppender(appender);
        Logger.getRootLogger().setLevel(Level.TRACE);
    }
    
    @AfterClass
    public static void tearDown() {
        Logger.getRootLogger().removeAllAppenders();
    }

    @Test
    public void wipeOut() {
        Logger.getRootLogger().setLevel(Level.INFO);
        //WOC 2010, round 1
        //TAKIZAWA Masaki 	64
        //TERKELSEN Signe 	0     
        WrappedBoard b = WrappedBoard.replay(
                split("F5F6E6F4G5G6G4C6F3F7E7D6D7F8E8F2G3C7G8H5H6H3D8G7H4H7H8E3H2C5B6C8C4B3C3B8A3A6A7A8B7B5B4D3C2D1G2F1G1H1D2E2E1B1A4A5A2C1"));
        b.getBoard();
        Assert.assertEquals("C1".toLowerCase(), b.getLastMove());
        Assert.assertEquals(0, b.getWhiteStones());
        Assert.assertEquals(62, b.getBlackStones()); //2 empty fields  
        Assert.assertEquals(b.getState("C1"), Board.STATE.BLACK);
        Logger.getRootLogger().setLevel(Level.TRACE);
    }

    @Test(expected = IllegalStateException.class)
    public void unplayable() {
        WrappedBoard.replay(
                split("F5F6E6F4G5G6G4A8"));
    }

    @Test(expected = IllegalStateException.class)
    public void wrongInput() {
        WrappedBoard.replay(
                split("I8"));
    }

    @Test(expected = IllegalStateException.class)
    public void wrongInput2() {
        WrappedBoard.replay(
                split("H9"));
    }

    @Test
    public void whiteWin() {
        //    WOC 2010, round 1
        //    ORTIZ George 	22
        //    BERG Matthias 	44
        WrappedBoard b = WrappedBoard.replay(split("F5F6E6F4G5G6G4E7F3D6F7H3D8D3H4H5D7E3E2D2G3F8C5E8G8B5C4B4C3D1F2C6F1H2B3C2B1C7A5A3A4A6C8B6H7C1E1G2B7A7H6H8B2B8G1H1G7A1A2A8"));
        b.getBoard();
        Assert.assertEquals("A8".toLowerCase(), b.getLastMove());
        Assert.assertEquals(20, b.getBlackStones());
        Assert.assertEquals(44, b.getWhiteStones());
    }
    
    @Test
    public void transposeBackpose() {
        //WOC 2010, round 1
        //DE GRAAF Jan C. 	40
        //HELMES Jiska
        WrappedBoard b = WrappedBoard.replay(split("F5F6E6F4E3D6C5F3G4E2G5G6C7C3D3C2D2C6F7B5F1H4H3H5E7D7B3E1B4F8"));
        b.markNextMoves();
        String transposed = b.getBoard();
        WrappedBoard backposed = WrappedBoard.createBoard(transposed, b.isNextPlayerBlack());
        backposed.markNextMoves();
        backposed.getBoard();
        Assert.assertEquals(12, backposed.getBlackStones());
        Assert.assertEquals(22, backposed.getWhiteStones());

    }

    @Test
    public void draw() {
        //WOC 2010, round 11
        //VAN DEN BIGGELAAR Nicky 	32
        //TAKANASHI Yusuke 	32
        WrappedBoard b = WrappedBoard.replay(split("F5D6C3D3C4F4F6F3E6E7D7G6F8F7H6C5C6D8E3B6G4B4B5H3H4E2D2G3F1F2G5E8C8C7A3E1D1C1B1C2A6G2B8A5G8H5H1A7B2G1A4B7H2A2A8A1B3H7G7H8"));
        b.getBoard();
        Assert.assertEquals("H8".toLowerCase(), b.getLastMove());
        Assert.assertEquals(32, b.getWhiteStones());
        Assert.assertEquals(32, b.getBlackStones());
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

    /*
     * does nothing but log happily
     */
    private static final class AppenderSkeletonImpl extends AppenderSkeleton {

        public AppenderSkeletonImpl() {
        }

        @Override
        public void doAppend(LoggingEvent le) {
            return;
        }

        @Override
        protected void append(LoggingEvent le) {
            return;
        }

        @Override
        public void close() {
            return;
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    }
}
