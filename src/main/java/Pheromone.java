

/**
 * Created by yay on 20.12.2016.
 */
public class Pheromone {
    private float value;

    public Pheromone() {
        this.value = 0f;
    }

    /**
     * Adds to the current value
     *
     * @param pheromoneValue value to be added
     */
    public void add(float pheromoneValue) {
        this.value += pheromoneValue;
    }

    /**
     * Reduced the current pheromone value by a percentage rate.
     *
     * @param decayRate rate of decay
     */
    public void decay(float decayRate) {
        this.value *= decayRate;
    }

    /**
     * Default getter for the pheromone value
     *
     * @return this.value
     */
    public Float getValue() {
        return new Float(value);
    }

}
