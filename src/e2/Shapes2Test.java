package e2;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


enum ShapeType{
    CIRCLE,
    SQUARE
}
class InvalidCanvasException extends Exception {
    // Canvas [canvas_id] has a shape with area larger than [max_area].
    public InvalidCanvasException(String id, double maxArea) {
        super(String.format("Canvas %s has a shape with area larger than %.2f", id, maxArea));
    }
}

interface Shape {
    public double area();
    ShapeType getType();
}

class Circle implements Shape {
    private final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public ShapeType getType() {
        return ShapeType.CIRCLE;
    }

    public double getRadius() {
        return radius;
    }

}

class Square implements Shape {
    private final double side;

    public Square(double side) {
        this.side = side;
    }

    @Override
    public double area() {
        return side * side;
    }

    @Override
    public ShapeType getType() {
        return ShapeType.SQUARE;
    }

    public double getSide() {
        return side;
    }
}

class Canvas {
    private String id;
    List<Shape> shapes;

    public Canvas(String id, List<Shape> shapes) {
        this.id = id;
        this.shapes = shapes;
    }

    public String getId() {
        return id;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public long numberOfSquares() {
        return shapes.stream()
                .filter(shape -> shape.getType().equals(ShapeType.SQUARE))
                .count();
    }

    public long numberOfCircles() {
        return shapes.stream()
                .filter(shape -> shape.getType().equals(ShapeType.CIRCLE))
                .count();
    }

    public double minArea() {
        return shapes.stream()
                .mapToDouble(Shape::area)
                .min()
                .orElse(0);
    }

    public double maxArea() {
        return shapes.stream()
                .mapToDouble(Shape::area)
                .max()
                .orElse(0);
    }

    public double averageArea() {
        return shapes.stream()
                .mapToDouble(Shape::area)
                .average()
                .orElse(0);
    }
    public double totalArea(){
        return shapes.stream()
                .mapToDouble(Shape::area)
                .sum();
    }
}

class ShapesApplication {
    private final double maxArea;
    List<Canvas> canvasList;

    public ShapesApplication(double maxArea) {
        this.maxArea = maxArea;
        canvasList = new ArrayList<>();
    }

    public void readCanvases(InputStream inputStream) throws InvalidCanvasException {
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] words = line.split("\\s+");
            String id = words[0];

            List<Shape> shapeList = new ArrayList<>();
            try {
                for (int i = 1; i < words.length; i += 2) {
                    String type = words[i];
                    int size = Integer.parseInt(words[i + 1]);
                    Shape shape = type.equals("C") ? new Circle(size) : new Square(size);
                    if(shape.area() > maxArea){
                        throw new InvalidCanvasException(id, maxArea);
                    }
                    shapeList.add(shape);
                }
                canvasList.add(new Canvas(id, shapeList));
            }catch (InvalidCanvasException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void printCanvases(OutputStream os) {
        PrintStream printStream = new PrintStream(os);

        List<Canvas> sortedCanvases = canvasList.stream()
                .sorted(Comparator.comparing(Canvas::totalArea).reversed())
                        .collect(Collectors.toList());

        //  ID total_shapes total_circles total_squares min_area max_area average_area
        sortedCanvases.forEach((canvas -> {
            printStream.printf("%s %d %d %d %.2f %.2f %.2f \n",
                    canvas.getId(), canvas.getShapes().size(),canvas.numberOfCircles(),
                    canvas.numberOfSquares(), canvas.minArea(), canvas.maxArea(), canvas.averageArea());
        }));
    }

}

public class Shapes2Test {

    public static void main(String[] args) throws InvalidCanvasException {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
            shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);

    }
}