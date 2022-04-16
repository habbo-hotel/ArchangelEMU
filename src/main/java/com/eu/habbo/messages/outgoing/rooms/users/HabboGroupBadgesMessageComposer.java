package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;

public class HabboGroupBadgesMessageComposer extends MessageComposer {
    private final THashMap<Integer, String> guildBadges;

    public HabboGroupBadgesMessageComposer(THashMap<Integer, String> guildBadges) {
        this.guildBadges = guildBadges;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.HabboGroupBadgesMessageComposer);
        this.response.appendInt(this.guildBadges.size());

        this.guildBadges.forEachEntry(new TObjectObjectProcedure<Integer, String>() {
            @Override
            public boolean execute(Integer guildId, String badge) {
                HabboGroupBadgesMessageComposer.this.response.appendInt(guildId);
                HabboGroupBadgesMessageComposer.this.response.appendString(badge);
                return true;
            }
        });
        return this.response;
    }
}