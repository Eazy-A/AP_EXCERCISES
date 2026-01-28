package e30;

import java.io.*;
import java.util.*;

class InvalidIDException extends Exception {
    public InvalidIDException(String id) {
        super(String.format("ID %s is not valid", id));
    }
}

class InvalidDimensionException extends Exception {
    public InvalidDimensionException() {
        super("Dimension 0 is not allowed!");
    }
}

interface Shape {
    double getPerimeter();

    double getArea();

    void scale(double coefficient);
}

class Rectangle implements Shape {
    private double length;
    private double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public double getPerimeter() {
        return length * 2 + width * 2;
    }

    @Override
    public double getArea() {
        return length * width;
    }

    @Override
    public void scale(double coefficient) {
        length = length * coefficient;
        width = width * coefficient;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f", length, width, getArea(), getPerimeter());
    }
}

class Circle implements Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double getPerimeter() {
        return Math.PI * 2 * radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public void scale(double coefficient) {
        radius = radius * coefficient;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f", radius, getArea(), getPerimeter());
    }
}

class Square implements Shape {
    private double side;

    public Square(double side) {
        this.side = side;
    }

    @Override
    public double getPerimeter() {
        return side * 4;
    }

    @Override
    public double getArea() {
        return side * side;
    }

    @Override
    public void scale(double coefficient) {
        side = side * coefficient;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f", side, getArea(), getPerimeter());
    }
}

class User {
    private final String id;
    private final Set<Shape> shapes = new TreeSet<>(Comparator.comparingDouble(Shape::getPerimeter)
            .thenComparing(Shape::getArea));

    public User(String id) {
        this.id = id;
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public String getId() {
        return id;
    }

    public void scaleShapes(double coefficient) {
        shapes.forEach(shape -> shape.scale(coefficient));
    }

    public int getShapesNum() {
        return shapes.size();
    }

    public double areaSum() {
        return shapes.stream()
                .mapToDouble(Shape::getArea)
                .sum();
    }

    public Set<Shape> getShapes() {
        return shapes;
    }

}

class Canvas {
    private final Map<String, User> userMap = new LinkedHashMap<>();
    private final Set<Shape> allShapes = new TreeSet<>(Comparator.comparing(Shape::getArea));

    public Canvas() {
    }

    public void readShapes(InputStream is) throws InvalidDimensionException {
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()) {
            try {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");
                String num = parts[0];
                Shape shape = ShapesFactory.create(parts, num);
                String id = parts[1];
                if (id.length() != 6 || !id.matches("\\p{Alnum}+")) throw new InvalidIDException(id);
                userMap.computeIfAbsent(id, k -> new User(id));
                userMap.get(id).addShape(shape);
                allShapes.add(shape);
            } catch (InvalidIDException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void scaleShapes(String userID, double coefficient) {
        User user = userMap.get(userID);
        if (user != null) {
            user.scaleShapes(coefficient);
        }
    }

    public void printAllShapes(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        allShapes.forEach(pw::println);
        pw.flush();

    }

    public void printByUserId(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        userMap.values().stream()
                .sorted(Comparator.comparing(User::getShapesNum).reversed().thenComparing(User::areaSum))
                .forEach(user -> {
                    pw.println("Shapes of user: " + user.getId());
                    user.getShapes().stream()
                            .sorted(Comparator.comparingDouble(Shape::getPerimeter))
                            .forEach(pw::println);
                });
        pw.flush();
    }

    public void statistics(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        DoubleSummaryStatistics stats = userMap.values().stream().flatMap(user -> user.getShapes().stream())
                .mapToDouble(Shape::getArea)
                .summaryStatistics();
        pw.printf("count: %d\n", stats.getCount());
        pw.printf("sum: %.2f\n", stats.getSum());
        pw.printf("min: %.2f\n", stats.getMin());
        pw.printf("average: %.2f\n", stats.getAverage());
        pw.printf("max: %.2f\n", stats.getMax());
        pw.flush();
    }
}

class ShapesFactory {
    public static Shape create(String[] parts, String num) throws InvalidDimensionException {
        switch (num) {
            case "1":
                double radius = Double.parseDouble(parts[2]);
                if (radius == 0) throw new InvalidDimensionException();
                return new Circle(radius);
            case "2":
                double side = Double.parseDouble(parts[2]);
                if (side == 0) throw new InvalidDimensionException();
                return new Square(side);
            case "3":
                double length = Double.parseDouble(parts[2]);
                double width = Double.parseDouble(parts[3]);
                if (length == 0 || width == 0) throw new InvalidDimensionException();
                return new Rectangle(length, width);
            default:
                throw new IllegalArgumentException("Invalid shape type: " + num);
        }
    }
}

public class CanvasTest {

    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        try {
            canvas.readShapes(System.in);
        } catch (InvalidDimensionException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}