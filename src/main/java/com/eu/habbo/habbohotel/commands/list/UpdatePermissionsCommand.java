package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class UpdatePermissionsCommand extends Command {
    public UpdatePermissionsCommand() {
        super("cmd_update_permissions");
    }
    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getPermissionsManager().reload();

        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_permissions"), RoomChatMessageBubbles.ALERT);

        return true;
    }
}
