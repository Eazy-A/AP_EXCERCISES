package e32;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}

interface ILocation {
    double getLongitude();

    double getLatitude();

    LocalDateTime getTimestamp();
}
class LocationUtils{
    public static double distanceBetween(ILocation location1, ILocation location2){
            return Math.sqrt(Math.pow(location1.getLatitude() - location2.getLatitude(), 2)
            +Math.pow(location1.getLongitude() - location2.getLongitude(), 2));
    }
    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }
    public static boolean isDanger(ILocation location1, ILocation location2){
        return distanceBetween(location1, location2) <= 2.0 && timeBetweenInSeconds(location1, location2) <= 300;
    }

    public static int dangerContactsBetween(User user1, User user2){
        int counter = 0;
        for (ILocation iLocation : user1.getLocationsList()){
            for (ILocation iLocation1 : user2.getLocationsList()){
                if(isDanger(iLocation, iLocation1)){
                    counter++;
                }
            }
        }
        return counter;
    }
}

class User {
    private String name;
    private String id;
    private List<ILocation> locationsList;
    private boolean isPositive;
    private LocalDateTime timeInfected;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        locationsList = new ArrayList<>();
        isPositive = false;
    }

    public void addLocations(List<ILocation> locationsList) {
        this.locationsList.addAll(locationsList);
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    public void setTimeInfected(LocalDateTime timeInfected) {
        this.timeInfected = timeInfected;
    }

    public List<ILocation> getLocationsList() {
        return locationsList;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getTimeInfected() {
        return timeInfected != null ? timeInfected : LocalDateTime.MAX;
    }

    public boolean isPositive() {
        return isPositive;
    }

    String userComplete(){
        return String.format("%s %s %s", name, id, timeInfected);
    }
    String userHidden(){
        return String.format("%s %s***", name, id.substring(0, 4));
    }

}

class StopCoronaApp {
    private Map<String, User> userMap;
    private Map<User, Map<User, Integer>> countingMapForNearContacts;

    public StopCoronaApp() {
        userMap = new LinkedHashMap<>();
        countingMapForNearContacts = new TreeMap<>(Comparator.comparing(User::getTimeInfected).thenComparing(User::getId));
    }

    public void addUser(String name, String id) throws UserAlreadyExistException {
        if (userMap.containsKey(id)) throw new UserAlreadyExistException("User already exists");
        userMap.put(id, new User(name, id));
    }


    public void addLocations(String id, List<ILocation> iLocations) {
        userMap.get(id).addLocations(iLocations);
    }

    public void detectNewCase(String id, LocalDateTime timestamp) {
        User user = userMap.get(id);
        user.setTimeInfected(timestamp);
        user.setPositive(true);
    }

    public Map<User, Integer> getDirectContacts(User u) {
       return countingMapForNearContacts.get(u)
               .entrySet()
               .stream()
               .filter(entry -> entry.getValue() != 0)
               .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Collection<User> getIndirectContacts(User u) {
        Set<User> directContacts = getDirectContacts(u).keySet();

        Set<User> indirectContacts = new TreeSet<>(Comparator.comparing(User::getName).thenComparing(User::getId));

        directContacts.stream()
                .flatMap(user -> getDirectContacts(user).keySet().stream())
                .filter(user -> !indirectContacts.contains(user) && !directContacts.contains(user) && !user.equals(u))
                .forEach(indirectContacts::add);

        return indirectContacts;
    }

    public void createReport() {
        for (User u : userMap.values()) {
            for (User u1 : userMap.values()) {
                if (!u.equals(u1)) {
                    countingMapForNearContacts.putIfAbsent(u, new TreeMap<>(Comparator.comparing(User::getTimeInfected).thenComparing(us -> us.getId())));
                    countingMapForNearContacts.computeIfPresent(u, (k, v) -> {
                        v.putIfAbsent(u1, 0);
                        v.computeIfPresent(u1, (k1, v1) -> {
                            v1 += LocationUtils.dangerContactsBetween(u, u1);
                            return v1;
                        });
                        return v;
                    });
                }
            }
        }

        List<Integer> directContactsCounts = new ArrayList<>();
        List<Integer> indirectContactsCounts = new ArrayList<>();

        for (User u1 : countingMapForNearContacts.keySet()) {
            if (u1.isPositive()) {
                System.out.println(u1.userComplete());
                System.out.println("Direct contacts:");
                Map<User, Integer> directContact = getDirectContacts(u1);
                directContact.entrySet().stream()
                        .sorted(comparingByValue(Comparator.reverseOrder()))
                        .forEach(entry -> System.out.println(String.format("%s %s", entry.getKey().userHidden(), entry.getValue())));
                int count = directContact.values().stream().mapToInt(i -> i).sum();
                System.out.println(String.format("Count of direct contacts: %d", count));
                directContactsCounts.add(count);


                Collection<User> indirectContacts = getIndirectContacts(u1);
                System.out.println("Indirect contacts: ");
                indirectContacts.forEach(user -> System.out.println(user.userHidden()));
                System.out.println(String.format("Count of indirect contacts: %d", indirectContacts.size()));
                indirectContactsCounts.add(indirectContacts.size());
            }
        }

        System.out.printf("Average direct contacts: %.4f\n", directContactsCounts.stream().mapToInt(i -> i).average().getAsDouble());
        System.out.printf("Average indirect contacts: %.4f", indirectContactsCounts.stream().mapToInt(i -> i).average().getAsDouble());
    }
}

public class StopCoronaTest {


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG": //register
                    String name = parts[1];
                    String id = parts[2];
                    try {
                        stopCoronaApp.addUser(name, id);
                    } catch (UserAlreadyExistException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC": //add locations
                    id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);

                    break;
                case "DET": //detect new cases
                    id = parts[1];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
                    stopCoronaApp.detectNewCase(id, timestamp);

                    break;
                case "REP": //print report
                    stopCoronaApp.createReport();
                    break;
                default:
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            @Override
            public double getLongitude() {
                return Double.parseDouble(lon);
            }

            @Override
            public double getLatitude() {
                return Double.parseDouble(lat);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.parse(timestamp);
            }
        };
    }
}
