package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;

public class GetWeeklyGameRewardWinnersEvent extends MessageHandler {
    @Override
    public void handle() {
        int gameId = this.packet.readInt();

        if (gameId == 3) {
        }
    }
}