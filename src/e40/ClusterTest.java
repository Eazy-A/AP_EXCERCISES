package e40;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * January 2016 Exam problem 2
 */
interface Clusterable<T>{
    long getId();
    double distance(T other);
}
class Cluster<T extends Clusterable<T>> {
    private final Map<Long, T> elements = new HashMap<>();

    public void addItem(T element) {
        elements.put(element.getId(), element);
    }

    public void near(long id, int top) {
        T target = elements.get(id);
        if (target == null) return;

        AtomicInteger count = new AtomicInteger(1);

        elements.values().stream()
                .filter(item -> item.getId() != id)
                .sorted(Comparator.comparingDouble(target::distance))
                .limit(top)
                .forEach(item ->
                        System.out.printf("%d. %d -> %.3f%n",
                                count.getAndIncrement(),
                                item.getId(),
                                target.distance(item)
                        ));
    }
}

class Point2D implements Clusterable<Point2D> {
    private long id;
    private float x;
    private float y;

    public Point2D(long id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public double distance(Point2D other) {
        return Math.sqrt(Math.pow((this.x-other.x),2) + Math.pow((this.y-other.y),2));
    }

}

public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }
}
