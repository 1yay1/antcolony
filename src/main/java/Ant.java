
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by yay on 20.12.2016.
 */
public abstract class Ant implements Callable<List<Integer>> {
    private List<Integer> path;
    private final Grid g;
    private volatile boolean reset;
    private volatile boolean hasReceivedUpdate;

    public List<Integer> getPath() {
        return path;
    }

    public Ant(Grid g) {
        this.g = g;
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
        while (path.size() < g.nodeCount()) {
            if (!reset) {
                hasReceivedUpdate = false;
                path.add(chooseNextNode());
            } else {
                init();
                hasReceivedUpdate = true;
            }
        }
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
     * Call method implementation of Callable interface
     *
     * @return List<Integer> of the built path.
     */
    public List<Integer> call() {
        buildPath();
        return path;
    }
}
