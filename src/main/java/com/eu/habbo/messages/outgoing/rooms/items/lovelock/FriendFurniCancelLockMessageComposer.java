package com.eu.habbo.messages.outgoing.rooms.items.lovelock;

import com.eu.habbo.habbohotel.items.interactions.InteractionLoveLock;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FriendFurniCancelLockMessageComposer extends MessageComposer {
    private final InteractionLoveLock loveLock;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.friendFurniCancelLockMessageComposer);
        this.response.appendInt(this.loveLock.getId());
        return this.response;
    }
}
