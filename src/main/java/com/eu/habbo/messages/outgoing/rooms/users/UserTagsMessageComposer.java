package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserTagsMessageComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userTagsMessageComposer);
        this.response.appendInt(this.habbo.getRoomUnit().getId());
        this.response.appendInt(this.habbo.getHabboStats().getTags().length);

        for (String tag : this.habbo.getHabboStats().getTags()) {
            this.response.appendString(tag);
        }
        return this.response;
    }
}
