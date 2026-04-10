package demo.elevatorsimulator.model;

import java.util.concurrent.LinkedBlockingQueue;

public class Elevator {

    private int currentFloor = 1;
    private final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

    public void addRequest(int floor) {
        if (floor == currentFloor) return;
        if (queue.contains(floor)) return;
        queue.add(floor);
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public LinkedBlockingQueue<Integer> getQueue() {
        return queue;
    }
}
