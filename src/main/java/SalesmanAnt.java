import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by yay on 21.12.2016.
 */
public class SalesmanAnt extends Ant {
    protected double q0, alpha, beta, decayRate;

    public SalesmanAnt(Grid g, BlockingQueue blockingQueue,int ants, int tourNumber, double q0, double alpha, double beta, double decayRate) {
        super(g, blockingQueue, ants, tourNumber);
        this.q0 = q0;
        this.alpha = alpha;
        this.beta = beta;
        this.decayRate = decayRate;
    }

    @Override
    protected void producePheromone(List<Integer> path) {
        double totalDistance;
        totalDistance = g.calculateDistanceFromPath(path);

        Map<Edge, EdgeInfo> pathInfo = this.getPathInfo(path);
        for (EdgeInfo edgeInfo : pathInfo.values()) {
            edgeInfo.setPheromone(edgeInfo.getPheromoneValue() +  (1 / totalDistance));
        }
    }

    protected void producePheromone() {

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
        g.decayAll(decayRate);
        for(List<Integer> p: allPathes) {
            producePheromone(p);
        }
        producePheromone(bestGlobalPath);
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
    protected List<Edge> getEdgesSortedByWeight(Collection<Edge> edges) {
        List<Edge> edgesSortedByWeight = new ArrayList<>(edges);
        Collections.sort(edgesSortedByWeight, (Edge o1, Edge o2) -> g.getEdgeInfo(o2).compareTo(g.getEdgeInfo(o1)));
        return edgesSortedByWeight;
    }

    @Override
    protected Integer chooseNextNode() {
        Integer previous = getPath().get(getPath().size() - 1);
        Map<Edge, EdgeInfo> possibleEdges = getPossibleNextEdgeInfoMap();
        /*possibleEdges.keySet().stream().forEach((e) -> System.out.println(e));
        System.exit(0);*/
        possibleEdges.values().forEach((v) -> v.calculateWeightedValue(alpha, beta));
        List<Edge> edgesSortedByWeightList = getEdgesSortedByWeight(possibleEdges.keySet());
        int edgeCount = possibleEdges.size() - 1;
       /* edgesSortedByWeightList.forEach(e -> {
            System.out.println(possibleEdges.get(e).getWeightedValue());
        });*/
        double random = ThreadLocalRandom.current().nextDouble();

        if (random < this.q0) {
            Edge edge = edgesSortedByWeightList.get(0);
            Integer arr[] = edge.getArr();
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
                Integer arr[] = e.getArr();
                return previous.equals(arr[0]) ? arr[1] : arr[0];
            }
        }
        Edge edge = edgesSortedByWeightList.get(edgeCount);
        Integer arr[] = edge.getArr();
        return previous.equals(arr[0]) ? arr[1] : arr[0];
    }
}
