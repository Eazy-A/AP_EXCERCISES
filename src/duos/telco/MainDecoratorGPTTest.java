package duos.telco;


public class MainDecoratorGPTTest {

    public static void main(String[] args) {

        PriceCalculator calculator =
                new TaxDecorator(
                        new PercentageDiscountDecorator(
                                new FixedDiscountDecorator(
                                        new BasePriceCalculator(100.0), // base price
                                        10.0                              // $10 coupon
                                ),
                                0.20                                  // 20% discount
                        ),
                        0.18                                      // 18% VAT
                );

        System.out.println("Final price: " + calculator.calculate());

        System.out.println("\n---- Another scenario ----");

        PriceCalculator noDiscount =
                new TaxDecorator(
                        new BasePriceCalculator(50.0),
                        0.10
                );

        System.out.println("Final price: " + noDiscount.calculate());
    }

    interface PriceCalculator {
        double calculate();
    }

    static class BasePriceCalculator implements PriceCalculator {
        private final double price;

        public BasePriceCalculator(double price) {
            this.price = price;
        }

        @Override
        public double calculate() {
            return price;
        }
    }

    static abstract class BaseDecorator implements PriceCalculator {
        private final PriceCalculator priceCalculator;

        public BaseDecorator(PriceCalculator priceCalculator) {
            this.priceCalculator = priceCalculator;
        }

        @Override
        public double calculate() {
            return priceCalculator.calculate();
        }
    }

    static class TaxDecorator extends BaseDecorator {
        private final double taxRate;

        public TaxDecorator(PriceCalculator priceCalculator, double taxRate) {
            super(priceCalculator);
            this.taxRate = taxRate;
        }

        @Override
        public double calculate() {
            double price = super.calculate();
            return price + price * taxRate;
        }
    }

    static class PercentageDiscountDecorator extends BaseDecorator {
        private final double percent;
        public PercentageDiscountDecorator(PriceCalculator priceCalculator, double percent) {
            super(priceCalculator);
            this.percent = percent;
        }

        @Override
        public double calculate() {
            double price = super.calculate();

            return price - price * percent;
        }
    }

    static class FixedDiscountDecorator extends BaseDecorator {
        private final double amount;
        public FixedDiscountDecorator(PriceCalculator priceCalculator, double amount) {
            super(priceCalculator);
            this.amount = amount;
        }

        @Override
        public double calculate() {
            return super.calculate() - amount;
        }
    }
}