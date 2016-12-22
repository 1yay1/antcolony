/**
 * Created by yay on 22.12.2016.
 */
public class EdgeInfo {
    private Pheromone pheromone;
    private double distance;


    private EdgeInfo(double distance) {
        this.pheromone = new Pheromone();
        this.distance = distance;
    }

    public EdgeInfo(Node n1, Node n2) {
        this(Node.calculateDistance(n1, n2));
    }

    public Pheromone getPheromone() {
        return pheromone;
    }

    public double getDistance() {
        return distance;
    }


}
