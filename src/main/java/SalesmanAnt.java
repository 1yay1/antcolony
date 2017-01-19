
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by yay on 21.12.2016.
 */
public class SalesmanAnt extends Ant {
    private double q0, alpha, beta, t0;

    public SalesmanAnt(Grid g, BlockingQueue blockingQueue, int tourNumber, double q0, double alpha, double beta, double t0) {
        super(g, blockingQueue, tourNumber);
        this.q0 = q0;
        this.alpha = alpha;
        this.beta = beta;
        this.t0 = t0;
    }

    @Override
    protected void producePheromone(Boolean isGlobal) {
        double path_total_distance;
        if (isGlobal == true)
            path_total_distance = this.getBestDistance();
        else
            path_total_distance = this.calculateDistanceFromPath(this.getPath());

        Map<Edge, EdgeInfo> path_segments = this.getPathInfo();
        for (Edge key : path_segments.keySet()) {
            double new_pheromone;
            if (isGlobal == true)
                new_pheromone = (1 - this.alpha) * path_segments.get(key).getPheromone().getValue() + this.alpha * (1 / path_total_distance);
            else
                new_pheromone = (1 - this.alpha) * path_segments.get(key).getPheromone().getValue() + this.alpha * this.t0;
            path_segments.get(key).getPheromone().setValue((float) new_pheromone);
        }

    }

    @Override
    protected Integer chooseNextNode() {
        Map<Edge, EdgeInfo> possibleEdges = getPossibleNextEdgeInfoMap();
        double random = ThreadLocalRandom.current().nextDouble(1);
        Map<Edge, Double> weightedPathValue = new HashMap<>();
        for (Map.Entry<Edge, EdgeInfo> entry : possibleEdges.entrySet()) {
            double distance = entry.getValue().getDistance();
            double pheromone = entry.getValue().getPheromone().getValue();
            weightedPathValue.put(entry.getKey(), Math.pow((1 / distance), beta) * pheromone);
        }
        if (random < this.q0) {
            Edge max = weightedPathValue.entrySet().stream().max((entry1, entry2) ->
                    entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            return (getPath().get(getPath().size() - 1).equals(max.getAsArray()[0]))
                    ? max.getAsArray()[1] : max.getAsArray()[0];

        }
        double sumValues = 0;
        for (Edge e : weightedPathValue.keySet()) {
            sumValues += weightedPathValue.get(e);
        }
        List<WeightedEdge> normalizedPathValue = new ArrayList<>();
        for (Edge e : weightedPathValue.keySet()) {
            normalizedPathValue.add(new WeightedEdge((Integer) e.toArray()[0], (Integer) e.toArray()[1], sumValues));
            sumValues -= weightedPathValue.get(e);
        }
        double randomselectPath = ThreadLocalRandom.current().nextDouble(sumValues);
        for (WeightedEdge we : normalizedPathValue) {
            if (we.weightedWay < randomselectPath) {
                return (getPath().get(getPath().size() - 1).equals(we.getAsArray()[0]))
                        ? we.getAsArray()[1] : we.getAsArray()[0];
            }
        }
        return null;
    }


}
