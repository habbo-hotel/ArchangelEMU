package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.messages.outgoing.gang.TurfCaptureTimeLeftComposer;
import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class CaptureTurfAction implements Runnable {

    private final Room room;
    private final RoomTile roomTile;
    private final Habbo capturingHabbo;

    @Override
    public void run() {
        // Start capturing
        if (this.room.getRoomTurfManager().getCapturingHabbo() == null) {
            if (this.room.getRoomTurfManager().getCapturingHabbo() == this.capturingHabbo) {
                return;
            }
            this.room.getRoomTurfManager().startCapturing(this.capturingHabbo);
        }

        // Ensure user is in the same room
        if (this.capturingHabbo.getRoomUnit().getRoom() != this.room) {
            this.capturingHabbo.shout(Emulator.getTexts().getValue("roleplay.turf_capture.cancel"));
            this.room.getRoomTurfManager().stopCapturing();
            return;
        }

        // Ensure user is within 5 blocks of the turf banner
        if (this.capturingHabbo.getRoomUnit().getLastRoomTile() != this.roomTile) {
            this.capturingHabbo.shout(Emulator.getTexts().getValue("roleplay.turf_capture.cancel"));
            this.room.getRoomTurfManager().stopCapturing();
            return;
        }

        // Ensure no other users outside your gang are in the room
        Collection<Habbo> usersInRoom = this.room.getRoomUnitManager().getCurrentHabbos().values();

        this.room.getRoomTurfManager().setBlocked(false);

        for (Habbo user : usersInRoom) {
            if (user.getHabboRoleplayStats().getGang() != this.capturingHabbo.getHabboRoleplayStats().getGang()) {
                this.room.getRoomTurfManager().setBlocked(true);
                return;
            }
        }

        if (!this.room.getRoomTurfManager().isBlocked()) {
            this.room.getRoomTurfManager().setSecondsLeft(this.room.getRoomTurfManager().getSecondsLeft() - 1);
        }

        this.room.sendComposer(new TurfCaptureTimeLeftComposer(this.room).compose());

        Emulator.getThreading().run(this, 1000);
    }
}