package demo.elevatorsimulator.model;

/** @author Amalie Leguén */

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

    public RequestManager(int minFloor, int maxFloor) {
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
    }

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

    public List<Integer> getPendingRequestsSnapshot() {
        lock.lock();
        try {
            return new ArrayList<>(requests);
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        lock.lock();
        try {
            running = false;
            requestAvailable.signalAll();
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
}
