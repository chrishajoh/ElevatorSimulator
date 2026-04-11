package demo.elevatorsimulator.view;

import javafx.animation.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ElevatorView {

    private Rectangle elevator;
    private Rectangle leftDoor;
    private Rectangle rightDoor;

    private final int[] floors = {500, 400, 300, 200, 100, 0};

    public ElevatorView(Rectangle elevator, Rectangle leftDoor, Rectangle rightDoor) {
        this.elevator = elevator;
        this.leftDoor = leftDoor;
        this.rightDoor = rightDoor;
    }

    // Flytt heis til etasje
    public void moveToFloor(int floor) {
        TranslateTransition move = new TranslateTransition(Duration.seconds(2), elevator);
        move.setToY(floors[floor] - elevator.getY());

        move.setOnFinished(e -> {
            elevator.setY(floors[floor]);
            leftDoor.setY(floors[floor]);
            rightDoor.setY(floors[floor]);
        });

        move.play();
    }

    //  Åpne dører
    public void openDoors() {
        TranslateTransition left = new TranslateTransition(Duration.seconds(1), leftDoor);
        left.setToX(-20);

        TranslateTransition right = new TranslateTransition(Duration.seconds(1), rightDoor);
        right.setToX(20);

        new ParallelTransition(left, right).play();
    }

    // Lukk dører
    public void closeDoors() {
        TranslateTransition left = new TranslateTransition(Duration.seconds(1), leftDoor);
        left.setToX(0);

        TranslateTransition right = new TranslateTransition(Duration.seconds(1), rightDoor);
        right.setToX(0);

        new ParallelTransition(left, right).play();
    }
}