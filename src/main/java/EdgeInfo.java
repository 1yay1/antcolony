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

    public EdgeInfo(AntNode n1, AntNode n2) {
        this(AntNode.calculateDistance(n1, n2));
    }

    public Pheromone getPheromone() {
        return pheromone;
    }

    public double getDistance() {
        return distance;
    }


}
