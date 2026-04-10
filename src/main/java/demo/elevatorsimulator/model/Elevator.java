package demo.elevatorsimulator.model;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents the elevator and manages the request queue.
 * The queue is thread-safe, allowing floor requests to be added
 * from one thread while the elevator logic processes them on another.
 *
 * @author Christoffer Johansen
 */
public class Elevator {

    private int currentFloor = 1;
    private final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

    /**
     * Adds a floor request to the queue.
     * Ignores the request if the elevator is already on that floor
     * or if the floor is already in the queue.
     *
     * @param floor the floor number to request
     */
    public void addRequest(int floor) {
        if (floor == currentFloor) return;
        if (queue.contains(floor)) return;
        queue.add(floor);
    }

    /**
     * Returns the floor the elevator is currently on.
     *
     * @return current floor number
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Returns the request queue.
     * The elevator logic thread uses this to process requests in order.
     *
     * @return the thread-safe queue of pending floor requests
     */
    public LinkedBlockingQueue<Integer> getQueue() {
        return queue;
    }
}
