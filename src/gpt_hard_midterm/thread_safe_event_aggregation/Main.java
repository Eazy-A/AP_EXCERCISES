package gpt_hard_midterm.thread_safe_event_aggregation;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class EventStatisticsService{
    private final IntSummaryStatistics cache = new IntSummaryStatistics();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void addDuration(int duration){
        lock.writeLock().lock();
        try{
            cache.accept(duration);
        }finally {
            lock.writeLock().unlock();
        }
    }

    public int getCount(){
        lock.readLock().lock();
        try{
            return Math.toIntExact(cache.getCount());
        }finally {
            lock.readLock().unlock();
        }
    }

    public int min(){
        lock.readLock().lock();
        try{
            return cache.getMin();
        }finally {
            lock.readLock().unlock();
        }
    }
    public int max(){
        lock.readLock().lock();
        try{
            return cache.getMax();
        }finally {
            lock.readLock().unlock();
        }
    }
    public double average(){
        lock.readLock().lock();
        try{
            return cache.getAverage();
        }finally {
            lock.readLock().unlock();
        }
    }
}
class AddEventTask implements Callable<String>{
    private final EventStatisticsService service;
    private final int duration;

    public AddEventTask(EventStatisticsService service, int duration) {
        this.service = service;
        this.duration = duration;
    }

    @Override
    public String call(){
        service.addDuration(duration);
        return String.format("ADDED %d\n", duration);
    }
}
class CountTask implements Callable<String>{
    private final EventStatisticsService service;

    public CountTask(EventStatisticsService service) {
        this.service = service;
    }

    @Override
    public String call() throws Exception {
        return String.format("COUNT %d\n", service.getCount());
    }
}

class MinTask implements Callable<String>{
    private final EventStatisticsService service;

    public MinTask(EventStatisticsService service) {
        this.service = service;
    }

    @Override
    public String call() throws Exception {
        return String.format("MIN %d\n", service.min());
    }
}
class MaxTask implements Callable<String>{
    private final EventStatisticsService service;

    public MaxTask(EventStatisticsService service) {
        this.service = service;
    }

    @Override
    public String call() throws Exception {
        return String.format("MAX %d\n", service.max());
    }
}
class AverageTask implements Callable<String>{
    private final EventStatisticsService service;

    public AverageTask(EventStatisticsService service) {
        this.service = service;
    }

    @Override
    public String call() throws Exception {
        return String.format("AVG %.2f", service.average());
    }
}
class ExecutorClass{
    public static List<Future<String>> execute(int threads, List<Callable<String>> tasks) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            return executor.invokeAll(tasks);
        }finally {
            executor.shutdown();
        }
    }
}
public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        EventStatisticsService service = new EventStatisticsService();
        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < n * 50; i++) {
            tasks.add(new AddEventTask(service, i * 4 + 20));
        }

        for (int i = 0; i < n * 10; i++) {
            tasks.add(new CountTask(service));
            tasks.add(new MinTask(service));
            tasks.add(new MaxTask(service));
            tasks.add(new AverageTask(service));
        }
        List<Future<String>> futures = ExecutorClass.execute(10, tasks);

        for (Future<String> f : futures){
            System.out.println(f.get());
        }
    }
}
