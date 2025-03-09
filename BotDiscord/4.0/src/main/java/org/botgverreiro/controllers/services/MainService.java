package org.botgverreiro.controllers.services;

import org.botgverreiro.Main;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainService {
    private final ScheduledExecutorService scheduler;
    private MainService() {
        this.scheduler = Executors.newScheduledThreadPool(Integer.parseInt(System.getenv("THREADS")));
    }

    private long computeInitialDelay(int hour) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(hour).withMinute(35).withSecond(0);

        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        return Math.abs(ChronoUnit.SECONDS.between(now,nextRun));
    }

    public void addServiceDaily(Runnable runnable, int hour) {
        long period = 24 * 60 * 60;
        scheduler.scheduleAtFixedRate(runnable,computeInitialDelay(hour), period, TimeUnit.SECONDS);
    }


    public void addServiceScheduled(Runnable runnable, List<LocalDateTime> localDateTimes){
        LocalDateTime now = LocalDateTime.now();
        localDateTimes.forEach(ldt -> scheduler.schedule(runnable,ChronoUnit.SECONDS.between(now,ldt),TimeUnit.SECONDS));
    }

    private static MainService mainService;
    public static MainService getInstance() {
        if(mainService == null)
            mainService = new MainService();
        return mainService;
    }
}
