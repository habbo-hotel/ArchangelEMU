package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.habboway.nux.InClientLinkMessageComposer;

import java.util.HashMap;
import java.util.Map;

public class NewUserExperienceScriptProceedEvent extends MessageHandler {
    public static final Map<Integer, String> keys = new HashMap<>() {
        {
            this.put(1, "BOTTOM_BAR_RECEPTION");
            this.put(2, "BOTTOM_BAR_NAVIGATOR");
            this.put(3, "CHAT_INPUT");
            this.put(4, "CHAT_HISTORY_BUTTON");
            this.put(5, "MEMENU_CLOTHES");
            this.put(6, "BOTTOM_BAR_CATALOGUE");
            this.put(7, "CREDITS_BUTTON");
            this.put(8, "DUCKETS_BUTTON");
            this.put(9, "DIAMONDS_BUTTON");
            this.put(10, "FRIENDS_BAR_ALL_FRIENDS");
            this.put(11, "BOTTOM_BAR_NAVIGATOR");
        }
    };

    public static void handle(Habbo habbo) {
        habbo.getHabboStats().setNux(true);
        int step = habbo.getHabboStats().increaseNuxStep();

        if (keys.containsKey(step)) {
            habbo.getClient().sendResponse(new InClientLinkMessageComposer("helpBubble/add/" + keys.get(step) + "/" + Emulator.getTexts().getValue("nux.step." + step)));
        } else if (!habbo.getHabboStats().isNuxReward()) {


        } else {
            habbo.getClient().sendResponse(new InClientLinkMessageComposer("nux/lobbyoffer/show"));
        }
    }

    @Override
    public void handle() {
        handle(this.client.getHabbo());
    }
}
