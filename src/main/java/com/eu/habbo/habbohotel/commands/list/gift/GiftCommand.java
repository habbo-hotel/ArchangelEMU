package com.eu.habbo.habbohotel.commands.list.gift;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import gnu.trove.map.hash.THashMap;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GiftCommand extends BaseGiftCommand {
    public GiftCommand() {
        super("cmd_gift");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length >= 3) {
            String username = params[1];
            int itemId;

            try {
                itemId = Integer.parseInt(params[2]);
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_gift.not_a_number"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (itemId <= 0) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_gift.not_a_number"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(itemId);

            if (baseItem == null) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_gift.not_found").replace("%itemid%", itemId + ""), RoomChatMessageBubbles.ALERT);
                return true;
            }

            HabboInfo habboInfo = HabboManager.getOfflineHabboInfo(username);

            if (habboInfo == null) {
                gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.error.cmd_gift.user_not_found"), username), RoomChatMessageBubbles.ALERT);
                return true;
            }

            String message = params.length > 3 ? IntStream.range(3, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining()) : "";

            HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(0, baseItem, 0, 0, "");
            Item giftItem = Emulator.getGameEnvironment().getItemManager().getItem((Integer) Emulator.getGameEnvironment().getCatalogManager().giftFurnis.values().toArray()[Emulator.getRandom().nextInt(Emulator.getGameEnvironment().getCatalogManager().giftFurnis.size())]);

            String extraData = "1\t" + item.getId() + "\t0\t0\t0\t" + message + "\t0\t0";

            Emulator.getGameEnvironment().getItemManager().createGift(username, giftItem, extraData, 0, 0);

            gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.succes.cmd_gift"), username).replace("%itemname%", item.getBaseItem().getName()), RoomChatMessageBubbles.ALERT);

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(habboInfo.getId());

            if (habbo != null) {
                habbo.getClient().sendResponse(new FurniListInvalidateComposer());

                THashMap<String, String> keys = new THashMap<>();
                keys.put("display", "BUBBLE");
                keys.put("image", "${image.library.url}notifications/gift.gif");
                keys.put("message", getTextsValue("generic.gift.received.anonymous"));
                habbo.getClient().sendResponse(new NotificationDialogMessageComposer(BubbleAlertKeys.RECEIVED_BADGE.getKey(), keys));
            }
            return true;
        }

        return false;
    }
}
