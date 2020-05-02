package com.eu.habbo.networking.gameserver.decoders;

import com.eu.habbo.networking.gameserver.GameServerAttributes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class GameByteDecryption extends ByteToMessageDecoder {

    public GameByteDecryption() {
        setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Read all available bytes.
        byte[] data = in.readBytes(in.readableBytes()).array();

        // Decrypt.
        ctx.channel().attr(GameServerAttributes.CRYPTO_CLIENT).get().parse(data);

        // Continue in the pipeline.
        out.add(Unpooled.wrappedBuffer(data));
    }

}
