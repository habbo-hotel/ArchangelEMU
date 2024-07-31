package com.eu.habbo.roleplay.messages.incoming.gang;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.actions.CaptureTurfAction;

public class TurfStartCaptureEvent  extends MessageHandler {
    @Override
    public void handle() {
        Emulator.getThreading().run(new CaptureTurfAction(this.client.getHabbo().getRoomUnit().getRoom(), this.client.getHabbo().getRoomUnit().getLastRoomTile(), this.client.getHabbo()));
    }
}