package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.CallForHelpPendingCallsMessageComposer;

public class RequestReportRoomEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new CallForHelpPendingCallsMessageComposer(Emulator.getGameEnvironment().getModToolManager().openTicketsForHabbo(this.client.getHabbo())));
    }
}
