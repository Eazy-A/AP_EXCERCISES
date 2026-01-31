package labs.lab7.lab72;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class FakeApiPing {

    // Result holder
    public static class ApiResult {
        public final int requestId;
        public final boolean success;
        public final String value;

        public ApiResult(int requestId, boolean success, String value) {
            this.requestId = requestId;
            this.success = success;
            this.value = value;
        }

        @Override
        public String toString() {
            return "ApiResult{" +
                    "requestId=" + requestId +
                    ", success=" + success +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class Api {
        public static ApiResult get(int requestId, int parameter) throws InterruptedException {
            long delayMillis = parameter * 100L;
            Thread.sleep(delayMillis);

            String response = "VALUE_" + parameter;
            return new ApiResult(requestId, true, response);
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt(); // number of API calls

        List<Callable<ApiResult>> tasks = new ArrayList<>();

        Semaphore semaphore = new Semaphore(3);


        for (int i = 0; i < n; i++) {
            int parameter = sc.nextInt();

            // requestId is the loop index
            int requestId = i + 1;
            //TODO add a Callable that invokes the API get method in the tasks list

            tasks.add(() -> {
                semaphore.acquire();
                try {
                    return Api.get(requestId, parameter);
                } finally {
                    semaphore.release();
                }
            });
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<ApiResult>> futures = new ArrayList<>();

        for (Callable<ApiResult> task : tasks) {
            futures.add(executor.submit(task));
        }

        List<ApiResult> results = new ArrayList<>();

        long timeoutMillis = 200;


        boolean stopSignal = false;

        for (int i = 0; i < futures.size(); i++) {
            int requestId = i + 1;
            if (stopSignal) {
                // If we already hit a timeout, don't even try .get()
                results.add(new ApiResult(requestId, false, "FAILED"));
                continue;
            }
            try {
                // Try to get result within 200ms
                results.add(futures.get(i).get(timeoutMillis, TimeUnit.MILLISECONDS));
            } catch (Exception e) {
                // FIRST TIMEOUT DETECTED
                stopSignal = true;
                executor.shutdownNow(); // Kill all threads in the background
                results.add(new ApiResult(requestId, false, "FAILED"));
            }
        }


        // Sorting by requestId
        results.sort(Comparator.comparingInt(r -> r.requestId));

        // Output
        for (ApiResult r : results) {
            System.out.printf(
                    "%d %s %s%n",
                    r.requestId,
                    r.success ? "OK" : "FAILED",
                    r.value
            );
        }
    }
}
