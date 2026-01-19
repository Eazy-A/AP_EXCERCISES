package e14;

import javax.swing.plaf.PanelUI;
import java.util.*;
import java.util.stream.Collectors;

class InvalidPositionException extends Exception{
    public InvalidPositionException(int position) {
        super("Invalid position " + position + ", alredy taken!" ); // hahahahhaha
    }
}
class Component {
    private String color;
    private int weight;
    private List<Component> componentList = new ArrayList<>();

    public Component(String color, int weight) {
        this.color = color;
        this.weight = weight;
    }

    public void addComponent(Component component){
        componentList.add(component);
        componentList = componentList.stream()
                .sorted(Comparator.comparingInt(Component::getWeight).thenComparing(Component::getColor))
                .collect(Collectors.toList());
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public void recursiveChangeColor(int weight, String color){
        if(this.weight < weight){
            this.color = color;
        }
        for (Component child : componentList){
            child.recursiveChangeColor(weight, color);
        }
    }
    public String printFormatter(int level){
        StringBuilder sb = new StringBuilder();
        String dashes = "---".repeat(level);

        sb.append(dashes).append(String.format("%d:%s\n", weight, color));
        for (Component child : componentList){
            sb.append(child.printFormatter(level+1));
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        return weight + ":" + color;
    }
}
class Window{
    private String name;
    private Map<Integer,Component> componentMap = new TreeMap<>();

    public Window(String name) {
        this.name = name;
    }

    public void addComponent(int position, Component component) throws InvalidPositionException {
        if (componentMap.containsKey(position)) throw new InvalidPositionException(position);
        componentMap.put(position, component);
    }

    public void switchComponents(int pos1, int pos2){
        Component fistComponent = componentMap.get(pos1);
        Component secondComponent = componentMap.get(pos2);

        componentMap.put(pos2, fistComponent);
        componentMap.put(pos1, secondComponent);
    }

    public void changeColor(int weight, String color) {
        for (Component component : componentMap.values()){
            component.recursiveChangeColor(weight, color);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("WINDOW ").append(name).append("\n");

        for (Map.Entry<Integer, Component> entry : componentMap.entrySet()){
            int position = entry.getKey();
            Component component = entry.getValue();
            sb.append(position).append(":");
            sb.append(component.printFormatter(0));
        }
        return sb.toString();
    }
}
public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if (what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.switchComponents(pos1, pos2);
        System.out.println(window);
    }
}
