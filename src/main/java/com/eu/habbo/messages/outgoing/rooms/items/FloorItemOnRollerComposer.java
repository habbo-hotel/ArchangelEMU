package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FloorItemOnRollerComposer extends MessageComposer {
    // THIS IS WRONG SlideObjectBundleMessageComposer
    private final HabboItem item;
    private final HabboItem roller;
    private final RoomTile oldLocation;
    private final double oldZ;
    private final RoomTile newLocation;
    private final double newZ;
    private final double heightOffset;
    private final Room room;

    public FloorItemOnRollerComposer(HabboItem item, HabboItem roller, RoomTile newLocation, double heightOffset, Room room) {
        this.item = item;
        this.roller = roller;
        this.newLocation = newLocation;
        this.heightOffset = heightOffset;
        this.room = room;
        this.oldLocation = null;
        this.oldZ = -1;
        this.newZ = -1;
    }


    @Override
    protected ServerMessage composeInternal() {
        short oldX = this.item.getX();
        short oldY = this.item.getY();

        this.response.init(Outgoing.slideObjectBundleMessageComposer);
        this.response.appendInt(this.oldLocation != null ? this.oldLocation.getX() : this.item.getX());
        this.response.appendInt(this.oldLocation != null ? this.oldLocation.getY() : this.item.getY());
        this.response.appendInt(this.newLocation.getX());
        this.response.appendInt(this.newLocation.getY());
        this.response.appendInt(1);
        this.response.appendInt(this.item.getId());
        this.response.appendString(Double.toString(this.oldLocation != null ? this.oldZ : this.item.getZ()));
        this.response.appendString(Double.toString(this.oldLocation != null ? this.newZ : (this.item.getZ() + this.heightOffset)));
        this.response.appendInt(this.roller != null ? this.roller.getId() : -1);

        if(this.oldLocation == null) {
            this.item.onMove(this.room, this.room.getLayout().getTile(this.item.getX(), this.item.getY()), this.newLocation);
            this.item.setX(this.newLocation.getX());
            this.item.setY(this.newLocation.getY());
            this.item.setZ(this.item.getZ() + this.heightOffset);
            this.item.needsUpdate(true);

            //TODO This is bad
            //
            THashSet<RoomTile> tiles = this.room.getLayout().getTilesAt(this.room.getLayout().getTile(oldX, oldY), this.item.getBaseItem().getWidth(), this.item.getBaseItem().getLength(), this.item.getRotation());
            tiles.addAll(this.room.getLayout().getTilesAt(this.room.getLayout().getTile(this.item.getX(), this.item.getY()), this.item.getBaseItem().getWidth(), this.item.getBaseItem().getLength(), this.item.getRotation()));
            this.room.updateTiles(tiles);
            //this.room.sendComposer(new UpdateStackHeightComposer(oldX, oldY, this.room.getStackHeight(oldX, oldY, true)).compose());
            //
            //this.room.updateHabbosAt(RoomLayout.getRectangle(this.item.getX(), this.item.getY(), this.item.getBaseItem().getWidth(), this.item.getBaseItem().getLength(), this.item.getRotation()));
        }

        return this.response;
    }
}
