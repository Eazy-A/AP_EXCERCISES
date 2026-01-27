package gpt_hard_midterm.generics_with_thread_safety;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

interface Repository<T>{
    String print();
    int getSize();
    void add(T element);
}
class ConcurrentRepository<T> implements Repository<T>{
    private final List<T> elements = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void add(T element){
        lock.writeLock().lock();
        try{
            elements.add(element);
        }finally {
            lock.writeLock().unlock();
        }
    }
    @Override
    public String print() {
        lock.readLock().lock();
        try{
            StringBuilder sb = new StringBuilder();
            elements.forEach(element -> sb.append("PRINT: ").append(element).append("\n"));
            return sb.toString();
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getSize() {
        lock.readLock().lock();
        try{
            return elements.size();
        }finally {
            lock.readLock().unlock();
        }
    }
}
class AddTask<T> implements Callable<String>{
    private final Repository<T> repo;
    private final T task;

    public AddTask(Repository<T> repo, T task) {
        this.repo = repo;
        this.task = task;
    }

    @Override
    public String call(){
        repo.add(task);
        return "ADDED " + task;
    }
}
class SizeTask<T> implements Callable<String>{
    private final Repository<T> repo;

    public SizeTask(Repository<T> repo) {
        this.repo = repo;
    }

    @Override
    public String call() throws Exception {
        return "SIZE: " + repo.getSize();
    }
}
class PrintTask<T> implements Callable<String>{
    private final Repository<T> repo;

    public PrintTask(Repository<T> repo) {
        this.repo = repo;
    }

    @Override
    public String call() throws Exception {
        return repo.print();
    }
}
class ExecutorClass{
    public static List<Future<String>> execute(int numThreads, List<Callable<String>> tasks) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(6);
        try{
            return executor.invokeAll(tasks);
        }finally {
            executor.shutdown();
        }
    }
}
public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Repository<String> repo = new ConcurrentRepository<>();

        List<Callable<String>> tasks = new ArrayList<>();

        tasks.add(new AddTask<>(repo, "X"));
        tasks.add(new AddTask<>(repo, "Y"));
        tasks.add(new AddTask<>(repo, "Z"));

        tasks.add(new SizeTask<>(repo));
        tasks.add(new PrintTask<>(repo));

        List<Future<String>> futures = ExecutorClass.execute(6, tasks);

        for (Future<String> f : futures){
            System.out.println(f.get());
        }

    }

}
