package com.eu.habbo.roleplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeOfDayManager {
    private static final long TICK_INTERVAL = 1000;
    private static final int DEFAULT_TICK_RATE = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeOfDayManager.class);

    private static TimeOfDayManager instance;

    public static TimeOfDayManager getInstance() {
        if (instance == null) {
            instance = new TimeOfDayManager();
        }
        return instance;
    }

    private long currentTime;
    private int tickRate;
    private ScheduledExecutorService executor;

    private TimeOfDayManager() {
        long millis = System.currentTimeMillis();
        this.currentTime = System.currentTimeMillis();
        this.tickRate = DEFAULT_TICK_RATE;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        startTick();
        LOGGER.info("Time of Day Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    private void startTick() {
        executor.scheduleAtFixedRate(this::tick, 0, TICK_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private synchronized void tick() {
        currentTime += TICK_INTERVAL * tickRate;
    }

    public synchronized long getCurrentTime() {
        return currentTime;
    }

    public synchronized void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    // Stop the ticking process
    public synchronized void stop() {
        executor.shutdown();
    }
}