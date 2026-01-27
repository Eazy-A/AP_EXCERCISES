package gpt_hard_midterm.decorator_pattern_with_concurrent_logging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// component
interface Logger {
    void log(String message);
}

// base decorator
abstract class LoggerDecorator implements Logger {
    protected final Logger source;

    public LoggerDecorator(Logger source) {
        this.source = source;
    }

    @Override
    public void log(String message) {
        source.log(message);
    }
}

// concrete component
class BasicLogger implements Logger {

    @Override
    public void log(String message) {
        System.out.println(message);
    }
}

class TimestampLogger extends LoggerDecorator {

    public TimestampLogger(Logger source) {
        super(source);
    }

    @Override
    public void log(String message) {
        String decoratedMessage = System.currentTimeMillis() + " " + message +  "\n";
        super.log(decoratedMessage);
    }
}

class ThreadNameLogger extends LoggerDecorator {

    public ThreadNameLogger(Logger source) {
        super(source);
    }

    @Override
    public void log(String message) {
        String decoratedMessage = "[" + Thread.currentThread().getName() + "] " + message + "\n";
        super.log(decoratedMessage);
    }
}

class LogTask implements Runnable {
    private final Logger logger;
    private final String message;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public LogTask(Logger logger, String message) {
        this.logger = logger;
        this.message = message;
    }

    @Override
    public void run() {
        lock.writeLock().lock();
        try {
            logger.log(message);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
public class Main {
    public static void main(String[] args) {
        Logger logger =
                new ThreadNameLogger(
                        new TimestampLogger(
                                new BasicLogger()
                        )
                );

        List<Runnable> tasks = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            tasks.add(new LogTask(logger, "MESSAGE_" + i));
        }
        ExecutorService executor = Executors.newFixedThreadPool(5);
        tasks.forEach(executor::execute);

    }

}
