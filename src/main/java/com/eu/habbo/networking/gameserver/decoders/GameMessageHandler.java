package com.eu.habbo.networking.gameserver.decoders;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.threading.runnables.ChannelReadHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@ChannelHandler.Sharable
@Slf4j
public class GameMessageHandler extends ChannelInboundHandlerAdapter {
    


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        if (!Emulator.getGameServer().getGameClientManager().addClient(ctx)) {
            ctx.channel().close();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ClientMessage message = (ClientMessage) msg;

        try {
            ChannelReadHandler handler = new ChannelReadHandler(ctx, message);

            if (PacketManager.MULTI_THREADED_PACKET_HANDLING) {
                Emulator.getThreading().run(handler);
                return;
            }

            handler.run();
        } catch (Exception e) {
            log.error("Caught exception", e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            ctx.channel().close();
            return;
        }
        if (Emulator.getConfig().getBoolean("debug.mode")) {
            if (cause instanceof TooLongFrameException) {
                log.error("Disconnecting client, reason: \"" + cause.getMessage() + "\".");
            } else {
                log.error("Disconnecting client, exception in GameMessageHander.", cause);
            }
        }
        ctx.channel().close();
    }

}