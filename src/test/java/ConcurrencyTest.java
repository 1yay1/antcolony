import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by yay on 22.12.2016.
 */
public class ConcurrencyTest {
    @Test
    public void testConcurrency() {
        Map<Integer, AntNode> map = new HashMap<>();
        AntNode[] antNodes = new AntNode[]{
                new AntNode(1, 1),
                new AntNode(2, 2),
                new AntNode(3, 3),
                new AntNode(3, 6)
        };
        for (int i = 0; i < antNodes.length; i++) {
            map.put(i, antNodes[i]);
        }
        Grid g = new Grid(map);
        ArrayBlockingQueue pathQueue = new ArrayBlockingQueue(100000);

    }
}
