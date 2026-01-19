package e8;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class NonExistingItemException extends Exception {
    public NonExistingItemException(String message) {
        super(message);
    }
}

abstract class Archive {
    protected int id;
    protected LocalDateTime dateArchived;

    public Archive(int id) {
        this.id = id;
    }

    public int getId() { return id; }

    public void setDateArchived(LocalDateTime date) {
        this.dateArchived = date;
    }

    public abstract String open(LocalDateTime attemptDate, DateTimeFormatter df);
}

class LockedArchive extends Archive {
    private LocalDateTime dateToOpen;

    public LockedArchive(int id, LocalDateTime dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }

    public LocalDateTime getDateToOpen() { return dateToOpen; }

    @Override
    public String open(LocalDateTime attemptDate, DateTimeFormatter df) {
        if (attemptDate.isBefore(dateToOpen)) {
            return String.format("Item %d cannot be opened before %s",
                    id, dateToOpen.format(df));
        }
        return String.format("Item %d opened at %s", id, attemptDate.format(df));
    }
}

class SpecialArchive extends Archive {
    private int maxOpen;
    private int timesOpened;

    public SpecialArchive(int id, int maxOpen) {
        super(id);
        this.maxOpen = maxOpen;
        this.timesOpened = 0;
    }

    public int getMaxOpen() { return maxOpen; }
    public int getTimesOpened() { return timesOpened; }
    public void incrementOpened() { this.timesOpened++; }

    @Override
    public String open(LocalDateTime attemptDate, DateTimeFormatter df) {
        if (timesOpened >= maxOpen) {
            return String.format("Item %d cannot be opened more than %d times",
                    id, maxOpen);
        }
        timesOpened++;
        return String.format("Item %d opened at %s", id, attemptDate.format(df));
    }
}

class ArchiveStore {
    private List<Archive> archiveList = new ArrayList<>();
    private StringBuilder log = new StringBuilder();

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy", Locale.US);

    public void archiveItem(Archive item, LocalDateTime date) {
        item.setDateArchived(date);
        archiveList.add(item);
        String formattedDate = date.format(DATE_FORMATTER);
        log.append(String.format("Item %d archived at %s\n", item.getId(), formattedDate));
    }

    public void openItem(int id, LocalDateTime date) throws NonExistingItemException {
        Archive a = archiveList.stream()
                .filter(archive -> archive.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NonExistingItemException("Item with id " + id + " doesn't exist"));

        String result = a.open(date, DATE_FORMATTER);
        log.append(result).append("\n");
    }

    public String getLog() {
        return log.toString();
    }
}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        // Equivalent to new Date(113, 10, 7) -> Nov 7, 2013
        LocalDateTime date = LocalDateTime.of(2013, 11, 7, 0, 0);

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine(); scanner.nextLine();

        for (int i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();
            // Modern date math
            LocalDateTime dateToOpen = date.plusDays(days);
            store.archiveItem(new LockedArchive(id, dateToOpen), date);
        }

        scanner.nextLine(); scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine(); scanner.nextLine();

        for (int i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            store.archiveItem(new SpecialArchive(id, maxOpen), date);
        }

        scanner.nextLine(); scanner.nextLine();
        while (scanner.hasNextInt()) {
            int idToOpen = scanner.nextInt();
            try {
                store.openItem(idToOpen, date);
            } catch (NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}