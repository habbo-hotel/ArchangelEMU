package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RecyclerFinishedComposer extends MessageComposer {
    public static final int RECYCLING_COMPLETE = 1;
    public static final int RECYCLING_CLOSED = 2;

    private final int code;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.recyclerFinishedComposer);
        this.response.appendInt(this.code);
        this.response.appendInt(0); //prize ID.
        return this.response;
    }
}
