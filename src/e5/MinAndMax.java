package e5;

import java.util.Scanner;

class MinMax<T extends Comparable<T>>{
    private T min;
    private T max;
    private int proccessedCount;

    public MinMax(){
        min = null;
        max = null;
        proccessedCount = 0;
    }

    public void update(T element) {
        if (min == null) {
            min = element;
            max = element;
        } else {
            if (element.compareTo(min) < 0) {
                min = element;
            } else if (element.compareTo(max) > 0) {
                max = element;
            } else if (element.compareTo(min) > 0 && element.compareTo(max) < 0) {
                proccessedCount++;
            }
        }
    }

    public T max(){
        return max;
    }

    public T min(){
        return min;
    }

    @Override
    public String toString() {
        return min + " " + max + " " + proccessedCount;
    }

}
public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}