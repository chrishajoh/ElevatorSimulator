package demo.elevatorsimulator.model;

/** @author Christoffer Johansen */
public class FloorButton {

    private final int floor;
    private Runnable onPress;

    public FloorButton(int floor) {
        this.floor = floor;
    }

    public void press() {
        if (onPress != null) onPress.run();
    }

    public int getFloor() {
        return floor;
    }

    public void setOnPress(Runnable onPress) {
        this.onPress = onPress;
    }
}
