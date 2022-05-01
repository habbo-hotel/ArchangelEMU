package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class JukeBoxPlaylistFullMessageComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.jukeBoxPlaylistFullMessageComposer);
        return this.response;
    }
}
