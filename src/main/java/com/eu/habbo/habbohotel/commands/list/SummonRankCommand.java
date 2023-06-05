package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
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

                    if (habbo.getHabboInfo().getCurrentRoom() == gameClient.getHabbo().getHabboInfo().getCurrentRoom())
                        continue;

                    Room room = habbo.getHabboInfo().getCurrentRoom();
                    if (room != null) {
                        Emulator.getGameEnvironment().getRoomManager().logExit(habbo);

                        room.removeHabbo(habbo, true);

                        habbo.getHabboInfo().setCurrentRoom(null);
                    }

                    Emulator.getGameEnvironment().getRoomManager().enterRoom(habbo, gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId(), "", true);

                    habbo.getClient().sendResponse(new RoomForwardMessageComposer(gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId()));

                }
            }
        }

        return true;
    }
}
