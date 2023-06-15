package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.IWiredTriggerInteraction;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.wired.WiredTriggerDataComposer;
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

    protected InteractionWiredTrigger(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    protected InteractionWiredTrigger(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public List<Integer> getBlockedEffects(Room room) {
        List<Integer> blockedEffects = new ArrayList<>();
        THashSet<InteractionWiredEffect> effects = room.getRoomSpecialTypes().getEffects(this.getX(), this.getY());

        for(InteractionWiredEffect effect : effects) {
            if (!effect.requiresTriggeringUser()) {
                blockedEffects.add(effect.getBaseItem().getSpriteId());
            }
        }

        return blockedEffects;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {
        if (client != null) {
            if (room.hasRights(client.getHabbo())) {
                client.sendResponse(new WiredTriggerDataComposer(this, room));
                this.activateBox(room);
            }
        }
    }

    public abstract WiredTriggerType getType();

    public boolean isTriggeredByRoomUnit() {
        return false;
    }

}
