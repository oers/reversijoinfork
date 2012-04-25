/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;
import org.slf4j.LoggerFactory;

/**
 * Just random play
 * @author smurawski
 */
public class RandomPlayTest {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RandomPlayTest.class);

    @BeforeClass
    public static void setUpClass() throws Exception {
        final ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
        consoleAppender.setThreshold(Level.INFO);
        Logger.getRootLogger().addAppender(consoleAppender);
    }

    @AfterClass
    public static void tearDown() {
        Logger.getRootLogger().removeAllAppenders();
    }

    @Test
    public void randomPlay() {
        Board b = new Board(true);
        b.markNextMoves();
        while (!b.isFinished()) {
            ArrayList<BoardMove> asList = new ArrayList<>(b.getPossibleMoves());
            Collections.shuffle(asList); //get random move
            BoardMove move = asList.get(0);
            b.makeMove(move.getRow(), move.getColumn());
            b.markNextMoves();
        }
        LOG.info("Black: " + b.getWhiteStones());
        LOG.info("White: " + b.getBlackStones());
        LOG.info(b.toString());
    }
}
