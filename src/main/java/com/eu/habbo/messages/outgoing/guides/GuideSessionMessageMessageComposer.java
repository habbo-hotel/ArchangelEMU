package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.guides.GuideChatMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuideSessionMessageMessageComposer extends MessageComposer {
    private final GuideChatMessage message;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guideSessionMessageMessageComposer);
        this.response.appendString(this.message.getMessage()); //Message
        this.response.appendInt(this.message.getUserId());   //Sender ID
        return this.response;
    }
}
