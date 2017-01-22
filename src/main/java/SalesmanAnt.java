
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
        if (isGlobal)
            path_total_distance = this.getBestDistance();
        else
            path_total_distance = this.calculateDistanceFromPath(this.getPath());

        Map<Edge, EdgeInfo> path_segments = this.getPathInfo();
        for (Edge key : path_segments.keySet()) {
            double new_pheromone;
            if (isGlobal)
                new_pheromone = (1 - this.alpha) * path_segments.get(key).getPheromoneValue() + this.alpha * (1 / path_total_distance);
            else
                new_pheromone = (1 - this.alpha) * path_segments.get(key).getPheromoneValue() + this.alpha * this.t0;
            path_segments.get(key).setPheromone(beta, new_pheromone);
        }

    }

    /*
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
        List<Edge> edgesSortedByWeightList = possibleEdges.keySet().stream()
                .sorted((k1, k2) -> possibleEdges.get(k1).compareTo(possibleEdges.get(k2)) * -1) //multiply by -1 for reverse order. highest first is needed.
                .collect(Collectors.toList());
        int edgeCount = possibleEdges.size() - 1;

        double random = ThreadLocalRandom.current().nextDouble();

        if (random < this.q0) {
            Edge edge = edgesSortedByWeightList.get(0);
            Integer arr[] = edge.getAsArray();
            return previous.equals(arr[0]) ? arr[1] : arr[0];
        }

        double sumValues = 0;
        for (Edge e : edgesSortedByWeightList) {
            sumValues += possibleEdges.get(e).getWeightedValue();
        }
        //List<WeightedEdge> normalizedPathValue;
        sumValues = sumValues * ThreadLocalRandom.current().nextDouble();

        for (Edge e : edgesSortedByWeightList) {
            //normalizedPathValue.add(new WeightedEdge((Integer) e.toArray()[0], (Integer) e.toArray()[1], sumValues));
            sumValues -= possibleEdges.get(e).getWeightedValue();
            if (sumValues < 0) {
                Integer arr[] = e.getAsArray();
                return previous.equals(arr[0]) ? arr[1] : arr[0];
            }
        }
        Edge edge = edgesSortedByWeightList.get(edgeCount);
        Integer arr[] = edge.getAsArray();
        return previous.equals(arr[0]) ? arr[1] : arr[0];
    }
}
