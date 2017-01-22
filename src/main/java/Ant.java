
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
    private volatile boolean pause;
    private BlockingQueue<List<Integer>> blockingQueue;
    private final int tourNumber;
    private double bestDistance;

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
        this.path = new ArrayList<>();
        this.path.add(1);
        reset = false;
        hasReceivedUpdate = false;
        pause = false;
    }

    protected Map<Edge, EdgeInfo> getPathInfo() {
        Set<Edge> edges = new HashSet<>();
        //System.out.println(Arrays.toString(path.toArray()));
        for (int i = 0; i < path.size() - 1; i++) {
            edges.add(new Edge(path.get(i), path.get(i + 1)));
        }
        edges.add(new Edge(path.get(0), path.get(path.size() - 1)));
        Map<Edge, EdgeInfo> edgeEdgeInfoMap = new HashMap<>();
        for (Edge e : edges) {
            edgeEdgeInfoMap.put(e, g.getOrCreateEdgeInfo(e));
        }
        return edgeEdgeInfoMap;
    }

    /**
     * Adds pheromone to the edgePheromoneMap in Grid g
     * Should be called for each edge traveled after building the path.
     */
    protected abstract void producePheromone(Boolean isGlobal);

    /**
     * Chooses the next node to travel.
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
        //final Integer lastNode = path.get(path.size() - 1);

        return g.getNodeKeySet().stream()
                .filter((k) -> !path.contains(k))
                .map((k) -> new Edge(k, path.get(path.size()-1)))
                .collect(Collectors.toMap((e) -> e, g::getOrCreateEdgeInfo));
    }

    protected double getBestDistance() {
        return this.bestDistance;
    }

    public void pause() {
        pause = true;
    }

    public void resume() {
        pause = false;
    }

    public boolean isPaused() {
        return pause;
    }

    /**
     * Building the path. Reacts to changes to the grid.
     * If there are changes to the grid, it resets the currently built path.
     */
    protected void buildPath() {
        List<Integer> bestPath = null;
        this.bestDistance = Double.MAX_VALUE;
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
                this.bestDistance = Double.MAX_VALUE;
                init();
            }
            while (path.size() < g.nodeCount()) {
                path.add(chooseNextNode());
                producePheromone(false);
            }
            
            if (bestPath == null) {
                bestPath = path;
            } else {
                Double tmp_distance = calculateDistanceFromPath(this.getPath());
                if (this.bestDistance > tmp_distance) {
                    this.bestDistance = tmp_distance;
                    bestPath = this.path;
                }
            }
            //
            init();
        }
        path = bestPath;
        this.bestDistance = calculateDistanceFromPath(bestPath); // Right side of the assigment can be replaced by a variable so the distance calculation is not perfomed twice
        producePheromone(true);
        System.out.println(Arrays.toString(bestPath.toArray()));
    }

    protected Double calculateDistanceFromPath(List<Integer> bestPath) {
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
        while (running) {
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
