package labs.lab4.lab43;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;


class TermFrequency {

    private Map<String, Integer> frequencies = new HashMap<>();

    public TermFrequency(InputStream inputStream, String[] stopWords) throws IOException {
        Set<String> stopSet = new HashSet<>(Arrays.asList(stopWords));
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        while (true) {
            String line = br.readLine();
            if (line == null) break;
            if (line.isEmpty()) continue;

            line = line.toLowerCase()
                    .replaceAll("\\.", "")
                    .replaceAll(",", "")
                    .trim();

            String[] words = line.split("\\s++");
            for (String word : words) {
                word = word.toLowerCase().trim();

                if (word.isEmpty() || stopSet.contains(word)) continue;

                frequencies.put(word, frequencies.getOrDefault(word, 0) + 1);
            }
        }
    }

    public int countTotal() {
        return frequencies.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int countDistinct(){
        return  frequencies.size();
    }

    public List<String> mostOften(int k){
        List<Entry<String, Integer>> list = new ArrayList<>(frequencies.entrySet());

        list.sort((a, b) -> {
            int cmp = b.getValue().compareTo(a.getValue());
            if (cmp != 0) return cmp;
            return a.getKey().compareTo(b.getKey());
        });

        List<String> result = new ArrayList<>();
        for (int i = 0; i < k && i < list.size(); i++) {
            result.add(list.get(i).getKey());
        }
        return result;
    }

}

public class TermFrequencyTest {
    public static void main(String[] args) throws IOException {
        String[] stop = new String[]{"во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја"};
        TermFrequency tf = new TermFrequency(System.in,
                stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}
// vasiot kod ovde

