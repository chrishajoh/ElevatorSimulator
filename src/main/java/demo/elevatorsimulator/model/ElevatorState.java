package demo.elevatorsimulator.model;

/**
 * Represents the elevator's overall state.
 * <p>
 * The enum describes what the elevator is currently doing,
 * regardless of which direction it may be moving in.
 * </p>
 *
 * @author Amalie Leguén
 * @version 1.0 (2026-04-08)
 * @since 1.0
 */
public enum ElevatorState {
    IDLE,
    MOVING,
    DOOR_OPERATION
}
