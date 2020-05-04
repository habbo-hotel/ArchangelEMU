package com.eu.habbo.messages;

import com.eu.habbo.util.PacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerMessage implements ReferenceCounted {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMessage.class);

    private int header;
    private ByteBufOutputStream stream;
    private ByteBuf channelBuffer;

    public ServerMessage() {
        this.channelBuffer = Unpooled.buffer();
        this.stream = new ByteBufOutputStream(this.channelBuffer);
    }

    public ServerMessage(int header) {
        this.header = header;
        this.channelBuffer = Unpooled.buffer();
        this.stream = new ByteBufOutputStream(this.channelBuffer);
        try {
            this.stream.writeInt(0);
            this.stream.writeShort(header);
        } catch (Exception e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public ServerMessage init(int id) {
        this.header = id;
        this.channelBuffer = Unpooled.buffer();
        this.stream = new ByteBufOutputStream(this.channelBuffer);

        try {
            this.stream.writeInt(0);
            this.stream.writeShort(id);
        } catch (Exception e) {
            LOGGER.error("ServerMessage exception", e);
        }
        return this;
    }

    public void appendRawBytes(byte[] bytes) {
        try {
            this.stream.write(bytes);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendString(String obj) {
        if (obj == null) {
            this.appendString("");
            return;
        }

        try {
            byte[] data = obj.getBytes();
            this.stream.writeShort(data.length);
            this.stream.write(data);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendChar(int obj) {
        try {
            this.stream.writeChar(obj);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendChars(Object obj) {
        try {
            this.stream.writeChars(obj.toString());
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendInt(Integer obj) {
        try {
            this.stream.writeInt(obj);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendInt(Short obj) {
        this.appendShort(0);
        this.appendShort(obj);
    }

    public void appendInt(Byte obj) {
        try {
            this.stream.writeInt((int) obj);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendInt(Boolean obj) {
        try {
            this.stream.writeInt(obj ? 1 : 0);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendShort(int obj) {
        try {
            this.stream.writeShort((short) obj);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendByte(Integer b) {
        try {
            this.stream.writeByte(b);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendBoolean(Boolean obj) {
        try {
            this.stream.writeBoolean(obj);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendDouble(double d) {
        try {
            this.stream.writeDouble(d);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public void appendDouble(Double obj) {
        try {
            this.stream.writeDouble(obj);
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }
    }

    public ServerMessage appendResponse(ServerMessage obj) {
        try {
            this.stream.write(obj.get().array());
        } catch (IOException e) {
            LOGGER.error("ServerMessage exception", e);
        }

        return this;
    }

    public void append(ISerialize obj) {
        obj.serialize(this);
    }

    public String getBodyString() {
        return PacketUtils.formatPacket(this.channelBuffer);
    }

    public int getHeader() {
        return this.header;
    }

    public ByteBuf get() {
        this.channelBuffer.setInt(0, this.channelBuffer.writerIndex() - 4);

        return this.channelBuffer.copy();
    }

    @Override
    public int refCnt() {
        return this.channelBuffer.refCnt();
    }

    @Override
    public ReferenceCounted retain() {
        this.channelBuffer.retain();
        return this;
    }

    @Override
    public ReferenceCounted retain(int i) {
        this.channelBuffer.retain(i);
        return this;
    }

    @Override
    public ReferenceCounted touch() {
        this.channelBuffer.touch();
        return this;
    }

    @Override
    public ReferenceCounted touch(Object o) {
        this.channelBuffer.touch(o);
        return this;
    }

    public boolean release() {
        return this.channelBuffer.release();
    }

    @Override
    public boolean release(int i) {
        return this.channelBuffer.release(i);
    }

}