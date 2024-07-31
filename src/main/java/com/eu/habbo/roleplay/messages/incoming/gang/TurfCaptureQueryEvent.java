package com.eu.habbo.roleplay.messages.incoming.gang;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.gang.TurfCaptureTimeLeftComposer;

public class TurfCaptureQueryEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new TurfCaptureTimeLeftComposer(this.client.getHabbo().getRoomUnit().getRoom()));
    }
}