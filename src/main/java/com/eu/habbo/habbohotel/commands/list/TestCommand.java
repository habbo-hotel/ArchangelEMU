package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;

import java.util.Map;
import java.util.Set;

public class TestCommand extends Command {
    public TestCommand() {
        super("cmd_test");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        StringBuilder message = new StringBuilder("RoomUnit Statuses");

        Set<Map.Entry<RoomUnitStatus, String>> statuses = gameClient.getHabbo().getRoomUnit().getStatuses().entrySet();

        message.append("(").append(statuses.size()).append("):\r\n");

        for(Map.Entry<RoomUnitStatus, String> status : statuses) {
            message.append(status.getKey().toString()).append("\r");
        }

        gameClient.getHabbo().alert(new String[]{message.toString()});
        return true;
    }
}
