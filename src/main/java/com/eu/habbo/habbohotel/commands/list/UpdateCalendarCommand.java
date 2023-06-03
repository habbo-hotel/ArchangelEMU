package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class UpdateCalendarCommand extends Command {
    public UpdateCalendarCommand() {
        super("cmd_update_calendar");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getCalendarManager().reload();
        gameClient.getHabbo().whisper(getTextsValue("commands.success.cmd_update_calendar"), RoomChatMessageBubbles.ALERT);
        return true;
    }
}
