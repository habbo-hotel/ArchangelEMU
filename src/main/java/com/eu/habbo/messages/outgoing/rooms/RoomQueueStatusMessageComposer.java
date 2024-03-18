package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomQueueStatusMessageComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomQueueStatusMessageComposer);
        this.response.appendInt(1); //Count
        {
            this.response.appendString("TEST"); //Name
            this.response.appendInt(94); //Target

            this.response.appendInt(1); //Count
            this.response.appendString("d");
            this.response.appendInt(1);
        }
        return this.response;
    }
}