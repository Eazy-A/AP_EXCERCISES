package ispit_juni_2023.delivery_app;

import java.util.*;


class DeliveryPerson {
    private String id;
    private String name;
    private Location currentLocation;
    private int totalDeliveries = 0;
    private float earning = 0;
    private float totalDeliveryFees = 0;

    public DeliveryPerson(String id, String name, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void addEarning(float cost) {
        earning += cost;
        totalDeliveries++;
    }

    public void addDeliveryFee(float fee) {
        totalDeliveryFees += fee;
    }

    public float averageDeliveryFee() {
        if (totalDeliveries == 0 || totalDeliveryFees == 0) return 0;
        return totalDeliveryFees / totalDeliveries;
    }

    public float getEarning() {
        return earning;
    }

    public int getTotalDeliveries() {
        return totalDeliveries;
    }

    @Override
    public String toString() {
//        ID: 2 Name: Riste Total deliveries: 1 Total delivery fee: 90.00 Average delivery fee: 90.00
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",
                id, name, totalDeliveries,totalDeliveryFees, averageDeliveryFee());
    }
}

class Delivery {
    private String name;
    private float cost;

    public Delivery(String name, float cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                '}';
    }
}

class Restaurant {
    private String id;
    private String name;
    private Location location;
    private List<Delivery> deliveryList = new ArrayList<>();

    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void addDelivery(Delivery delivery) {
        deliveryList.add(delivery);
    }

    public float averagePrice() {
        return (float) deliveryList.stream()
                .mapToDouble(Delivery::getCost)
                .average()
                .orElse(0);
    }

    public float totalEarned() {
        return (float) deliveryList.stream()
                .mapToDouble(Delivery::getCost)
                .sum();
    }

    @Override
    public String toString() {
//        ID: 1 Name: Morino Total orders: 1 Total amount earned: 450.00 Average amount earned: 450.00
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",
                id, name, deliveryList.size(), totalEarned(), averagePrice());
    }
}

class User {
    private String id;
    private String name;
    private List<Address> addressList = new ArrayList<>();
    private float spent = 0;
    private int totalOrders = 0;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void addAddress(String name, Location location) {
        addressList.add(new Address(name, location));
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void addSpent(float spent) {
        this.spent += spent;
        totalOrders++;
    }

    public float getSpent() {
        return spent;
    }

    public float averageSpent() {
        if(spent == 0 || totalOrders == 0) return 0;
        return spent / totalOrders;
    }

    @Override
    public String toString() {
//        ID: 1 Name: stefan Total orders: 1 Total amount spent: 450.00 Average amount spent: 450.00
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",
                id, name, totalOrders, spent, averageSpent());
    }
}

class DeliveryApp {
    private String name;
    private List<DeliveryPerson> deliveryPersonList = new ArrayList<>();
    private List<Restaurant> restaurantList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();

    public DeliveryApp(String name) {
        this.name = name;
    }

    public void registerDeliveryPerson(String id, String name, Location currentLocation) {
        deliveryPersonList.add(new DeliveryPerson(id, name, currentLocation));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurantList.add(new Restaurant(id, name, location));
    }

    public void addUser(String id, String name) {
        userList.add(new User(id, name));
    }

    public void addAddress(String id, String addressName, Location location) {
        userList.stream()
                .filter(user -> user.getId().equals(id))
                .limit(1)
                .forEach(user -> user.addAddress(addressName, location));
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        int min = Integer.MAX_VALUE;
        User recipientUser = userList.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow();

        Location userLocation = recipientUser.getAddressList()
                .stream()
                .filter(a -> a.getName().equals(userAddressName))
                .map(Address::getLocation)
                .findFirst()
                .orElseThrow();

        Restaurant recipientRestaurant = restaurantList.stream()
                .filter(restaurant -> restaurant.getId().equals(restaurantId))
                .findFirst()
                .orElseThrow();

        DeliveryPerson closestDeliveryPerson = null;

        Delivery delivery = new Delivery(" ", cost);
        recipientRestaurant.addDelivery(delivery);

        for (DeliveryPerson d : deliveryPersonList) {
            int distance = d.getCurrentLocation().distance(recipientRestaurant.getLocation());
            if (distance < min) {
                min = distance;
                closestDeliveryPerson = d;
            }
            else if(distance == min){
                assert closestDeliveryPerson != null;
                if (closestDeliveryPerson.getTotalDeliveries()> d.getTotalDeliveries()){
                    closestDeliveryPerson = d;
                }
            }
        }
        if (closestDeliveryPerson != null) {
            int distance = closestDeliveryPerson.getCurrentLocation().distance(recipientRestaurant.getLocation());
            closestDeliveryPerson.setCurrentLocation(userLocation);
            float deliveryFee = (distance / 10) * 10;
            float earning = 90 + deliveryFee;
            closestDeliveryPerson.addEarning(earning);
            closestDeliveryPerson.addDeliveryFee(earning);
        }
        recipientUser.addSpent(cost);
    }

    public void printUsers() {
        userList.stream()
                .sorted(Comparator.comparing(User::getSpent).reversed())
                .forEach(System.out::println);

    }

    public void printRestaurants() {
        restaurantList.stream()
                .sorted(Comparator.comparing(Restaurant::averagePrice).reversed())
                .forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliveryPersonList.stream()
                .sorted(Comparator.comparing(DeliveryPerson::getEarning).reversed())
                .forEach(System.out::println);
    }
}

class Address {
    private String name;
    private Location location;

    public Address(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
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

