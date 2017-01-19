import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yay on 21.12.2016.
 */
public class Grid {
    private final ConcurrentHashMap<Edge, EdgeInfo> synchronizedEdgePheromoneMap;
    private final ConcurrentHashMap<Integer, AntNode> synchronizedIntegerNodeMap;
    private volatile boolean updating;


    public Grid(File f) {
        this(loadFile(f));
    }

    public Grid(Map<Integer, AntNode> map) {
        synchronizedIntegerNodeMap = new ConcurrentHashMap(map);
        synchronizedEdgePheromoneMap = new ConcurrentHashMap();
        updating = false;
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

    private EdgeInfo getEdgeInfo(Edge edge) {
        return synchronizedEdgePheromoneMap.get(edge);
    }

    private void addEdgeInfo(Edge edge, EdgeInfo edgeInfo) {
        synchronizedEdgePheromoneMap.put(edge, edgeInfo);
    }

    protected EdgeInfo getOrCreateEdgeInfo(Edge e) {
        EdgeInfo edgeInfo = this.getEdgeInfo(e);
        synchronized (this) {
            if (edgeInfo == null) {
                Integer[] edgeIntegers = e.getAsArray();
                edgeInfo = new EdgeInfo(this.getNode(edgeIntegers[0]), this.getNode(edgeIntegers[1]));
                this.addEdgeInfo(e, edgeInfo);
            }
        }
        return edgeInfo;
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
        System.out.print("UPDATING..");
        updating = true;
        for (Ant a : ants) {
            while(!a.isPaused()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronizedIntegerNodeMap.put(n.getId(), n);
        updating = false;
        System.out.print("UPDATED!..");
    }

    /**
     * removes a node, same blocknig as addNode method
     * also has to remove all edges already added to the edge map containing the node to be removed
     *
     * @param id
     * @param ants
     */
    public void removeNode(Integer id, Collection<Ant> ants) {
        System.out.print("UPDATING..");
        updating = true;
        for (Ant a : ants) {
            while(!a.isPaused()) {
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
                for (Integer i : e) {
                    if (i == id) {
                        synchronizedEdgePheromoneMap.remove(e);
                        break;
                    }
                }
            }
        }
        for (Ant a : ants) {
            a.resume();
        }
        updating = false;
        System.out.print("UPDATED!..");
    }


    public boolean isUpdating() {
        return updating;
    }
}
