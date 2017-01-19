import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yay on 21.12.2016.
 */
public class AntNodeTest {
    @Test
    public void testEquals() throws Exception {
        assertEquals(new AntNode(0,1), new AntNode(0,1));
        assertEquals(new AntNode(0,1), new AntNode(1,0));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(new AntNode(0,1).hashCode(), new AntNode(0,1).hashCode());
        assertEquals(new AntNode(1,0).hashCode(), new AntNode(0,1).hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(new AntNode(0,1).toString(), new AntNode(0,1).toString());
        assertEquals(new AntNode(0,1).toString(), new AntNode(1,0).toString());
    }

}