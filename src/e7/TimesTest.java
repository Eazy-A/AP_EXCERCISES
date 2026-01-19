package e7;

import java.io.*;
import java.util.*;

import static java.lang.Integer.parseInt;

class UnsupportedFormatException extends Exception {
    public UnsupportedFormatException(String message) {
        super(message);
    }
}

class InvalidTimeException extends Exception {
    public InvalidTimeException(String message) {
        super(message);
    }
}

interface TimeFormatter {
    String format(Time time);
}

class Format24 implements TimeFormatter {

    @Override
    public String format(Time time) {
        return String.format("%2d:%02d", time.getHours(), time.getMinutes());
    }
}

class FormatAMPM implements TimeFormatter {
    @Override
    public String format(Time time) {
        int h = (time.getHours() == 0 || time.getHours() == 12) ? 12 : time.getHours() % 12;
        String suffix = (time.getHours() < 12) ? "AM" : "PM";
        return String.format("%2d:%02d %s", h, time.getMinutes(), suffix);
    }
}

class Time {
    private int hours;
    private int minutes;

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

}

class TimeTable {
    List<Time> times;

    public TimeTable() {
        times = new ArrayList<>();
    }

    public void readTimes(InputStream inputStream) throws IOException, UnsupportedFormatException, InvalidTimeException {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) continue;
            String[] words = line.split("\\s+");
            for (String word : words) {
                String[] timeParts;
                if (word.contains(":")) {
                    timeParts = word.split(":");
                } else if (word.contains(".")) {
                    timeParts = word.split("\\.");
                } else {
                    throw new UnsupportedFormatException(word);
                }
                int h = parseInt(timeParts[0]);
                int m = parseInt(timeParts[1]);

                if (h < 0 || h > 23 || m < 0 || m > 59) {
                    throw new InvalidTimeException(word);
                }

                times.add(new Time(h, m));
            }
        }
    }

    public void writeTimes(OutputStream outputStream, TimeFormatter formatter) {
        PrintStream ps = new PrintStream(outputStream);
        times.sort(Comparator.comparingInt(Time::getHours).thenComparing(Time::getMinutes));

        times.forEach(time -> ps.println(formatter.format(time)));
    }

}

public class TimesTest {

    public static void main(String[] args) throws IOException {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, new Format24());
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, new FormatAMPM());
    }

}