package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.users.ChatMessageComposer;

public class PushCommand extends Command {
    public PushCommand() {
        super("cmd_push", Emulator.getTexts().getValue("commands.keys.cmd_push").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (params.length != 2) return true;

        Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(params[1]);

        if (habbo == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_push.not_found").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (habbo == gameClient.getHabbo()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_push.push_self"), RoomChatMessageBubbles.ALERT);
            return true;
        }
        RoomTile tFront = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTileInFront(gameClient.getHabbo().getRoomUnit().getCurrentLocation(), gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue());

        if (tFront != null && tFront.isWalkable() && tFront.x == habbo.getRoomUnit().getX() && tFront.y == habbo.getRoomUnit().getY()) {
            RoomTile tFrontTarget = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTileInFront(habbo.getRoomUnit().getCurrentLocation(), gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue());

            if (tFrontTarget != null && tFrontTarget.isWalkable()) {
                if (gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getDoorTile() == tFrontTarget) {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_push.invalid").replace("%username%", params[1]));
                    return true;
                }
                habbo.getRoomUnit().setGoalLocation(tFrontTarget);
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new ChatMessageComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_push.push").replace("%user%", params[1]).replace("%gender_name%", (gameClient.getHabbo().getHabboInfo().getGender().equals(HabboGender.M) ? Emulator.getTexts().getValue("gender.him") : Emulator.getTexts().getValue("gender.her"))), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.NORMAL)).compose());
            }
        } else {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_push.cant_reach").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }


        return true;
    }
}
