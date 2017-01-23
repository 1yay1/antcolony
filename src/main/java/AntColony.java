import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.Symbol;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yay on 20.12.2016.
 */
public class AntColony {
    public static void main(String[] args) {
        File f = new File("06-map-100x100-200.txt");
        final Grid g = new Grid(f);

        double q0, alpha, beta, decayRate;
        double step = 0.1;

        int antCount = 25;
        int tours = 20;
        q0 = 0.25;
        alpha = 1;
        beta = 2;
        decayRate = 0.2;

        final BlockingQueue<List<Integer>> blockingQueue = new ArrayBlockingQueue(1);
        Ant aMeise = new SalesmanAnt(g, blockingQueue, antCount, tours , q0, alpha, beta, decayRate);
        Thread aThread = new Thread(aMeise);
        aThread.start();
        System.out.print("Paras: q0: " + q0 + " alpha: " + alpha + " beta: " + beta + " t0: " + decayRate + "\n");

        while (true) {
            List<Integer> path = blockingQueue.peek();
            if(path == null) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("D: " + g.calculateDistanceFromPath(path));
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //System.out.println("\t" + Arrays.toString(a.getPath().toArray()) +" D : " + a.calculateDistanceFromPath(a.getPath()));

        //a.getPathInfo().values().stream().forEach((v) -> System.out.println(v.getDistance() + " : " + v.getPheromoneValue() + " : " + v.getWeightedValue()));
    }
}
