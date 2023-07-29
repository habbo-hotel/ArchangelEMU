package com.eu.habbo.threading.runnables.hopper;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionCostumeHopper;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.threading.runnables.HabboItemNewState;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class HopperActionThree implements Runnable {
    private final RoomItem teleportOne;
    private final Room room;
    private final GameClient client;
    private final int targetRoomId;
    private final int targetItemId;


    @Override
    public void run() {
        RoomItem targetTeleport;
        Room targetRoom = this.room;

        if (this.teleportOne.getRoomId() != this.targetRoomId) {
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(this.client.getHabbo(), this.room, false);
            targetRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(this.targetRoomId);
            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), targetRoom.getRoomInfo().getId(), "", false);
        }

        targetTeleport = targetRoom.getRoomItemManager().getRoomItemById(this.targetItemId);

        if (targetTeleport == null) {
            this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
            this.client.getHabbo().getRoomUnit().setCanWalk(true);
            return;
        }

        targetTeleport.setExtraData("2");
        targetRoom.updateItem(targetTeleport);
        this.client.getHabbo().getRoomUnit().setLocation(this.room.getLayout().getTile(targetTeleport.getCurrentPosition().getX(), targetTeleport.getCurrentPosition().getY()));
        this.client.getHabbo().getRoomUnit().setPreviousLocationZ(targetTeleport.getCurrentZ());
        this.client.getHabbo().getRoomUnit().setCurrentZ(targetTeleport.getCurrentZ());
        this.client.getHabbo().getRoomUnit().setRotation(RoomRotation.values()[targetTeleport.getRotation() % 8]);
        this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
        targetRoom.sendComposer(new UserUpdateComposer(this.client.getHabbo().getRoomUnit()).compose());

        Emulator.getThreading().run(new HabboItemNewState(this.teleportOne, this.room, "0"), 500);
        Emulator.getThreading().run(new HopperActionFour(targetTeleport, targetRoom, this.client), 500);

        if (targetTeleport instanceof InteractionCostumeHopper) {
            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("CostumeHopper"));
        }
    }
}
