package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.RoomForwardMessageComposer;

public class SummonRankCommand extends Command {
    public SummonRankCommand() {
        super("cmd_summon_rank");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        int minRank;

        if (params.length >= 2) {
            try {
                minRank = Integer.parseInt(params[1]);
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.generic.cmd_summonrank.error"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            for (Habbo habbo : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values()) {
                if (habbo.getHabboInfo().getPermissionGroup().getId() >= minRank) {
                    if (habbo == gameClient.getHabbo())
                        continue;

                    if (habbo.getRoomUnit().getRoom() == gameClient.getHabbo().getRoomUnit().getRoom())
                        continue;

                    Room room = habbo.getRoomUnit().getRoom();
                    if (room != null) {
                        Emulator.getGameEnvironment().getRoomManager().logExit(habbo);

                        room.getRoomUnitManager().removeHabbo(habbo, true);
                    }

                    Emulator.getGameEnvironment().getRoomManager().enterRoom(habbo, gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId(), "", true);

                    habbo.getClient().sendResponse(new RoomForwardMessageComposer(gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId()));

                }
            }
        }

        return true;
    }
}
