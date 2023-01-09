package com.eu.habbo.threading.runnables.teleport;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleportTile;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TeleportActionOne implements Runnable {
    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    @Override
    public void run() {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != this.room)
            return;

        int delay = 500;

        if (this.currentTeleport instanceof InteractionTeleportTile) {
            delay = 0;
        }

        if (this.client.getHabbo().getRoomUnit().getCurrentLocation() != this.room.getLayout().getTile(this.currentTeleport.getX(), this.currentTeleport.getY())) {
            this.client.getHabbo().getRoomUnit().setLocation(this.room.getLayout().getTile(this.currentTeleport.getX(), this.currentTeleport.getY()));
            this.client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[(this.currentTeleport.getRotation() + 4) % 8]);
            this.client.getHabbo().getRoomUnit().setStatus(RoomUnitStatus.MOVE, this.currentTeleport.getX() + "," + this.currentTeleport.getY() + "," + this.currentTeleport.getZ());
            this.room.scheduledComposers.add(new UserUpdateComposer(this.client.getHabbo().getRoomUnit()).compose());
            this.client.getHabbo().getRoomUnit().setLocation(this.room.getLayout().getTile(this.currentTeleport.getX(), this.currentTeleport.getY()));
        }

        Emulator.getThreading().run(new TeleportActionTwo(this.currentTeleport, this.room, this.client), delay);
    }
}
