package com.eu.habbo.roleplay.guilds.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.roleplay.users.HabboWeapon;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WeaponsComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeaponsComponent.class);

    @Getter
    private final THashMap<Integer, HabboWeapon> weapons = new THashMap<Integer, HabboWeapon>();

    @Setter
    @Getter
    private HabboWeapon equippedWeapon;


    public final Guild guild;
    public WeaponsComponent(Guild guild) {
        this.guild = guild;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_guilds_weapons WHERE guild_id = ?")) {
            statement.setInt(1, guild.getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.weapons.put(set.getInt("weapon_id"), new HabboWeapon(set));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public HabboWeapon getWeaponByUniqueName(String uniqueName) {
        for (HabboWeapon weapon : weapons.values()) {
            if (weapon.getWeapon().getUniqueName().equals(uniqueName)) {
                return weapon;
            }
        }
        return null;
    }

    public void createWeapon(int weaponID) {
        HabboWeapon weapon = new HabboWeapon(weaponID, guild.getId());
        this.weapons.put(weaponID, weapon);
    }

    public void dispose() {
        synchronized (this.weapons) {
            this.weapons.clear();
        }
    }

}