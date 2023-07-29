package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUsersComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserRemoveMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class TeleportInteraction extends Thread {

    private final Room room;
    private final GameClient client;
    private final RoomItem teleportOne;
    private int state;
    private Room targetRoom;
    private RoomItem teleportTwo;

    @Deprecated
    public TeleportInteraction(Room room, GameClient client, RoomItem teleportOne) {
        this.room = room;
        this.client = client;
        this.teleportOne = teleportOne;
        this.teleportTwo = null;
        this.targetRoom = null;
        this.state = 1;
    }

    @Override
    public void run() {
        try {
            if (this.state == 5) {
                this.teleportTwo.setExtraData("1");
                this.targetRoom.updateItem(this.teleportTwo);
                this.room.updateItem(this.teleportOne);
                RoomTile tile = RoomItem.getSquareInFront(this.room.getLayout(), this.teleportTwo);
                if (tile != null) {
                    this.client.getHabbo().getRoomUnit().setGoalLocation(tile);
                }
                Emulator.getThreading().run(this.teleportTwo, 500);
                Emulator.getThreading().run(this.teleportOne, 500);
            } else if (this.state == 4) {
                int[] data = Emulator.getGameEnvironment().getItemManager().getTargetTeleportRoomId(this.teleportOne);
                if (data.length == 2 && data[0] != 0) {
                    if (this.room.getRoomInfo().getId() == data[0]) {
                        this.targetRoom = this.room;
                        this.teleportTwo = this.room.getRoomItemManager().getRoomItemById(data[1]);

                        if (this.teleportTwo == null) {
                            this.teleportTwo = this.teleportOne;
                        }
                    } else {
                        this.targetRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(data[0]);
                        this.teleportTwo = this.targetRoom.getRoomItemManager().getRoomItemById(data[1]);
                    }
                } else {
                    this.targetRoom = this.room;
                    this.teleportTwo = this.teleportOne;
                }

                this.teleportOne.setExtraData("2");
                this.teleportTwo.setExtraData("2");

                if (this.room != this.targetRoom) {
                    Emulator.getGameEnvironment().getRoomManager().logExit(this.client.getHabbo());
                    this.room.getRoomUnitManager().removeHabbo(this.client.getHabbo(), true);
                    Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), this.targetRoom);
                }

                this.client.getHabbo().getRoomUnit().setRotation(RoomRotation.values()[this.teleportTwo.getRotation()]);
                this.client.getHabbo().getRoomUnit().setLocation(this.room.getLayout().getTile(this.teleportTwo.getCurrentPosition().getX(), this.teleportTwo.getCurrentPosition().getY()));
                this.client.getHabbo().getRoomUnit().setCurrentZ(this.teleportTwo.getCurrentZ());

                this.room.sendComposer(new UserRemoveMessageComposer(this.client.getHabbo().getRoomUnit()).compose());
                this.targetRoom.sendComposer(new UserRemoveMessageComposer(this.client.getHabbo().getRoomUnit()).compose());
                this.targetRoom.sendComposer(new RoomUsersComposer(this.client.getHabbo()).compose());
                this.targetRoom.sendComposer(new UserUpdateComposer(this.client.getHabbo().getRoomUnit()).compose());
                this.targetRoom.sendComposer(new AvatarEffectMessageComposer(this.client.getHabbo().getRoomUnit()).compose());
                this.room.updateItem(this.teleportOne);
                this.targetRoom.updateItem(this.teleportTwo);

                this.state = 5;

                Emulator.getThreading().run(this, 500);
            } else if (this.state == 3) {
                this.teleportOne.setExtraData("0");
                this.room.updateItem(this.teleportOne);
                this.state = 4;
                Emulator.getThreading().run(this, 500);
            } else if (this.state == 2) {
                this.client.getHabbo().getRoomUnit().setGoalLocation(this.room.getLayout().getTile(this.teleportOne.getCurrentPosition().getX(), this.teleportOne.getCurrentPosition().getY()));
                this.client.getHabbo().getRoomUnit().setRotation(RoomRotation.values()[this.newRotation(this.teleportOne.getRotation())]);
                this.client.getHabbo().getRoomUnit().addStatus(RoomUnitStatus.MOVE, this.teleportOne.getCurrentPosition().getX() + "," + this.teleportOne.getCurrentPosition().getY() + "," + this.teleportOne.getCurrentZ());
                //room.sendComposer(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()));

                this.state = 3;

                Emulator.getThreading().run(this, 500);
            } else if (this.state == 1) {
                RoomTile loc = RoomItem.getSquareInFront(this.room.getLayout(), this.teleportOne);

                if (this.client.getHabbo().getRoomUnit().getCurrentPosition().getX() == loc.getX()) {
                    if (this.client.getHabbo().getRoomUnit().getCurrentPosition().getY() == loc.getY()) {
                        this.teleportOne.setExtraData("1");
                        this.room.updateItem(this.teleportOne);
                        this.state = 2;

                        Emulator.getThreading().run(this, 250);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Caught exception", e);
        }
    }

    private int newRotation(int rotation) {
        if (rotation == 4)
            return 0;
        if (rotation == 6)
            return 2;
        else
            return rotation + 4;
    }
}
