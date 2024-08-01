package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionGuildFurni;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
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
        if (!validateCapturingConditions()) {
            return;
        }

        Collection<Habbo> usersInRoom = this.room.getRoomUnitManager().getCurrentHabbos().values();

        boolean isContested = isRoomContested(usersInRoom);

        if (isContested) {
            handleContestedRoom();
        }

        if (!isContested) {
            progressCapture();
        }

        if (this.room.getRoomTurfManager().getSecondsLeft() <= 0) {
            captureSuccessful();
            return;
        }

        scheduleNextRun();
        handleTurfOwnerRegainTime(usersInRoom);
    }

    private boolean validateCapturingConditions() {
        if (this.room.getRoomTurfManager().getCapturingHabbo() == null) {
            return initializeCapture();
        }

        if (this.capturingHabbo.getRoomUnit().getRoom() != this.room) {
            this.room.getRoomTurfManager().stopCapturing();
            return false;
        }

        if (!isWithinCaptureRange()) {
            this.room.getRoomTurfManager().stopCapturing();
            return false;
        }

        if (this.capturingHabbo.getHabboRoleplayStats().isDead()) {
            this.room.getRoomTurfManager().stopCapturing();
            return false;
        }

        return true;
    }

    private boolean initializeCapture() {
        if (this.capturingHabbo.getHabboRoleplayStats().getGang() == null) {
            this.capturingHabbo.whisper(Emulator.getTexts().getValue("roleplay.turf_capture.no_gang"));
            return false;
        }
        this.capturingHabbo.shout(Emulator.getTexts().getValue("roleplay.turf_capture.start"));
        this.capturingHabbo.getRoomUnit().giveEffect(CaptureTurfAction.CAPTURING_EFFECT_ID, -1);
        this.room.getRoomTurfManager().startCapturing(this.capturingHabbo);
        return true;
    }

    private boolean isWithinCaptureRange() {
        return Math.abs(this.capturingHabbo.getRoomUnit().getCurrentPosition().getX() - this.roomTile.getX()) <= 4 &&
                Math.abs(this.capturingHabbo.getRoomUnit().getCurrentPosition().getY() - this.roomTile.getY()) <= 4;
    }

    private boolean isRoomContested(Collection<Habbo> usersInRoom) {
        for (Habbo user : usersInRoom) {
            if (user == this.capturingHabbo) {
                continue;
            }
            if (user.getHabboRoleplayStats().getGang() == null ||
                    user.getHabboRoleplayStats().getGang() != this.capturingHabbo.getHabboRoleplayStats().getGang()) {
                return true;
            }
        }
        return false;
    }

    private void handleContestedRoom() {
        this.room.getRoomTurfManager().pauseCapturing();
    }

    private void progressCapture() {
        this.room.getRoomTurfManager().resumeCapturing();
        this.room.getRoomTurfManager().decrementSecondsLeft();
    }

    private void scheduleNextRun() {
        this.room.sendComposer(new TurfCaptureTimeLeftComposer(this.room).compose());
        Emulator.getThreading().run(this, 1000);
    }

    private void handleTurfOwnerRegainTime(Collection<Habbo> usersInRoom) {
        if (this.room.getRoomInfo().getGuild() == null) {
            return;
        }

        for (Habbo user : usersInRoom) {
            if (user.getHabboRoleplayStats().getGang() == this.room.getRoomInfo().getGuild()) {
                this.room.getRoomTurfManager().regainTime();
                break;
            }
        }
    }
    private void captureSuccessful() {
        this.room.getRoomInfo().setGuild(this.capturingHabbo.getHabboRoleplayStats().getGang());
        this.room.setNeedsUpdate(true);

        Collection<RoomItem> roomItems = this.room.getRoomItemManager().getCurrentItems().values();

        for (RoomItem item : roomItems) {
            if (item.getBaseItem().getInteractionType().getClass().isAssignableFrom(InteractionGuildFurni.class)) {
                ((InteractionGuildFurni) item).setGuildId(this.capturingHabbo.getHabboRoleplayStats().getGang().getId());
            }
        }

        this.capturingHabbo.shout(Emulator.getTexts().getValue("roleplay.turf_capture.success"));
        this.room.getRoomTurfManager().stopCapturing();
    }

}