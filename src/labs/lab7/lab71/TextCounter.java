package labs.lab7.lab71;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextCounter {

    // Result holder
    public static class Counter {
        public final int textId;
        public final int lines;
        public final int words;
        public final int chars;

        public Counter(int textId, int lines, int words, int chars) {
            this.textId = textId;
            this.lines = lines;
            this.words = words;
            this.chars = chars;
        }

        @Override
        public String toString() {
            return "Counter{" +
                    "textId=" + textId +
                    ", lines=" + lines +
                    ", words=" + words +
                    ", chars=" + chars +
                    '}';
        }


    }


    public static Callable<Counter> getTextCounter(int textId, String text) {
        return () -> {
            int lines = text.isEmpty() ? 0 : text.split("\n", -1).length;

            String trimmed = text.trim();

            int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;

            int chars = text.length();

            return new Counter(textId, lines, words, chars);
        };
    }


    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();       // number of texts
        sc.nextLine();              // consume newline

        List<Callable<Counter>> tasks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int textId = sc.nextInt();
            sc.nextLine();          // consume newline

            int lines = sc.nextInt();   // number of lines for this text
            sc.nextLine();              // consume newline

            StringBuilder text = new StringBuilder();
            for (int j = 0; j < lines; j++) {
                text.append(sc.nextLine());
                if (j < lines - 1) {
                    text.append("\n");
                }
            }
            //TODO add a Callable<Counter> for each text read in the tasks list
            Callable<Counter> task = getTextCounter(textId, text.toString());
            tasks.add(task);
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        //TODO invoke All tasks on the executor and create a List<Future<?>>
        List<Future<Counter>> futures = executor.invokeAll(tasks);

        List<Counter> results = new ArrayList<>();

        //TODO extract results from the List<Future>
        for (Future<Counter> f : futures) {
            results.add(f.get());
        }

        Callable<Counter> aggregatorTask = () -> {
            int totalLines = 0;
            int totalWords = 0;
            int totalCharacters = 0;

            for (Counter c : results) {
                totalLines += c.lines;
                totalWords += c.words;
                totalCharacters += c.chars;
            }

            return new Counter(-1, totalLines, totalWords, totalCharacters);
        };

        Future<Counter> aggregateFuture = executor.submit(aggregatorTask);

        Counter finalResult = aggregateFuture.get();

        executor.shutdown();

        // Sorting by textId (important concept!)
        results.sort(Comparator.comparingInt(c -> c.textId));

        // Output (optional for debugging / demonstration)
        for (Counter c : results) {
            System.out.printf(
                    "%d %d %d %d%n",
                    c.textId, c.lines, c.words, c.chars
            );
        }
        System.out.println("Total: " + finalResult);
    }
}
