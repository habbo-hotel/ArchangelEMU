package com.eu.habbo.messages.outgoing.polls;

import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PollOfferComposer extends MessageComposer {
    private final Poll poll;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.pollOfferComposer);
        this.response.appendInt(this.poll.getId());
        this.response.appendString(this.poll.getTitle());
        this.response.appendString(this.poll.getThanksMessage());
        this.response.appendString(this.poll.getTitle());
        return this.response;
    }
}
