package second_midterm_mock.concurrent_event_statistics;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


class EventStatisticsService {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final DoubleSummaryStatistics cachedStats = new DoubleSummaryStatistics();

    public void addDuration(int duration) {
        lock.writeLock().lock();
        try {
            cachedStats.accept(duration);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getCount() {
        lock.readLock().lock();
        try {
            return Math.toIntExact(cachedStats.getCount());
        } finally {
            lock.readLock().unlock();
        }
    }

    public double getAverage() {
        lock.readLock().lock();
        try {
            return cachedStats.getAverage();
        }finally {
            lock.readLock().unlock();
        }
    }

    public double getMin() {
        lock.readLock().lock();
        try {
            return cachedStats.getMin();
        }finally {
            lock.readLock().unlock();
        }
    }

    public double getMax() {
        lock.readLock().lock();
        try {
            return cachedStats.getMax();
        }finally {
            lock.readLock().unlock();
        }
    }

}

class AddDurationTask implements Callable<String> {
    private final EventStatisticsService service;
    private final int duration;

    public AddDurationTask(EventStatisticsService service, int duration) {
        this.service = service;
        this.duration = duration;
    }

    @Override
    public String call() {
        service.addDuration(duration);
        return String.format("DURATION %d ADDED. Total: %d", duration, service.getCount());
    }
}

class GetAverageTask implements Callable<String> {
    private final EventStatisticsService service;

    public GetAverageTask(EventStatisticsService service) {
        this.service = service;
    }

    @Override
    public String call(){
        return String.format("AVERAGE: %.2f\n", service.getAverage());
    }
}

class GetMinTask implements Callable<String> {
    private final EventStatisticsService service;

    public GetMinTask(EventStatisticsService service) {
        this.service = service;
    }

    @Override
    public String call(){
        return String.format("MIN: %.2f", service.getMin());
    }

}
class GetMaxTask implements Callable<String> {
    private final EventStatisticsService service;

    public GetMaxTask(EventStatisticsService service) {
        this.service = service;
    }

    @Override
    public String call(){
        return String.format("MAX: %.2f\n", service.getMax());
    }
}
class ConcurrentExecutor {
    public static List<Future<String>> runAll(int threads, List<Callable<String>> tasks)
            throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try{
            return executor.invokeAll(tasks);
        }finally {
            executor.shutdown();
        }
    }

}

public class EventStatisticsTest {
    public static void main(String[] args) throws Exception {
        EventStatisticsService service = new EventStatisticsService();
        Scanner sc = new Scanner(System.in);
        int k = sc.nextInt();

        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 1; i <= k * 100; i++) {
            tasks.add(new AddDurationTask(service, i * 5));
        }

        for (int i = 0; i < k * 10; i++) {
            tasks.add(new GetAverageTask(service));
            tasks.add(new GetMinTask(service));
            tasks.add(new GetMaxTask(service));
        }

        List<Future<String>> results =
                ConcurrentExecutor.runAll(6, tasks);

        for (Future<String> f : results) {
            System.out.println(f.get());
        }
    }
}

