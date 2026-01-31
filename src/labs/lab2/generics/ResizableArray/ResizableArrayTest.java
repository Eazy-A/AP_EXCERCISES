package labs.lab2.generics.ResizableArray;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

//class ArrayIndexOutOfBoundsException extends Exception {
//    public ArrayIndexOutOfBoundsException() {
//        super();
//    }
//
//    public ArrayIndexOutOfBoundsException(String message) {
//        System.out.println(message);
//    }
//}

class ResizableArray<T> {
    private T[] elements;
    private int size = 0;
    private static final int defaultInitialCapacity = 10;

    @SuppressWarnings("unchecked")
    public ResizableArray() {
        this.elements = (T[]) new Object[defaultInitialCapacity];
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public void addElement(T element) {
        if (size == elements.length) {
            T[] newArr = (T[]) new Object[elements.length * 2];
            System.arraycopy(elements, 0, newArr, 0, size);
            elements = newArr;
        }
        elements[size++] = element;
    }

    public boolean removeElement(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                for (int j = i; j < size - 1; j++) {
                    elements[j] = elements[j + 1];
                }
                elements[size - 1] = null;
                size--;
                return true;
            }
        }
        return false;
    }

    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Object[] toArray() {
        Object[] newArray = new Object[size];
        System.arraycopy(elements, 0, newArray, 0, size);
        return newArray;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int count() {
        return size;
    }

    public T elementAt(int index) throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= size)
            throw new ArrayIndexOutOfBoundsException("Array index out of bounds");
        return elements[index];
    }


    @SuppressWarnings("unchecked")
    public static <T> void copyAll(ResizableArray<? super T> dest, ResizableArray<? extends T> src) {
        int n = src.count();
        Object[] temp = src.toArray();
        for (int i = 0; i < n; i++) {
            dest.addElement((T) temp[i]);
        }
    }

    public T[] getElements() {
        return elements;
    }

    public int getSize() {
        return size;
    }
}

class IntegerArray extends ResizableArray<Integer> {
    public double sum() {
        double sum = 0;
        for (int i = 0; i < getSize(); i++) {
            try {
                sum += elementAt(i);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException();
            }
        }
        return sum;
    }

    public double mean() {
        return sum() / getSize();
    }

    int countNonZero() {
        int counter = 0;
        for (int i = 0; i < getSize(); i++) {
            try {
                if (!elementAt(i).equals(0)) {
                    counter++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException(e);
            }
        }
        return counter;
    }

    public IntegerArray distinct() {
        IntegerArray newArray = new IntegerArray();
        for (int i = 0; i < getSize(); i++) {
            try {
                Integer e = elementAt(i);
                if (!newArray.contains(e)) {
                    newArray.addElement(e);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new RuntimeException(ex);
            }
        }
        return newArray;
    }

    public IntegerArray increment(int offset) {
        IntegerArray newArray = new IntegerArray();
        for (int i = 0; i < getSize(); i++) {
            try {
                Integer e = elementAt(i);
                e += offset;
                newArray.addElement(e);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException();
            }
        }
        return newArray;
    }

    // dopolnitelno baranje labs
    public class ArrayTransformer<T, R> {
        public  ResizableArray<R> map(ResizableArray<T> source, Function<? super T,? extends R> mapper) {
            ResizableArray<R> out = new ResizableArray<>();
            for (int i = 0; i < source.getSize(); i++) {
                out.addElement(mapper.apply(source.elementAt(i)));
            }
            return out;
        }

        public static <T> ResizableArray <T> filter(ResizableArray<? extends T> source, Predicate<? super T> predicate){
            ResizableArray<T> out = new ResizableArray<>();
            for (int i = 0; i < source.getSize(); i++) {
                if(predicate.test(source.elementAt(i))){
                    out.addElement(source.elementAt(i));
                }
            }
            return out;
        }       
    }

}

public class ResizableArrayTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int test = jin.nextInt();
        if (test == 0) { //test ResizableArray on ints
            ResizableArray<Integer> a = new ResizableArray<Integer>();
            System.out.println(a.count());
            int first = jin.nextInt();
            a.addElement(first);
            System.out.println(a.count());
            int last = first;
            while (jin.hasNextInt()) {
                last = jin.nextInt();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
        }
        if (test == 1) { //test ResizableArray on strings
            ResizableArray<String> a = new ResizableArray<String>();
            System.out.println(a.count());
            String first = jin.next();
            a.addElement(first);
            System.out.println(a.count());
            String last = first;
            for (int i = 0; i < 4; ++i) {
                last = jin.next();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
            ResizableArray<String> b = new ResizableArray<String>();
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));

            System.out.println(a.removeElement(first));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
        }
        if (test == 2) { //test IntegerArray
            IntegerArray a = new IntegerArray();
            System.out.println(a.isEmpty());
            while (jin.hasNextInt()) {
                a.addElement(jin.nextInt());
            }
            jin.next();
            System.out.println(a.sum());
            System.out.println(a.mean());
            System.out.println(a.countNonZero());
            System.out.println(a.count());
            IntegerArray b = a.distinct();
            System.out.println(b.sum());
            IntegerArray c = a.increment(5);
            System.out.println(c.sum());
            if (a.sum() > 100)
                ResizableArray.copyAll(a, a);
            else
                ResizableArray.copyAll(a, b);
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.contains(jin.nextInt()));
            System.out.println(a.contains(jin.nextInt()));
        }
        if (test == 3) { //test insanely large arrays
            LinkedList<ResizableArray<Integer>> resizable_arrays = new LinkedList<ResizableArray<Integer>>();
            for (int w = 0; w < 500; ++w) {
                ResizableArray<Integer> a = new ResizableArray<Integer>();
                int k = 2000;
                int t = 1000;
                for (int i = 0; i < k; ++i) {
                    a.addElement(i);
                }

                a.removeElement(0);
                for (int i = 0; i < t; ++i) {
                    a.removeElement(k - i - 1);
                }
                resizable_arrays.add(a);
            }
            System.out.println("You implementation finished in less then 3 seconds, well done!");
        }
    }

}

