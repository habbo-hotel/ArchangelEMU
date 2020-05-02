package com.eu.habbo.networking.gameserver.encoders;

import com.eu.habbo.messages.outgoing.MessageComposer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class MessageComposerEncoder extends MessageToMessageEncoder<MessageComposer> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageComposer message, List<Object> out) throws Exception {
        out.add(message.compose());
    }

}
