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

import java.util.Map;

public class MassBadgeCommand extends BaseBadgeCommand {
    public MassBadgeCommand() {
        super("cmd_massbadge", Emulator.getTexts().getValue("commands.keys.cmd_massbadge").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 2) {
            String badge;

            badge = params[1];

            if (!badge.isEmpty()) {
                ServerMessage message = createServerMessage(badge);

                for (Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet()) {
                    Habbo habbo = set.getValue();

                    sendBadgeToClient(badge, message, habbo);
                }
                return true;
            }
        }
        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_massbadge.no_badge"), RoomChatMessageBubbles.ALERT);
        return true;


    }


}
