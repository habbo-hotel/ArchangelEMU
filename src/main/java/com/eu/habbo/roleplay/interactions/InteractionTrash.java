package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionGymEquipment;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTrash extends InteractionGymEquipment {

    public static String INTERACTION_TYPE = "rp_trash";

    public static int RESPAWN_DELAY = 1000 * 25;
    public static int LOOT_EFFECT_ID = 37;
    public static int LOOT_EFFECT_DELAY = 1000 * 3;

    public InteractionTrash(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionTrash(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {

        String extraData = this.getExtraData();

        if (extraData == null) {
            this.setExtraData(String.valueOf(0));
            this.room.updateItemState(this);
        }

        boolean trashEmpty = Integer.parseInt(this.getExtraData()) == 0;

        if (trashEmpty) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.trash_can.nothing_found"));
            Emulator.getThreading().run(() -> {
                this.setExtraData(String.valueOf(1));
                this.room.updateItemState(this);
            }, InteractionTrash.RESPAWN_DELAY);
            return;
        }

        this.setExtraData(String.valueOf(0));
        this.room.updateItemState(this);

        client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.trash_can.success"));
        client.getHabbo().getRoomUnit().setHandItem(InteractionTrash.LOOT_EFFECT_ID);
        this.room.sendComposer(new CarryObjectMessageComposer(client.getHabbo().getRoomUnit()).compose());

        Item appleBaseItem = Emulator.getGameEnvironment().getItemManager().getItemByInteractionType(InteractionApple.class);
        RoomItem appleRoomItem = Emulator.getGameEnvironment().getItemManager().createItem(client.getHabbo().getHabboInfo().getId(), appleBaseItem, 0, 0, "");
        client.getHabbo().getInventory().getItemsComponent().addItem(appleRoomItem);
        client.getHabbo().getClient().sendResponse(new UnseenItemsComposer(appleRoomItem));
        client.getHabbo().getClient().sendResponse(new FurniListInvalidateComposer());

        Emulator.getThreading().run(() -> {
            client.getHabbo().getRoomUnit().setHandItem(1);
            this.room.sendComposer(new CarryObjectMessageComposer(client.getHabbo().getRoomUnit()).compose());
        }, InteractionTrash.LOOT_EFFECT_DELAY);
    }
}