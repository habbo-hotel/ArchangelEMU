package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.RoomForwardMessageComposer;

public class SummonCommand extends Command {
    public SummonCommand() {
        super("cmd_summon");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getRoomUnit().getRoom() == null)
            return true;

        if (params.length < 2) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_summon.forgot_username"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Habbo habbo = getHabbo(params[1]);

        if (habbo == null) {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_summon.not_found"), params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (gameClient.getHabbo().getHabboInfo().getUsername().equals(habbo.getHabboInfo().getUsername())) {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.generic.cmd_summon.self"), params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (gameClient.getHabbo().getRoomUnit().getRoom() == habbo.getRoomUnit().getRoom()) {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.generic.cmd_summon.same_room"), params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Room room = habbo.getRoomUnit().getRoom();

        //WHY? Why not just roomManager -> leaveRoom()
        if (room != null) {
            Emulator.getGameEnvironment().getRoomManager().logExit(habbo);

            room.getRoomUnitManager().removeHabbo(habbo, true);
        }

        Emulator.getGameEnvironment().getRoomManager().enterRoom(habbo, gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId(), "", true);

        habbo.getClient().sendResponse(new RoomForwardMessageComposer(gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId()));

        gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_summon.summoned"), params[1]), RoomChatMessageBubbles.ALERT);

        habbo.alert(replaceUser(getTextsValue("commands.generic.cmd_summon.been_summoned"), gameClient.getHabbo().getHabboInfo().getUsername()));

        return true;
    }
}
