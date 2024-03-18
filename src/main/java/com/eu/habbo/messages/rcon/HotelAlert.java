package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.HabboBroadcastMessageComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.ModeratorMessageComposer;
import com.google.gson.Gson;

import java.util.Map;

public class HotelAlert extends RCONMessage<HotelAlert.JSONHotelAlert> {

    public HotelAlert() {
        super(JSONHotelAlert.class);
    }

    @Override
    public void handle(Gson gson, JSONHotelAlert object) {
        ServerMessage serverMessage;
        if (object.url.isEmpty()) {
            serverMessage = new HabboBroadcastMessageComposer(object.message).compose();
        } else {
            serverMessage = new ModeratorMessageComposer(object.message, object.url).compose();
        }

        if (serverMessage != null) {
            for (Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet()) {
                Habbo habbo = set.getValue();
                if (habbo.getHabboStats().isBlockStaffAlerts())
                    continue;

                habbo.getClient().sendResponse(serverMessage);
            }
        }
    }

    static class JSONHotelAlert {

        public String message;


        public String url = "";
    }
}
