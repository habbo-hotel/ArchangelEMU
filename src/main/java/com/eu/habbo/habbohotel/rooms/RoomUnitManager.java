package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionGate;
import com.eu.habbo.habbohotel.items.interactions.InteractionPyramid;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.bots.RoomBotManager;
import com.eu.habbo.habbohotel.rooms.constants.RoomRightLevels;
import com.eu.habbo.habbohotel.rooms.constants.RoomTileState;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomPet;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.units.Unit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.inventory.PetAddedToInventoryComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;
import com.eu.habbo.messages.outgoing.rooms.users.IgnoreResultMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserRemoveMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureRolledEvent;
import com.eu.habbo.plugin.events.users.UserRolledEvent;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;
import static com.eu.habbo.habbohotel.rooms.Room.CAUGHT_EXCEPTION;
import static com.eu.habbo.habbohotel.rooms.utils.cycle.CycleFunctions.cycleIdle;

@Slf4j
@Getter
public class RoomUnitManager {
    private final Room room;
    private final ConcurrentHashMap<Integer, RoomUnit> currentRoomUnits;
    private final ConcurrentHashMap<Integer, Habbo> currentHabbos;
    private final ConcurrentHashMap<Integer, Pet> currentPets;
    private volatile int roomUnitCounter;
    public final Object roomUnitLock;

    private final RoomBotManager roomBotManager;

    private int roomIdleCycles;

    @Setter
    private long rollerCycle = System.currentTimeMillis();


    public RoomUnitManager(Room room) {
        this.room = room;
        this.currentRoomUnits = new ConcurrentHashMap<>();
        this.currentHabbos = new ConcurrentHashMap<>();
        this.currentPets = new ConcurrentHashMap<>();
        this.roomUnitCounter = 0;
        this.roomUnitLock = new Object();
        roomBotManager = new RoomBotManager(this);
    }

    public synchronized void load(Connection connection) {
        this.roomIdleCycles = 0;
        roomBotManager.loadBots(connection);
        this.loadPets(connection);
    }



    private synchronized void loadPets(Connection connection) {
        this.currentPets.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username as pet_owner_name, users_pets.* FROM users_pets INNER JOIN users ON users_pets.user_id = users.id WHERE room_id = ?")) {
            statement.setInt(1, this.room.getRoomInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Pet pet = PetManager.loadPet(set);

                    pet.setRoom(this.room);
                    pet.getRoomUnit().setRoom(this.room);
                    pet.getRoomUnit().setLocation(this.room.getLayout().getTile((short) set.getInt("x"), (short) set.getInt("y")));
                    if (pet.getRoomUnit().getCurrentPosition() == null || pet.getRoomUnit().getCurrentPosition().getState() == RoomTileState.INVALID) {
                        pet.getRoomUnit().setCurrentZ(this.room.getLayout().getDoorTile().getStackHeight());
                        pet.getRoomUnit().setLocation(this.room.getLayout().getDoorTile());
                        pet.getRoomUnit().setRotation(RoomRotation.fromValue(this.room.getLayout().getDoorDirection()));
                    } else {
                        pet.getRoomUnit().setCurrentZ(set.getDouble("z"));
                        pet.getRoomUnit().setRotation(RoomRotation.values()[set.getInt("rot")]);
                    }
                    pet.getRoomUnit().setRoomUnitType(RoomUnitType.PET);
                    pet.getRoomUnit().setCanWalk(true);
                    this.addRoomUnit(pet);
                    this.room.getFurniOwnerNames().put(pet.getUserId(), set.getString("pet_owner_name"));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error("Caught Exception", e);
        }
    }

    public void addRoomUnit(Unit unit) {
        synchronized (this.roomUnitLock) {
            //TODO Maybe set the room in this method

            unit.getRoomUnit().setVirtualId(this.roomUnitCounter);
            this.currentRoomUnits.put(unit.getRoomUnit().getVirtualId(), unit.getRoomUnit());
            this.roomUnitCounter++;

            switch (unit.getRoomUnit().getRoomUnitType()) {
                case HABBO -> {
                    this.currentHabbos.put(((Habbo) unit).getHabboInfo().getId(), (Habbo) unit);
                    unit.getRoomUnit().getRoom().updateDatabaseUserCount();
                }
                case BOT -> {
                    roomBotManager.addBot((Bot)unit);
                }
                case PET -> {
                    this.currentPets.put(((Pet) unit).getId(), (Pet) unit);
                    Habbo habbo = this.getRoomHabboById(((Pet) unit).getUserId());
                    if (habbo != null) {
                        unit.getRoomUnit().getRoom().getFurniOwnerNames().put(((Pet) unit).getUserId(), this.getRoomHabboById(((Pet) unit).getUserId()).getHabboInfo().getUsername());
                    }
                }
            }
        }
    }

    public Collection<RoomUnit> getRoomUnitsAt(RoomTile tile) {
        return this.currentRoomUnits.values().stream().filter(roomUnit -> roomUnit.getCurrentPosition().equals(tile)).collect(Collectors.toSet());
    }

    public boolean areRoomUnitsAt(RoomTile tile) {
        return this.currentRoomUnits.values().stream().anyMatch(roomUnit -> roomUnit.getCurrentPosition().equals(tile));
    }

    public boolean areRoomUnitsAt(RoomTile tile, RoomUnit skippedRoomUnit) {
        if(skippedRoomUnit == null) {
            return this.areRoomUnitsAt(tile);
        }
        
        return this.currentRoomUnits.values().stream().filter(roomUnit -> !roomUnit.equals(skippedRoomUnit)).anyMatch(roomUnit -> roomUnit.getCurrentPosition().equals(tile));
    }

    public List<RoomUnit> getAvatarsAt(RoomTile tile) {
        return Stream.concat(this.getHabbosAt(tile).stream(), roomBotManager.getBotsAt(tile).stream()).map(Unit::getRoomUnit).collect(Collectors.toList());
    }

    public int getRoomHabbosCount() {
        return this.currentHabbos.size();
    }

    public boolean hasHabbosAt(RoomTile tile) {
        return this.currentHabbos.values().stream().anyMatch(habbo -> habbo.getRoomUnit().getCurrentPosition().equals(tile));
    }

    public Collection<Habbo> getHabbosAt(RoomTile tile) {
        return this.currentHabbos.values().stream().filter(habbo -> habbo.getRoomUnit().getCurrentPosition().equals(tile)).collect(Collectors.toSet());
    }

    public Habbo getRoomHabboById(int habboId) {
        return this.currentHabbos.get(habboId);
    }

    public Habbo getRoomHabboByUsername(String username) {
        return this.currentHabbos.values().stream().filter(habbo -> habbo.getHabboInfo().getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    public Habbo getHabboByVirtualId(int virtualId) {
        return this.currentHabbos.values().stream().filter(habbo -> habbo.getRoomUnit().getVirtualId() == virtualId).findFirst().orElse(null);
    }

    public Habbo getHabboByRoomUnit(RoomUnit roomUnit) {
        return this.currentHabbos.values().stream().filter(habbo -> habbo.getRoomUnit() == roomUnit).findFirst().orElse(null);
    }

    public void updateHabbosAt(RoomTile tile) {
        Collection<Habbo> habbos = this.getHabbosAt(tile);

        if(habbos == null || habbos.isEmpty()) {
            return;
        }

        RoomItem item = this.room.getRoomItemManager().getTopItemAt(tile.getX(), tile.getY());

        for (Habbo habbo : habbos) {
            double z = habbo.getRoomUnit().getCurrentPosition().getStackHeight();

            if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.SIT) && ((item == null && !habbo.getRoomUnit().isCmdSitEnabled()) || (item != null && !item.getBaseItem().allowSit()))) {
                habbo.getRoomUnit().removeStatus(RoomUnitStatus.SIT);
            }

            if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.LAY) && ((item == null && !habbo.getRoomUnit().isCmdLayEnabled()) || (item != null && !item.getBaseItem().allowLay()))) {
                habbo.getRoomUnit().removeStatus(RoomUnitStatus.LAY);
            }

            if (item != null && (item.getBaseItem().allowSit() || item.getBaseItem().allowLay())) {
                if(item.getBaseItem().allowSit()) {
                    habbo.getRoomUnit().addStatus(RoomUnitStatus.SIT, String.valueOf(Item.getCurrentHeight(item)));
                } else if(item.getBaseItem().allowLay()) {
                    habbo.getRoomUnit().addStatus(RoomUnitStatus.LAY, String.valueOf(Item.getCurrentHeight(item)));
                }

                habbo.getRoomUnit().setCurrentZ(item.getCurrentZ());
                habbo.getRoomUnit().setRotation(RoomRotation.fromValue(item.getRotation()));
            } else {
                habbo.getRoomUnit().setCurrentZ(z);
            }
        }
    }





    public void placePet(Pet pet, Room room, short x, short y, double z) {
        synchronized (this.currentPets) {
            RoomTile spawnTile = room.getLayout().getTile(x, y);

            if (spawnTile == null) {
                spawnTile = room.getLayout().getDoorTile();
            }

            pet.setRoom(room);
            pet.getRoomUnit().walkTo(spawnTile);
            pet.getRoomUnit().setLocation(spawnTile)
                    .setRoomUnitType(RoomUnitType.PET)
                    .setCanWalk(true)
                    .setCurrentZ(z);

            if (pet.getRoomUnit().getCurrentPosition() == null) {
                pet.getRoomUnit()
                        .setLocation(room.getLayout().getDoorTile())
                        .setRotation(RoomRotation.fromValue(room.getLayout().getDoorDirection()));
            }

            pet.setSqlUpdateNeeded(true);
            room.getFurniOwnerNames().put(pet.getUserId(), this.getRoomHabboById(pet.getUserId()).getHabboInfo().getUsername());
            this.addRoomUnit(pet);
            room.sendComposer(new RoomPetComposer(pet).compose());
        }
    }

    public boolean hasPetsAt(RoomTile tile) {
        return this.currentPets.values().stream().anyMatch(pet -> pet.getRoomUnit().getCurrentPosition().equals(tile));
    }

    public Collection<Pet> getPetsAt(RoomTile tile) {
        return this.currentPets.values().stream().filter(pet -> pet.getRoomUnit().getCurrentPosition().equals(tile)).collect(Collectors.toSet());
    }

    public Pet getRoomPetById(int petId) {
        return this.currentPets.get(petId);
    }

    public Pet getPetByRoomUnit(RoomUnit roomUnit) {
        return this.currentPets.values().stream().filter(pet -> pet.getRoomUnit() == roomUnit).findFirst().orElse(null);
    }

    public void pickUpMyPets(Habbo owner) {
        THashSet<Pet> pets = new THashSet<>();

        synchronized (this.currentPets) {
            for (Pet pet : this.currentPets.values()) {
                if (pet.getUserId() == owner.getHabboInfo().getId()) {
                    pets.add(pet);
                }
            }
        }

        for (Pet pet : pets) {
            pet.removeFromRoom();
            Emulator.getThreading().run(pet);
            owner.getInventory().getPetsComponent().addPet(pet);
            owner.getClient().sendResponse(new PetAddedToInventoryComposer(pet));
            this.currentPets.remove(pet.getId());
        }
    }

    public void removeHabbo(Habbo habbo, boolean sendRemovePacket) {
        if(!this.currentHabbos.containsKey(habbo.getHabboInfo().getId())) {
            return;
        }

        RoomHabbo roomHabbo = habbo.getRoomUnit();

        if(roomHabbo.getCurrentPosition() != null) {
            roomHabbo.getCurrentPosition().removeUnit(habbo.getRoomUnit());
        }

        synchronized (this.roomUnitLock) {
            this.currentHabbos.remove(habbo.getHabboInfo().getId());
            removeUnit(roomHabbo.getVirtualId());
        }

        roomHabbo.getRoom().sendComposer(new UserRemoveMessageComposer(roomHabbo).compose());

        //MOVE THIS TO RoomTile.java -> removeUnit()
        RoomItem item = roomHabbo.getRoom().getRoomItemManager().getTopItemAt(roomHabbo.getCurrentPosition());

        if (item != null) {
            try {
                item.onWalkOff(habbo.getRoomUnit(), roomHabbo.getRoom(), new Object[]{});
            } catch (Exception e) {
                log.error("Caught Exception", e);
            }
        }

        if (habbo.getHabboInfo().getCurrentGame() != null && roomHabbo.getRoom().getGame(habbo.getHabboInfo().getCurrentGame()) != null) {
            roomHabbo.getRoom().getGame(habbo.getHabboInfo().getCurrentGame()).removeHabbo(habbo);
        }

        RoomTrade trade = roomHabbo.getRoom().getActiveTradeForHabbo(habbo);

        if (trade != null) {
            trade.stopTrade(habbo);
        }

        if (!roomHabbo.getRoom().getRoomInfo().isRoomOwner(habbo)) {
            this.pickUpMyPets(habbo);
        }

        roomHabbo.getRoom().updateDatabaseUserCount();
        roomHabbo.clear();
    }


    public Pet removePet(int petId) {
        Pet pet = this.currentPets.get(petId);
        removeUnit(pet.getRoomUnit().getVirtualId());
        return this.currentPets.remove(petId);
    }

    public void removeAllPetsExceptRoomOwner() {
        ArrayList<Pet> toRemovePets = new ArrayList<>();
        ArrayList<Pet> removedPets = new ArrayList<>();
        synchronized (this.currentPets) {
            for (Pet pet : this.currentPets.values()) {
                try {
                    if (pet.getUserId() != pet.getRoomUnit().getRoom().getRoomInfo().getOwnerInfo().getId()) {
                        toRemovePets.add(pet);
                    }

                } catch (NoSuchElementException e) {
                    log.error("Caught exception", e);
                    break;
                }
            }
        }

        for (Pet pet : toRemovePets) {
            removedPets.add(pet);

            pet.removeFromRoom();

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(pet.getUserId());
            if (habbo != null) {
                habbo.getInventory().getPetsComponent().addPet(pet);
                habbo.getClient().sendResponse(new PetAddedToInventoryComposer(pet));
            }

            pet.setSqlUpdateNeeded(true);
            pet.run();
        }

        for (Pet pet : removedPets) {
            this.currentPets.remove(pet.getId());
            removeUnit(pet.getRoomUnit().getVirtualId());
        }
    }

    public void clear() {
        synchronized (this.roomUnitLock) {
            this.currentRoomUnits.clear();
            this.currentHabbos.clear();
            this.roomBotManager.clear();
            this.currentPets.clear();
            this.roomUnitCounter = 0;
        }
    }

    public void dispose() {
        for(Habbo habbo : this.currentHabbos.values()) {
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this.room, true);
        }

        this.currentHabbos.clear();

       roomBotManager.dispose();
        Iterator<Pet> petIterator = this.currentPets.values().iterator();

        while(petIterator.hasNext()) {
            try {
                Pet pet = petIterator.next();
                pet.setSqlUpdateNeeded(true);
                Emulator.getThreading().run(pet);
            } catch (NoSuchElementException e) {
                log.error("Caught Exception", e);
                break;
            }
        }

        this.currentPets.clear();
        this.currentRoomUnits.clear();
    }

    public void removeUnit(int virtualId) {
        this.currentRoomUnits.remove(virtualId);
    }

    public boolean cycle(boolean cycleOdd){
        boolean foundRightHolder = false;
        if (!getCurrentHabbos().isEmpty()) {
            this.roomIdleCycles = 0;

            THashSet<RoomUnit> updatedUnit = new THashSet<>();
            ArrayList<Habbo> toKick = new ArrayList<>();

            final long millis = System.currentTimeMillis();

            for (Habbo habbo : getCurrentHabbos().values()) {
                if (!foundRightHolder) {
                    foundRightHolder = habbo.getRoomUnit().getRightsLevel() != RoomRightLevels.NONE;
                }

                if (habbo.getRoomUnit().getEffectId() > 0 && millis / 1000 > habbo.getRoomUnit().getEffectEndTimestamp()) {
                    habbo.getRoomUnit().giveEffect(0, -1);
                }

                if (habbo.getRoomUnit().isKicked()) {
                    habbo.getRoomUnit().setKickCount(habbo.getRoomUnit().getKickCount() + 1);

                    if (habbo.getRoomUnit().getKickCount() >= 5) {
                        room.scheduledTasks.add(() -> Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, room));
                        continue;
                    }
                }

                if (Emulator.getConfig().getBoolean("hotel.rooms.auto.idle")) {
                    cycleIdle(room, habbo, toKick);
                }

                if (Emulator.getConfig().getBoolean("hotel.rooms.deco_hosting") && room.getRoomInfo().getOwnerInfo().getId() != habbo.getHabboInfo().getId()) {
                    //Check if the time already have 1 minute (120 / 2 = 60s)
                    if (habbo.getRoomUnit().getTimeInRoom() >= 120) {
                        AchievementManager.progressAchievement(room.getRoomInfo().getOwnerInfo().getId(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoHosting"));
                        habbo.getRoomUnit().resetTimeInRoom();
                    } else {
                        habbo.getRoomUnit().increaseTimeInRoom();
                    }
                }

                if (habbo.getHabboStats().isMutedBubbleTracker() && habbo.getHabboStats().allowTalk()) {
                    habbo.getHabboStats().setMutedBubbleTracker(false);
                    room.sendComposer(new IgnoreResultMessageComposer(habbo, IgnoreResultMessageComposer.UNIGNORED).compose());
                }

                // Substract 1 from the chatCounter every odd cycle, which is every (500ms * 2).
                if (cycleOdd && habbo.getHabboStats().getChatCounter().get() > 0) {
                    habbo.getHabboStats().getChatCounter().decrementAndGet();
                }

                habbo.getRoomUnit().cycle();

                if(habbo.getRoomUnit().isStatusUpdateNeeded()) {
                    habbo.getRoomUnit().setStatusUpdateNeeded(false);
                    updatedUnit.add(habbo.getRoomUnit());
                }
            }

            if (!toKick.isEmpty()) {
                for (Habbo habbo : toKick) {
                    Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, room);
                }
            }

            updatedUnit.addAll(roomBotManager.cycle());
            if (!getCurrentPets().isEmpty() && room.isAllowBotsWalk()) {
                Iterator<Pet> petIterator = getCurrentPets().values().iterator();
                while(petIterator.hasNext()) {
                    final Pet pet;
                    try {
                        pet = petIterator.next();
                    } catch (Exception e) {
                        break;
                    }

                    pet.getRoomUnit().cycle();
                    pet.cycle();

                    if(pet.getRoomUnit().isStatusUpdateNeeded()) {
                        pet.getRoomUnit().setStatusUpdateNeeded(false);
                        updatedUnit.add(pet.getRoomUnit());
                    }

                    if (pet.getRoomUnit().isWalking() && pet.getRoomUnit().getPath().size() == 1 && pet.getRoomUnit().hasStatus(RoomUnitStatus.GESTURE)) {
                        pet.getRoomUnit().removeStatus(RoomUnitStatus.GESTURE);
                        updatedUnit.add(pet.getRoomUnit());
                    }
                }
            }


            if (room.getRoomInfo().getRollerSpeed() != -1 && this.rollerCycle >= room.getRoomInfo().getRollerSpeed()) {
                this.rollerCycle = 0;

                THashSet<MessageComposer> messages = new THashSet<>();

                //Find alternative for this.
                //Reason is that tile gets updated after every roller.
                List<Integer> rollerFurniIds = new ArrayList<>();
                List<Integer> rolledUnitIds = new ArrayList<>();


                room.getRoomSpecialTypes().getRollers().forEachValue(roller -> {

                    RoomItem newRoller = null;

                    RoomTile rollerTile = room.getLayout().getTile(roller.getCurrentPosition().getX(), roller.getCurrentPosition().getY());

                    if (rollerTile == null)
                        return true;

                    THashSet<RoomItem> itemsOnRoller = new THashSet<>();

                    for (RoomItem item : room.getRoomItemManager().getItemsAt(rollerTile)) {
                        if (item.getCurrentZ() >= roller.getCurrentZ() + Item.getCurrentHeight(roller)) {
                            itemsOnRoller.add(item);
                        }
                    }

                    itemsOnRoller.remove(roller);

                    if (!areRoomUnitsAt(rollerTile) && itemsOnRoller.isEmpty())
                        return true;

                    RoomTile tileInFront = room.getLayout().getTileInFront(room.getLayout().getTile(roller.getCurrentPosition().getX(), roller.getCurrentPosition().getY()), roller.getRotation());

                    if (tileInFront == null)
                        return true;

                    if (!room.getLayout().tileExists(tileInFront.getX(), tileInFront.getY()))
                        return true;

                    if (tileInFront.getState() == RoomTileState.INVALID)
                        return true;

                    if (!tileInFront.getAllowStack() && !(tileInFront.isWalkable() || tileInFront.getState() == RoomTileState.SIT || tileInFront.getState() == RoomTileState.LAY))
                        return true;

                    if (areRoomUnitsAt(tileInFront))
                        return true;

                    THashSet<RoomItem> itemsNewTile = new THashSet<>();
                    itemsNewTile.addAll(room.getRoomItemManager().getItemsAt(tileInFront));
                    itemsNewTile.removeAll(itemsOnRoller);

                    itemsOnRoller.removeIf(item -> item.getCurrentPosition().getX() != roller.getCurrentPosition().getX() || item.getCurrentPosition().getY() != roller.getCurrentPosition().getY() || rollerFurniIds.contains(item.getId()));

                    RoomItem topItem = room.getRoomItemManager().getTopItemAt(tileInFront.getX(), tileInFront.getY());

                    boolean allowUsers = true;
                    boolean allowFurniture = true;
                    boolean stackContainsRoller = false;

                    for (RoomItem item : itemsNewTile) {
                        if (!(item.getBaseItem().allowWalk() || item.getBaseItem().allowSit()) && !(item instanceof InteractionGate && item.getExtraData().equals("1"))) {
                            allowUsers = false;
                        }
                        if (item instanceof InteractionRoller) {
                            newRoller = item;
                            stackContainsRoller = true;

                            if ((item.getCurrentZ() != roller.getCurrentZ() || (itemsNewTile.size() > 1 && item != topItem)) && !InteractionRoller.NO_RULES) {
                                allowUsers = false;
                                allowFurniture = false;
                                continue;
                            }

                            break;
                        } else {
                            allowFurniture = false;
                        }
                    }

                    if (allowFurniture) {
                        allowFurniture = tileInFront.getAllowStack();
                    }

                    double zOffset = 0;
                    if (newRoller != null) {
                        if ((!itemsNewTile.isEmpty() && (itemsNewTile.size() > 1)) && !InteractionRoller.NO_RULES) {
                            return true;
                        }
                    } else {
                        zOffset = -Item.getCurrentHeight(roller) + tileInFront.getStackHeight() - rollerTile.getZ();
                    }

                    if (allowUsers) {
                        Event roomUserRolledEvent = null;

                        if (Emulator.getPluginManager().isRegistered(UserRolledEvent.class, true)) {
                            roomUserRolledEvent = new UserRolledEvent(null, null, null);
                        }

                        ArrayList<RoomUnit> unitsOnTile = new ArrayList<>(getRoomUnitsAt(rollerTile));

                        for (RoomUnit roomUnit : getRoomUnitsAt(rollerTile)) {
                            if (roomUnit instanceof RoomPet) {
                                Pet pet = getPetByRoomUnit(roomUnit);
                                if (pet instanceof RideablePet rideablePet && rideablePet.getRider() != null) {
                                    unitsOnTile.remove(roomUnit);
                                }
                            }
                        }

                        THashSet<Integer> usersRolledThisTile = new THashSet<>();

                        for (RoomUnit roomUnit : unitsOnTile) {
                            if (rolledUnitIds.contains(roomUnit.getVirtualId())) continue;

                            if (usersRolledThisTile.size() >= Room.ROLLERS_MAXIMUM_ROLL_AVATARS) break;

                            if (stackContainsRoller && !allowFurniture && !(topItem != null && topItem.isWalkable()))
                                continue;

                            if (roomUnit.hasStatus(RoomUnitStatus.MOVE))
                                continue;

                            double newZ = roomUnit.getCurrentZ() + zOffset;

                            if (roomUserRolledEvent != null && roomUnit.getRoomUnitType() == RoomUnitType.HABBO) {
                                roomUserRolledEvent = new UserRolledEvent(getHabboByRoomUnit(roomUnit), roller, tileInFront);
                                Emulator.getPluginManager().fireEvent(roomUserRolledEvent);

                                if (roomUserRolledEvent.isCancelled())
                                    continue;
                            }

                            // horse riding
                            boolean isRiding = false;
                            if (roomUnit.getRoomUnitType() == RoomUnitType.HABBO) {
                                Habbo rollingHabbo = getHabboByRoomUnit(roomUnit);
                                if (rollingHabbo != null && rollingHabbo.getHabboInfo() != null) {
                                    RideablePet ridingPet = rollingHabbo.getRoomUnit().getRidingPet();
                                    if (ridingPet != null) {
                                        RoomUnit ridingUnit = ridingPet.getRoomUnit();
                                        newZ = ridingUnit.getCurrentZ() + zOffset;
                                        rolledUnitIds.add(ridingUnit.getVirtualId());
                                        updatedUnit.remove(ridingUnit);
                                        messages.add(new RoomUnitOnRollerComposer(ridingUnit, roller, ridingUnit.getCurrentPosition(), ridingUnit.getCurrentZ(), tileInFront, newZ, room));
                                        isRiding = true;
                                    }
                                }
                            }

                            usersRolledThisTile.add(roomUnit.getVirtualId());
                            rolledUnitIds.add(roomUnit.getVirtualId());
                            updatedUnit.remove(roomUnit);
                            messages.add(new RoomUnitOnRollerComposer(roomUnit, roller, roomUnit.getCurrentPosition(), roomUnit.getCurrentZ() + (isRiding ? 1 : 0), tileInFront, newZ + (isRiding ? 1 : 0), room));

                            if (itemsOnRoller.isEmpty()) {
                                RoomItem item = room.getRoomItemManager().getTopItemAt(tileInFront.getX(), tileInFront.getY());

                                if (item != null && itemsNewTile.contains(item) && !itemsOnRoller.contains(item)) {
                                    Emulator.getThreading().run(() -> {
                                        if (roomUnit.getTargetPosition() == rollerTile) {
                                            try {
                                                item.onWalkOn(roomUnit, room, new Object[]{rollerTile, tileInFront});
                                            } catch (Exception e) {
                                                log.error(CAUGHT_EXCEPTION, e);
                                            }
                                        }
                                    }, room.getRoomInfo().getRollerSpeed() == 0 ? 250 : InteractionRoller.DELAY);
                                }
                            }
                        }
                    }

                    if (!messages.isEmpty()) {
                        for (MessageComposer message : messages) {
                            room.sendComposer(message.compose());
                        }
                        messages.clear();
                    }

                    if (allowFurniture || !stackContainsRoller || InteractionRoller.NO_RULES) {
                        Event furnitureRolledEvent = null;

                        if (Emulator.getPluginManager().isRegistered(FurnitureRolledEvent.class, true)) {
                            furnitureRolledEvent = new FurnitureRolledEvent(null, null, null);
                        }

                        if (newRoller == null || topItem == newRoller) {
                            List<RoomItem> sortedItems = new ArrayList<>(itemsOnRoller);
                            sortedItems.sort((o1, o2) -> {
                                return Double.compare(o2.getCurrentZ(), o1.getCurrentZ());
                            });

                            for (RoomItem item : sortedItems) {
                                if ((item.getCurrentPosition().getX() == roller.getCurrentPosition().getX() && item.getCurrentPosition().getY() == roller.getCurrentPosition().getY() && zOffset <= 0) && (item != roller)) {
                                    if (furnitureRolledEvent != null) {
                                        furnitureRolledEvent = new FurnitureRolledEvent(item, roller, tileInFront);
                                        Emulator.getPluginManager().fireEvent(furnitureRolledEvent);

                                        if (furnitureRolledEvent.isCancelled())
                                            continue;
                                    }

                                    messages.add(new FloorItemOnRollerComposer(item, roller, tileInFront, zOffset, room));
                                    rollerFurniIds.add(item.getId());
                                }
                            }
                        }
                    }


                    if (!messages.isEmpty()) {
                        for (MessageComposer message : messages) {
                            this.sendComposer(message.compose());
                        }
                        messages.clear();
                    }

                    return true;
                });


                int currentTime = (int) (room.getCycleTimestamp() / 1000);
                for (RoomItem pyramid : room.getRoomSpecialTypes().getItemsOfType(InteractionPyramid.class)) {
                    if (pyramid instanceof InteractionPyramid interactionPyramid && interactionPyramid.getNextChange() < currentTime) {
                        interactionPyramid.change(room);
                    }
                }
            } else {
                this.rollerCycle++;
            }

            if (!updatedUnit.isEmpty()) {
                this.sendComposer(new UserUpdateComposer(updatedUnit).compose());
            }

            room.getRoomTraxManager().cycle();
        } else {

            if (this.roomIdleCycles < 60)
                this.roomIdleCycles++;
            else
                this.dispose();
        }

        return foundRightHolder;
    }

    private void sendComposer(ServerMessage serverMessage) {
        room.sendComposer(serverMessage);
    }
}
