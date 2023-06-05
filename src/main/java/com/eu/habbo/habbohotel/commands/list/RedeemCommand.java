package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItems;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;

public class RedeemCommand extends Command {
    public RedeemCommand() {
        super("cmd_redeem");
    }

    @Override
    public boolean handle(final GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom().getActiveTradeForHabbo(gameClient.getHabbo()) != null)
            return false;
        ArrayList<HabboItem> items = new ArrayList<>();

        int credits = 0;
        int pixels = 0;

        TIntIntMap points = new TIntIntHashMap();

        for (HabboItem item : gameClient.getHabbo().getInventory().getItemsComponent().getItemsAsValueCollection()) {
            if ((item.getBaseItem().getName().startsWith("CF_") || item.getBaseItem().getName().startsWith("CFC_") || item.getBaseItem().getName().startsWith("DF_") || item.getBaseItem().getName().startsWith("PF_")) && item.getUserId() == gameClient.getHabbo().getHabboInfo().getId()) {
                items.add(item);
                if ((item.getBaseItem().getName().startsWith("CF_") || item.getBaseItem().getName().startsWith("CFC_")) && !item.getBaseItem().getName().contains("_diamond_")) {
                    try {
                        credits += Integer.parseInt(item.getBaseItem().getName().split("_")[1]);
                    } catch (Exception ignored) {
                    }

                } else if (item.getBaseItem().getName().startsWith("PF_")) {
                    try {
                        pixels += Integer.parseInt(item.getBaseItem().getName().split("_")[1]);
                    } catch (Exception ignored) {
                    }
                } else if (item.getBaseItem().getName().startsWith("DF_")) {
                    int pointsType;
                    int pointsAmount;

                    pointsType = Integer.parseInt(item.getBaseItem().getName().split("_")[1]);
                    pointsAmount = Integer.parseInt(item.getBaseItem().getName().split("_")[2]);

                    points.adjustOrPutValue(pointsType, pointsAmount, pointsAmount);
                } else if (item.getBaseItem().getName().startsWith("CF_diamond_")) {
                    int pointsType;
                    int pointsAmount;

                    pointsType = 5;
                    pointsAmount = Integer.parseInt(item.getBaseItem().getName().split("_")[2]);

                    points.adjustOrPutValue(pointsType, pointsAmount, pointsAmount);
                }
            }
        }

        TIntObjectHashMap<HabboItem> deleted = new TIntObjectHashMap<>();
        items.forEach(item -> {
            gameClient.getHabbo().getInventory().getItemsComponent().removeHabboItem(item);
            deleted.put(item.getId(), item);
        });

        Emulator.getThreading().run(new QueryDeleteHabboItems(deleted));

        gameClient.sendResponse(new FurniListInvalidateComposer());
        gameClient.getHabbo().giveCredits(credits);
        gameClient.getHabbo().givePixels(pixels);

        final String[] message = {getTextsValue("generic.redeemed")};

        message[0] += getTextsValue("generic.credits");
        message[0] += ": " + credits;

        if (pixels > 0) {
            message[0] += ", " + getTextsValue("generic.pixels");
            message[0] += ": " + pixels;
        }

        if (!points.isEmpty()) {
            points.forEachEntry((a, b) -> {
                gameClient.getHabbo().givePoints(a, b);
                message[0] += " ," + getTextsValue("seasonal.name." + a) + ": " + b;
                return true;
            });
        }

        gameClient.getHabbo().whisper(message[0], RoomChatMessageBubbles.ALERT);

        return true;
    }
}
