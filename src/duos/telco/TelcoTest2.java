package duos.telco;

import java.util.*;
import java.util.stream.Collectors;

// Helper for time formatting
class DurationConverter {
    public static String convert(long duration) {
        long minutes = duration / 60;
        long seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

// Custom Exception
class InvalidOperation extends Exception {
}

// State Interface
interface ICallState {
    void answer(long timestamp) throws InvalidOperation;

    void end(long timestamp) throws InvalidOperation;

    void hold(long timestamp) throws InvalidOperation;

    void resume(long timestamp) throws InvalidOperation;
}

// Base State logic
abstract class CallState implements ICallState {
    Call call;
    public long initialized, start, end, holdStarted;
    public long durationHold = 0;

    public CallState(Call call) {
        this.call = call;
    }

    public CallState(CallState oldState) {
        this.call = oldState.call;
        this.initialized = oldState.initialized;
        this.start = oldState.start;
        this.end = oldState.end;
        this.holdStarted = oldState.holdStarted;
        this.durationHold = oldState.durationHold;
    }

    public long getTotalDuration() {
        if (start == 0 || end == 0) return 0;
        return (end - start - durationHold);
    }
}

// --- Concrete States ---

class InitializedState extends CallState {
    public InitializedState(Call call, long ts) {
        super(call);
        this.initialized = ts;
    }

    @Override
    public void answer(long ts) {
        this.start = ts;
        call.state = new InProgressState(this);
    }

    @Override
    public void end(long ts) {
        call.state = new IdleState(this);
    } // Missed call logic

    @Override
    public void hold(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }

    @Override
    public void resume(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }
}

class InProgressState extends CallState {
    public InProgressState(CallState old) {
        super(old);
    }

    @Override
    public void answer(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }

    @Override
    public void end(long ts) {
        this.end = ts;
        call.state = new IdleState(this);
    }

    @Override
    public void hold(long ts) {
        this.holdStarted = ts;
        call.state = new PausedState(this);
    }

    @Override
    public void resume(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }
}

class PausedState extends CallState {
    public PausedState(CallState old) {
        super(old);
    }

    @Override
    public void answer(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }

    @Override
    public void end(long ts) {
        this.durationHold += (ts - holdStarted);
        this.end = ts;
        call.state = new IdleState(this);
    }

    @Override
    public void hold(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }

    @Override
    public void resume(long ts) {
        this.durationHold += (ts - holdStarted);
        call.state = new InProgressState(this);
    }
}

class IdleState extends CallState {
    public IdleState(CallState old) {
        super(old);
    }

    @Override
    public void answer(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }

    @Override
    public void end(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }

    @Override
    public void hold(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }

    @Override
    public void resume(long ts) throws InvalidOperation {
        throw new InvalidOperation();
    }
}

// --- Call and App ---

class Call {
    String uuid, dialer, receiver;
    public CallState state;

    public Call(String uuid, String dialer, String receiver, long timestamp) {
        this.uuid = uuid;
        this.dialer = dialer;
        this.receiver = receiver;
        this.state = new InitializedState(this, timestamp);
    }

    public void update(long ts, String action) throws InvalidOperation {
        switch (action) {
            case "ANSWER":
                state.answer(ts);
                break;
            case "END":
                state.end(ts);
                break;
            case "HOLD":
                state.hold(ts);
                break;
            case "RESUME":
                state.resume(ts);
                break;
        }
    }

    public long getStart() {
        return state.start == 0 ? state.initialized : state.start;
    }

    public long getTotalDuration() {
        return state.getTotalDuration();
    }
}

class TelcoApp {
    private final Map<String, Call> callsByUuid = new HashMap<>();
    private final Map<String, List<Call>> callsByPhone = new HashMap<>();

    // Professor's Comparators
    Comparator<Call> byStart = Comparator.comparing(Call::getStart).thenComparing(c -> c.uuid);
    Comparator<Call> byDuration = Comparator.comparing(Call::getTotalDuration)
            .thenComparing(Call::getStart).reversed();

    void addCall(String uuid, String dialer, String receiver, long ts) {
        Call c = new Call(uuid, dialer, receiver, ts);
        callsByUuid.put(uuid, c);
        callsByPhone.computeIfAbsent(dialer, k -> new ArrayList<>()).add(c);
        callsByPhone.computeIfAbsent(receiver, k -> new ArrayList<>()).add(c);
    }

    void updateCall(String uuid, long ts, String action) {
        try {
            callsByUuid.get(uuid).update(ts, action);
        } catch (Exception e) { /* Valid actions guaranteed per requirements */ }
    }

    private void printCall(Call c, String phone) {
        String type = c.dialer.equals(phone) ? "D" : "R";
        String other = c.dialer.equals(phone) ? c.receiver : c.dialer;
        String end = c.state.end == 0 ? "MISSED CALL" : String.valueOf(c.state.end);
        System.out.printf("%s %s %d %s %s%n", type, other, c.getStart(), end,
                DurationConverter.convert(c.getTotalDuration()));
    }

    void printChronologicalReport(String phone) {
        callsByPhone.getOrDefault(phone, new ArrayList<>()).stream()
                .sorted(byStart).forEach(c -> printCall(c, phone));
    }

    void printReportByDuration(String phone) {
        callsByPhone.getOrDefault(phone, new ArrayList<>()).stream()
                .sorted(byDuration).forEach(c -> printCall(c, phone));
    }

    void printCallsDuration() {
        TreeMap<String, Long> result = callsByUuid.values().stream()
                .collect(Collectors.groupingBy(
                        c -> String.format("%s <-> %s", c.dialer, c.receiver),
                        TreeMap::new,
                        Collectors.summingLong(Call::getTotalDuration)
                ));
        result.forEach((key, val) -> System.out.printf("%s : %s%n", key, DurationConverter.convert(val)));
    }
}

public class TelcoTest2 {
    public static void main(String[] args) {
        TelcoApp app = new TelcoApp();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String command = parts[0];

            if (command.equals("addCall")) {
                String uuid = parts[1];
                String dialer = parts[2];
                String receiver = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                app.addCall(uuid, dialer, receiver, timestamp);
            } else if (command.equals("updateCall")) {
                String uuid = parts[1];
                long timestamp = Long.parseLong(parts[2]);
                String action = parts[3];
                app.updateCall(uuid, timestamp, action);
            } else if (command.equals("printChronologicalReport")) {
                String phoneNumber = parts[1];
                app.printChronologicalReport(phoneNumber);
            } else if (command.equals("printReportByDuration")) {
                String phoneNumber = parts[1];
                app.printReportByDuration(phoneNumber);
            } else {
                app.printCallsDuration();
            }
        }
    }
}