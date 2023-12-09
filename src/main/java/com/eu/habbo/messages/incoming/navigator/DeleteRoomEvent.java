package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.PetAddedToInventoryComposer;
import com.eu.habbo.plugin.events.navigator.NavigatorRoomDeletedEvent;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeleteRoomEvent extends MessageHandler {


    @Override
    public void handle() {
        int roomId = this.packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getActiveRoomById(roomId);

        if(room == null) {
            return;
        }

        if(!room.getRoomInfo().isRoomOwner(this.client.getHabbo())) {
            String message = Emulator.getTexts().getValue("scripter.warning.room.delete").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%roomname%", room.getRoomInfo().getName()).replace("%roomowner%", room.getRoomInfo().getOwnerInfo().getUsername());
            ScripterManager.scripterDetected(this.client, message);
            log.info(message);
            return;
        }

        if (room.getRoomInfo().getId() == this.client.getHabbo().getHabboInfo().getHomeRoom()) {
            return;
        }

        if (Emulator.getPluginManager().fireEvent(new NavigatorRoomDeletedEvent(this.client.getHabbo(), room)).isCancelled()) {
            return;
        }

        room.getRoomItemManager().ejectAllFurni();
        room.getRoomItemManager().ejectUserFurni(room.getRoomInfo().getOwnerInfo().getId());

        List<Bot> bots = new ArrayList<>(room.getRoomUnitManager().getRoomBotManager().getCurrentBots().values());
        for (Bot bot : bots) {
            Emulator.getGameEnvironment().getBotManager().pickUpBot(bot, null, room);
        }

        List<Pet> pets = new ArrayList<>(room.getRoomUnitManager().getRoomPetManager().getCurrentPets().values());
        for (Pet pet : pets) {
            if (pet instanceof RideablePet rideablePet) {
                if (rideablePet.getRider() != null) {
                    rideablePet.getRider().getRoomUnit().dismountPet(true);
                }
            }

            pet.removeFromRoom();
            Emulator.getThreading().run(pet);

            Habbo owner = Emulator.getGameEnvironment().getHabboManager().getHabbo(pet.getUserId());

            if (owner != null) {
                owner.getClient().sendResponse(new PetAddedToInventoryComposer(pet));
                owner.getInventory().getPetsComponent().addPet(pet);
            }
        }

        if (room.getRoomInfo().hasGuild()) {
            Emulator.getGameEnvironment().getGuildManager().deleteGuild(room.getRoomInfo().getGuild());
        }

        room.preventUnloading = false;

        room.getRoomRightsManager().removeAllRights();
        room.getRoomWordFilterManager().removeAllWords();
        room.dispose();
        Emulator.getGameEnvironment().getRoomManager().uncacheRoom(room);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rooms WHERE id = ? LIMIT 1")) {
                statement.setInt(1, roomId);
                statement.execute();
            }

            if (room.getRoomInfo().isModelOverridden()) {
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM room_models_custom WHERE id = ? LIMIT 1")) {
                    stmt.setInt(1, roomId);
                    stmt.execute();
                }
            }

            try (PreparedStatement votes = connection.prepareStatement("DELETE FROM room_votes WHERE room_id = ?")) {
                votes.setInt(1, roomId);
                votes.execute();
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }
}
