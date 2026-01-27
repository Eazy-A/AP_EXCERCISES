package gpt_hard_midterm.consumer_with_blocking_logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Buffer{
    private final int[] storage;
    private int size = 0;
    private int head = 0;
    private int tail = 0;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Buffer(int capacity) {
        this.storage = new int[capacity];
    }
    public void put(int value) throws InterruptedException {
        lock.writeLock().lock();
        try {
            while (size == storage.length) {
                wait(); // buffer full, producer waits
            }
            storage[tail] = value;
            tail = (tail + 1) % storage.length;
            size++;
            notifyAll(); // tell consumers there is data
        }finally {
            lock.writeLock().unlock();
        }
    }
    public int get() throws InterruptedException {
        lock.readLock().lock();
        try {
            while (size == 0) {
                wait(); // buffer empty, consumer waits
            }
            int value = storage[head];
            head = (head + 1) % storage.length;
            size--;
            notifyAll(); // tell producers there is space
            return value;
        }finally {
            lock.readLock().unlock();
        }
    }
}
class Producer implements Runnable{
    private Buffer buffer;
    private int num;

    public Producer(Buffer buffer, int num) {
        this.buffer = buffer;
        this.num = num;
    }

    @Override
    public void run() {

    }
}
class Consumer implements Runnable{
    private Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {

    }
}
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int producers = sc.nextInt();
        int consumers = sc.nextInt();

        Buffer buffer = new Buffer(4);
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < producers; i++) {
            tasks.add(new Producer(buffer, i * 10));
        }

        for (int i = 0; i < consumers; i++) {
            tasks.add(new Consumer(buffer));
        }

        // THREAD EXECUTION IS INTENTIONALLY OMITTED
    }

}
