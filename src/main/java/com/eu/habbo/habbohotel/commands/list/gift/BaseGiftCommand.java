package com.eu.habbo.habbohotel.commands.list.gift;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseGiftCommand extends Command {
    public BaseGiftCommand(String name)
    {
        super(name);
    }

    protected boolean validateGiftCommand(GameClient gameClient, String[] params) {
        Integer itemId = getItemId(params);
        if (itemId == null) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_gift.not_a_number"), RoomChatMessageBubbles.ALERT);
            return false;
        }

        if (itemId <= 0) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_gift.not_a_number"), RoomChatMessageBubbles.ALERT);
            return false;
        }

        final Item baseItem = getBaseItem(itemId);

        if (baseItem == null) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_gift.not_found").replace("%itemid%", itemId + ""), RoomChatMessageBubbles.ALERT);
            return false;
        }

        return true;
    }

    protected String getFinalMessage(String[] params) {
        if (params.length > 2) {
            return IntStream.range(2, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());
        }

        return "";
    }

    protected void createGift(String finalMessage, Habbo habbo, String[] params) {
        HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(0, getBaseItem(params), 0, 0, "");

        Item giftItem = Emulator.getGameEnvironment().getItemManager().getItem((Integer) Emulator.getGameEnvironment().getCatalogManager().giftFurnis.values().toArray()[Emulator.getRandom().nextInt(Emulator.getGameEnvironment().getCatalogManager().giftFurnis.size())]);

        String extraData = "1\t" + item.getId() + "\t0\t0\t0\t" + finalMessage + "\t0\t0";

        Emulator.getGameEnvironment().getItemManager().createGift(habbo.getHabboInfo().getUsername(), giftItem, extraData, 0, 0);

        habbo.getClient().sendResponse(new FurniListInvalidateComposer());
    }


    protected Item getBaseItem(String[] params) {
        return Emulator.getGameEnvironment().getItemManager().getItem(getItemId(params));
    }

    protected Item getBaseItem(Integer itemId) {
        return Emulator.getGameEnvironment().getItemManager().getItem(itemId);
    }

    protected Integer getItemId(String[] params) {
        try {
            return Integer.parseInt(params[1]);
        } catch (Exception e) {
            return null;
        }
    }
}
