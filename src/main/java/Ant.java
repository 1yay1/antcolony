
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by yay on 20.12.2016.
 */
public abstract class Ant implements Runnable {
    private List<Integer> path;
    private final Grid g;
    private volatile boolean reset;
    private volatile boolean hasReceivedUpdate;
    private volatile boolean running;
    private BlockingQueue<List<Integer>> blockingQueue;
    private final int tourNumber;

    public List<Integer> getPath() {
        return path;
    }

    public Ant(Grid g, BlockingQueue blockingQueue, int tourNumber) {
        this.g = g;
        this.blockingQueue = blockingQueue;
        this.tourNumber = tourNumber;
        init();
    }

    /**
     * Init method initializes base values.
     * Is called in constructor, and any time there is a change to the grid of nodes.
     * If there is a change to the grid, while we are currently producing a new path, we need to abort and initialize again.
     */
    private void init() {
        this.path = new ArrayList<Integer>();
        this.path.add(0);
        reset = false;
        hasReceivedUpdate = false;
    }

    private Map<Edge, EdgeInfo> getPathInfo() {
        Set<Edge> edges = new HashSet<>();
        for(int i = 0; i < path.size() - 1; i++) {
            edges.add(new Edge(path.get(i), path.get(i+1)));
        }
        Map<Edge, EdgeInfo> edgeEdgeInfoMap = new HashMap<>();
        for(Edge e: edges) {
            edgeEdgeInfoMap.put(e, g.getOrCreateEdgeInfo(e));
        }
        return edgeEdgeInfoMap;
    }

    /**
     * Adds pheromone to the edgePheromoneMap in Grid g
     * Should be called for each edge traveled after building the path.
     */
    protected abstract void producePheromone();

    /**
     * Chooeses the next node to travel.
     * Should account for already added nodes the the path, since we don't want to visit a node twice.
     *
     * @return Integer id of the node to be traveled next.
     */
    protected abstract Integer chooseNextNode();


    /**
     * retrieves a map of possible next edges and their mapped edgeinfo.
     *
     * @return
     */
    protected Map<Edge, EdgeInfo> getPossibleNextEdgeInfoMap() {
        final Integer lastNode = path.get(path.size() - 1);
        return g.getNodeKeySet().stream()
                .filter((k) -> !path.contains(k))
                .map((k) -> new Edge(k, path.get(lastNode)))
                .collect(Collectors.toMap((e) -> e, g::getOrCreateEdgeInfo));
    }

    /**
     * Building the path. Reacts to changes to the grid.
     * If there are changes to the grid, it resets the currently built path.
     */
    protected void buildPath() {
        List<Integer> bestPath = null;
        for(int i = 0; i < tourNumber; i++) {
            while (path.size() < g.nodeCount()) {
                if (!reset) {
                    hasReceivedUpdate = false;
                    path.add(chooseNextNode());
                } else {
                    init();
                    hasReceivedUpdate = true;
                }
            }
            producePheromone();
            if(bestPath == null) {
                bestPath = path;
            } else {
                if(calculateDistanceFromPath(bestPath) > calculateDistanceFromPath(path)) {
                    bestPath = path;
                }
            }
            init();
        }
        path = bestPath;
        producePheromone();
    }

    private Double calculateDistanceFromPath(List<Integer> bestPath) {
        Map<Edge, EdgeInfo> map = getPathInfo();
        return map.values().stream()
                .mapToDouble((v) -> v.getDistance())
                .sum();
    }

    /**
     * Sets the volatile reset flag
     *
     * @return value of reset
     */
    public void reset() {
        reset = true;
    }

    /**
     * Checks the value of hasReceivedUpdate
     *
     * @return hasReceivedUpdate
     */
    public boolean hasReceivedUpdate() {
        return hasReceivedUpdate;
    }


    /**
     * Run method implementation of Callable interface
     *
     * @return puts newly built path in queue
     */
    @Override
    public void run() {
        running = true;
        while(running) {
            buildPath();
            boolean success = blockingQueue.offer(path);
            while (!success) {
                try {
                    Thread.sleep(1);
                    success = blockingQueue.offer(path);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sets the stop flag.
     */
    public void stop() {
        this.running = false;
    }
}
