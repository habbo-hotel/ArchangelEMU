package com.eu.habbo.habbohotel.rooms.trades;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.types.IRoomManager;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.set.hash.THashSet;

public class RoomTradeManager extends IRoomManager {
    private final THashSet<RoomTrade> activeTrades;
    public RoomTradeManager(Room room) {
        super(room);
        this.activeTrades = new THashSet<>(0);
    }

    public void startTrade(Habbo userOne, Habbo userTwo) {
        RoomTrade trade = new RoomTrade(userOne, userTwo, room);
        synchronized (this.activeTrades) {
            this.activeTrades.add(trade);
        }

        trade.start();
    }

    public void stopTrade(RoomTrade trade) {
        synchronized (this.activeTrades) {
            this.activeTrades.remove(trade);
        }
    }

    public RoomTrade getActiveTradeForHabbo(Habbo user) {
        synchronized (this.activeTrades) {
            for (RoomTrade trade : this.activeTrades) {
                for (RoomTradeUser habbo : trade.getRoomTradeUsers()) {
                    if (habbo.getHabbo() == user) return trade;
                }
            }
        }
        return null;
    }

}
