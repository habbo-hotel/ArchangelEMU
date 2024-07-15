package com.eu.habbo.roleplay.users.inventory;


import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.users.HabboTonic;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TonicsComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(TonicsComponent.class);

    @Getter
    private final THashMap<Integer, HabboTonic> tonics = new THashMap<Integer, HabboTonic>();

    public final Habbo habbo;
    public TonicsComponent(Habbo habbo) {
        this.habbo = habbo;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_users_tonics WHERE user_id = ?")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.tonics.put(set.getInt("tonic_id"), new HabboTonic(set));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        if(habbo.getHabboInfo().getPermissionGroup().getRoomEffect() > 0)
            this.createTonic(habbo.getHabboInfo().getPermissionGroup().getRoomEffect());
    }

    public HabboTonic getTonicByUniqueName(String uniqueName) {
        for (HabboTonic tonic : tonics.values()) {
            if (tonic.getTonic().getUniqueName().equals(uniqueName)) {
                return tonic;
            }
        }
        return null;
    }

    public void createTonic(int tonicID) {
        HabboTonic tonic = new HabboTonic(tonicID, habbo.getHabboInfo().getId());
        this.tonics.put(tonicID, tonic);
    }

    public void dispose() {
        synchronized (this.tonics) {
            this.tonics.clear();
        }
    }

}