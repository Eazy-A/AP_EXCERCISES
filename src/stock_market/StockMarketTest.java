package stock_market;

import java.util.*;


interface Investor {
    void signal(String ticker, double oldPrice, double newPrice);
}

class StockTracker {
    public Map<String, List<Investor>> subscriptions = new HashMap<>();

    public void subscribe(String ticker, Investor investor) {
        subscriptions.computeIfAbsent(ticker, k -> new ArrayList<>()).add(investor);
    }

    public void onPriceChange(String ticker, double oldPrice, double newPrice) {
        double change = Math.abs(newPrice - oldPrice) / oldPrice;

        if (change > 0.05) {
            List<Investor> investors = subscriptions.get(ticker);
            if (investors != null) {
                for (Investor investor : investors) {
                    investor.signal(ticker, oldPrice, newPrice);
                }
            }
        }
    }
}

class RetailInvestor implements Investor {
    private String name;

    public RetailInvestor(String name) {
        this.name = name;
    }

    @Override
    public void signal(String ticker, double oldPrice, double newPrice) {
        System.out.printf("Investor %s notified: %s changed from %.2f to %.2f\n", name, ticker, oldPrice, newPrice);
    }
}

class Stock {
    private String ticker;
    private double currentPrice;
    private StockTracker tracker;

    public Stock(String ticker, double currentPrice, StockTracker tracker) {
        this.ticker = ticker;
        this.currentPrice = currentPrice;
        this.tracker = tracker;
    }

    public void updatePrice(double newPrice) {
        double oldPrice = this.currentPrice;
        this.currentPrice = newPrice;

        tracker.onPriceChange(ticker, oldPrice, newPrice);
    }
}


public class StockMarketTest {
    public static void main(String[] args) {
        StockTracker tracker = new StockTracker();

        // Create Investors
        RetailInvestor investor1 = new RetailInvestor("Alice");
        RetailInvestor investor2 = new RetailInvestor("Bob");
        RetailInvestor investor3 = new RetailInvestor("Charlie");

        // Alice follows Apple and Google
        tracker.subscribe("AAPL", investor1);
        tracker.subscribe("GOOGL", investor1);

        // Bob follows Apple only
        tracker.subscribe("AAPL", investor2);

        // Charlie follows Google only
        tracker.subscribe("GOOGL", investor3);

        // Create Stocks
        Stock apple = new Stock("AAPL", 150.0, tracker);
        Stock google = new Stock("GOOGL", 2800.0, tracker);

        System.out.println("--- Scenario 1: Small change (Less than 5%) ---");
        // 150 -> 153 is only a 2% change. No one should be notified.
        apple.updatePrice(153.0);

        System.out.println("\n--- Scenario 2: Large change (More than 5%) ---");
        // 153 -> 170 is ~11% change. Alice and Bob should be notified.
        apple.updatePrice(170.0);

        System.out.println("\n--- Scenario 3: Significant drop in Google ---");
        // 2800 -> 2500 is ~10.7% drop. Alice and Charlie should be notified.
        google.updatePrice(2500.0);

        System.out.println("\n--- Scenario 4: Google stays steady ---");
        // 2500 -> 2510 is < 1% change. No notifications.
        google.updatePrice(2510.0);
    }
}