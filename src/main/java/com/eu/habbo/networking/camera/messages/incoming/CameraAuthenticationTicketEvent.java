package com.eu.habbo.networking.camera.messages.incoming;

import com.eu.habbo.messages.outgoing.gamecenter.basejump.LoadGameMessageComposer;
import com.eu.habbo.networking.camera.CameraIncomingMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class CameraAuthenticationTicketEvent extends CameraIncomingMessage {
    public CameraAuthenticationTicketEvent(Short header, ByteBuf body) {
        super(header, body);
    }

    @Override
    public void handle(Channel client) {
        String ticket = this.readString();

        if (ticket.startsWith("FASTFOOD")) {
            LoadGameMessageComposer.FASTFOOD_KEY = ticket;
        }
    }
}
