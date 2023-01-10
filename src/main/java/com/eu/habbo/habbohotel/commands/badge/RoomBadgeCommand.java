package com.eu.habbo.habbohotel.commands.badge;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.inventory.BadgesComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import com.eu.habbo.messages.outgoing.users.BadgeReceivedComposer;
import gnu.trove.map.hash.THashMap;

public class RoomBadgeCommand extends BaseBadgeCommand {
    public RoomBadgeCommand() {
        super("cmd_roombadge", Emulator.getTexts().getValue("commands.keys.cmd_roombadge").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient == null)
            return true;

        if (params.length == 2) {
            String badge;

            badge = params[1];

            if (!badge.isEmpty()) {
                ServerMessage message = createServerMessage(badge);

                for (Habbo habbo : gameClient.getHabbo().getRoomUnit().getRoom().getHabbos()) {
                    sendBadgeToClient(badge, message, habbo);
                }
            }
            return true;
        }

        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_roombadge.no_badge"), RoomChatMessageBubbles.ALERT);
        return true;
    }


}
