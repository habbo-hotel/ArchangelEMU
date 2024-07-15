package com.eu.habbo.roleplay.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corp.CorpTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HabboRoleplayHelper {

    public static List<Habbo> getUsersByCorpTag(CorpTag corpTag) {
        List<Habbo> habbosInCorp = new ArrayList<>();
        ConcurrentHashMap<Integer, Habbo> habbosOnline = Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos();

        for (Habbo habbo : habbosOnline.values()) {
            if (habbo.getHabboRoleplayStats().getCorp().getTags().contains(corpTag)) {
                habbosInCorp.add(habbo);
            }
        }

        return habbosInCorp;
    }

    public static List<Habbo> getUsersWorking(List<Habbo> habbos) {
        List<Habbo> habbosWorking = new ArrayList<>();
        for (Habbo habbo : habbos) {
            if (habbo.getHabboRoleplayStats().isWorking()) {
                habbosWorking.add(habbo);
            }
        }

        return habbosWorking;
    }

}
