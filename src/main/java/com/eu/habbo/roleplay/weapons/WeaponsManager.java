package com.eu.habbo.roleplay.weapons;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WeaponsManager {
    private static WeaponsManager instance;

    public static WeaponsManager getInstance() {
        if (instance == null) {
            instance = new WeaponsManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WeaponsManager.class);

    private TIntObjectHashMap<Weapon> weapons;

    public Weapon getWeaponByID(int weaponID) {
        return this.weapons.get(weaponID);
    }


    public WeaponsManager() {
        long millis = System.currentTimeMillis();
        this.weapons = new TIntObjectHashMap<>();

        this.reload();

        LOGGER.info("Weapons Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void reload() {
        this.loadWeapons();
    }

    private void loadWeapons() {
        this.weapons.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_weapons ORDER BY id ASC")) {
            while (set.next()) {
                Weapon weapon = null;
                if (!this.weapons.containsKey(set.getInt("id"))) {
                    weapon = new Weapon(set);
                    this.weapons.put(set.getInt("id"), weapon);
                } else {
                    weapon = this.weapons.get(set.getInt("id"));
                    weapon.load(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void dispose() {
        this.weapons = null;
    }
}