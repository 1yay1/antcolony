import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.Symbol;

import java.io.File;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yay on 20.12.2016.
 */
public class AntColony {
    public static void main(String[] args) {
        File f = new File("05-map-10x10-36border.txt");
        final Grid g = new Grid(f);

        final BlockingQueue<List<Integer>> blockingQueue = new ArrayBlockingQueue(1000);
        Ant a = new SalesmanAnt(g, blockingQueue, 1, 0.5, 0.5, 0.5, 0.8);
        for(int i = 0; i < 1000; i++) {
            a.buildPath();
        }
        //a.getPathInfo().values().stream().forEach((v) -> System.out.println(v.getDistance() + " : " + v.getPheromoneValue() + " : " + v.getWeightedValue()));
}
}
