import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by yay on 21.12.2016.
 */
public class Grid {
    private final ConcurrentHashMap<Edge, EdgeInfo> synchronizedEdgePheromoneMap;
    private final ConcurrentHashMap<Integer, AntNode> synchronizedIntegerNodeMap;
    private volatile boolean updating;
    private volatile List<Integer> globalBestPath;


    public Grid(File f) {
        this(loadFile(f));
    }

    public Grid(Map<Integer, AntNode> map) {
        synchronizedIntegerNodeMap = new ConcurrentHashMap(map);
        synchronizedEdgePheromoneMap = new ConcurrentHashMap();
        calculateEdgeInfos();
        updating = false;
        globalBestPath = null;
    }

    public synchronized void setGlobalBestPath(List<Integer> globalBestPath) {
        if (this.globalBestPath == null || calculateDistanceFromPath(globalBestPath) < calculateDistanceFromPath(globalBestPath)) {
            this.globalBestPath = globalBestPath;
        }
    }

    public List<Integer> getGlobalBestPath() {
        return globalBestPath;
    }

    public int nodeCount() {
        return synchronizedIntegerNodeMap.size();
    }

    public Set<Integer> getNodeKeySet() {
        return synchronizedIntegerNodeMap.keySet();
    }

    public Set<Edge> getEdgeKeySet() {
        return synchronizedEdgePheromoneMap.keySet();
    }


    private void calculateEdgeInfos() {
        ArrayList<Integer> nodes = synchronizedIntegerNodeMap.keySet().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                Edge e = new Edge(nodes.get(i), nodes.get(j));
                initializeEdgeInfo(e);
            }
        }
    }

    /**
     * Loads a file as the city grid.
     *
     * @param f File object to be read
     * @return Map of the read Nodes
     */
    private static Map<Integer, AntNode> loadFile(File f) {
        Map<Integer, AntNode> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            int y = countLines(f) - 1;
            String line = br.readLine();
            while (line != null) {
                String[] splitLine = line.split("\\s+");
                for (int x = 0; x < splitLine.length; x++) {
                    if (!splitLine[x].equals("00") && !splitLine[x].equals("0")) {
                        Integer id = Integer.parseInt(splitLine[x]);
                        AntNode n = new AntNode(x, y);
                        map.put(id, n);
                    }
                }
                y--;
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Helper method to count lines in a file
     *
     * @param gridFile
     * @return amount of linebreaks in a file
     */
    private static int countLines(File gridFile) {
        int lines = 0;
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader(gridFile));
            lnr.skip(Integer.MAX_VALUE);
            lines = lnr.getLineNumber();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public EdgeInfo getEdgeInfo(Edge edge) {
        //System.out.println(edge.toString());
        return synchronizedEdgePheromoneMap.get(edge);
    }

    private void addEdgeInfo(Edge edge, EdgeInfo edgeInfo) {
        synchronizedEdgePheromoneMap.put(edge, edgeInfo);
    }

    /*protected EdgeInfo getEdgeInfo(Edge e) {
        EdgeInfo edgeInfo = this.getEdgeInfo(e);
        synchronized (this) {
            if (edgeInfo == null) {
                Integer[] edgeIntegers = e.getArr();
                edgeInfo = new EdgeInfo(this.getNode(edgeIntegers[0]), this.getNode(edgeIntegers[1]));
                this.addEdgeInfo(e, edgeInfo);
            }
        }
        return edgeInfo;
    }*/

    private void initializeEdgeInfo(Edge e) {
        Integer[] edgeIntegers = e.getArr();
        EdgeInfo edgeInfo = new EdgeInfo(this.getNode(edgeIntegers[0]), this.getNode(edgeIntegers[1]));
        this.addEdgeInfo(e, edgeInfo);
    }

    protected void decayAll(double decayRate) {
        for (EdgeInfo edgeInfo : synchronizedEdgePheromoneMap.values()) {
            edgeInfo.setPheromone(edgeInfo.getPheromoneValue() * (1 - decayRate));
        }
    }

    protected void decay(List<Integer> path, double decayRate) {
        for(EdgeInfo edgeInfo: getPathInfo(path).values()) {
            edgeInfo.setPheromone(edgeInfo.getPheromoneValue() * (1 - decayRate));
        }
    }

    /**
     * Gets all edges that contain a node specified by id
     *
     * @param id of the node
     * @return sorted List<Map<Edge,Pheromone> of all Edges with their Pheromone value containing the node with the given id
     *//*
    public List<Map<Edge, EdgeInfo>> getEdgeInfoFrom(Integer from) {
        return synchronizedEdgePheromoneMap.entrySet().stream()
                .filter((m) -> m.getKey().contains(from))
                .sorted((e1, e2) -> e1.getValue().getPheromone().getValue().compareTo(e2.getValue().getPheromone().getValue()))
                .map((e) -> {
                    Map<Edge, EdgeInfo> m = new HashMap<>();
                    m.put(e.getKey(), e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }*/
    public AntNode getNode(Integer id) {
        return synchronizedIntegerNodeMap.get(id);
    }

    /**
     * adds a new node to the grid, blocks other threads from accessing this object.
     * waits for all working ants to receive the updates
     *
     * @param n
     * @param ants
     */
    public void addNode(AntNode n, Collection<Ant> ants) {
        System.out.print("Adding " + n.getId());
        updating = true;
        for (Ant a : ants) {
            while (!a.isPaused()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronizedIntegerNodeMap.forEach((k, v) -> addEdgeInfo(new Edge(k, n.getId()), new EdgeInfo(v, n)));
        synchronizedIntegerNodeMap.put(n.getId(), n);
        this.globalBestPath = null;
        updating = false;
        System.out.print("Added " + n.getId());
    }

    public Integer getRandomStartingNode() {
        List<Integer> nodes = new ArrayList<>(getNodeKeySet());
        Collections.shuffle(nodes, ThreadLocalRandom.current());
        return nodes.get(0);
    }
    /**
     * removes a node, same blocking as addNode method
     * also has to remove all edges already added to the edge map containing the node to be removed
     *
     * @param id
     * @param ants
     */
    public void removeNode(Integer id, Collection<Ant> ants) {
        System.out.println("Removing " + id);
        updating = true;
        for (Ant a : ants) {
            while (!a.isPaused()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronized (this) {
            synchronizedIntegerNodeMap.remove(id);
            for (Edge e : synchronizedEdgePheromoneMap.keySet()) {
                for (Integer i : e.getArr()) {
                    if (i == id) {
                        synchronizedEdgePheromoneMap.remove(e);
                        break;
                    }
                }
            }
        }
        this.globalBestPath = null;
        for (Ant a : ants) {
            a.resume();
        }
        updating = false;
        System.out.println("Removed " + id);
    }

    public String getNodesAsString() {
        StringBuilder sb = new StringBuilder();
        this.synchronizedIntegerNodeMap.forEach((v, k) -> {
            sb.append(v.toString());
            sb.append((" : "));
            sb.append(k.toString());
            sb.append("\n");
        });
        return sb.toString();
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
            edgeEdgeInfoMap.put(e, this.getEdgeInfo(e));
        }
        return edgeEdgeInfoMap;
    }

    protected Double calculateDistanceFromPath(List<Integer> path) {
        Map<Edge, EdgeInfo> map = getPathInfo(path);
        return map.values().stream()
                .mapToDouble((v) -> v.getDistance())
                .sum();
    }

    public boolean isUpdating() {
        return updating;
    }
}
