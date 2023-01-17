package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.ArrayList;
import java.util.List;

public class RespectPetEvent extends MessageHandler {

    @Override
    public void handle() {
        final int petId = this.packet.readInt();

        final Habbo habbo = this.client.getHabbo();
        if (habbo == null) { return; }

        final Room room = habbo.getHabboInfo().getCurrentRoom();
        if (room == null) { return; }

        final Pet pet = room.getPet(petId);
        if (pet == null) { return; }

        if (habbo.getHabboStats().getPetRespectPointsToGive() > 0 || pet instanceof MonsterplantPet) {

            List<Runnable> tasks = new ArrayList<>();
            tasks.add(() -> {
                pet.scratched(habbo);
                Emulator.getThreading().run(pet);
            });

            RoomTile tile = habbo.getRoomUnit().getClosestAdjacentTile(pet.getRoomUnit().getX(), pet.getRoomUnit().getY(), true);
            if(tile != null) {
                habbo.getRoomUnit().setGoalLocation(tile);
            }

            Emulator.getThreading().run(new RoomUnitWalkToLocation(habbo.getRoomUnit(), tile, room, tasks, tasks));
        }
    }
}