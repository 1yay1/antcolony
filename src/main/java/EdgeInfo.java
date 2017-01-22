import java.util.Comparator;

/**
 * Created by yay on 22.12.2016.
 */
public class EdgeInfo implements Comparable<EdgeInfo> {
    private Pheromone pheromone;
    private double distance;
    private double weightedValue;
    private static final double BETA = 2;

    public double getWeightedValue() { return weightedValue; }

    private EdgeInfo(double distance) {
        this.pheromone = new Pheromone();
        this.distance = distance;
        this.weightedValue = 0;
    }
    
    public double calculateWeightedValue(double beta) {
        return Math.pow((1/distance), beta) * pheromone.getValue();
    }

    public EdgeInfo(AntNode n1, AntNode n2) {
        this(AntNode.calculateDistance(n1, n2));
    }

    public Double getPheromoneValue() {
        return pheromone.getValue();
    }

    public void setPheromone(double beta, double pheromoneValue) {
        this.pheromone.setValue(pheromoneValue);
        this.weightedValue = calculateWeightedValue(beta);
    }

    public double getDistance() {
        return distance;
    }


    @Override
    public int compareTo(EdgeInfo o) {
        return new Double(this.getWeightedValue()).compareTo(new Double(o.getWeightedValue()));
    }
}
