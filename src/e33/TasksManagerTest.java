package e33;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


class DeadlineNotValidException extends Exception {
    public DeadlineNotValidException(LocalDateTime dateTime) {
        super("The deadline " + dateTime + " has already passed");
    }
}
interface ITask {
    int getPriority();

    String getCategory();

    LocalDateTime getDeadline();

    String getDescription();
}

class SimpleTask implements ITask {
    private final String category, name, description;

    public SimpleTask(String category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public LocalDateTime getDeadline() {
        return LocalDateTime.MAX;
    }

    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public String toString() {
        return String.format("Task{name='%s', description='%s'}", name, description);
    }
}

abstract class TaskDecorator implements ITask{
    protected ITask wrapped;

    public TaskDecorator(ITask wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int getPriority() {
        return wrapped.getPriority();
    }

    @Override
    public String getCategory() {
        return wrapped.getCategory();
    }

    @Override
    public LocalDateTime getDeadline() {
        return wrapped.getDeadline();
    }
}

class PriorityDecorator extends TaskDecorator{
    private final int priority;

    public PriorityDecorator(ITask wrapped, int priority) {
        super(wrapped);
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }
    @Override
    public String toString() {
        return wrapped.toString().replace("}", ", priority=" + priority + "}");
    }
}
class DeadlineDecorator extends TaskDecorator{
    private final LocalDateTime deadline;

    public DeadlineDecorator(ITask wrapped, LocalDateTime deadline) {
        super(wrapped);
        this.deadline = deadline;
    }

    @Override
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }

    @Override
    public String toString() {
        return wrapped.toString().replace("}", ", deadline=" + deadline + "}");
    }
}
class TaskFactory {
    private static final LocalDateTime REFERENCE_DATE = LocalDateTime.of(2020, 6, 2, 0, 0);

    public static ITask create(String line) throws DeadlineNotValidException {
        String[] parts = line.split(",");
        ITask task = new SimpleTask(parts[0], parts[1], parts[2]);

        for (int i = 3; i < parts.length; i++) {
            String val = parts[i].trim();
            if (val.contains("-") || val.contains("T")) {
                LocalDateTime date = LocalDateTime.parse(val);
                if (date.isBefore(REFERENCE_DATE)) throw new DeadlineNotValidException(date);
                task = new DeadlineDecorator(task, date);
            } else {
                task = new PriorityDecorator(task, Integer.parseInt(val));
            }
        }
        return task;
    }
}

class TaskManager {
    private final List<ITask> tasks = new ArrayList<>();

    public void readTasks(InputStream inputStream){
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;
            try {
                ITask task = TaskFactory.create(line);
                tasks.add(task);
            } catch (DeadlineNotValidException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private Comparator<ITask> priorityComparator() {
        return Comparator.comparing(ITask::getPriority)
                .thenComparing(ITask::getDeadline);
    }

    public void printTasks(OutputStream os, boolean includePriority, boolean includeCategory) {
        PrintStream ps = new PrintStream(os);

        if (includeCategory) {
            Map<String, List<ITask>> groups = tasks.stream()
                    .collect(Collectors.groupingBy(ITask::getCategory, TreeMap::new, Collectors.toList()));

            groups.forEach((category, taskList) -> {
                ps.println(category.toUpperCase());
                if (includePriority) {
                    taskList.stream().sorted(priorityComparator()).forEach(ps::println);
                } else {
                    taskList.forEach(ps::println);
                }
            });
        } else {
            if (includePriority) {
                tasks.stream().sorted(priorityComparator()).forEach(ps::println);
            } else {
                tasks.stream().sorted(Comparator.comparing(ITask::getDeadline)).forEach(ps::println);
            }
        }
    }
}

public class TasksManagerTest {

    public static void main(String[] args) throws DeadlineNotValidException {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);

        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}
