package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.outgoing.rooms.items.ObjectAddMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;

public class RespawnItemAction implements Runnable {

    private final RoomItem roomItem;

    public RespawnItemAction(RoomItem roomItem) {
        this.roomItem = roomItem;

        this.roomItem.getRoom().getRoomItemManager().removeRoomItem(this.roomItem);
        this.roomItem.getRoom().sendComposer(new RemoveFloorItemComposer(this.roomItem, true).compose());
        Emulator.getThreading().run(this, Emulator.getConfig().getInt("roleplay.mining.respawn_delay", 5000));
    }
    @Override
    public void run() {
        this.roomItem.getRoom().getRoomItemManager().addRoomItem(this.roomItem);
        this.roomItem.getRoom().sendComposer(new ObjectAddMessageComposer(this.roomItem, this.roomItem.getRoom().getFurniOwnerNames().get(this.roomItem.getOwnerInfo().getId())).compose());
    }


}
