package com.eu.habbo.messages.outgoing.polls;

import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.habbohotel.polls.PollQuestion;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PollContentsComposer extends MessageComposer {
    private final Poll poll;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.pollContentsComposer);

        this.response.appendInt(this.poll.getId());
        this.response.appendString(this.poll.getTitle());
        this.response.appendString(this.poll.getThanksMessage());
        this.response.appendInt(this.poll.getQuestions().size());
        for (PollQuestion question : this.poll.getQuestions()) {
            question.serialize(this.response);
        }

        this.response.appendBoolean(true);
        return this.response;
    }
}
