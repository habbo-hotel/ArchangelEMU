package com.eu.habbo.messages.outgoing.gamecenter;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameCenterGameComposer extends MessageComposer {
    public final static int OK = 0;
    public final static int ERROR = 1;

    private final int gameId;
    private final int status;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.gameStatusMessageComposer);
        this.response.appendInt(this.gameId);
        this.response.appendInt(this.status);
        return this.response;
    }
}
