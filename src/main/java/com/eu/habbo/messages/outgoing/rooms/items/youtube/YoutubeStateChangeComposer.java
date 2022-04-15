package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class YoutubeStateChangeComposer extends MessageComposer {
    private final int furniId;
    private final int state;

    public YoutubeStateChangeComposer(int furniId, int state) {
        this.furniId = furniId;
        this.state = state;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.YoutubeControlVideoMessageComposer);
        this.response.appendInt(this.furniId);
        this.response.appendInt(this.state);

        return this.response;
    }
}
