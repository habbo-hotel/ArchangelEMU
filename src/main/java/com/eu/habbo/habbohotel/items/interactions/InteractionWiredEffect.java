package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.IWiredEffectInteraction;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.wired.WiredEffectDataComposer;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class InteractionWiredEffect extends InteractionWired implements IWiredEffectInteraction {
    @Getter
    @Setter
    private List<Integer> blockedTriggers;

    @Getter
    @Setter
    private WiredEffectType type;

    public InteractionWiredEffect(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionWiredEffect(int id, int userId, Item item, String extraData, int limitedStack, int limitedSells) {
        super(id, userId, item, extraData, limitedStack, limitedSells);
    }

    public List<Integer> getBlockedTriggers(Room room) {
        List<Integer> blockedTriggers = new ArrayList<>();
        THashSet<InteractionWiredTrigger> triggers = room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY());

        for(InteractionWiredTrigger trigger : triggers) {
            if(!trigger.isTriggeredByRoomUnit()) {
                blockedTriggers.add(trigger.getBaseItem().getSpriteId());
            }
        }

        return blockedTriggers;
    }

    public boolean requiresTriggeringUser() {
        return false;
    }
}
