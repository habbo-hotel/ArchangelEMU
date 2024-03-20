package com.eu.habbo.roleplay.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomSpecialTypes;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.facility.FacilityHospitalsManager;
import com.eu.habbo.roleplay.items.interactions.InteractionHospitalBed;
import com.eu.habbo.roleplay.weapons.Weapon;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.Random;

public class HabboRoleplayStats implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HabboRoleplayStats.class);

    private static HabboRoleplayStats createNewStats(Habbo habbo) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_users_stats (user_id) VALUES (?)")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
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

    protected Habbo habbo;

    protected int currentHealth;

    public int getCurrentHealth() {
        return this.currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.setCurrentHealth(currentHealth, false);
    }

    public void setCurrentHealth(int currentHealth, boolean overrideMaxHealth) {
        this.currentHealth = currentHealth;
        if (this.currentHealth > this.maximumHealth && overrideMaxHealth) {
            this.currentHealth = this.maximumHealth;
        }

        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }

        if (this.currentHealth == 0) {
            this.setIsDead(true);
        }

        if (this.currentHealth > 0 && this.isDead) {
            this.setIsDead(false);
        }

        if (this.currentHealth > 0) {
            String userHealthRemainingMessage = Emulator.getTexts().
                    getValue("commands.roleplay.user_health_remaining")
                    .replace("%currentHealth%", Integer.toString(this.getCurrentHealth()))
                    .replace("%maximumHealth%", Integer.toString(this.getMaximumHealth()));
            this.habbo.shout(userHealthRemainingMessage);
        }
    }

    protected int maximumHealth;

    public int getMaximumHealth() {
        return this.maximumHealth;
    }

    public void setMaximumHealth(int healthPoints) {
        this.maximumHealth = healthPoints;
    }


    @Getter
    @Setter
    private int corporationID;

    @Getter
    @Setter
    private int corporationPositionID;


    @Getter
    protected boolean isDead;
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
        this.isDead = set.getInt("current_health") <= 0;
        this.currentHealth = set.getInt("current_health");
        this.maximumHealth = set.getInt("maximum_health");
        this.corporationID = set.getInt("corporation_id");
        this.corporationPositionID = set.getInt("corporation_position_id");
    }

    public void dispose() {
        this.run();
        this.habbo = null;
    }

    @Override
    public void run() {

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE rp_users_stats SET current_health = ?, maximum_health = ?, corporation_id = ?, corporation_position_id = ? WHERE user_id = ? LIMIT 1")) {
                statement.setInt(1, this.currentHealth);
                statement.setInt(2, this.maximumHealth);
                statement.setInt(3, this.corporationID);
                statement.setInt(4, this.corporationPositionID);
                statement.setInt(5, this.habbo.getHabboInfo().getId());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }


}