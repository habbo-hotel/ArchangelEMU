package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.SellablePetPalettesMessageComposer;

public class RequestPetBreedsEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        String petName = this.packet.readString();
        this.client.sendResponse(new SellablePetPalettesMessageComposer(petName, Emulator.getGameEnvironment().getPetManager().getBreeds(petName)));
    }
}
