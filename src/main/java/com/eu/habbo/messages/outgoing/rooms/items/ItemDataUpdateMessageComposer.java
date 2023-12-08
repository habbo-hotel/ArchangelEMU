package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ItemDataUpdateMessageComposer extends MessageComposer {
    private final InteractionPostIt postIt;


    @Override
    protected ServerMessage composeInternal() {
        if (this.postIt.getExtraData().isEmpty() || this.postIt.getExtraData().length() < 6) {
            this.postIt.setExtraData("FFFF33");
        }

        this.response.init(Outgoing.itemDataUpdateMessageComposer);
        this.response.appendString(this.postIt.getId() + "");
        this.response.appendString(this.postIt.getExtraData());
        return this.response;
    }
}
