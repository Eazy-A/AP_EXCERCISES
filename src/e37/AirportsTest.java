package e37;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class Airport {
    private final String name;
    private final String country;
    private final String code;
    private final int passengers;

    public Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public int getPassengers() {
        return passengers;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)\n%s\n%d", name, code, country, passengers);
    }
}

class Flight {
    private final String from;
    private final String to;
    private final int time;
    private final int duration;

    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.duration = duration;
    }

    public int getTime() {
        return time;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String calculatedTime() {
        int startHours = (time / 60) % 24;
        int startMinutes = time % 60;

        int totalMinutes = time + duration;
        int endHours = (totalMinutes / 60) % 24;
        int endMinutes = totalMinutes % 60;

        int days = totalMinutes / (24 * 60);
        String dayArrival = days > 0 ? " +" + days + "d" : "";

        int dH = duration / 60;
        int dM = duration % 60;

        return String.format("%02d:%02d-%02d:%02d%s %dh%02dm",
                startHours, startMinutes, endHours, endMinutes, dayArrival, dH, dM);
    }

    @Override
    public String toString() {
        return String.format("%s-%s %s", from, to, calculatedTime());
    }
}

class Airports {
    private final List<Airport> airportsList = new ArrayList<>();
    private final Set<Flight> flightSet = new TreeSet<>(Comparator.comparing(Flight::getTo).thenComparing(Flight::getTime).thenComparing(Flight::getFrom));

    public void addAirport(String name, String country, String code, int passengers) {
        airportsList.add(new Airport(name, country, code, passengers));
    }

    public void addFlights(String from, String to, int time, int duration) {
        flightSet.add(new Flight(from, to, time, duration));
    }

    public void showFlightsFromAirport(String code) {
        Airport airport = airportsList.stream()
                .filter(a -> a.getCode().equals(code))
                .findFirst()
                .orElseThrow();

        System.out.println(airport);
        AtomicInteger index = new AtomicInteger(1);
        flightSet.stream()
                .filter(f -> f.getFrom().equals(code))
                .forEach(f -> System.out.println(index.getAndIncrement() + ". " + f));

    }

    public void showDirectFlightsFromTo(String from, String to) {
        List<Flight> flights = flightSet.stream()
                .filter(flight -> flight.getFrom().equals(from) && flight.getTo().equals(to))
                .collect(Collectors.toList());

        if (flights.isEmpty()) System.out.println("No flights from " + from + " to " + to);
        else flights.forEach(System.out::println);
    }

    public void showDirectFlightsTo(String to) {
        flightSet.stream()
                .filter(flight -> flight.getTo().equals(to))
                .forEach(System.out::println);
    }
}

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}


