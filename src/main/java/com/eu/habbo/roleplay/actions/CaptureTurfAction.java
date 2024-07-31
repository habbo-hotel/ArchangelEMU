package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionGuildFurni;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.interactions.InteractionTurfBanner;
import com.eu.habbo.roleplay.messages.outgoing.gang.TurfCaptureTimeLeftComposer;
import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class CaptureTurfAction implements Runnable {

    public static int CAPTURING_EFFECT_ID = 631;

    private final Room room;
    private final RoomTile roomTile;
    private final Habbo capturingHabbo;

    @Override
    public void run() {
        // Start capturing
        if (this.room.getRoomTurfManager().getCapturingHabbo() == null) {
            this.capturingHabbo.shout(Emulator.getTexts().getValue("roleplay.turf_capture.start"));
            this.capturingHabbo.getRoomUnit().giveEffect(CaptureTurfAction.CAPTURING_EFFECT_ID, -1);
            this.room.getRoomTurfManager().startCapturing(this.capturingHabbo);
        }

        // Ensure user is in the same room
        if (this.capturingHabbo.getRoomUnit().getRoom() != this.room) {
            this.room.getRoomTurfManager().stopCapturing();
        }

        // Ensure user is within 5 blocks of the turf banner
        if (this.capturingHabbo.getRoomUnit().getLastRoomTile() != this.roomTile) {
            this.room.getRoomTurfManager().stopCapturing();
            return;
        }

        if (this.room.getRoomTurfManager().isBlocked()) {
            this.capturingHabbo.shout(Emulator.getTexts().getValue("roleplay.turf_capture.cancel"));
            this.capturingHabbo.getRoomUnit().giveEffect(0, -1);
        }

        // Ensure no other users outside your gang are in the room
        Collection<Habbo> usersInRoom = this.room.getRoomUnitManager().getCurrentHabbos().values();

        this.room.getRoomTurfManager().setBlocked(false);

        for (Habbo user : usersInRoom) {
            if (user.getHabboRoleplayStats().getGang() != this.capturingHabbo.getHabboRoleplayStats().getGang()) {
                this.room.getRoomTurfManager().setBlocked(true);
                break;
            }
        }

        if (!this.room.getRoomTurfManager().isBlocked()) {
            this.room.getRoomTurfManager().setSecondsLeft(this.room.getRoomTurfManager().getSecondsLeft() - 1);
        }

        if (this.room.getRoomTurfManager().getSecondsLeft() <= 0) {
            this.room.getRoomInfo().setGuild(this.capturingHabbo.getHabboRoleplayStats().getGang());
            this.room.setNeedsUpdate(true);

            Collection<RoomItem> roomItems = this.room.getRoomItemManager().getCurrentItems().values();

            for (RoomItem item : roomItems) {
                if (item.getBaseItem().getInteractionType().getClass().isAssignableFrom(InteractionGuildFurni.class)) {
                    ((InteractionGuildFurni) item).setGuildId(1);
                }
            }

            this.capturingHabbo.shout(Emulator.getTexts().getValue("roleplay.turf_capture.success"));
            this.room.getRoomTurfManager().stopCapturing();
        }

        this.room.sendComposer(new TurfCaptureTimeLeftComposer(this.room).compose());

        if (this.room.getRoomTurfManager().getSecondsLeft() > 0) {
            Emulator.getThreading().run(this, 1000);
        }
    }
}