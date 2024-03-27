package com.eu.habbo.roleplay.gangs;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GangManager {

    private static GangManager instance;

    public static GangManager getInstance() {
        if (instance == null) {
            instance = new GangManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GangManager.class);

    private TIntObjectHashMap<Gang> gangs;

    public Gang getGangById(int gangID) {
        return this.gangs.get(gangID);
    }

    public Gang getGangByName(String gangName) {
        int[] keys = this.gangs.keys();
        for (int key : keys) {
            Gang gang = this.gangs.get(key);
            if (gang.getName().equalsIgnoreCase(gangName)) {
                return gang;
            }
        }
        return null;
    }

    public Gang createGangWithDefaultPosition(String gangName, int userID, int roomID) {
        Gang newGang = GangRepository.getInstance().createGang(gangName, userID, roomID);
        this.gangs.put(newGang.getId(), newGang);
        GangPosition newPosition = GangRepository.getInstance().createGangPosition(newGang.getId(), "Member");
        this.gangs.get(newGang.getId()).addPosition(newPosition);
        return newGang;
    }

    private GangManager() {
        long millis = System.currentTimeMillis();
        this.gangs = new TIntObjectHashMap<>();

        this.reload();

        LOGGER.info("Gangs Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    public void reload() {
        this.loadGangs();
    }

    private void loadGangs() {
        this.gangs.clear();
        this.gangs = GangRepository.getInstance().getAllGangs();
    }

    public void dispose() {
        this.gangs = null;
    }
}