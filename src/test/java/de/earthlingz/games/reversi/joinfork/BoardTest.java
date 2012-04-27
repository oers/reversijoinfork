/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import de.earthlingz.games.reversi.joinfork.Board.STATE;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author smurawski
 */
public class BoardTest {
    
    public BoardTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        final ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
        consoleAppender.setThreshold(Level.DEBUG);
        Logger.getRootLogger().addAppender(consoleAppender);
    }
    
    @AfterClass
    public static void tearDown() {
        Logger.getRootLogger().removeAllAppenders();
    }
    
    @Test public void bitmasks()
    {                       
        Board b = new Board(true, 0, 0, true);
        
        for(int i = 1; i < 8; i++)
        {
            for(int j = 1; j < 8; j++)
            {
                assertTrue("Empty: " + b.getBoolboard(), b.isEmpty(i, j));
            }
        }

        System.out.println((long)1 << 63);
        
        //black
        assertTrue("Empty: " + b.getBoolboard(), b.isEmpty(5, 5));
        b.setStone(5, 5, 1);
        assertFalse("Not Empty: " + b.getBoolboard(), b.isEmpty(5, 5));
        assertTrue("Black: " + b.getBoolboard(), b.isStone(5, 5, 1));
        assertEquals(STATE.BLACK, b.getState(5, 5));
        
        assertTrue("Empty: " + b.toString(), b.isEmpty(0, 0));
        assertTrue("Empty: " + b.toString(), b.isEmpty(1, 1));
        assertTrue("Empty: " + b.toString(), b.isEmpty(2, 2));
        assertTrue("Empty: " + b.toString(), b.isEmpty(3, 3));
        assertTrue("Empty: " + b.toString(), b.isEmpty(4, 4));
        assertTrue("Empty: " + b.toString(), b.isEmpty(6, 6));
        assertTrue("Empty: " + b.toString(), b.isEmpty(7, 7));
        
        
        //white
        assertTrue("Empty: " + b.toString(), b.isEmpty(6, 6));
        b.setStone(6, 6, 0);
        assertFalse("Not Empty: " + b.getBoolboard(), b.isEmpty(6, 6));
        assertTrue("White:" + b.getBoolboard(), b.isStone(6, 6, 0));
        assertEquals(STATE.WHITE, b.getState(6, 6));
        
        b.setStone(7, 7, 0); //h8 is special
        assertFalse("Not Empty: " + b.getBoolboard(), b.isEmpty(7, 7));
        assertEquals(STATE.WHITE, b.getState(7, 7));
        assertTrue("White:" + b.getBoolboard(), b.isStone(7, 7, 0));
    }
    
    @Test public void playGameNoChecks()
    {
        Board b = new Board(true);
        //wb
        //bw     
        assertEquals("Black " + b, STATE.BLACK, b.getState(3, 4));
        assertEquals("Black " + b, STATE.BLACK, b.getState(4, 3));
        assertEquals("White " + b, STATE.WHITE, b.getState(3, 3));
        assertEquals("White " + b, STATE.WHITE, b.getState(4, 4));
        assertTrue("Black " + b, b.isStone(3, 4, 1));
        assertTrue("Black " + b, b.isStone(4, 3, 1));
        assertTrue("White " + b, b.isStone(3, 3, 0));
        assertTrue("White " + b, b.isStone(4, 4, 0));
        
        
        assertTrue("" +b, b.markNextMoves()); //mark available moves
        assertTrue("" +b, b.markNextMoves()); //mark available moves, must work twice
        
        assertEquals("Black", b.getState(3, 4), STATE.BLACK);
        assertEquals("Black", b.getState(4, 3), STATE.BLACK);
        assertEquals("White", b.getState(3, 3), STATE.WHITE);
        assertEquals("White", b.getState(4, 4), STATE.WHITE);
        
        //new possible moves all Black of Course
        assertEquals("Selectable", b.getState(2, 3), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(4, 5), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(3, 2), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(5, 4), STATE.SELECTABLE);
        
        assertEquals(true, b.isNextPlayerBlack());
        
        //make Illegal Move and expect an Exception

        assertFalse(b.makeMove(1, 1));
   
        //nothing has changed
        assertEquals("Black", b.getState(3, 4), STATE.BLACK);
        assertEquals("Black", b.getState(4, 3), STATE.BLACK);
        assertEquals("White", b.getState(3, 3), STATE.WHITE);
        assertEquals("White", b.getState(4, 4), STATE.WHITE);
        
        //new possible moves all Black of Course
        assertEquals("Selectable", b.getState(2, 3), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(4, 5), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(3, 2), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(5, 4), STATE.SELECTABLE);
        
        //make Legal Move
        assertTrue(b.makeMove(2, 3));
           //nothing has changed
        assertEquals("Black", b.getState(2, 3), STATE.BLACK);
        assertEquals("Black", b.getState(3, 4), STATE.BLACK);
        assertEquals("Black", b.getState(4, 3), STATE.BLACK);
        assertEquals("White", b.getState(3, 3), STATE.BLACK);
        assertEquals("White", b.getState(4, 4), STATE.WHITE);
        
         b.markNextMoves(); //mark available moves
        
        //all marked fields have to be playable
        for(BoardMove move : b.getPossibleMoves())
        {
           assertTrue(move+ " is not legal but was marked as legal",b.isLegalMove(move.getRow(), move.getColumn()));

        }
        //try this move again, should fail
       assertFalse(b.makeMove(2, 3));  
    }
    
    @Test public void playGame()
    {
        Board b = new Board(false);
        b.toString(); // just need to know, that it works
        //wb
        //bw     
        assertEquals("Black", b.getState(3, 4), STATE.BLACK);
        assertEquals("Black", b.getState(4, 3), STATE.BLACK);
        assertEquals("White", b.getState(3, 3), STATE.WHITE);
        assertEquals("White", b.getState(4, 4), STATE.WHITE);
        
        b.markNextMoves(); //mark available moves
        
        assertEquals("Black", b.getState(3, 4), STATE.BLACK);
        assertEquals("Black", b.getState(4, 3), STATE.BLACK);
        assertEquals("White", b.getState(3, 3), STATE.WHITE);
        assertEquals("White", b.getState(4, 4), STATE.WHITE);
        
        //new possible moves all Black of Course
        assertEquals("Selectable", b.getState(2, 3), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(4, 5), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(3, 2), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(5, 4), STATE.SELECTABLE);
        
        assertEquals(true, b.isNextPlayerBlack());
        
        //make Illegal Move
        assertFalse(b.makeMove(1, 1));
        
        //nothing has changed
        assertEquals("Black", b.getState(3, 4), STATE.BLACK);
        assertEquals("Black", b.getState(4, 3), STATE.BLACK);
        assertEquals("White", b.getState(3, 3), STATE.WHITE);
        assertEquals("White", b.getState(4, 4), STATE.WHITE);
        
        //new possible moves all Black of Course
        assertEquals("Selectable", b.getState(2, 3), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(4, 5), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(3, 2), STATE.SELECTABLE);
        assertEquals("Selectable", b.getState(5, 4), STATE.SELECTABLE);
        
        //make Legal Move
        assertTrue(b.makeMove(2, 3));
        assertEquals(1, b.getWhiteStones());
        assertEquals(4, b.getBlackStones());
        
        assertEquals(b.toStringBoard(), false, b.isNextPlayerBlack()); //white has next move
        //nothing has changed
        assertEquals("Black", b.getState(2, 3), STATE.BLACK); //move we made
        assertEquals("Black", b.getState(3, 4), STATE.BLACK);
        assertEquals("Black", b.getState(4, 3), STATE.BLACK);
        assertEquals("White", b.getState(3, 3), STATE.BLACK);
        assertEquals("White", b.getState(4, 4), STATE.WHITE);
        
        b.markNextMoves();
        b.toString(); // just need to know, that it works
        
        //try this move again
        assertFalse(b.makeMove(2, 3));
    }
}
