package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.unknown.IgnoredUsersComposer;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;

public class GetIgnoredUsersEvent extends MessageHandler {

    ArrayList<String> ignoredUsernames = new ArrayList<>();
    @Override
    public void handle() throws Exception {
        Habbo habbo = this.client.getHabbo();
        if(habbo == null) return;

        TIntArrayList ignoredUsersId = habbo.getHabboStats().getIgnoredUsers();

        if (ignoredUsersId.size() > 0) {
            int userId = ignoredUsersId.iterator().next();

            HabboInfo user = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(userId);
            if(user == null) user = HabboManager.getOfflineHabboInfo(userId);
            if(user != null) ignoredUsernames.add(user.getUsername());
        }

        this.client.sendResponse(new IgnoredUsersComposer(ignoredUsernames));
    }
}
