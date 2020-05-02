package com.eu.habbo.networking.gameserver.encoders;

import com.eu.habbo.messages.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameServerMessageLogger extends MessageToMessageEncoder<ServerMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerMessageLogger.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerMessage message, List<Object> out) throws Exception {
        LOGGER.debug("[SERVER][{}]", message.getHeader());

        String body = message.getBodyString();
        if (body == null || body.length() == 0) {
            LOGGER.debug("\n" + message.getBodyString());
        }

        out.add(message.retain());
    }

}
