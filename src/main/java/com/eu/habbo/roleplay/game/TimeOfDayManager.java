package com.eu.habbo.roleplay.game;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.messages.outgoing.game.TimeOfDayComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeOfDayManager {
    private static final long TICK_INTERVAL = 1000;
    private static final long REPORT_INTERVAL = 1000 * 60;
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeOfDayManager.class);

    private static TimeOfDayManager instance;

    public static TimeOfDayManager getInstance() {
        if (instance == null) {
            instance = new TimeOfDayManager();
        }
        return instance;
    }

    private long currentTime;
    private final ScheduledExecutorService executor;

    private TimeOfDayManager() {
        long millis = System.currentTimeMillis();
        this.currentTime = System.currentTimeMillis();
        this.executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::tick, 0, TimeOfDayManager.TICK_INTERVAL, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(this::announceTime, 0, TimeOfDayManager.REPORT_INTERVAL, TimeUnit.MILLISECONDS);
        LOGGER.info("Time of Day Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void cycle() {
        executor.scheduleAtFixedRate(this::announceTime, 0, TICK_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void announceTime() {
        Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values().stream().forEach(habbo -> habbo.getClient().sendResponse(new TimeOfDayComposer()));
    }
    private synchronized void tick() {
        currentTime += TICK_INTERVAL;
    }

    public synchronized long getCurrentTime() {
        return currentTime;
    }

    // Stop the ticking process
    public synchronized void stop() {
        executor.shutdown();
    }
}