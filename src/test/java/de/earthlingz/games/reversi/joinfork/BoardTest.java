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
import org.junit.*;
import static org.junit.Assert.*;

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
        consoleAppender.setThreshold(Level.INFO);
        Logger.getRootLogger().addAppender(consoleAppender);
    }
    
    @AfterClass
    public static void tearDown() {
        Logger.getRootLogger().removeAllAppenders();
    }
    
    @Test public void playGameNoChecks()
    {
        Board b = new Board(true);
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
        for(int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if(b.getBoolboard()[i][j] == STATE.SELECTABLE)
                {
                    assertTrue(i +"/" +j + " is not legal but was marked as legal",b.isLegalMove(i, j));
                }
            }
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
        assertEquals(false, b.isNextPlayerBlack()); //white has next move
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
