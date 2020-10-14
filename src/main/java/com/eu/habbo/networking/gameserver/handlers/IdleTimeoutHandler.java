package com.eu.habbo.networking.gameserver.handlers;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.handshake.PingComposer;
import com.eu.habbo.networking.gameserver.GameServerAttributes;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleTimeoutHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                GameClient client = ctx.channel().attr(GameServerAttributes.CLIENT).get();
                if (client != null) {
                    client.sendResponse(new PingComposer());
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
