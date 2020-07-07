package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.RoomUnitGiveHanditem;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import com.eu.habbo.util.pathfinding.Rotation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

public class InteractionVendingMachine extends InteractionDefault {
    public InteractionVendingMachine(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtradata("0");
    }

    public InteractionVendingMachine(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
        
        if (client == null) {
            return;
        }

        RoomTile tile = this.getRequiredTile(client.getHabbo(), room);

        if (tile != null && 
            tile.equals(client.getHabbo().getRoomUnit().getCurrentLocation())) {
                if (!this.getExtradata().equals("0") || this.getExtradata().length() != 0) {
                    return;
                }
                
                if (!client.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.SIT) && (!client.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.MOVE) || tile.equals(client.getHabbo().getRoomUnit().getGoal()))) {
                    room.updateHabbo(client.getHabbo());
                    this.rotateToMachine(client.getHabbo().getRoomUnit());
                    client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
                    room.scheduledComposers.add(new RoomUserStatusComposer(client.getHabbo().getRoomUnit()).compose());
                }

                super.onClick(client, room, new Object[]{"TOGGLE_OVERRIDE"});

                this.setExtradata("1");
                room.scheduledComposers.add(new FloorItemUpdateComposer(this).compose());

                this.runGiveItemThread(client, room);
        } else {
            if (!tile.isWalkable() && tile.state != RoomTileState.SIT && tile.state != RoomTileState.LAY) {
                for (RoomTile t : room.getLayout().getTilesAround(room.getLayout().getTile(this.getX(), this.getY()))) {
                    if (t != null && t.isWalkable()) {
                        tile = t;
                        break;
                    }
                }
            }

            RoomTile finalTile = tile;
            client.getHabbo().getRoomUnit().setGoalLocation(tile);

            Emulator.getThreading().run(new RoomUnitWalkToLocation(client.getHabbo().getRoomUnit(), tile, room, () -> {
                this.setExtradata("1");
                room.scheduledComposers.add(new FloorItemUpdateComposer(this).compose());

                try {
                    super.onClick(client, room, new Object[]{"TOGGLE_OVERRIDE"});
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Emulator.getThreading().run(() -> client.getHabbo().getRoomUnit().setMoveBlockingTask(Emulator.getThreading().run(() -> {
                    if (client.getHabbo().getRoomUnit().getCurrentLocation().equals(finalTile)) {
                        this.rotateToMachine(client.getHabbo().getRoomUnit());
                        room.sendComposer(new RoomUserStatusComposer(client.getHabbo().getRoomUnit()).compose());
                    }

                    this.runGiveItemThread(client, room);
                }, 300)), 250);
            }, null));
        }
    }

    private void runGiveItemThread(GameClient client, Room room) {
        try {
            ScheduledFuture thread = Emulator.getThreading().run(() -> {
                Emulator.getThreading().run(this, 1000);
                this.giveVendingMachineItem(client.getHabbo(), room);

                if (this.getBaseItem().getEffectM() > 0 && client.getHabbo().getHabboInfo().getGender() == HabboGender.M)
                    room.giveEffect(client.getHabbo(), this.getBaseItem().getEffectM(), -1);
                if (this.getBaseItem().getEffectF() > 0 && client.getHabbo().getHabboInfo().getGender() == HabboGender.F)
                    room.giveEffect(client.getHabbo(), this.getBaseItem().getEffectF(), -1);
            }, 500);

            if (thread.isDone()) {
                thread.get();
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        if (this.getExtradata().equals("1")) {
            this.setExtradata("0");
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
            if (room != null) {
                room.updateItem(this);
            }
        }
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    private void rotateToMachine(RoomUnit unit) {
        if (unit.getCurrentLocation().getState() != RoomTileState.OPEN) {
            // if sitting on a chair or laying on a bed, skip rotating altogether
            return;
        }

        RoomUserRotation rotation = RoomUserRotation.values()[Rotation.Calculate(unit.getX(), unit.getY(), this.getX(), this.getY())];
        boolean onlyHead = false;

        switch (unit.getBodyRotation()) {
            case NORTH_EAST:
                if (rotation.equals(RoomUserRotation.NORTH) || rotation.equals(RoomUserRotation.EAST))
                    onlyHead = true;
                break;

            case NORTH_WEST:
                if (rotation.equals(RoomUserRotation.NORTH) || rotation.equals(RoomUserRotation.WEST))
                    onlyHead = true;
                break;

            case SOUTH_EAST:
                if (rotation.equals(RoomUserRotation.SOUTH) || rotation.equals(RoomUserRotation.EAST))
                    onlyHead = true;
                break;

            case SOUTH_WEST:
                if (rotation.equals(RoomUserRotation.SOUTH) || rotation.equals(RoomUserRotation.WEST))
                    onlyHead = true;
                break;
        }

        if (onlyHead) {
            unit.setHeadRotation(rotation);
        } else {
            unit.setRotation(rotation);
        }
    }

    public void giveVendingMachineItem(Habbo habbo, Room room) {
        Emulator.getThreading().run(new RoomUnitGiveHanditem(habbo.getRoomUnit(), room, this.getBaseItem().getRandomVendingItem()));
    }

    public RoomTile getRequiredTile(Habbo habbo, Room room) {
        return getSquareInFront(room.getLayout(), this);
    }
}
