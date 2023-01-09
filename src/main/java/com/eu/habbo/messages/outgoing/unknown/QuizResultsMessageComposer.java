package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class QuizResultsMessageComposer extends MessageComposer {
    private final String unknownString;
    private final List<Integer> unknownIntegerList;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.quizResultsMessageComposer);
        this.response.appendString(this.unknownString);
        this.response.appendInt(this.unknownIntegerList.size());
        for (Integer i : this.unknownIntegerList) {
            this.response.appendInt(i);
        }
        return this.response;
    }
}