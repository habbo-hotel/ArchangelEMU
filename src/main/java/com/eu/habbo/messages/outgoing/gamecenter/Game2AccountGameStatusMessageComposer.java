package com.eu.habbo.messages.outgoing.gamecenter;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class Game2AccountGameStatusMessageComposer extends MessageComposer {
    private final int gameId;
    private final int gamesLeft;

    public Game2AccountGameStatusMessageComposer(int gameId, int gamesLeft) {
        this.gameId = gameId;
        this.gamesLeft = gamesLeft;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.game2AccountGameStatusMessageComposer);
        this.response.appendInt(this.gameId);
        this.response.appendInt(this.gamesLeft);
        this.response.appendInt(1);
        return this.response;
    }
}