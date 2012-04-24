/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.earthlingz.games.reversi.joinfork;

import junit.framework.Assert;
import org.junit.Test;


/**
 *
 * @author smurawski
 */
public class BoardMoveTest {
    

    @Test public void simpleTest() //for the sake of code coverage ;)
    {
        BoardMove a = new BoardMove(1, 1);
        BoardMove b = new BoardMove(2, 2);
        BoardMove c = new BoardMove(1, 1);
        
        Assert.assertEquals(a, c);
        Assert.assertNotSame(b, c);
        Assert.assertNotSame(b, null);
        Assert.assertNotSame(b, new Object());
        
        Assert.assertEquals(a.hashCode(), c.hashCode());
        Assert.assertNotSame(b.hashCode(), c.hashCode());
    }
}
