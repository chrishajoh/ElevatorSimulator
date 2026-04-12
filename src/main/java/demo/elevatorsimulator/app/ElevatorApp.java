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

/**
 * Hovedklassen for heissimulatoren.
 * <p>
 * Starter JavaFX-applikasjonen og setter opp den grafiske representasjonen
 * av heisen med dører. Kjører en automatisk sekvens der heisen beveger seg
 * gjennom alle etasjer, åpner og lukker dørene på hver etasje.
 * </p>
 *
 * @author Fredrik
 * @version 1.0
 */
public class ElevatorApp extends Application {

    /** Den grafiske representasjonen av heiskabinen. */
    private Rectangle elevator;

    /** Venstre dør på heisen. */
    private Rectangle leftDoor;

    /** Høyre dør på heisen. */
    private Rectangle rightDoor;

    /** Etikett som viser gjeldende etasje. */
    private Label floorLabel;

    /**
     * Y-koordinater for hver etasje i scenen.
     * Indeks 0 tilsvarer etasje 0 (bunn), indeks 5 tilsvarer etasje 5 (topp).
     */
    private final int[] floors = {500, 400, 300, 200, 100, 0};

    /** Gruppe som samler heis og dører slik at de beveger seg sammen. */
    private Group elevatorGroup;

    /**
     * Initialiserer og viser applikasjonens hovedvindu.
     * Oppretter heisen, dørene og etasje-etiketten, og starter den automatiske kjøresekvensen.
     *
     * @param stage primærvinduet som JavaFX-plattformen leverer
     */
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

    /**
     * Kjører den automatiske heissekvensen.
     * Heisen beveger seg fra etasje 1 til etasje 5, stopper i hver etasje,
     * åpner dørene, venter ett sekund, og lukker dørene igjen.
     */
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

    /**
     * Oppretter en animasjon som flytter heisen til angitt etasje.
     * Oppdaterer etasje-etiketten når animasjonen er ferdig.
     *
     * @param floor etasjenummeret heisen skal flyttes til (0–5)
     * @return en {@link TranslateTransition} som representerer bevegelsen
     */
    private TranslateTransition moveToFloor(int floor) {

        double targetY = floors[floor];

        TranslateTransition move = new TranslateTransition(Duration.seconds(2), elevatorGroup);
        move.setToY(targetY - floors[0]);

        move.setOnFinished(e -> {
            floorLabel.setText("Floor: " + floor);
        });

        return move;
    }

    /**
     * Oppretter en animasjon som åpner heisdørene.
     * Venstre dør glir til venstre og høyre dør glir til høyre samtidig.
     *
     * @return en {@link ParallelTransition} som animerer begge dørene
     */
    private ParallelTransition openDoors() {

        TranslateTransition left = new TranslateTransition(Duration.seconds(0.5), leftDoor);
        left.setToX(-20);

        TranslateTransition right = new TranslateTransition(Duration.seconds(0.5), rightDoor);
        right.setToX(20);

        return new ParallelTransition(left, right);
    }

    /**
     * Oppretter en animasjon som lukker heisdørene.
     * Begge dørene glir tilbake til sin opprinnelige posisjon samtidig.
     *
     * @return en {@link ParallelTransition} som animerer begge dørene
     */
    private ParallelTransition closeDoors() {

        TranslateTransition left = new TranslateTransition(Duration.seconds(0.5), leftDoor);
        left.setToX(0);

        TranslateTransition right = new TranslateTransition(Duration.seconds(0.5), rightDoor);
        right.setToX(0);

        return new ParallelTransition(left, right);
    }

    /**
     * Oppretter en pause i animasjonssekvensen.
     *
     * @param seconds antall sekunder pausen skal vare
     * @return en {@link PauseTransition} med angitt varighet
     */
    private PauseTransition waitTime(int seconds) {
        return new PauseTransition(Duration.seconds(seconds));
    }

    /**
     * Inngangspunkt for applikasjonen.
     *
     * @param args kommandolinjeargumenter (brukes ikke)
     */
    public static void main(String[] args) {
        launch();
    }
}