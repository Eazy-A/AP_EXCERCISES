package parallel_sum;

import java.util.*;
import java.util.concurrent.*;

class SumTask implements Callable<Long> {
    private int[] array;
    private int start, end;

    public SumTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    public Long call() {
        long localSum = 0;
        for (int i = start; i < end; i++) {
            localSum += array[i];
        }
        return localSum;
    }
}

public class ParallelSumTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int[] data = new int[1_000_000];
        Random r = new Random();
        for(int i=0; i<data.length; i++) data[i] = r.nextInt(100) + 1;

        int numThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Future<Long>> futures = new ArrayList<>();

        int chunkSize = data.length / numThreads;
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? data.length : (i + 1) * chunkSize;
            SumTask task = new SumTask(data, start, end);
            futures.add(executor.submit(task));
        }

        long globalTotal = 0;
        for (Future<Long> f : futures){
            globalTotal += f.get();
        }


        System.out.println("Total Sum " + globalTotal);
        executor.shutdown();
    }
}