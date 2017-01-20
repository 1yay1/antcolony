
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
                new_pheromone = (1 - this.alpha) * path_segments.get(key).getPheromoneValue() + this.alpha * (1 / path_total_distance);
            else
                new_pheromone = (1 - this.alpha) * path_segments.get(key).getPheromoneValue() + this.alpha * this.t0;
            path_segments.get(key).setPheromone(new_pheromone);
        }

    }

    /*
    todo:
        remove weightededge class, distance and pheromone is saved in edgeinfo fix roulette select.
           // Returns the selected index based on the weights(probabilities)
            int rouletteSelect(double[] weight) {
                // calculate the total weight
                double weight_sum = 0;
                for(int i=0; i<weight.length; i++) {
                    weight_sum += weight[i];
                }
                // get a random value
                double value = randUniformPositive() * weight_sum;
                // locate the random value based on the weights
                for(int i=0; i<weight.length; i++) {
                    value -= weight[i];
                    if(value <= 0) return i;
                }
                // when rounding errors occur, we return the last item's index
                return weight.length - 1;
            }
     */
    @Override
    protected Integer chooseNextNode() {
        Integer previous = getPath().get(getPath().size()-1);
        Map<Edge, EdgeInfo> possibleEdges = getPossibleNextEdgeInfoMap();
        double random = ThreadLocalRandom.current().nextDouble(1);
        Map<Edge, Double> weightedPathValue = new HashMap<>();
        for (Map.Entry<Edge, EdgeInfo> entry : possibleEdges.entrySet()) {
            double distance = entry.getValue().getDistance();
            double pheromone = entry.getValue().getPheromoneValue();
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
        //List<WeightedEdge> normalizedPathValue;
        sumValues = sumValues * ThreadLocalRandom.current().nextDouble();
        ArrayList<Edge> edges = new ArrayList(weightedPathValue.keySet());
        for (Edge e : edges) {
            //normalizedPathValue.add(new WeightedEdge((Integer) e.toArray()[0], (Integer) e.toArray()[1], sumValues));
            sumValues -= weightedPathValue.get(e);
            if (sumValues < 0) {
                Object[] arr = e.toArray();
                Integer first = (Integer) arr[0];
                Integer second = (Integer) arr[1];
                if (first.equals(previous)) {
                    return second;
                } else {
                    return first;
                }
            }
        }
        Edge e = edges.get(edges.size()-1);
        Object[] arr = e.toArray();
        Integer first = (Integer) arr[0];
        Integer second = (Integer) arr[1];
        if (first.equals(previous)) {
            return second;
        } else {
            return first;
        }

        /*double randomselectPath = ThreadLocalRandom.current().nextDouble(sumValues);
        for (WeightedEdge we : normalizedPathValue) {
            if (we.weightedWay < randomselectPath) {
                return (getPath().get(getPath().size() - 1).equals(we.getAsArray()[0]))
                        ? we.getAsArray()[1] : we.getAsArray()[0];
            }
        }
        return null;*/
    }


}
