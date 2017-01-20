import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yay on 23.12.2016.
 */
public class AntGUI extends Application {

    @Override
    public void start(Stage stage) {
        BlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(1000);

        BorderPane mainRoot = new BorderPane();
        Pane root = new Pane();
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(0, 100, 15, 100));
        mainRoot.setCenter(root);
        mainRoot.setBottom(label);

        final Grid g = new Grid(new HashMap());
        final List<Ant> ants = new ArrayList<>();
        final Scene scene = new Scene(mainRoot, 600, 600);

        scene.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                int x = (int) event.getSceneX();
                int y = (int) event.getSceneY();
                AntNode antNode = new AntNode(x, y);
                FXAntNode fxAntNode = new FXAntNode(antNode);
                fxAntNode.setOnMouseClicked((event1 -> {
                    if(event1.getButton().equals(MouseButton.SECONDARY)) {
                        g.removeNode(fxAntNode.getAntNodeId(), ants);
                        root.getChildren().remove(fxAntNode);
                    }
                }));
                g.addNode(antNode, ants);

                Set<Line> temp = new HashSet();
                for(Node n1: root.getChildren()) {
                    if(n1 instanceof FXAntNode) {
                        Line l = new Line(fxAntNode.getCenterX(), fxAntNode.getCenterY(), ((FXAntNode) n1).getCenterX(), ((FXAntNode) n1).getCenterY());
                        temp.add(l);
                    }
                }
                root.getChildren().add(fxAntNode);
                root.getChildren().addAll(temp);
                label.setText("Total City: " + 4);
            }
        });

        stage.setTitle("TSP Genetic");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
