import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

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

        final Grid g = new Grid(new HashMap<>());

        final Scene scene = new Scene(mainRoot, 600, 600);


        int antCount = 25;
        int tours = 20;
        double q0 = 0.25;
        double alpha = 1;
        double beta = 2;
        double decayRate = 0.2;

        final List<Ant> ants = new ArrayList<>();
        final BlockingQueue<List<Integer>> blockingQueue = new ArrayBlockingQueue(1);
        final List<Thread> antColonyThreads = new ArrayList<>();

        final Map<Edge, FXAntLine> edgeLineMap = new HashMap<>();

        scene.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                int x = (int) event.getSceneX();
                int y = (int) event.getSceneY();
                AntNode antNode = new AntNode(x, y);
                FXAntNode fxAntNode1 = new FXAntNode(antNode);
                fxAntNode1.setOnMouseClicked((event1 -> {
                    if(event1.getButton().equals(MouseButton.SECONDARY)) {
                        g.removeNode(fxAntNode1.getAntNodeId(), ants);
                        root.getChildren().remove(fxAntNode1);
                    }
                }));
                g.addNode(antNode, ants);

                Set<Line> temp = new HashSet();
                for(Node n1: root.getChildren()) {
                    if(n1 instanceof FXAntNode) {
                        FXAntNode fxAntNode2 = (FXAntNode) n1;
                        Edge edge = new Edge(fxAntNode1.getAntNodeId(), fxAntNode2.getAntNodeId());
                        FXAntLine line = new FXAntLine(fxAntNode1.getCenterX(), fxAntNode1.getCenterY(),fxAntNode2.getCenterX(), fxAntNode2.getCenterY(), edge);
                        line.setVisible(false);
                        edgeLineMap.put(new Edge(fxAntNode1.getAntNodeId(), fxAntNode2.getAntNodeId()), line);
                        temp.add(line);
                    }
                }
                root.getChildren().add(fxAntNode1);
                root.getChildren().addAll(temp);
                label.setText("Total City: " + 4);
            }
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<Integer> path = blockingQueue.poll();
                if(path == null) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("D: " + g.calculateDistanceFromPath(path));
                    /*Map<Edge, EdgeInfo> pathInfo = g.getPathInfo(path);
                    pathInfo.keySet().stream().forEach((k) -> {
                        edgeLineMap.get(k).setVisible(true);
                    });*/

                }
                //System.out.println("D: " + g.calculateDistanceFromPath(path));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        scene.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                for(int i = 0; i < 1; i++) {
                    ants.add(new SalesmanAnt(g, blockingQueue, antCount, tours , q0, alpha, beta, decayRate));
                    antColonyThreads.add(new Thread(ants.get(i)));
                }
                antColonyThreads.forEach((t) -> t.start());
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timeline.play();
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
