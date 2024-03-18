package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.PermissionCommand;

import java.util.Collections;
import java.util.List;

public class CommandsCommand extends Command {
    public CommandsCommand() {
        super("cmd_commands");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        StringBuilder message = new StringBuilder(getTextsValue("commands.generic.cmd_commands.text"));

        List<String> commands = gameClient.getHabbo().getHabboInfo().getPermissionGroup().getCommands();

        Collections.sort(commands);

        message.append("(").append(commands.size()).append("):\r\n");

        for(String commandName : commands) {
            PermissionCommand command = Emulator.getGameEnvironment().getPermissionsManager().getCommand(commandName);

            if(command == null) {
                continue;
            }

            message.append(command.getDescription()).append("\r");
        }

        gameClient.getHabbo().alert(new String[]{message.toString()});
        return true;
    }
}
