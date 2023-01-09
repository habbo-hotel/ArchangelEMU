package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UniqueMachineIDComposer extends MessageComposer {

    private final String machineId;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.uniqueMachineIDComposer);
        this.response.appendString(this.machineId);
        return this.response;
    }

}
