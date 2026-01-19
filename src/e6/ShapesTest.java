package e6;

import javax.lang.model.element.TypeElement;
import java.util.*;

enum Color {
    RED, GREEN, BLUE
}

enum Type {
    CIRCLE, RECTANGLE
}

interface Scalable {
    void scale(float scaleFactor);
}

interface Stackable {
    float weight();

    Type getType();
}

abstract class Shape implements Stackable, Scalable {
    private String id;
    private Color color;

    public Shape(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }
}

class Circle extends Shape {
    private float radius;

        public Circle(String id, Color color, float radius) {
        super(id, color);
        this.radius = radius;
    }

    @Override
    public void scale(float scaleFactor) {
        radius = radius * scaleFactor;
    }

    @Override
    public float weight() {
        return (float) (radius * radius * Math.PI);
    }

    @Override
    public Type getType() {
        return Type.CIRCLE;
    }

    // C: [id:5 места од лево] [color:10 места од десно] [weight:10.2 места од десно]
    @Override
    public String toString() {
        return String.format("C: %-5s %10s %10.2f", getId(), getColor(), weight());
    }

    public float getRadius() {
        return radius;
    }
}

class Rectangle extends Shape {
    private float width;
    private float height;

    public Rectangle(String id, Color color, float width, float height) {
        super(id, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public void scale(float scaleFactor) {
        width = width * scaleFactor;
        height = height * scaleFactor;
    }

    @Override
    public float weight() {
        return width * height;
    }

    @Override
    public Type getType() {
        return Type.RECTANGLE;
    }


    // R: [id:5 места од лево] [color:10 места од десно] [weight:10.2 места од десно]
    @Override
    public String toString() {
        return String.format("R: %-5s %10s %10.2f", getId(), getColor(), weight());
    }
}

class Canvas {
    private List<Shape> shapes = new ArrayList<>();

    public void insertSorted(Shape newShape){
        int index = 0;

        while (index < shapes.size() && shapes.get(index).weight() >= newShape.weight()){
            index ++;
        }
        shapes.add(index, newShape);
    }
    // adding a circle
    public void add(String id, Color color, float radius) {
        insertSorted(new Circle(id, color, radius));
    }

    // adding a rectangle
    public void add(String id, Color color, float width, float height) {
        insertSorted(new Rectangle(id, color, width, height));
    }

    public void scale(String id, float scaleFactor) {
        Shape shape = shapes.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow();
        shapes.remove(shape);
        shape.scale(scaleFactor);
        insertSorted(shape);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        shapes.forEach(shape -> {
            sb.append(shape);
            sb.append("\n");
        });

        return sb.toString();
    }

}

public class ShapesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println("ORIGNAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }
}
