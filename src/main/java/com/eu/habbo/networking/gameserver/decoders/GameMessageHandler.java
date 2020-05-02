package com.eu.habbo.networking.gameserver.decoders;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.threading.runnables.ChannelReadHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;

import java.io.IOException;

@ChannelHandler.Sharable
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClientMessage message = (ClientMessage) msg;

        try {
            ChannelReadHandler handler = new ChannelReadHandler(ctx, message);

            if (PacketManager.MULTI_THREADED_PACKET_HANDLING) {
                Emulator.getThreading().run(handler);
                return;
            }

            handler.run();
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof TooLongFrameException) {
            Emulator.getLogging().logErrorLine("Disconnecting client, reason: \"" + cause.getMessage() + "\".");
        } else {
            Emulator.getLogging().logErrorLine("Disconnecting client, exception in GameMessageHander:");
            Emulator.getLogging().logErrorLine(cause.toString());

            for (StackTraceElement element : cause.getStackTrace()) {
                Emulator.getLogging().logErrorLine(element.toString());
            }
        }

        ctx.channel().close();
    }

}