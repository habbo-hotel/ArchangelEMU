package com.eu.habbo.habbohotel.gameclients;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.crypto.HabboEncryption;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.MessageComposer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class GameClient {

    private final Channel channel;
    private final HabboEncryption encryption;

    private Habbo habbo;
    private boolean handshakeFinished;
    private String machineId = "";

    public final ConcurrentHashMap<Integer, Integer> incomingPacketCounter = new ConcurrentHashMap<>(25);
    public final ConcurrentHashMap<Class<? extends MessageHandler>, Long> messageTimestamps = new ConcurrentHashMap<>();
    public long lastPacketCounterCleared = Emulator.getIntUnixTimestamp();

    public GameClient(Channel channel) {
        this.channel = channel;
        this.encryption = Emulator.getCrypto().isEnabled()
                ? new HabboEncryption(
                    Emulator.getCrypto().getExponent(),
                    Emulator.getCrypto().getModulus(),
                    Emulator.getCrypto().getPrivateExponent())
                : null;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public HabboEncryption getEncryption() {
        return encryption;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public void setHabbo(Habbo habbo) {
        this.habbo = habbo;
    }

    public boolean isHandshakeFinished() {
        return handshakeFinished;
    }

    public void setHandshakeFinished(boolean handshakeFinished) {
        this.handshakeFinished = handshakeFinished;
    }

    public String getMachineId() {
        return this.machineId;
    }

    public void setMachineId(String machineId) {
        if (machineId == null) {
            throw new RuntimeException("Cannot set machineID to NULL");
        }
        this.machineId = machineId;

        if (this.habbo != null) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET machine_id = ? WHERE id = ? LIMIT 1")) {
                statement.setString(1, this.machineId);
                statement.setInt(2, this.habbo.getHabboInfo().getId());
                statement.execute();
            } catch (SQLException e) {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public void sendResponse(MessageComposer composer) {
        if (this.channel.isOpen()) {
            try {
                this.channel.write(composer.compose(), this.channel.voidPromise());
                this.channel.flush();
            } catch (Exception e) {
                Emulator.getLogging().logPacketError(e);
            }
        }
    }

    public void sendResponse(ServerMessage response) {
        if (this.channel.isOpen()) {
            if (response == null || response.getHeader() <= 0) {
                return;
            }

            this.channel.write(response, this.channel.voidPromise());
            this.channel.flush();
        }
    }

    public void sendResponses(ArrayList<ServerMessage> responses) {
        if (this.channel.isOpen()) {
            for (ServerMessage response : responses) {
                if (response == null || response.getHeader() <= 0) {
                    return;
                }

                this.channel.write(response);
            }

            this.channel.flush();
        }
    }

    public void dispose() {
        try {
            this.channel.close();

            if (this.habbo != null) {
                if (this.habbo.isOnline()) {
                    this.habbo.getHabboInfo().setOnline(false);
                    this.habbo.disconnect();
                }

                this.habbo = null;
            }
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
    }
}