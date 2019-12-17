/*
 * Morning Star
 * Copyright (C) 2019
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.eu.habbo.habbohotel.gameclients;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class GameClient
{
    /// Constructor
    /// @p_Channel : Channel
    public GameClient(Channel p_Channel) {
        this.m_Channel = p_Channel;
    }


    /// Composer class - Abstract class
    /// @p_Composer : Composer
    public void sendResponse(MessageComposer p_Composer) {
        if (this.m_Channel.isOpen())
        {
            try
            {
                ServerMessage l_ServerMessage = p_Composer.compose();
                this.sendResponse(l_ServerMessage);

            } catch (Exception l_Exception)
            {
                Emulator.getLogging().logPacketError(l_Exception);
            }
        }
    }

    /// Send Raw Response
    /// @p_Response : Response
    public void sendResponse(ServerMessage p_Response)
    {
        if (this.m_Channel.isOpen())
        {
            if (p_Response == null || p_Response.getHeader() <= 0)
            {
                return;
            }

            if (PacketManager.DEBUG_SHOW_PACKETS)
                Emulator.getLogging().logPacketLine("[" + Logging.ANSI_PURPLE + "SERVER" + Logging.ANSI_RESET + "] => [" + p_Response.getHeader() + "] -> " + p_Response.getBodyString());

            this.m_Channel.write(p_Response.get(), this.m_Channel.voidPromise());
            this.m_Channel.flush();
        }
    }

    /// Send packed response
    /// @p_Responses : Response Array
    public void sendResponses(ArrayList<ServerMessage> p_Responses)
    {
        ByteBuf l_Buffer = Unpooled.buffer();

        if (this.m_Channel.isOpen()) {
            for (ServerMessage l_Itr : p_Responses)
            {
                if (l_Itr == null || l_Itr.getHeader() <= 0) {
                    return;
                }

                if (PacketManager.DEBUG_SHOW_PACKETS)
                    Emulator.getLogging().logPacketLine("[" + Logging.ANSI_PURPLE + "SERVER" + Logging.ANSI_RESET + "] => [" + l_Itr.getHeader() + "] -> " + l_Itr.getBodyString());

                l_Buffer.writeBytes(l_Itr.get());
            }
            this.m_Channel.write(l_Buffer.copy(), this.m_Channel.voidPromise());
            this.m_Channel.flush();
        }
        l_Buffer.release();
    }

    /// Dispose Habbo
    public void dispose() {

        try
        {
            this.m_Channel.close();

            if (this.m_Habbo != null) {
                if (this.m_Habbo.isOnline())
                {
                    this.m_Habbo.getHabboInfo().setOnline(false);
                    this.m_Habbo.disconnect();
                }

                this.m_Habbo = null;
            }
        } catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    ///////////////////////////////////////////
    //            GETTERS/SETTERS
    ///////////////////////////////////////////

    public Channel getChannel()         { return this.m_Channel;    }
    public String getMachineId()        { return this.m_MachineId;  }
    public Habbo getHabbo()             { return this.m_Habbo;      }

    public void setHabbo(Habbo p_Habbo) { this.m_Habbo = p_Habbo;   }
    public void setMachineId(String p_MachineId)
    {
        if (p_MachineId == null)
        {
            throw new RuntimeException("Cannot set machineID to NULL");
        }
        this.m_MachineId = p_MachineId;

        if (this.m_MachineId != null)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET machine_id = ? WHERE id = ? LIMIT 1")) {
                statement.setString(1, this.m_MachineId);
                statement.setInt(2, this.m_Habbo.getHabboInfo().getId());
                statement.execute();
            } catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    public final ConcurrentHashMap<Integer, Integer> incomingPacketCounter = new ConcurrentHashMap<>(25);
    public long lastPacketCounterCleared = Emulator.getIntUnixTimestamp();

    private final Channel m_Channel;
    private Habbo m_Habbo;
    private String m_MachineId;
}