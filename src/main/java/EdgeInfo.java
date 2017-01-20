import java.util.Comparator;

/**
 * Created by yay on 22.12.2016.
 */
public class EdgeInfo implements Comparator<EdgeInfo> {
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
    
    public double calculateWeightedValue() {
        return Math.pow((1/distance), BETA) * pheromone.getValue();
    }

    public EdgeInfo(AntNode n1, AntNode n2) {
        this(AntNode.calculateDistance(n1, n2));
    }

    public Double getPheromoneValue() {
        return pheromone.getValue();
    }

    public void setPheromone(Double pheromoneValue) {
        this.pheromone.setValue(pheromoneValue);
        this.weightedValue = calculateWeightedValue();
    }

    public double getDistance() {
        return distance;
    }


    @Override
    public int compare(EdgeInfo o1, EdgeInfo o2) {
        Double v1 = o1.getWeightedValue();
        Double v2 = o2.getWeightedValue();
        return v1.compareTo(v2);
        //return o1.getWeightedValue() > o2.getWeightedValue() ? return o1.getWeightedValue() : o1.getWeightedValue()
    }
}
