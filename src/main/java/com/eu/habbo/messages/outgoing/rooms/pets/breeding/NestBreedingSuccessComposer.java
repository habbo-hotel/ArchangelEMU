package com.eu.habbo.messages.outgoing.rooms.pets.breeding;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NestBreedingSuccessComposer extends MessageComposer {
    private final int type;
    private final int race;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.nestBreedingSuccessComposer);
        this.response.appendInt(this.type);
        this.response.appendInt(this.race);
        return this.response;
    }
}