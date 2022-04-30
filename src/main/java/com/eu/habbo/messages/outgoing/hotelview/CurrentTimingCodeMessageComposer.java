package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CurrentTimingCodeMessageComposer extends MessageComposer {
    private final String data;
    private final String key;

    public CurrentTimingCodeMessageComposer(String data, String key) {
        this.data = data;
        this.key = key;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.currentTimingCodeMessageComposer);

        this.response.appendString(this.data);
        this.response.appendString(this.key);

        return this.response;
    }
}
