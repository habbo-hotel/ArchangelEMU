package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomInviteErrorComposer extends MessageComposer {
    private final int errorCode;
    private final THashSet<MessengerBuddy> buddies;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomInviteErrorComposer);
        this.response.appendInt(this.errorCode);
        this.response.appendInt(this.buddies.size());
        this.buddies.forEach(object -> {
            RoomInviteErrorComposer.this.response.appendInt(object.getId());
            return true;
        });
        return this.response;
    }
}