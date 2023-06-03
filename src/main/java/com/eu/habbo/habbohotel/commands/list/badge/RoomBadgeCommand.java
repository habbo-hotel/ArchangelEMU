package com.eu.habbo.habbohotel.commands.list.badge;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.ServerMessage;

public class RoomBadgeCommand extends BaseBadgeCommand {
    public RoomBadgeCommand() {
        super("cmd_room_badge");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient == null)
            return true;

        if (params.length == 2) {
            String badge = params[1];

            if (!badge.isEmpty()) {
                ServerMessage message = createServerMessage(badge);

                gameClient.getHabbo().getRoomUnit().getRoom().getHabbos()
                        .forEach(habbo -> sendBadgeToClient(badge, message, habbo));
            }
            return true;
        }

        gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_roombadge.no_badge"), RoomChatMessageBubbles.ALERT);
        return true;
    }


}
