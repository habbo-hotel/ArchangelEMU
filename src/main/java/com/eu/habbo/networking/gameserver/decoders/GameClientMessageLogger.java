package com.eu.habbo.networking.gameserver.decoders;

import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.util.ANSI;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameClientMessageLogger extends MessageToMessageDecoder<ClientMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameClientMessageLogger.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ClientMessage message, List<Object> out) {
        LOGGER.debug(String.format("[" + ANSI.GREEN + "CLIENT" + ANSI.DEFAULT + "][%-4d] => %s", message.getMessageId(), message.getMessageBody()));

        out.add(message);
    }

}
