package e17;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

class AmountNotAllowedException extends Exception {
    public AmountNotAllowedException(double amount) {
        super(String.format("Receipt with amount %.0f is not allowed to be scanned", amount));
    }
}

interface Tax {
    double taxValue();
}

class TaxA implements Tax {

    @Override
    public double taxValue() {
        return 0.18;
    }
}

class TaxB implements Tax {
    @Override
    public double taxValue() {
        return 0.05;
    }
}

class TaxV implements Tax {

    @Override
    public double taxValue() {
        return 0;
    }
}

class TaxFactory {
    public static Tax getTax(String type) {
        switch (type) {
            case "A":
                return new TaxA();
            case "B":
                return new TaxB();
            case "V":
                return new TaxV();
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}

class Item {
    private final int price;
    private final Tax taxType;

    public Item(Tax taxType, int price) {
        this.taxType = taxType;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public Tax getTaxType() {
        return taxType;
    }
}

class Receipt {
    private final String id;
    private final List<Item> itemList = new ArrayList<>();

    public Receipt(String id) {
        this.id = id;
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public int sumAmounts() {
        return itemList.stream()
                .mapToInt(Item::getPrice)
                .sum();
    }

    public double taxReturn() {
        double totalReturn = 0;
        for (Item item : itemList) {
            totalReturn += item.getTaxType().taxValue() * item.getPrice();
        }
        return totalReturn * 0.15;
    }

    @Override
    public String toString() {
        return String.format("%10s\t%10d\t%10.5f", this.id, sumAmounts(), taxReturn());
    }
}

class MojDDV {
    private final List<Receipt> receiptList = new ArrayList<>();
    public void readRecords(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            try {
                String[] words = line.split("\\s+");
                String id = words[0];
                Receipt receipt = new Receipt(id);
                for (int i = 1; i < words.length; i += 2) {
                    int price = Integer.parseInt(words[i]);
                    String taxType = words[i + 1];
                    receipt.addItem(new Item(TaxFactory.getTax(taxType), price));
                }
                if (receipt.sumAmounts() > 30000) throw new AmountNotAllowedException(receipt.sumAmounts());
                receiptList.add(receipt);
            } catch (AmountNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void printTaxReturns(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);

        receiptList.forEach(pw::println);

        pw.flush();

    }
    private DoubleSummaryStatistics statistics(){
        return receiptList.stream()
                .mapToDouble(Receipt::taxReturn)
                .summaryStatistics();
    }
    public void printStatistics(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        pw.printf("min:\t%05.03f\nmax:\t%05.03f\nsum:\t%05.03f\ncount:\t%-5d\navg:\t%05.03f"
                , statistics().getMin(), statistics().getMax(), statistics().getSum(),statistics().getCount(), statistics().getAverage());
        pw.flush();
    }

}

public class MojDDVTest {
    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);

    }
}
