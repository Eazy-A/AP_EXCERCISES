package ispit_juni_2023.delivery_app;

import java.util.*;

class DeliveryPerson {
    private final String id, name;
    private Location currentLocation;
    private final DoubleSummaryStatistics deliveries = new DoubleSummaryStatistics();

    public DeliveryPerson(String id, String name, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void addEarning(double earning) {
        deliveries.accept(earning);
    }

    public double getEarnings() {
        return deliveries.getSum();
    }
    public long getNumDeliveries(){
        return deliveries.getCount();
    }
    public int distanceToLocation(Location location){
        return currentLocation.distance(location);
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",
                id, name, deliveries.getCount(), deliveries.getSum(),deliveries.getAverage());
    }
}

class Restaurant {
    private final String id, name;
    private final Location location;
    private final DoubleSummaryStatistics orders = new DoubleSummaryStatistics();

    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void addOrder(double order) {
        orders.accept(order);
    }

    public double averageOrderPrice() {
        return orders.getAverage();
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",
                id, name, orders.getCount(), orders.getSum(),orders.getAverage());
    }

    public String getId() {
        return id;
    }
}

class User {
    private final String id, name;
    private final Map<String, Location> addresses = new LinkedHashMap<>();
    private final DoubleSummaryStatistics orders = new DoubleSummaryStatistics();

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addAddress(String addressName, Location location) {
        addresses.putIfAbsent(addressName, location);
    }

    public Map<String, Location> getAddresses() {
        return addresses;
    }

    public void addSpent(double spent) {
        orders.accept(spent);

    }

    public double getSpent() {
        return orders.getSum();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",
                id, name, orders.getCount(), orders.getSum(), orders.getAverage());
    }
}

class DeliveryApp {
    private final String name;
    private final Map<String, DeliveryPerson> deliveryPeople = new LinkedHashMap<>();
    private final Map<String, Restaurant> restaurants = new LinkedHashMap<>();
    private final Map<String, User> users = new TreeMap<>();

    public DeliveryApp(String name) {
        this.name = name;
    }

    public void registerDeliveryPerson(String id, String name, Location currentLocation) {
        deliveryPeople.putIfAbsent(id, new DeliveryPerson(id, name, currentLocation));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurants.putIfAbsent(id, new Restaurant(id, name, location));
    }

    public void addUser(String id, String name) {
        users.putIfAbsent(id, new User(id, name));
    }

    public void addAddress(String id, String addressName, Location location) {
        users.get(id).addAddress(addressName, location);
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        User user = users.get(userId);
        Restaurant restaurant = restaurants.get(restaurantId);

        DeliveryPerson deliveryPerson = deliveryPeople.values().stream()
                .min(Comparator.<DeliveryPerson>comparingInt(d -> d.distanceToLocation(restaurant.getLocation()))
                        .thenComparingLong(DeliveryPerson::getNumDeliveries)
                        .thenComparing(DeliveryPerson::getId))
                .orElseThrow();


        double deliveryDistance = restaurant.getLocation().distance(user.getAddresses().get(userAddressName));

        double totalPay = 90 + 10 * Math.floor(deliveryDistance / 10);
        user.addSpent(cost);
        restaurant.addOrder(cost);
        deliveryPerson.addEarning(totalPay);

        deliveryPerson.setCurrentLocation(user.getAddresses().get(userAddressName));
    }

    public void printUsers() {
        users.values().stream()
                .sorted(Comparator.comparing(User::getSpent).thenComparing(User::getId).reversed())
                .forEach(System.out::println);
    }

    public void printRestaurants() {
        restaurants.values().stream()
                .sorted(Comparator.comparingDouble(Restaurant::averageOrderPrice).thenComparing(Restaurant::getId).reversed())
                .forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliveryPeople.values().stream()
                .sorted(Comparator.comparingDouble(DeliveryPerson::getEarnings).thenComparing(DeliveryPerson::getId).reversed())
                .forEach(System.out::println);
    }
}

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}
