import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yay on 23.01.2017.
 */
public class SalesmanAntACO extends SalesmanAnt {
    protected double t0, epsilon;

    public SalesmanAntACO(Grid g, BlockingQueue blockingQueue, int ants, int tourNumber, double q0, double alpha, double beta, double decayRate, double t0, double epsilon) {
        super(g, blockingQueue, ants, tourNumber, q0, alpha, beta, decayRate);
        this.t0 = t0;
        this.epsilon = epsilon;
    }

    @Override
    public void buildPath() {
        List<List<Integer>> allPathes = new ArrayList<>();
        //this.bestDistance = Double.MAX_VALUE;
        for(int a = 0; a < ants; a++) {
            List<Integer> bestPath = null;
            for (int i = 0; i < tourNumber; i++) {
                if (g.isUpdating()) {
                    pause = true;
                    while (g.isUpdating()) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    pause = false;
                    bestPath = null;
                    allPathes = new ArrayList<>();
                    bestGlobalPath = null;
                    this.bestDistance = Double.MAX_VALUE;
                    init();
                }
                while (path.size() < g.nodeCount()) {
                    path.add(chooseNextNode());
                    removePheromoneOnLastEdge( epsilon,  t0);
                }
                //producePheromone();

                if (bestPath == null || g.calculateDistanceFromPath(bestPath) > g.calculateDistanceFromPath(path)) {
                    bestPath = path;
                }
                allPathes.add(path);
                //
                init();
            }
            if (bestGlobalPath == null || g.calculateDistanceFromPath(bestGlobalPath) > g.calculateDistanceFromPath(bestPath)) {
                bestGlobalPath = bestPath;
            }
            allPathes.add(bestPath);
        }
        g.decay(bestGlobalPath, decayRate);
        producePheromone(bestGlobalPath);
        //g.setGlobalBestPath(bestGlobalPath);
    }


    protected void removePheromoneOnLastEdge(double epsilon, double t0) {
        EdgeInfo edgeInfo = g.getEdgeInfo(new Edge(path.get(path.size()-1), path.get(path.size()-2)));
        edgeInfo.setPheromone((1-epsilon) * edgeInfo.getPheromoneValue() + epsilon * t0);
    }
}
