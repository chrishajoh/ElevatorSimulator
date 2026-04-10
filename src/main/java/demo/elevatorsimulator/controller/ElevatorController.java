package demo.elevatorsimulator.controller;

import demo.elevatorsimulator.model.Elevator;
import demo.elevatorsimulator.model.FloorButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controls the elevator by creating floor buttons and wiring them to the elevator model.
 * Each button is assigned a callback that adds its floor to the elevator's request queue when pressed.
 * Exposes the buttons so the view can bind them to the UI.
 *
 * @author Christoffer Johansen
 */
public class ElevatorController {

    private static final int NUM_FLOORS = 9;

    private final Elevator model;
    private final List<FloorButton> buttons = new ArrayList<>();

    /**
     * Creates the controller, initializes all floor buttons (1-9),
     * and wires each button to add its floor to the elevator queue on press.
     *
     * @param model the elevator model to send requests to
     */
    public ElevatorController(Elevator model) {
        this.model = model;

        for (int floor = 1; floor <= NUM_FLOORS; floor++) {
            FloorButton btn = new FloorButton(floor);
            btn.setOnPress(() -> model.addRequest(btn.getFloor()));
            buttons.add(btn);
        }
    }

    /**
     * Returns an unmodifiable list of all floor buttons.
     * The view uses this to create the UI buttons and bind them to {@link FloorButton#press()}.
     *
     * @return unmodifiable list of {@link FloorButton}
     */
    public List<FloorButton> getButtons() {
        return Collections.unmodifiableList(buttons);
    }
}
