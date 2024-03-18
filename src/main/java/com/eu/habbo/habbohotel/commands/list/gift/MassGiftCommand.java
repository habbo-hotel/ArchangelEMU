package com.eu.habbo.habbohotel.commands.list.gift;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import gnu.trove.map.hash.THashMap;

public class MassGiftCommand extends BaseGiftCommand {
    public MassGiftCommand() {
        super("cmd_mass_gift");
    }
    @Override
    public boolean handle(final GameClient gameClient, String[] params) {
        if (params.length >= 2) {
            if (!validateGiftCommand(gameClient, params)) {
                return true;
            }

            final String finalMessage = getFinalMessage(params);

            THashMap<String, String> keys = new THashMap<>();
            keys.put("display", "BUBBLE");
            keys.put("image", "${image.library.url}notifications/gift.gif");
            keys.put("message", getTextsValue("generic.gift.received.anonymous"));
            ServerMessage giftNotificationMessage = new NotificationDialogMessageComposer(BubbleAlertKeys.RECEIVED_BADGE.getKey(), keys).compose();

            Emulator.getThreading().run(() -> Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values().forEach(habbo -> {
                        createGift(finalMessage, habbo, params);
                        habbo.getClient().sendResponse(giftNotificationMessage);
                    })
            );


            return true;
        }

        return false;
    }
}
