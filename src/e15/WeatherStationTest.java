package e15;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class Measurement {
    private float temperature;
    private float humidity;
    private float wind;
    private float visibility;
    private LocalDateTime date;

    public Measurement(float temperature, float humidity, float wind, float visibility, LocalDateTime date) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.visibility = visibility;
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public float getTemperature() {
        return temperature;
    }

    @Override
    public String toString() {
//        24.6 80.2 km/h 28.7% 51.7 km Tue Dec 17 23:40:15 CET 2013
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s", temperature, wind, humidity, visibility, date);
    }
}

class WeatherStation {
    private int days;
    private List<Measurement> measurementList = new ArrayList<>(days);

    public WeatherStation(int days) {
        this.days = days;
    }

    public void addMeasurement(float temperature, float wind, float humidity, float visibility, LocalDateTime date) {
        if(!measurementList.isEmpty()){
            LocalDateTime lastDate = measurementList.get(measurementList.size()-1).getDate();

            if(ChronoUnit.SECONDS.between(lastDate, date) < 150) return;

        }
        measurementList.add(new Measurement(temperature, humidity, wind, visibility, date));
        measurementList.removeIf(m -> ChronoUnit.DAYS.between(m.getDate(), date) >= days);
    }

    public int total() {
        return measurementList.size();
    }

    public void status(LocalDateTime from, LocalDateTime to) {
        List<Measurement> filteredMeasurements = measurementList.stream()
                .filter(measurement -> (measurement.getDate().isAfter(from) || measurement.getDate().isEqual(from))
                        && (measurement.getDate().isBefore(to) || measurement.getDate().isEqual(to)))
                        .collect(Collectors.toList());

        if (filteredMeasurements.isEmpty()) throw new RuntimeException("java.lang.RuntimeException");

        filteredMeasurements.forEach(System.out::println);

        System.out.printf("Average temperature: %.2f", avgTemperature(filteredMeasurements));
    }

    public float avgTemperature(List<Measurement> measurements){
        return (float) measurements.stream()
                .mapToDouble(Measurement::getTemperature)
                .average()
                .orElse(0);
    }
}

public class WeatherStationTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);

        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);

            line = scanner.nextLine();


            LocalDateTime date = LocalDateTime.parse(line, df);

            ws.addMeasurement(temp, wind, hum, vis, date);
        }

        String lineFrom = scanner.nextLine();
        LocalDateTime from = LocalDateTime.parse(lineFrom, df);

        String lineTo = scanner.nextLine();
        LocalDateTime to = LocalDateTime.parse(lineTo, df);

        scanner.close();

        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
