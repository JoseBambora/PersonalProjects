package org.jdaextension.configuration;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SchedulerManager {
    private static SchedulerManager schedulerManager;
    private final ScheduledExecutorService scheduler;

    protected SchedulerManager() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static SchedulerManager getInstance() {
        if (schedulerManager == null)
            schedulerManager = new SchedulerManager();
        return schedulerManager;
    }

    protected void schedulerCycle(Alarm alarm) {
        scheduler.scheduleAtFixedRate(alarm.runnable(), 0, alarm.delay(), alarm.timeUnit());
    }

    protected void schedulerCycle(List<Alarm> alarms) {
        alarms.forEach(alarm -> scheduler.scheduleAtFixedRate(alarm.runnable(), 0, alarm.delay(), alarm.timeUnit()));
    }

    protected void schedulerStatic(Alarm alarm) {
        scheduler.schedule(alarm.runnable(), alarm.delay(), alarm.timeUnit());
    }

    protected void schedulerStatic(List<Alarm> alarms) {
        alarms.forEach(alarm -> scheduler.schedule(alarm.runnable(), alarm.delay(), alarm.timeUnit()));
    }
}
