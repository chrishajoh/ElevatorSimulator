package demo.elevatorsimulator.model;

/**
 * Represents a physical floor button in the elevator system.
 * When pressed, it triggers a callback (typically adding a floor request to the queue).
 * The callback is assigned by the controller, keeping the button decoupled from the elevator logic.
 *
 * @author Christoffer Johansen
 */
public class FloorButton {

    private final int floor;
    private Runnable onPress;

    /**
     * Creates a button for the given floor.
     *
     * @param floor the floor number this button represents
     */
    public FloorButton(int floor) {
        this.floor = floor;
    }

    /**
     * Simulates pressing the button.
     * Executes the assigned callback if one has been set.
     */
    public void press() {
        if (onPress != null) onPress.run();
    }

    /**
     * Returns the floor number this button represents.
     *
     * @return floor number
     */
    public int getFloor() {
        return floor;
    }

    /**
     * Assigns the callback to run when this button is pressed.
     *
     * @param onPress a {@link Runnable} that is executed on button press
     */
    public void setOnPress(Runnable onPress) {
        this.onPress = onPress;
    }
}
