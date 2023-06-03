package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class DisconnectCommand extends Command {
    public DisconnectCommand() {
        super("cmd_disconnect");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 2) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_disconnect.forgot_username"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (params[1].equalsIgnoreCase(gameClient.getHabbo().getHabboInfo().getUsername())) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_disconnect.disconnect_self"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Habbo target = getHabbo(params[1]);

        if (target == null) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_disconnect.user_offline"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (target.getHabboInfo().getPermissionGroup().getLevel() > gameClient.getHabbo().getHabboInfo().getPermissionGroup().getLevel()) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_disconnect.higher_rank"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        target.getClient().getChannel().close();

        gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_disconnect.disconnected"), params[1]), RoomChatMessageBubbles.ALERT);
        return true;
    }
}
