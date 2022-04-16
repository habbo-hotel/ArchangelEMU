package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.guides.GuideChatMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuideSessionMessageMessageComposer extends MessageComposer {
    private final GuideChatMessage message;

    public GuideSessionMessageMessageComposer(GuideChatMessage message) {
        this.message = message;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.GuideSessionMessageMessageComposer);
        this.response.appendString(this.message.message); //Message
        this.response.appendInt(this.message.userId);   //Sender ID
        return this.response;
    }
}
