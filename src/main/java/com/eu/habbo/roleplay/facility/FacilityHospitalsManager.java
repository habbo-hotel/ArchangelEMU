package com.eu.habbo.roleplay.facility;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FacilityHospitalsManager {

    public static String HOSPITAL_ROOM_TAG = "hospital";

    private static FacilityHospitalsManager instance;

    public static FacilityHospitalsManager getInstance() {
        if (instance == null) {
            instance = new FacilityHospitalsManager();
        }
        return instance;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(FacilityHospitalsManager.class);
    private final List<Habbo> usersToHeal;
    private final ScheduledExecutorService scheduler;

    private FacilityHospitalsManager() {
        long millis = System.currentTimeMillis();
        this.usersToHeal = new CopyOnWriteArrayList<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.startHealingProcess();
        LOGGER.info("Hospital Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    public Room getHospital() {
        return FacilityManager.getFirstRoomWithTag(FacilityHospitalsManager.HOSPITAL_ROOM_TAG);
    }

    public void addUserToHeal(Habbo user) {
        this.usersToHeal.add(user);
    }

    public void removeUserToHeal(Habbo user) {
        this.usersToHeal.remove(user);
    }

    public void startHealingProcess() {
        Runnable healTask = () -> {
            for (Habbo user : usersToHeal) {
                user.getHabboRoleplayStats().setHealth(user.getHabboRoleplayStats().getHealthNow() + 5);
            }
        };

        // Schedule the task to run every 5 seconds
        scheduler.scheduleAtFixedRate(healTask, 0, 5, TimeUnit.SECONDS);
    }

    public void stopHealingProcess() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public void dispose() {
        this.stopHealingProcess();
    }
}