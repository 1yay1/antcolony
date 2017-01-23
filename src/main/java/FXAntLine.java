import javafx.scene.shape.Line;

/**
 * Created by yay on 23.01.2017.
 */
public class FXAntLine extends Line {
    private Edge edge;


    public FXAntLine(double startX, double startY, double endX, double endY, Edge edge) {
        super(startX, startY, endX, endY);
        this.edge = edge;
    }

    public Edge getEdge() {
        return edge;
    }
}
