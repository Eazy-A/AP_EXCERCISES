package e17;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class AmountNotAllowedException extends Exception {
    public AmountNotAllowedException(double amount) {
//        Receipt with amount [сума на сите артикли] is not allowed to be scanned
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
    private double price;
    private Tax taxType;

    public Item(Tax taxType, double price) {
        this.taxType = taxType;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public Tax getTaxType() {
        return taxType;
    }
}

class Receipt {
    private String id;
    private List<Item> itemList = new ArrayList<>();

    public Receipt(String id) {
        this.id = id;
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public double sumAmounts() {
        return itemList.stream()
                .mapToDouble(Item::getPrice)
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
//        ID SUM_OF_AMOUNTS TAX_RETURN
        return String.format("%10s\t%10.0f\t%10.5f", id, sumAmounts(), taxReturn());
    }

    public List<Item> getItemList() {
        return itemList;
    }
}

class MojDDV {
    private List<Receipt> receiptList = new ArrayList<>();

    public void readRecords(InputStream inputStream) {
//        ID item_price1 item_tax_type1 item_price2 item_tax_type2 … item_price-n item_tax_type-n
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            try {
                String[] words = line.split("\\s+");
                String id = words[0];
                Receipt receipt = new Receipt(id);
                for (int i = 1; i < words.length; i += 2) {
                    double price = Double.parseDouble(words[i]);
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

    public void printStatistics(OutputStream outputStream) {
//         min: MIN max: MAX sum: SUM count: COUNT average: AVERAGE
        PrintWriter pw = new PrintWriter(outputStream);
        pw.printf("min:%9.3f\nmax:%10.3f\nsum:%10.3f\ncount:%2d\navg:%9.3f", min(), max(), sum(),count(), average());
        pw.flush();
    }

    public double min() {
        return receiptList.stream()
                .mapToDouble(Receipt::taxReturn)
                .min()
                .orElse(0);
    }

    public double max() {
        return receiptList.stream()
                .mapToDouble(Receipt::taxReturn)
                .max()
                .orElse(0);
    }

    public double sum() {
        return receiptList.stream()
                .mapToDouble(Receipt::taxReturn)
                .sum();
    }
    public int count(){
        return receiptList.size();
    }

    public double average() {
        return receiptList.stream()
                .mapToDouble(Receipt::taxReturn)
                .average()
                .orElse(0);
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
