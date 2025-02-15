package org.botgverreiro.bot.threads;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the class responsible for handling the multi-threading on the application.
 * Basically, I create n threads (n is specified on the configuration file), and then I will give tasks for that threads
 * through the time.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class TaskManager {
    private final BlockingQueue<Runnable> taskQueue;
    private final List<Thread> threadList;
    private final AtomicInteger numberTasks = new AtomicInteger(0);

    public TaskManager() {
        int numberThreads = Integer.parseInt(System.getenv("THREADS"));
        taskQueue = new LinkedBlockingDeque<>();
        threadList = new ArrayList<>(numberThreads);
        for (int i = 0; i < numberThreads; i++) {
            Thread t = new Thread(this::run);
            threadList.add(t);
            t.start();
        }
    }

    public void addTask(Runnable runnable) {
        this.taskQueue.add(runnable);
        numberTasks.incrementAndGet();
    }

    public void stop() {
        threadList.forEach(Thread::interrupt);
    }

    private void run() {
        while (true) {
            try {
                Runnable task = taskQueue.take();
                // System.out.println(Thread.currentThread().getName() + " a executar pedido.");
                task.run();
                numberTasks.decrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void waitTask() {
        while (numberTasks.get() != 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
