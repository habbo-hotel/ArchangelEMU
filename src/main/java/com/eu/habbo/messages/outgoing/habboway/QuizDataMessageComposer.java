package com.eu.habbo.messages.outgoing.habboway;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class QuizDataMessageComposer extends MessageComposer {
    public final String name;
    public final int[] items;

    public QuizDataMessageComposer(String name, int[] items) {
        this.name = name;
        this.items = items;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.QuizDataMessageComposer);
        this.response.appendString(this.name);
        this.response.appendInt(this.items.length);
        for (int item : this.items) {
            this.response.appendInt(item);
        }
        return this.response;
    }
}