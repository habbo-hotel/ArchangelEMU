package com.eu.habbo.threading.runnables.teleport;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class TeleportAction implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeleportAction.class);

    private final RoomItem currentTeleport;
    private final Room room;
    private final GameClient client;

    @Override
    public void run() {
        if (this.client.getHabbo().getRoomUnit().getRoom() != this.room) return;

        // Move user to the teleport tile
        if (this.client.getHabbo().getRoomUnit().getCurrentPosition() != this.room.getLayout().getTile(this.currentTeleport.getCurrentPosition().getX(), this.currentTeleport.getCurrentPosition().getY())) {
            this.client.getHabbo().getRoomUnit().setLocation(this.room.getLayout().getTile(this.currentTeleport.getCurrentPosition().getX(), this.currentTeleport.getCurrentPosition().getY()));
            this.client.getHabbo().getRoomUnit().setRotation(RoomRotation.values()[(this.currentTeleport.getRotation() + 4) % 8]);
            this.client.getHabbo().getRoomUnit().addStatus(RoomUnitStatus.MOVE, this.currentTeleport.getCurrentPosition().getX() + "," + this.currentTeleport.getCurrentPosition().getY() + "," + this.currentTeleport.getCurrentZ());
            this.room.scheduledComposers.add(new UserUpdateComposer(this.client.getHabbo().getRoomUnit()).compose());
        }

        // Remove the MOVE status
        this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
        this.room.sendComposer(new UserUpdateComposer(this.client.getHabbo().getRoomUnit()).compose());

        InteractionTeleport teleport = (InteractionTeleport) this.currentTeleport;
        if (teleport.getTargetRoomId() > 0 && teleport.getTargetId() > 0) {
            int id = teleport.getTargetId();
            RoomItem targetItem = this.room.getRoomItemManager().getRoomItemById(id);
            if (targetItem == null || ((InteractionTeleport) targetItem).getTargetRoomId() != teleport.getTargetRoomId()) {
                teleport.setTargetRoomId(0);
                teleport.setTargetId(0);
                if (targetItem != null) {
                    ((InteractionTeleport) targetItem).setTargetId(0);
                    ((InteractionTeleport) targetItem).setTargetRoomId(0);
                }
            }
        } else {
            teleport.setTargetRoomId(0);
            teleport.setTargetId(0);

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT items_teleports.*, A.room_id as a_room_id, A.id as a_id, B.room_id as b_room_id, B.id as b_id " +
                                 "FROM items_teleports " +
                                 "INNER JOIN items AS A ON items_teleports.teleport_one_id = A.id " +
                                 "INNER JOIN items AS B ON items_teleports.teleport_two_id = B.id " +
                                 "WHERE (teleport_one_id = ? OR teleport_two_id = ?)")) {
                statement.setInt(1, this.currentTeleport.getId());
                statement.setInt(2, this.currentTeleport.getId());
                try (ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        if (set.getInt("a_id") != this.currentTeleport.getId()) {
                            teleport.setTargetId(set.getInt("a_id"));
                            teleport.setTargetRoomId(set.getInt("a_room_id"));
                        } else {
                            teleport.setTargetId(set.getInt("b_id"));
                            teleport.setTargetRoomId(set.getInt("b_room_id"));
                        }
                    }
                }
            } catch (SQLException e) {
                    TeleportAction.LOGGER.error("Caught SQL exception", e);
            }
        }

        this.currentTeleport.setExtraData("0");
        this.room.updateItem(this.currentTeleport);

        if (teleport.getTargetRoomId() == 0) {
            Emulator.getThreading().run(new TeleportActionFive(this.currentTeleport, this.room, this.client), 0);
            return;
        }

        Room targetRoom = this.room;
        if (this.currentTeleport.getRoomId() != teleport.getTargetRoomId()) {
            targetRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(teleport.getTargetRoomId());
        }

        if (targetRoom == null) {
            Emulator.getThreading().run(new TeleportActionFive(this.currentTeleport, this.room, this.client), 0);
            return;
        }

        if (targetRoom.isPreLoaded()) {
            targetRoom.loadData();
        }

        RoomItem targetTeleport = targetRoom.getRoomItemManager().getRoomItemById(teleport.getTargetId());
        if (targetTeleport == null) {
            Emulator.getThreading().run(new TeleportActionFive(this.currentTeleport, this.room, this.client), 0);
            return;
        }

        RoomTile teleportLocation = targetRoom.getLayout().getTile(targetTeleport.getCurrentPosition().getX(), targetTeleport.getCurrentPosition().getY());
        if (teleportLocation == null) {
            Emulator.getThreading().run(new TeleportActionFive(this.currentTeleport, this.room, this.client), 0);
            return;
        }

        this.client.getHabbo().getRoomUnit().setLocation(teleportLocation);
        this.client.getHabbo().getRoomUnit().getPath().clear();
        this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
        this.client.getHabbo().getRoomUnit().setCurrentZ(teleportLocation.getStackHeight());

        if (targetRoom != this.room) {
            this.room.getRoomUnitManager().removeHabbo(this.client.getHabbo(), false);
            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), targetRoom.getRoomInfo().getId(), "", Emulator.getConfig().getBoolean("hotel.teleport.locked.allowed"), teleportLocation);
        }

        this.client.getHabbo().getRoomUnit().setRotation(RoomRotation.values()[targetTeleport.getRotation() % 8]);
        targetTeleport.setExtraData("2");
        targetRoom.updateItem(targetTeleport);
        this.client.getHabbo().getRoomUnit().setRoom(targetRoom);
        Emulator.getThreading().run(new TeleportActionFour(targetTeleport, targetRoom, this.client), 0);
    }
}
