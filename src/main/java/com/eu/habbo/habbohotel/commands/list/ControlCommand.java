package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class ControlCommand extends Command {
    public ControlCommand() {
        super("cmd_control");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            if (params.length == 2) {
                Habbo target = getHabbo(params[1]);

                if (target == null) {
                    gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_control.not_found"), params[1]), RoomChatMessageBubbles.ALERT);
                    return true;
                }

                if (target == gameClient.getHabbo()) {
                    gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_control.not_self"), RoomChatMessageBubbles.ALERT);
                    return true;
                }

                Habbo oldHabbo = (Habbo) gameClient.getHabbo().getRoomUnit().getCacheable().remove("control");

                if (oldHabbo != null) {
                    oldHabbo.getRoomUnit().getCacheable().remove("controller");
                    gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_control.stopped"), oldHabbo.getHabboInfo().getUsername()), RoomChatMessageBubbles.ALERT);
                }
                gameClient.getHabbo().getRoomUnit().getCacheable().put("control", target);
                target.getRoomUnit().getCacheable().put("controller", gameClient.getHabbo());
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_control.controlling"), params[1]), RoomChatMessageBubbles.ALERT);
            } else {
                Object habbo = gameClient.getHabbo().getRoomUnit().getCacheable().get("control");

                if (habbo != null) {
                    gameClient.getHabbo().getRoomUnit().getCacheable().remove("control");

                    gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_control.stopped"), ((Habbo) habbo).getHabboInfo().getUsername()), RoomChatMessageBubbles.ALERT);
                }
            }
            return true;
        }

        return true;
    }
}
