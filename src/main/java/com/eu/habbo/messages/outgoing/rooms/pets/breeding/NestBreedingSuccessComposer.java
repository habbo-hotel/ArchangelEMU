package com.eu.habbo.messages.outgoing.rooms.pets.breeding;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NestBreedingSuccessComposer extends MessageComposer {
    private final int type;
    private final int race;

    public NestBreedingSuccessComposer(int type, int race) {
        this.type = type;
        this.race = race;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.NestBreedingSuccessComposer);
        this.response.appendInt(this.type);
        this.response.appendInt(this.race);
        return this.response;
    }
}