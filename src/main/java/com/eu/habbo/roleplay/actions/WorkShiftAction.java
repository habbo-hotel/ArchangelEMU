package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpPosition;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorkShiftAction implements Runnable {
    private final Habbo user;
    private final Corp corp;
    private final CorpPosition corpPosition;

    @Override
    public void run() {
        Emulator.getThreading().run(this, 25000);
    }

}
