package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.trading.TradingOpenFailedComposer;

public class OpenTradingEvent extends MessageHandler {
    @Override
    public void handle() {
        if (Emulator.getIntUnixTimestamp() - this.client.getHabbo().getHabboStats().getLastTradeTimestamp() > 10) {
            this.client.getHabbo().getHabboStats().setLastTradeTimestamp(Emulator.getIntUnixTimestamp());
            int userId = this.packet.readInt();

            Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
            if (room != null) {
                if (userId >= 0 && userId != this.client.getHabbo().getRoomUnit().getId()) {
                    Habbo targetUser = room.getHabboByRoomUnitId(userId);

                    boolean tradeAnywhere = this.client.getHabbo().hasRight(Permission.ACC_TRADE_ANYWHERE);

                    if (!RoomTrade.TRADING_ENABLED && !tradeAnywhere) {
                        this.client.sendResponse(new TradingOpenFailedComposer(TradingOpenFailedComposer.HOTEL_TRADING_NOT_ALLOWED));
                        return;
                    }

                    if ((room.getTradeMode() == 0 || (room.getTradeMode() == 1 && this.client.getHabbo().getHabboInfo().getId() != room.getOwnerId())) && !tradeAnywhere) {
                        this.client.sendResponse(new TradingOpenFailedComposer(TradingOpenFailedComposer.ROOM_TRADING_NOT_ALLOWED));
                        return;
                    }

                    if (targetUser == null) return;

                    if (targetUser.getHabboStats().userIgnored(this.client.getHabbo().getHabboInfo().getId())) return;

                    if (this.client.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.TRADING)) {
                        this.client.sendResponse(new TradingOpenFailedComposer(TradingOpenFailedComposer.YOU_ALREADY_TRADING));
                        return;
                    }

                    if (!this.client.getHabbo().getHabboStats().allowTrade()) {
                        this.client.sendResponse(new TradingOpenFailedComposer(TradingOpenFailedComposer.YOU_TRADING_OFF));
                        return;
                    }

                    if (targetUser.getRoomUnit().hasStatus(RoomUnitStatus.TRADING)) {
                        this.client.sendResponse(new TradingOpenFailedComposer(TradingOpenFailedComposer.TARGET_ALREADY_TRADING, targetUser.getHabboInfo().getUsername()));
                        return;
                    }

                    if (!targetUser.getHabboStats().allowTrade()) {
                        this.client.sendResponse(new TradingOpenFailedComposer(TradingOpenFailedComposer.TARGET_TRADING_NOT_ALLOWED, targetUser.getHabboInfo().getUsername()));
                        return;
                    }

                    room.startTrade(this.client.getHabbo(), targetUser);
                }
            }
        }
    }
}
