package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.users.ChatMessageComposer;

public class SuperPullCommand extends Command {
    public SuperPullCommand() {
        super("cmd_super_pull");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length != 2) {
            return true;
        }

        Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(params[1]);

        if (habbo == null) {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_pull.not_found"), params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        } else if (habbo == gameClient.getHabbo()) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_pull.pull_self"), RoomChatMessageBubbles.ALERT);
            return true;
        } else {
            RoomTile tile = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTileInFront(gameClient.getHabbo().getRoomUnit().getCurrentLocation(), gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue());

            if (tile != null && tile.isWalkable()) {
                if (tile == gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getDoorTile()) {
                    gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.error.cmd_pull.invalid"), params[1]), RoomChatMessageBubbles.ALERT);
                    return true;
                }
                habbo.getRoomUnit().setGoalLocation(tile);
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new ChatMessageComposer(new RoomChatMessage(replaceUser(getTextsValue("commands.succes.cmd_pull.pull"), params[1]).replace("%gender_name%", (gameClient.getHabbo().getHabboInfo().getGender().equals(HabboGender.M) ? getTextsValue("gender.him") : getTextsValue("gender.her"))), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.NORMAL)).compose());
            }
        }
        return true;

    }
}
