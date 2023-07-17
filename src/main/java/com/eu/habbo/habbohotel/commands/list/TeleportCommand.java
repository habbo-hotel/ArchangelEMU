package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class TeleportCommand extends Command {
    public TeleportCommand() {
        super("cmd_teleport");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getRiding() != null){ //TODO Make this an event plugin which fires that can be cancelled
            return true;
        }
        
        if (gameClient.getHabbo().getRoomUnit().isCmdTeleportEnabled()) {
            gameClient.getHabbo().getRoomUnit().setCmdTeleportEnabled(false);
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_teleport.disabled"), RoomChatMessageBubbles.ALERT);
            return true;
        } else {
            gameClient.getHabbo().getRoomUnit().setCmdTeleportEnabled(true);
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_teleport.enabled"), RoomChatMessageBubbles.ALERT);
            return true;
        }
    }
}
