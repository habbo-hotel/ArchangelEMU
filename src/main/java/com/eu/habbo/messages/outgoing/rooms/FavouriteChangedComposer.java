package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FavouriteChangedComposer extends MessageComposer {
    private final int roomId;
    private final boolean favorite;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.favouriteChangedComposer);
        this.response.appendInt(this.roomId);
        this.response.appendBoolean(this.favorite);
        return this.response;
    }
}