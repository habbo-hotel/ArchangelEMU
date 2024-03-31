package com.eu.habbo.roleplay.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomSpecialTypes;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corps.Corporation;
import com.eu.habbo.roleplay.corps.CorporationManager;
import com.eu.habbo.roleplay.corps.CorporationPosition;
import com.eu.habbo.roleplay.facility.FacilityHospitalsManager;
import com.eu.habbo.roleplay.government.GovernmentManager;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.roleplay.items.interactions.InteractionHospitalBed;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;
import com.eu.habbo.roleplay.weapons.Weapon;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.Random;

public class HabboRoleplayStats implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HabboRoleplayStats.class);

    private static HabboRoleplayStats createNewStats(Habbo habbo) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_users_stats (user_id, corporation_id, corporation_position_id) VALUES (?, ?, ?)")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, GovernmentManager.getInstance().getWelfareCorp().getGuild().getId());
            statement.setInt(3,GovernmentManager.getInstance().getWelfareCorp().getPositionByOrderID(1).getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }

        return load(habbo);
    }

    public static HabboRoleplayStats load(Habbo habbo) {
        HabboRoleplayStats stats = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_users_stats WHERE user_id = ? LIMIT 1")) {
                statement.setInt(1, habbo.getHabboInfo().getId());
                try (ResultSet set = statement.executeQuery()) {
                    set.next();
                    if (set.getRow() != 0) {
                        stats = new HabboRoleplayStats(set, habbo);
                    } else {
                        stats = createNewStats(habbo);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }

        return stats;
    }

    private Habbo habbo;
    @Getter
    private int healthNow;
    @Getter
    private int healthMax;
    @Getter
    private int energyNow;
    @Getter
    private int energyMax;
    @Getter
    private int hungerNow;
    @Getter
    private int hungerMax;
    private int corporationID;
    private int corporationPositionID;
    private Integer gangID;
    private Integer gangPositionID;
    @Getter
    private boolean isDead;

    public void setHealth(int healthCurrent) {
        this.setHealth(healthCurrent, false);
    }

    public void setHealth(int currentHealth, boolean overrideMaxHealth) {
        this.healthNow = currentHealth;

        if (this.healthNow > this.healthMax && overrideMaxHealth) {
            this.healthMax = this.healthNow;
        }

        if (this.healthNow < 0) {
            this.healthNow = 0;
        }

        if (this.healthNow == 0) {
            this.setIsDead(true);
        }

        if (this.healthNow > 0 && this.isDead) {
            this.setIsDead(false);
        }

        if (this.healthNow > 0) {
            String userHealthRemainingMessage = Emulator.getTexts().
                    getValue("commands.roleplay.user_health_remaining")
                    .replace("%currentHealth%", Integer.toString(this.getHealthNow()))
                    .replace("%maximumHealth%", Integer.toString(this.getHealthMax()));
            this.habbo.shout(userHealthRemainingMessage);
        }

        this.habbo.getRoomUnit().getRoom().sendComposer(new UserRoleplayStatsChangeComposer(this.habbo).compose());

    }

    public Corporation getCorporation() {
        return CorporationManager.getInstance().getCorporationByID(this.corporationID);
    }

    public void setCorporation(int corporationID, int corporationPositionID) {
        this.corporationID = corporationID;
        this.corporationPositionID = corporationPositionID;
        this.habbo.getRoomUnit().getRoom().sendComposer(new UserRoleplayStatsChangeComposer(this.habbo).compose());
        this.run();
    }

    public CorporationPosition getCorporationPosition() {
        return this.getCorporation().getPositionByID(this.corporationPositionID);
    }

    public Guild getGang() {
        if (this.gangID == null) {
            return null;
        }
        return Emulator.getGameEnvironment().getGuildManager().getGuild(this.gangID);
    }

    public void setGang(Integer gangID ) {
        this.gangID = gangID;
        this.habbo.getRoomUnit().getRoom().sendComposer(new UserRoleplayStatsChangeComposer(this.habbo).compose());
        this.run();
    }

    public GuildMember getGangPosition() {
        if (this.gangPositionID == null) {
            return null;
        }
        return Emulator.getGameEnvironment().getGuildManager().getGuildMember(this.gangID, this.habbo.getHabboInfo().getId());
    }

    public void setIsDead(boolean isDead) {
        this.isDead = isDead;

        if (this.isDead) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.user_is_dead"));
            Room hospitalRoom = FacilityHospitalsManager.getInstance().getHospital();
            if (hospitalRoom == null) {
                return;
            }
            this.habbo.goToRoom(hospitalRoom.getRoomInfo().getId());
            RoomSpecialTypes types = hospitalRoom.getRoomSpecialTypes();
            THashSet<RoomItem> hospitalBedItems = hospitalRoom.getRoomSpecialTypes().getItemsOfType(InteractionHospitalBed.class);
            for (RoomItem hospitalBedItem : hospitalBedItems) {
                List<RoomTile> hospitalBedRoomTiles = hospitalBedItem.getOccupyingTiles(hospitalRoom.getLayout());
                RoomTile firstAvailableHospitalBedTile = hospitalBedRoomTiles.get(0);
                if (firstAvailableHospitalBedTile == null) {
                    return;
                }
                this.habbo.getRoomUnit().setLocation(firstAvailableHospitalBedTile);
            }
        }
    }

    public int getDamageModifier() {
        Random random = new Random();
        int damageModifier = random.nextInt(10) + 1;
        if (this.habbo.getInventory().getWeaponsComponent().getEquippedWeapon() != null) {
            Weapon equippedWeapon = this.habbo.getInventory().getWeaponsComponent().getEquippedWeapon().getWeapon();
            damageModifier += random.nextInt(equippedWeapon.getMaxDamage() - equippedWeapon.getMinDamage() + equippedWeapon.getMaxDamage());
        }
        return damageModifier;
    }

    private HabboRoleplayStats(ResultSet set, Habbo habbo) throws SQLException {
        this.habbo = habbo;
        this.isDead = set.getInt("health_now") <= 0;
        this.healthNow = set.getInt("health_now");
        this.healthMax = set.getInt("health_max");
        this.energyNow = set.getInt("energy_now");
        this.energyMax = set.getInt("energy_max");
        this.hungerNow = set.getInt("hunger_now");
        this.hungerMax = set.getInt("hunger_max");
        this.corporationID = set.getInt("corporation_id");
        this.corporationPositionID = set.getInt("corporation_position_id");
        this.gangID = set.getInt("gang_id") != 0 ? set.getInt("gang_id") : null;
        this.gangPositionID = set.getInt("gang_position_id") != 0 ? set.getInt("gang_position_id") : null;
    }

    public void dispose() {
        this.run();
        this.habbo = null;
    }

    @Override
    public void run() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE rp_users_stats SET health_now = ?, health_max = ?, energy_now = ?, energy_max = ?, armor_now = ?, armor_max = ?, corporation_id = ?, corporation_position_id = ?, gang_id = ?, gang_position_id = ? WHERE user_id = ? LIMIT 1")) {
                statement.setInt(1, this.healthNow);
                statement.setInt(2, this.healthMax);
                statement.setInt(3, this.energyNow);
                statement.setInt(4, this.energyMax);
                statement.setInt(5, this.hungerNow);
                statement.setInt(6, this.hungerMax);
                statement.setInt(7, this.corporationID);
                statement.setInt(8, this.corporationPositionID);

                if (this.gangID != null) statement.setInt(9, this.gangID);
                if (this.gangID == null) statement.setNull(9, Types.INTEGER);

                if (this.gangPositionID != null) statement.setInt(10, this.gangPositionID);
                if (this.gangPositionID == null) statement.setNull(10, Types.INTEGER);

                statement.setInt(7, this.habbo.getHabboInfo().getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }


}