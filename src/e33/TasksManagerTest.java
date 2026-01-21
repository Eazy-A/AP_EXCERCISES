package e33;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class DeadlineNotValidException extends Exception {
    public DeadlineNotValidException(LocalDateTime dateTime) {
        super("The deadline " + dateTime + " has already passed");
    }
}

interface ITask {
    int getPriority();

    String getCategory();

    LocalDateTime getTime();
}

abstract class Task implements ITask {
    protected String category;
    protected String name;
    protected String description;

    public Task(String category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
    }


}

class NormalTask extends Task {

    public NormalTask(String category, String name, String description) {
        super(category, name, description);
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
    public LocalDateTime getTime() {
        return LocalDateTime.MAX;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name +
                "', description='" + description + '\'' +
                '}';
    }
}

class PriorityTask extends Task {
    private int priority;

    public PriorityTask(String category, String name, String description, int priority) {
        super(category, name, description);
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public LocalDateTime getTime() {
        return LocalDateTime.MAX;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name +
                "', description='" + description + '\'' +
                ", priority=" + priority +
                '}';
    }
}

class DeadlineTask extends Task {
    private LocalDateTime deadline;

    public DeadlineTask(String category, String name, String description, LocalDateTime deadline) {
        super(category, name, description);
        this.deadline = deadline;
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
    public LocalDateTime getTime() {
        return deadline;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name +
                "', description='" + description +
                "', deadline=" + deadline +
                '}';
    }
}

class PriorityDeadlineTask extends Task {
    private int priority;
    private LocalDateTime deadline;

    public PriorityDeadlineTask(String category, String name, String description, LocalDateTime deadline, int priority) {
        super(category, name, description);
        this.deadline = deadline;
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public LocalDateTime getTime() {
        return deadline;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name +
                "', description='" + description + '\'' +
                ", deadline=" + deadline +
                ", priority=" + priority +
                '}';
    }
}

class TaskFactory {
    private static LocalDateTime DEADLINE = LocalDateTime.of(2020, 6, 2, 0, 0);

    public static Task create(String line) throws DeadlineNotValidException {
        String[] parts = line.split(",");
        String category = parts[0];
        String name = parts[1];
        String description = parts[2];
        if (parts.length == 3) {
            return new NormalTask(category, name, description);
        }

        String value = parts[3].trim();

        if (parts.length == 4) {
            if (value.contains("-") || value.contains("T")) {
                LocalDateTime deadline = LocalDateTime.parse(value);
                if (deadline.isBefore(DEADLINE)) throw new DeadlineNotValidException(deadline);
                return new DeadlineTask(category, name, description, deadline);
            } else {
                return new PriorityTask(category, name, description, Integer.parseInt(value));
            }
        } else {
            LocalDateTime deadline = LocalDateTime.parse(value);
            if (deadline.isBefore(DEADLINE)) throw new DeadlineNotValidException(deadline);
            int priority = Integer.parseInt(parts[4].trim());
            return new PriorityDeadlineTask(category, name, description, deadline, priority);
        }
    }

}

class TaskManager {
    private List<Task> tasks = new ArrayList<>();

    public void readTasks(InputStream inputStream) throws DeadlineNotValidException {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;
            try {
                Task task = TaskFactory.create(line);
                tasks.add(task);
            } catch (DeadlineNotValidException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private Comparator<Task> priorityComparator() {
        return Comparator.comparing(Task::getPriority)
                .thenComparing(Task::getTime);
    }

    public void printTasks(OutputStream os, boolean includePriority, boolean includeCategory) {
        PrintStream ps = new PrintStream(os);

        if (includeCategory) {
            Map<String, List<Task>> groups = tasks.stream()
                    .collect(Collectors.groupingBy(Task::getCategory, TreeMap::new, Collectors.toList()));

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
                tasks.stream().sorted(Comparator.comparing(ITask::getTime)).forEach(ps::println);
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
