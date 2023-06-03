package com.eu.habbo.habbohotel.gameclients;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.networking.gameserver.GameServerAttributes;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameClientManager {

    @Getter
    private final ConcurrentMap<ChannelId, GameClient> clients;

    public GameClientManager() {
        this.clients = new ConcurrentHashMap<>();
    }



    public boolean addClient(ChannelHandlerContext ctx) {
        GameClient client = new GameClient(ctx.channel());
        ctx.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> GameClientManager.this.disposeClient(ctx.channel()));

        ctx.channel().attr(GameServerAttributes.CLIENT).set(client);
        ctx.fireChannelRegistered();

        return this.clients.putIfAbsent(ctx.channel().id(), client) == null;
    }


    public void disposeClient(GameClient client) {
        this.disposeClient(client.getChannel());
    }

    private void disposeClient(Channel channel) {
        GameClient client = channel.attr(GameServerAttributes.CLIENT).get();

        if (client != null) {
            client.dispose();
        }
        channel.deregister();
        channel.attr(GameServerAttributes.CLIENT).set(null);
        channel.closeFuture();
        channel.close();
        this.clients.remove(channel.id());
    }


    public boolean containsHabbo(Integer id) {
        if (!this.clients.isEmpty()) {
            for (GameClient client : this.clients.values()) {
                if (client.getHabbo() != null) {
                    if (client.getHabbo().getHabboInfo() != null) {
                        if (client.getHabbo().getHabboInfo().getId() == id)
                            return true;
                    }
                }
            }
        }
        return false;
    }


    public Habbo getHabbo(int id) {
        for (GameClient client : this.clients.values()) {
            if (client.getHabbo() == null)
                continue;

            if (client.getHabbo().getHabboInfo().getId() == id)
                return client.getHabbo();
        }

        return null;
    }


    public Habbo getHabbo(String username) {
        for (GameClient client : this.clients.values()) {
            if (client.getHabbo() == null)
                continue;

            if (client.getHabbo().getHabboInfo().getUsername().equalsIgnoreCase(username))
                return client.getHabbo();
        }

        return null;
    }


    public List<Habbo> getHabbosWithIP(String ip) {
        List<Habbo> habbos = new ArrayList<>();

        for (GameClient client : this.clients.values()) {
            if (client.getHabbo() != null && client.getHabbo().getHabboInfo() != null) {
                if (client.getHabbo().getHabboInfo().getIpLogin().equalsIgnoreCase(ip)) {
                    habbos.add(client.getHabbo());
                }
            }
        }

        return habbos;
    }


    public List<Habbo> getHabbosWithMachineId(String machineId) {
        List<Habbo> habbos = new ArrayList<>();

        for (GameClient client : this.clients.values()) {
            if (client.getHabbo() != null && client.getHabbo().getHabboInfo() != null && client.getMachineId().equalsIgnoreCase(machineId)) {
                habbos.add(client.getHabbo());
            }
        }

        return habbos;
    }


    public void sendBroadcastResponse(MessageComposer composer) {
        this.sendBroadcastResponse(composer.compose());
    }


    public void sendBroadcastResponse(ServerMessage message) {
        for (GameClient client : this.clients.values()) {
            client.sendResponse(message);
        }
    }


    public void sendBroadcastResponse(ServerMessage message, GameClient exclude) {
        for (GameClient client : this.clients.values()) {
            if (client.equals(exclude))
                continue;

            client.sendResponse(message);
        }
    }


    public void sendBroadcastResponse(ServerMessage message, String minPermission, GameClient exclude) {
        for (GameClient client : this.clients.values()) {
            if (client.equals(exclude))
                continue;

            if (client.getHabbo() != null) {
                if (client.getHabbo().hasRight(minPermission)) {
                    client.sendResponse(message);
                }
            }
        }
    }
}