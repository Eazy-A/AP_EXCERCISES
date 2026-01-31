package labs.lab5.lab52;

import java.io.InputStream;
import java.util.*;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);

        Map<String, Set<String>> anagrams = new TreeMap<>();

        while(scanner.hasNext()){
            String word = scanner.next();
            char[] letters = word.toCharArray();
            Arrays.sort(letters);
            String sortedWord = String.valueOf(letters);


            anagrams.computeIfAbsent(sortedWord, k -> new TreeSet<>())
                    .add(word);
        }

        anagrams.entrySet().stream()
                        .sorted(Comparator.comparing(e -> e.getValue()
                                .iterator().next()))
                                .forEach(e -> {
                                    e.getValue()
                                            .forEach(s -> System.out.print(s + " "));
                                    System.out.println();
                                });


    }
}
