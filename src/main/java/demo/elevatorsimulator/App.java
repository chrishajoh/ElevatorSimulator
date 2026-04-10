package demo.elevatorsimulator;

import demo.elevatorsimulator.controller.ElevatorController;
import demo.elevatorsimulator.model.Elevator;
import demo.elevatorsimulator.view.ElevatorView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 700;
    private static final String WINDOW_TITLE = "Elevator Simulator";

    @Override
    public void start(Stage stage) {
        Elevator model = new Elevator();
        ElevatorController controller = new ElevatorController(model);
        ElevatorView view = new ElevatorView(controller);

        BorderPane root = new BorderPane();
        root.setLeft(view.getRoot());

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
