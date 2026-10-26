package util;

import java.util.LinkedList;
import java.util.Queue;

public class TransactionBuffer {
    private final Queue<Runnable> buffer = new LinkedList<>();
    private final int capacity = 5;

    public synchronized void produce(Runnable task) throws InterruptedException {
        while (buffer.size() == capacity) {
            wait();
        }
        buffer.add(task);
        notifyAll();
    }

    public synchronized Runnable consume() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait();
        }
        Runnable task = buffer.poll();
        notifyAll();
        return task;
    }
}