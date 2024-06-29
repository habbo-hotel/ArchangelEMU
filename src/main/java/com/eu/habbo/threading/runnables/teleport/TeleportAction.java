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
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class TeleportAction implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeleportAction.class);

    private final RoomItem currentTeleport;
    private final Room room;
    private final GameClient client;

    @Override
    public void run() {
        synchronized (this.room) {
            if (this.client.getHabbo().getRoomUnit().getRoom() != this.room) return;

            // Remove the MOVE status
            this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
            this.room.sendComposer(new UserUpdateComposer(this.client.getHabbo().getRoomUnit()).compose());
        }

        // Resolve teleport target asynchronously
        CompletableFuture<Void> teleportSetupFuture = CompletableFuture.supplyAsync(this::resolveTeleportTarget)
                .thenAcceptAsync(teleport -> {
                    synchronized (this.room) {
                        if (teleport.getTargetRoomId() == 0) {
                            this.client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.teleport.no_connect"));
                        } else {
                            proceedToTargetRoom(teleport);
                        }
                    }
                })
                .exceptionally(throwable -> {
                    TeleportAction.LOGGER.error("Error resolving teleport target", throwable);
                    return null;
                });

        // Wait for completion of teleport setup
        teleportSetupFuture.join();
    }

    private InteractionTeleport resolveTeleportTarget() {
        synchronized (this.room) {
            InteractionTeleport teleport = (InteractionTeleport) this.currentTeleport;

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
                TeleportAction.LOGGER.error("Caught SQL exception while resolving teleport target", e);
            }

            return teleport;
        }
    }

    private void proceedToTargetRoom(InteractionTeleport teleport) {
        synchronized (this.room) {
            if (this.currentTeleport.getRoomId() != teleport.getTargetRoomId()) {
                Room targetRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(teleport.getTargetRoomId());
                if (targetRoom == null) {
                    return;
                }

                if (targetRoom.isPreLoaded()) {
                    targetRoom.loadData();
                }

                RoomItem targetTeleport = targetRoom.getRoomItemManager().getRoomItemById(teleport.getTargetId());
                if (targetTeleport == null) {
                    this.proceedToTeleportCompletion(null);
                    return;
                }

                RoomTile teleportLocation = targetRoom.getLayout().getTile(targetTeleport.getCurrentPosition().getX(), targetTeleport.getCurrentPosition().getY());
                if (teleportLocation == null) {
                    this.proceedToTeleportCompletion(targetTeleport);
                    return;
                }

                RoomTile targetTile = this.room.getLayout().getTile(this.currentTeleport.getCurrentPosition().getX(), this.currentTeleport.getCurrentPosition().getY());
                if (!this.client.getHabbo().getRoomUnit().getCurrentPosition().equals(targetTile)) {
                    this.client.getHabbo().getRoomUnit().setLocation(teleportLocation);
                    this.client.getHabbo().getRoomUnit().getPath().clear();
                    this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
                    this.client.getHabbo().getRoomUnit().setCurrentZ(teleportLocation.getStackHeight());
                }

                if (targetRoom != this.room) {
                    this.room.getRoomUnitManager().removeHabbo(this.client.getHabbo(), false);
                    Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), targetRoom.getRoomInfo().getId(), "", Emulator.getConfig().getBoolean("hotel.teleport.locked.allowed"), teleportLocation);
                }

                this.client.getHabbo().getRoomUnit().setRotation(RoomRotation.values()[targetTeleport.getRotation() % 8]);
                targetTeleport.setExtraData("1");
                targetRoom.updateItem(targetTeleport);
                this.client.getHabbo().getRoomUnit().setRoom(targetRoom);

                Emulator.getThreading().run(() -> proceedToTeleportCompletion(targetTeleport), 0);
            }
        }
    }

    private void proceedToTeleportCompletion(RoomItem targetTeleport) {
        synchronized (this.room) {
            this.client.getHabbo().getRoomUnit().setCanWalk(true);
            this.client.getHabbo().getRoomUnit().setLeavingTeleporter(true);

            this.client.getHabbo().getRoomUnit().setLeavingTeleporter(false);
            this.client.getHabbo().getRoomUnit().setTeleporting(false);
            this.client.getHabbo().getRoomUnit().setCanWalk(true);

            targetTeleport.setExtraData("0");
        }
    }
}
