package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.PermissionCommand;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommandsCommand extends Command {
    public CommandsCommand() {
        super("cmd_commands");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        StringBuilder message = new StringBuilder(getTextsValue("commands.generic.cmd_commands.text"));

        List<PermissionCommand> commands = gameClient.getHabbo().getHabboInfo().getPermissionGroup().getCommands();

        Collections.sort(commands, Comparator.comparing(PermissionCommand::getName));

        message.append("(").append(commands.size()).append("):\r\n");

        for(PermissionCommand command : commands) {
            message.append(command.getDescription()).append("\r");
        }

        gameClient.getHabbo().alert(new String[]{message.toString()});
        return true;
    }
}
