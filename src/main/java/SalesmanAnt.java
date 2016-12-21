/**
 * Created by yay on 21.12.2016.
 */
public class SalesmanAnt extends Ant {

    private final double decayRate;
    private final double choiceRate;

    public SalesmanAnt(Grid g, double decayRate, double choiceRate) {
        super(g);
        this.decayRate = decayRate;
        this.choiceRate = choiceRate;
    }

    @Override
    protected void producePheromone() {

    }

    @Override
    protected Integer chooseNextNode() {
        return null;
    }
}
