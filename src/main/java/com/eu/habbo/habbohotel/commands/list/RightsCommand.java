package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.PermissionRight;

import java.util.Set;

public class RightsCommand extends Command {
    public RightsCommand() {
        super("cmd_rights");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        StringBuilder message = new StringBuilder(getTextsValue("commands.generic.cmd_rights.text"));

        Set<PermissionRight> rights = gameClient.getHabbo().getHabboInfo().getPermissionGroup().getRights();

        message.append("(").append(rights.size()).append("):\r\n");

        for(PermissionRight right : rights) {
            message.append(right.getName()).append(" - ").append(right.getDescription()).append("\r");
        }

        gameClient.getHabbo().alert(new String[]{message.toString()});
        return true;
    }
}
