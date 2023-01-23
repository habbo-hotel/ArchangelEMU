package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionDice;
import com.eu.habbo.habbohotel.items.interactions.InteractionSpinningBottle;
import com.eu.habbo.habbohotel.items.interactions.InteractionWired;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionMonsterPlantSeed;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.OpenPetPackageRequestedMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureToggleEvent;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UseFurnitureEvent extends MessageHandler {

    private static final List<String> PET_PRESENTS = List.of("val11_present", "gnome_box", "leprechaun_box", "velociraptor_egg", "pterosaur_egg", "petbox_epic");

    @Override
    public void handle() {
        try {
            Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

            if (room == null)
                return;

            int itemId = this.packet.readInt();
            int state = this.packet.readInt();

            HabboItem item = room.getHabboItem(itemId);

            if (item == null || item instanceof InteractionDice || item instanceof InteractionSpinningBottle)
                return;

            Event furnitureToggleEvent = new FurnitureToggleEvent(item, this.client.getHabbo(), state);
            Emulator.getPluginManager().fireEvent(furnitureToggleEvent);

            if (furnitureToggleEvent.isCancelled())
                return;

            //Do not move to onClick(). Wired could trigger it.
            if (handleMonsterPlantSeed(room, item)) return;


            if (PET_PRESENTS.contains(item.getBaseItem().getName().toLowerCase()) && room.getCurrentPets().size() < Room.MAXIMUM_PETS) {
                this.client.sendResponse(new OpenPetPackageRequestedMessageComposer(item));
                return;
            }

            item.onClick(this.client, room, new Object[]{state});

            if (item instanceof InteractionWired) {
                this.client.getHabbo().getRoomUnit().setGoalLocation(this.client.getHabbo().getRoomUnit().getCurrentLocation());
            }
        } catch (Exception e) {
            log.error("Caught exception", e);
        }
    }

    private boolean handleMonsterPlantSeed(Room room, HabboItem item) {
        if (item instanceof InteractionMonsterPlantSeed) {
            Emulator.getThreading().run(new QueryDeleteHabboItem(item.getId()));

            boolean isRare = item.getBaseItem().getName().contains("rare");
            int rarity = getRarity(item, isRare);

            MonsterplantPet pet = Emulator.getGameEnvironment().getPetManager().createMonsterplant(room, this.client.getHabbo(), isRare, room.getLayout().getTile(item.getX(), item.getY()), rarity);
            room.sendComposer(new RemoveFloorItemComposer(item, true).compose());
            room.removeHabboItem(item);
            room.updateTile(room.getLayout().getTile(item.getX(), item.getY()));
            room.placePet(pet, item.getX(), item.getY(), item.getZ());
            pet.cycle();
            room.sendComposer(new UserUpdateComposer(pet.getRoomUnit()).compose());
            return true;
        }
        return false;
    }

    private int getRarity(HabboItem item, boolean isRare) {
        if (item.getExtradata().isEmpty() || Integer.parseInt(item.getExtradata()) - 1 < 0) {
            return isRare ? InteractionMonsterPlantSeed.randomGoldenRarityLevel() : InteractionMonsterPlantSeed.randomRarityLevel();
        } else {
            try {
                return Integer.parseInt(item.getExtradata()) - 1;
            } catch (Exception ignored) {
                return 0;
            }
        }
    }
}
