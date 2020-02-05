package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.RoomUnitVendingMachineAction;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import com.eu.habbo.util.pathfinding.Rotation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InteractionEffectVendingMachine extends InteractionDefault {
    public InteractionEffectVendingMachine(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtradata("0");
    }

    public InteractionEffectVendingMachine(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (client != null) {
            RoomTile tile = getSquareInFront(room.getLayout(), this);

            if (tile != null) {
                if (tile.equals(client.getHabbo().getRoomUnit().getCurrentLocation())) {
                    if (this.getExtradata().equals("0") || this.getExtradata().length() == 0) {
                        room.updateHabbo(client.getHabbo());
                        if (!client.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.SIT)) {
                            client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[Rotation.Calculate(client.getHabbo().getRoomUnit().getX(), client.getHabbo().getRoomUnit().getY(), this.getX(), this.getY())]);
                            client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
                            room.scheduledComposers.add(new RoomUserStatusComposer(client.getHabbo().getRoomUnit()).compose());
                        }
                        this.setExtradata("1");
                        room.scheduledComposers.add(new FloorItemUpdateComposer(this).compose());
                        Emulator.getThreading().run(this, 1000);
                        HabboItem instance = this;
                        Emulator.getThreading().run(new Runnable() {
                            @Override
                            public void run() {
                                room.giveEffect(client.getHabbo().getRoomUnit(), instance.getBaseItem().getRandomVendingItem(), 30);
                            }
                        });
                    }
                } else {
                    if (!tile.isWalkable() && tile.state != RoomTileState.SIT && tile.state != RoomTileState.LAY) {
                        for (RoomTile t : room.getLayout().getTilesAround(room.getLayout().getTile(this.getX(), this.getY()))) {
                            if (t != null && t.isWalkable()) {
                                tile = t;
                                break;
                            }
                        }
                    }

                    List<Runnable> onSuccess = new ArrayList<>();
                    List<Runnable> onFail = new ArrayList<>();

                    onSuccess.add(() -> Emulator.getThreading().run(() -> {
                        try {
                            this.onClick(client, room, objects);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 500));

                    client.getHabbo().getRoomUnit().setGoalLocation(tile);
                    Emulator.getThreading().run(new RoomUnitWalkToLocation(client.getHabbo().getRoomUnit(), tile, room, onSuccess, onFail));
                }
            }
        }
    }

}