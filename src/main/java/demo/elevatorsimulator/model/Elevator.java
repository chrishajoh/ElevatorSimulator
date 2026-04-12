package demo.elevatorsimulator.model;

/** @author Amalie Leguén */

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator implements Runnable {
    private final ReentrantLock lock = new ReentrantLock();
    private final RequestManager requestManager;

    private final int minFloor;
    private final int maxFloor;

    private int currentFloor;
    private ElevatorState elevatorState;
    private DoorState doorState;
    private Direction direction;
    private boolean running = true;

    private final long moveDelayMs;
    private final long doorActionDelayMs;
    private final long doorOpenWaitMs;

    public Elevator(int startFloor, int minFloor, int maxFloor) {
        this(startFloor, minFloor, maxFloor, 1000, 500, 1200);
    }

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

    public void addRequest(int floor) {
        requestManager.addRequest(floor);
    }

    public void stopElevator() {
        lock.lock();
        try {
            running = false;
        } finally {
            lock.unlock();
        }
        requestManager.stop();
    }

    public int getCurrentFloor() {
        lock.lock();
        try {
            return currentFloor;
        } finally {
            lock.unlock();
        }
    }

    public ElevatorState getElevatorState() {
        lock.lock();
        try {
            return elevatorState;
        } finally {
            lock.unlock();
        }
    }

    public DoorState getDoorState() {
        lock.lock();
        try {
            return doorState;
        } finally {
            lock.unlock();
        }
    }

    public Direction getDirection() {
        lock.lock();
        try {
            return direction;
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        lock.lock();
        try {
            return running;
        } finally {
            lock.unlock();
        }
    }

    public List<Integer> getPendingRequestsSnapshot() {
        return requestManager.getPendingRequestsSnapshot();
    }

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
