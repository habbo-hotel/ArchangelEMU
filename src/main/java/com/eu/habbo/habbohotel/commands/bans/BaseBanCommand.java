package com.eu.habbo.habbohotel.commands.bans;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;


public abstract class BaseBanCommand extends Command {
    protected HabboInfo habboInfo;
    protected String reason;
    protected int count;

    public BaseBanCommand(String permission, String[] keys) {
        super(permission, keys);
    }


    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        this.reason = getReason(params);
        this.habboInfo = getHabboInfo(params);
        return false;
    }

    private String getReason(String[] params) {
        StringBuilder reason = new StringBuilder();

        if (params.length > 2) {
            for (int i = 2; i < params.length; i++) {
                reason.append(params[i]);
                reason.append(" ");
            }
        }

        return reason.toString();
    }

    protected HabboInfo getHabboInfo(String[] params) {
        if (params.length >= 2) {
            Habbo h = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

            if (h != null) {
                return h.getHabboInfo();
            } else {
                return HabboManager.getOfflineHabboInfo(params[1]);
            }
        }
        return null;
    }
}
