package com.eu.habbo.networking.gameserver.encoders;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.messages.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class GameServerMessageLogger extends MessageToMessageEncoder<ServerMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerMessage message, List<Object> out) throws Exception {
        Emulator.getLogging().logPacketLine("[" + Logging.ANSI_PURPLE + "SERVER" + Logging.ANSI_RESET + "] => [" + message.getHeader() + "] -> " + message.getBodyString());

        out.add(message);
    }

}
