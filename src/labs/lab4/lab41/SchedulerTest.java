package labs.lab4.lab41;

import java.util.*;
import java.util.stream.Collectors;

class Scheduler<T> {
    private Map<Date, T> items;
    public Scheduler(){
        items = new HashMap<>();
    }

    public void add(Date d, T t){
        items.put(d,t);
    }

    public boolean remove(Date d){
        T removed = items.remove(d);
        return removed != null;
    }

    public T next(){
        Date now = new Date();
        T next = null;
        long minDiff = Integer.MAX_VALUE;
        for (var date : items.entrySet()){
            long diff = date.getKey().getTime() - now.getTime();
            if (diff < minDiff && diff > 0){
                minDiff = diff;
                next = date.getValue();
            }
        }
        return next;
    }

    public T last(){
        Date now = new Date();
        T last = null;
        long minDiff = Integer.MAX_VALUE;
        for (var date : items.entrySet()){
            long diff = now.getTime() - date.getKey().getTime();
            if (diff < minDiff && diff > 0){
                minDiff = diff;
                last = date.getValue();
            }
        }
        return last;
    }

    public ArrayList<T> getAll(Date begin, Date end){
        return items.keySet().stream()
                .filter(d -> d.after(begin) && d.before(end))
                .map(d -> items.get(d))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public T getFirst(){
        return items.keySet().stream()
                .sorted()
                .map(t -> items.get(t))
                .findFirst().get();
    }
    public T getLast(){
        return items.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .map(t -> items.get(t))
                .findFirst().get();
    }

}

public class SchedulerTest {


    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            Scheduler<String> scheduler = new Scheduler<String>();
            Date now = new Date();
            scheduler.add(new Date(now.getTime()-7200000), jin.next());
            scheduler.add(new Date(now.getTime()-3600000), jin.next());
            scheduler.add(new Date(now.getTime()-14400000), jin.next());
            scheduler.add(new Date(now.getTime()+7200000), jin.next());
            scheduler.add(new Date(now.getTime()+14400000), jin.next());
            scheduler.add(new Date(now.getTime()+3600000), jin.next());
            scheduler.add(new Date(now.getTime()+18000000), jin.next());
            System.out.println(scheduler.getFirst());
            System.out.println(scheduler.getLast());
        }
        if ( k == 3 ) { //test Scheduler with String
            Scheduler<String> scheduler = new Scheduler<String>();
            Date now = new Date();
            scheduler.add(new Date(now.getTime()-7200000), jin.next());
            scheduler.add(new Date(now.getTime()-3600000), jin.next());
            scheduler.add(new Date(now.getTime()-14400000), jin.next());
            scheduler.add(new Date(now.getTime()+7200000), jin.next());
            scheduler.add(new Date(now.getTime()+14400000), jin.next());
            scheduler.add(new Date(now.getTime()+3600000), jin.next());
            scheduler.add(new Date(now.getTime()+18000000), jin.next());
            System.out.println(scheduler.next());
            System.out.println(scheduler.last());
            ArrayList<String> res = scheduler.getAll(new Date(now.getTime()-10000000), new Date(now.getTime()+17000000));
            Collections.sort(res);
            for ( String t : res ) {
                System.out.print(t+" , ");
            }
        }
        if ( k == 4 ) {//test Scheduler with ints complex
            Scheduler<Integer> scheduler = new Scheduler<Integer>();
            int counter = 0;
            ArrayList<Date> to_remove = new ArrayList<Date>();

            while ( jin.hasNextLong() ) {
                Date d = new Date(jin.nextLong());
                int i = jin.nextInt();
                if ( (counter&7) == 0 ) {
                    to_remove.add(d);
                }
                scheduler.add(d,i);
                ++counter;
            }
            jin.next();

            while ( jin.hasNextLong() ) {
                Date l = new Date(jin.nextLong());
                Date h = new Date(jin.nextLong());
                ArrayList<Integer> res = scheduler.getAll(l,h);
                Collections.sort(res);
                System.out.println(l.toString().replace("UTC", "GMT")+" <: "+print(res)+" >: "+(h.toString().replace("UTC", "GMT")));
            }
            System.out.println("test");
            ArrayList<Integer> res = scheduler.getAll(new Date(0),new Date(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
            for ( Date d : to_remove ) {
                scheduler.remove(d);
            }
            res = scheduler.getAll(new Date(0),new Date(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
        }
    }

    private static <T> String print(ArrayList<T> res) {
        if ( res == null || res.size() == 0 ) return "NONE";
        StringBuffer sb = new StringBuffer();
        for ( T t : res ) {
            sb.append(t+" , ");
        }
        return sb.substring(0, sb.length()-3);
    }


}