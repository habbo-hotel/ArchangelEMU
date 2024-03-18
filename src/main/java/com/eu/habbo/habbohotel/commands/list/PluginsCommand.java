package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.MOTDNotificationComposer;
import com.eu.habbo.plugin.HabboPlugin;

import java.util.Collections;

public class PluginsCommand extends Command {
    public PluginsCommand() {
        super("cmd_plugins");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        StringBuilder message = new StringBuilder("Plugins (" + Emulator.getPluginManager().getPlugins().size() + ")\r");

        for (HabboPlugin plugin : Emulator.getPluginManager().getPlugins()) {
            message.append("\r").append(plugin.configuration.name).append(" By ").append(plugin.configuration.author);
        }


        if (Emulator.getConfig().getBoolean("commands.plugins.oldstyle")) {
            gameClient.sendResponse(new MOTDNotificationComposer(Collections.singletonList(message.toString())));
        } else {
            gameClient.getHabbo().alert(message.toString());
        }

        return true;
    }
}
