package com.eu.habbo.habbohotel.items;

import com.eu.habbo.Emulator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class CrackableReward {
    private final int itemId;
    private final int count;
    private final Map<Integer, Map.Entry<Integer, Integer>> prizes;
    private  final String achievementTick;
    private final String achievementCracked;
    private final int requiredEffect;
    private final int subscriptionDuration;
    private final RedeemableSubscriptionType subscriptionType;
    private int totalChance;

    public CrackableReward(ResultSet set) throws SQLException {
        this.itemId = set.getInt("item_id");
        this.count = set.getInt("count");
        this.achievementTick = set.getString("achievement_tick");
        this.achievementCracked = set.getString("achievement_cracked");
        this.requiredEffect = set.getInt("required_effect");
        this.subscriptionDuration = set.getInt("subscription_duration");
        this.subscriptionType = RedeemableSubscriptionType.fromString(set.getString("subscription_type"));


        String[] prizes = set.getString("prizes").split(";");
        this.prizes = new HashMap<>();

        if (set.getString("prizes").isEmpty()) return;

        this.totalChance = 0;
        for (String prize : prizes) {
            try {
                int itemId = 0;
                int chance = 100;

                if (prize.contains(":") && prize.split(":").length == 2) {
                    itemId = Integer.parseInt(prize.split(":")[0]);
                    chance = Integer.parseInt(prize.split(":")[1]);
                } else if (prize.contains(":")) {
                    log.error("Invalid configuration of crackable prizes (item id: " + this.itemId + "). '" + prize + "' format should be itemId:chance.");
                } else {
                    itemId = Integer.parseInt(prize.replace(":", ""));
                }

                this.prizes.put(itemId, new AbstractMap.SimpleEntry<>(this.totalChance, this.totalChance + chance));
                this.totalChance += chance;
            } catch (Exception e) {
                log.error("Caught exception", e);
            }
        }
    }

    public int getRandomReward() {
        if (this.prizes.size() == 0) return 0;

        int random = Emulator.getRandom().nextInt(this.totalChance);

        int notFound = 0;
        for (Map.Entry<Integer, Map.Entry<Integer, Integer>> set : this.prizes.entrySet()) {
            notFound = set.getKey();
            if (random >= set.getValue().getKey() && random < set.getValue().getValue()) {
                return set.getKey();
            }
        }

        return notFound;
    }
}
