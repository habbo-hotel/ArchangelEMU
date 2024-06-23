package com.eu.habbo.roleplay.messages.outgoing.taxi;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TaxiDispatchedComposer extends MessageComposer {

    private final int roomID;
    private final long arrivesAt;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.taxiDispatchedComposer);
        this.response.appendInt(this.roomID);
        this.response.appendInt(Integer.parseInt(String.valueOf(this.arrivesAt)));
        return this.response;
    }
}
