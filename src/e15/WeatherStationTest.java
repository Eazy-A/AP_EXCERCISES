package e15;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class Measurement {
    private final float temperature;
    private final float humidity;
    private final float wind;
    private final float visibility;
    private final Date date;

    public Measurement(float temperature, float humidity, float wind, float visibility, Date date) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.visibility = visibility;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public float getTemperature() {
        return temperature;
    }

    @Override
    public String toString() { // goofy aah
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s",
                temperature, wind, humidity, visibility, sdf.format(date));
    }
}

class WeatherStation {
    private int days;
    private final List<Measurement> measurementList = new ArrayList<>(days);

    public WeatherStation(int days) {
        this.days = days;
    }

    public void addMeasurement(float temperature, float wind, float humidity, float visibility, Date date) {
        if (!measurementList.isEmpty()) {
            Date lastDate = measurementList.get(measurementList.size() - 1).getDate();

            if (ChronoUnit.SECONDS.between(lastDate.toInstant(), date.toInstant()) < 150) return;

        }
        measurementList.add(new Measurement(temperature, humidity, wind, visibility, date));
        measurementList.removeIf(m -> ChronoUnit.DAYS.between(m.getDate().toInstant(), date.toInstant()) >= days);
    }

    public int total() {
        return measurementList.size();
    }

    public void status(Date from, Date to) {
        List<Measurement> filteredMeasurements = measurementList.stream()
                .filter(measurement -> (measurement.getDate().after(from) || measurement.getDate().equals(from))
                        && (measurement.getDate().before(to) || measurement.getDate().equals(to)))
                .collect(Collectors.toList());

        if (filteredMeasurements.isEmpty()) throw new RuntimeException();

        filteredMeasurements.forEach(System.out::println);

        System.out.printf("Average temperature: %.2f", avgTemperature(filteredMeasurements));
    }

    public double avgTemperature(List<Measurement> measurements) {
        return measurements.stream()
                .mapToDouble(Measurement::getTemperature)
                .average()
                .orElse(0);
    }
}

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
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
            Date date = df.parse(line);
            ws.addMeasurement(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}