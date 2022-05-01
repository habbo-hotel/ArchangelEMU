package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UniqueMachineIDComposer extends MessageComposer {

    private final String machineId;

    public UniqueMachineIDComposer(String machineId) {
        this.machineId = machineId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.uniqueMachineIDComposer);
        this.response.appendString(this.machineId);
        return this.response;
    }

}
