package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InteractionAppleTree extends InteractionDefault {
    public static String INTERACTION_TYPE = "rp_apple_tree";

    public static int MAX_APPLE_COUNT = 5;
    public static int RESPAWN_APPLE_DELAY = 1000 * 25;
    public static int APPLE_HAND_ITEM_ID = 37;
    public static int APPLE_HAND_EXPIRE_DELAY = 1000 * 5;

    // Map to store the last interaction time for each user
    private static final Map<Integer, Long> lastInteraction = new HashMap<>();

    public InteractionAppleTree(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionAppleTree(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        int userId = client.getHabbo().getHabboInfo().getId();
        long currentTime = System.currentTimeMillis();

        String extraData = this.getExtraData();

        if (extraData == null) {
            this.setExtraData(String.valueOf(0));
            this.room.updateItemState(this);
        }

        int applesAte = Integer.parseInt(this.getExtraData());

        if (applesAte >= InteractionAppleTree.MAX_APPLE_COUNT) {
            client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.fruit_tree.nothing_found"));
            Emulator.getThreading().run(() -> {
                this.setExtraData(String.valueOf(0));
                this.room.updateItemState(this);
            }, InteractionAppleTree.RESPAWN_APPLE_DELAY);
            return;
        }

        this.setExtraData(String.valueOf(applesAte + 1));
        this.room.updateItemState(this);

        client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.fruit_tree.success"));
        client.getHabbo().getRoomUnit().setHandItem(InteractionAppleTree.APPLE_HAND_ITEM_ID);
        this.room.sendComposer(new CarryObjectMessageComposer(client.getHabbo().getRoomUnit()).compose());

        if (applesAte >= InteractionAppleTree.MAX_APPLE_COUNT) {
            client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.fruit_tree.nothing_found"));
            Emulator.getThreading().run(() -> {
                this.setExtraData(String.valueOf(0));
                this.room.updateItemState(this);
            }, InteractionAppleTree.RESPAWN_APPLE_DELAY);
            return;
        }

        Item appleBaseItem = Emulator.getGameEnvironment().getItemManager().getItemByInteractionType(InteractionApple.class);
        RoomItem appleRoomItem = Emulator.getGameEnvironment().getItemManager().createItem(client.getHabbo().getHabboInfo().getId(), appleBaseItem, 0, 0, "");
        client.getHabbo().getInventory().getItemsComponent().addItem(appleRoomItem);
        client.getHabbo().getClient().sendResponse(new UnseenItemsComposer(appleRoomItem));
        client.getHabbo().getClient().sendResponse(new FurniListInvalidateComposer());

        Emulator.getThreading().run(() -> {
            client.getHabbo().getRoomUnit().setHandItem(0);
            this.room.sendComposer(new CarryObjectMessageComposer(client.getHabbo().getRoomUnit()).compose());
        }, InteractionAppleTree.APPLE_HAND_EXPIRE_DELAY);
    }
}