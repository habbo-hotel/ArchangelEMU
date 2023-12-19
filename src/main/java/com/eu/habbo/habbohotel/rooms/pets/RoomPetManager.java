package com.eu.habbo.habbohotel.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitManager;
import com.eu.habbo.habbohotel.rooms.constants.RoomTileState;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.pets.entities.RoomPet;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomUnitSubManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.inventory.PetAddedToInventoryComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;
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

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
public class RoomPetManager extends RoomUnitSubManager {
    @Getter
    private final ConcurrentHashMap<Integer, Pet> currentPets;

    public RoomPetManager(RoomUnitManager roomUnitManager) {
        super(roomUnitManager);
        this.currentPets = new ConcurrentHashMap<>();
    }

    public synchronized void loadPets(Connection connection) {
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
                    roomUnitManager.addRoomUnit(pet);
                    this.room.getFurniOwnerNames().put(pet.getUserId(), set.getString("pet_owner_name"));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error("Caught Exception", e);
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
            room.getFurniOwnerNames().put(pet.getUserId(), roomUnitManager.getRoomHabboById(pet.getUserId()).getHabboInfo().getUsername());
            roomUnitManager.addRoomUnit(pet);
            room.sendComposer(new RoomPetComposer(pet).compose());
        }
    }

    public boolean hasPetsAt(RoomTile tile) {
        return this.currentPets.values().stream().anyMatch(pet -> pet.getRoomUnit().getCurrentPosition().equals(tile));
    }

    public Collection<Pet> getPetsAt(RoomTile tile) {
        return this.currentPets.values().stream().filter(pet -> pet.getRoomUnit().getCurrentPosition().equals(tile)).collect(Collectors.toSet());
    }

    public Collection<? extends Pet> getPetsOnItem(RoomItem item) {
        return currentPets.values().stream()
                .filter(pet ->
                        pet.getRoomUnit().getCurrentPosition().getX() >= item.getCurrentPosition().getX() &&
                                pet.getRoomUnit().getCurrentPosition().getX() < item.getCurrentPosition().getX() + item.getBaseItem().getLength() &&
                                pet.getRoomUnit().getCurrentPosition().getY() >= item.getCurrentPosition().getY() &&
                                pet.getRoomUnit().getCurrentPosition().getY() < item.getCurrentPosition().getY() + item.getBaseItem().getWidth())
                .toList();
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

    public Pet removePet(int petId) {
        Pet pet = currentPets.get(petId);
        roomUnitManager.removeUnit(pet.getRoomUnit().getVirtualId());
        return remove(petId);
    }

    public void removeAllPetsExceptRoomOwner() {
        ArrayList<Pet> toRemovePets = new ArrayList<>();
        ArrayList<Pet> removedPets = new ArrayList<>();
        synchronized (currentPets) {
            for (Pet pet : currentPets.values()) {
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
            currentPets.remove(pet.getId());
            roomUnitManager.removeUnit(pet.getRoomUnit().getVirtualId());
        }
    }

    @Override
    public List<RoomPet> cycle() {
        List<RoomPet> updatedUnits = new ArrayList<>();
        if (!getCurrentPets().isEmpty() && room.isAllowBotsWalk()) {
            Iterator<Pet> petIterator = getCurrentPets().values().iterator();
            while (petIterator.hasNext()) {
                final Pet pet;
                try {
                    pet = petIterator.next();
                } catch (Exception e) {
                    break;
                }

                pet.getRoomUnit().cycle();
                pet.cycle();

                if (pet.getRoomUnit().isStatusUpdateNeeded()) {
                    pet.getRoomUnit().setStatusUpdateNeeded(false);
                    updatedUnits.add(pet.getRoomUnit());
                }

                if (pet.getRoomUnit().isWalking() && pet.getRoomUnit().getPath().size() == 1 && pet.getRoomUnit().hasStatus(RoomUnitStatus.GESTURE)) {
                    pet.getRoomUnit().removeStatus(RoomUnitStatus.GESTURE);
                    updatedUnits.add(pet.getRoomUnit());
                }
            }
        }

        return updatedUnits;
    }

    public void addPet(Pet pet) {
        currentPets.put(pet.getId(), pet);
    }

    public Pet remove(int petId) {
        return currentPets.remove(petId);
    }

    public void clear() {
        currentPets.clear();
    }

    public void dispose() {
        Iterator<Pet> petIterator = this.currentPets.values().iterator();

        while (petIterator.hasNext()) {
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
    }


}
