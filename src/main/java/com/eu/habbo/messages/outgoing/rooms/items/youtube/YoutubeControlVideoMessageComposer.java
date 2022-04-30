package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class YoutubeControlVideoMessageComposer extends MessageComposer {
    private final int furniId;
    private final int state;

    public YoutubeControlVideoMessageComposer(int furniId, int state) {
        this.furniId = furniId;
        this.state = state;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.youtubeControlVideoMessageComposer);
        this.response.appendInt(this.furniId);
        this.response.appendInt(this.state);

        return this.response;
    }
}
