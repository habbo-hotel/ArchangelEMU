package com.eu.habbo.messages.incoming.guardians;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ChatReviewGuideDecidesOnOfferEvent extends MessageHandler {
    @Override
    public void handle() {
        Emulator.getGameEnvironment().getGuideManager().acceptTicket(this.client.getHabbo(), this.packet.readBoolean());
    }
}
