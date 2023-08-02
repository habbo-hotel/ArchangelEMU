package com.eu.habbo.threading.runnables.hopper;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HopperActionOne implements Runnable {
    private final RoomItem teleportOne;
    private final Room room;
    private final GameClient client;

    @Override
    public void run() {
        //this.client.getHabbo().getRoomUnit().setGoalLocation(this.teleportOne.getX(), this.teleportOne.getY());
        this.client.getHabbo().getRoomUnit().setRotation(RoomRotation.values()[(this.teleportOne.getRotation() + 4) % 8]);
        this.client.getHabbo().getRoomUnit().addStatus(RoomUnitStatus.MOVE, this.teleportOne.getCurrentPosition().getX() + "," + this.teleportOne.getCurrentPosition().getY() + "," + this.teleportOne.getCurrentZ());
        this.room.scheduledComposers.add(new UserUpdateComposer(this.client.getHabbo().getRoomUnit()).compose());
        this.client.getHabbo().getRoomUnit().setLocation(this.room.getLayout().getTile(this.teleportOne.getCurrentPosition().getX(), this.teleportOne.getCurrentPosition().getY()));
        this.client.getHabbo().getRoomUnit().setCurrentZ(this.teleportOne.getCurrentZ());

        Emulator.getThreading().run(() -> {
            HopperActionOne.this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
            HopperActionOne.this.room.sendComposer(new UserUpdateComposer(HopperActionOne.this.client.getHabbo().getRoomUnit()).compose());
        }, 750);

        Emulator.getThreading().run(new HopperActionTwo(this.teleportOne, this.room, this.client), 1250);
    }
}
