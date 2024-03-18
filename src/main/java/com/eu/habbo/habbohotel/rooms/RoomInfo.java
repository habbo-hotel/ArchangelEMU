package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.constants.RoomState;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Getter
@Setter
@Slf4j
public class RoomInfo {
    private final int id;
    private HabboInfo ownerInfo;
    private volatile RoomCategory category;
    private String name;
    private String description;
    private RoomState state;
    private int maxUsers;
    private String password;
    private Guild guild;
    private volatile int score;
    private String tags;
    private String floorPaint;
    private String wallPaint;
    private String landscapePaint;
    private int wallThickness;
    private int wallHeight;
    private int floorThickness;
    private volatile boolean hideWalls;
    private volatile boolean allowPets;
    private volatile boolean allowPetsEat;
    private volatile boolean allowWalkthrough;
    private volatile int chatMode;
    private volatile int chatWeight;
    private volatile int chatSpeed;
    private volatile int chatDistance;
    private volatile int chatProtection;
    private volatile int whoCanMuteOption;
    private volatile int whoCanKickOption;
    private volatile int whoCanBanOption;
    private volatile int tradeMode;
    private volatile boolean diagonalMoveEnabled;
    private volatile int rollerSpeed;
    private int pollId;
    private volatile boolean jukeboxEnabled;
    private volatile boolean hiddenWiredEnabled;
    private volatile boolean staffPicked;
    private volatile boolean promoted;
    private volatile boolean publicRoom;
    private boolean modelOverridden;
    private HashMap<Integer, RoomMoodlightData> moodLightData;
    private final HashMap<Integer, RoomMoodlightData> defaultMoodData;

    public RoomInfo(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.ownerInfo = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(set.getInt("owner_id"));
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.category = Emulator.getGameEnvironment().getRoomManager().getCategory(set.getInt("category"));
        this.state = RoomState.valueOf(set.getString("state").toUpperCase());
        this.maxUsers = set.getInt("users_max");
        this.password = set.getString("password");
        this.guild = Emulator.getGameEnvironment().getGuildManager().getGuild(set.getInt("guild_id"));
        this.score = set.getInt("score");
        this.tags = set.getString("tags");

        this.floorPaint = set.getString("paper_floor");
        this.wallPaint = set.getString("paper_wall");
        this.landscapePaint = set.getString("paper_landscape");
        this.wallThickness = set.getInt("thickness_wall");
        this.wallHeight = set.getInt("wall_height");
        this.floorThickness = set.getInt("thickness_floor");
        this.hideWalls = set.getBoolean("allow_hidewall");

        this.allowPets = set.getBoolean("allow_other_pets");
        this.allowPetsEat = set.getBoolean("allow_other_pets_eat");
        this.allowWalkthrough = set.getBoolean("allow_walkthrough");

        this.chatMode = set.getInt("chat_mode");
        this.chatWeight = set.getInt("chat_weight");
        this.chatSpeed = set.getInt("chat_speed");
        this.chatDistance = set.getInt("chat_hearing_distance");
        this.chatProtection = set.getInt("chat_protection");

        this.whoCanMuteOption = set.getInt("who_can_mute");
        this.whoCanKickOption = set.getInt("who_can_kick");
        this.whoCanBanOption = set.getInt("who_can_ban");
        this.tradeMode = set.getInt("trade_mode");
        this.diagonalMoveEnabled = set.getBoolean("move_diagonally");

        this.rollerSpeed = set.getInt("roller_speed");

        this.pollId = set.getInt("poll_id");

        this.jukeboxEnabled = set.getBoolean("jukebox_active");
        this.hiddenWiredEnabled = set.getBoolean("hidewired");

        this.staffPicked = set.getBoolean("is_staff_picked");
        this.promoted = set.getBoolean("promoted");
        this.publicRoom = set.getBoolean("is_public");

        this.modelOverridden = set.getBoolean("override_model");

        defaultMoodData = new HashMap<>();

        for (int i = 1; i <= 3; i++) {
            RoomMoodlightData data = RoomMoodlightData.fromString("");
            data.setId(i);
            defaultMoodData.put(i, data);
        }


        this.moodLightData = new HashMap<>();

        for (String s : set.getString("moodlight_data").split(";")) {
            RoomMoodlightData data = RoomMoodlightData.fromString(s);
            this.moodLightData.put(data.getId(), data);
        }

    }

    public void update(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET name = ?, description = ?, password = ?, state = ?, users_max = ?, category = ?, score = ?, paper_floor = ?, paper_wall = ?, paper_landscape = ?, thickness_wall = ?, wall_height = ?, thickness_floor = ?, moodlight_data = ?, tags = ?, allow_other_pets = ?, allow_other_pets_eat = ?, allow_walkthrough = ?, allow_hidewall = ?, chat_mode = ?, chat_weight = ?, chat_speed = ?, chat_hearing_distance = ?, chat_protection =?, who_can_mute = ?, who_can_kick = ?, who_can_ban = ?, poll_id = ?, guild_id = ?, roller_speed = ?, override_model = ?, is_staff_picked = ?, promoted = ?, trade_mode = ?, move_diagonally = ?, owner_id = ?, owner_name = ?, jukebox_active = ?, hidewired = ? WHERE id = ?")) {
            statement.setString(1, this.name);
            statement.setString(2, this.description);
            statement.setString(3, this.password);
            statement.setString(4, this.state.name().toLowerCase());
            statement.setInt(5, this.maxUsers);
            statement.setInt(6, this.category.getId());
            statement.setInt(7, this.score);
            statement.setString(8, this.floorPaint);
            statement.setString(9, this.wallPaint);
            statement.setString(10, this.landscapePaint);
            statement.setInt(11, this.wallThickness);
            statement.setInt(12, this.wallHeight);
            statement.setInt(13, this.floorThickness);
            StringBuilder moodLightData = new StringBuilder();

            int moodLightId = 1;
            for (RoomMoodlightData data : this.moodLightData.values()) {
                data.setId(moodLightId);
                moodLightData.append(data).append(";");
                moodLightId++;
            }

            statement.setString(14, moodLightData.toString());
            statement.setString(15, this.tags);
            statement.setString(16, this.allowPets ? "1" : "0");
            statement.setString(17, this.allowPetsEat ? "1" : "0");
            statement.setString(18, this.allowWalkthrough ? "1" : "0");
            statement.setString(19, this.hideWalls ? "1" : "0");
            statement.setInt(20, this.chatMode);
            statement.setInt(21, this.chatWeight);
            statement.setInt(22, this.chatSpeed);
            statement.setInt(23, this.chatDistance);
            statement.setInt(24, this.chatProtection);
            statement.setInt(25, this.whoCanMuteOption);
            statement.setInt(26, this.whoCanKickOption);
            statement.setInt(27, this.whoCanBanOption);
            statement.setInt(28, this.pollId);
            statement.setInt(29, (this.hasGuild()) ? this.guild.getId() : 0);
            statement.setInt(30, this.rollerSpeed);
            statement.setString(31, this.modelOverridden ? "1" : "0");
            statement.setString(32, this.staffPicked ? "1" : "0");
            statement.setString(33, this.promoted ? "1" : "0");
            statement.setInt(34, this.tradeMode);
            statement.setString(35, this.diagonalMoveEnabled ? "1" : "0");
            statement.setInt(36, this.ownerInfo.getId());
            statement.setString(37, this.ownerInfo.getUsername());
            statement.setString(38, this.jukeboxEnabled ? "1" : "0");
            statement.setString(39, this.hiddenWiredEnabled ? "1" : "0");
            statement.setInt(40, this.id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }

    public boolean hasGuild() {
        return this.guild != null;
    }

    public String getOwnerName() {
        return this.getOwnerInfo().getUsername();
    }

    public boolean isRoomOwner(Habbo owner) {
        return this.ownerInfo.getId() == owner.getHabboInfo().getId() || owner.hasPermissionRight(Permission.ACC_ANYROOMOWNER);
    }

    public String[] filterAnything() {
        return new String[]{this.getOwnerInfo().getUsername(), this.getGuildName(), this.getDescription()};
    }

    public String getGuildName() {
        if (this.guild == null) {
            return "";
        }

        return this.guild.getName();
    }

    public void setName(String name) {
        this.name = name;

        if (this.name.length() > 50) {
            this.name = this.name.substring(0, 50);
        }

        if (this.guild != null) {
            this.guild.setRoomName(name);
        }
    }

    public void setDescription(String description) {
        this.description = description;

        if (this.description.length() > 250) {
            this.description = this.description.substring(0, 250);
        }
    }

    public void setPassword(String password) {
        this.password = password;

        if (this.password.length() > 20) {
            this.password = this.password.substring(0, 20);
        }
    }
}