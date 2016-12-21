import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by yay on 21.12.2016.
 */
public class Grid {
    private final ConcurrentHashMap<Edge, Pheromone> synchronizedEdgePheromoneMap;
    private final ConcurrentHashMap<Integer, Node> synchronizedIntegerNodeMap;
    private volatile boolean updated;


    public Grid(File f) {
        Map m = loadFile(f);
        synchronizedIntegerNodeMap = new ConcurrentHashMap();
        synchronizedEdgePheromoneMap = new ConcurrentHashMap();
        updated = false;
    }

    public int size() {
        return synchronizedIntegerNodeMap.size();
    }

    /**
     * Loads a file as the city grid.
     *
     * @param f File object to be read
     * @return Map of the read Nodes
     */
    private Map<Integer, Node> loadFile(File f) {
        Map<Integer, Node> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            int y = countLines(f) - 1;
            String line = br.readLine();
            while (line != null) {
                String[] splitLine = line.split("\\s+");
                for (int x = 0; x < splitLine.length; x++) {
                    if (!splitLine[x].equals("00") && !splitLine[x].equals("0")) {
                        Integer id = Integer.parseInt(splitLine[x]);
                        Node n = new Node(x, y);
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

    public Pheromone getPheromone(Edge e) {
        return synchronizedEdgePheromoneMap.get(e);
    }

    /**
     * Gets all edges that contain a node specified by id
     *
     * @param i id of the node
     * @return sorted List<Map<Edge,Pheromone> of all Edges with their Pheromone value containing the node with the given id
     */
    public List<Map<Edge, Pheromone>> getAllEdgesFromAsSortedList(Integer i) {
        return synchronizedEdgePheromoneMap.entrySet().stream()
                .filter((m) -> m.getKey().contains(i))
                .sorted((e1, e2) -> e1.getValue().getValue().compareTo(e2.getValue().getValue()))
                .map((e) -> {
                    Map<Edge, Pheromone> m = new HashMap<>();
                    m.put(e.getKey(), e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    public Node getNode(Integer id) {
        return synchronizedIntegerNodeMap.get(id);
    }

    /**
     * adds a new node to the grid, blocks other threads from accessing this object.
     * waits for all working ants to receive the updates
     *
     * @param n
     * @param ants
     */
    public void addNode(Node n, Collection<Ant> ants) {
        synchronized (this) {
            synchronizedIntegerNodeMap.put(n.getId(), n);
        }
        for (Ant a : ants) {
            a.reset();
        }
        for (Ant a : ants) {
            while (!a.hasReceivedUpdate()) ;
        }
    }

    /**
     * removes a node, same blocknig as addNode method
     * also has to remove all edges already added to the edge map containing the node to be removed
     *
     * @param id
     * @param ants
     */
    public void removeNode(Integer id, Collection<Ant> ants) {
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
            a.reset();
        }
        for (Ant a : ants) {
            while (!a.hasReceivedUpdate()) ;
        }
    }

}
