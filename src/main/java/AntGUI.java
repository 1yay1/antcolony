import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by yay on 23.12.2016.
 */
public class AntGUI extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainRoot = new BorderPane();
        Pane root = new Pane();
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(0, 100, 15, 100));
        mainRoot.setCenter(root);
        mainRoot.setBottom(label);

        Scene scene = new Scene(mainRoot, 600, 600);
        primaryStage.setTitle("AAAAAAAANTS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
