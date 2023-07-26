package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.*;
import com.eu.habbo.plugin.events.users.UserRightsTakenEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
@Getter
@Setter
public class RoomRightsManager {
    private final Room room;
    private final List<Integer> rights;

    public RoomRightsManager(Room room) {
        this.room = room;
        this.rights = new ArrayList<>();
    }

    public synchronized void load(Connection connection) {
        this.loadRights(connection);
    }

    private void loadRights(Connection connection) {
        this.rights.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM room_rights WHERE room_id = ?")) {
            statement.setInt(1, this.room.getRoomInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.rights.add(set.getInt(DatabaseConstants.USER_ID));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error("Caught Exception", e);
        }
    }

    public boolean hasRights(Habbo habbo) {
        return this.room.getRoomInfo().isRoomOwner(habbo) || this.rights.contains(habbo.getHabboInfo().getId()) || (habbo.getRoomUnit().getRightsLevel() != RoomRightLevels.NONE && this.room.getRoomUnitManager().getCurrentHabbos().containsKey(habbo.getHabboInfo().getId()));
    }

    public HashMap<Integer, String> getUsersWithRights() {
        HashMap<Integer, String> rightsMap = new HashMap<>();

        if (this.rights.isEmpty()) {
            return rightsMap;
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT users.username AS username, users.id AS user_id FROM room_rights INNER JOIN users ON room_rights.user_id = users.id WHERE room_id = ?")) {
            statement.setInt(1, this.room.getRoomInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int userId = set.getInt("user_id");
                    String username = set.getString("username");
                    rightsMap.put(userId, username);
                }
            }
        } catch (SQLException e) {
            log.error("Error while fetching users with rights:", e);
        }

        return rightsMap;
    }

    public void giveRights(Habbo habbo) {
        if (this.rights.contains(habbo.getHabboInfo().getId())) {
            return;
        }

        if (this.rights.add(habbo.getHabboInfo().getId())) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_rights VALUES (?, ?)")) {
                statement.setInt(1, this.room.getRoomInfo().getId());
                statement.setInt(2, habbo.getHabboInfo().getId());
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }

        this.room.getRoomRightsManager().refreshRightsForHabbo(habbo);
        this.room.sendComposer(new FlatControllerAddedComposer(this.room, habbo.getHabboInfo().getId(), habbo.getHabboInfo().getUsername()).compose());
    }

    public void giveRights(MessengerBuddy buddy) {
        if (this.rights.contains(buddy.getId())) {
            return;
        }

        this.rights.add(buddy.getId());

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_rights VALUES (?, ?)")) {
            statement.setInt(1, this.room.getRoomInfo().getId());
            statement.setInt(2, buddy.getId());
            statement.execute();
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        Habbo roomOwner = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.room.getRoomInfo().getOwnerInfo().getId());

        if (roomOwner != null) {
            this.room.sendComposer(new FlatControllerAddedComposer(this.room, buddy.getId(), buddy.getUsername()).compose());
        }
    }

    public void removeRights(int userId) {
        Habbo habbo = this.room.getRoomUnitManager().getRoomHabboById(userId);

        if (Emulator.getPluginManager().fireEvent(new UserRightsTakenEvent(this.room.getRoomUnitManager().getRoomHabboById(this.room.getRoomInfo().getOwnerInfo().getId()), userId, habbo)).isCancelled())
            return;

        this.room.sendComposer(new FlatControllerRemovedComposer(this.room, userId).compose());

        if (this.rights.remove(Integer.valueOf(userId))) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ? AND user_id = ?")) {
                statement.setInt(1, this.room.getRoomInfo().getId());
                statement.setInt(2, userId);
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }

        if (habbo != null) {
            this.room.getRoomItemManager().ejectUserFurni(habbo.getHabboInfo().getId());
            habbo.getRoomUnit().setRightsLevel(RoomRightLevels.NONE);
            habbo.getRoomUnit().removeStatus(RoomUnitStatus.FLAT_CONTROL);
            this.refreshRightsForHabbo(habbo);
        }
    }

    public void removeAllRights() {
        for (int userId : this.rights) {
            this.room.getRoomItemManager().ejectUserFurni(userId);
        }

        this.rights.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ?")) {
            statement.setInt(1, this.room.getRoomInfo().getId());
            statement.execute();
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        this.refreshRightsInRoom();
    }

    private void refreshRightsInRoom() {
        for (Habbo habbo : this.room.getRoomUnitManager().getCurrentHabbos().values()) {
            if (habbo.getRoomUnit().getRoom() == room) {
                this.refreshRightsForHabbo(habbo);
            }
        }
    }

    public void refreshRightsForHabbo(Habbo habbo) {
        RoomItem item;
        RoomRightLevels flatCtrl = RoomRightLevels.NONE;

        if (habbo.getHabboStats().isRentingSpace()) {
            item = this.room.getRoomItemManager().getCurrentItems().get(habbo.getHabboStats().getRentedItemId());

            if (item != null) {
                return;
            }
        }

        if (habbo.hasPermissionRight(Permission.ACC_ANYROOMOWNER) || this.room.getRoomInfo().isRoomOwner(habbo)) {
            habbo.getClient().sendResponse(new YouAreOwnerMessageComposer());
            flatCtrl = RoomRightLevels.MODERATOR;
        } else if (this.hasRights(habbo) && !this.room.getRoomInfo().hasGuild()) {
            flatCtrl = RoomRightLevels.RIGHTS;
        } else if (this.room.getRoomInfo().hasGuild()) {
            flatCtrl = this.room.getGuildRightLevel(habbo);
        }

        habbo.getClient().sendResponse(new YouAreControllerMessageComposer(flatCtrl));

        habbo.getRoomUnit().setStatus(RoomUnitStatus.FLAT_CONTROL, String.valueOf(flatCtrl.getLevel()));
        habbo.getRoomUnit().setRightsLevel(flatCtrl);

        if (flatCtrl.equals(RoomRightLevels.MODERATOR)) {
            habbo.getClient().sendResponse(new FlatControllersComposer(this.room));
        }
    }
}
