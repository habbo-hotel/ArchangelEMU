package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredGiveRewardItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class WiredEffectGiveReward extends InteractionWiredEffect {
    public final static int LIMIT_ONCE = 0;
    public final static int LIMIT_N_DAY = 1;
    public final static int LIMIT_N_HOURS = 2;
    public final static int LIMIT_N_MINUTES = 3;
    public final int PARAM_REWARD_LIMIT = 0;
    public final int PARAM_UNIQUE_REWARD = 1;
    public final int PARAM_LIMIT = 2;
    public final int PARAM_LIMITATION_INTERVAL = 3;
    public THashSet<WiredGiveRewardItem> rewardItems = new THashSet<>();
    private int given;

    public WiredEffectGiveReward(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectGiveReward(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int rewardTime = this.getWiredSettings().getIntegerParams().get(PARAM_REWARD_LIMIT);
        boolean uniqueRewards = this.getWiredSettings().getIntegerParams().get(PARAM_UNIQUE_REWARD) == 1;
        int limit = this.getWiredSettings().getIntegerParams().get(PARAM_LIMIT);
        int limitationInterval = this.getWiredSettings().getIntegerParams().get(PARAM_LIMITATION_INTERVAL);

        String data = this.getWiredSettings().getStringParam();

        String[] items = data.split(";");

        int i = 1;

        for(String item : items) {
            String[] d = item.split(",");

            if(d.length == 3) {
                if(!(d[1].contains(":") || d[1].contains(";"))) {
                    this.rewardItems.add(new WiredGiveRewardItem(i, d[0].equalsIgnoreCase("0"), d[1], Integer.valueOf(d[2])));
                    continue;
                }
            }

            return false;
        }

        Habbo habbo = room.getHabbo(roomUnit);

        return habbo != null && WiredHandler.getReward(habbo, this);
    }

    public int getLimit() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_LIMIT);
    }

    public int getRewardTime() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_REWARD_LIMIT);
    }

    public boolean isUniqueRewards() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_UNIQUE_REWARD) == 1;
    }

    public int getLimitationInterval() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_LIMITATION_INTERVAL);
    }

    @Override
    protected long requiredCooldown() {
        return 0;
    }

    public void incrementGiven() {
        this.given++;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.GIVE_REWARD;
    }
}