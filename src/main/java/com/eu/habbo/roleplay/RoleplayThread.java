package com.eu.habbo.roleplay;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.facility.FacilityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoleplayThread implements Runnable {

    private static RoleplayThread instance;

    public static RoleplayThread getInstance() {
        if (instance == null) {
            instance = new RoleplayThread();
        }
        return instance;
    }

    private static final int DELAY = 5000;

    public RoleplayThread() {
        Emulator.getThreading().run(this, DELAY);
    }

    @Override
    public void run() {
        Emulator.getThreading().run(this, DELAY);
        FacilityManager.getInstance().cycle();
    }

}