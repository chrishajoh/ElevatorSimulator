package demo.elevatorsimulator.app;

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ElevatorApp extends Application {

    private Rectangle elevator;
    private Rectangle leftDoor;
    private Rectangle rightDoor;

    private Label floorLabel;

    private final int[] floors = {500, 400, 300, 200, 100, 0};

    private Group elevatorGroup;

    @Override
    public void start(Stage stage) {

        Pane root = new Pane();

        //  Heisen
        elevator = new Rectangle(80, 80);
        elevator.setFill(Color.GRAY);

        //  Lager Dører
        leftDoor = new Rectangle(40, 80);
        rightDoor = new Rectangle(40, 80);

        leftDoor.setFill(Color.DARKGRAY);
        rightDoor.setFill(Color.DARKGRAY);

        leftDoor.setX(0);
        rightDoor.setX(40);

        //  Grupperer heisen og dørene sammen
        elevatorGroup = new Group(elevator, leftDoor, rightDoor);
        elevatorGroup.setLayoutX(160);
        elevatorGroup.setLayoutY(floors[0]);

        root.getChildren().add(elevatorGroup);

        // Etasje nummer
        floorLabel = new Label("Floor: 0");
        floorLabel.setLayoutX(320);
        floorLabel.setLayoutY(20);
        root.getChildren().add(floorLabel);

        Scene scene = new Scene(root, 400, 600);

        stage.setTitle("Heis Simulator");
        stage.setScene(scene);
        stage.show();

        runElevatorSequence();
    }

    // Sekvens
    private void runElevatorSequence() {

        SequentialTransition sequence = new SequentialTransition();

        for (int i = 1; i <= 5; i++) {
            int floor = i;

            sequence.getChildren().add(moveToFloor(floor));
            sequence.getChildren().add(openDoors());
            sequence.getChildren().add(waitTime(1));
            sequence.getChildren().add(closeDoors());
        }

        sequence.play();
    }

    //  heisen beveger seg til ønsket etasje
    private TranslateTransition moveToFloor(int floor) {

        double targetY = floors[floor];

        TranslateTransition move = new TranslateTransition(Duration.seconds(2), elevatorGroup);
        move.setToY(targetY - floors[0]);

        move.setOnFinished(e -> {
            floorLabel.setText("Floor: " + floor);
        });

        return move;
    }

    // Åpner dører
    private ParallelTransition openDoors() {

        TranslateTransition left = new TranslateTransition(Duration.seconds(0.5), leftDoor);
        left.setToX(-20);

        TranslateTransition right = new TranslateTransition(Duration.seconds(0.5), rightDoor);
        right.setToX(20);

        return new ParallelTransition(left, right);
    }

    //  Lukker dører
    private ParallelTransition closeDoors() {

        TranslateTransition left = new TranslateTransition(Duration.seconds(0.5), leftDoor);
        left.setToX(0);

        TranslateTransition right = new TranslateTransition(Duration.seconds(0.5), rightDoor);
        right.setToX(0);

        return new ParallelTransition(left, right);
    }

    //  kort pause
    private PauseTransition waitTime(int seconds) {
        return new PauseTransition(Duration.seconds(seconds));
    }

    public static void main(String[] args) {
        launch();
    }
}