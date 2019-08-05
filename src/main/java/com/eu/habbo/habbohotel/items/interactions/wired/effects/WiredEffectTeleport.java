package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.threading.runnables.RoomUnitTeleport;
import com.eu.habbo.threading.runnables.SendRoomUnitEffectComposer;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WiredEffectTeleport extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.TELEPORT;

    protected List<HabboItem> items;

    public WiredEffectTeleport(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.items = new ArrayList<>();
    }

    public WiredEffectTeleport(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new ArrayList<>();
    }

    public static void teleportUnitToTile(RoomUnit roomUnit, RoomTile tile) {
        if (roomUnit == null || tile == null)
            return;

        Room room = roomUnit.getRoom();

        if (room == null)
            return;

        // makes a temporary effect
        roomUnit.getRoom().unIdle(roomUnit.getRoom().getHabbo(roomUnit));
        room.sendComposer(new RoomUserEffectComposer(roomUnit, 4).compose());
        Emulator.getThreading().run(new SendRoomUnitEffectComposer(room, roomUnit), WiredHandler.TELEPORT_DELAY + 1000);

        if (tile.state == RoomTileState.INVALID || tile.state == RoomTileState.BLOCKED) {
            RoomTile alternativeTile = null;
            List<RoomTile> optionalTiles = room.getLayout().getTilesAround(tile);

            Collections.reverse(optionalTiles);
            for (RoomTile optionalTile : optionalTiles) {
                if (optionalTile.state != RoomTileState.INVALID && optionalTile.state != RoomTileState.BLOCKED) {
                    alternativeTile = optionalTile;
                }
            }

            if (alternativeTile != null) {
                tile = alternativeTile;
            }
        }

        Emulator.getThreading().run(new RoomUnitTeleport(roomUnit, room, tile.x, tile.y, tile.getStackHeight() + (tile.state == RoomTileState.SIT ? -0.5 : 0), roomUnit.getEffectId()), WiredHandler.TELEPORT_DELAY);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<>();

        for (HabboItem item : this.items) {
            if (item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for (HabboItem item : items) {
            this.items.remove(item);
        }
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (HabboItem item : this.items)
            message.appendInt(item.getId());

        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        if (this.requiresTriggeringUser()) {
            List<Integer> invalidTriggers = new ArrayList<>();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>() {
                @Override
                public boolean execute(InteractionWiredTrigger object) {
                    if (!object.isTriggeredByRoomUnit()) {
                        invalidTriggers.add(object.getId());
                    }
                    return true;
                }
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers) {
                message.appendInt(i);
            }
        } else {
            message.appendInt(0);
        }
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient) {
        packet.readInt();
        packet.readString();

        this.items.clear();

        int count = packet.readInt();

        for (int i = 0; i < count; i++) {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(packet.readInt()));
        }

        this.setDelay(packet.readInt());

        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        THashSet<HabboItem> items = new THashSet<>();

        for (HabboItem item : this.items) {
            if (item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for (HabboItem item : items) {
            this.items.remove(item);
        }

        if (!this.items.isEmpty()) {
            int i = Emulator.getRandom().nextInt(this.items.size()) + 1;
            int j = 1;

            int tryCount = 0;
            while (tryCount < this.items.size()) {
                tryCount++;
                HabboItem item = this.items.get((tryCount - 1 + i) % this.items.size());

                teleportUnitToTile(roomUnit, room.getLayout().getTile(item.getX(), item.getY()));
                break;

            }
            return true;
        }

        return false;
    }

    @Override
    public String getWiredData() {
        StringBuilder wiredData = new StringBuilder(this.getDelay() + "\t");

        if (this.items != null && !this.items.isEmpty()) {
            for (HabboItem item : this.items) {
                wiredData.append(item.getId()).append(";");
            }
        }

        return wiredData.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items = new ArrayList<>();
        String[] wiredData = set.getString("wired_data").split("\t");

        if (wiredData.length >= 1) {
            this.setDelay(Integer.valueOf(wiredData[0]));
        }
        if (wiredData.length == 2) {
            if (wiredData[1].contains(";")) {
                for (String s : wiredData[1].split(";")) {
                    HabboItem item = room.getHabboItem(Integer.valueOf(s));

                    if (item != null)
                        this.items.add(item);
                }
            }
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean requiresTriggeringUser() {
        return true;
    }

    @Override
    protected long requiredCooldown() {
        return 0;
    }
}
