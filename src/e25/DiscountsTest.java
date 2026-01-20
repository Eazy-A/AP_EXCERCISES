package e25;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

class Product {
    private double discountPrice;
    private double price;

    public Product(double discountPrice, double price) {
        this.discountPrice = discountPrice;
        this.price = price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public double getPrice() {
        return price;
    }
    public double absoluteDiscount() {
        return price - discountPrice;
    }

    public double discountPercentage() {
        double percent = price / 100.0;
        return 100 - discountPrice / percent;

    }

    @Override
    public String toString() {
        return String.format("%.0f%% %.0f/%.0f", Math.floor(discountPercentage()), discountPrice, price);
    }
}

class Store {
    private String name;
    private Set<Product> prices;

    public Store(String name, Set<Product> prices) {
        this.name = name;
        this.prices = prices;
    }

    public double totalPrice() {
        return prices.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    public double totalDiscountPrice() {
        return prices.stream()
                .mapToDouble(Product::getDiscountPrice)
                .sum();
    }

    public double averageDiscount() {
        return prices.stream()
                .mapToDouble(product -> {
                    double discount = product.getDiscountPrice();
                    double price = product.getPrice();
                    return price - discount;
                })
                .average()
                .orElse(0);
    }
    public double averageDiscountPercentage(){
        return prices.stream()
                .mapToDouble(product -> Math.floor(product.discountPercentage()))
                .average()
                .orElse(0);
    }

    public double totalDiscount() {
        return totalPrice() - totalDiscountPrice();
    }

    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s\nAverage discount: %.1f%%\nTotal discount: %.0f\n", name, averageDiscountPercentage(), totalDiscount()));
        prices.forEach(price -> sb.append(price).append("\n"));
        sb.setLength(sb.length()-1);
        return sb.toString();
    }
}

class Discounts {
    private List<Store> stores = new ArrayList<>();

    public int readStores(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] spaceParts = line.split("\\s+");
            String name = spaceParts[0];
            Set<Product> pricesList = new TreeSet<>(Comparator.comparing(Product::discountPercentage).reversed());
            for (int i = 1; i < spaceParts.length; i++) {
                String combinedPricesString = spaceParts[i];

                String[] dotsParts = combinedPricesString.split(":");

                double priceOnDiscount = Double.parseDouble(dotsParts[0]);
                double price = Double.parseDouble(dotsParts[1]);
                pricesList.add(new Product(priceOnDiscount, price));
            }
            stores.add(new Store(name, pricesList));
        }

        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        return stores.stream()
                .sorted(Comparator.comparing(Store::averageDiscountPercentage).reversed().thenComparing(Store::getName))
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return stores.stream()
                .sorted(Comparator.comparing(Store::totalDiscount).thenComparing(Store::getName))
                .limit(3)
                .collect(Collectors.toList());
    }
}


public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}
