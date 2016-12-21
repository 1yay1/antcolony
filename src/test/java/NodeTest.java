import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yay on 21.12.2016.
 */
public class NodeTest {
    @Test
    public void testEquals() throws Exception {
        assertEquals(new Node(0,1), new Node(0,1));
        assertEquals(new Node(0,1), new Node(1,0));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(new Node(0,1).hashCode(), new Node(0,1).hashCode());
        assertEquals(new Node(1,0).hashCode(), new Node(0,1).hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(new Node(0,1).toString(), new Node(0,1).toString());
        assertEquals(new Node(0,1).toString(), new Node(1,0).toString());
    }

}