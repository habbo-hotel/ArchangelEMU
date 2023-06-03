package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.users.ChatMessageComposer;

public class PushCommand extends Command {
    public PushCommand() {
        super("cmd_push");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length != 2) return true;

        Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(params[1]);

        if (habbo == null) {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_push.not_found"), params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (habbo == gameClient.getHabbo()) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_push.push_self"), RoomChatMessageBubbles.ALERT);
            return true;
        }
        RoomTile tFront = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTileInFront(gameClient.getHabbo().getRoomUnit().getCurrentLocation(), gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue());

        if (tFront != null && tFront.isWalkable()) {
            if (tFront.getX() == habbo.getRoomUnit().getX() && tFront.getY() == habbo.getRoomUnit().getY()) {
                RoomTile tFrontTarget = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTileInFront(habbo.getRoomUnit().getCurrentLocation(), gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue());

                if (tFrontTarget != null && tFrontTarget.isWalkable()) {
                    if (gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getDoorTile() == tFrontTarget) {
                        gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.error.cmd_push.invalid"), params[1]));
                        return true;
                    }
                    habbo.getRoomUnit().setGoalLocation(tFrontTarget);
                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(
                            new ChatMessageComposer(
                                    new RoomChatMessage(
                                            replaceUser(getTextsValue("commands.succes.cmd_push.push"), params[1])
                                                    .replace("%gender_name%", (gameClient.getHabbo().getHabboInfo().getGender().equals(HabboGender.M) ? getTextsValue("gender.him") : getTextsValue("gender.her"))), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.NORMAL)).compose()
                    );
                }
            } else {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_push.cant_reach"), params[1]), RoomChatMessageBubbles.ALERT);
                return true;
            }
        }
        return true;
    }
}
