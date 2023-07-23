package com.eu.habbo.habbohotel.rooms.entities.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.IEventTriggers;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.DanceMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserChangeMessageComposer;
import com.eu.habbo.messages.outgoing.users.FigureUpdateComposer;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.Pair;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class RoomItem implements Runnable, IEventTriggers {
    @Getter
    private final int id;
    @Getter
    @Setter
    private int ownerId;
    @Getter
    @Setter
    private int roomId;
    @Getter
    private final Item baseItem;
    @Getter
    @Setter
    private String wallPosition;
    @Setter
    @Getter
    private short x;
    @Setter
    @Getter
    private short y;
    @Getter
    private double z;
    @Getter
    private int rotation;
    @Setter
    @Getter
    @Accessors(chain = true)
    private String extradata;
    @Getter
    private int limitedStack;
    @Getter
    private int limitedSells;
    private boolean needsUpdate = false;
    private boolean needsDelete = false;
    private boolean isFromGift = false;
    @SuppressWarnings("rawtypes")
    private static final Class[] TOGGLING_INTERACTIONS = new Class[]{
            InteractionGameTimer.class,
            InteractionWired.class,
            InteractionWiredHighscore.class,
            InteractionMultiHeight.class
    };

    public RoomItem(ResultSet set, Item baseItem) throws SQLException {
        this.id = set.getInt("id");
        this.ownerId = set.getInt(DatabaseConstants.USER_ID);
        this.roomId = set.getInt("room_id");
        this.baseItem = baseItem;
        this.wallPosition = set.getString("wall_pos");
        this.x = set.getShort("x");
        this.y = set.getShort("y");
        this.z = set.getDouble("z");
        this.rotation = set.getInt("rot");
        this.extradata = set.getString("extra_data").isEmpty() ? "0" : set.getString("extra_data");

        String ltdData = set.getString("limited_data");
        if (!ltdData.isEmpty()) {
            this.limitedStack = Integer.parseInt(set.getString("limited_data").split(":")[0]);
            this.limitedSells = Integer.parseInt(set.getString("limited_data").split(":")[1]);
        }
    }

    public RoomItem(int id, int ownerId, Item item, String extradata, int limitedStack, int limitedSells) {
        this.id = id;
        this.ownerId = ownerId;
        this.roomId = 0;
        this.baseItem = item;
        this.wallPosition = "";
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.rotation = 0;
        this.extradata = extradata.isEmpty() ? "0" : extradata;
        this.limitedSells = limitedSells;
        this.limitedStack = limitedStack;
    }

    public static RoomTile getSquareInFront(RoomLayout roomLayout, RoomItem item) {
        return roomLayout.getTileInFront(roomLayout.getTile(item.getX(), item.getY()), item.getRotation());
    }

    public void serializeFloorData(ServerMessage serverMessage) {
        try {
            serverMessage.appendInt(this.getId());
            serverMessage.appendInt(this.baseItem.getSpriteId());
            serverMessage.appendInt(this.x);
            serverMessage.appendInt(this.y);
            serverMessage.appendInt(this.getRotation());
            serverMessage.appendString(Double.toString(this.z));

            serverMessage.appendString((this.getBaseItem().getInteractionType().getType() == InteractionTrophy.class || this.getBaseItem().getInteractionType().getType() == InteractionCrackable.class || this.getBaseItem().getName().equalsIgnoreCase("gnome_box")) ? "1.0" : ((this.getBaseItem().allowWalk() || this.getBaseItem().allowSit() && this.roomId != 0) ? Item.getCurrentHeight(this) + "" : ""));
            //serverMessage.appendString( ? "1.0" : ((this.getBaseItem().allowWalk() || this.getBaseItem().allowSit() && this.roomId != 0) ? Item.getCurrentHeight(this) : ""));

        } catch (Exception e) {
            log.error("Caught exception", e);
        }
    }

    public void serializeExtradata(ServerMessage serverMessage) {
        if (this.isLimited()) {
            serverMessage.appendInt(this.getLimitedSells());
            serverMessage.appendInt(this.getLimitedStack());
        }
    }

    public void serializeWallData(ServerMessage serverMessage) {
        serverMessage.appendString(this.getId() + "");
        serverMessage.appendInt(this.baseItem.getSpriteId());
        serverMessage.appendString(this.wallPosition);

        if (this instanceof InteractionPostIt)
            serverMessage.appendString(this.extradata.split(" ")[0]);
        else
            serverMessage.appendString(this.extradata);
        serverMessage.appendInt(-1);
        serverMessage.appendInt(this.isUsable());
        serverMessage.appendInt(this.getOwnerId());
    }

    public int getGiftAdjustedId() {
        if (this.isFromGift) return -this.id;

        return this.id;
    }

    public void setZ(double z) {
        if (z > 9999 || z < -9999) return;
        this.z = z;
    }

    public void setRotation(int rotation) {
        this.rotation = (byte) (rotation % 8);
    }

    public boolean needsUpdate() {
        return this.needsUpdate;
    }

    public boolean needsDelete() {
        return needsDelete;
    }

    public void needsUpdate(boolean value) {
        this.needsUpdate = value;
    }

    public void needsDelete(boolean value) {
        this.needsDelete = value;
    }

    public boolean isLimited() {
        return this.limitedStack > 0;
    }

    public int getMaximumRotations() { return this.baseItem.getRotations(); }

    @Override
    public void run() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            if (this.needsDelete) {
                this.needsUpdate = false;
                this.needsDelete = false;

                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM items WHERE id = ?")) {
                    statement.setInt(1, this.getId());
                    statement.execute();
                }
            } else if (this.needsUpdate) {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE items SET user_id = ?, room_id = ?, wall_pos = ?, x = ?, y = ?, z = ?, rot = ?, extra_data = ?, limited_data = ? WHERE id = ?")) {
                    statement.setInt(1, this.ownerId);
                    statement.setInt(2, this.roomId);
                    statement.setString(3, this.wallPosition);
                    statement.setInt(4, this.x);
                    statement.setInt(5, this.y);
                    statement.setDouble(6, Math.max(-9999, Math.min(9999, Math.round(this.z * Math.pow(10, 6)) / Math.pow(10, 6))));
                    statement.setInt(7, this.rotation);
                    statement.setString(8, this instanceof InteractionGuildGate ? "" : this.getDatabaseExtraData());
                    statement.setString(9, this.limitedStack + ":" + this.limitedSells);
                    statement.setInt(10, this.id);
                    statement.execute();
                } catch (SQLException e) {
                    log.error("Caught SQL exception", e);
                    log.error("SQLException trying to save HabboItem: " + this);
                }

                this.needsUpdate = false;
            }

        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public abstract boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects);

    public abstract boolean isWalkable();

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (client != null && this.getBaseItem().getType() == FurnitureType.FLOOR) {
            if (objects != null && objects.length >= 2 && objects[1] instanceof WiredEffectType) {
                return;
            }

            if ((this.getBaseItem().getStateCount() > 1 && !(this instanceof InteractionDice) || !(this instanceof InteractionSpinningBottle))  || Arrays.asList(RoomItem.TOGGLING_INTERACTIONS).contains(this.getClass()) || (objects != null && objects.length == 1 && objects[0].equals("TOGGLE_OVERRIDE"))) {
                WiredHandler.handle(WiredTriggerType.STATE_CHANGED, client.getHabbo().getRoomUnit(), room, new Object[]{this});
            }
        }
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        /*if (objects != null && objects.length >= 1 && objects[0] instanceof InteractionWired)
            return;*/

        WiredHandler.handle(WiredTriggerType.WALKS_ON_FURNI, roomUnit, room, new Object[]{this});

        if(roomUnit instanceof RoomAvatar roomAvatar) {
            if ((this.getBaseItem().allowSit() || this.getBaseItem().allowLay()) && !roomAvatar.getDanceType().equals(DanceType.NONE)) {
                roomAvatar.setDanceType(DanceType.NONE);
                room.sendComposer(new DanceMessageComposer(roomAvatar).compose());
            }
        }

        if (!this.getBaseItem().getClothingOnWalk().isEmpty() && roomUnit.getPreviousLocation() != roomUnit.getGoalLocation() && roomUnit.getGoalLocation() == room.getLayout().getTile(this.x, this.y)) {
            Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

            if (habbo != null && habbo.getClient() != null) {
                String[] clothingKeys = Arrays.stream(this.getBaseItem().getClothingOnWalk().split("\\.")).map(k -> k.split("-")[0]).toArray(String[]::new);
                habbo.getHabboInfo().setLook(String.join(".", Arrays.stream(habbo.getHabboInfo().getLook().split("\\.")).filter(k -> !ArrayUtils.contains(clothingKeys, k.split("-")[0])).toArray(String[]::new)) + "." + this.getBaseItem().getClothingOnWalk());

                habbo.getClient().sendResponse(new FigureUpdateComposer(habbo));
                if (habbo.getRoomUnit().getRoom() != null) {
                    habbo.getRoomUnit().getRoom().sendComposer(new UserChangeMessageComposer(habbo).compose());
                }
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        if(objects != null && objects.length > 0) {
            WiredHandler.handle(WiredTriggerType.WALKS_OFF_FURNI, roomUnit, room, new Object[]{this});
        }
    }

    public abstract void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception;


    public void onPlace(Room room) {
        //TODO: IMPORTANT: MAKE THIS GENERIC. (HOLES, ICE SKATE PATCHES, BLACK HOLE, BUNNY RUN FIELD, FOOTBALL FIELD)
        Achievement roomDecoAchievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoFurniCount");
        Habbo owner = room.getRoomUnitManager().getRoomHabboById(this.getOwnerId());

        int furniCollecterProgress;
        if (owner == null) {
            furniCollecterProgress = AchievementManager.getAchievementProgressForHabbo(this.getOwnerId(), roomDecoAchievement);
        } else {
            furniCollecterProgress = owner.getHabboStats().getAchievementProgress(roomDecoAchievement);
        }

        int difference = room.getUserFurniCount(this.getOwnerId()) - furniCollecterProgress;
        if (difference > 0) {
            if (owner != null) {
                AchievementManager.progressAchievement(owner, roomDecoAchievement, difference);
            } else {
                AchievementManager.progressAchievement(this.getOwnerId(), roomDecoAchievement, difference);
            }
        }

        Achievement roomDecoUniqueAchievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoFurniTypeCount");

        int uniqueFurniCollecterProgress;
        if (owner == null) {
            uniqueFurniCollecterProgress = AchievementManager.getAchievementProgressForHabbo(this.getOwnerId(), roomDecoUniqueAchievement);
        } else {
            uniqueFurniCollecterProgress = owner.getHabboStats().getAchievementProgress(roomDecoUniqueAchievement);
        }

        int uniqueDifference = room.getUserUniqueFurniCount(this.getOwnerId()) - uniqueFurniCollecterProgress;
        if (uniqueDifference > 0) {
            if (owner != null) {
                AchievementManager.progressAchievement(owner, roomDecoUniqueAchievement, uniqueDifference);
            } else {
                AchievementManager.progressAchievement(this.getOwnerId(), roomDecoUniqueAchievement, uniqueDifference);
            }
        }
    }

    public void onPickUp(Room room) {
        if (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0) {
            RoomItem topItem2 = room.getRoomItemManager().getTopItemAt(this.getX(), this.getY(), this);
            int nextEffectM = 0;
            int nextEffectF = 0;

            if(topItem2 != null) {
                nextEffectM = topItem2.getBaseItem().getEffectM();
                nextEffectF = topItem2.getBaseItem().getEffectF();
            }

            for (Habbo habbo : room.getHabbosOnItem(this)) {
                if (this.getBaseItem().getEffectM() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.M) && habbo.getRoomUnit().getEffectId() == this.getBaseItem().getEffectM()) {
                    habbo.getRoomUnit().giveEffect(nextEffectM, -1);
                }

                if (this.getBaseItem().getEffectF() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.F) && habbo.getRoomUnit().getEffectId() == this.getBaseItem().getEffectF()) {
                    habbo.getRoomUnit().giveEffect(nextEffectF, -1);
                }
            }

            RoomTile tile = room.getLayout().getTile(this.getX(), this.getY());
            for (Bot bot : room.getRoomUnitManager().getBotsAt(tile)) {
                if (this.getBaseItem().getEffectM() > 0 && bot.getGender().equals(HabboGender.M) && bot.getRoomUnit().getEffectId() == this.getBaseItem().getEffectM()) {
                    bot.getRoomUnit().giveEffect(nextEffectM, -1);
                }

                if (this.getBaseItem().getEffectF() > 0 && bot.getGender().equals(HabboGender.F) && bot.getRoomUnit().getEffectId() == this.getBaseItem().getEffectF()) {
                    bot.getRoomUnit().giveEffect(nextEffectF, -1);
                }
            }
        }
    }

    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        if (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0) {
            RoomItem topItem2 = room.getRoomItemManager().getTopItemAt(oldLocation.getX(), oldLocation.getY(), this);
            int nextEffectM = 0;
            int nextEffectF = 0;

            if(topItem2 != null) {
                nextEffectM = topItem2.getBaseItem().getEffectM();
                nextEffectF = topItem2.getBaseItem().getEffectF();
            }

            List<Habbo> oldHabbos = new ArrayList<>();
            List<Habbo> newHabbos = new ArrayList<>();
            List<Bot> oldBots = new ArrayList<>();
            List<Bot> newBots = new ArrayList<>();

            for (RoomTile tile : room.getLayout().getTilesAt(oldLocation, this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation())) {
                oldHabbos.addAll(room.getRoomUnitManager().getHabbosAt(tile));
                oldBots.addAll(room.getRoomUnitManager().getBotsAt(tile));
            }

            for (RoomTile tile : room.getLayout().getTilesAt(oldLocation, this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation())) {
                newHabbos.addAll(room.getRoomUnitManager().getHabbosAt(tile));
                newBots.addAll(room.getRoomUnitManager().getBotsAt(tile));
            }

            oldHabbos.removeAll(newHabbos);
            oldBots.removeAll(newBots);

            int finalNextEffectM = nextEffectM;
            int finalNextEffectF = nextEffectF;

            oldHabbos.forEach(habbo -> {
                if (this.getBaseItem().getEffectM() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.M) && habbo.getRoomUnit().getEffectId() == this.getBaseItem().getEffectM()) {
                    habbo.getRoomUnit().giveEffect(finalNextEffectM, -1);
                }
                if (this.getBaseItem().getEffectF() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.F) && habbo.getRoomUnit().getEffectId() == this.getBaseItem().getEffectF()) {
                    habbo.getRoomUnit().giveEffect(finalNextEffectF, -1);
                }
            });

            newHabbos.forEach(habbo -> {
                if (this.getBaseItem().getEffectM() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.M) && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectM()) {
                    habbo.getRoomUnit().giveEffect(this.getBaseItem().getEffectM(), -1);
                }
                if (this.getBaseItem().getEffectF() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.F) && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectF()) {
                    habbo.getRoomUnit().giveEffect(this.getBaseItem().getEffectF(), -1);
                }
            });


            oldBots.forEach(bot -> {
                if (this.getBaseItem().getEffectM() > 0 && bot.getGender().equals(HabboGender.M) && bot.getRoomUnit().getEffectId() == this.getBaseItem().getEffectM()) {
                    bot.getRoomUnit().giveEffect(finalNextEffectM, -1);
                }

                if (this.getBaseItem().getEffectF() > 0 && bot.getGender().equals(HabboGender.F) && bot.getRoomUnit().getEffectId() == this.getBaseItem().getEffectF()) {
                    bot.getRoomUnit().giveEffect(finalNextEffectF, -1);
                }
            });

            newBots.forEach(bot -> {
                if (this.getBaseItem().getEffectM() > 0 && bot.getGender().equals(HabboGender.M) && bot.getRoomUnit().getEffectId() != this.getBaseItem().getEffectM()) {
                    bot.getRoomUnit().giveEffect(this.getBaseItem().getEffectM(), -1);
                }
                if (this.getBaseItem().getEffectF() > 0 && bot.getGender().equals(HabboGender.F) && bot.getRoomUnit().getEffectId() != this.getBaseItem().getEffectF()) {
                    bot.getRoomUnit().giveEffect(this.getBaseItem().getEffectF(), -1);
                }
            });
        }
    }

    public String getDatabaseExtraData() {
        return this.getExtradata();
    }

    @Override
    public String toString() {
        return "ID: " + this.id + ", BaseID: " + this.getBaseItem().getId() + ", X: " + this.x + ", Y: " + this.y + ", Z: " + this.z + ", Extradata: " + this.extradata;
    }

    public boolean allowWiredResetState() {
        return false;
    }

    public boolean isUsable() {
        return this.baseItem.getStateCount() > 1;
    }

    public boolean canStackAt(List<Pair<RoomTile, THashSet<RoomItem>>> itemsAtLocation) {
        return true;
    }

    public boolean isFromGift() {
        return isFromGift;
    }

    public void setFromGift(boolean fromGift) {
        isFromGift = fromGift;
    }

    public boolean invalidatesToRoomKick() { return false; }

    public List<RoomTile> getOccupyingTiles(RoomLayout layout) {
        List<RoomTile> tiles = new ArrayList<>();

        Rectangle rect = RoomLayout.getRectangle(this.getX(), this.getY(), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation());

        for (int i = rect.x; i < rect.x + rect.getWidth(); i++) {
            for (int j = rect.y; j < rect.y + rect.getHeight(); j++) {
                tiles.add(layout.getTile((short) i, (short) j));
            }
        }

        return tiles;
    }

    public RoomTile getOverrideGoalTile(RoomUnit unit, Room room, RoomTile tile) {
        return tile;
    }

    public RoomTileState getOverrideTileState(RoomTile tile, Room room) {
        return null;
    }

    public boolean canOverrideTile(RoomUnit unit, Room room, RoomTile tile) {
        return false;
    }

    public Rectangle getRectangle() {
        return RoomLayout.getRectangle(
                this.getX(),
                this.getY(),
                this.getBaseItem().getWidth(),
                this.getBaseItem().getLength(),
                this.getRotation());
    }

    public Rectangle getRectangle(int marginX, int marginY) {
        return RoomLayout.getRectangle(
                this.getX() - marginX,
                this.getY() - marginY,
                this.getBaseItem().getWidth() + (marginX * 2),
                this.getBaseItem().getLength() + (marginY * 2),
                this.getRotation());
    }
}
