package com.eu.habbo.habbohotel.rooms.infractions;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.types.IRoomManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.UserUnbannedFromRoomComposer;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Getter
@Slf4j
public class RoomInfractionManager extends IRoomManager {

    private final TIntObjectHashMap<RoomBan> bannedHabbos;
    private final TIntIntHashMap mutedHabbos;

    public RoomInfractionManager(Room room) {
        super(room);
        this.bannedHabbos = new TIntObjectHashMap<>();
        this.mutedHabbos = new TIntIntHashMap();
    }

    public void loadBans(Connection connection) {
        this.bannedHabbos.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username, users.id, room_bans.* FROM room_bans INNER JOIN users ON room_bans.user_id = users.id WHERE ends > ? AND room_bans.room_id = ?")) {
            statement.setInt(1, Emulator.getIntUnixTimestamp());
            statement.setInt(2, room.getRoomInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (this.bannedHabbos.containsKey(set.getInt(DatabaseConstants.USER_ID)))
                        continue;

                    this.bannedHabbos.put(set.getInt(DatabaseConstants.USER_ID), new RoomBan(set));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }

    public void unbanHabbo(int userId) {
        RoomBan ban = this.bannedHabbos.remove(userId);

        if (ban != null) {
            ban.delete();
        }

        this.sendComposer(new UserUnbannedFromRoomComposer(room, userId).compose());
    }

    public boolean isBanned(Habbo habbo) {
        RoomBan ban = this.bannedHabbos.get(habbo.getHabboInfo().getId());

        boolean banned = ban != null && ban.getEndTimestamp() > Emulator.getIntUnixTimestamp() && !habbo.hasPermissionRight(Permission.ACC_ANYROOMOWNER) && !habbo.hasPermissionRight(Permission.ACC_ENTERANYROOM);

        if (!banned && ban != null) {
            this.unbanHabbo(habbo.getHabboInfo().getId());
        }

        return banned;
    }

    public void addRoomBan(RoomBan roomBan) {
        this.bannedHabbos.put(roomBan.getUserId(), roomBan);
    }

    public void muteHabbo(Habbo habbo, int minutes) {
        synchronized (this.mutedHabbos) {
            this.mutedHabbos.put(habbo.getHabboInfo().getId(), Emulator.getIntUnixTimestamp() + (minutes * 60));
        }
    }

    public boolean isMuted(Habbo habbo) {
        if (room.getRoomInfo().isRoomOwner(habbo) || room.getRoomRightsManager().hasRights(habbo))
            return false;

        if (this.mutedHabbos.containsKey(habbo.getHabboInfo().getId())) {
            boolean time = this.mutedHabbos.get(habbo.getHabboInfo().getId()) > Emulator.getIntUnixTimestamp();

            if (!time) {
                this.mutedHabbos.remove(habbo.getHabboInfo().getId());
            }

            return time;
        }

        return false;
    }


    public void floodMuteHabbo(Habbo habbo, int timeOut) {
        habbo.getHabboStats().setMutedCount(habbo.getHabboStats().getMutedCount() + 1);
        timeOut += (timeOut * (int) Math.ceil(Math.pow(habbo.getHabboStats().getMutedCount(), 2)));
        habbo.getHabboStats().getChatCounter().set(0);
        habbo.mute(timeOut, true);
    }
}
