package demo.elevatorsimulator.model;

/*
 * Represents the door's state.
 * <p>
 * The enum is used to make it explicit whether the door is open,
 * closed or transitioning between those states.
 * </p>
 *
 * @author Amalie Leguén
 * @version 1.0 (2026-04-08)
 * @since 1.0
 */
public enum DoorState {
    OPEN,
    OPENING,
    CLOSED,
    CLOSING
}
