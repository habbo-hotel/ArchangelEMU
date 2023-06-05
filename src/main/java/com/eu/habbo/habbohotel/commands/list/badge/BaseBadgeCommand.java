package com.eu.habbo.habbohotel.commands.list.badge;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.inventory.BadgesComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import com.eu.habbo.messages.outgoing.users.BadgeReceivedComposer;
import gnu.trove.map.hash.THashMap;

public abstract class BaseBadgeCommand extends Command {
    public BaseBadgeCommand(String name)
    {
        super(name);
    }

    protected ServerMessage createServerMessage(String badge) {
        THashMap<String, String> keys = new THashMap<>();
        keys.put("display", "BUBBLE");
        keys.put("image", "${image.library.url}album1584/" + badge + ".gif");
        keys.put("message", getTextsValue("commands.generic.cmd_badge.received"));
        return new NotificationDialogMessageComposer(BubbleAlertKeys.RECEIVED_BADGE.getKey(), keys).compose();
    }

    protected void sendBadgeToClient(String badge, ServerMessage message, Habbo habbo) {
        if (habbo.isOnline() && habbo.getInventory() != null && habbo.getInventory().getBadgesComponent() != null && !habbo.getInventory().getBadgesComponent().hasBadge(badge)) {
            HabboBadge b = BadgesComponent.createBadge(badge, habbo);

            habbo.getClient().sendResponse(new BadgeReceivedComposer(b));
            habbo.getClient().sendResponse(message);
        }
    }

    protected String replaceUserAndBadge(String input, String name, String badge) {
        return replaceUser(input, name).replace("%badge%", badge);
    }
}
