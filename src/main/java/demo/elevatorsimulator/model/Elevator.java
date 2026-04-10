package demo.elevatorsimulator.model;
/**
 * Represents the elevator in the system.
 * <p>
 * The class models the elevator's state and behaviour over time.
 * The elevator runs as its own thread, retrieves requests from {@code RequestManager},
 * and processes one request at a time.
 * </p>
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 *     <li>Maintaining the elevator's state, door state and direction</li>
 *     <li>Moving the elevator between valid floors</li>
 *     <li>Opening and closing the door in a controlled manner</li>
 *     <li>Enforcing central safety rules in the system</li>
 * </ul>
 *
 * <p>
 *     Important safety rules:
 * </p>
 * <ul>
 *     <li>The elevator must never move while the door is not closed.</li>
 *     <li>The elevator can only be at one floor at a time.</li>
 *     <li>One request must be fully processed before the next is retrived.</li>
 * </ul>
 *
 * @author Amalie Leguén
 * @version 1.0 (2026-04-08)
 * @since 1.0
 */

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator implements Runnable {
    private final ReentrantLock lock = new ReentrantLock();
    private final RequestManager requestManager;

    private final int minFloor;
    private final int maxFloor;

    // Shared state protected by the lock
    private int currentFloor;
    private ElevatorState elevatorState;
    private DoorState doorState;
    private Direction direction;
    private boolean running = true;

    // Simulated timing values
    private final long moveDelayMs;
    private final long doorActionDelayMs;
    private final long doorOpenWaitMs;

    /**
     * Creates a new elevator with default simulation timings.
     *
     * @param startFloor the floor where the elevator starts
     * @param minFloor   the lowest valid floor
     * @param maxFloor   the highest valid floor
     */
    public Elevator(int startFloor, int minFloor, int maxFloor) {
        this(startFloor, minFloor, maxFloor, 1000, 500, 1200);
    }

    /**
     * Creats a new elevator with configurable simulation timings.
     *
     * @param startFloor        the floor where the elevator starts
     * @param minFloor          the lowest valid floor
     * @param maxFloor          the highest valid floor
     * @param moveDelayMs       the time in milliseconds to move one floor
     * @param doorActionDelayMs the time in milliseconds to open or close the door
     * @param doorOpenWaitMs    the time in milliseconds the door remains open
     */
    public Elevator(int startFloor, int minFloor, int maxFloor,
                    long moveDelayMs, long doorActionDelayMs, long doorOpenWaitMs) {
        if (minFloor > maxFloor) {
            throw new IllegalArgumentException(
                    "[Elevator] Lowest floor cannot be greater than highest floor.");
        }

        if (startFloor < minFloor || startFloor > maxFloor) {
            throw new IllegalArgumentException(
                    "[Elevator] Start floor is outside the valid floor range.");
        }

        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.currentFloor = startFloor;

        this.elevatorState = ElevatorState.IDLE;
        this.doorState = DoorState.CLOSED;
        this.direction = Direction.IDLE;

        this.moveDelayMs = moveDelayMs;
        this.doorActionDelayMs = doorActionDelayMs;
        this.doorOpenWaitMs = doorOpenWaitMs;

        this.requestManager = new RequestManager(minFloor, maxFloor);
    }

    /**
     * Adds a new floor request.
     *
     * @param floor the requested floor
     */
    public void addRequest(int floor) {
        requestManager.addRequest(floor);
    }

    /**
     * Stops the elevator in a controlled manner.
     * Also stops the {@code RequestManager} so that a waiting elevator thread
     * can terminate safely.
     */
    public void stopElevator() {
        lock.lock();
        try {
            running = false;
        } finally {
            lock.unlock();
        }
        requestManager.stop();
    }

    /**
     * Returns the elevator's current floor.
     *
     * @return the current floor
     */
    public int getCurrentFloor() {
        lock.lock();
        try {
            return currentFloor;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the elevator's overall state.
     *
     * @return the elevator state
     */
    public ElevatorState getElevatorState() {
        lock.lock();
        try {
            return elevatorState;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the door's current state.
     *
     * @return the door state
     */
    public DoorState getDoorState() {
        lock.lock();
        try {
            return doorState;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the elevator's current direction.
     *
     * @return the direction
     */
    public Direction getDirection() {
        lock.lock();
        try {
            return direction;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns whether the elevator is still running.
     *
     * @return {@code true} if running, otherwise {@code false}
     */
    public boolean isRunning() {
        lock.lock();
        try {
            return running;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a snapshot of the pending requests.
     *
     * @return a list of pending floor requests
     */
    public List<Integer> getPendingRequestsSnapshot() {
        return requestManager.getPendingRequestsSnapshot();
    }

    /**
     * Main loop of the elevator thread.
     * The elevator waits for the next request and completes it before
     * retrieving the next one.
     */
    @Override
    public void run() {
        try {
            while (true) {
                int targetFloor = requestManager.waitForNextRequest();

                if (!isRunning() || targetFloor == -1) {
                    System.out.println("[Elevator] Stopping elevator thread.");
                    break;
                }

                System.out.println("[Elevator] New target floor received: " + targetFloor);
                moveToFloor(targetFloor);
            }
        } catch (InterruptedException e) {
            System.out.println("[Elevator] Thread interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Moves the elevator in a controlled manner to the requested floor.
     *
     * @param targetFloor the target floor
     * @throws InterruptedException if the thread is interrupted during execution
     */
    private void moveToFloor(int targetFloor) throws InterruptedException {
        closeDoorIfNecessary();

        while (true) {
            lock.lock();
            try {
                if (currentFloor == targetFloor) {
                    elevatorState = ElevatorState.IDLE;
                    direction = Direction.IDLE;
                    System.out.println("[Elevator] Arrived at floor: " + currentFloor);
                    break;
                }

                if (doorState != DoorState.CLOSED) {
                    throw new IllegalStateException(
                            "[Elevator] Cannot move while the door is not closed.");
                }

                elevatorState = ElevatorState.MOVING;

                if (currentFloor < targetFloor) {
                    direction = Direction.UP;
                    System.out.println("[Elevator] Moving up from floor: " + currentFloor);
                } else {
                    direction = Direction.DOWN;
                    System.out.println("[Elevator] Moving down from floor: " + currentFloor);
                }
            } finally {
                lock.unlock();
            }

            Thread.sleep(moveDelayMs);

            lock.lock();
            try {
                if (direction == Direction.UP) {
                    currentFloor++;
                } else if (direction == Direction.DOWN) {
                    currentFloor--;
                }

                System.out.println("[Elevator] Current floor: " + currentFloor);
            } finally {
                lock.unlock();
            }
        }

        openDoor();
        Thread.sleep(doorOpenWaitMs);
        closeDoor();
    }

    /**
     * Opens the door in a controlled manner.
     *
     * @throws InterruptedException if the thread is interrupted while opening the door
     */
    private void openDoor() throws InterruptedException {
        lock.lock();
        try {
            if (doorState == DoorState.OPEN || doorState == DoorState.OPENING) {
                return;
            }

            if (elevatorState == ElevatorState.MOVING) {
                throw new IllegalStateException(
                        "[Elevator] Door cannot be opened while the elevator is moving.");
            }

            elevatorState = ElevatorState.DOOR_OPERATION;
            direction = Direction.IDLE;
            doorState = DoorState.OPENING;

            System.out.println("[Elevator] Door opening...");
        } finally {
            lock.unlock();
        }

        Thread.sleep(doorActionDelayMs);

        lock.lock();
        try {
            doorState = DoorState.OPEN;
            System.out.println("[Elevator] Door is open.");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Closes the door in a controlled manner.
     *
     * @throws InterruptedException if the thread is interrupted while closing the door
     */
    private void closeDoor() throws InterruptedException {
        lock.lock();
        try {
            if (doorState == DoorState.CLOSED || doorState == DoorState.CLOSING) {
                return;
            }

            elevatorState = ElevatorState.DOOR_OPERATION;
            direction = Direction.IDLE;
            doorState = DoorState.CLOSING;

            System.out.println("[Elevator] Door closing...");
        } finally {
            lock.unlock();
        }

        Thread.sleep(doorActionDelayMs);

        lock.lock();
        try {
            doorState = DoorState.CLOSED;
            elevatorState = ElevatorState.IDLE;
            direction = Direction.IDLE;

            System.out.println("[Elevator] Door is closed.");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Closes the door if it is not already closed.
     *
     * @throws InterruptedException if the thread is interrupted while closing the door
     */
    private void closeDoorIfNecessary() throws InterruptedException {
        lock.lock();
        try {
            if (doorState == DoorState.CLOSED) {
                return;
            }
        } finally {
            lock.unlock();
        }
        closeDoor();
    }
}
