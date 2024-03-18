package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CompetitionVotingInfoMessageComposer extends MessageComposer {
    private final int unknownInt1;
    private final String unknownString1;
    private final int unknownInt2;
    private final int unknownInt3;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.competitionVotingInfoMessageComposer);
        this.response.appendInt(this.unknownInt1);
        this.response.appendString(this.unknownString1);
        this.response.appendInt(this.unknownInt2);
        this.response.appendInt(this.unknownInt3);
        return this.response;
    }
}