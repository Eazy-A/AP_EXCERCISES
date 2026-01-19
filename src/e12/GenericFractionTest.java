package e12;

import java.util.Scanner;

class ZeroDenominatorException extends Exception {
    public ZeroDenominatorException(String message) {
        super(message);
    }
}

class GenericFraction<T extends Number, U extends Number> {
    private T numerator;
    private U denominator;

    public GenericFraction(T numerator, U denominator) throws ZeroDenominatorException {
        this.numerator = numerator;
        if (!denominator.equals(0)) {
            this.denominator = denominator;
        } else throw new ZeroDenominatorException("Denominator cannot be zero");
    }

    private double findGCD(double n1, double n2) {
        n1 = Math.abs(n1);
        n2 = Math.abs(n2);
        while (n2 != 0) {
            double temp = n2;
            n2 = n1 % n2;
            n1 = temp;
        }
        return n1;
    }

    // adding two fractions
    public GenericFraction<Double, Double> add(GenericFraction<? extends Number, ? extends Number> gf) throws ZeroDenominatorException {
        double a = this.numerator.doubleValue();
        double b = this.denominator.doubleValue();

        double c = gf.getNumerator().doubleValue();
        double d = gf.getDenominator().doubleValue();

        double newNumerator = (a * d) + (b * c);
        double newDenominator = b * d;

        double common = findGCD(newNumerator, newDenominator);

        return new GenericFraction<>(newNumerator / common, newDenominator / common);
    }

    // fraction as real number
    public double toDouble() {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    @Override
    public String toString() {
        // [numerator] / [denominator]
        return String.format("%.2f / %.2f", numerator.doubleValue(), denominator.doubleValue());
    }

    public T getNumerator() {
        return numerator;
    }

    public U getDenominator() {
        return denominator;
    }
}

public class GenericFractionTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double n1 = scanner.nextDouble();
        double d1 = scanner.nextDouble();
        float n2 = scanner.nextFloat();
        float d2 = scanner.nextFloat();
        int n3 = scanner.nextInt();
        int d3 = scanner.nextInt();
        try {
            GenericFraction<Double, Double> gfDouble = new GenericFraction<Double, Double>(n1, d1);
            GenericFraction<Float, Float> gfFloat = new GenericFraction<Float, Float>(n2, d2);
            GenericFraction<Integer, Integer> gfInt = new GenericFraction<Integer, Integer>(n3, d3);
            System.out.printf("%.2f\n", gfDouble.toDouble());
            System.out.println(gfDouble.add(gfFloat));
            System.out.println(gfInt.add(gfFloat));
            System.out.println(gfDouble.add(gfInt));
            gfInt = new GenericFraction<Integer, Integer>(n3, 0);
        } catch (ZeroDenominatorException e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }

}


