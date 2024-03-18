package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class YoutubeControlVideoMessageComposer extends MessageComposer {
    private final int furniId;
    private final int state;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.youtubeControlVideoMessageComposer);
        this.response.appendInt(this.furniId);
        this.response.appendInt(this.state);

        return this.response;
    }
}
