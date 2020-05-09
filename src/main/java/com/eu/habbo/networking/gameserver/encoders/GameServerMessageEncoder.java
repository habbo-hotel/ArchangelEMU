package com.eu.habbo.networking.gameserver.encoders;

import com.eu.habbo.messages.ServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class GameServerMessageEncoder extends MessageToByteEncoder<ServerMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerMessage message, ByteBuf out) {
        ByteBuf buf = message.get();

        try {
            out.writeBytes(buf);
        } finally {
            // Release copied buffer.
            buf.release();
        }
    }

}
