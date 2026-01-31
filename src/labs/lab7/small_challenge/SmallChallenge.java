package labs.lab7.small_challenge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SmallChallenge {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {

        List<Callable<Integer>> numbers = new ArrayList<>();
        int[] inputs = {2, 4, 6};
        for (int i : inputs) {
            numbers.add(() -> {
                Thread.sleep(1000);
                return i * i;
            });
        }


        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Future<Integer>> futures = executor.invokeAll(numbers);

        for (Future<Integer> f : futures) {
            Integer result = f.get(10, TimeUnit.SECONDS);
            System.out.println("The square is: " + result);
        }

        executor.shutdown();

    }
}
