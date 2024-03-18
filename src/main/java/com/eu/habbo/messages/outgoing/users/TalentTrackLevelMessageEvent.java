package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TalentTrackLevelMessageEvent extends MessageComposer {
    private final String name;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.talentTrackLevelMessageEvent);
        this.response.appendString(this.name);
        this.response.appendInt(4);
        this.response.appendInt(4);
        return this.response;
    }
}
