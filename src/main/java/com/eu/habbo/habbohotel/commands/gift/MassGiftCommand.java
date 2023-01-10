package com.eu.habbo.habbohotel.commands.gift;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import gnu.trove.map.hash.THashMap;

import java.util.Map;

public class MassGiftCommand extends BaseGiftCommand {
    public MassGiftCommand() {
        super("cmd_massgift", Emulator.getTexts().getValue("commands.keys.cmd_massgift").split(";"));
    }

    @Override
    public boolean handle(final GameClient gameClient, String[] params) {
        if (params.length >= 2) {
            if(!validateGiftCommand(gameClient, params)){
                return true;
            }

            final String finalMessage = getFinalMessage(params);

            THashMap<String, String> keys = new THashMap<>();
            keys.put("display", "BUBBLE");
            keys.put("image", "${image.library.url}notifications/gift.gif");
            keys.put("message", Emulator.getTexts().getValue("generic.gift.received.anonymous"));
            ServerMessage giftNotificationMessage = new NotificationDialogMessageComposer(BubbleAlertKeys.RECEIVED_BADGE.getKey(), keys).compose();

            Emulator.getThreading().run(() -> {
                for (Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet()) {
                    Habbo habbo = set.getValue();

                    createGift(finalMessage, habbo, params);
                    habbo.getClient().sendResponse(giftNotificationMessage);
                }
            });


            return true;
        }

        return false;
    }
}
