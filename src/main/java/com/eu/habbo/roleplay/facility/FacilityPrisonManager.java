package com.eu.habbo.roleplay.facility;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FacilityPrisonManager {

    public static String PRISON_TAG = "prison";
    
    private static FacilityPrisonManager instance;

    public static FacilityPrisonManager getInstance() {
        if (instance == null) {
            instance = new FacilityPrisonManager();
        }
        return instance;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(FacilityPrisonManager.class);
    private final List<PrisonSentence> usersInJail;
    private FacilityPrisonManager() {
        long millis = System.currentTimeMillis();
        this.usersInJail = new CopyOnWriteArrayList<>();
        LOGGER.info("Prison Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    public Room getPrison() {
        return FacilityManager.getFirstRoomWithTag(FacilityPrisonManager.PRISON_TAG);
    }

    public PrisonSentence getPrisonTime(Habbo user) {
        if (this.usersInJail.isEmpty()) {
            return null;
        }
        return this.usersInJail.get(user.getHabboInfo().getId());
    }
    public void addPrisonTime(Habbo user, String crime, int timeLeft) {
        this.usersInJail.add(new PrisonSentence(user, crime, timeLeft, 0));
        user.shout(Emulator.getTexts().getValue("roleplay.prison.starts_sentence"));
    }

    public void removePrisonTime(Habbo user) {
        if (this.getPrisonTime(user) == null) {
            return;
        }
        this.usersInJail.remove(user.getHabboInfo().getId());
        user.shout(Emulator.getTexts().getValue("roleplay.prison.finishes_sentence"));
    }

    public void cycle() {
        for (PrisonSentence userSentence : usersInJail) {
            if (userSentence.getTimeLeft() == 0) {
                this.removePrisonTime(userSentence.getHabbo());
                return;
            }
            userSentence.getHabbo().shout(Emulator.getTexts().
                    getValue("roleplay.prison.sentence_time_left")
                    .replace(":timeLeft", Integer.toString(userSentence.getTimeLeft()))
                    .replace(":timeServed", Integer.toString(userSentence.getTimeServed()))
            );
        }
    }

    public void dispose() {
    }
}