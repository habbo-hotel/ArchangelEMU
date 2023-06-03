package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import gnu.trove.map.hash.THashMap;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventCommand extends Command {
    public EventCommand() {
        super("cmd_event");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null || params.length < 2) {
            return false;
        }
        String message = IntStream.range(1, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());

        THashMap<String, String> codes = new THashMap<>();
        codes.put("ROOMNAME", gameClient.getHabbo().getHabboInfo().getCurrentRoom().getName());
        codes.put("ROOMID", gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId() + "");
        codes.put("USERNAME", gameClient.getHabbo().getHabboInfo().getUsername());
        codes.put("LOOK", gameClient.getHabbo().getHabboInfo().getLook());
        codes.put("TIME", Emulator.getDate().toString());
        codes.put("MESSAGE", message);

        ServerMessage msg = new NotificationDialogMessageComposer("hotel.event", codes).compose();

        for (Habbo habbo : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values()) {
            if (habbo.getHabboStats().isBlockStaffAlerts()) {
                continue;
            }

            habbo.getClient().sendResponse(msg);
        }

        return true;

    }
}
