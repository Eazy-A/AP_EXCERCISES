package e38;

import java.util.*;
import java.util.stream.Collectors;

class BlockContainer<T extends Comparable<T>> {
    private final List<Set<T>> blocks;
    private final int maxBlockSize;

    public BlockContainer(int n) {
        blocks = new ArrayList<>();
        maxBlockSize = n;
    }

    public void add(T a) {
        if (blocks.isEmpty() || blocks.getLast().size() == maxBlockSize) { //getLast() doesn't work in moodle coderunner, use get(blocks.size - 1) instead
            blocks.add(new TreeSet<>());
        }
        blocks.getLast().add(a);

    }

    public boolean remove(T a) {
        for (Set<T> block : blocks) {
            if (block.remove(a)) {
                if (block.isEmpty()) blocks.remove(block);
                return true;
            }
        }
        return false;
    }

    public void sort() {
        List<T> allSorted = blocks.stream()
                .flatMap(Collection::stream)
                .sorted()
                .toList(); // to  list doesn't work in moodle coderunner

        blocks.clear();

        allSorted.forEach(this::add);
    }

    @Override
    public String toString() {
        return blocks.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }
}

public class BlockContainerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int size = scanner.nextInt();
        BlockContainer<Integer> integerBC = new BlockContainer<Integer>(size);
        scanner.nextLine();
        Integer lastInteger = null;
        for (int i = 0; i < n; ++i) {
            int element = scanner.nextInt();
            lastInteger = element;
            integerBC.add(element);
        }
        System.out.println("+++++ Integer Block Container +++++");
        System.out.println(integerBC);
        System.out.println("+++++ Removing element +++++");
        integerBC.remove(lastInteger);
        System.out.println("+++++ Sorting container +++++");
        integerBC.sort();
        System.out.println(integerBC);
        BlockContainer<String> stringBC = new BlockContainer<String>(size);
        String lastString = null;
        for (int i = 0; i < n; ++i) {
            String element = scanner.next();
            lastString = element;
            stringBC.add(element);
        }
        System.out.println("+++++ String Block Container +++++");
        System.out.println(stringBC);
        System.out.println("+++++ Removing element +++++");
        stringBC.remove(lastString);
        System.out.println("+++++ Sorting container +++++");
        stringBC.sort();
        System.out.println(stringBC);
    }
}



