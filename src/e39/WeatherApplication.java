package e39;

import java.util.*;


class WeatherDispatcher {

    Set<Display> displays = new LinkedHashSet<>();

    public void register(Display display) {
        displays.add(display);
    }

    public void remove(Display display) {
        displays.remove(display);
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        for (Display display : displays) {
            display.update(temperature, humidity, pressure);
        }
        System.out.println();
    }


}

interface Display {
    void display();

    void update(float temperature, float humidity, float pressure);
}

class CurrentConditionsDisplay implements Display {
    private float temperature;
    private float humidity;

    public CurrentConditionsDisplay(WeatherDispatcher weatherDispatcher) {
        weatherDispatcher.register(this);
    }


    @Override
    public void display() {
        System.out.printf("Temperature: %.1fF\nHumidity: %.1f%%\n", temperature, humidity);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        display();
    }
}

class ForecastDisplay implements Display{
    private float pressure;
    private float prevPressure;
    boolean firstUpdate = true;

    public ForecastDisplay(WeatherDispatcher weatherDispatcher) {
        weatherDispatcher.register(this);
    }

    @Override
    public void display() {
        System.out.print("Forecast: ");
        if (pressure > prevPressure) {
            System.out.println("Improving");
        } else if (pressure == prevPressure) {
            System.out.println("Same");
        } else if (pressure < prevPressure) {
            System.out.println("Cooler");
        }
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        this.prevPressure = this.pressure;
        this.pressure = pressure;
        if (firstUpdate) {
            firstUpdate = false;
        }
        display();
    }
}


public class WeatherApplication {

    public static void main(String[] args) {
        WeatherDispatcher weatherDispatcher = new WeatherDispatcher();

        CurrentConditionsDisplay currentConditions = new CurrentConditionsDisplay(weatherDispatcher);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherDispatcher);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            weatherDispatcher.setMeasurements(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
            if (parts.length > 3) {
                int operation = Integer.parseInt(parts[3]);
                if (operation == 1) {
                    weatherDispatcher.remove(forecastDisplay);
                }
                if (operation == 2) {
                    weatherDispatcher.remove(currentConditions);
                }
                if (operation == 3) {
                    weatherDispatcher.register(forecastDisplay);
                }
                if (operation == 4) {
                    weatherDispatcher.register(currentConditions);
                }

            }
        }
    }
}