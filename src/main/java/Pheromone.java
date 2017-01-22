

/**
 * Created by yay on 20.12.2016.
 */
public class Pheromone {
    private double value;

    public Pheromone() {
        this.value = 0d;
    }

    /**
     * Adds to the current value
     *
     * @param pheromoneValue value to be added
     */
    public void add(double pheromoneValue) {
        //decay(0.1f);
        this.value += pheromoneValue;
    }

    /**
     * Reduced the current pheromone value by a percentage rate.
     *
     * @param decayRate rate of decay
     */
    public void decay(double decayRate) {
        this.value *= decayRate;
    }

    /**
     * Default getter for the pheromone value
     *
     * @return this.value
     */
    public double getValue() {
        return value;
    }
    /**
     * Default setter for the pheromone value
     */ 
    public void setValue(double value) {
        this.value=value;
    }


}
