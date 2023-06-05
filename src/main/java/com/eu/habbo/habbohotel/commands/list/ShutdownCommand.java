package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.HabboBroadcastMessageComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.HotelWillCloseInMinutesComposer;
import com.eu.habbo.threading.runnables.ShutdownEmulator;

public class ShutdownCommand extends Command {
    public ShutdownCommand() {
        super("cmd_shutdown");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        StringBuilder reason = new StringBuilder("-");
        int minutes = 0;
        if (params.length > 2) {
            reason = new StringBuilder();
            for (int i = 1; i < params.length; i++) {
                reason.append(params[i]).append(" ");
            }
        } else {
            if (params.length == 2) {
                try {
                    minutes = Integer.parseInt(params[1]);
                } catch (Exception e) {
                    reason = new StringBuilder(params[1]);
                }
            }
        }

        ServerMessage message;
        if (!reason.toString().equals("-")) {
            message = new HabboBroadcastMessageComposer("<b>" + getTextsValue("generic.warning") + "</b> \r\n" +
                    getTextsValue("generic.shutdown").replace("%minutes%", minutes + "") + "\r\n" +
                    getTextsValue("generic.reason.specified") + ": <b>" + reason + "</b>\r" +
                    "\r" +
                    "- " + gameClient.getHabbo().getHabboInfo().getUsername()).compose();
        } else {
            message = new HotelWillCloseInMinutesComposer(minutes).compose();
        }
        RoomTrade.TRADING_ENABLED = false;
        ShutdownEmulator.timestamp = Emulator.getIntUnixTimestamp() + (60 * minutes);
        Emulator.getThreading().run(new ShutdownEmulator(message), (long) minutes * 60 * 1000);
        return true;
    }
}
