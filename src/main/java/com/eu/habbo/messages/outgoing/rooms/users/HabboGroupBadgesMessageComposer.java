package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HabboGroupBadgesMessageComposer extends MessageComposer {
    private final THashMap<Integer, String> guildBadges;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.habboGroupBadgesMessageComposer);
        this.response.appendInt(this.guildBadges.size());

        this.guildBadges.forEachEntry((guildId, badge) -> {
            HabboGroupBadgesMessageComposer.this.response.appendInt(guildId);
            HabboGroupBadgesMessageComposer.this.response.appendString(badge);
            return true;
        });
        return this.response;
    }
}