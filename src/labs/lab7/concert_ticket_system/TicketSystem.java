package labs.lab7.concert_ticket_system;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class TicketSystem {

    public static class TicketAccount {
        private int ticketsAvailable = 100;
        private final ReentrantLock lock = new ReentrantLock();

        public boolean buyTicket() throws InterruptedException {
            // TODO: Use the lock here to safely decrease ticketsAvailable
            // 1. Lock 2. Check if > 0 3. Decrease 4. Unlock in 'finally'
            if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    if (ticketsAvailable > 0) {
                        ticketsAvailable--;
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }
    }

    public static class PurchaseResult {
        public final int requestId;
        public final boolean success;

        public PurchaseResult(int requestId, boolean success) {
            this.requestId = requestId;
            this.success = success;
        }
    }

    public static void main(String[] args) throws Exception {
        TicketAccount account = new TicketAccount();
        ExecutorService executor = Executors.newFixedThreadPool(20);
        Semaphore serverCapacity = new Semaphore(10); // Limit to 10

        List<Callable<PurchaseResult>> tasks = new ArrayList<>();

        for (int i = 1; i <= 200; i++) {
            int id = i;
            tasks.add(() -> {
                // TODO: 1. Use semaphore.acquire()
                serverCapacity.acquire();
                // TODO: 2. Try to buy ticket
                try {
                    boolean success = account.buyTicket();
                    return new PurchaseResult(id, success);
                } finally {
                    serverCapacity.release();
                }
                // TODO: 3. Use semaphore.release() in 'finally'
            });
        }

        List<Future<PurchaseResult>> futures = new ArrayList<>();

        for (Callable<PurchaseResult> t : tasks) {
            futures.add(executor.submit(t));
        }

        List<PurchaseResult> results = new ArrayList<>();


        boolean stopSignal = false;
        for (int i = 0; i < futures.size(); i++) {
            int requestId = i + 1;
            if (stopSignal) {
                results.add(new PurchaseResult(requestId, false));
                continue;
            }
            try {
                results.add(futures.get(i).get(200, TimeUnit.MILLISECONDS));
            } catch (Exception e) {
                stopSignal = true;
                executor.shutdownNow();
                results.add(new PurchaseResult(requestId, false));
            }
        }

        // TODO: Invoke tasks and handle the "Kill Switch" if a Timeout occurs
        // TODO: Sort results by requestId and print them
    }
}
