package demo.elevatorsimulator.view;

/** @author Fredrik */

import demo.elevatorsimulator.controller.ElevatorController;
import demo.elevatorsimulator.model.FloorButton;
import javafx.animation.*;
import java.util.LinkedList;
import java.util.Queue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ElevatorView {

    private final HBox root = new HBox(20);

    private Rectangle elevator;
    private Rectangle leftDoor;
    private Rectangle rightDoor;
    private Label floorLabel;
    private Group elevatorGroup;

    private final int[] floors = {500, 440, 380, 320, 260, 200, 140, 80, 20};

    private final Queue<Integer> animationQueue = new LinkedList<>();
    private boolean isAnimating = false;
    private final VBox queueDisplay = new VBox(5);

    public ElevatorView(ElevatorController controller) {
        Pane shaft = new Pane();

        Rectangle shaftBackground = new Rectangle(80, 580);
        shaftBackground.setLayoutX(160);
        shaftBackground.setLayoutY(10);
        shaftBackground.setFill(Color.LIGHTGRAY);
        shaftBackground.setStroke(Color.DARKGRAY);
        shaft.getChildren().add(shaftBackground);

        elevator = new Rectangle(80, 80);
        elevator.setFill(Color.GRAY);

        leftDoor = new Rectangle(40, 80);
        rightDoor = new Rectangle(40, 80);
        leftDoor.setFill(Color.DARKGRAY);
        rightDoor.setFill(Color.DARKGRAY);
        leftDoor.setX(0);
        rightDoor.setX(40);

        elevatorGroup = new Group(elevator, leftDoor, rightDoor);
        elevatorGroup.setLayoutX(160);
        elevatorGroup.setLayoutY(floors[0]);

        floorLabel = new Label("Floor: 1");
        floorLabel.setLayoutX(10);
        floorLabel.setLayoutY(20);

        for (int i = 0; i < floors.length; i++) {
            Label floorNumber = new Label(String.valueOf(i + 1));
            floorNumber.setLayoutX(130);
            floorNumber.setLayoutY(floors[i] + 30);
            shaft.getChildren().add(floorNumber);
        }

        shaft.getChildren().addAll(elevatorGroup, floorLabel);
        shaft.setPrefSize(400, 600);

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(5);
        buttonGrid.setVgap(5);
        for (FloorButton btn : controller.getButtons()) {
            Button button = new Button(String.valueOf(btn.getFloor()));
            button.setMinWidth(50);
            button.setOnAction(e -> {
                btn.press();
                animationQueue.add(btn.getFloor());
                updateQueueDisplay();
                if (!isAnimating) processNextInQueue();
            });
            int index = btn.getFloor() - 1;
            buttonGrid.add(button, index % 3, index / 3);
        }

        Label queueTitle = new Label("Queue:");
        VBox controlPanel = new VBox(10, buttonGrid, queueTitle, queueDisplay);

        root.getChildren().addAll(shaft, controlPanel);
    }

    public Node getRoot() {
        return root;
    }

    private void updateQueueDisplay() {
        queueDisplay.getChildren().clear();
        for (int floor : animationQueue) {
            queueDisplay.getChildren().add(new Label("Floor " + floor));
        }
    }

    private void processNextInQueue() {
        if (animationQueue.isEmpty()) {
            isAnimating = false;
            return;
        }
        isAnimating = true;
        int floor = animationQueue.peek();
        SequentialTransition sequence = new SequentialTransition(
            moveToFloor(floor),
            openDoors(),
            new PauseTransition(Duration.seconds(1)),
            closeDoors()
        );
        sequence.setOnFinished(e -> {
            animationQueue.poll();
            updateQueueDisplay();
            processNextInQueue();
        });
        sequence.play();
    }

    private TranslateTransition moveToFloor(int floor) {
        double targetY = floors[floor - 1];
        TranslateTransition move = new TranslateTransition(Duration.seconds(2), elevatorGroup);
        move.setToY(targetY - floors[0]);
        move.setOnFinished(e -> floorLabel.setText("Floor: " + floor));
        return move;
    }

    private ParallelTransition openDoors() {
        TranslateTransition left = new TranslateTransition(Duration.seconds(0.5), leftDoor);
        left.setToX(-20);
        TranslateTransition right = new TranslateTransition(Duration.seconds(0.5), rightDoor);
        right.setToX(20);
        return new ParallelTransition(left, right);
    }

    private ParallelTransition closeDoors() {
        TranslateTransition left = new TranslateTransition(Duration.seconds(0.5), leftDoor);
        left.setToX(0);
        TranslateTransition right = new TranslateTransition(Duration.seconds(0.5), rightDoor);
        right.setToX(0);
        return new ParallelTransition(left, right);
    }
}
