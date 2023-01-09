package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.games.freeze.FreezeGamePlayer;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GamePlayerValueMessageComposer extends MessageComposer {
    private final FreezeGamePlayer gamePlayer;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.gamePlayerValueMessageComposer);
        this.response.appendInt(this.gamePlayer.getHabbo().getRoomUnit().getId());
        this.response.appendInt(this.gamePlayer.getLives());
        return this.response;
    }
}
