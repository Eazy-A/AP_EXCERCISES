package e1;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;


class Canvas {
    private String id;
    private List<Integer> sizes;
    private int squaresCount;


    public Canvas(String id, List<Integer> sizes, int squaresCount) {
        this.id = id;
        this.sizes = sizes;
        this.squaresCount = squaresCount;
    }

    public int getPerimeter() {
        return (sizes.stream()
                .reduce(Integer::sum)
                .orElse(0)) * 4;
    }

    public String getId() {
        return id;
    }

    public List<Integer> getSizes() {
        return sizes;
    }

    public int getSquaresCount() {
        return squaresCount;
    }
}

class ShapesApplication {

    List<Canvas> canvasList = new ArrayList<>();

    public ShapesApplication() {
    }

    public int readCanvases(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        int counter = 0;

        while (scanner.hasNextLine()) {
            int squaresCount = 0;
            String line = scanner.nextLine();
            String[] words = line.split("\\s+");
            String id = words[0];
            List<Integer> sizes = new ArrayList<>();
            for (int i = 1; i < words.length; i++) {
                String size = words[i];
                sizes.add(Integer.valueOf(size));
                counter++;
                squaresCount++;
            }

            canvasList.add(new Canvas(id, sizes, squaresCount));
        }
        return counter;
    }

    public void printLargestCanvasTo(PrintStream out) {
        PrintStream printStream = new PrintStream(out);
        if(canvasList.isEmpty()) return;

        Canvas maxCanvas = canvasList.stream()
                .max(Comparator.comparingInt(Canvas::getPerimeter))
                .orElseThrow();

        printStream.println(maxCanvas.getId() + " " + maxCanvas.getSquaresCount() + " " + maxCanvas.getPerimeter());
    }

}

public class Shapes1Test {

    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}