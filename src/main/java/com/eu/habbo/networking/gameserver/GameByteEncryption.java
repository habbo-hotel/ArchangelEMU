package com.eu.habbo.networking.gameserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class GameByteEncryption extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // Convert msg to ByteBuf.
        ByteBuf out = (ByteBuf) msg;

        // Read all available bytes.
        byte[] data = out.array();

        // Encrypt.
        ctx.channel().attr(GameServerAttributes.CRYPTO_SERVER).get().parse(data);

        // Continue in the pipeline.
        ctx.write(Unpooled.wrappedBuffer(data));
    }

}
