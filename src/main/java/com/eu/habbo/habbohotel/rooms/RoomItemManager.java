package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagPole;
import com.eu.habbo.habbohotel.items.interactions.pets.*;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredBlob;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import com.eu.habbo.messages.outgoing.rooms.HeightMapUpdateMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.ItemRemoveMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.ObjectAddMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureBuildheightEvent;
import com.eu.habbo.plugin.events.furniture.FurniturePickedUpEvent;
import com.eu.habbo.plugin.events.furniture.FurniturePlacedEvent;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
@Getter
public class RoomItemManager {
    private final Room room;
    private final RoomWiredManager wiredManager;
    private final ConcurrentHashMap<Integer, RoomItem> currentItems;
    private final ConcurrentHashMap<Integer, RoomItem> floorItems;
    private final ConcurrentHashMap<Integer, RoomItem> wallItems;
    private final HashSet<ICycleable> cycleTasks;
    private final HashMap<Integer, InteractionNest> nests;
    private final HashMap<Integer, InteractionPetDrink> petDrinks;
    private final HashMap<Integer, InteractionPetFood> petFoods;
    private final HashMap<Integer, InteractionPetToy> petToys;
    private final HashMap<Integer, InteractionRoller> rollers;
    private final HashMap<Integer, InteractionGameScoreboard> gameScoreboards;
    private final HashMap<Integer, InteractionGameGate> gameGates;
    private final HashMap<Integer, InteractionGameTimer> gameTimers;
    private final HashMap<Integer, InteractionBattleBanzaiTeleporter> banzaiTeleporters;
    private final HashMap<Integer, InteractionFreezeExitTile> freezeExitTile;
    private final HashMap<Integer, RoomItem> undefinedSpecials;

    public RoomItemManager(Room room) {
        this.room = room;
        this.currentItems = new ConcurrentHashMap<>();
        this.wiredManager = new RoomWiredManager();

        this.floorItems = new ConcurrentHashMap<>();
        this.wallItems = new ConcurrentHashMap<>();

        this.cycleTasks = new HashSet<>(0);

        this.nests = new HashMap<>(0);
        this.petDrinks = new HashMap<>(0);
        this.petFoods = new HashMap<>(0);
        this.petToys = new HashMap<>(0);

        this.rollers = new HashMap<>(0);

        this.gameScoreboards = new HashMap<>(0);
        this.gameGates = new HashMap<>(0);
        this.gameTimers = new HashMap<>(0);

        this.banzaiTeleporters = new HashMap<>(0);
        this.freezeExitTile = new HashMap<>(0);

        this.undefinedSpecials = new HashMap<>(0);
    }

    public synchronized void load(Connection connection) {
        this.loadItems(connection);
    }

    private synchronized void loadItems(Connection connection) {
        this.currentItems.clear();
        this.wiredManager.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE room_id = ?")) {
            statement.setInt(1, this.room.getRoomInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    RoomItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(set);

                    if (item != null) {
                        this.addRoomItem(item);
                    }
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error("Caught Exception", e);
        }
    }

    public void addRoomItem(RoomItem item) {
        if (this.currentItems.size() > Room.MAXIMUM_FURNI) {
            log.error("Room ID: {} has exceeded the furniture limit ({} > {}).", this.room.getRoomInfo().getId(), this.currentItems.size(), Room.MAXIMUM_FURNI);
        }

        synchronized (this.currentItems) {
            try {
                this.currentItems.put(item.getId(), item);
                this.sortItem(item);
                item.setRoomId(this.room.getRoomInfo().getId());
            } catch (Exception ignored) {

            }
        }
    }

    public void removeHabboItem(RoomItem item) {
        if (item != null) {

            RoomItem i;
            synchronized (this.currentItems) {
                i = this.currentItems.remove(item.getId());
            }

            if (i != null) {
                if (item instanceof ICycleable) {
                    this.removeCycleTask((ICycleable) item);
                }

                if (item instanceof InteractionWired wired) {
                    this.wiredManager.removeWired(wired);
                } else if (item instanceof InteractionBattleBanzaiTeleporter) {
                    this.removeBanzaiTeleporter((InteractionBattleBanzaiTeleporter) item);
                } else if (item instanceof InteractionRoller) {
                    this.removeRoller((InteractionRoller) item);
                } else if (item instanceof InteractionGameScoreboard) {
                    this.removeScoreboard((InteractionGameScoreboard) item);
                } else if (item instanceof InteractionGameGate) {
                    this.removeGameGate((InteractionGameGate) item);
                } else if (item instanceof InteractionGameTimer) {
                    this.removeGameTimer((InteractionGameTimer) item);
                } else if (item instanceof InteractionFreezeExitTile) {
                    this.removeFreezeExitTile((InteractionFreezeExitTile) item);
                } else if (item instanceof InteractionNest) {
                    this.removeNest((InteractionNest) item);
                } else if (item instanceof InteractionPetDrink) {
                    this.removePetDrink((InteractionPetDrink) item);
                } else if (item instanceof InteractionPetFood) {
                    this.removePetFood((InteractionPetFood) item);
                } else if (item instanceof InteractionPetToy) {
                    this.removePetToy((InteractionPetToy) item);
                } else if (item instanceof InteractionPetTree) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionPetTrampoline) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionMoodLight) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionPyramid) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionMusicDisc) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionBattleBanzaiSphere) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionTalkingFurniture) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionWaterItem) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionWater) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionMuteArea) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionTagPole) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionTagField) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionJukeBox) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionPetBreedingNest) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionBlackHole) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionWiredHighscore) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionStickyPole) {
                    this.removeUndefined(item);
                } else if (item instanceof WiredBlob) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionTent) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionSnowboardSlope) {
                    this.removeUndefined(item);
                } else if (item instanceof InteractionBuildArea) {
                    this.removeUndefined(item);
                }
            }
        }
    }

    public HashSet<RoomItem> getItemsOfType(Class<? extends RoomItem> type) {
        return this.currentItems.values().stream()
                .filter(item -> type.equals(item.getClass()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public FurnitureMovementError canPlaceFurnitureAt(RoomItem item, Habbo habbo, RoomTile tile, int rotation) {
        if (this.currentItems.size() >= Room.MAXIMUM_FURNI) {
            return FurnitureMovementError.MAX_ITEMS;
        }

        if (item instanceof InteractionMoodLight && !this.getItemsOfType(InteractionMoodLight.class).isEmpty()) {
            return FurnitureMovementError.MAX_DIMMERS;
        }
        if (item instanceof InteractionJukeBox && !this.getItemsOfType(InteractionJukeBox.class).isEmpty()) {
            return FurnitureMovementError.MAX_SOUNDFURNI;
        }

        if (tile == null || tile.getState() == RoomTileState.INVALID) {
            return FurnitureMovementError.INVALID_MOVE;
        }

        rotation %= 8;
        if (this.room.hasRights(habbo) || this.room.getGuildRightLevel(habbo).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS) || habbo.hasRight(Permission.ACC_MOVEROTATE)) {
            return FurnitureMovementError.NONE;
        }

        if (habbo.getHabboStats().isRentingSpace()) {
            RoomItem rentSpace = this.currentItems.get(habbo.getHabboStats().getRentedItemId());

            if (rentSpace != null) {
                if (!RoomLayout.squareInSquare(RoomLayout.getRectangle(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), RoomLayout.getRectangle(tile.getX(), tile.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation))) {
                    return FurnitureMovementError.NO_RIGHTS;
                } else {
                    return FurnitureMovementError.NONE;
                }
            }
        }

        for (RoomItem area : this.getItemsOfType(InteractionBuildArea.class)) {
            if (((InteractionBuildArea) area).inSquare(tile) && ((InteractionBuildArea) area).isBuilder(habbo.getHabboInfo().getUsername())) {
                return FurnitureMovementError.NONE;
            }
        }

        return FurnitureMovementError.NO_RIGHTS;
    }

    public FurnitureMovementError placeFloorItemAt(RoomItem item, RoomTile tile, int rotation, Habbo owner) {
        boolean pluginHelper = false;
        if (Emulator.getPluginManager().isRegistered(FurniturePlacedEvent.class, true)) {
            FurniturePlacedEvent event = Emulator.getPluginManager().fireEvent(new FurniturePlacedEvent(item, owner, tile));

            if (event.isCancelled()) {
                return FurnitureMovementError.CANCEL_PLUGIN_PLACE;
            }

            pluginHelper = event.hasPluginHelper();
        }

        THashSet<RoomTile> occupiedTiles = this.room.getLayout().getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);

        FurnitureMovementError fits = this.furnitureFitsAt(item, tile, rotation);

        if (!fits.equals(FurnitureMovementError.NONE) && !pluginHelper) {
            return fits;
        }

        double height = tile.getStackHeight();

        for (RoomTile tile2 : occupiedTiles) {
            double sHeight = tile2.getStackHeight();
            if (sHeight > height) {
                height = sHeight;
            }
        }

        if (Emulator.getPluginManager().isRegistered(FurnitureBuildheightEvent.class, true)) {
            FurnitureBuildheightEvent event = Emulator.getPluginManager().fireEvent(new FurnitureBuildheightEvent(item, owner, 0.00, height));
            if (event.hasChangedHeight()) {
                height = event.getUpdatedHeight();
            }
        }

        item.setZ(height);
        item.setX(tile.getX());
        item.setY(tile.getY());
        item.setRotation(rotation);
        item.needsUpdate(true);

        this.addRoomItem(item);

        item.onPlace(this.room);
        this.room.updateTiles(occupiedTiles);

        this.room.sendComposer(new ObjectAddMessageComposer(item, this.room.getFurniOwnerName(item.getUserId())).compose());

        for (RoomTile t : occupiedTiles) {
            this.room.updateHabbosAt(t);
            this.room.updateBotsAt(t);
        }

        Emulator.getThreading().run(item);
        return FurnitureMovementError.NONE;
    }

    public void pickUpItem(RoomItem item, Habbo picker) {
        if (item == null)
            return;

        if (Emulator.getPluginManager().isRegistered(FurniturePickedUpEvent.class, true)) {
            Event furniturePickedUpEvent = new FurniturePickedUpEvent(item, picker);
            Emulator.getPluginManager().fireEvent(furniturePickedUpEvent);

            if (furniturePickedUpEvent.isCancelled())
                return;
        }

        this.removeHabboItem(item);
        item.onPickUp(this.room);
        item.setRoomId(0);
        item.needsUpdate(true);

        if (item.getBaseItem().getType() == FurnitureType.FLOOR) {
            this.room.sendComposer(new RemoveFloorItemComposer(item).compose());

            THashSet<RoomTile> updatedTiles = new THashSet<>();
            Rectangle rectangle = RoomLayout.getRectangle(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

            for (short x = (short) rectangle.x; x < rectangle.x + rectangle.getWidth(); x++) {
                for (short y = (short) rectangle.y; y < rectangle.y + rectangle.getHeight(); y++) {
                    double stackHeight = this.room.getStackHeight(x, y, false);
                    RoomTile tile = this.room.getLayout().getTile(x, y);

                    if (tile != null) {
                        tile.setStackHeight(stackHeight);
                        updatedTiles.add(tile);
                    }
                }
            }
            this.room.sendComposer(new HeightMapUpdateMessageComposer(this.room, updatedTiles).compose());
            this.room.updateTiles(updatedTiles);
            updatedTiles.forEach(tile -> {
                this.room.updateHabbosAt(tile);
                this.room.updateBotsAt(tile.getX(), tile.getY());
            });
        } else if (item.getBaseItem().getType() == FurnitureType.WALL) {
            this.room.sendComposer(new ItemRemoveMessageComposer(item).compose());
        }

        Habbo habbo = (picker != null && picker.getHabboInfo().getId() == item.getId() ? picker : Emulator.getGameServer().getGameClientManager().getHabbo(item.getUserId()));
        if (habbo != null) {
            habbo.getInventory().getItemsComponent().addItem(item);
            habbo.getClient().sendResponse(new UnseenItemsComposer(item));
            habbo.getClient().sendResponse(new FurniListInvalidateComposer());
        }
        Emulator.getThreading().run(item);
    }

    public FurnitureMovementError furnitureFitsAt(RoomItem item, RoomTile tile, int rotation) {
        return furnitureFitsAt(item, tile, rotation, true);
    }

    public FurnitureMovementError furnitureFitsAt(RoomItem item, RoomTile tile, int rotation, boolean checkForUnits) {
        if (!this.room.getLayout().fitsOnMap(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation))
            return FurnitureMovementError.INVALID_MOVE;

        if (item instanceof InteractionStackHelper) return FurnitureMovementError.NONE;


        THashSet<RoomTile> occupiedTiles = this.room.getLayout().getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);
        for (RoomTile t : occupiedTiles) {
            if (t.getState() == RoomTileState.INVALID) return FurnitureMovementError.INVALID_MOVE;
            if (!Emulator.getConfig().getBoolean("wired.place.under", false) || (Emulator.getConfig().getBoolean("wired.place.under", false) && !item.isWalkable() && !item.getBaseItem().allowSit() && !item.getBaseItem().allowLay())) {
                if (checkForUnits) {
                    if (this.room.getRoomUnitManager().hasHabbosAt(t)) return FurnitureMovementError.TILE_HAS_HABBOS;
                }
                if (checkForUnits) {
                    if (!this.room.getRoomUnitManager().getBotsAt(t).isEmpty()) return FurnitureMovementError.TILE_HAS_BOTS;
                }
                if (checkForUnits) {
                    if (this.room.getRoomUnitManager().hasPetsAt(t)) return FurnitureMovementError.TILE_HAS_PETS;
                }
            }
        }

        Optional<RoomItem> stackHelper = this.room.getItemsAt(tile).stream().filter(InteractionStackHelper.class::isInstance).findAny();

        List<Pair<RoomTile, THashSet<RoomItem>>> tileFurniList = new ArrayList<>();
        for (RoomTile t : occupiedTiles) {
            tileFurniList.add(Pair.create(t, this.room.getItemsAt(t)));

            RoomItem topItem = this.room.getTopItemAt(t.getX(), t.getY(), item);
            if (topItem != null && !topItem.getBaseItem().allowStack() && !t.getAllowStack()) {
                return FurnitureMovementError.CANT_STACK;
            }

            if ((stackHelper.isPresent() && item.getBaseItem().getInteractionType().getType() == InteractionWater.class) || topItem != null && (topItem.getBaseItem().getInteractionType().getType() == InteractionWater.class && (item.getBaseItem().getInteractionType().getType() == InteractionWater.class || item.getBaseItem().getInteractionType().getType() != InteractionWaterItem.class))) {
                return FurnitureMovementError.CANT_STACK;
            }
        }

        if (!item.canStackAt(tileFurniList)) {
            return FurnitureMovementError.CANT_STACK;
        }

        return FurnitureMovementError.NONE;
    }

    public void addCycleTask(ICycleable task) {
        this.cycleTasks.add(task);
    }

    public void removeCycleTask(ICycleable task) {
        this.cycleTasks.remove(task);
    }

    public void addBanzaiTeleporter(InteractionBattleBanzaiTeleporter item) {
        this.banzaiTeleporters.put(item.getId(), item);
    }

    public void removeBanzaiTeleporter(InteractionBattleBanzaiTeleporter item) {
        this.banzaiTeleporters.remove(item.getId());
    }

    public void addRoller(InteractionRoller item) {
        synchronized (this.rollers) {
            this.rollers.put(item.getId(), item);
        }
    }

    public void removeRoller(InteractionRoller roller) {
        synchronized (this.rollers) {
            this.rollers.remove(roller.getId());
        }
    }

    public void addGameScoreboard(InteractionGameScoreboard scoreboard) {
        this.gameScoreboards.put(scoreboard.getId(), scoreboard);
    }

    public void removeScoreboard(InteractionGameScoreboard scoreboard) {
        this.gameScoreboards.remove(scoreboard.getId());
    }

    public void addGameGate(InteractionGameGate gameGate) {
        this.gameGates.put(gameGate.getId(), gameGate);
    }

    public void removeGameGate(InteractionGameGate gameGate) {
        this.gameGates.remove(gameGate.getId());
    }

    public void addGameTimer(InteractionGameTimer gameTimer) {
        this.gameTimers.put(gameTimer.getId(), gameTimer);
    }

    public void removeGameTimer(InteractionGameTimer gameTimer) {
        this.gameTimers.remove(gameTimer.getId());
    }

    public void addFreezeExitTile(InteractionFreezeExitTile freezeExitTile) {
        this.freezeExitTile.put(freezeExitTile.getId(), freezeExitTile);
    }

    public void removeFreezeExitTile(InteractionFreezeExitTile freezeExitTile) {
        this.freezeExitTile.remove(freezeExitTile.getId());
    }

    public void addNest(InteractionNest item) {
        this.nests.put(item.getId(), item);
    }

    public void removeNest(InteractionNest item) {
        this.nests.remove(item.getId());
    }

    public void addPetDrink(InteractionPetDrink item) {
        this.petDrinks.put(item.getId(), item);
    }

    public void removePetDrink(InteractionPetDrink item) {
        this.petDrinks.remove(item.getId());
    }

    public void addPetFood(InteractionPetFood item) {
        this.petFoods.put(item.getId(), item);
    }

    public void removePetFood(InteractionPetFood petFood) {
        this.petFoods.remove(petFood.getId());
    }

    public InteractionPetToy getPetToy(int itemId) {
        return this.petToys.get(itemId);
    }

    public void addPetToy(InteractionPetToy item) {
        this.petToys.put(item.getId(), item);
    }

    public void removePetToy(InteractionPetToy petToy) {
        this.petToys.remove(petToy.getId());
    }

    public void addUndefined(RoomItem item) {
        synchronized (this.undefinedSpecials) {
            this.undefinedSpecials.put(item.getId(), item);
        }
    }

    public void removeUndefined(RoomItem item) {
        synchronized (this.undefinedSpecials) {
            this.undefinedSpecials.remove(item.getId());
        }
    }

    private void sortItem(RoomItem item) {
        if(item.getBaseItem().getType().equals(FurnitureType.FLOOR)) {
            this.floorItems.put(item.getId(), item);
            this.sortFloorItem(item);
        } else if(item.getBaseItem().getType().equals(FurnitureType.WALL)) {
            this.wallItems.put(item.getId(), item);
        }
    }

    private void sortFloorItem(RoomItem item) {
        if (item instanceof ICycleable) {
            this.addCycleTask((ICycleable) item);
        }

        if(item instanceof InteractionWired wired) {
            this.wiredManager.addWired(wired);
        } else if (item instanceof InteractionBattleBanzaiTeleporter interactionBattleBanzaiTeleporter) {
            this.addBanzaiTeleporter(interactionBattleBanzaiTeleporter);
        } else if (item instanceof InteractionRoller interactionRoller) {
            this.addRoller(interactionRoller);
        } else if (item instanceof InteractionGameScoreboard interactionGameScoreboard) {
            this.addGameScoreboard(interactionGameScoreboard);
        } else if (item instanceof InteractionGameGate interactionGameGate) {
            this.addGameGate(interactionGameGate);
        } else if (item instanceof InteractionGameTimer interactionGameTimer) {
            this.addGameTimer(interactionGameTimer);
        } else if (item instanceof InteractionFreezeExitTile interactionFreezeExitTile) {
            this.addFreezeExitTile(interactionFreezeExitTile);
        } else if (item instanceof InteractionNest interactionNest) {
            this.addNest(interactionNest);
        } else if (item instanceof InteractionPetDrink interactionPetDrink) {
            this.addPetDrink(interactionPetDrink);
        } else if (item instanceof InteractionPetFood interactionPetFood) {
            this.addPetFood(interactionPetFood);
        } else if (item instanceof InteractionPetToy interactionPetToy) {
            this.addPetToy(interactionPetToy);
        } else if (item instanceof InteractionPetTree) {
            this.addUndefined(item);
        } else if (item instanceof InteractionPetTrampoline) {
            this.addUndefined(item);
        } else if (item instanceof InteractionMoodLight) {
            this.addUndefined(item);
        } else if (item instanceof InteractionPyramid) {
            this.addUndefined(item);
        } else if (item instanceof InteractionMusicDisc) {
            this.addUndefined(item);
        } else if (item instanceof InteractionBattleBanzaiSphere) {
            this.addUndefined(item);
        } else if (item instanceof InteractionTalkingFurniture) {
            this.addUndefined(item);
        } else if (item instanceof InteractionWater) {
            this.addUndefined(item);
        } else if (item instanceof InteractionWaterItem) {
            this.addUndefined(item);
        } else if (item instanceof InteractionMuteArea) {
            this.addUndefined(item);
        } else if (item instanceof InteractionBuildArea) {
            this.addUndefined(item);
        } else if (item instanceof InteractionTagPole) {
            this.addUndefined(item);
        } else if (item instanceof InteractionTagField) {
            this.addUndefined(item);
        } else if (item instanceof InteractionJukeBox) {
            this.addUndefined(item);
        } else if (item instanceof InteractionPetBreedingNest) {
            this.addUndefined(item);
        } else if (item instanceof InteractionBlackHole) {
            this.addUndefined(item);
        } else if (item instanceof InteractionWiredHighscore) {
            this.addUndefined(item);
        } else if (item instanceof InteractionStickyPole) {
            this.addUndefined(item);
        } else if (item instanceof WiredBlob) {
            this.addUndefined(item);
        } else if (item instanceof InteractionTent) {
            this.addUndefined(item);
        } else if (item instanceof InteractionSnowboardSlope) {
            this.addUndefined(item);
        } else if (item instanceof InteractionFireworks) {
            this.addUndefined(item);
        }
    }
}
