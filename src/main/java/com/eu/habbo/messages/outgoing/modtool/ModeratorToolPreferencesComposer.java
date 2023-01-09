package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ModeratorToolPreferencesComposer extends MessageComposer {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.moderatorToolPreferencesComposer);
        this.response.appendInt(this.x);
        this.response.appendInt(this.y);
        this.response.appendInt(this.width);
        this.response.appendInt(this.height);
        return this.response;
    }
}