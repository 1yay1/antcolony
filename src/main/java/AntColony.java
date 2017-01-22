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
        File f = new File("05-map-10x10-36border.txt");
        final Grid g = new Grid(f);

        double q0, alpha, beta, decayRate;
        double step = 0.1;

        q0 = 0.1;
        alpha = 0.5;
        beta = 0.9;
        decayRate = 0.08;

        final BlockingQueue<List<Integer>> blockingQueue = new ArrayBlockingQueue(1000);
        Ant a = new SalesmanAnt(g, blockingQueue, 100, q0, alpha, beta, decayRate);

        System.out.print("Paras: q0: " + q0 + " alpha: " + alpha + " beta: " + beta + " t0: " + decayRate + "\n");
        for (int i = 0; i < 2000; i++) {
            a.buildPath();
            if (a.calculateDistanceFromPath(a.getBestGlobalPath()) <= 36) {
                System.out.println("\ti: " + i + " " + Arrays.toString(a.getBestGlobalPath().toArray()) + " D : " + a.calculateDistanceFromPath(a.getPath()));
                break;
            }
        }
        System.out.println("\ti: " + 2000 + " " + Arrays.toString(a.getBestGlobalPath().toArray()) + " D : " + a.calculateDistanceFromPath(a.getPath()));

        //System.out.println("\t" + Arrays.toString(a.getPath().toArray()) +" D : " + a.calculateDistanceFromPath(a.getPath()));

        //a.getPathInfo().values().stream().forEach((v) -> System.out.println(v.getDistance() + " : " + v.getPheromoneValue() + " : " + v.getWeightedValue()));
    }
}
