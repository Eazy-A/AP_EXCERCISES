package second_midterm_mock.news_sub_system;

import java.time.LocalDateTime;
import java.util.*;

interface Observer {
    void update(Event event);
    void printFeed();
}

class User implements Observer {
    private final String username;
    private final Set<Event> eventsFeed = new TreeSet<>(Comparator.comparing(Event::getTimestamp));

    public User(String username) {
        this.username = username;
    }

    @Override
    public void update(Event event) {
        eventsFeed.add(event);
    }
    public void printFeed(){
        eventsFeed.forEach(System.out::print);
    }

}

class EventNewsSystem {
    private final Map<String, Observer> userMap = new HashMap<>();
    private final Map<String, Set<Observer>> typeSubscribers = new HashMap<>();
    private final Map<String, Set<Observer>> organizerSubscribers = new HashMap<>();

    public EventNewsSystem(List<String> types, List<String> organizers) {
        types.forEach(type -> typeSubscribers.putIfAbsent(type, new HashSet<>()));
        organizers.forEach(organizer -> organizerSubscribers.putIfAbsent(organizer, new HashSet<>()));
    }

    public void addUser(String username) {
        userMap.putIfAbsent(username, new User(username));
    }

    public void subscribeUserToType(String username, String type) {
        if (!typeSubscribers.containsKey(type) || !userMap.containsKey(username)) return;
        typeSubscribers.get(type).add(userMap.get(username));
    }

    public void unsubscribeUserFromType(String username, String type) {
        if (!typeSubscribers.containsKey(type) || !userMap.containsKey(username)) return;
        typeSubscribers.get(type).remove(userMap.get(username));
    }

    public void subscribeUserToOrganizer(String username, String organizer) {
        if (!organizerSubscribers.containsKey(organizer) || !userMap.containsKey(username)) return;
        organizerSubscribers.get(organizer).add(userMap.get(username));
    }

    public void unsubscribeUserFromOrganizer(String username, String organizer) {
        if (!organizerSubscribers.containsKey(organizer) || !userMap.containsKey(username)) return;
        organizerSubscribers.get(organizer).remove(userMap.get(username));
    }

    public void publishEvent(Event event) {
        String type = event.getType();
        String organizer = event.getOrganizer();

        typeSubscribers.get(type).forEach(user -> user.update(event));
        organizerSubscribers.get(organizer).forEach(user -> user.update(event));
    }

    public void printEventsForUser(String username) {
        if (!userMap.containsKey(username)) return;
        System.out.println("News for user " + username);
        userMap.get(username).printFeed();
    }
}

class Event {
    private final String type;
    private final String organizer;
    private final String description;
    private final LocalDateTime timestamp;

    public Event(String type, String organizer, String description, LocalDateTime timestamp) {
        this.type = type;
        this.organizer = organizer;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public String getOrganizer() {
        return organizer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "]" + organizer + " - " + type + "\n"
                + description + "\n";
    }
}

public class EventNewsTest {
    public static void main(String[] args) {
        List<String> types = List.of("concert", "conference", "sports");
        List<String> organizers = List.of("LiveNation", "UEFA", "Google");

        EventNewsSystem system = new EventNewsSystem(types, organizers);
        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);

            switch (parts[0]) {
                case "ADD_USER":
                    system.addUser(parts[1]);
                    break;

                case "SUB_TYPE": {
                    String[] p = parts[1].split("\\s+");
                    system.subscribeUserToType(p[0], p[1]);
                    break;
                }

                case "SUB_ORG": {
                    String[] p = parts[1].split("\\s+");
                    system.subscribeUserToOrganizer(p[0], p[1]);
                    break;
                }

                case "PUBLISH": {
                    String[] p = parts[1].split("\\s+", 4);
                    Event event = new Event(
                            p[0],
                            p[1],
                            p[3],
                            LocalDateTime.parse(p[2])
                    );
                    system.publishEvent(event);
                    break;
                }

                case "PRINT":
                    system.printEventsForUser(parts[1]);
                    break;
            }
        }
    }
}
