package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.ModeratorMessageComposer;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HotelAlertCommand extends Command {
    public HotelAlertCommand() {
        super("cmd_ha");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length > 1) {
            String message = IntStream.range(1, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());

            ServerMessage msg = new ModeratorMessageComposer(message + "\r\n-" + gameClient.getHabbo().getHabboInfo().getUsername(), "").compose();

            Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values().stream()
                    .filter(habbo -> !habbo.getHabboStats().isBlockStaffAlerts())
                    .forEach(habbo -> habbo.getClient().sendResponse(msg));
        } else {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_ha.forgot_message"), RoomChatMessageBubbles.ALERT);
        }
        return true;
    }
}
