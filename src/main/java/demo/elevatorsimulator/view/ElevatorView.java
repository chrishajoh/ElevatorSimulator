package demo.elevatorsimulator.view;

import javafx.animation.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Håndterer den visuelle representasjonen av heisen og dens dører.
 * <p>
 * Klassen innkapsler animasjonslogikken for å flytte heisen mellom etasjer
 * samt åpne og lukke dørene ved hjelp av JavaFX-transisjoner.
 * </p>
 *
 * @author Fredrik
 * @version 1.0
 */
public class ElevatorView {

    /** Den grafiske representasjonen av heiskabinen. */
    private Rectangle elevator;

    /** Venstre dør på heisen. */
    private Rectangle leftDoor;

    /** Høyre dør på heisen. */
    private Rectangle rightDoor;

    /**
     * Y-koordinater for hver etasje i scenen.
     * Indeks 0 tilsvarer etasje 0 (bunn), indeks 5 tilsvarer etasje 5 (topp).
     */
    private final int[] floors = {500, 400, 300, 200, 100, 0};

    /**
     * Oppretter en ny {@code ElevatorView} med de angitte grafiske elementene.
     *
     * @param elevator  rektangel som representerer heiskabinen
     * @param leftDoor  rektangel som representerer venstre dør
     * @param rightDoor rektangel som representerer høyre dør
     */
    public ElevatorView(Rectangle elevator, Rectangle leftDoor, Rectangle rightDoor) {
        this.elevator = elevator;
        this.leftDoor = leftDoor;
        this.rightDoor = rightDoor;
    }

    /**
     * Animerer heisen til å flytte seg til angitt etasje.
     * Oppdaterer Y-posisjonen til heisen og begge dørene når animasjonen er ferdig.
     *
     * @param floor etasjenummeret heisen skal flyttes til (0–5)
     */
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

    /**
     * Animerer åpningen av heisdørene.
     * Venstre dør glir til venstre og høyre dør glir til høyre samtidig.
     */
    public void openDoors() {
        TranslateTransition left = new TranslateTransition(Duration.seconds(1), leftDoor);
        left.setToX(-20);

        TranslateTransition right = new TranslateTransition(Duration.seconds(1), rightDoor);
        right.setToX(20);

        new ParallelTransition(left, right).play();
    }

    /**
     * Animerer lukkingen av heisdørene.
     * Begge dørene glir tilbake til sin opprinnelige posisjon samtidig.
     */
    public void closeDoors() {
        TranslateTransition left = new TranslateTransition(Duration.seconds(1), leftDoor);
        left.setToX(0);

        TranslateTransition right = new TranslateTransition(Duration.seconds(1), rightDoor);
        right.setToX(0);

        new ParallelTransition(left, right).play();
    }
}