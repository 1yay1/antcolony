
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by yay on 20.12.2016.
 */
public abstract class Ant implements Runnable {
    private static final int MAX_ITER = 200;
    protected List<Integer> path;
    protected final Grid g;
    protected volatile boolean reset;
    protected volatile boolean hasReceivedUpdate;
    protected volatile boolean running;
    protected volatile boolean pause;
    protected BlockingQueue<List<Integer>> blockingQueue;
    protected final int tourNumber;
    protected final int ants;
    protected double bestDistance;
    protected List<Integer> bestGlobalPath;

    public List<Integer> getBestGlobalPath() {return bestGlobalPath;}

    public List<Integer> getPath() {
        return path;
    }

    public Ant(Grid g, BlockingQueue blockingQueue,int ants, int tourNumber) {
        this.g = g;
        this.blockingQueue = blockingQueue;
        this.tourNumber = tourNumber;
        this.ants = ants;
        bestDistance = Double.MAX_VALUE;
        bestGlobalPath = null;
        init();
    }

    /**
     * Init method initializes base values.
     * Is called in constructor, and any time there is a change to the grid of nodes.
     * If there is a change to the grid, while we are currently producing a new path, we need to abort and initialize again.
     */
    protected void init() {
        this.path = new ArrayList<>();
        this.path.add(ThreadLocalRandom.current().nextInt(g.nodeCount()) + 1);
        reset = false;
        hasReceivedUpdate = false;
        pause = false;
    }

    protected Map<Edge, EdgeInfo> getPathInfo(List<Integer> path) {
        Set<Edge> edges = new HashSet<>();
        //System.out.println(Arrays.toString(path.toArray()));
        for (int i = 0; i < path.size() - 1; i++) {
            edges.add(new Edge(path.get(i), path.get(i + 1)));
        }
        edges.add(new Edge(path.get(0), path.get(path.size() - 1)));
        Map<Edge, EdgeInfo> edgeEdgeInfoMap = new HashMap<>();
        for (Edge e : edges) {
            edgeEdgeInfoMap.put(e, g.getEdgeInfo(e));
        }
        return edgeEdgeInfoMap;
    }

    /**
     * Adds pheromone to the edgePheromoneMap in Grid g
     * Should be called for each edge traveled after building the path.
     */
    protected abstract void producePheromone(List<Integer> path);

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
        return g.getNodeKeySet().stream()
                .filter((k) -> !path.contains(k))
                .map((k) -> new Edge(k, path.get(path.size()-1)))
                .collect(Collectors.toMap((e) -> e, g::getEdgeInfo));
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

    public abstract void buildPath();
        /*List<Integer> bestPath = null;
        List<List<Integer>> allPathes = new ArrayList<>();
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
                allPathes = new ArrayList<>();
                this.bestDistance = Double.MAX_VALUE;
                init();
            }
            while (path.size() < g.nodeCount()) {
                path.add(chooseNextNode());

            }
            //producePheromone();

            if (bestPath == null) {
                bestPath = path;
            } else {
                Double tmp_distance = calculateDistanceFromPath(this.getPath());
                if (this.bestDistance > tmp_distance) {
                    this.bestDistance = tmp_distance;
                    bestPath = this.path;
                }
            }
            allPathes.add(path);
            //
            init();
        }
        path = bestPath;
        this.bestDistance = calculateDistanceFromPath(bestPath); // Right side of the assigment can be replaced by a variable so the distance calculation is not perfomed twice
        for(List<Integer> p : allPathes) {
            producePheromone(p);
        }
        producePheromone(bestPath);
        System.out.print(Arrays.toString(bestPath.toArray()));*/


/*    protected Double calculateDistanceFromPath(List<Integer> path) {
        Map<Edge, EdgeInfo> map = g.getPathInfo(path);
        return map.values().stream()
                .mapToDouble((v) -> v.getDistance())
                .sum();
    }*/

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
        int i = 0;
        while (true){
            buildPath();
            synchronized (blockingQueue) {
                List<Integer> oldPath = blockingQueue.poll();
                if (oldPath == null || g.calculateDistanceFromPath(oldPath) > g.calculateDistanceFromPath(bestGlobalPath)) {
                    oldPath = bestGlobalPath;
                }
                blockingQueue.offer(oldPath);
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
