package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.bots.BotManager;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.rooms.constants.RoomTileState;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomBot;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
import com.eu.habbo.habbohotel.units.Unit;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.outgoing.generic.alerts.BotErrorComposer;
import com.eu.habbo.messages.outgoing.inventory.BotAddedToInventoryComposer;
import com.eu.habbo.messages.outgoing.inventory.BotRemovedFromInventoryComposer;
import com.eu.habbo.messages.outgoing.inventory.PetAddedToInventoryComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUsersComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserRemoveMessageComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.bots.BotPickUpEvent;
import com.eu.habbo.plugin.events.bots.BotPlacedEvent;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
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

@Slf4j
@Getter
public class RoomUnitManager {
    private final Room room;
    private final ConcurrentHashMap<Integer, RoomUnit> currentRoomUnits;
    private final ConcurrentHashMap<Integer, Habbo> currentHabbos;
    private final ConcurrentHashMap<Integer, Bot> currentBots;
    private final ConcurrentHashMap<Integer, Pet> currentPets;
    private volatile int roomUnitCounter;
    public final Object roomUnitLock;

    public RoomUnitManager(Room room) {
        this.room = room;
        this.currentRoomUnits = new ConcurrentHashMap<>();
        this.currentHabbos = new ConcurrentHashMap<>();
        this.currentBots = new ConcurrentHashMap<>();
        this.currentPets = new ConcurrentHashMap<>();
        this.roomUnitCounter = 0;
        this.roomUnitLock = new Object();
    }

    public synchronized void load(Connection connection) {
        this.loadBots(connection);
        this.loadPets(connection);
    }

    private synchronized void loadBots(Connection connection) {
        this.currentBots.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username AS owner_name, bots.* FROM bots INNER JOIN users ON bots.owner_id = users.id WHERE room_id = ?")) {
            statement.setInt(1, this.room.getRoomInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Bot bot = Emulator.getGameEnvironment().getBotManager().loadBot(set);

                    if (bot != null) {
                        bot.getRoomUnit().setRoom(this.room);

                        RoomTile spawnTile = this.room.getLayout().getTile((short) set.getInt("x"), (short) set.getInt("y"));

                        if(spawnTile == null) {
                            bot.getRoomUnit().setCanWalk(false);
                        } else {
                            if(spawnTile.getState().equals(RoomTileState.INVALID)) {
                                bot.getRoomUnit().setCanWalk(false);
                            }

                            bot.getRoomUnit().setCurrentPosition(spawnTile);
                            bot.getRoomUnit().setCurrentZ(set.getDouble("z"));
                            bot.getRoomUnit().setRotation(RoomRotation.values()[set.getInt("rot")]);
                            bot.getRoomUnit().setSpawnTile(spawnTile);
                            bot.getRoomUnit().setSpawnHeight(spawnTile.getState().equals(RoomTileState.INVALID) ? 0 : spawnTile.getStackHeight());
                        }

                        bot.getRoomUnit().setDanceType(DanceType.values()[set.getInt("dance")]);

                        bot.getRoomUnit().giveEffect(set.getInt("effect"), Integer.MAX_VALUE, false);

                        this.addRoomUnit(bot);
                    }
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error("Caught Exception", e);
        }
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
                    this.currentBots.put(((Bot) unit).getId(), (Bot) unit);
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
        return Stream.concat(this.getHabbosAt(tile).stream(), this.getBotsAt(tile).stream()).map(Unit::getRoomUnit).collect(Collectors.toList());
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

    public Bot getRoomBotById(int botId) {
        return this.currentBots.get(botId);
    }

    public List<Bot> getBotsByName(String name) {
        synchronized (this.currentBots) {
            return currentBots.values().stream().filter(bot -> bot.getName().equalsIgnoreCase(name)).toList();
        }
    }

    public Bot getBotByRoomUnit(RoomUnit roomUnit) {
        return this.currentBots.values().stream().filter(bot -> bot.getRoomUnit() == roomUnit).findFirst().orElse(null);
    }

    public boolean hasBotsAt(RoomTile tile) {
        return this.currentBots.values().stream().anyMatch(bot -> bot.getRoomUnit().getCurrentPosition().equals(tile));
    }

    public Collection<Bot> getBotsAt(RoomTile tile) {
        return this.currentBots.values().stream().filter(bot -> bot.getRoomUnit().getCurrentPosition().equals(tile)).collect(Collectors.toSet());
    }

    public void placeBot(Bot bot, Habbo botOwner, int x, int y) {
        synchronized (this.currentBots) {
            RoomTile spawnTile = room.getLayout().getTile((short) x, (short) y);

            if(spawnTile == null || (!spawnTile.isWalkable() && !this.room.canSitOrLayAt(spawnTile)) || this.areRoomUnitsAt(spawnTile)) {
                botOwner.getClient().sendResponse(new BotErrorComposer(BotErrorComposer.ROOM_ERROR_BOTS_SELECTED_TILE_NOT_FREE));
                return;
            }

            if (Emulator.getPluginManager().isRegistered(BotPlacedEvent.class, false)) {
                Event event = new BotPlacedEvent(bot, spawnTile, botOwner);
                Emulator.getPluginManager().fireEvent(event);

                if (event.isCancelled()) {
                    return;
                }
            }

            if(this.currentBots.size() >= Room.MAXIMUM_BOTS && !botOwner.hasPermissionRight(Permission.ACC_UNLIMITED_BOTS)) {
                botOwner.getClient().sendResponse(new BotErrorComposer(BotErrorComposer.ROOM_ERROR_MAX_BOTS));
                return;
            }

            RoomBot roomBot = bot.getRoomUnit();

            roomBot.setRoom(this.room);
            roomBot.setCurrentPosition(spawnTile);
            roomBot.setCurrentZ(spawnTile.getStackHeight());
            roomBot.setRotation(RoomRotation.SOUTH);
            roomBot.setSpawnTile(spawnTile);
            roomBot.setSpawnHeight(spawnTile.getState().equals(RoomTileState.INVALID) ? 0 : spawnTile.getStackHeight());

            bot.setSqlUpdateNeeded(true);
            Emulator.getThreading().run(bot);

            this.addRoomUnit(bot);

            this.room.sendComposer(new RoomUsersComposer(bot).compose());

            roomBot.instantUpdate();

            botOwner.getInventory().getBotsComponent().removeBot(bot);
            botOwner.getClient().sendResponse(new BotRemovedFromInventoryComposer(bot));
            bot.onPlace(botOwner, room);
        }
    }

    public void updateBotsAt(RoomTile tile) {
        Collection<Bot> bots = this.getBotsAt(tile);

        if(bots == null || bots.isEmpty()) {
            return;
        }

        RoomItem item = this.room.getRoomItemManager().getTopItemAt(tile.getX(), tile.getY());

        bots.forEach(bot -> {
            double z = bot.getRoomUnit().getCurrentPosition().getStackHeight();

            if (bot.getRoomUnit().hasStatus(RoomUnitStatus.SIT) && ((item == null && !bot.getRoomUnit().isCmdSitEnabled()) || (item != null && !item.getBaseItem().allowSit()))) {
                bot.getRoomUnit().removeStatus(RoomUnitStatus.SIT);
            }

            if (bot.getRoomUnit().hasStatus(RoomUnitStatus.LAY) && ((item == null && !bot.getRoomUnit().isCmdLayEnabled()) || (item != null && !item.getBaseItem().allowLay()))) {
                bot.getRoomUnit().removeStatus(RoomUnitStatus.LAY);
            }

            if (item != null && (item.getBaseItem().allowSit() || item.getBaseItem().allowLay())) {
                if(item.getBaseItem().allowSit()) {
                    bot.getRoomUnit().addStatus(RoomUnitStatus.SIT, String.valueOf(Item.getCurrentHeight(item)));
                } else if(item.getBaseItem().allowLay()) {
                    bot.getRoomUnit().addStatus(RoomUnitStatus.LAY, String.valueOf(Item.getCurrentHeight(item)));
                }

                bot.getRoomUnit().setCurrentZ(item.getCurrentZ());
                bot.getRoomUnit().setRotation(RoomRotation.fromValue(item.getRotation()));
            } else {
                bot.getRoomUnit().setCurrentZ(z);
            }
        });
    }

    public void pickUpBot(Bot bot, Habbo picker) {
        HabboInfo botOwnerInfo = picker == null ? bot.getOwnerInfo() : picker.getHabboInfo();

        BotPickUpEvent pickedUpEvent = new BotPickUpEvent(bot, picker);
        Emulator.getPluginManager().fireEvent(pickedUpEvent);

        if (pickedUpEvent.isCancelled())
            return;

        if (picker == null || (bot.getOwnerInfo().getId() == picker.getHabboInfo().getId() || picker.hasPermissionRight(Permission.ACC_ANYROOMOWNER))) {
            if (picker != null && !picker.hasPermissionRight(Permission.ACC_UNLIMITED_BOTS) && picker.getInventory().getBotsComponent().getBots().size() >= BotManager.MAXIMUM_BOT_INVENTORY_SIZE) {
                picker.alert(Emulator.getTexts().getValue("error.bots.max.inventory").replace("%amount%", String.valueOf(BotManager.MAXIMUM_BOT_INVENTORY_SIZE)));
                return;
            }

            bot.onPickUp(picker, this.room);

            bot.setFollowingHabboId(0);

            //@DEPRECATED
            bot.setOwnerId(botOwnerInfo.getId());
            bot.setOwnerName(botOwnerInfo.getUsername());

            bot.setOwnerInfo(botOwnerInfo);

            this.removeBot(bot);

            bot.setSqlUpdateNeeded(true);
            Emulator.getThreading().run(bot);

            Habbo receiver = picker == null ? Emulator.getGameEnvironment().getHabboManager().getHabbo(botOwnerInfo.getId()) : picker;

            if (receiver != null) {
                receiver.getInventory().getBotsComponent().addBot(bot);
                receiver.getClient().sendResponse(new BotAddedToInventoryComposer(bot));
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
            this.currentRoomUnits.remove(roomHabbo.getVirtualId());
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

    public void removeBot(Bot bot) {
        if (this.currentBots.containsKey(bot.getId())) {
            //TODO gotta do a method to removeUnit and clear tile
            if (bot.getRoomUnit().getCurrentPosition() != null) {
                bot.getRoomUnit().getCurrentPosition().removeUnit(bot.getRoomUnit());
            }

            this.currentBots.remove(bot.getId());
            this.currentRoomUnits.remove(bot.getRoomUnit().getVirtualId());

            bot.getRoomUnit().setRoom(null);

            this.room.sendComposer(new UserRemoveMessageComposer(bot.getRoomUnit()).compose());
        }
    }

    public Pet removePet(int petId) {
        Pet pet = this.currentPets.get(petId);
        this.currentRoomUnits.remove(pet.getRoomUnit().getVirtualId());
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
            this.currentRoomUnits.remove(pet.getRoomUnit().getVirtualId());
        }
    }

    public void clear() {
        synchronized (this.roomUnitLock) {
            this.currentRoomUnits.clear();
            this.currentHabbos.clear();
            this.currentBots.clear();
            this.currentPets.clear();
            this.roomUnitCounter = 0;
        }
    }

    public void dispose() {
        for(Habbo habbo : this.currentHabbos.values()) {
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this.room, true);
        }

        this.currentHabbos.clear();

        Iterator<Bot> botIterator = this.currentBots.values().iterator();

        while(botIterator.hasNext()) {
            try {
                Bot bot = botIterator.next();
                bot.setSqlUpdateNeeded(true);
                Emulator.getThreading().run(bot);
            } catch (NoSuchElementException e) {
                log.error("Caught Exception", e);
                break;
            }
        }

        this.currentBots.clear();

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
}
