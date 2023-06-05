package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class UpdateTextsCommand extends Command {
    public UpdateTextsCommand() {
        super("cmd_update_texts");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        try {
            Emulator.getTexts().reload();
            Emulator.getGameEnvironment().getCommandsManager().reloadCommands();
            gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_texts"), RoomChatMessageBubbles.ALERT);
        } catch (Exception e) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_update_texts.failed"), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}
