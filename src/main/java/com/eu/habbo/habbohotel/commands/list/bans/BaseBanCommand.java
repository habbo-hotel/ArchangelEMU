package com.eu.habbo.habbohotel.commands.list.bans;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;

import java.util.stream.Collectors;
import java.util.stream.IntStream;


public abstract class BaseBanCommand extends Command {
    protected static final int TEN_YEARS = 315569260;
    protected HabboInfo habboInfo;
    protected String reason;
    protected int count;

    public BaseBanCommand(String name)
    {
        super(name);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        this.reason = getReason(params);
        this.habboInfo = getHabboInfo(params);
        return false;
    }

    protected String getReason(String[] params) {
        if (params.length > 2) {
            return IntStream.range(2, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());
        }

        return "";
    }

    protected HabboInfo getHabboInfo(String[] params) {
        if (params.length >= 2) {
            Habbo h = getHabbo(params[1]);

            if (h != null) {
                return h.getHabboInfo();
            } else {
                return HabboManager.getOfflineHabboInfo(params[1]);
            }
        }
        return null;
    }
}
