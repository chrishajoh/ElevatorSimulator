package demo.elevatorsimulator.controller;

import demo.elevatorsimulator.model.Elevator;
import demo.elevatorsimulator.model.FloorButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElevatorController {

    private static final int NUM_FLOORS = 9;

    private final Elevator model;
    private final List<FloorButton> buttons = new ArrayList<>();

    public ElevatorController(Elevator model) {
        this.model = model;

        for (int floor = 1; floor <= NUM_FLOORS; floor++) {
            FloorButton btn = new FloorButton(floor);
            btn.setOnPress(() -> model.addRequest(btn.getFloor()));
            buttons.add(btn);
        }
    }

    public List<FloorButton> getButtons() {
        return Collections.unmodifiableList(buttons); // Prevents other code from modifying the buttons
    }
}