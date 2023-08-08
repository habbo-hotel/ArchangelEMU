package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.DiceValueMessageComposer;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InteractionOneWayGate extends RoomItem {
    private boolean walkable = false;

    public InteractionOneWayGate(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtraData("0");
    }

    public InteractionOneWayGate(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
        this.setExtraData("0");
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return this.getBaseItem().allowWalk();
    }

    @Override
    public boolean isWalkable() {
        return walkable;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        if (this.getExtraData().length() == 0) {
            this.setExtraData("0");
            this.setSqlUpdateNeeded(true);
        }

        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtraData());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onClick(final GameClient client, final Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (client != null) {
            RoomTile tileInfront = room.getLayout().getTileInFront(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()), this.getRotation());
            if (tileInfront == null)
                return;

            RoomTile currentLocation = room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());
            if (currentLocation == null)
                return;

            RoomUnit unit = client.getHabbo().getRoomUnit();
            if (unit == null)
                return;

            if (tileInfront.equals(unit.getCurrentPosition()) && !room.getRoomUnitManager().areRoomUnitsAt(currentLocation)) {
                List<Runnable> onSuccess = new ArrayList<>();
                List<Runnable> onFail = new ArrayList<>();

                onSuccess.add(() -> {
                    unit.setCanLeaveRoomByDoor(false);
                    walkable = this.getBaseItem().allowWalk();
                    RoomTile tile = room.getLayout().getTileInFront(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()), this.getRotation() + 4);
                    unit.walkTo(tile);
                    Emulator.getThreading().run(new RoomUnitWalkToLocation(unit, tile, room, onFail, onFail));

                    Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.WALKS_ON_FURNI, unit, room, new Object[]{this}), 500);
                });

                onFail.add(() -> {
                    unit.setCanLeaveRoomByDoor(true);
                    walkable = this.getBaseItem().allowWalk();
                    room.updateTile(currentLocation);
                    room.sendComposer(new DiceValueMessageComposer(this.getId(), 0).compose());
                    unit.removeOverrideTile(currentLocation);
                });

                walkable = true;
                room.updateTile(currentLocation);
                unit.addOverrideTile(currentLocation);
                unit.walkTo(currentLocation);
                Emulator.getThreading().run(new RoomUnitWalkToLocation(unit, currentLocation, room, onSuccess, onFail));
                room.sendComposer(new DiceValueMessageComposer(this.getId(), 1).compose());

                /*
                room.scheduledTasks.add(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        gate.roomUnitID = client.getHabbo().getRoomUnit().getId();
                        room.updateTile(gatePosition);
                        client.getHabbo().getRoomUnit().setGoalLocation(room.getLayout().getTileInFront(room.getLayout().getTile(InteractionOneWayGate.this.getX(), InteractionOneWayGate.this.getY()), InteractionOneWayGate.this.getRotation() + 4));
                    }
                });
                */
            }
        }
    }

    private void refresh(Room room) {
        this.setExtraData("0");
        room.sendComposer(new DiceValueMessageComposer(this.getId(), 0).compose());
        room.updateTile(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()));
    }

    @Override
    public void onPickUp(Room room) {
        this.setExtraData("0");
        this.refresh(room);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
        this.refresh(room);
    }

    @Override
    public void onPlace(Room room) {
        super.onPlace(room);
        this.refresh(room);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        this.refresh(room);
    }
}
