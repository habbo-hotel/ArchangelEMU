package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionVikingCotie extends InteractionDefault {
    public InteractionVikingCotie(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionVikingCotie(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {
        if (this.getExtraData().isEmpty()) {
            this.setExtraData("0");
        }

        if (client != null && client.getHabbo().getHabboInfo().getId() == this.getOwnerInfo().getId()) {
            if (client.getHabbo().getRoomUnit().getEffectId() == 172 || client.getHabbo().getRoomUnit().getEffectId() == 173) {
                int state = Integer.parseInt(this.getExtraData());

                if (state < 5) {
                    state++;
                    this.setExtraData(state + "");
                    room.updateItem(this);

                    if (state == 5) {
                        AchievementManager.progressAchievement(client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("ViciousViking"));
                    }
                }
            }
        }
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }
}
