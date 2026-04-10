package demo.elevatorsimulator.model;
/**
 * Handles incoming floor requests in the elevator system.
 * <p>
 * The class owns the request queue and provides thread-safe insertion,
 * retrieval, waiting and signalling by using {@code ReentrantLock}
 * and {@code Condition}.
 * </p>
 *
 * <p>
 * The purpose of the class is to seperate request handling from the
 * elevator's movement logic, giving each class a clearer responsibility.
 * </p>
 *
 * @author Amalie Leguén
 * @version 1.0 (2026-04-08)
 * @since 1.0
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RequestManager {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition requestAvailable = lock.newCondition();
    private final Queue<Integer> requests = new LinkedList<>();

    private final int minFloor;
    private final int maxFloor;
    private boolean running = true;

    /**
     * Creates a new request manager for a valid floor range.
     *
     * @param minFloor the lowest valid floor
     * @param maxFloor the highest valid floor
     */
    public RequestManager(int minFloor, int maxFloor) {
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
    }

    /**
     * Adds a new floor request to the queue.
     * If a thread is waiting for the next request, it is signalled.
     *
     * @param floor the requested floor
     */
    public void addRequest(int floor) {
        lock.lock();
        try {
            if (floor < minFloor || floor > maxFloor) {
                System.out.println("[RequestManager] Invalid floor request: " + floor);
                return;
            }

            requests.offer(floor);
            System.out.println("[RequestManager] Request added for floor: " + floor);

            requestAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Waits until the next request becomes available in the queue.
     *
     * @return the next target floor, or {@code -1} if the manager has been stopped
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public int waitForNextRequest() throws InterruptedException {
        lock.lock();
        try {
            while (running && requests.isEmpty()) {
                System.out.println("[RequestManager] No requests available. Waiting...");
                requestAvailable.await();
            }

            if (!running) {
                return -1;
            }

            int targetFloor = requests.poll();
            System.out.println("[RequestManager] Retrieved next request: floor " + targetFloor);
            return targetFloor;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a snapshot of the pending requests.
     *
     * @return a copy of the pending request queue
     */
    public List<Integer> getPendingRequestsSnapshot() {
        lock.lock();
        try {
            return new ArrayList<>(requests);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stops the request manager in a controlled manner.
     * Any waiting thread is signalled so it can terminate safely.
     */
    public void stop() {
        lock.lock();
        try {
            running = false;
            requestAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns whether the request manager is still running.
     *
     * @return {@code true} if the manager is running, otherwise {@code false}
     */
    public boolean isRunning() {
        lock.lock();
        try {
            return running;
        } finally {
            lock.unlock();
        }
    }
}