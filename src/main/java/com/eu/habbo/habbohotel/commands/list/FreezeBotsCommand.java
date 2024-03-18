package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatMessageBubbles;

public class FreezeBotsCommand extends Command {
    public FreezeBotsCommand() {
        super("cmd_freeze_bots");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getRoomUnit().getRoom() == null) {
            return false;
        }

        if (gameClient.getHabbo().getHabboInfo().getId() == gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getOwnerInfo().getId()
                || gameClient.getHabbo().hasPermissionRight(Permission.ACC_ANYROOMOWNER)) {
            gameClient.getHabbo().getRoomUnit().getRoom().setAllowBotsWalk(!gameClient.getHabbo().getRoomUnit().getRoom().isAllowBotsWalk());
            gameClient.getHabbo().whisper(gameClient.getHabbo().getRoomUnit().getRoom().isAllowBotsWalk() ? getTextsValue("commands.succes.cmd_freeze_bots.unfrozen") : getTextsValue("commands.succes.cmd_freeze_bots.frozen"), RoomChatMessageBubbles.ALERT);
        } else {
            gameClient.getHabbo().whisper(getTextsValue("generic.cannot_do_that"), RoomChatMessageBubbles.ALERT);
        }

        return true;

    }
}
