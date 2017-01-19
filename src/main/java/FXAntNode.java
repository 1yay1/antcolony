import javafx.scene.shape.Circle;

/**
 * Created by yay on 19.01.2017.
 */
public class FXAntNode extends Circle {
    public static final String STYLE_UNCLICKED = "-fx-fill: red; -fx-stroke: black";
    public static final String STYLE_CLICKED = "-fx-fill: green; -fx-stroke: black";
    public boolean isClicked;

    private int antNodeId;

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public FXAntNode(AntNode antNode) {
        this(antNode.x, antNode.y, antNode.getId());
    }

    private FXAntNode(double centerX, double centerY, int antNodeId) {
        super(centerX, centerY, 5);
        this.setStyle(STYLE_UNCLICKED);
        this.isClicked = false;
        this.antNodeId = antNodeId;
        //
    }

    public int getAntNodeId() {
        return antNodeId;
    }
}