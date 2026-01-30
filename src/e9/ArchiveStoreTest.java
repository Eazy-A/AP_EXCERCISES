package e9;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.*;

class NonExistingItemException extends Exception {
    public NonExistingItemException(int id) {
        super("Item with id " + id + " doesn't exist");
    }
}

class Archive {
    int id;
    LocalDate dateArchived;

    public Archive(LocalDate dateArchived, int id) {
        this.dateArchived = dateArchived;
        this.id = id;
    }
    public String tryOpen(LocalDate openDate){
        return "Item " + id + " opened at " + openDate;
    }

}

class LockedArchive extends Archive {
    LocalDate dateToOpen;

    public LockedArchive(int id, LocalDate dateToOpen) {
        super(LocalDate.now(), id);
        this.dateToOpen = dateToOpen;
    }

    @Override
    public String tryOpen(LocalDate openDate) {
        if (dateToOpen.isAfter(openDate)){
            return "Item " + id + " cannot be opened before " + dateToOpen;
        }
        return super.tryOpen(openDate);
    }
}

class SpecialArchive extends Archive {
    int maxOpen;
    int timesOpened = 0;

    public SpecialArchive(int id, int maxOpen) {
        super(LocalDate.now(), id);
        this.maxOpen = maxOpen;
    }

    @Override
    public String tryOpen(LocalDate openDate) {
        if (timesOpened >= maxOpen) {
            return "Item " + id + " cannot be opened more than " + maxOpen + " times";
        }
        timesOpened++;
        return super.tryOpen(openDate);
    }
}

class ArchiveStore {
    private final List<Archive> archives;
    private final List<String> logs = new ArrayList<>();

    public ArchiveStore() {
        archives = new ArrayList<>();
    }

    public void archiveItem(Archive item, LocalDate date) {
        item.dateArchived = date;
        archives.add(item);
        logs.add("Item " + item.id + " archived at " + item.dateArchived);
    }

    public void openItem(int id, LocalDate date) throws NonExistingItemException {
        Archive foundArchive = archives.stream()
                .filter(a -> a.id == id)
                .findFirst()
                .orElseThrow(() -> new NonExistingItemException(id));

        String result = foundArchive.tryOpen(date);
        logs.add(result);
    }

    String getLog() {
        return String.join("\n", logs);
    }

}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while (scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch (NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}




