import javafx.scene.shape.Circle;

/**
 * Created by yay on 19.01.2017.
 */
public class FXAntNode extends Circle {
    public static final String STYLE_UNCLICKED = "-fx-fill: red; -fx-stroke: black";
    public boolean isClicked;

    private int antNodeId;


    public FXAntNode(AntNode antNode) {
        this(antNode.x, antNode.y, antNode.getId());
    }

    private FXAntNode(double centerX, double centerY, int antNodeId) {
        super(centerX, centerY, 5);
        this.setStyle(STYLE_UNCLICKED);
        this.antNodeId = antNodeId;
    }

    public int getAntNodeId() {
        return antNodeId;
    }
}