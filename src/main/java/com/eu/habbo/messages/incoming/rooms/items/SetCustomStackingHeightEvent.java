package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.HeightMapUpdateMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.UpdateStackHeightTileHeightComposer;
import gnu.trove.set.hash.THashSet;

public class SetCustomStackingHeightEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        if (this.client.getHabbo().getRoomUnit().getRoom() == null)
            return;

        if (this.client.getHabbo().getHabboInfo().getId() == this.client.getHabbo().getRoomUnit().getRoom().getRoomInfo().getOwnerInfo().getId() || this.client.getHabbo().getRoomUnit().getRoom().getRoomRightsManager().hasRights(this.client.getHabbo())) {
            RoomItem item = this.client.getHabbo().getRoomUnit().getRoom().getRoomItemManager().getRoomItemById(itemId);

            if (item instanceof InteractionStackHelper) {
                Room room = this.client.getHabbo().getRoomUnit().getRoom();
                RoomTile itemTile = room.getLayout().getTile(item.getX(), item.getY());
                double stackerHeight = this.packet.readInt();

                THashSet<RoomTile> tiles = room.getLayout().getTilesAt(itemTile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                if (stackerHeight == -100) {
                    for (RoomTile tile : tiles) {
                        double stackheight = room.getStackHeight(tile.getX(), tile.getY(), false, item) * 100;
                        if (stackheight > stackerHeight) {
                            stackerHeight = stackheight;
                        }
                    }
                } else {
                    stackerHeight = Math.min(Math.max(stackerHeight, itemTile.getZ() * 100.0), Room.MAXIMUM_FURNI_HEIGHT * 100);
                }

                double height = 0;
                if (stackerHeight >= 0) {
                    height = stackerHeight / 100.0D;
                }

                for (RoomTile tile : tiles) {
                    tile.setStackHeight(height);
                }

                item.setZ(height);
                item.setExtradata((int) (height * 100) + "");
                item.needsUpdate(true);
                this.client.getHabbo().getRoomUnit().getRoom().updateItem(item);
                this.client.getHabbo().getRoomUnit().getRoom().updateTiles(tiles);
                this.client.getHabbo().getRoomUnit().getRoom().sendComposer(new HeightMapUpdateMessageComposer (room, tiles).compose());
                this.client.getHabbo().getRoomUnit().getRoom().sendComposer(new UpdateStackHeightTileHeightComposer(item, (int) ((height) * 100)).compose());
            }
        }
    }
}
