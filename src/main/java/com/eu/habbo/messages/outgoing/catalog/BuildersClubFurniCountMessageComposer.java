package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BuildersClubFurniCountMessageComposer extends MessageComposer {
    private final int mode;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.buildersClubFurniCountMessageComposer);
        this.response.appendInt(this.mode);
        return this.response;
    }
}
