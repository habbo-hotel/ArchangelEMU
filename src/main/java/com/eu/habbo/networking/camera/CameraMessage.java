package com.eu.habbo.networking.camera;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

public class CameraMessage {
    @Getter protected final short header;
    protected final ByteBuf buffer;

    public CameraMessage(short header) {
        this.header = header;
        this.buffer = Unpooled.buffer();
    }

}
