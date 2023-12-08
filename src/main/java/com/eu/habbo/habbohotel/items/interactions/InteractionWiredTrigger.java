package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.IWiredTriggerInteraction;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class InteractionWiredTrigger extends InteractionWired implements IWiredTriggerInteraction {
    @Getter
    @Setter
    private List<Integer> blockedEffects;

    @Getter
    @Setter
    private WiredTriggerType type;

    protected InteractionWiredTrigger(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    protected InteractionWiredTrigger(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    public List<Integer> getBlockedEffects(Room room) {
        List<Integer> blockedEffects = new ArrayList<>();
        THashSet<InteractionWiredEffect> effects = room.getRoomSpecialTypes().getEffects(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());

        for(InteractionWiredEffect effect : effects) {
            if (!effect.requiresTriggeringUser()) {
                blockedEffects.add(effect.getBaseItem().getSpriteId());
            }
        }

        return blockedEffects;
    }

    public boolean isTriggeredByRoomUnit() {
        return false;
    }
}
