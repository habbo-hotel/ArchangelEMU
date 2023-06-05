package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class UpdatePluginsCommand extends Command {
    public UpdatePluginsCommand() {
        super("cmd_update_plugins");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getPluginManager().reload();

        gameClient.getHabbo().whisper("This is an unsafe command and could possibly lead to memory leaks.\rIt is recommended to restart the emulator in order to reload plugins.");
        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_plugins").replace("%count%", Emulator.getPluginManager().getPlugins().size() + ""), RoomChatMessageBubbles.ALERT);
        return true;
    }
}
