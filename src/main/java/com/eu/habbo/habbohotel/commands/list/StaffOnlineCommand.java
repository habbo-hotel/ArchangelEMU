package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StaffOnlineCommand extends Command {
    public StaffOnlineCommand() {
        super("cmd_staff_online");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        int minRank = Emulator.getConfig().getInt("commands.cmd_staffonline.min_rank");

        if (params.length >= 2) {
            try {
                int i = Integer.parseInt(params[1]);

                if (i < 1) {
                    gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_staffonline.positive_only"), RoomChatMessageBubbles.ALERT);
                    return true;
                } else {
                    minRank = i;
                }
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_staffonline.numbers_only"), RoomChatMessageBubbles.ALERT);
                return true;
            }
        }

        synchronized (Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos()) {
            int finalMinRank = minRank;
            List<Habbo> staffs = Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values().stream()
                    .filter(habbo -> habbo.getHabboInfo().getPermissionGroup().getId() >= finalMinRank)
                    .sorted(Comparator.comparingInt(o->o.getHabboInfo().getId()))
                    .toList();

            String message = staffs.stream()
                    .map(habbo -> habbo.getHabboInfo().getUsername() + ": " + habbo.getHabboInfo().getPermissionGroup().getName() + "\r")
                    .collect(Collectors.joining("", getTextsValue("commands.generic.cmd_staffonline.staffs") + "\r\n", ""));

            gameClient.getHabbo().alert(message);
        }

        return true;
    }
}
