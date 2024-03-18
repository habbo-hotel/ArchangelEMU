package com.eu.habbo.messages.incoming.trading;

public class CloseTradingEvent extends TradingEvent {
    @Override
    public void handle() {
        stopTrade(this.client.getHabbo());
    }
}
