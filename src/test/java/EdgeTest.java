import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yay on 21.12.2016.
 */
public class EdgeTest {
    @Test
    public void testEquals() throws Exception {
        assertEquals(new Edge(0,1), new Edge(0,1));
        assertEquals(new Edge(0,1), new Edge(1,0));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(new Edge(0,1).hashCode(), new Edge(0,1).hashCode());
        assertEquals(new Edge(1,0).hashCode(), new Edge(0,1).hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(new Edge(0,1).toString(), new Edge(0,1).toString());
        assertEquals(new Edge(0,1).toString(), new Edge(1,0).toString());
    }

}