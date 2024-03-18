package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.VoucherRedeemErrorMessageComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.HotelWillCloseInMinutesComposer;
import com.eu.habbo.threading.runnables.ShutdownEmulator;

public class RedeemVoucherEvent extends MessageHandler {
    @Override
    public void handle() {
        if (ShutdownEmulator.timestamp > 0) {
            this.client.sendResponse(new HotelWillCloseInMinutesComposer((ShutdownEmulator.timestamp - Emulator.getIntUnixTimestamp()) / 60));
            return;
        }

        String voucherCode = this.packet.readString();

        if (voucherCode.contains(" ")) {
            this.client.sendResponse(new VoucherRedeemErrorMessageComposer(VoucherRedeemErrorMessageComposer.TECHNICAL_ERROR));
            return;
        }

        Emulator.getGameEnvironment().getCatalogManager().redeemVoucher(this.client, voucherCode);
    }
}