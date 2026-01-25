package e42;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * I partial exam 2016
 */
enum Unit {
    F {
        @Override
        public double toCelsius(double t) {
            return (t - 32) * 5 / 9;
        }

        @Override
        public double fromCelsius(double t) {
            return (t * 9) / 5 + 32;
        }
    },
    C {
        @Override
        public double toCelsius(double t) {
            return t;
        }

        @Override
        public double fromCelsius(double t) {
            return t;
        }
    };

    public abstract double toCelsius(double t);

    public abstract double fromCelsius(double t);

    public double convertTo(double t, Unit targetUnit) {
        return targetUnit.fromCelsius(this.toCelsius(t));
    }
}

class Day implements Comparable<Day> {
    private final Unit originalUnit;
    private final int dayOfYear;
    private final List<Double> temperatures;


    public Day(Unit unit, int dayOfYear, List<Double> temperatures) {
        this.originalUnit = unit;
        this.dayOfYear = dayOfYear;
        this.temperatures = temperatures;
    }


    public String getStatsString(Unit targetUnit) {
        DoubleSummaryStatistics statistics = temperatures.stream()
                .mapToDouble(t -> originalUnit.convertTo(t, targetUnit))
                .summaryStatistics();

        return String.format("%3d: Count: %3d Min: %6.2f%s Max: %6.2f%s Avg: %6.2f%s",
                dayOfYear, temperatures.size(), statistics.getMin(), targetUnit, statistics.getMax(), targetUnit, statistics.getAverage(), targetUnit);
    }

    @Override
    public String toString() {
        return getStatsString(originalUnit);
    }

    @Override
    public int compareTo(Day o) {
        return Integer.compare(this.dayOfYear, o.dayOfYear);
    }
}

class DailyTemperatures {
    private final List<Day> daysData = new ArrayList<>();

    public DailyTemperatures() {
    }

    public void readTemperatures(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");

            int dayOfYear = Integer.parseInt(parts[0]);
            List<Double> temps = new ArrayList<>();
            if (parts[1].contains("F")) {
                for (int i = 1; i < parts.length; i++) {
                    parts[i] = parts[i].replace("F", "");
                    temps.add(Double.parseDouble(parts[i]));
                }
                daysData.add(new Day(Unit.F, dayOfYear, temps));
            } else {
                for (int i = 1; i < parts.length; i++) {
                    parts[i] = parts[i].replace("C", "");
                    temps.add(Double.parseDouble(parts[i]));
                }
                daysData.add(new Day(Unit.C, dayOfYear, temps));
            }
        }

    }

    public void writeDailyStats(OutputStream outputStream, char scale) {
        PrintStream ps = new PrintStream(outputStream);
        Unit unit = Unit.valueOf(String.valueOf(scale));

        daysData.stream()
                .sorted()
                .forEach(day -> ps.println(day.getStatsString(unit)));
    }

}

public class DailyTemperatureTest {
    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, 'C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, 'F');
    }
}
