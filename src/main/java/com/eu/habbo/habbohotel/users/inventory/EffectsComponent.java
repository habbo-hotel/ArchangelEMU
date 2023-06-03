package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.inventory.AvatarEffectActivatedMessageComposer;
import com.eu.habbo.messages.outgoing.inventory.AvatarEffectAddedMessageComposer;
import com.eu.habbo.messages.outgoing.inventory.AvatarEffectExpiredMessageComposer;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
@Getter
public class EffectsComponent {

    private final THashMap<Integer, HabboEffect> effects = new THashMap<>();
    private final Habbo habbo;
    @Setter
    private int activatedEffect = 0;

    public EffectsComponent(Habbo habbo) {
        this.habbo = habbo;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_effects WHERE user_id = ?")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.effects.put(set.getInt("effect"), new HabboEffect(set));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }
        if(habbo.getHabboInfo().getPermissionGroup().getRoomEffect() > 0)
            this.createRankEffect(habbo.getHabboInfo().getPermissionGroup().getRoomEffect());
    }

    public HabboEffect createEffect(int effectId) {
        return createEffect(effectId, 86400);
    }

    public HabboEffect createEffect(int effectId, int duration) {
        HabboEffect effect;
        synchronized (this.effects) {
            if (this.effects.containsKey(effectId)) {
                effect = this.effects.get(effectId);

                if (effect.getTotal() <= 99) {
                    effect.setTotal(effect.getTotal() + 1);
                }
            } else {
                effect = new HabboEffect(effectId, this.habbo.getHabboInfo().getId());
                effect.setDuration(duration);
                effect.insert();
            }

            this.addEffect(effect);
        }

        return effect;
    }

    public HabboEffect createRankEffect(int effectId) {
        HabboEffect rankEffect = new HabboEffect(effectId, habbo.getHabboInfo().getId());
        rankEffect.setDuration(0);
        rankEffect.setRankEnable(true);
        rankEffect.setActivationTimestamp(Emulator.getIntUnixTimestamp());
        rankEffect.setEnabled(true);
        this.effects.put(effectId, rankEffect);
        this.activatedEffect = effectId; // enabled by default
        return rankEffect;
    }

    public void addEffect(HabboEffect effect) {
        this.effects.put(effect.getEffect(), effect);

        this.habbo.getClient().sendResponse(new AvatarEffectAddedMessageComposer(effect));
    }

    public void dispose() {
        synchronized (this.effects) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users_effects SET duration = ?, activation_timestamp = ?, total = ? WHERE user_id = ? AND effect = ?")) {
                this.effects.forEachValue(effect -> {
                    if(!effect.isRankEnable()) {
                        try {
                            statement.setInt(1, effect.getDuration());
                            statement.setInt(2, effect.getActivationTimestamp());
                            statement.setInt(3, effect.getTotal());
                            statement.setInt(4, effect.getUserId());
                            statement.setInt(5, effect.getEffect());
                            statement.addBatch();
                        } catch (SQLException e) {
                            log.error(CAUGHT_SQL_EXCEPTION, e);
                        }
                    }
                    return true;
                });

                statement.executeBatch();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }

            this.effects.clear();
        }
    }

    public boolean ownsEffect(int effectId) {
        return this.effects.containsKey(effectId);
    }

    public void activateEffect(int effectId) {
        HabboEffect effect = this.effects.get(effectId);

        if (effect != null) {
            if (effect.isRemaining()) {
                effect.setActivationTimestamp(Emulator.getIntUnixTimestamp());
            } else {
                this.habbo.getClient().sendResponse(new AvatarEffectExpiredMessageComposer(effect));
            }
        }
    }

    public void enableEffect(int effectId) {
        HabboEffect effect = this.effects.get(effectId);

        if (effect != null) {
            if (!effect.isActivated()) {
                this.activateEffect(effect.getEffect());
            }

            this.activatedEffect = effectId;

            if (this.habbo.getHabboInfo().getCurrentRoom() != null) {
                this.habbo.getHabboInfo().getCurrentRoom().giveEffect(this.habbo, effectId, effect.remainingTime());
            }

            this.habbo.getClient().sendResponse(new AvatarEffectActivatedMessageComposer(effect));
        }
    }

    public boolean hasActivatedEffect(int effectId) {
        HabboEffect effect = this.effects.get(effectId);

        if (effect != null) {
            return effect.isActivated();
        }

        return false;
    }

    @Getter
    @Setter
    public static class HabboEffect {
        private int effect;
        private int userId;
        private int duration = 86400;
        private int activationTimestamp = -1;
        private int total = 1;
        private boolean enabled = false;
        private boolean isRankEnable = false;

        public HabboEffect(ResultSet set) throws SQLException {
            this.setEffect(set.getInt("effect"));
            this.setUserId(set.getInt(DatabaseConstants.USER_ID));
            this.setDuration(set.getInt("duration"));
            this.setActivationTimestamp(set.getInt("activation_timestamp"));
            this.setTotal(set.getInt("total"));
        }

        public HabboEffect(int effect, int userId) {
            this.setEffect(effect);
            this.setUserId(userId);
        }

        public boolean isActivated() {
            return this.getActivationTimestamp() >= 0;
        }

        public boolean isRemaining() {
            if(this.getDuration() <= 0)
                return true;

            if (this.getTotal() > 0 && this.getActivationTimestamp() >= 0
                    && Emulator.getIntUnixTimestamp() - this.getActivationTimestamp() >= this.getDuration()) {
                this.setActivationTimestamp(-1);
                this.setTotal(this.getTotal() - 1);
            }

            return this.getTotal() > 0;
        }

        public int remainingTime() {
            if(this.getDuration() <= 0) //permanant
                return Integer.MAX_VALUE;

            return Emulator.getIntUnixTimestamp() - this.getActivationTimestamp() + this.getDuration();
        }

        public void insert() {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users_effects (user_id, effect, total, duration) VALUES (?, ?, ?, ?)")) {
                statement.setInt(1, this.getUserId());
                statement.setInt(2, this.getEffect());
                statement.setInt(3, this.getTotal());
                statement.setInt(4, this.getDuration());
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }

        public void delete() {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM users_effects WHERE user_id = ? AND effect = ?")) {
                statement.setInt(1, this.getUserId());
                statement.setInt(2, this.getEffect());
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }

    }
}
