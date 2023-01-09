package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class YouArePlayingGameMessageComposer extends MessageComposer {
    public final boolean isPlaying;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.youArePlayingGameMessageComposer);
        this.response.appendBoolean(this.isPlaying);
        return this.response;
    }
}