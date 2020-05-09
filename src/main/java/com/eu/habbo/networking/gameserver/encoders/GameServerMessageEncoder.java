package com.eu.habbo.networking.gameserver.encoders;

import com.eu.habbo.messages.ServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.IllegalReferenceCountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerMessageEncoder extends MessageToByteEncoder<ServerMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerMessage message, ByteBuf out) {
        try {
            ByteBuf buf = message.get();

            try {
                out.writeBytes(buf);
            } finally {
                // Release copied buffer.
                buf.release();
            }
        } catch (IllegalReferenceCountException e) {
            LOGGER.error("IllegalReferenceCountException happened for packet {}.", message.getHeader());
            throw e;
        }
    }

}
