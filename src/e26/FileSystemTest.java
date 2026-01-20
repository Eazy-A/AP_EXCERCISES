package e26;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Partial exam II 2016/2017
 */
class File implements Comparable<File> {
    private final String name;
    private final int size;
    private final LocalDateTime timeStamp;

    public File(String name, int size, LocalDateTime timeStamp) {
        this.name = name;
        this.size = size;
        this.timeStamp = timeStamp;
    }

    public int getSize() {
        return size;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(File o) {
        if (this.timeStamp.isAfter(o.timeStamp)) return 1;
        else if (this.timeStamp.isBefore(o.timeStamp)) return -1;
        else if (String.CASE_INSENSITIVE_ORDER.compare(this.name, o.name) == 0)
            return Integer.compare(this.size, o.size);
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return String.format("%-10s %5dB %s", name, size, timeStamp);
    }
}

class FileSystem {
    private final Map<Character, Set<File>> folders = new LinkedHashMap<>();

    public void addFile(char folder, String name, int size, LocalDateTime createdAt) {
        folders.computeIfAbsent(folder, _ -> new LinkedHashSet<>()).add(new File(name, size, createdAt)); // k instead of _ so it works in moodle
    }

    public List<File> findAllHiddenFilesWithSizeLessThen(int size) {
        return folders.values().stream()
                .flatMap(Collection::stream)
                .filter(file -> file.getName().startsWith(".") && file.getSize() < size)
                .sorted()
                .collect(Collectors.toList());
    }

    public int totalSizeOfFilesFromFolders(List<Character> f) {
        return this.folders.entrySet().stream()
                .filter(entry -> f.contains(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .mapToInt(File::getSize)
                .sum();
    }

    public Map<Integer, Set<File>> byYear() {
        return folders.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.groupingBy(
                        file -> file.getTimeStamp().getYear(),
                        HashMap::new,
                        Collectors.toSet()
                ));
    }

    public Map<String, Long> sizeByMonthAndDay() {
        return folders.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.groupingBy(
                        file -> file.getTimeStamp().getMonth().toString() + "-" + file.getTimeStamp().getDayOfMonth(),
                        HashMap::new,
                        Collectors.summingLong(File::getSize)
                ));
    }

}

public class FileSystemTest {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            fileSystem.addFile(parts[0].charAt(0), parts[1],
                    Integer.parseInt(parts[2]),
                    LocalDateTime.of(2016, 12, 29, 0, 0, 0).minusDays(Integer.parseInt(parts[3]))
            );
        }
        int action = scanner.nextInt();
        if (action == 0) {
            scanner.nextLine();
            int size = scanner.nextInt();
            System.out.println("== Find all hidden files with size less then " + size);
            List<File> files = fileSystem.findAllHiddenFilesWithSizeLessThen(size);
            files.forEach(System.out::println);
        } else if (action == 1) {
            scanner.nextLine();
            String[] parts = scanner.nextLine().split(":");
            System.out.println("== Total size of files from folders: " + Arrays.toString(parts));
            int totalSize = fileSystem.totalSizeOfFilesFromFolders(Arrays.stream(parts)
                    .map(s -> s.charAt(0))
                    .collect(Collectors.toList()));
            System.out.println(totalSize);
        } else if (action == 2) {
            System.out.println("== Files by year");
            Map<Integer, Set<File>> byYear = fileSystem.byYear();
            byYear.keySet().stream().sorted()
                    .forEach(key -> {
                        System.out.printf("Year: %d\n", key);
                        Set<File> files = byYear.get(key);
                        files.stream()
                                .sorted()
                                .forEach(System.out::println);
                    });
        } else if (action == 3) {
            System.out.println("== Size by month and day");
            Map<String, Long> byMonthAndDay = fileSystem.sizeByMonthAndDay();
            byMonthAndDay.keySet().stream().sorted()
                    .forEach(key -> System.out.printf("%s -> %d\n", key, byMonthAndDay.get(key)));
        }
        scanner.close();
    }
}

